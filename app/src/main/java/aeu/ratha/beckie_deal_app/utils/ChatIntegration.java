package aeu.ratha.beckie_deal_app.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

/**
 * Chat Integration Utility
 * Handles opening Facebook, Facebook Messenger, and Telegram conversations
 */
public class ChatIntegration {
    
    private static final String TAG = "ChatIntegration";
    
    // Facebook Configuration
    public static final String FACEBOOK_PAGE_URL = "https://www.facebook.com/YOUR_PAGE_NAME";
    public static final String FACEBOOK_PAGE_ID = "YOUR_PAGE_ID_HERE";
    
    // Facebook Messenger Configuration
    public static final String FACEBOOK_MESSENGER_URL = "https://m.me/YOUR_PAGE_ID_HERE";
    
    // Telegram Configuration
    public static final String TELEGRAM_BOT_USERNAME = "YOUR_BOT_USERNAME_HERE";
    
    /**
     * Open Facebook page
     * 
     * @param activity The current activity context
     */
    public static void openFacebook(Activity activity) {
        try {
            // Try to open via Facebook app
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            facebookIntent.setData(Uri.parse("fb://page/" + FACEBOOK_PAGE_ID));
            
            // Check if Facebook app is installed
            if (isPackageInstalled(activity, "com.facebook.katana")) {
                facebookIntent.setPackage("com.facebook.katana");
                activity.startActivity(facebookIntent);
                Log.d(TAG, "Opened Facebook app");
            } else {
                // Fallback to browser
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(FACEBOOK_PAGE_URL));
                activity.startActivity(webIntent);
                Log.d(TAG, "Opened Facebook via browser");
            }
            
            logChatInteraction("facebook", "Visit page");
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening Facebook: " + e.getMessage());
            try {
                // Fallback: Open web version
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse(FACEBOOK_PAGE_URL));
                activity.startActivity(webIntent);
            } catch (Exception ex) {
                Toast.makeText(activity, "Unable to open Facebook. Please check your connection.", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Open Facebook Messenger conversation with a page
     * 
     * @param activity The current activity context
     * @param productName Optional product name for context
     */
    public static void openFacebookMessenger(Activity activity, String productName) {
        try {
            // Try to open via Facebook Messenger app
            Intent messengerIntent = new Intent(Intent.ACTION_VIEW);
            messengerIntent.setData(Uri.parse("https://m.me/" + FACEBOOK_PAGE_ID));
            
            // Check if package is installed
            if (isPackageInstalled(activity, "com.facebook.orca")) {
                messengerIntent.setPackage("com.facebook.orca");
                activity.startActivity(messengerIntent);
                Log.d(TAG, "Opened Facebook Messenger app");
            } else {
                // Fallback to browser
                activity.startActivity(messengerIntent);
                Log.d(TAG, "Opened Facebook Messenger via browser");
            }
            
            logChatInteraction("facebook_messenger", productName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening Facebook Messenger: " + e.getMessage());
            // Fallback: Open web version
            try {
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse("https://m.facebook.com/" + FACEBOOK_PAGE_ID));
                activity.startActivity(webIntent);
            } catch (Exception ex) {
                Toast.makeText(activity, "Unable to open Messenger. Please install it from Play Store.", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Open Telegram conversation with bot or group
     * 
     * @param activity The current activity context
     * @param productName Optional product name for context
     */
    public static void openTelegram(Activity activity, String productName) {
        try {
            // Try to open via Telegram app
            Intent telegramIntent = new Intent(Intent.ACTION_VIEW);
            
            // Direct to bot
            telegramIntent.setData(Uri.parse("https://t.me/" + TELEGRAM_BOT_USERNAME));
            
            // Check if package is installed
            if (isPackageInstalled(activity, "org.telegram.messenger")) {
                telegramIntent.setPackage("org.telegram.messenger");
                activity.startActivity(telegramIntent);
                Log.d(TAG, "Opened Telegram app");
            } else {
                // Fallback to browser
                activity.startActivity(telegramIntent);
                Log.d(TAG, "Opened Telegram via browser");
            }
            
            logChatInteraction("telegram", productName);
            
        } catch (Exception e) {
            Log.e(TAG, "Error opening Telegram: " + e.getMessage());
            // Fallback: Open web version
            try {
                Intent webIntent = new Intent(Intent.ACTION_VIEW);
                webIntent.setData(Uri.parse("https://t.me/" + TELEGRAM_BOT_USERNAME));
                activity.startActivity(webIntent);
            } catch (Exception ex) {
                Toast.makeText(activity, "Unable to open Telegram. Please install it from Play Store.", 
                    Toast.LENGTH_SHORT).show();
            }
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
        // TODO: Add Firebase Analytics or other tracking here
        // FirebaseAnalytics.getInstance(context).logEvent("chat_opened", bundle);
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
