package aeu.ratha.beckie_deal_app.data.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * API service for coupon operations.
 * Handles coupon validation and application to carts.
 */
public interface CouponApiService {
    
    /**
     * Apply coupon code to cart.
     * 
     * Request:
     * {
     *   "code": "WELCOME10",
     *   "cart_id": "1"
     * }
     * 
     * Response:
     * {
     *   "status": "success",
     *   "data": {
     *     "coupon": "WELCOME10",
     *     "discount_amount": 159.9,
     *     "grand_total": 1439.1,
     *     "cart": { ... updated cart with coupon applied ... }
     *   }
     * }
     * 
     * @param data Map containing "code" and "cart_id"
     * @return Coupon response with updated cart
     */
    @Headers("Accept: application/json")
    @POST("coupons/apply")
    Call<Map<String, Object>> applyCoupon(@Body Map<String, Object> data);
}
