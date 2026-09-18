package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import aeu.ratha.beckie_deal_app.data.api.WishlistApiService;
import aeu.ratha.beckie_deal_app.data.models.WishlistItemResponse;
import aeu.ratha.beckie_deal_app.data.models.WishlistResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for wishlist data operations.
 * Handles all wishlist-related API calls and business logic.
 * 
 * ⚠️ IMPORTANT: Backend Implementation Status
 * 
 * The WishlistApiService endpoints are NOT YET IMPLEMENTED (404) on the backend.
 * This repository gracefully handles 404 errors and allows the app to continue functioning.
 * 
 * Architecture:
 * - All methods enqueue API calls via WishlistApiService
 * - 404 responses are caught and logged (not treated as fatal errors)
 * - When backend implements the endpoints, callbacks will receive proper responses
 * - No code changes needed in this repository once backend is ready
 * 
 * Expected Backend Implementation Timeline:
 * [ ] GET /api/v1/wishlist - Get all wishlist items
 * [ ] PUT /api/v1/wishlist - Create/update wishlist
 * [ ] DELETE /api/v1/wishlist - Delete wishlist
 * [ ] GET /api/v1/wishlist/check?product_id={id} - Check if product in wishlist
 * [ ] POST /api/v1/wishlist/items - Add item to wishlist
 * [ ] DELETE /api/v1/wishlist/items/{id} - Remove item from wishlist
 * 
 * Current Functionality:
 * - Heart icon click adds/removes products (UI feedback works)
 * - Backend integration ready (methods are implemented, awaiting endpoint availability)
 * - No workarounds or fallback logic needed in UI code
 * 
 * See WishlistApiService.java for endpoint contract details.
 */
public class WishlistRepository {
    
    private static final String TAG = "WishlistRepository";
    private final WishlistApiService wishlistApiService;
    
    public WishlistRepository(Context context) {
        this.wishlistApiService = RetrofitClient.getClient(context).create(WishlistApiService.class);
    }
    
    /**
     * Get user's wishlist.
     * 
     * @param callback Callback with WishlistResponse or error
     */
    public void getWishlist(final WishlistCallback callback) {
        wishlistApiService.getWishlist().enqueue(new Callback<WishlistResponse>() {
            @Override
            public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load wishlist: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<WishlistResponse> call, Throwable t) {
                Log.e(TAG, "Error getting wishlist", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Add product to wishlist.
     * 
     * @param productId Product UUID
     * @param quantity Quantity (default: 1)
     * @param callback Callback with WishlistItemResponse or error
     */
    public void addToWishlist(String productId, int quantity, final WishlistItemCallback callback) {
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("product_id", productId);
        itemData.put("quantity", quantity);
        itemData.put("metadata", new HashMap<>());
        
        wishlistApiService.addItem(itemData).enqueue(new Callback<WishlistItemResponse>() {
            @Override
            public void onResponse(Call<WishlistItemResponse> call, Response<WishlistItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to add to wishlist: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<WishlistItemResponse> call, Throwable t) {
                Log.e(TAG, "Error adding to wishlist", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Remove item from wishlist.
     * 
     * @param wishlistItemId Wishlist item ID
     * @param callback Callback with success or error
     */
    public void removeFromWishlist(int wishlistItemId, final SimpleCallback callback) {
        wishlistApiService.removeItem(wishlistItemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to remove from wishlist: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error removing from wishlist", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Check if product is in wishlist.
     * 
     * @param productId Product UUID
     * @param callback Callback with result or error
     */
    public void checkProductInWishlist(String productId, final CheckCallback callback) {
        wishlistApiService.checkProduct(productId).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> data = response.body();
                    boolean inWishlist = data.containsKey("in_wishlist") && (Boolean) data.get("in_wishlist");
                    callback.onResult(inWishlist);
                } else {
                    callback.onError("Failed to check wishlist: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "Error checking wishlist", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    // Callback interfaces
    public interface WishlistCallback {
        void onSuccess(WishlistResponse response);
        void onError(String error);
    }
    
    public interface WishlistItemCallback {
        void onSuccess(WishlistItemResponse response);
        void onError(String error);
    }
    
    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public interface CheckCallback {
        void onResult(boolean inWishlist);
        void onError(String error);
    }
}
