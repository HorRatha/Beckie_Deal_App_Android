package aeu.ratha.beckie_deal_app.security;

import android.content.Context;
import android.util.Log;

import aeu.ratha.beckie_deal_app.data.models.User;

/**
 * Singleton class for managing JWT authentication tokens.
 * Provides a centralized API for token storage, retrieval, and user session management.
 */
public class TokenManager {
    private static final String TAG = "TokenManager";
    private static TokenManager instance;
    private final SecurePreferences securePrefs;
    
    /**
     * Private constructor for singleton pattern.
     * @param context Application context
     */
    private TokenManager(Context context) {
        this.securePrefs = new SecurePreferences(context.getApplicationContext());
    }
    
    /**
     * Get singleton instance of TokenManager.
     * @param context Application or Activity context
     * @return TokenManager instance
     */
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Save authentication data after successful login/register.
     * @param accessToken JWT access token
     * @param tokenType Token type (e.g., "Bearer")
     * @param expiresIn Token expiration in seconds
     * @param user User object containing user information
     */
    public void saveAuthData(String accessToken, String tokenType, int expiresIn, User user) {
        securePrefs.saveAuthToken(accessToken, tokenType, expiresIn);
        
        if (user != null) {
            String userName = user.getFullName();
            securePrefs.saveUserInfo(
                    user.getId(),
                    user.getEmail(),
                    userName
            );
        }
        
        Log.d(TAG, "Authentication data saved successfully");
    }
    
    /**
     * Get the stored access token.
     * @return Access token or null if not logged in
     */
    public String getAccessToken() {
        return securePrefs.getAccessToken();
    }
    
    /**
     * Get the full authorization header value.
     * @return "Bearer {token}" or null if not logged in
     */
    public String getAuthorizationHeader() {
        String token = getAccessToken();
        if (token != null && !token.isEmpty()) {
            String tokenType = securePrefs.getTokenType();
            return tokenType + " " + token;
        }
        return null;
    }
    
    /**
     * Get the stored token type.
     * @return Token type (usually "Bearer")
     */
    public String getTokenType() {
        return securePrefs.getTokenType();
    }
    
    /**
     * Get token expiration time.
     * @return Expiration in seconds
     */
    public int getExpiresIn() {
        return securePrefs.getExpiresIn();
    }
    
    /**
     * Get the logged-in user's ID.
     * @return User ID or null
     */
    public String getUserId() {
        return securePrefs.getUserId();
    }
    
    /**
     * Get the logged-in user's email.
     * @return User email or null
     */
    public String getUserEmail() {
        return securePrefs.getUserEmail();
    }
    
    /**
     * Get the logged-in user's name.
     * @return User name or null
     */
    public String getUserName() {
        return securePrefs.getUserName();
    }
    
    /**
     * Check if user is currently logged in.
     * @return true if valid access token exists, false otherwise
     */
    public boolean isLoggedIn() {
        return securePrefs.isLoggedIn();
    }
    
    /**
     * Logout user and clear all stored authentication data.
     */
    public void logout() {
        securePrefs.clearAll();
        Log.d(TAG, "User logged out, all data cleared");
    }
    
    /**
     * Clear only tokens while keeping user info.
     * Useful for token refresh scenarios.
     */
    public void clearTokens() {
        securePrefs.clearTokens();
        Log.d(TAG, "Tokens cleared");
    }
    
    /**
     * Update only the access token (useful for token refresh).
     * @param newAccessToken New JWT token
     * @param expiresIn New expiration time
     */
    public void updateAccessToken(String newAccessToken, int expiresIn) {
        String tokenType = securePrefs.getTokenType();
        securePrefs.saveAuthToken(newAccessToken, tokenType, expiresIn);
        Log.d(TAG, "Access token updated");
    }
}
