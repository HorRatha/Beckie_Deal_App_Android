package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.data.api.OrderApiService;
import aeu.ratha.beckie_deal_app.data.models.ApiOrder;
import aeu.ratha.beckie_deal_app.data.models.OrdersApiResponse;
import aeu.ratha.beckie_deal_app.data.models.SingleOrderApiResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private static final String TAG = "OrderRepository";
    private final Context context;
    private final OrderApiService orderApiService;

    public interface OrdersCallback {
        void onSuccess(List<ApiOrder> orders);
        void onError(String errorMessage);
    }

    public interface SingleOrderCallback {
        void onSuccess(ApiOrder order);
        void onError(String errorMessage);
    }

    public OrderRepository(Context context) {
        this.context = context != null ? context.getApplicationContext() : null;
        this.orderApiService = RetrofitClient.getClient(context).create(OrderApiService.class);
    }

    /**
     * Fetches ALL pages of orders iteratively to ensure all user data is loaded and displayed.
     */
    public void fetchAllOrders(final OrdersCallback callback) {
        fetchAllPages(1, 15, new ArrayList<>(), callback);
    }

    private void fetchAllPages(final int page, final int perPage, final List<ApiOrder> accumulatedOrders, final OrdersCallback callback) {
        orderApiService.getOrders(page, perPage).enqueue(new Callback<OrdersApiResponse>() {
            @Override
            public void onResponse(Call<OrdersApiResponse> call, Response<OrdersApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrdersApiResponse apiResponse = response.body();
                    List<ApiOrder> pageOrders = apiResponse.data;

                    if (pageOrders != null) {
                        accumulatedOrders.addAll(pageOrders);
                    }

                    int lastPage = page;
                    if (apiResponse.meta != null && apiResponse.meta.lastPage > 0) {
                        lastPage = apiResponse.meta.lastPage;
                    } else if (pageOrders != null && pageOrders.size() >= perPage) {
                        // Fallback if meta is absent but page was full
                        lastPage = page + 1;
                    }

                    if (page < lastPage && pageOrders != null && !pageOrders.isEmpty()) {
                        Log.d(TAG, "Fetching next orders page: " + (page + 1) + "/" + lastPage);
                        fetchAllPages(page + 1, perPage, accumulatedOrders, callback);
                    } else {
                        Log.d(TAG, "Successfully loaded all orders pages. Total count: " + accumulatedOrders.size());
                        if (context != null) {
                            aeu.ratha.beckie_deal_app.utils.managers.ProductImageCacheManager.resolveOrderProductImages(context, accumulatedOrders, null);
                        }
                        callback.onSuccess(accumulatedOrders);
                    }
                } else {
                    Log.e(TAG, "Failed to load page " + page + ", code: " + response.code());
                    if (!accumulatedOrders.isEmpty()) {
                        callback.onSuccess(accumulatedOrders);
                    } else {
                        callback.onError("Failed to load orders (" + response.code() + ")");
                    }
                }
            }

            @Override
            public void onFailure(Call<OrdersApiResponse> call, Throwable t) {
                Log.e(TAG, "Network error fetching page " + page, t);
                if (!accumulatedOrders.isEmpty()) {
                    callback.onSuccess(accumulatedOrders);
                } else {
                    callback.onError("Network error: " + t.getLocalizedMessage());
                }
            }
        });
    }

    /**
     * Fetch a single order by ID.
     */
    public void getOrderById(long orderId, final SingleOrderCallback callback) {
        orderApiService.getOrderById(orderId).enqueue(new Callback<SingleOrderApiResponse>() {
            @Override
            public void onResponse(Call<SingleOrderApiResponse> call, Response<SingleOrderApiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    callback.onSuccess(response.body().data);
                } else {
                    callback.onError("Order not found (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<SingleOrderApiResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getLocalizedMessage());
            }
        });
    }
}
