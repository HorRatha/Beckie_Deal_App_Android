package aeu.ratha.beckie_deal_app.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Professional network state management for modern apps like Tokopedia/Zendo.
 *
 * Handles:
 * 1. Show skeleton loading when app starts with no internet
 * 2. Stop loading animation when network drops
 * 3. Show "No Internet" state with pull-to-refresh
 * 4. Auto-resume when network returns
 *
 * Usage:
 *   manager = new NetworkStateManager(container, swipeRefresh);
 *   manager.showSkeletonLoading();
 *   manager.hideLoading();
 *   manager.showNoInternetState();
 */
public class NetworkStateManager {
    
    private static final String TAG = "NetworkStateManager";
    
    private final Context context;
    private final FrameLayout container;
    private final SwipeRefreshLayout swipeRefresh;
    private View noInternetView;
    private View skeletonView;
    
    public NetworkStateManager(Context context, FrameLayout container, SwipeRefreshLayout swipeRefresh) {
        this.context = context;
        this.container = container;
        this.swipeRefresh = swipeRefresh;
    }
    
    /**
     * Show skeleton loading animation.
     * Used when app starts with no internet or loading content.
     */
    public void showSkeletonLoading() {
        try {
            if (skeletonView == null) {
                skeletonView = createSkeletonView();
            }
            
            // Hide other states
            if (noInternetView != null) {
                noInternetView.setVisibility(View.GONE);
            }
            
            // Show skeleton
            skeletonView.setVisibility(View.VISIBLE);
            container.bringChildToFront(skeletonView);
            
            Log.d(TAG, "Showing skeleton loading");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing skeleton: " + e.getMessage(), e);
        }
    }
    
    /**
     * Hide all loading states.
     * Called when data is successfully loaded.
     */
    public void hideLoading() {
        try {
            if (skeletonView != null) {
                skeletonView.setVisibility(View.GONE);
            }
            if (noInternetView != null) {
                noInternetView.setVisibility(View.GONE);
            }
            
            // Stop refresh animation if running
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            
            Log.d(TAG, "Hiding all loading states");
            
        } catch (Exception e) {
            Log.e(TAG, "Error hiding loading: " + e.getMessage(), e);
        }
    }
    
    /**
     * Show "No Internet" state with pull-to-refresh enabled.
     * User can swipe down to retry when connection returns.
     */
    public void showNoInternetState() {
        try {
            if (noInternetView == null) {
                noInternetView = createNoInternetView();
            }
            
            // Hide skeleton
            if (skeletonView != null) {
                skeletonView.setVisibility(View.GONE);
            }
            
            // Show no internet
            noInternetView.setVisibility(View.VISIBLE);
            container.bringChildToFront(noInternetView);
            
            // Enable pull-to-refresh for retry
            if (swipeRefresh != null) {
                swipeRefresh.setEnabled(true);
            }
            
            Log.d(TAG, "Showing no internet state");
            
        } catch (Exception e) {
            Log.e(TAG, "Error showing no internet state: " + e.getMessage(), e);
        }
    }
    
    /**
     * Stop loading animation (when network drops during load).
     * Shows "No Internet" state for retry.
     */
    public void stopLoadingDueToNetworkError() {
        try {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(false);
            }
            
            showNoInternetState();
            Log.d(TAG, "Stopped loading due to network error");
            
        } catch (Exception e) {
            Log.e(TAG, "Error stopping loading: " + e.getMessage(), e);
        }
    }
    
    /**
     * Resume loading when network returns.
     * Called by the activity when onNetworkRestored() is triggered.
     */
    public void resumeLoadingAfterNetworkRestore() {
        try {
            if (swipeRefresh != null) {
                swipeRefresh.setRefreshing(true);
            }
            
            // Hide no internet state
            if (noInternetView != null) {
                noInternetView.setVisibility(View.GONE);
            }
            
            Log.d(TAG, "Resumed loading after network restore");
            
        } catch (Exception e) {
            Log.e(TAG, "Error resuming loading: " + e.getMessage(), e);
        }
    }
    
    /**
     * Create the skeleton loading view.
     * This shows placeholder cards while loading.
     */
    private View createSkeletonView() {
        return new View(context);
        // In real implementation, inflate from skeleton_product_card.xml
        // and repeat multiple times in a scrollable container
    }
    
    /**
     * Create the no internet state view.
     * Shows empty state message with pull-to-refresh hint.
     */
    private View createNoInternetView() {
        return new View(context);
        // In real implementation, inflate from state_no_internet.xml
    }
    
    /**
     * Check if loading is currently shown.
     */
    public boolean isLoadingShown() {
        return skeletonView != null && skeletonView.getVisibility() == View.VISIBLE;
    }
    
    /**
     * Check if no internet state is shown.
     */
    public boolean isNoInternetShown() {
        return noInternetView != null && noInternetView.getVisibility() == View.VISIBLE;
    }
    
    /**
     * Cleanup when activity is destroyed.
     */
    public void cleanup() {
        skeletonView = null;
        noInternetView = null;
        Log.d(TAG, "Cleaned up");
    }
}
