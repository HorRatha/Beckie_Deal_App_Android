package aeu.ratha.beckie_deal_app.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Chat Integration Utility
 * Handles opening Facebook, Facebook Messenger, and Telegram conversations.
 */
public class ChatIntegration {
    
    private static final String TAG = "ChatIntegration";
    
    // Facebook Page URL
    public static final String FACEBOOK_PAGE_URL = "https://www.facebook.com/share/1SWPzaY49M/?mibextid=wwXIfr";
    
    // Facebook Messenger URL
    public static final String FACEBOOK_MESSENGER_URL = "https://m.me/beckiedealmac";
    
    // Telegram URL
    public static final String TELEGRAM_URL = "https://t.me/beckiedealsale";
    
    /**
     * Open Facebook page URL.
     * 
     * @param activity The current activity context
     */
    public static void openFacebook(Activity activity) {
        if (activity == null) return;
        try {
            Uri uri = Uri.parse(FACEBOOK_PAGE_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (isPackageInstalled(activity, "com.facebook.katana")) {
                intent.setPackage("com.facebook.katana");
            }
            activity.startActivity(intent);
            logChatInteraction("facebook", FACEBOOK_PAGE_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error opening Facebook app, trying browser fallback: " + e.getMessage());
            openInBrowser(activity, FACEBOOK_PAGE_URL, "Facebook");
        }
    }
    
    /**
     * Open Facebook Messenger conversation.
     * 
     * @param activity The current activity context
     * @param productName Optional product name for context logging
     */
    public static void openFacebookMessenger(Activity activity, String productName) {
        if (activity == null) return;
        try {
            Uri uri = Uri.parse(FACEBOOK_MESSENGER_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (isPackageInstalled(activity, "com.facebook.orca")) {
                intent.setPackage("com.facebook.orca");
            }
            activity.startActivity(intent);
            logChatInteraction("facebook_messenger", productName != null ? productName : FACEBOOK_MESSENGER_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error opening Messenger app, trying browser fallback: " + e.getMessage());
            openInBrowser(activity, FACEBOOK_MESSENGER_URL, "Messenger");
        }
    }
    
    /**
     * Open Telegram channel or chat.
     * 
     * @param activity The current activity context
     * @param productName Optional product name for context logging
     */
    public static void openTelegram(Activity activity, String productName) {
        if (activity == null) return;
        try {
            Uri uri = Uri.parse(TELEGRAM_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (isPackageInstalled(activity, "org.telegram.messenger")) {
                intent.setPackage("org.telegram.messenger");
            }
            activity.startActivity(intent);
            logChatInteraction("telegram", productName != null ? productName : TELEGRAM_URL);
        } catch (Exception e) {
            Log.e(TAG, "Error opening Telegram app, trying browser fallback: " + e.getMessage());
            openInBrowser(activity, TELEGRAM_URL, "Telegram");
        }
    }

    private static void openInBrowser(Activity activity, String url, String serviceName) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(browserIntent);
        } catch (Exception ex) {
            Log.e(TAG, "Error opening browser for " + serviceName + ": " + ex.getMessage());
            Toast.makeText(activity, "Unable to open " + serviceName + ". Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Check if a package/app is installed on the device
     */
    private static boolean isPackageInstalled(Activity activity, String packageName) {
        try {
            activity.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Log chat interaction for analytics
     */
    private static void logChatInteraction(String platform, String context) {
        Log.d(TAG, "Chat interaction - Platform: " + platform + ", Context: " + context);
    }
    
    /**
     * Build message context for chat
     */
    public static String buildProductContext(String productName, String productId, String productPrice) {
        return String.format(
            "Hi! I have a question about %s (ID: %s, Price: %s)",
            productName, productId, productPrice
        );
    }
}
