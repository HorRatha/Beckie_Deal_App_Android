package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.OrdersApiResponse;
import aeu.ratha.beckie_deal_app.data.models.SingleOrderApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API Interface for Order management endpoints.
 * Endpoint: /api/v1/orders
 */
public interface OrderApiService {

    /**
     * Get paginated user orders.
     * GET /orders?page={page}&per_page={perPage}
     */
    @GET("orders")
    Call<OrdersApiResponse> getOrders(
            @Query("page") int page,
            @Query("per_page") int perPage
    );

    /**
     * Get specific order details by ID.
     * GET /orders/{id}
     */
    @GET("orders/{id}")
    Call<SingleOrderApiResponse> getOrderById(@Path("id") long orderId);
}
