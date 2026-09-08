package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.BrandResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for brand endpoints.
 * Defines all brand-related API calls matching Laravel backend routes.
 */
public interface BrandApiService {
    
    /**
     * Get all brands with pagination.
     * GET /api/v1/master/brands
     * 
     * @param limit Number of items per page (default: 20)
     * @param offset Starting position (default: 0)
     * @return Call object with BrandResponse containing list of brands and pagination
     */
    @GET("master/brands")
    Call<BrandResponse> getBrands(
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );
    
    /**
     * Get all brands with default pagination (20 items, offset 0).
     * GET /api/v1/master/brands
     * 
     * @return Call object with BrandResponse containing list of brands and pagination
     */
    @GET("master/brands")
    Call<BrandResponse> getBrands();
    
    /**
     * Get featured brands only.
     * GET /api/v1/master/brands?featured=true
     * 
     * @param featured Filter for featured brands
     * @param limit Number of items per page
     * @param offset Starting position
     * @return Call object with BrandResponse containing list of featured brands
     */
    @GET("master/brands")
    Call<BrandResponse> getFeaturedBrands(
            @Query("featured") Boolean featured,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset
    );
}
