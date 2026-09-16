package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import aeu.ratha.beckie_deal_app.data.api.CartApiService;
import aeu.ratha.beckie_deal_app.data.models.CartItemResponse;
import aeu.ratha.beckie_deal_app.data.models.CartResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for cart data operations.
 * Handles all cart-related API calls and business logic.
 */
public class CartRepository {
    
    private static final String TAG = "CartRepository";
    private final CartApiService cartApiService;
    
    public CartRepository(Context context) {
        this.cartApiService = RetrofitClient.getClient(context).create(CartApiService.class);
    }
    
    /**
     * Get user's cart with all items.
     * 
     * @param callback Callback with CartResponse or error
     */
    public void getCart(final CartCallback callback) {
        cartApiService.getCart().enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✓ Cart loaded successfully");
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to load cart: " + response.code();
                    Log.e(TAG, "✗ " + error);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, "✗ " + error, t);
                callback.onError(error);
            }
        });
    }
    
    /**
     * Add product to cart.
     * 
     * @param productId Product UUID
     * @param quantity Quantity to add
     * @param callback Callback with CartItemResponse or error
     */
    public void addToCart(String productId, int quantity, final CartItemCallback callback) {
        // Validate product ID
        if (productId == null || productId.isEmpty()) {
            String error = "Product ID is required";
            Log.e(TAG, "✗ " + error);
            callback.onError(error);
            return;
        }
        
        Map<String, Object> itemData = new HashMap<>();
        itemData.put("product_id", productId);
        itemData.put("quantity", quantity);
        itemData.put("metadata", new HashMap<>());
        
        Log.d(TAG, "Adding to cart - product_id: " + productId + ", quantity: " + quantity);
        
        cartApiService.addItem(itemData).enqueue(new Callback<CartItemResponse>() {
            @Override
            public void onResponse(Call<CartItemResponse> call, Response<CartItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✓ Item added to cart");
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to add to cart: " + response.code();
                    Log.e(TAG, "✗ " + error);
                    
                    // Try to get error details from response body
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error body", e);
                    }
                    
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<CartItemResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, "✗ " + error, t);
                callback.onError(error);
            }
        });
    }
    
    /**
     * Update cart item quantity.
     * 
     * @param cartItemId Cart item ID
     * @param quantity New quantity
     * @param callback Callback with CartItemResponse or error
     */
    public void updateQuantity(int cartItemId, int quantity, final CartItemCallback callback) {
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("quantity", quantity);
        
        cartApiService.updateItem(cartItemId, updateData).enqueue(new Callback<CartItemResponse>() {
            @Override
            public void onResponse(Call<CartItemResponse> call, Response<CartItemResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✓ Cart item updated");
                    callback.onSuccess(response.body());
                } else {
                    String error = "Failed to update item: " + response.code();
                    Log.e(TAG, "✗ " + error);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<CartItemResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, "✗ " + error, t);
                callback.onError(error);
            }
        });
    }
    
    /**
     * Remove item from cart.
     * 
     * @param cartItemId Cart item ID
     * @param callback Callback with success or error
     */
    public void removeFromCart(int cartItemId, final SimpleCallback callback) {
        cartApiService.removeItem(cartItemId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "✓ Item removed from cart");
                    callback.onSuccess();
                } else {
                    String error = "Failed to remove item: " + response.code();
                    Log.e(TAG, "✗ " + error);
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, "✗ " + error, t);
                callback.onError(error);
            }
        });
    }
    
    /**
     * Checkout cart (create order).
     * New API structure - sends all fields flat, not nested in shipping_address
     * 
     * @param checkoutData All checkout data (cart_id, shipping fields, payment method, etc.)
     * @param callback Callback with checkout response or error
     */
    public void checkout(Map<String, Object> checkoutData, final CheckoutCallback callback) {
        Log.d(TAG, "Checkout request: " + checkoutData);
        
        cartApiService.checkout(checkoutData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✓ Checkout successful");
                    callback.onSuccess(response.body());
                } else {
                    String error = "Checkout failed: " + response.code();
                    Log.e(TAG, "✗ " + error);
                    
                    // Try to get error details
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error response: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error body", e);
                    }
                    
                    callback.onError(error);
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, "✗ " + error, t);
                callback.onError(error);
            }
        });
    }
    
    // Callback interfaces
    
    public interface CartCallback {
        void onSuccess(CartResponse response);
        void onError(String error);
    }
    
    public interface CartItemCallback {
        void onSuccess(CartItemResponse response);
        void onError(String error);
    }
    
    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public interface CheckoutCallback {
        void onSuccess(Map<String, Object> response);
        void onError(String error);
    }
}
