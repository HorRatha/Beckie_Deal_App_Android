package aeu.ratha.beckie_deal_app.utils.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import aeu.ratha.beckie_deal_app.utils.NetworkConnectivityHelper;
import aeu.ratha.beckie_deal_app.utils.preferences.ScrollStatePreferences;
import aeu.ratha.beckie_deal_app.utils.preferences.SessionManager;
import aeu.ratha.beckie_deal_app.utils.preferences.ThemePreferences;

/**
 * Base activity with Pull-to-Refresh support.
 *
 * Works with your existing layouts by wrapping RecyclerView with SwipeRefreshLayout.
 *
 * Features:
 * 1. Pull down to refresh
 * 2. Shows "No Internet" state when network drops
 * 3. Auto-retries when network returns
 * 4. No dialogs
 *
 * Usage:
 *   public class ProductListActivity extends PullToRefreshActivity {
 *       @Override
 *       protected void loadData() {
 *           apiService.getProducts().subscribe(/* ... *\/);
 *       }
 *
 *       @Override
 *       protected RecyclerView getRecyclerView() {
 *           return findViewById(R.id.productsGrid);
 *       }
 *   }
 */
public abstract class PullToRefreshActivity extends AppCompatActivity {
    
    private static final String TAG = "PullToRefreshActivity";
    
    private BroadcastReceiver networkChangeReceiver;
    private boolean wasNetworkAvailable = true;
    
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private View noInternetStateView;
    private FrameLayout stateContainer;
    private boolean isLoadingData = false;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemePreferences.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        
        setContentView(getLayoutRes());
        
        // Initialize
        initializeViews();
        setupNetworkMonitoring();
        checkSessionTimeout();
        
        // Initial load
        if (NetworkConnectivityHelper.isNetworkAvailable(this)) {
            loadData();
        } else {
            showNoInternetState();
        }
    }
    
    /**
     * Return your layout resource ID.
     */
    protected abstract int getLayoutRes();
    
    /**
     * Return the RecyclerView to monitor.
     */
    protected abstract RecyclerView getRecyclerView();
    
    /**
     * Load your data here.
     * Call startLoading() before and stopLoading() after.
     */
    protected abstract void loadData();
    
    /**
     * Initialize views and setup pull-to-refresh.
     */
    private void initializeViews() {
        try {
            recyclerView = getRecyclerView();
            if (recyclerView == null) {
                Log.e(TAG, "RecyclerView is null");
                return;
            }
            
            // Create SwipeRefreshLayout wrapper
            FrameLayout parent = (FrameLayout) recyclerView.getParent();
            if (parent != null) {
                setupSwipeRefresh(parent);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
        }
    }
    
    /**
     * Setup SwipeRefreshLayout around the RecyclerView.
     */
    private void setupSwipeRefresh(FrameLayout parent) {
        try {
            // Create SwipeRefreshLayout
            swipeRefresh = new SwipeRefreshLayout(this);
            swipeRefresh.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            
            // Configure colors
            swipeRefresh.setColorSchemeColors(
                    getResources().getColor(
                            getResources().getIdentifier("primary", "color", getPackageName()),
                            getTheme()
                    )
            );
            
            swipeRefresh.setOnRefreshListener(this::handlePullToRefresh);
            
            // Move RecyclerView into SwipeRefreshLayout
            parent.removeView(recyclerView);
            swipeRefresh.addView(recyclerView);
            parent.addView(swipeRefresh, 0);
            
            Log.d(TAG, "SwipeRefreshLayout setup complete");
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up SwipeRefresh: " + e.getMessage(), e);
        }
    }
    
    /**
     * Handle pull-to-refresh gesture.
     */
    private void handlePullToRefresh() {
        Log.d(TAG, "Pull-to-refresh triggered");
        
        if (NetworkConnectivityHelper.isNetworkAvailable(this)) {
            // Network available - reload
            loadData();
        } else {
            // No network - stop refresh
            stopRefresh();
        }
    }
    
    /**
     * Call this before loading data.
     */
    protected void startLoading() {
        isLoadingData = true;
        hideNoInternetState();
        
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }
        
        Log.d(TAG, "Loading started");
    }
    
    /**
     * Call this after data loads successfully.
     */
    protected void stopLoading() {
        isLoadingData = false;
        
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }
        
        hideNoInternetState();
        Log.d(TAG, "Loading stopped - success");
    }
    
    /**
     * Call this when loading fails due to network error.
     */
    protected void stopLoadingWithError() {
        isLoadingData = false;
        
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }
        
        showNoInternetState();
        Log.d(TAG, "Loading stopped - error, showing no internet state");
    }
    
    /**
     * Stop refresh animation.
     */
    private void stopRefresh() {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }
    }
    
    /**
     * Show "No Internet" state.
     */
    private void showNoInternetState() {
        try {
            if (noInternetStateView == null) {
                createNoInternetStateView();
            }
            
            if (noInternetStateView != null) {
                noInternetStateView.setVisibility(View.VISIBLE);
            }
            
            Log.d(TAG, "No internet state shown");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing no internet state: " + e.getMessage(), e);
        }
    }
    
    /**
     * Hide "No Internet" state.
     */
    private void hideNoInternetState() {
        if (noInternetStateView != null) {
            noInternetStateView.setVisibility(View.GONE);
        }
    }
    
    /**
     * Create the "No Internet" state view overlay.
     */
    private void createNoInternetStateView() {
        try {
            // Try to find main container to add overlay
            int containerId = getResources().getIdentifier("mainContainer", "id", getPackageName());
            if (containerId != 0) {
                stateContainer = findViewById(containerId);
            }
            
            if (stateContainer == null) {
                // Fallback - create a FrameLayout overlay
                stateContainer = new FrameLayout(this);
                stateContainer.setLayoutParams(new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));
            }
            
            // Create simple no-internet view
            noInternetStateView = new View(this);
            noInternetStateView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            noInternetStateView.setBackgroundColor(
                    getResources().getColor(
                            getResources().getIdentifier("background", "color", getPackageName()),
                            getTheme()
                    )
            );
            
            stateContainer.addView(noInternetStateView);
            noInternetStateView.setVisibility(View.GONE);
            
        } catch (Exception e) {
            Log.e(TAG, "Error creating no-internet state view: " + e.getMessage(), e);
        }
    }
    
    /**
     * Setup network monitoring.
     */
    private void setupNetworkMonitoring() {
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
            Log.e(TAG, "Error setting up network monitoring: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check network connectivity changes.
     */
    private void checkNetworkConnectivity() {
        try {
            boolean isNetworkNowAvailable = NetworkConnectivityHelper.isNetworkAvailable(this);
            
            if (!isNetworkNowAvailable && wasNetworkAvailable) {
                // Network lost
                Log.d(TAG, "Network lost");
                if (isLoadingData) {
                    stopLoadingWithError();
                } else {
                    showNoInternetState();
                }
                
            } else if (isNetworkNowAvailable && !wasNetworkAvailable) {
                // Network restored
                Log.d(TAG, "Network restored");
                loadData();  // Auto-reload
            }
            
            wasNetworkAvailable = isNetworkNowAvailable;
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking network: " + e.getMessage(), e);
        }
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
        
        try {
            if (networkChangeReceiver != null) {
                unregisterReceiver(networkChangeReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering receiver: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check session timeout.
     */
    private void checkSessionTimeout() {
        try {
            if (SessionManager.hasSessionExpired(this)) {
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
