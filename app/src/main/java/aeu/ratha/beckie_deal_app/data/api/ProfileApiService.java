package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.User;
import aeu.ratha.beckie_deal_app.data.models.ProfileApiResponse;
import aeu.ratha.beckie_deal_app.data.models.MyProductsResponse;
import aeu.ratha.beckie_deal_app.data.models.ChangePasswordRequest;
import aeu.ratha.beckie_deal_app.data.models.ChangePasswordResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Retrofit API service interface for user profile endpoints.
 * Defines all profile-related API calls matching Laravel backend routes.
 */
public interface ProfileApiService {
    
    /**
     * Get authenticated user profile information.
     * GET /api/v1/profile
     * 
     * Returns wrapped response: { status, message, data: User }
     * Requires authentication token in Authorization header.
     * Returns user profile data: first_name, last_name, email, phone
     * 
     * @return Call object with ProfileApiResponse containing User data
     */
    @GET("profile")
    Call<ProfileApiResponse> getProfile();
    
    /**
     * Upload or replace user profile avatar image.
     * POST /api/v1/profile/avatar
     * 
     * Requires authentication token in Authorization header.
     * Multipart form field: image
     * Accepted formats: JPEG, PNG, GIF, or WebP, up to 5 MB
     * 
     * @param imageFile Image file as MultipartBody.Part
     * @return Call object with User profile data (updated with avatar URL)
     */
    @Multipart
    @POST("profile/avatar")
    Call<User> uploadAvatar(@Part MultipartBody.Part imageFile);
    
    /**
     * Get user's purchased/ordered products.
     * GET /api/v1/my-products?per_page={perPage}&page={page}
     * 
     * Requires authentication token in Authorization header.
     * Returns a paginated list of products the user has purchased/ordered.
     * 
     * Response structure:
     * {
     *   "status": "success",
     *   "message": "",
     *   "data": {
     *     "items": [...],
     *     "pagination": {
     *       "total": 0,
     *       "per_page": 20,
     *       "current_page": 1,
     *       "last_page": 1
     *     }
     *   }
     * }
     * 
     * @param perPage Number of items per page (default: 20)
     * @param page Current page number (default: 1)
     * @return Call object with MyProductsResponse
     */
    @GET("my-products")
    Call<MyProductsResponse> getMyProducts(
        @Query("per_page") int perPage,
        @Query("page") int page
    );
    
    /**
     * Change user password.
     * POST /api/v1/auth/change-password
     * 
     * Requires authentication token in Authorization header.
     * Request body must contain:
     * - current_password: The user's current password
     * - new_password: The desired new password
     * - new_password_confirmation: Confirmation of the new password (must match new_password)
     * 
     * Response structure:
     * {
     *   "status": "success",
     *   "message": "Password changed successfully.",
     *   "data": null
     * }
     * 
     * @param request ChangePasswordRequest containing current and new passwords
     * @return Call object with ChangePasswordResponse
     */
    @POST("auth/change-password")
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest request);
}
