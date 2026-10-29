package aeu.ratha.beckie_deal_app.utils.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import aeu.ratha.beckie_deal_app.data.api.ProductApiService;
import aeu.ratha.beckie_deal_app.data.models.ApiOrder;
import aeu.ratha.beckie_deal_app.data.models.ApiOrderItem;
import aeu.ratha.beckie_deal_app.data.models.SingleProductResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import aeu.ratha.beckie_deal_app.utils.helpers.ImageUrlHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductImageCacheManager {

    private static final String TAG = "ProductImageCache";
    private static final String PREF_NAME = "beckie_deal_product_images_cache";
    private static final String KEY_IMAGE_MAP = "cached_product_images_map";

    private static final Map<String, String> memoryCache = new ConcurrentHashMap<>();
    private static final Set<String> fetchingIds = new HashSet<>();

    public interface ImageFetchListener {
        void onImageFetched(String productId, String imageUrl);
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static void initFromPrefsIfNeeded(Context context) {
        if (!memoryCache.isEmpty() || context == null) return;
        try {
            SharedPreferences prefs = getPrefs(context);
            String json = prefs.getString(KEY_IMAGE_MAP, null);
            if (json != null && !json.trim().isEmpty()) {
                Type type = new TypeToken<HashMap<String, String>>() {}.getType();
                Map<String, String> map = new Gson().fromJson(json, type);
                if (map != null) {
                    memoryCache.putAll(map);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image cache from prefs", e);
        }
    }

    public static String getCachedImageUrl(Context context, String productId) {
        if (productId == null || productId.trim().isEmpty()) return null;
        initFromPrefsIfNeeded(context);
        return memoryCache.get(productId);
    }

    public static void cacheImageUrl(Context context, String productId, String imageUrl) {
        if (productId == null || imageUrl == null) return;
        String fullUrl = ImageUrlHelper.getImageUrlOrFallback(imageUrl);
        memoryCache.put(productId, fullUrl);

        if (context != null) {
            try {
                SharedPreferences prefs = getPrefs(context);
                String json = new Gson().toJson(memoryCache);
                prefs.edit().putString(KEY_IMAGE_MAP, json).apply();
            } catch (Exception e) {
                Log.e(TAG, "Error saving image cache to prefs", e);
            }
        }
    }

    /**
     * Inspects list of orders, populates cached images into items, and asynchronously
     * fetches missing product images from /products/{id}!
     */
    public static void resolveOrderProductImages(Context context, List<ApiOrder> orders, ImageFetchListener listener) {
        if (orders == null || context == null) return;
        initFromPrefsIfNeeded(context);

        ProductApiService apiService = RetrofitClient.getClient(context).create(ProductApiService.class);

        for (ApiOrder order : orders) {
            if (order.items == null) continue;
            for (ApiOrderItem item : order.items) {
                if (item.productId == null || item.productId.trim().isEmpty()) continue;

                String cached = memoryCache.get(item.productId);
                if (cached != null) {
                    item.productThumbnail = cached;
                } else {
                    fetchProductImage(context, apiService, item, listener);
                }
            }
        }
    }

    private static void fetchProductImage(Context context, ProductApiService apiService, ApiOrderItem item, ImageFetchListener listener) {
        final String productId = item.productId;
        synchronized (fetchingIds) {
            if (fetchingIds.contains(productId)) return;
            fetchingIds.add(productId);
        }

        apiService.getProduct(productId).enqueue(new Callback<SingleProductResponse>() {
            @Override
            public void onResponse(Call<SingleProductResponse> call, Response<SingleProductResponse> response) {
                synchronized (fetchingIds) {
                    fetchingIds.remove(productId);
                }

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    String thumb = response.body().getData().getThumbnail();
                    if (thumb != null && !thumb.trim().isEmpty()) {
                        String fullUrl = ImageUrlHelper.getImageUrlOrFallback(thumb);
                        item.productThumbnail = fullUrl;
                        cacheImageUrl(context, productId, fullUrl);

                        if (listener != null) {
                            listener.onImageFetched(productId, fullUrl);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SingleProductResponse> call, Throwable t) {
                synchronized (fetchingIds) {
                    fetchingIds.remove(productId);
                }
                Log.e(TAG, "Failed to fetch product image for ID: " + productId, t);
            }
        });
    }
}
