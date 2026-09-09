package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.ProductResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for product endpoints.
 */
public interface ProductApiService {
    
    /**
     * Get all products with pagination.
     * Full URL: {BASE_URL}/products
     * 
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @return Call object with ProductResponse
     */
    @GET("products")
    Call<ProductResponse> getProducts(
        @Query("limit") int limit,
        @Query("offset") int offset
    );
    
    /**
     * Get products filtered by category.
     * Full URL: {BASE_URL}/products?category_id={categoryId}
     * 
     * @param categoryId Category ID to filter by
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @return Call object with ProductResponse
     */
    @GET("products")
    Call<ProductResponse> getProductsByCategory(
        @Query("category_id") int categoryId,
        @Query("limit") int limit,
        @Query("offset") int offset
    );
    
    /**
     * Get products filtered by brand.
     * Full URL: {BASE_URL}/products?brand_id={brandId}
     * 
     * @param brandId Brand ID to filter by
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @return Call object with ProductResponse
     */
    @GET("products")
    Call<ProductResponse> getProductsByBrand(
        @Query("brand_id") int brandId,
        @Query("limit") int limit,
        @Query("offset") int offset
    );
    
    /**
     * Get products filtered by both category and brand.
     * Full URL: {BASE_URL}/products?category_id={categoryId}&brand_id={brandId}
     * 
     * @param categoryId Category ID to filter by
     * @param brandId Brand ID to filter by
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @return Call object with ProductResponse
     */
    @GET("products")
    Call<ProductResponse> getProductsByCategoryAndBrand(
        @Query("category_id") int categoryId,
        @Query("brand_id") int brandId,
        @Query("limit") int limit,
        @Query("offset") int offset
    );
    
    /**
     * Get featured products.
     * Full URL: {BASE_URL}/products?is_featured=true
     * 
     * @param isFeatured Filter for featured products
     * @param limit Maximum number of products to return
     * @param offset Number of products to skip
     * @return Call object with ProductResponse
     */
    @GET("products")
    Call<ProductResponse> getFeaturedProducts(
        @Query("is_featured") boolean isFeatured,
        @Query("limit") int limit,
        @Query("offset") int offset
    );
    
    /**
     * Get a single product by ID.
     * Full URL: {BASE_URL}/products/{id}
     * 
     * @param productId Product ID
     * @return Call object with single product response (data will be an object, not array)
     */
    @GET("products/{id}")
    Call<ProductResponse> getProduct(
        @Path("id") int productId
    );
    
    /**
     * Get product variants for a specific product.
     * Full URL: {BASE_URL}/products/{id}/variants
     * 
     * @param productId Product ID
     * @return Call object with product variants response
     */
    @GET("products/{id}/variants")
    Call<ProductResponse> getProductVariants(
        @Path("id") int productId
    );
}
