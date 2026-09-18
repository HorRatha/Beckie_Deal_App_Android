package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.PromotionResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for promotions/coupons endpoints.
 * Base URL: /api/v1
 *
 * STATUS: ✅ WORKING
 * 
 * The backend now returns active promotions/coupon codes available to users.
 * This endpoint is used to display promotional offers on the home screen.
 */
public interface PromotionApiService {
    
    /**
     * Get all active promotions/coupon codes with pagination.
     * GET /api/v1/promotions?limit={limit}&offset={offset}
     * 
     * @param limit Number of promotions per page (default: 20)
     * @param offset Starting position (default: 0)
     * @return Call object with PromotionResponse containing list of promotions
     */
    @GET("promotions")
    Call<PromotionResponse> getPromotions(
        @Query("limit") int limit,
        @Query("offset") int offset
    );
    
    /**
     * Get all active promotions with default pagination.
     * GET /api/v1/promotions
     * 
     * @return Call object with PromotionResponse
     */
    @GET("promotions")
    Call<PromotionResponse> getPromotions();
}
