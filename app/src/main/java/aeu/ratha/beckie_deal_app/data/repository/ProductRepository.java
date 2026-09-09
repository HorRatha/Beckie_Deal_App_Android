package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import aeu.ratha.beckie_deal_app.data.api.ProductApiService;
import aeu.ratha.beckie_deal_app.data.models.Product;
import aeu.ratha.beckie_deal_app.data.models.ProductResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for managing product data from API.
 * Handles API calls, caching, and error handling.
 */
public class ProductRepository {
    
    private static final String TAG = "ProductRepository";
    private static ProductRepository instance;
    private final ProductApiService apiService;
    private List<Product> cachedProducts;
    
    private ProductRepository(Context context) {
        this.apiService = RetrofitClient.getClient(context).create(ProductApiService.class);
        this.cachedProducts = new ArrayList<>();
    }
    
    /**
     * Get singleton instance of ProductRepository.
     */
    public static synchronized ProductRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProductRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Fetch all products with pagination.
     * 
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @param useCache Whether to use cached data if available
     * @param callback Callback with products or error message
     */
    public void fetchProducts(int limit, int offset, boolean useCache, ProductCallback callback) {
        if (useCache && !cachedProducts.isEmpty()) {
            Log.d(TAG, "Returning cached products: " + cachedProducts.size());
            callback.onSuccess(cachedProducts, cachedProducts.size());
            return;
        }
        
        Log.d(TAG, "Fetching products from API (limit=" + limit + ", offset=" + offset + ")");
        
        Call<ProductResponse> call = apiService.getProducts(limit, offset);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, 
                                   @NonNull Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    
                    if (productResponse.isSuccess() && productResponse.getData() != null) {
                        List<Product> products = productResponse.getData();
                        int total = productResponse.getTotalCount();
                        
                        Log.d(TAG, "✓ Successfully fetched " + products.size() + " products (total: " + total + ")");
                        
                        // Update cache
                        if (offset == 0) {
                            cachedProducts.clear();
                        }
                        cachedProducts.addAll(products);
                        
                        callback.onSuccess(products, total);
                    } else {
                        String errorMsg = "API returned non-success status: " + productResponse.getStatus();
                        Log.e(TAG, "✗ " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "API error: " + response.code() + " - " + response.message();
                    Log.e(TAG, "✗ " + errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, "✗ " + errorMsg, t);
                callback.onError(errorMsg);
            }
        });
    }
    
    /**
     * Fetch products filtered by category.
     * 
     * @param categoryId Category ID to filter by
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @param callback Callback with products or error message
     */
    public void fetchProductsByCategory(int categoryId, int limit, int offset, ProductCallback callback) {
        Log.d(TAG, "Fetching products for category " + categoryId + 
            " (limit=" + limit + ", offset=" + offset + ")");
        
        Call<ProductResponse> call = apiService.getProductsByCategory(categoryId, limit, offset);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, 
                                   @NonNull Response<ProductResponse> response) {
                handleProductResponse(response, callback, "category " + categoryId);
            }
            
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                handleFailure(t, callback);
            }
        });
    }
    
    /**
     * Fetch products filtered by brand.
     * 
     * @param brandId Brand ID to filter by
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @param callback Callback with products or error message
     */
    public void fetchProductsByBrand(int brandId, int limit, int offset, ProductCallback callback) {
        Log.d(TAG, "Fetching products for brand " + brandId + 
            " (limit=" + limit + ", offset=" + offset + ")");
        
        Call<ProductResponse> call = apiService.getProductsByBrand(brandId, limit, offset);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, 
                                   @NonNull Response<ProductResponse> response) {
                handleProductResponse(response, callback, "brand " + brandId);
            }
            
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                handleFailure(t, callback);
            }
        });
    }
    
    /**
     * Fetch products filtered by both category and brand.
     * 
     * @param categoryId Category ID to filter by
     * @param brandId Brand ID to filter by
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @param callback Callback with products or error message
     */
    public void fetchProductsByCategoryAndBrand(int categoryId, int brandId, 
                                                int limit, int offset, ProductCallback callback) {
        Log.d(TAG, "Fetching products for category " + categoryId + 
            " and brand " + brandId + " (limit=" + limit + ", offset=" + offset + ")");
        
        Call<ProductResponse> call = apiService.getProductsByCategoryAndBrand(
            categoryId, brandId, limit, offset);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, 
                                   @NonNull Response<ProductResponse> response) {
                handleProductResponse(response, callback, 
                    "category " + categoryId + " and brand " + brandId);
            }
            
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                handleFailure(t, callback);
            }
        });
    }
    
    /**
     * Fetch featured products.
     * 
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @param callback Callback with products or error message
     */
    public void fetchFeaturedProducts(int limit, int offset, ProductCallback callback) {
        Log.d(TAG, "Fetching featured products (limit=" + limit + ", offset=" + offset + ")");
        
        Call<ProductResponse> call = apiService.getFeaturedProducts(true, limit, offset);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, 
                                   @NonNull Response<ProductResponse> response) {
                handleProductResponse(response, callback, "featured products");
            }
            
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                handleFailure(t, callback);
            }
        });
    }
    
    /**
     * Fetch a single product by ID.
     * 
     * @param productId Product ID
     * @param callback Callback with product or error message
     */
    public void fetchProduct(int productId, SingleProductCallback callback) {
        Log.d(TAG, "Fetching product " + productId);
        
        Call<ProductResponse> call = apiService.getProduct(productId);
        call.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, 
                                   @NonNull Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    
                    if (productResponse.isSuccess() && productResponse.getData() != null 
                        && !productResponse.getData().isEmpty()) {
                        Product product = productResponse.getData().get(0);
                        Log.d(TAG, "✓ Successfully fetched product: " + product.getName());
                        callback.onSuccess(product);
                    } else {
                        String errorMsg = "API returned no product data";
                        Log.e(TAG, "✗ " + errorMsg);
                        callback.onError(errorMsg);
                    }
                } else {
                    String errorMsg = "API error: " + response.code() + " - " + response.message();
                    Log.e(TAG, "✗ " + errorMsg);
                    callback.onError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                handleFailure(t, new ProductCallback() {
                    @Override
                    public void onSuccess(List<Product> products, int total) {}
                    
                    @Override
                    public void onError(String errorMessage) {
                        callback.onError(errorMessage);
                    }
                });
            }
        });
    }
    
    /**
     * Common response handler for product list endpoints.
     */
    private void handleProductResponse(Response<ProductResponse> response, 
                                       ProductCallback callback, String context) {
        if (response.isSuccessful() && response.body() != null) {
            ProductResponse productResponse = response.body();
            
            if (productResponse.isSuccess() && productResponse.getData() != null) {
                List<Product> products = productResponse.getData();
                int total = productResponse.getTotalCount();
                
                Log.d(TAG, "✓ Successfully fetched " + products.size() + 
                    " products for " + context + " (total: " + total + ")");
                
                callback.onSuccess(products, total);
            } else {
                String errorMsg = "API returned non-success status: " + productResponse.getStatus();
                Log.e(TAG, "✗ " + errorMsg + " for " + context);
                callback.onError(errorMsg);
            }
        } else {
            String errorMsg = "API error: " + response.code() + " - " + response.message();
            Log.e(TAG, "✗ " + errorMsg + " for " + context);
            callback.onError(errorMsg);
        }
    }
    
    /**
     * Common failure handler.
     */
    private void handleFailure(Throwable t, ProductCallback callback) {
        String errorMsg = "Network error: " + t.getMessage();
        Log.e(TAG, "✗ " + errorMsg, t);
        callback.onError(errorMsg);
    }
    
    /**
     * Clear cached products.
     */
    public void clearCache() {
        Log.d(TAG, "Clearing product cache");
        cachedProducts.clear();
    }
    
    /**
     * Get cached products (for offline mode or quick access).
     */
    public List<Product> getCachedProducts() {
        return new ArrayList<>(cachedProducts);
    }
    
    /**
     * Callback interface for product list operations.
     */
    public interface ProductCallback {
        void onSuccess(List<Product> products, int total);
        void onError(String errorMessage);
    }
    
    /**
     * Callback interface for single product operations.
     */
    public interface SingleProductCallback {
        void onSuccess(Product product);
        void onError(String errorMessage);
    }
}
