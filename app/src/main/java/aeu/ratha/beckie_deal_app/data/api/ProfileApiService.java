package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.User;
import aeu.ratha.beckie_deal_app.data.models.ProfileApiResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
}
