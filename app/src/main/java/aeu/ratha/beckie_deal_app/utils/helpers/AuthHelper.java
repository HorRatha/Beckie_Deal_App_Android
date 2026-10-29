package aeu.ratha.beckie_deal_app.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import aeu.ratha.beckie_deal_app.ui.activities.auth.LoginActivity;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.utils.preferences.ScrollStatePreferences;
import aeu.ratha.beckie_deal_app.utils.preferences.SessionManager;

/**
 * Helper class for authentication validation, logout, and user prompts.
 * 
 * Responsibilities:
 * - Check authentication status
 * - Prompt users to login when needed
 * - Centralized logout with secure state clearing
 * - Cleanup of all user-specific data on logout
 * 
 * Security:
 * - All tokens cleared via TokenManager
 * - All saved scroll positions and filters cleared
 * - All in-memory caches cleared
 * - App restarted with clean task stack
 */
public class AuthHelper {
    
    private static final String TAG = "AuthHelper";
    
    // Choose your preferred authentication prompt style
    public enum PromptStyle {
        DIRECT_NAVIGATION,  // Recommended: Navigate directly to login (like Amazon, eBay)
        SNACKBAR_PROMPT,    // Modern: Snackbar with action button
        DIALOG_PROMPT       // Traditional: Material Dialog with options
    }
    
    // Set your preferred style here - can be changed app-wide
    private static PromptStyle DEFAULT_PROMPT_STYLE = PromptStyle.DIRECT_NAVIGATION;

