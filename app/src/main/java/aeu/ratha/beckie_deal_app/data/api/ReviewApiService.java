package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.ProductReviewsResponse;
import aeu.ratha.beckie_deal_app.data.models.Review;
import aeu.ratha.beckie_deal_app.data.models.ReviewCreateRequest;
import aeu.ratha.beckie_deal_app.data.models.ReviewsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for review endpoints.
 *
 * Base URL: {API_BASE_URL}  (e.g. https://.../api/v1/)
 *
 * ENDPOINTS:
 *  - GET /reviews?page={page}&per_page={perPage}      - Get user's reviews (paginated)
 *  - POST /reviews                                       - Create a new review
 *  - GET /reviews/{reviewId}                            - Get review detail
 *  - PATCH /reviews/{reviewId}                          - Update an existing review
 *  - DELETE /reviews/{reviewId}                         - Delete a review (no content response)
 *  - GET /products/{product_id}/reviews                 - Get all reviews for a product
 *  - GET /products/{product_id}/related                 - Get related products
 */
public interface ReviewApiService {

    /**
     * Get the current user's reviews with pagination.
     * GET /reviews?page={page}&per_page={perPage}
     *
     * @param page     Page number (1-indexed)
     * @param perPage  Items per page (default 10)
     * @return Paginated list of user's reviews with summary
     */
    @GET("reviews")
    Call<ReviewsResponse> getMyReviews(
        @Query("page") int page,
        @Query("per_page") int perPage
    );

    /**
     * Create a new review for a product.
     * POST /reviews
     *
     * Requires both product_id and order_id since review is tied to a purchase.
     *
     * @param request Review creation data (rating, title, comment, product_id, order_id)
     * @return Created review data with ID
     */
    @POST("reviews")
    Call<ReviewCreateResponse> createReview(
        @Body ReviewCreateRequest request
    );

    /**
     * Get details of a specific review.
     * GET /reviews/{reviewId}
     *
     * @param reviewId Review ID
     * @return Full review data
     */
    @GET("reviews/{reviewId}")
    Call<ReviewDetailResponse> getReviewDetail(
        @Path("reviewId") long reviewId
    );

    /**
     * Update an existing review.
     * PATCH /reviews/{reviewId}
     *
     * Only the review author can update their own review.
     *
     * @param reviewId Review ID
     * @param request  Updated review data
     * @return Updated review data
     */
    @PATCH("reviews/{reviewId}")
    Call<ReviewCreateResponse> updateReview(
        @Path("reviewId") long reviewId,
        @Body ReviewCreateRequest request
    );

    /**
     * Delete a review.
     * DELETE /reviews/{reviewId}
     *
     * Returns 204 No Content on success.
     * Only the review author can delete their own review.
     *
     * @param reviewId Review ID
     * @return Empty response body
     */
    @DELETE("reviews/{reviewId}")
    Call<Void> deleteReview(
        @Path("reviewId") long reviewId
    );

    /**
     * Get all reviews for a specific product.
     * GET /products/{product_id}/reviews
     *
     * Returns reviews with summary statistics (average rating, breakdown by stars).
     *
     * @param productId Product UUID
     * @param page      Page number (default 1)
     * @param perPage   Items per page (default 10)
     * @return Product reviews with summary and pagination
     */
    @GET("products/{product_id}/reviews")
    Call<ProductReviewsResponse> getProductReviews(
        @Path("product_id") String productId,
        @Query("page") int page,
        @Query("per_page") int perPage
    );

    /**
     * Response wrapper for create/update review operations.
     */
    class ReviewCreateResponse {
        public String status;
        public String message;
        public Review data;
    }

    /**
     * Response wrapper for review detail operations.
     */
    class ReviewDetailResponse {
        public String status;
        public String message;
        public Review data;
    }
}
