package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import aeu.ratha.beckie_deal_app.data.api.ReviewApiService;
import aeu.ratha.beckie_deal_app.data.models.ProductReviewsResponse;
import aeu.ratha.beckie_deal_app.data.models.Review;
import aeu.ratha.beckie_deal_app.data.models.ReviewCreateRequest;
import aeu.ratha.beckie_deal_app.data.models.ReviewsResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for managing review data from the API.
 *
 * Handles:
 *  - Fetching user's own reviews
 *  - Fetching reviews for a specific product
 *  - Creating new reviews
 *  - Updating existing reviews
 *  - Deleting reviews
 */
public class ReviewRepository {

    private static final String TAG = "ReviewRepository";
    private static ReviewRepository instance;

    private final ReviewApiService apiService;

    /** Cache for user's reviews */
    private final List<Review> cachedUserReviews = new ArrayList<>();

    /** Cache for product reviews keyed by product ID */
    private final Map<String, List<Review>> productReviewsCache = new HashMap<>();

    /** Cache timestamp tracking */
    private static final long CACHE_VALIDITY_MS = 5 * 60 * 1000;  // 5 minutes
    private long lastUserReviewsCacheTime = 0;
    private final Map<String, Long> productReviewsCacheTime = new HashMap<>();

    private ReviewRepository(Context context) {
        this.apiService = RetrofitClient.getClient(context).create(ReviewApiService.class);
    }

    public static synchronized ReviewRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ReviewRepository(context.getApplicationContext());
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cache Validity Checking
    // ─────────────────────────────────────────────────────────────────────────

    private boolean isUserReviewsCacheFresh() {
        if (cachedUserReviews.isEmpty()) return false;
        long timeSinceCache = System.currentTimeMillis() - lastUserReviewsCacheTime;
        boolean fresh = timeSinceCache < CACHE_VALIDITY_MS;
        Log.d(TAG, "User reviews cache age: " + (timeSinceCache / 1000) + "s, Fresh: " + fresh);
        return fresh;
    }