    /**
     * Check if user is authenticated.
     * 
     * @param context Application or Activity context
     * @return true if user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn(Context context) {
        TokenManager tokenManager = TokenManager.getInstance(context);
        return tokenManager.isLoggedIn();
    }

    /**
     * Centralized logout method - SECURE STATE CLEARING.
     * 
     * This method securely logs out the user and ensures all sensitive data is cleared:
     * 1. Clears JWT tokens via TokenManager
     * 2. Clears all saved scroll positions and filter preferences
     * 3. Clears in-memory caches (repositories)
     * 4. Clears session tracking data
     * 5. Restarts app to MainActivity with clean task stack
     * 
     * @param context Application or Activity context
     */
    public static void logout(Context context) {
        try {
            Log.d(TAG, "Starting secure logout...");
            
            // 1. Clear JWT Tokens (secure storage)
            TokenManager.getInstance(context).logout();
            Log.d(TAG, "JWT tokens cleared");
            
            // 2. Clear All Saved Scroll Positions & Filter Preferences
            try {
                context.getSharedPreferences("scroll_state_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();
                Log.d(TAG, "Scroll state preferences cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing scroll state: " + e.getMessage());
            }
            
            // 3. Clear session tracking data
            SessionManager.clearSessionTimestamp(context);
            Log.d(TAG, "Session timestamp cleared");
            
            // 4. Clear other preferences that might contain user-specific data
            try {
                context.getSharedPreferences("view_mode_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();
                Log.d(TAG, "View mode preferences cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing view mode: " + e.getMessage());
            }

            // 5. Clear user notifications and order caches on logout
            try {
                context.getSharedPreferences("beckie_deal_notifications_pref", Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();
                Log.d(TAG, "Notifications preferences cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing notifications: " + e.getMessage());
            }

            try {
                context.getSharedPreferences("beckie_deal_product_images_cache", Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();
                Log.d(TAG, "Product image cache cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing product image cache: " + e.getMessage());
            }

            // Clear in-memory cart and static cache on logout
            try {
                aeu.ratha.beckie_deal_app.utils.managers.CartManager.getInstance().clearCart();
                aeu.ratha.beckie_deal_app.ui.activities.cart.CartActivity.clearStaticCache();
                Log.d(TAG, "Cart memory and static cache cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing cart state: " + e.getMessage());
            }

            Log.d(TAG, "Logout completed successfully - restarting app");
            
            // 6. Restart to MainActivity with clean task stack
            // This ensures no Guest user data is visible to the next logged-in user
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            // Even if there's an error, try to navigate back to MainActivity
            try {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
            } catch (Exception e2) {
                Log.e(TAG, "Error navigating to MainActivity: " + e2.getMessage());
            }
        }
    }
    
    /**
     * Login success - clear Guest data and prepare for logged-in user.
     * 
     * Call this after successful login to clear any guest-user scroll state
     * and ensure fresh data is loaded for the logged-in user.
     * 
     * @param context Application or Activity context
     */
    public static void onLoginSuccess(Context context) {
        try {
            Log.d(TAG, "Login successful - clearing guest state");
            
            // Clear previous Guest scroll positions & search queries
            try {
                context.getSharedPreferences("scroll_state_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();
                Log.d(TAG, "Guest scroll state cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing guest scroll state: " + e.getMessage());
            }
            
            // Clear session timestamp to reset inactivity timer
            SessionManager.clearSessionTimestamp(context);
            Log.d(TAG, "Session timestamp reset for new user");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onLoginSuccess: " + e.getMessage());
        }
    }

    /**
     * Check authentication and show prompt if not authenticated.
     * Uses the default prompt style.
     * 
     * @param activity Current activity
     * @param featureName Name of the feature being accessed (e.g., "add to cart", "wishlist")
     * @return true if user is authenticated, false if login is required
     */
    public static boolean requireAuthentication(Activity activity, String featureName) {
        return requireAuthentication(activity, featureName, DEFAULT_PROMPT_STYLE);
    }
    
    /**
     * Check authentication with specific prompt style.
     * 
     * @param activity Current activity
     * @param featureName Name of the feature requiring authentication
     * @param style Prompt style to use
     * @return true if user is authenticated, false if login is required
     */
    public static boolean requireAuthentication(Activity activity, String featureName, PromptStyle style) {
        if (isUserLoggedIn(activity)) {
            return true;
        }
        
        switch (style) {
            case DIRECT_NAVIGATION:
                navigateToLogin(activity, featureName);
                break;
            case SNACKBAR_PROMPT:
                showSnackbarPrompt(activity, featureName);
                break;
            case DIALOG_PROMPT:
                showLoginPrompt(activity, featureName);
                break;
        }
        
        return false;
    }

    /**
     * Navigate directly to login screen with message.
     * Recommended approach - fast and clear.
     * 
     * @param activity Current activity
     * @param featureName Feature that requires authentication
     */
    private static void navigateToLogin(Activity activity, String featureName) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra("message", "Please login to " + featureName);
        intent.putExtra("feature", featureName);
        activity.startActivity(intent);
    }

    /**
     * Show modern Snackbar with login action.
     * Good middle ground between dialog and direct navigation.
     * 
     * @param activity Current activity
     * @param featureName Feature that requires authentication
     */
    private static void showSnackbarPrompt(Activity activity, String featureName) {
        View rootView = activity.findViewById(android.R.id.content);
        
        Snackbar.make(rootView, "Login required to " + featureName, Snackbar.LENGTH_LONG)
                .setAction("LOGIN", v -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.putExtra("message", "Login to " + featureName);
                    activity.startActivity(intent);
                })
                .setActionTextColor(activity.getResources().getColor(R.color.primary, null))
                .show();
    }

    /**
     * Show a Material Dialog prompting user to login or register.
     * Traditional approach with most options.
     * 
     * @param activity Current activity
     * @param featureName Name of the feature requiring authentication
     */
    public static void showLoginPrompt(Activity activity, String featureName) {
        new MaterialAlertDialogBuilder(activity, R.style.Theme_MaterialComponents_DayNight_Dialog_Alert_Rounded)
                .setTitle("Login Required")
                .setMessage("Please login or create an account to " + featureName + ".")
                .setIcon(R.drawable.ic_lock_24)
                .setPositiveButton("Login", (dialog, which) -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.putExtra("message", "Login to " + featureName);
                    activity.startActivity(intent);
                })
                .setNegativeButton("Register", (dialog, which) -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.putExtra("openRegister", true); // Open register tab
                    activity.startActivity(intent);
                })
                .setNeutralButton("Cancel", null)
                .setCancelable(true)
                .show();
    }

    /**
     * Show a custom styled dialog with better visual design.
     * 
     * @param activity Current activity
     * @param title Dialog title
     * @param message Dialog message
     */
    public static void showCustomLoginPrompt(Activity activity, String title, String message) {
        new MaterialAlertDialogBuilder(activity, R.style.Theme_MaterialComponents_DayNight_Dialog_Alert_Rounded)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_lock_24)
                .setPositiveButton("Login", (dialog, which) -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                })
                .setNegativeButton("Register", (dialog, which) -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.putExtra("openRegister", true); // Open register tab
                    activity.startActivity(intent);
                })
                .setNeutralButton("Later", null)
                .setCancelable(true)
                .show();
    }

    /**
     * Redirect to login screen directly without showing a dialog.
     * 
     * @param activity Current activity
     */
    public static void redirectToLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Check authentication for cart operations.
     * 
     * @param activity Current activity
     * @return true if user is authenticated, false otherwise
     */
    public static boolean requireAuthForCart(Activity activity) {
        return requireAuthentication(activity, "add items to cart");
    }

    /**
     * Check authentication for wishlist operations.
     * 
     * @param activity Current activity
     * @return true if user is authenticated, false otherwise
     */
    public static boolean requireAuthForWishlist(Activity activity) {
        return requireAuthentication(activity, "add items to wishlist");
    }

    /**
     * Check authentication for purchase/checkout operations.
     * 
     * @param activity Current activity
     * @return true if user is authenticated, false otherwise
     */
    public static boolean requireAuthForPurchase(Activity activity) {
        return requireAuthentication(activity, "complete your purchase");
    }
    
    /**
     * Set the default prompt style for the entire app.
     * Call this in Application.onCreate() or MainActivity.onCreate()
     * 
     * @param style The prompt style to use by default
     */
    public static void setDefaultPromptStyle(PromptStyle style) {
        DEFAULT_PROMPT_STYLE = style;
    }

    /**
     * Get the current user's display name.
     * 
     * @param context Application or Activity context
     * @return User's name or "Guest" if not logged in
     */
    public static String getUserDisplayName(Context context) {
        TokenManager tokenManager = TokenManager.getInstance(context);
        if (tokenManager.isLoggedIn()) {
            String name = tokenManager.getUserName();
            return name != null && !name.isEmpty() ? name : "User";
        }
        return "Guest";
    }

    /**
     * Get the current user's email.
     * 
     * @param context Application or Activity context
     * @return User's email or null if not logged in
     */
    public static String getUserEmail(Context context) {
        TokenManager tokenManager = TokenManager.getInstance(context);
        if (tokenManager.isLoggedIn()) {
            return tokenManager.getUserEmail();
        }
        return null;
    }
}
