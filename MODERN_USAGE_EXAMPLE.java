package aeu.ratha.beckie_deal_app.ui.activities.example;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.ModernBaseActivity;

/**
 * Example: Modern Activity with Skeleton Loading + Pull-to-Refresh
 *
 * This follows the Tokopedia/Zendo pattern:
 * 1. App opens → Show skeleton loading
 * 2. Network drops → Loading stops, shows "Pull to refresh"
 * 3. Network returns → Auto-reload
 * 4. User swipes down → Manual refresh
 *
 * NO dialogs, just clean UI states.
 */
public class ModernProductsActivity extends ModernBaseActivity {
    
    private RecyclerView productsRecyclerView;
    
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_products;
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Your init code here
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
    }
    
    @Override
    protected void loadData() {
        Log.d("ProductsActivity", "Loading products...");
        
        // Show skeleton loading
        showSkeletonLoading();
        
        // Make API call
        apiService.getProducts()
            .subscribe(
                products -> {
                    // Success - hide loading and show data
                    hideLoading();
                    updateUI(products);
                },
                error -> {
                    // Error - show no internet state
                    Log.e("ProductsActivity", "Error loading products: " + error.getMessage());
                    showNoInternetState();
                }
            );
    }
    
    private void updateUI(List<Product> products) {
        // Bind data to RecyclerView
        adapter.submitList(products);
        productsRecyclerView.scrollToPosition(0);
    }
}

/**
 * ============================================================================
 * FLOW DIAGRAM - Professional App Pattern
 * ============================================================================
 *
 * SCENARIO 1: App Opens with NO Internet
 * ─────────────────────────────────────
 * User opens app
 *     ↓
 * onCreate() called
 *     ↓
 * checkNetworkAvailable() → FALSE
 *     ↓
 * showSkeletonLoading()
 *     ↓
 * User sees: Skeleton cards (placeholder loading)
 *     ↓
 * Network becomes available
 *     ↓
 * onNetworkRestored() triggered
 *     ↓
 * Auto-call loadData()
 *     ↓
 * skeleton hidden, real data shown
 *
 *
 * SCENARIO 2: Network Loss During Load
 * ────────────────────────────────────
 * User has data, scrolling
 *     ↓
 * loadData() → skeleton shown
 *     ↓
 * API request in progress...
 *     ↓
 * Network drops
 *     ↓
 * onNetworkLost() triggered
 *     ↓
 * stopLoadingDueToNetworkError()
 *     ↓
 * User sees: Empty state with "Pull down to refresh"
 *     ↓
 * User swipes down (pull-to-refresh)
 *     ↓
 * Network available? → YES
 *     ↓
 * loadData() called again
 *     ↓
 * Data loads normally
 *
 *
 * SCENARIO 3: User Manual Retry (Pull-to-Refresh)
 * ───────────────────────────────────────────────
 * User sees: "Pull down to refresh" state
 *     ↓
 * User swipes down
 *     ↓
 * handlePullToRefresh() triggered
 *     ↓
 * checkNetworkAvailable()
 *     ↓
 * YES → loadData()
 * NO → stop refresh animation
 *     ↓
 * Continue showing "Pull down to refresh"
 *
 *
 * ============================================================================
 * MODERN ACTIVITY LAYOUT REQUIREMENTS
 * ============================================================================
 *
 * Your activity_products.xml should have:
 *
 * <?xml version="1.0" encoding="utf-8"?>
 * <FrameLayout
 *     xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:id="@+id/mainContainer"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent">
 *
 *     <!-- Main content (SwipeRefreshLayout) -->
 *     <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
 *         android:id="@+id/swipeRefresh"
 *         android:layout_width="match_parent"
 *         android:layout_height="match_parent">
 *
 *         <RecyclerView
 *             android:id="@+id/productsRecyclerView"
 *             android:layout_width="match_parent"
 *             android:layout_height="match_parent" />
 *     </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>
 *
 *     <!-- Skeleton loading overlay (created by system) -->
 *     <!-- No-internet state overlay (created by system) -->
 * </FrameLayout>
 *
 *
 * ============================================================================
 * KEY DIFFERENCES FROM DIALOG APPROACH
 * ============================================================================
 *
 * ❌ OLD (Dialog Approach):
 *    - Network lost → Dialog appears
 *    - User clicks "Try Again"
 *    - Manual retry required
 *    - Blocks interaction
 *
 * ✅ NEW (Modern Approach):
 *    - Network lost → Loading stops, shows empty state
 *    - Pull-to-refresh enabled (swipe down)
 *    - Network returns → Auto-retry
 *    - Doesn't block interaction
 *
 *
 * ============================================================================
 * IMPLEMENTATION CHECKLIST
 * ============================================================================
 *
 * □ Create activity extending ModernBaseActivity
 * □ Implement getLayoutRes() - return your layout
 * □ Implement loadData() - your API call logic
 * □ Call showSkeletonLoading() before API call
 * □ Call hideLoading() on success
 * □ Call showNoInternetState() on error
 * □ Your layout must have:
 *   □ FrameLayout with id="mainContainer"
 *   □ SwipeRefreshLayout with id="swipeRefresh"
 *   □ RecyclerView or content inside SwipeRefresh
 * □ Test with:
 *   □ App start with no internet (shows skeleton)
 *   □ Network loss during load (shows empty state)
 *   □ Pull-to-refresh (manual retry)
 *   □ Network restore (auto-reload)
 *
 *
 * ============================================================================
 */
