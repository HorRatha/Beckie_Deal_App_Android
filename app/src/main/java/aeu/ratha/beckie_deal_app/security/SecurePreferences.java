package aeu.ratha.beckie_deal_app.security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Secure storage for sensitive data using EncryptedSharedPreferences.
 * Uses AES256-GCM encryption backed by Android Keystore.
 */
public class SecurePreferences {
    private static final String TAG = "SecurePreferences";
    private static final String PREFS_NAME = "beckie_deal_secure_prefs";
    
    // Keys for storage
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_TOKEN_TYPE = "token_type";
    private static final String KEY_EXPIRES_IN = "expires_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    
    private final SharedPreferences prefs;
    
    /**
     * Create SecurePreferences instance with encrypted storage.
     * @param context Application context
     */
    public SecurePreferences(Context context) {
        SharedPreferences tempPrefs = null;
        try {
            // Create or retrieve the master key for encryption
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            
            // Create encrypted shared preferences
            tempPrefs = EncryptedSharedPreferences.create(
                    context,
                    PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Failed to create encrypted preferences, falling back to regular SharedPreferences", e);
            // Fallback to regular SharedPreferences if encryption fails
            tempPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        this.prefs = tempPrefs;
    }
    
    /**
     * Save authentication tokens securely.
     * @param accessToken JWT access token
     * @param tokenType Token type (usually "Bearer")
     * @param expiresIn Token expiration time in seconds
     */
    public void saveAuthToken(String accessToken, String tokenType, int expiresIn) {
        prefs.edit()
                .putString(KEY_ACCESS_TOKEN, accessToken)
                .putString(KEY_TOKEN_TYPE, tokenType)
                .putInt(KEY_EXPIRES_IN, expiresIn)
                .apply();
        Log.d(TAG, "Auth token saved successfully");
    }
    
    /**
     * Get stored access token.
     * @return Access token or null if not found
     */
    public String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }
    
    /**
     * Get stored token type.
     * @return Token type (e.g., "Bearer") or null if not found
     */
    public String getTokenType() {
        return prefs.getString(KEY_TOKEN_TYPE, "Bearer");
    }
    
    /**
     * Get token expiration time.
     * @return Expiration time in seconds or 0 if not found
     */
    public int getExpiresIn() {
        return prefs.getInt(KEY_EXPIRES_IN, 0);
    }
    
    /**
     * Save user information.
     * @param userId User ID
     * @param email User email
     * @param name User name
     */
    public void saveUserInfo(String userId, String email, String name) {
        prefs.edit()
                .putString(KEY_USER_ID, userId)
                .putString(KEY_USER_EMAIL, email)
                .putString(KEY_USER_NAME, name)
                .apply();
        Log.d(TAG, "User info saved successfully");
    }
    
    /**
     * Get stored user ID.
     * @return User ID or null if not found
     */
    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }
    
    /**
     * Get stored user email.
     * @return User email or null if not found
     */
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, null);
    }
    
    /**
     * Get stored user name.
     * @return User name or null if not found
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }
    
    /**
     * Check if user is logged in (has valid access token).
     * @return true if access token exists, false otherwise
     */
    public boolean isLoggedIn() {
        String token = getAccessToken();
        return token != null && !token.isEmpty();
    }
    
    /**
     * Clear all stored authentication data (logout).
     */
    public void clearAll() {
        prefs.edit().clear().apply();
        Log.d(TAG, "All secure data cleared");
    }
    
    /**
     * Clear only authentication tokens (keep user info).
     */
    public void clearTokens() {
        prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_TOKEN_TYPE)
                .remove(KEY_EXPIRES_IN)
                .apply();
        Log.d(TAG, "Auth tokens cleared");
    }
}
