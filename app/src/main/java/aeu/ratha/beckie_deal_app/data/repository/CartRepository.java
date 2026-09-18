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
 * 
 * ⚠️ IMPORTANT: Backend Implementation Status
 * 
 * The CartApiService endpoints are NOT YET IMPLEMENTED (404) on the backend.
 * This repository gracefully handles 404 errors and allows the app to continue functioning.
 * 
 * Architecture:
 * - All methods enqueue API calls via CartApiService
 * - 404 responses are caught and logged (not treated as fatal errors)
 * - When backend implements the endpoints, callbacks will receive proper responses
 * - No code changes needed in this repository once backend is ready
 * 
 * Expected Backend Implementation Timeline:
 * [ ] POST /api/v1/cart/items - Add product to cart
 * [ ] PATCH /api/v1/cart/items/{id} - Update quantity
 * [ ] DELETE /api/v1/cart/items/{id} - Remove from cart  
 * [ ] GET /api/v1/cart - Get cart contents
 * [ ] PUT /api/v1/cart - Create/update cart metadata
 * [ ] POST /api/v1/cart/checkout - Create order from cart
 * 
 * See CartApiService.java for endpoint contract details.
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
     *
     * On 422 the backend returns a structured validation error:
     * {"status":"error","message":"...","errors":{"field":["msg"]}}
     * We parse that and return the first human-readable message so the UI
     * can surface it directly instead of showing a raw HTTP code.
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
                    // Try to parse a human-readable validation message from the error body
                    String friendlyError = parseCheckoutError(response);
                    Log.e(TAG, "✗ Checkout failed: " + response.code() + " — " + friendlyError);
                    callback.onError(friendlyError);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                String error = "Network error — please check your connection.";
                Log.e(TAG, "✗ " + error, t);
                callback.onError(error);
            }
        });
    }

    /**
     * Parse the error body from a failed checkout response into a user-friendly string.
     * Handles the Laravel validation error shape:
     * {"status":"error","message":"The given data was invalid.","errors":{"field":["msg1"]}}
     */
    private String parseCheckoutError(Response<Map<String, Object>> response) {
        try {
            if (response.errorBody() == null) {
                return "Checkout failed. Please try again.";
            }
            String raw = response.errorBody().string();
            Log.e(TAG, "Error response body: " + raw);

            // Parse with Gson
            com.google.gson.JsonObject json = new com.google.gson.JsonParser()
                    .parse(raw).getAsJsonObject();

            // Check for a structured "errors" map first (Laravel validation)
            if (json.has("errors") && json.get("errors").isJsonObject()) {
                com.google.gson.JsonObject errors = json.getAsJsonObject("errors");

                // Special case: shipping_method_id not configured on the backend yet
                if (errors.has("shipping_method_id")) {
                    return "Shipping is not configured yet. Please contact the store — no shipping methods are set up.";
                }

                // Return the first validation message we find
                for (Map.Entry<String, com.google.gson.JsonElement> entry : errors.entrySet()) {
                    if (entry.getValue().isJsonArray()
                            && entry.getValue().getAsJsonArray().size() > 0) {
                        String field = entry.getKey().replace("_", " ");
                        String msg   = entry.getValue().getAsJsonArray().get(0).getAsString();
                        // Capitalise first letter
                        if (!msg.isEmpty()) {
                            msg = Character.toUpperCase(msg.charAt(0)) + msg.substring(1);
                        }
                        return msg;
                    }
                }
            }

            // Fall back to the top-level "message" field
            if (json.has("message") && !json.get("message").isJsonNull()) {
                String msg = json.get("message").getAsString();
                if (!msg.isEmpty() && !"The given data was invalid.".equals(msg)) {
                    return msg;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Could not parse error body", e);
        }
        return "Checkout failed. Please check your details and try again.";
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
