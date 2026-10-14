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
     * Get all categories with hierarchical structure.
     * Full URL: {BASE_URL}/api/v1/categories
     * 
     * Note: This endpoint returns categories directly in data array
     * with parent categories having a "children" array for subcategories.
     * The endpoint does NOT support pagination.
     * 
     * @return Call object with CategoryResponse containing flat list of categories
     */
    @GET("categories")
    Call<CategoryResponse> getCategories();
}
