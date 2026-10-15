package aeu.ratha.beckie_deal_app.utils.helpers;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import aeu.ratha.beckie_deal_app.utils.preferences.SessionManager;
import aeu.ratha.beckie_deal_app.utils.preferences.ScrollStatePreferences;
import aeu.ratha.beckie_deal_app.utils.preferences.ThemePreferences;

/**
 * Base activity for the app.
 *
 * Responsibilities:
 * 1. Dark mode preference restoration (ThemePreferences)
 * 2. Session timeout detection (SessionManager) - 5 minute inactivity
 * 3. Activity lifecycle tracking for state preservation
 *
 * When user returns after 5+ minutes of inactivity:
 * - All saved scroll positions are cleared
 * - Cached repositories are flushed
 * - Fresh data will be fetched from the API on next navigation
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Restore the user's saved dark mode preference before the UI is drawn.
        ThemePreferences.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        
        // Check for session timeout on activity creation
        checkSessionTimeout();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Check for session timeout when returning to activity
        checkSessionTimeout();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Record that user is leaving the activity
        SessionManager.recordActivity(this);
    }
    
    /**
     * Check if session has expired due to 5+ minutes of inactivity.
     * If expired, clear scroll positions and cached data to force fresh API fetches.
     */
    private void checkSessionTimeout() {
        try {
            if (SessionManager.hasSessionExpired(this)) {
                Log.d(TAG, "Session expired - clearing cached state");
                onSessionExpired();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking session timeout: " + e.getMessage());
        }
    }
    
    /**
     * Called when session has timed out (> 5 minutes of inactivity).
     * Clears all saved scroll positions, filters, and cached data.
     * 
     * Subclasses can override this to perform additional cleanup if needed.
     */
    protected void onSessionExpired() {
        try {
            // 1. Clear all saved scroll positions and filter states
            ScrollStatePreferences.clearPageState(this, "");  // Passing empty string clears all for this app
            
            // Clear SharedPreferences used for scroll state
            getSharedPreferences("scroll_state_prefs", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
            
            // 2. Update session timestamp to prevent repeated expiration checks
            SessionManager.recordActivity(this);
            
            Log.d(TAG, "Session expired - all cached state cleared, fresh data will be fetched");
        } catch (Exception e) {
            Log.e(TAG, "Error in onSessionExpired: " + e.getMessage());
        }
    }
}
