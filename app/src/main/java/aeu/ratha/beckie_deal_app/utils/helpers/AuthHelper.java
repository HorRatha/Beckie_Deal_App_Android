package aeu.ratha.beckie_deal_app.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import aeu.ratha.beckie_deal_app.ui.activities.auth.LoginActivity;

/**
 * Helper class for authentication validation and user prompts.
 * Ensures users are logged in before accessing protected features like cart, wishlist, and checkout.
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
