package aeu.ratha.beckie_deal_app.ui.activities.product;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Product;
import aeu.ratha.beckie_deal_app.utils.helpers.PullToRefreshActivity;

/**
 * Example: ProductListActivity with Pull-to-Refresh
 *
 * NO changes to your layout needed!
 * Just extend PullToRefreshActivity and implement 3 methods.
 *
 * How it works:
 * 1. User swipes down → handlePullToRefresh()
 * 2. Network check → No internet? Stop. Have internet? Reload.
 * 3. Loading starts → startLoading() shows refresh animation
 * 4. Data arrives → stopLoading() hides animation, shows data
 * 5. Network drops → stopLoadingWithError() shows "Pull to refresh"
 * 6. Network returns → Auto-reload
 */
public class ExampleProductListActivity extends PullToRefreshActivity {
    
    private static final String TAG = "ProductListActivity";
    
    private RecyclerView productsGrid;
    private ProductAdapter adapter;
    
    @Override
    protected int getLayoutRes() {
        return R.layout.activity_product_list;  // Your existing layout
    }
    
    @Override
    protected RecyclerView getRecyclerView() {
        return findViewById(R.id.productsGrid);  // Your existing RecyclerView
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize your adapter
        productsGrid = findViewById(R.id.productsGrid);
        adapter = new ProductAdapter();
        productsGrid.setAdapter(adapter);
    }
    
    @Override
    protected void loadData() {
        Log.d(TAG, "Loading products...");
        
        // Start loading animation
        startLoading();
        
        // Make API call
        apiService.getProducts()
            .subscribe(
                products -> {
                    // Success
                    Log.d(TAG, "Products loaded: " + products.size());
                    stopLoading();  // Hide loading, show data
                    updateUI(products);
                },
                error -> {
                    // Error
                    Log.e(TAG, "Error loading products: " + error.getMessage());
                    stopLoadingWithError();  // Show "Pull to refresh" state
                }
            );
    }
    
    private void updateUI(List<Product> products) {
        adapter.submitList(products);
        productsGrid.scrollToPosition(0);
    }
}

/**
 * ============================================================================
 * USAGE GUIDE - Super Simple
 * ============================================================================
 *
 * STEP 1: Change base class
 * ─────────────────────────
 * Before:
 *   public class ProductListActivity extends AppCompatActivity {
 *
 * After:
 *   public class ProductListActivity extends PullToRefreshActivity {
 *
 *
 * STEP 2: Implement 3 methods
 * ─────────────────────────────
 *
 *   @Override
 *   protected int getLayoutRes() {
 *       return R.layout.activity_product_list;  // Your layout
 *   }
 *
 *   @Override
 *   protected RecyclerView getRecyclerView() {
 *       return findViewById(R.id.productsGrid);  // Your RecyclerView
 *   }
 *
 *   @Override
 *   protected void loadData() {
 *       startLoading();  // Show loading
 *       
 *       // Your API call
 *       apiService.getProducts()
 *           .subscribe(
 *               products -> {
 *                   stopLoading();  // Hide loading
 *                   updateUI(products);
 *               },
 *               error -> stopLoadingWithError()  // Show empty state
 *           );
 *   }
 *
 *
 * STEP 3: Done!
 * ──────────────
 * That's it! You now have:
 * ✓ Pull-to-refresh (swipe down)
 * ✓ No internet state
 * ✓ Auto-retry when network returns
 * ✓ Network loss detection
 *
 *
 * ============================================================================
 * FLOW DIAGRAM
 * ============================================================================
 *
 * APP START:
 * ─────────
 * Network available? → YES → loadData() → Show loading → Hide when done
 *                  → NO  → showNoInternetState()
 *
 *
 * USER SWIPES DOWN (Pull-to-refresh):
 * ──────────────────────────────────
 * User swipes down
 *     ↓
 * handlePullToRefresh()
 *     ↓
 * Network available? → YES → loadData()
 *                  → NO  → Stop refresh animation
 *
 *
 * NETWORK DROPS MID-LOAD:
 * ──────────────────────
 * loadData() running
 *     ↓
 * Network drops
 *     ↓
 * stopLoadingWithError()
 *     ↓
 * Show: "Pull to refresh" state
 *     ↓
 * User sees empty screen with animation stopped
 *
 *
 * NETWORK RETURNS:
 * ────────────────
 * Network available
 *     ↓
 * Auto-call loadData()
 *     ↓
 * Show loading
 *     ↓
 * Data loads
 *     ↓
 * Hide loading, show data
 *
 *
 * ============================================================================
 * METHOD REFERENCE
 * ============================================================================
 *
 * startLoading()
 *   → Shows loading animation (pull-to-refresh indicator)
 *   → Call before API request
 *
 * stopLoading()
 *   → Hides loading animation
 *   → Call after successful data load
 *
 * stopLoadingWithError()
 *   → Hides loading animation
 *   → Shows "Pull to refresh" empty state
 *   → Call when API fails (network error)
 *
 * getRecyclerView()
 *   → Return your RecyclerView
 *   → Used to inject SwipeRefreshLayout
 *
 * loadData()
 *   → Implement your data loading logic here
 *   → Called on app start + auto-retry
 *
 *
 * ============================================================================
 * IMPORTANT NOTES
 * ============================================================================
 *
 * 1. NO layout changes needed
 *    - Works with your existing layouts
 *    - Automatically wraps RecyclerView
 *
 * 2. Pull-to-refresh color
 *    - Uses your primary color automatically
 *    - From your colors.xml
 *
 * 3. Network detection
 *    - Automatic
 *    - Works silently in background
 *
 * 4. No dialogs
 *    - Just states and loading animations
 *    - Professional UX like Tokopedia
 *
 * 5. Auto-retry
 *    - When network returns, automatically reloads
 *    - User doesn't need to do anything
 *
 *
 * ============================================================================
 * ERROR HANDLING
 * ============================================================================
 *
 * Network error during load?
 *   → stopLoadingWithError()
 *   → Shows empty state
 *   → User can swipe down to retry
 *
 * Network error before API call?
 *   → showNoInternetState() shown automatically
 *   → Pull-to-refresh disabled
 *   → Waits for network
 *
 *
 * ============================================================================
 * TESTING
 * ============================================================================
 *
 * Test 1: Pull-to-refresh
 * ───────────────────────
 * 1. Open activity
 * 2. Wait for data to load
 * 3. Swipe down (pull-to-refresh)
 * 4. Should see loading animation
 * 5. Data reloads
 *
 *
 * Test 2: Network loss mid-load
 * ──────────────────────────────
 * 1. Disable network
 * 2. Open activity
 * 3. Should show "Pull to refresh" state
 * 4. Enable network
 * 5. Should auto-reload
 *
 *
 * Test 3: Manual refresh with no network
 * ───────────────────────────────────────
 * 1. Disable network
 * 2. See "Pull to refresh" state
 * 3. Swipe down
 * 4. Nothing happens (no network to load)
 * 5. Enable network
 * 6. Swipe down again
 * 7. Data loads
 *
 *
 * ============================================================================
 */
