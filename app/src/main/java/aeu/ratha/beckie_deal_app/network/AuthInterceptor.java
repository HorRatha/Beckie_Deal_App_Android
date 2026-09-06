package aeu.ratha.beckie_deal_app.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import aeu.ratha.beckie_deal_app.security.TokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp Interceptor that automatically adds JWT token to API requests.
 * Skips auth endpoints (login, register) to prevent circular dependency.
 */
public class AuthInterceptor implements Interceptor {
    private static final String TAG = "AuthInterceptor";
    private final TokenManager tokenManager;
    
    public AuthInterceptor(Context context) {
        this.tokenManager = TokenManager.getInstance(context);
    }
    
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        
        // Get the request path
        String path = originalRequest.url().encodedPath();
        
        // Skip adding token for auth endpoints (login, register)
        if (shouldSkipAuth(path)) {
            Log.d(TAG, "Skipping auth for: " + path);
            return chain.proceed(originalRequest);
        }
        
        // Get the access token
        String token = tokenManager.getAccessToken();
        
        // If no token, proceed without authorization header
        if (token == null || token.isEmpty()) {
            Log.d(TAG, "No token available, proceeding without auth");
            return chain.proceed(originalRequest);
        }
        
        // Add Authorization header with Bearer token
        Request authenticatedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .build();
        
        Log.d(TAG, "Added auth token to request: " + path);
        return chain.proceed(authenticatedRequest);
    }
    
    /**
     * Determine if the request should skip authentication.
     * @param path URL path
     * @return true if auth should be skipped
     */
    private boolean shouldSkipAuth(String path) {
        return path.contains("/auth/login") 
                || path.contains("/auth/register")
                || path.contains("/auth/forgot-password")
                || path.contains("/auth/reset-password");
    }
}
