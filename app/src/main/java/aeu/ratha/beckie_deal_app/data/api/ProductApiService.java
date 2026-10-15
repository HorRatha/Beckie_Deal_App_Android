package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.ProductResponse;
import aeu.ratha.beckie_deal_app.data.models.SingleProductResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for product endpoints.
 *
 * Base URL: {API_BASE_URL}  (e.g. https://.../api/v1/)
 *
 * PAGINATION DETAILS:
 *  - API uses page-based pagination with query params: ?page={page}&per_page={perPage}
 *  - Default per_page is 20 items
 *  - Response does NOT include pagination metadata (total, current_page, last_page)
 *  - Must check response length to determine if more pages exist
 *  - Products list does NOT include brand_id or category_id fields
 *  - Cannot filter by category_id or brand_id on the server side
 *  - Must fetch individual product details to get complete brand/category info
 *
 * FETCHING ALL PRODUCTS:
 *  - For fetching all products, start at page=1 and keep incrementing until:
 *    a) Response has fewer items than per_page (indicates last page), OR
 *    b) Response is empty
 *  - Recommended per_page: 20-100 for balance between request count and response size
 *
 * BACKEND IMPROVEMENTS NEEDED:
 *  1. Add brand_id and category_id to products list response
 *  2. Include pagination metadata: {total, current_page, per_page, last_page}
 *  3. Populate products_count in categories endpoint
 *
 * WORKAROUNDS (client-side):
 *  - Fetch individual product details to get brand/category info
 *  - Locally count products per category/brand
 *  - Cache product details for filtering
 *  - Implement pagination loop in repository to fetch all pages
 */
public interface ProductApiService {

    /**
     * Get all products with page-based pagination.
     * GET /products?sort_by={sortBy}&page={page}&per_page={perPage}
     * 
     * @param sortBy   Sorting option (e.g. "featured", "newest", "popular")
     * @param page     Page number (1-indexed)
     * @param perPage  Items per page (default 20)
     */
    @GET("products")
    Call<ProductResponse> getProducts(
        @Query("sort_by")  String sortBy,
        @Query("page")     int page,
        @Query("per_page") int perPage
    );

    /**
     * Search products by name keyword with pagination.
     * GET /products?search={query}&page={page}&per_page={perPage}
     * 
     * @param query   Search keyword
     * @param page    Page number (1-indexed)
     * @param perPage Items per page (default 20)
     */
    @GET("products")
    Call<ProductResponse> searchProducts(
        @Query("search")   String query,
        @Query("page")     int page,
        @Query("per_page") int perPage
    );

    /**
     * Get a single product by UUID.
     * GET /products/{id}
     *
     * Returns a {@link SingleProductResponse} where data is an OBJECT (not an array),
     * including the nested category, brand, variants and images.
     */
    @GET("products/{id}")
    Call<SingleProductResponse> getProduct(
        @Path("id") String productId
    );
}
