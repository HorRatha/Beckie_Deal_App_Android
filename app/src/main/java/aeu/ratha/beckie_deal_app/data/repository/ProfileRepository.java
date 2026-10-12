package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import aeu.ratha.beckie_deal_app.data.api.ProfileApiService;
import aeu.ratha.beckie_deal_app.data.models.User;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for managing user profile data from the API.
 * 
 * Handles fetching, caching, and uploading of the authenticated user's profile information.
 */
public class ProfileRepository {

    private static final String TAG = "ProfileRepository";
    private static ProfileRepository instance;

    private final ProfileApiService apiService;
    
    /** Cached user profile data */
    private User cachedProfile;

    private ProfileRepository(Context context) {
        this.apiService = RetrofitClient.getClient(context).create(ProfileApiService.class);
    }

    /**
     * Get singleton instance of ProfileRepository.
     * 
     * @param context Application context
     * @return ProfileRepository singleton instance
     */
    public static synchronized ProfileRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileRepository(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Fetch authenticated user profile information.
     * 
     * GET /api/v1/profile
     * Requires authentication token in Authorization header.
     * Returns: { status, message, data: { first_name, last_name, email, phone, ... } }
     * 
     * @param useCache Return cached profile if available (set to false to force refresh)
     * @param callback Result callback
     */
    public void getProfile(boolean useCache, @NonNull ProfileCallback callback) {
        // Return cached profile if requested and available
        if (useCache && cachedProfile != null) {
            Log.d(TAG, "Returning cached user profile");
            callback.onSuccess(cachedProfile);
            return;
        }

        Log.d(TAG, "Fetching user profile from API - endpoint: /api/v1/profile");
        apiService.getProfile().enqueue(new Callback<aeu.ratha.beckie_deal_app.data.models.ProfileApiResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<aeu.ratha.beckie_deal_app.data.models.ProfileApiResponse> call,
                                   @NonNull retrofit2.Response<aeu.ratha.beckie_deal_app.data.models.ProfileApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    aeu.ratha.beckie_deal_app.data.models.ProfileApiResponse apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.hasData()) {
                        User profile = apiResponse.getData();
                        cachedProfile = profile;
                        Log.d(TAG, "Profile fetched successfully");
                        Log.d(TAG, "  Email: " + profile.getEmail());
                        Log.d(TAG, "  FirstName: " + profile.getFirstName());
                        Log.d(TAG, "  LastName: " + profile.getLastName());
                        Log.d(TAG, "  Name: " + profile.getName());
                        Log.d(TAG, "  FullName: " + profile.getFullName());
                        callback.onSuccess(profile);
                    } else {
                        String errorMsg = "API Error: " + apiResponse.getStatus() + " - " + apiResponse.getMessage();
                        Log.e(TAG, errorMsg);
                        callback.onFailure(new Exception(errorMsg));
                    }
                } else {
                    String errorMsg = "HTTP " + response.code() + ": " + response.message();
                    Log.e(TAG, "API Error: " + errorMsg);
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Response error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not read error body: " + e.getMessage());
                    }
                    callback.onFailure(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<aeu.ratha.beckie_deal_app.data.models.ProfileApiResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                callback.onFailure(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    /**
     * Upload user profile avatar image.
     * 
     * POST /api/v1/profile/avatar
     * Multipart form field: image
     * Accepted formats: JPEG, PNG, GIF, or WebP, up to 5 MB
     * 
     * @param imageFile Image file as MultipartBody.Part
     * @param callback Result callback with updated User profile
     */
    public void uploadAvatar(@NonNull MultipartBody.Part imageFile, 
                            @NonNull AvatarUploadCallback callback) {
        Log.d(TAG, "Uploading avatar image");
        
        apiService.uploadAvatar(imageFile).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,
                                   @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User profile = response.body();
                    cachedProfile = profile; // Update cache with new profile
                    Log.d(TAG, "Avatar uploaded successfully");
                    callback.onSuccess(profile);
                } else {
                    String errorMsg = "HTTP " + response.code() + ": " + response.message();
                    Log.e(TAG, "Avatar upload error: " + errorMsg);
                    callback.onFailure(new Exception(errorMsg));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.e(TAG, "Avatar upload network error: " + t.getMessage(), t);
                callback.onFailure(new Exception("Network error: " + t.getMessage(), t));
            }
        });
    }

    /**
     * Clear cached profile (useful on logout).
     */
    public void clearCache() {
        Log.d(TAG, "Clearing profile cache");
        cachedProfile = null;
    }

    /**
     * Get currently cached profile (may be null).
     * 
     * @return Cached User profile or null if not loaded
     */
    public User getCachedProfile() {
        return cachedProfile;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Callback Interfaces
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Callback interface for profile fetch operations.
     */
    public interface ProfileCallback {
        /**
         * Called when profile fetch succeeds.
         * 
         * @param profile The User profile object
         */
        void onSuccess(User profile);

        /**
         * Called when profile fetch fails.
         * 
         * @param error Exception describing the error
         */
        void onFailure(Exception error);
    }

    /**
     * Callback interface for avatar upload operations.
     */
    public interface AvatarUploadCallback {
        /**
         * Called when avatar upload succeeds.
         * 
         * @param profile Updated User profile with new avatar data
         */
        void onSuccess(User profile);

        /**
         * Called when avatar upload fails.
         * 
         * @param error Exception describing the error
         */
        void onFailure(Exception error);
    }
}
