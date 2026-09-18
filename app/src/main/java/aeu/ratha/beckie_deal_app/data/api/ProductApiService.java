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
 * Notes on backend constraints (confirmed by live testing):
 *  - GET /products returns a flat list; category_id / brand_id fields are NOT included per item.
 *  - The ?category_id= and ?brand_id= query filters expect UUID format, but the
 *    master/categories and master/brands endpoints return integer IDs → those filters
 *    are unusable client-side.  Category/brand filtering is done client-side after fetching
 *    all products and loading their details individually.
 *  - GET /products/{id} returns the full product with category, brand, variants, images.
 *  - ?search= performs a name-based text search.
 *  - ?is_featured= is accepted but currently ignored by the backend (returns all products).
 */
public interface ProductApiService {

    /**
     * Get all products with pagination.
     * GET /products?limit={limit}&offset={offset}
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
