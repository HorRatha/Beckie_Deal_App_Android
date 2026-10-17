package aeu.ratha.beckie_deal_app.utils.avatar;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/**
 * Manages avatar generation for both logged-in and guest users.
 * 
 * For logged-in users: Uses uploaded profile avatar URL
 * For guest users: Generates DiceBear avatar URL based on device ID
 * 
 * DiceBear provides unique, colorful avatars based on a seed string.
 * Same seed = Same avatar (ensures consistency across app sessions)
 */
public class AvatarManager {
    
    private static final String PREFS_NAME = "avatar_prefs";
    private static final String GUEST_SEED_KEY = "guest_avatar_seed";
    private static final String DICEBEAR_BASE_URL = "https://api.dicebear.com/7.x/avataaars/svg";
    
    /**
     * Get avatar URL for a user.
     * 
     * @param context Context for SharedPreferences and device ID access
     * @param avatarUrl Profile avatar URL (null or empty for guest users)
     * @return Avatar image URL (either profile URL or DiceBear URL)
     */
    public static String getAvatarUrl(Context context, String avatarUrl) {
        // If user has uploaded an avatar, use their profile avatar
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            return avatarUrl;
        }
        
        // Otherwise, generate DiceBear avatar for guest
        return generateGuestAvatarUrl(context);
    }
    
    /**
     * Generate DiceBear avatar URL for guest users.
     * Uses device ID as seed, or generates a random seed if this is first run.
     * 
     * @param context Context for SharedPreferences and device ID
     * @return DiceBear avatar URL
     */
    private static String generateGuestAvatarUrl(Context context) {
        String seed = getOrCreateGuestAvatarSeed(context);
        
        // Format: https://api.dicebear.com/7.x/avataaars/svg?seed=SEED&size=256
        return String.format(
            "%s?seed=%s&size=256&scale=80&flip=false",
            DICEBEAR_BASE_URL,
            seed
        );
    }
    
    /**
     * Get or create a consistent guest avatar seed.
     * 
     * Strategy:
     * 1. First run: Create seed from device ANDROID_ID (unique per device)
     * 2. Subsequent runs: Retrieve stored seed for consistency
     * 
     * This ensures guest users see the same avatar every time they open the app
     * (as long as they don't clear app data).
     * 
     * @param context Context
     * @return Seed string for DiceBear
     */
    private static String getOrCreateGuestAvatarSeed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Check if we already have a seed stored
        String existingSeed = prefs.getString(GUEST_SEED_KEY, null);
        if (existingSeed != null) {
            android.util.Log.d("AvatarManager", "Using existing guest seed: " + existingSeed);
            return existingSeed;
        }
        
        // Create new seed from device ANDROID_ID
        String seed = "guest_" + getDeviceId(context);
        
        // Store for future use
        prefs.edit().putString(GUEST_SEED_KEY, seed).apply();
        
        android.util.Log.d("AvatarManager", "Created new guest seed: " + seed);
        return seed;
    }
    
    /**
     * Get unique device ID (ANDROID_ID).
     * This is unique per device and persists even after app uninstall
     * (until device factory reset).
     * 
     * @param context Context
     * @return Device ID string
     */
    private static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    
    /**
     * Clear guest avatar seed (call on logout or app reset).
     * This will generate a new avatar next time.
     * 
     * @param context Context
     */
    public static void clearGuestAvatarSeed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(GUEST_SEED_KEY).apply();
        android.util.Log.d("AvatarManager", "Cleared guest avatar seed");
    }
    
    /**
     * Force generate a new guest avatar seed.
     * Useful for "Change Avatar" feature.
     * 
     * @param context Context
     * @return New avatar URL
     */
    public static String generateNewGuestAvatar(Context context) {
        clearGuestAvatarSeed(context);
        String newUrl = generateGuestAvatarUrl(context);
        android.util.Log.d("AvatarManager", "Generated new guest avatar URL: " + newUrl);
        return newUrl;
    }
    
    /**
     * DiceBear Style Options
     * (You can add UI to let users choose avatar style)
     */
    public enum AvatarStyle {
        AVATAAARS("avataaars"),           // Default - fun avatars
        PIXEL_ART("pixel-art"),            // Pixel style
        PERSONAS("personas"),              // Simple personas
        LORELEI("lorelei"),                // Lorelei style
        MICAH("micah");                    // Micah style
        
        private final String value;
        
        AvatarStyle(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    /**
     * Generate avatar URL with custom style.
     * 
     * @param context Context
     * @param style Avatar style (from AvatarStyle enum)
     * @return Avatar URL with custom style
     */
    public static String generateGuestAvatarUrlWithStyle(Context context, AvatarStyle style) {
        String seed = getOrCreateGuestAvatarSeed(context);
        return String.format(
            "https://api.dicebear.com/7.x/%s/svg?seed=%s&size=256&scale=80&flip=false",
            style.getValue(),
            seed
        );
    }
}
