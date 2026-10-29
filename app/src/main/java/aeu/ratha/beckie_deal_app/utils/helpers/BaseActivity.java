package aeu.ratha.beckie_deal_app.utils.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import aeu.ratha.beckie_deal_app.ui.dialogs.NoInternetDialogFragment;
import aeu.ratha.beckie_deal_app.utils.NetworkConnectivityHelper;
import aeu.ratha.beckie_deal_app.utils.managers.NotificationStorageManager;
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
 * 4. Network connectivity monitoring and error handling
 *
 * When user returns after 5+ minutes of inactivity:
 * - All saved scroll positions are cleared
 * - Cached repositories are flushed
 * - Fresh data will be fetched from the API on next navigation
 *
 * When network is lost:
 * - A no-internet dialog is displayed
 * - Activities can override onNetworkRestored() for custom handling
 */
public abstract class BaseActivity extends AppCompatActivity {
    
    private static final String TAG = "BaseActivity";
    
    private BroadcastReceiver networkChangeReceiver;
    private boolean isNetworkDialogShown = false;
    private boolean wasNetworkAvailable = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Restore the user's saved dark mode preference before the UI is drawn.
        ThemePreferences.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        
        // Check for session timeout on activity creation
        checkSessionTimeout();
        
        // Setup network connectivity monitoring
        setupNetworkConnectivityMonitor();
    }
    
    /**
     * Setup network connectivity broadcast receiver to monitor connection changes.
     */
    private void setupNetworkConnectivityMonitor() {
        try {
            networkChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    checkNetworkConnectivity();
                }
            };
            
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(networkChangeReceiver, filter, Context.RECEIVER_EXPORTED);
            
            // Initial check
            wasNetworkAvailable = NetworkConnectivityHelper.isNetworkAvailable(this);
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up network monitor: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if network connectivity has changed and handle accordingly.
     */
    private void checkNetworkConnectivity() {
        try {
            boolean isNetworkNowAvailable = NetworkConnectivityHelper.isNetworkAvailable(this);
            
            if (!isNetworkNowAvailable && wasNetworkAvailable) {
                // Network just lost
                Log.d(TAG, "Network connection lost");
                onNetworkLost();
                
            } else if (isNetworkNowAvailable && !wasNetworkAvailable) {
                // Network just restored
                Log.d(TAG, "Network connection restored");
                isNetworkDialogShown = false;
                onNetworkRestored();
            }
            
            wasNetworkAvailable = isNetworkNowAvailable;
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking network connectivity: " + e.getMessage(), e);
        }
    }
    
    /**
     * Called when network connection is lost.
     * Shows the no-internet dialog by default.
     * Subclasses can override to customize behavior.
     */
    protected void onNetworkLost() {
        if (!isNetworkDialogShown) {
            isNetworkDialogShown = true;
            NoInternetDialogFragment.show(
                    getSupportFragmentManager(),
                    this::checkNetworkConnectivity,  // Retry
                    () -> {
                        // On close, just dismiss
                    }
            );
        }
    }
    
    /**
     * Called when network connection is restored.
     * Subclasses can override to refresh data or retry failed operations.
     */
    protected void onNetworkRestored() {
        Log.d(TAG, "Network restored - subclasses can override this to refresh data");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Check for session timeout when returning to activity
        checkSessionTimeout();
        
        // Check current network status when resuming
        checkNetworkConnectivity();

        // Update notification badge count
        NotificationStorageManager.updateNotificationBadge(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Record that user is leaving the activity
        SessionManager.recordActivity(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Unregister network receiver to prevent memory leaks
        try {
            if (networkChangeReceiver != null) {
                unregisterReceiver(networkChangeReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering network receiver: " + e.getMessage(), e);
        }
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
