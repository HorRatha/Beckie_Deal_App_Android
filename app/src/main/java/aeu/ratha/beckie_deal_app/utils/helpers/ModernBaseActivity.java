package aeu.ratha.beckie_deal_app.utils.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import aeu.ratha.beckie_deal_app.utils.NetworkConnectivityHelper;
import aeu.ratha.beckie_deal_app.utils.NetworkStateManager;
import aeu.ratha.beckie_deal_app.utils.preferences.SessionManager;
import aeu.ratha.beckie_deal_app.utils.preferences.ScrollStatePreferences;
import aeu.ratha.beckie_deal_app.utils.preferences.ThemePreferences;

/**
 * Modern BaseActivity for professional apps (Tokopedia/Zendo style).
 *
 * Features:
 * 1. Skeleton loading on app start with no internet
 * 2. Stop loading when network drops mid-load
 * 3. Pull-to-refresh for retry
 * 4. Auto-resume when network returns
 * 5. NO dialogs - just clean UI states
 *
 * Usage:
 *   public class MyActivity extends ModernBaseActivity {
 *       @Override
 *       protected int getLayoutRes() {
 *           return R.layout.activity_my;
 *       }
 *
 *       @Override
 *       protected void loadData() {
 *           // Your data loading logic
 *           showSkeletonLoading();
 *           apiService.getData().subscribe(/* ... *\/);
 *       }
 *   }
 */
public abstract class ModernBaseActivity extends AppCompatActivity {
    
    private static final String TAG = "ModernBaseActivity";
    
    private BroadcastReceiver networkChangeReceiver;
    private boolean wasNetworkAvailable = true;
    
    protected NetworkStateManager stateManager;
    protected SwipeRefreshLayout swipeRefresh;
    protected FrameLayout mainContainer;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // Restore theme
        ThemePreferences.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        
        setContentView(getLayoutRes());
        
        // Initialize views
        initializeStateManager();
        
        // Setup network monitoring
        setupNetworkConnectivityMonitor();
        
        // Check session timeout
        checkSessionTimeout();
        
        // Initial load
        if (NetworkConnectivityHelper.isNetworkAvailable(this)) {
            // Network available - load normally
            loadData();
        } else {
            // No network - show skeleton then empty state
            showSkeletonLoading();
        }
    }
    
    /**
     * Override to return your layout resource.
     * Example: return R.layout.activity_products;
     */
    protected abstract int getLayoutRes();
    
    /**
     * Override to implement your data loading logic.
     */
    protected abstract void loadData();
    
    /**
     * Initialize the state manager for handling skeleton/no-internet states.
     * Find your SwipeRefreshLayout and main container in this method.
     */
    protected void initializeStateManager() {
        try {
            // Try to find SwipeRefreshLayout - if not found, it's optional
            int swipeId = getResources().getIdentifier("swipeRefresh", "id", getPackageName());
            if (swipeId != 0) {
                swipeRefresh = findViewById(swipeId);
                if (swipeRefresh != null) {
                    swipeRefresh.setOnRefreshListener(this::handlePullToRefresh);
                }
            }
            
            // Try to find main container - if not found, use a default
            int containerId = getResources().getIdentifier("mainContainer", "id", getPackageName());
            if (containerId != 0) {
                mainContainer = findViewById(containerId);
            }
            
            stateManager = new NetworkStateManager(this, mainContainer, swipeRefresh);
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing state manager: " + e.getMessage(), e);
        }
    }
    
    /**
     * Handle pull-to-refresh gesture.
     * User swiped down to retry.
     */
    private void handlePullToRefresh() {
        Log.d(TAG, "Pull-to-refresh triggered");
        
        if (NetworkConnectivityHelper.isNetworkAvailable(this)) {
            // Network available - reload data
            loadData();
        } else {
            // Still no network - stop refresh animation
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
        }
    }
    
    /**
     * Setup network connectivity monitoring.
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
            
            wasNetworkAvailable = NetworkConnectivityHelper.isNetworkAvailable(this);
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up network monitor: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if network connectivity has changed.
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
                onNetworkRestored();
            }
            
            wasNetworkAvailable = isNetworkNowAvailable;
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking network: " + e.getMessage(), e);
        }
    }
    
    /**
     * Called when network is lost during use.
     * Stop loading and show no-internet state.
     */
    protected void onNetworkLost() {
        if (stateManager != null) {
            stateManager.stopLoadingDueToNetworkError();
        }
    }
    
    /**
     * Called when network is restored.
     * Auto-reload data.
     */
    protected void onNetworkRestored() {
        if (stateManager != null) {
            stateManager.resumeLoadingAfterNetworkRestore();
        }
        
        // Auto-reload
        loadData();
    }
    
    /**
     * Show skeleton loading animation.
     * Call this before making API calls.
     */
    protected void showSkeletonLoading() {
        if (stateManager != null) {
            stateManager.showSkeletonLoading();
        }
    }
    
    /**
     * Hide loading states.
     * Call this after data is loaded successfully.
     */
    protected void hideLoading() {
        if (stateManager != null) {
            stateManager.hideLoading();
        }
    }
    
    /**
     * Show no-internet state.
     * Call this when loading fails due to network error.
     */
    protected void showNoInternetState() {
        if (stateManager != null) {
            stateManager.showNoInternetState();
        }
    }
    
    /**
     * Check if loading is currently shown.
     */
    protected boolean isLoadingShown() {
        return stateManager != null && stateManager.isLoadingShown();
    }
    
    /**
     * Check if no-internet state is shown.
     */
    protected boolean isNoInternetShown() {
        return stateManager != null && stateManager.isNoInternetShown();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        checkSessionTimeout();
        checkNetworkConnectivity();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        SessionManager.recordActivity(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Cleanup
        try {
            if (networkChangeReceiver != null) {
                unregisterReceiver(networkChangeReceiver);
            }
            if (stateManager != null) {
                stateManager.cleanup();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in cleanup: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check session timeout (existing logic).
     */
    private void checkSessionTimeout() {
        try {
            if (SessionManager.hasSessionExpired(this)) {
                Log.d(TAG, "Session expired");
                onSessionExpired();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking session: " + e.getMessage());
        }
    }
    
    /**
     * Called when session expires.
     */
    protected void onSessionExpired() {
        try {
            ScrollStatePreferences.clearPageState(this, "");
            getSharedPreferences("scroll_state_prefs", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
            SessionManager.recordActivity(this);
        } catch (Exception e) {
            Log.e(TAG, "Error in onSessionExpired: " + e.getMessage());
        }
    }
}
