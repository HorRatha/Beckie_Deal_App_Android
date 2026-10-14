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
 * IMPORTANT API LIMITATIONS (confirmed by live testing):
 *  - GET /products endpoint currently returns ~12 items regardless of limit parameter
 *  - Response does NOT include pagination metadata (total, page info)
 *  - Products list does NOT include brand_id or category_id fields
 *  - Cannot filter by category_id or brand_id on the server side
 *  - Must fetch individual product details to get complete brand/category info
 *
 * BACKEND IMPROVEMENTS NEEDED:
 *  1. Add brand_id and category_id to products list response
 *  2. Include pagination object: {total, current_page, per_page, last_page}
 *  3. Support higher limits (currently capped at ~12 items)
 *  4. Populate products_count in categories endpoint
 *
 * WORKAROUNDS (client-side):
 *  - Fetch individual product details to get brand/category info
 *  - Locally count products per category/brand
 *  - Cache product details for filtering
 */
public interface ProductApiService {

    /**
     * Get all products with pagination.
     * GET /products?limit={limit}&offset={offset}
     * 
     * NOTE: API currently ignores limit parameter and returns ~12 items.
     * Use higher numbers (200, 300) to attempt fetching more, though
     * actual response may still be limited by backend.
     */
    @GET("products")
    Call<ProductResponse> getProducts(
        @Query("limit")  int limit,
        @Query("offset") int offset
    );

    /**
     * Search products by name keyword.
     * GET /products?search={query}&limit={limit}&offset={offset}
     */
    @GET("products")
    Call<ProductResponse> searchProducts(
        @Query("search") String query,
        @Query("limit")  int limit,
        @Query("offset") int offset
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
