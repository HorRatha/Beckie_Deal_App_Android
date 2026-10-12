package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.LoginRequest;
import aeu.ratha.beckie_deal_app.data.models.LoginResponse;
import aeu.ratha.beckie_deal_app.data.models.RegisterRequest;
import aeu.ratha.beckie_deal_app.data.models.RegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit API service interface for authentication endpoints.
 * Defines all auth-related API calls matching Laravel backend routes.
 */
public interface AuthApiService {
    
    /**
     * Login user with email and password.
     * POST /api/v1/auth/login
     * 
     * @param request LoginRequest containing email and password
     * @return Call object with LoginResponse containing token and user data
     */
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    
    /**
     * Register new user account.
     * POST /api/v1/auth/register
     * 
     * @param request RegisterRequest containing user registration data
     * @return Call object with RegisterResponse containing token and user data
     */
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    
    /**
     * Logout current user (requires authentication).
     * POST /api/v1/auth/logout
     * 
     * @return Call object with Void response
     */
    @POST("auth/logout")
    Call<Void> logout();
    
    /**
     * Get current authenticated user profile.
     * GET /api/v1/auth/user
     * 
     * @return Call object with User profile data
     */
    @retrofit2.http.GET("auth/user")
    Call<aeu.ratha.beckie_deal_app.data.models.User> getUserProfile();
    
    /**
     * Verify email OTP after registration.
     * POST /api/v1/auth/verify-email-otp
     * 
     * @param request VerifyEmailOtpRequest containing email and 6-digit OTP
     * @return Call object with VerifyEmailOtpResponse containing token and user data
     */
    @POST("auth/verify-email-otp")
    Call<aeu.ratha.beckie_deal_app.data.models.VerifyEmailOtpResponse> verifyEmailOtp(@Body aeu.ratha.beckie_deal_app.data.models.VerifyEmailOtpRequest request);
    
    /**
     * Resend email OTP for verification.
     * POST /api/v1/auth/resend-email-otp
     * 
     * @param email The email to resend OTP to
     * @return Call object with generic response
     */
    @POST("auth/resend-email-otp")
    Call<aeu.ratha.beckie_deal_app.data.models.LoginResponse> resendEmailOtp(@retrofit2.http.Body aeu.ratha.beckie_deal_app.data.models.LoginRequest email);
    
    // Future auth endpoints can be added here:
    // @POST("auth/refresh")
    // Call<LoginResponse> refreshToken(@Body RefreshTokenRequest request);
    
    // @POST("auth/forgot-password")
    // Call<MessageResponse> forgotPassword(@Body ForgotPasswordRequest request);
    
    // @POST("auth/reset-password")
    // Call<MessageResponse> resetPassword(@Body ResetPasswordRequest request);
}
