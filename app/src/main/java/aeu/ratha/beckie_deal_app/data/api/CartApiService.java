package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.CartResponse;
import aeu.ratha.beckie_deal_app.data.models.CartItemResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Retrofit API interface for Cart endpoints.
 * Base URL: /api/v1
 */
public interface CartApiService {
    
    /**
     * Get user's cart with all items.
     * GET /cart
     */
    @GET("cart")
    Call<CartResponse> getCart();
    
    /**
     * Create or update cart.
     * PUT /cart
     * 
     * Body example:
     * {
     *   "metadata": {}
     * }
     */
    @PUT("cart")
    Call<CartResponse> createOrUpdateCart(@Body Map<String, Object> cartData);
    
    /**
     * Add item to cart.
     * POST /cart/items
     * 
     * Body example:
     * {
     *   "product_id": "uuid",
     *   "quantity": 1,
     *   "metadata": {}
     * }
     */
    @POST("cart/items")
    Call<CartItemResponse> addItem(@Body Map<String, Object> itemData);
    
    /**
     * Update cart item quantity.
     * PATCH /cart/items/{id}
     * 
     * Body example:
     * {
     *   "quantity": 2
     * }
     */
    @PATCH("cart/items/{id}")
    Call<CartItemResponse> updateItem(
        @Path("id") int cartItemId,
        @Body Map<String, Object> updateData
    );
    
    /**
     * Remove item from cart.
     * DELETE /cart/items/{id}
     */
    @DELETE("cart/items/{id}")
    Call<Void> removeItem(@Path("id") int cartItemId);
    
    /**
     * Checkout cart (create order).
     * POST /cart/checkout
     * 
     * Body example:
     * {
     *   "shipping_address": {},
     *   "payment_method": "card",
     *   "notes": ""
     * }
     */
    @POST("cart/checkout")
    Call<Map<String, Object>> checkout(@Body Map<String, Object> checkoutData);
}