    private boolean isProductReviewsCacheFresh(String productId) {
        if (!productReviewsCache.containsKey(productId)) return false;
        Long cacheTime = productReviewsCacheTime.get(productId);
        if (cacheTime == null) return false;
        long timeSinceCache = System.currentTimeMillis() - cacheTime;
        boolean fresh = timeSinceCache < CACHE_VALIDITY_MS;
        Log.d(TAG, "Product reviews cache (" + productId + ") age: " + (timeSinceCache / 1000) + "s, Fresh: " + fresh);
        return fresh;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // User's Reviews Operations
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get current user's reviews with pagination.
     * Fetches from cache if fresh, otherwise from API.
     *
     * @param page     Page number (1-indexed)
     * @param perPage  Items per page
     * @param callback Callback with reviews or error
     */
    public void getMyReviews(int page, int perPage, 
                             @NonNull ReviewsCallback callback) {
        // Use cache only for first page if fresh
        if (page == 1 && isUserReviewsCacheFresh()) {
            Log.d(TAG, "Using cached user reviews");
            callback.onReviewsLoaded(cachedUserReviews);
            return;
        }

        apiService.getMyReviews(page, perPage).enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewsResponse> call,
                                   @NonNull Response<ReviewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewsResponse reviewsResponse = response.body();
                    if (reviewsResponse.isSuccess() && reviewsResponse.getData() != null) {
                        List<Review> reviews = reviewsResponse.getData().getItems();
                        
                        // Cache only if first page
                        if (page == 1) {
                            cachedUserReviews.clear();
                            if (reviews != null) {
                                cachedUserReviews.addAll(reviews);
                            }
                            lastUserReviewsCacheTime = System.currentTimeMillis();
                        }
                        
                        callback.onReviewsLoaded(reviews != null ? reviews : new ArrayList<>());
                    } else {
                        callback.onError("Failed to load reviews: " + reviewsResponse.getMessage());
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewsResponse> call,
                                   @NonNull Throwable t) {
                Log.e(TAG, "Error fetching user reviews", t);
                callback.onError("Error: " + t.getMessage());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Product Reviews Operations
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Get all reviews for a specific product with pagination.
     * Fetches from cache if fresh, otherwise from API.
     *
     * @param productId Product UUID
     * @param page      Page number (1-indexed)
     * @param perPage   Items per page
     * @param callback  Callback with product reviews response or error
     */
    public void getProductReviews(@NonNull String productId, int page, int perPage,
                                   @NonNull ProductReviewsCallback callback) {
        // Use cache only for first page if fresh
        if (page == 1 && isProductReviewsCacheFresh(productId)) {
            Log.d(TAG, "Using cached product reviews for " + productId);
            List<Review> cached = productReviewsCache.get(productId);
            if (cached != null) {
                callback.onProductReviewsLoaded(productId, cached);
                return;
            }
        }

        apiService.getProductReviews(productId, page, perPage)
            .enqueue(new Callback<ProductReviewsResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductReviewsResponse> call,
                                       @NonNull Response<ProductReviewsResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ProductReviewsResponse reviewsResponse = response.body();
                        if (reviewsResponse.isSuccess() && reviewsResponse.getData() != null) {
                            List<Review> reviews = reviewsResponse.getData().getItems();
                            
                            // Cache only if first page
                            if (page == 1) {
                                if (reviews != null) {
                                    productReviewsCache.put(productId, new ArrayList<>(reviews));
                                } else {
                                    productReviewsCache.put(productId, new ArrayList<>());
                                }
                                productReviewsCacheTime.put(productId, System.currentTimeMillis());
                            }
                            
                            callback.onProductReviewsLoaded(productId, reviews != null ? reviews : new ArrayList<>());
                            if (reviewsResponse.getData().getSummary() != null) {
                                callback.onReviewSummary(productId, reviewsResponse.getData().getSummary());
                            }
                        } else {
                            callback.onError("Failed to load product reviews: " + reviewsResponse.getMessage());
                        }
                    } else {
                        callback.onError("Network error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProductReviewsResponse> call,
                                       @NonNull Throwable t) {
                    Log.e(TAG, "Error fetching product reviews", t);
                    callback.onError("Error: " + t.getMessage());
                }
            });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Create Review Operation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Create a new review for a product.
     *
     * @param request  Review data (rating, title, comment, product_id, order_id)
     * @param callback Callback with created review or error
     */
    public void createReview(@NonNull ReviewCreateRequest request,
                             @NonNull ReviewCreateCallback callback) {
        if (!request.isValid()) {
            callback.onError("Invalid review data: missing required fields");
            return;
        }

        apiService.createReview(request).enqueue(new Callback<ReviewApiService.ReviewCreateResponse>() {
            @Override
            public void onResponse(@NonNull Call<ReviewApiService.ReviewCreateResponse> call,
                                   @NonNull Response<ReviewApiService.ReviewCreateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewApiService.ReviewCreateResponse result = response.body();
                    if ("success".equalsIgnoreCase(result.status) && result.data != null) {
                        // Invalidate user reviews cache since we added a new review
                        invalidateUserReviewsCache();
                        callback.onReviewCreated(result.data);
                    } else {
                        callback.onError("Failed to create review: " + result.message);
                    }
                } else {
                    callback.onError("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ReviewApiService.ReviewCreateResponse> call,
                                   @NonNull Throwable t) {
                Log.e(TAG, "Error creating review", t);
                callback.onError("Error: " + t.getMessage());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Update Review Operation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Update an existing review.
     *
     * @param reviewId Review ID
     * @param request  Updated review data
     * @param callback Callback with updated review or error
     */
    public void updateReview(long reviewId, @NonNull ReviewCreateRequest request,
                             @NonNull ReviewCreateCallback callback) {
        if (!request.isValid()) {
            callback.onError("Invalid review data: missing required fields");
            return;
        }

        apiService.updateReview(reviewId, request)
            .enqueue(new Callback<ReviewApiService.ReviewCreateResponse>() {
                @Override
                public void onResponse(@NonNull Call<ReviewApiService.ReviewCreateResponse> call,
                                       @NonNull Response<ReviewApiService.ReviewCreateResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ReviewApiService.ReviewCreateResponse result = response.body();
                        if ("success".equalsIgnoreCase(result.status) && result.data != null) {
                            // Invalidate caches since review was updated
                            invalidateUserReviewsCache();
                            invalidateProductReviewsCache(result.data.getProductId());
                            callback.onReviewCreated(result.data);
                        } else {
                            callback.onError("Failed to update review: " + result.message);
                        }
                    } else {
                        callback.onError("Network error: " + response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ReviewApiService.ReviewCreateResponse> call,
                                       @NonNull Throwable t) {
                    Log.e(TAG, "Error updating review", t);
                    callback.onError("Error: " + t.getMessage());
                }
            });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Delete Review Operation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Delete a review.
     *
     * @param reviewId   Review ID
     * @param productId  Product ID (for cache invalidation)
     * @param callback   Callback with success or error
     */
    public void deleteReview(long reviewId, @Nullable String productId,
                             @NonNull DeleteReviewCallback callback) {
        apiService.deleteReview(reviewId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Invalidate caches since review was deleted
                    invalidateUserReviewsCache();
                    if (productId != null) {
                        invalidateProductReviewsCache(productId);
                    }
                    callback.onReviewDeleted();
                } else {
                    callback.onError("Failed to delete review: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call,
                                   @NonNull Throwable t) {
                Log.e(TAG, "Error deleting review", t);
                callback.onError("Error: " + t.getMessage());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cache Invalidation
    // ─────────────────────────────────────────────────────────────────────────

    private void invalidateUserReviewsCache() {
        cachedUserReviews.clear();
        lastUserReviewsCacheTime = 0;
        Log.d(TAG, "User reviews cache invalidated");
    }

    private void invalidateProductReviewsCache(String productId) {
        productReviewsCache.remove(productId);
        productReviewsCacheTime.remove(productId);
        Log.d(TAG, "Product reviews cache invalidated for " + productId);
    }

    public void invalidateAllCaches() {
        invalidateUserReviewsCache();
        productReviewsCache.clear();
        productReviewsCacheTime.clear();
        Log.d(TAG, "All review caches invalidated");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Callback Interfaces
    // ─────────────────────────────────────────────────────────────────────────

    public interface ReviewsCallback {
        void onReviewsLoaded(List<Review> reviews);
        void onError(String error);
    }

    public interface ProductReviewsCallback {
        void onProductReviewsLoaded(String productId, List<Review> reviews);
        void onReviewSummary(String productId, aeu.ratha.beckie_deal_app.data.models.ReviewSummary summary);
        void onError(String error);
    }

    public interface ReviewCreateCallback {
        void onReviewCreated(Review review);
        void onError(String error);
    }

    public interface DeleteReviewCallback {
        void onReviewDeleted();
        void onError(String error);
    }
}
