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
 * 
 * ⚠️ STATUS: NOT YET IMPLEMENTED (404)
 * 
 * All endpoints in this service return HTTP 404 - NOT FOUND.
 * The backend does not yet have cart functionality implemented.
 * 
 * @deprecated Backend endpoints not yet implemented. Use CartRepository for now,
 *             which gracefully handles 404 responses. When backend implements
 *             these endpoints, CartRepository will automatically use them.
 * 
 * Backend TODO:
 *  - POST /cart/items (add to cart)
 *  - PATCH /cart/items/{id} (update quantity)
 *  - DELETE /cart/items/{id} (remove from cart)
 *  - GET /cart (get cart contents)
 *  - PUT /cart (create/update cart)
 *  - POST /cart/checkout (create order)
 */
@Deprecated
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
