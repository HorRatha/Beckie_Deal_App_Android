package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.CategoryResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for category endpoints.
 */
public interface CategoryApiService {
    
    /**
     * Get all master categories with pagination.
     * Full URL: {BASE_URL}/master/categories
     * 
     * @param limit Maximum number of categories to return
     * @param offset Number of categories to skip
     * @return Call object with CategoryResponse
     */
    @GET("master/categories")
    Call<CategoryResponse> getCategories(
        @Query("limit") int limit,
        @Query("offset") int offset
    );
}
