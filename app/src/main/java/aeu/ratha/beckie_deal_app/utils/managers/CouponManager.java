package aeu.ratha.beckie_deal_app.utils.managers;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import aeu.ratha.beckie_deal_app.data.api.CouponApiService;
import aeu.ratha.beckie_deal_app.data.models.Cart;
import aeu.ratha.beckie_deal_app.data.models.CouponResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Manages coupon codes, validation, and discount calculation.
 * Handles API calls for coupon application to cart.
 * 
 * ⚠️ IMPORTANT: Backend Implementation Status
 * 
 * The CouponApiService endpoint is NOT YET IMPLEMENTED (404) on the backend.
 * This manager gracefully handles 404 errors and allows the app to continue functioning.
 * 
 * Architecture:
 * - All coupon API calls enqueue via CouponApiService
 * - 404 responses are caught and logged (not treated as fatal errors)
 * - When backend implements the endpoint, callbacks will receive proper responses
 * - No code changes needed in this manager once backend is ready
 * 
 * Expected Backend Implementation:
 * [ ] POST /api/v1/coupons/apply - Apply coupon code to cart
 *      Request: { "code": "WELCOME10", "cart_id": "1" }
 *      Response: { "status": "success", "data": { "coupon": "WELCOME10", 
 *                  "discount_amount": 159.9, "grand_total": 1439.1, "cart": {...} } }
 * 
 * Current Functionality:
 * - Coupon input field in checkout (UI ready)
 * - Methods implemented but handle 404 gracefully
 * - Checkout proceeds even if coupon endpoint unavailable
 * 
 * See CouponApiService.java for endpoint contract details.
 */
public class CouponManager {
    private static final String TAG = "CouponManager";
    private static CouponManager instance;
    
    private Context context;
    private CouponApiService apiService;
    
    // Current applied coupon state
    private String appliedCouponCode;
    private double appliedDiscountAmount;
    private Cart updatedCart;

    private CouponManager() {
        // Constructor is private for singleton
    }

    public static synchronized CouponManager getInstance() {
        if (instance == null) {
            instance = new CouponManager();
        }
        return instance;
    }
    
    /**
     * Initialize with context (call this early in app lifecycle)
     */
    public void initialize(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = RetrofitClient.getClient(context).create(CouponApiService.class);
    }
    
    /**
     * Get API service, initializing if needed
     */
    private CouponApiService getApiService() {
        if (apiService == null && context != null) {
            apiService = RetrofitClient.getClient(context).create(CouponApiService.class);
        }
        return apiService;
    }

    /**
     * Apply coupon code via API
     * @param code Coupon code to apply
     * @param cartId Cart ID to apply coupon to
     * @param callback Callback with result
     */
    public void applyCoupon(String code, String cartId, Context context, final CouponCallback callback) {
        if (code == null || code.trim().isEmpty()) {
            callback.onError("Please enter a coupon code");
            return;
        }
        
        if (cartId == null || cartId.isEmpty()) {
            callback.onError("Cart not found");
            return;
        }
        
        // Initialize if needed
        if (apiService == null) {
            initialize(context);
        }
        
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("code", code.trim().toUpperCase());
        requestData.put("cart_id", cartId);
        
        Log.d(TAG, "Applying coupon: " + code + " to cart: " + cartId);
        
        getApiService().applyCoupon(requestData).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> body = response.body();
                    String status = (String) body.get("status");
                    
                    if ("success".equals(status)) {
                        // Extract coupon data
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) body.get("data");
                        
                        if (data != null) {
                            appliedCouponCode = (String) data.get("coupon");
                            
                            Object discountObj = data.get("discount_amount");
                            if (discountObj instanceof Number) {
                                appliedDiscountAmount = ((Number) discountObj).doubleValue();
                            }
                            
                            Log.d(TAG, "✓ Coupon applied: " + appliedCouponCode + 
                                ", Discount: $" + appliedDiscountAmount);
                            
                            callback.onSuccess(appliedCouponCode, appliedDiscountAmount);
                        } else {
                            callback.onError("Invalid response from server");
                        }
                    } else {
                        String message = (String) body.get("message");
                        callback.onError(message != null ? message : "Failed to apply coupon");
                    }
                } else {
                    Log.e(TAG, "✗ Failed to apply coupon: " + response.code());
                    callback.onError("Invalid coupon code or server error");
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "✗ Network error applying coupon", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Restore coupon state from an already-applied backend cart response.
     * Called when the cart loads and already has a coupon_code + discount_total.
     * Does NOT make an API call — it just syncs local state with what the backend reports.
     */
    public void applyCouponFromCart(String couponCode, double discountAmount) {
        if (couponCode == null || couponCode.isEmpty()) return;
        this.appliedCouponCode = couponCode;
        this.appliedDiscountAmount = discountAmount;
        Log.d(TAG, "Coupon state restored from cart: " + couponCode + " / $" + discountAmount);
    }

    /**
     * Remove currently applied coupon (local state only)
     * Note: Backend doesn't have a remove endpoint, so we just clear local state
     * The cart will be refreshed to get the updated state without coupon
     */
    public void removeCoupon() {
        appliedCouponCode = null;
        appliedDiscountAmount = 0.0;
        updatedCart = null;
        Log.d(TAG, "Coupon removed from local state");
    }

    /**
     * Check if a coupon is currently applied
     */
    public boolean hasCoupon() {
        return appliedCouponCode != null && !appliedCouponCode.isEmpty();
    }
    
    /**
     * Get currently applied coupon code
     */
    public String getAppliedCouponCode() {
        return appliedCouponCode;
    }
    
    /**
     * Get current discount amount
     */
    public double getDiscountAmount() {
        return appliedDiscountAmount;
    }
    
    /**
     * Calculate discount for current applied coupon
     * @deprecated Use getDiscountAmount() instead - discount is calculated by backend
     */
    @Deprecated
    public double calculateDiscount(double subtotal) {
        return appliedDiscountAmount;
    }
    
    /**
     * Callback interface for coupon operations
     */
    public interface CouponCallback {
        void onSuccess(String couponCode, double discountAmount);
        void onError(String errorMessage);
    }
    
    /**
     * Legacy compatibility: Result object for coupon application attempts
     * @deprecated Use CouponCallback instead
     */
    @Deprecated
    public static class CouponResult {
        public final boolean success;
        public final String message;
        public final Object coupon;

        public CouponResult(boolean success, String message, Object coupon) {
            this.success = success;
            this.message = message;
            this.coupon = coupon;
        }
    }
}
