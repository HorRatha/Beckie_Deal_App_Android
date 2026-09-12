package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.WishlistItemResponse;
import aeu.ratha.beckie_deal_app.data.models.WishlistResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Map;

/**
 * Retrofit API service interface for wishlist endpoints.
 * Based on actual API response structure from backend.
 */
public interface WishlistApiService {
    
    /**
     * Get user's wishlist with all items.
     * GET /api/v1/wishlist
     * 
     * @return Call object with WishlistResponse containing wishlist and items
     */
    @GET("wishlist")
    Call<WishlistResponse> getWishlist();
    
    /**
     * Create or update wishlist.
     * PUT /api/v1/wishlist
     * 
     * @param wishlistData Map containing name, description, is_public, is_active, metadata
     * @return Call object with WishlistResponse
     */
    @PUT("wishlist")
    Call<WishlistResponse> createOrUpdateWishlist(@Body Map<String, Object> wishlistData);
    
    /**
     * Delete user's wishlist.
     * DELETE /api/v1/wishlist
     * 
     * @return Call object with response
     */
    @DELETE("wishlist")
    Call<Void> deleteWishlist();
    
    /**
     * Check if a product is in the wishlist.
     * GET /api/v1/wishlist/check?product_id={productId}
     * 
     * @param productId Product UUID to check
     * @return Call object with check result
     */
    @GET("wishlist/check")
    Call<Map<String, Object>> checkProduct(@Query("product_id") String productId);
    
    /**
     * Add item to wishlist.
     * POST /api/v1/wishlist/items
     * 
     * Request body: {
     *   "product_id": "uuid",
     *   "product_variant_id": "uuid" (optional),
     *   "quantity": 1,
     *   "metadata": {}
     * }
     * 
     * @param itemData Map containing product_id, product_variant_id, quantity, metadata
     * @return Call object with WishlistItemResponse
     */
    @POST("wishlist/items")
    Call<WishlistItemResponse> addItem(@Body Map<String, Object> itemData);
    
    /**
     * Remove item from wishlist.
     * DELETE /api/v1/wishlist/items/{wishlistItemId}
     * 
     * @param wishlistItemId Wishlist item ID to remove
     * @return Call object with response
     */
    @DELETE("wishlist/items/{wishlistItemId}")
    Call<Void> removeItem(@Path("wishlistItemId") int wishlistItemId);
}
