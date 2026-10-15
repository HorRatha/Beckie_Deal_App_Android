# Scroll Indicator - Practical Implementation Examples

## Example 1: ProductListActivity (RecyclerView)

### Step 1: Add to Layout (activity_product_list.xml)

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Your existing top bar, toolbar, etc. -->
    <com.google.android.material.appbar.MaterialToolbar
        android:id="@+id/topAppBar"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        app:layout_constraintTop_toTopOf="parent" />

    <!-- RecyclerView -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/productsGrid"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:paddingBottom="8dp"
        app:layout_constraintTop_toBottomOf="@id/topAppBar"
        app:layout_constraintBottom_toTopOf="@id/bottomNav" />

    <!-- Scroll Indicator (overlaid on RecyclerView) -->
    <FrameLayout
        android:id="@+id/scrollIndicatorContainer"
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:layout_gravity="center_horizontal|bottom"
        android:paddingBottom="8dp"
        android:pointerEvents="none"
        app:layout_constraintTop_toBottomOf="@id/productsGrid"
        app:layout_constraintBottom_toTopOf="@id/bottomNav">

        <com.google.android.material.card.MaterialCardView
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:layout_gravity="center_horizontal|bottom"
            app:cardCornerRadius="24dp"
            app:cardElevation="4dp"
            app:cardBackgroundColor="@color/white_fixed">

            <ImageView
                android:id="@+id/scrollArrowDown"
                android:layout_width="24dp"
                android:layout_height="24dp"
                android:layout_gravity="center"
                android:src="@drawable/ic_arrow_down"
                android:contentDescription="Scroll down"
                app:tint="@color/brand_orange" />
        </com.google.android.material.card.MaterialCardView>
    </FrameLayout>

    <!-- Bottom Navigation -->
    <include
        android:id="@+id/bottomNav"
        layout="@layout/layout_bottom_nav"
        app:layout_constraintBottom_toBottomOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

### Step 2: Update Activity Code (ProductListActivity.java)

```java
package aeu.ratha.beckie_deal_app.ui.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.ScrollIndicatorHelper;
import aeu.ratha.beckie_deal_app.utils.ScrollStatePreferences;

public class ProductListActivity extends AppCompatActivity {

    private RecyclerView productsGrid;
    private View scrollIndicatorContainer;
    private String mode = "new_collection";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        
        productsGrid = findViewById(R.id.productsGrid);
        scrollIndicatorContainer = findViewById(R.id.scrollIndicatorContainer);
        
        // Set up RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productsGrid.setLayoutManager(layoutManager);
        
        // Attach scroll indicator
        productsGrid.post(() -> {
            if (scrollIndicatorContainer != null) {
                ScrollIndicatorHelper.attachToRecyclerView(productsGrid, scrollIndicatorContainer);
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Load products
        loadProducts();
        
        // Restore scroll position
        GridLayoutManager layoutManager = (GridLayoutManager) productsGrid.getLayoutManager();
        if (layoutManager != null) {
            ScrollStatePreferences.restoreScrollPosition(this, "productList_" + mode, layoutManager, productsGrid);
        }
        
        // Update indicator visibility
        scrollIndicatorContainer.post(() -> {
            ScrollIndicatorHelper.updateIndicatorVisibility(productsGrid, scrollIndicatorContainer);
        });
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Save scroll position
        GridLayoutManager layoutManager = (GridLayoutManager) productsGrid.getLayoutManager();
        if (layoutManager != null) {
            ScrollStatePreferences.saveScrollPosition(this, "productList_" + mode, layoutManager);
        }
    }
    
    private void loadProducts() {
        // Your product loading logic here
        // After adapter is set:
        ScrollIndicatorHelper.updateIndicatorVisibility(productsGrid, scrollIndicatorContainer);
    }
    
    private String getPageKey() {
        return "productList_" + mode;
    }
}
```

---

## Example 2: CategoryDetailActivity (RecyclerView with Filters)

### Integration Code

```java
package aeu.ratha.beckie_deal_app.ui.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.ScrollIndicatorHelper;

public class CategoryDetailActivity extends AppCompatActivity {

    private RecyclerView categoryProductsGrid;
    private View scrollIndicator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);
        
        categoryProductsGrid = findViewById(R.id.categoryProductsGrid);
        scrollIndicator = findViewById(R.id.scrollIndicator);
        
        // Set up RecyclerView with 2-column grid
        categoryProductsGrid.setLayoutManager(new GridLayoutManager(this, 2));
        
        // Attach scroll indicator
        categoryProductsGrid.post(() -> {
            ScrollIndicatorHelper.attachToRecyclerView(categoryProductsGrid, scrollIndicator);
        });
    }
    
    // Called when user applies filters (price, rating, year, etc.)
    public void onFilterApplied() {
        // Load filtered products
        loadFilteredProducts();
        
        // Reset scroll position to top
        categoryProductsGrid.smoothScrollToPosition(0);
        
        // Update indicator
        ScrollIndicatorHelper.updateIndicatorVisibility(categoryProductsGrid, scrollIndicator);
    }
    
    // Called when user switches brands
    public void onBrandChanged(int brandId) {
        loadProductsByBrand(brandId);
        
        // Scroll to top
        categoryProductsGrid.smoothScrollToPosition(0);
        
        // Update indicator
        ScrollIndicatorHelper.updateIndicatorVisibility(categoryProductsGrid, scrollIndicator);
    }
    
    private void loadFilteredProducts() {
        // Fetch filtered data from API/Database
    }
    
    private void loadProductsByBrand(int brandId) {
        // Fetch data by brand
    }
}
```

---

## Example 3: SearchActivity (Dynamic RecyclerView)

### Integration Code

```java
package aeu.ratha.beckie_deal_app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.ScrollIndicatorHelper;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private RecyclerView searchResultsView;
    private View scrollIndicator;
    private String lastQuery = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        searchInput = findViewById(R.id.searchInput);
        searchResultsView = findViewById(R.id.searchResultsView);
        scrollIndicator = findViewById(R.id.scrollIndicator);
        
        // Set up linear layout for search results
        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
        
        // Search input listener
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            performSearch(searchInput.getText().toString());
            return true;
        });
        
        // Attach scroll indicator
        searchResultsView.post(() -> {
            ScrollIndicatorHelper.attachToRecyclerView(searchResultsView, scrollIndicator);
        });
    }
    
    private void performSearch(String query) {
        lastQuery = query;
        
        // Clear previous results
        ScrollIndicatorHelper.updateIndicatorVisibility(searchResultsView, scrollIndicator);
        
        // Execute search (API call)
        executeSearchQuery(query);
    }
    
    private void executeSearchQuery(String query) {
        // API call to search products
        // On success:
        showSearchResults();
        
        // Update indicator after results loaded
        ScrollIndicatorHelper.updateIndicatorVisibility(searchResultsView, scrollIndicator);
    }
    
    private void showSearchResults() {
        // Set adapter and scroll to top
        searchResultsView.smoothScrollToPosition(0);
    }
}
```

---

## Example 4: AllBrandsActivityV2 (Simple List)

### Integration Code

```java
package aeu.ratha.beckie_deal_app.ui.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.ScrollIndicatorHelper;

public class AllBrandsActivityV2 extends AppCompatActivity {

    private RecyclerView brandsList;
    private View scrollIndicator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_brands);
        
        brandsList = findViewById(R.id.brandsList);
        scrollIndicator = findViewById(R.id.scrollIndicator);
        
        // Linear layout for brands list
        brandsList.setLayoutManager(new LinearLayoutManager(this));
        
        // Load brands
        loadBrands();
        
        // Attach scroll indicator
        brandsList.post(() -> {
            ScrollIndicatorHelper.attachToRecyclerView(brandsList, scrollIndicator);
        });
    }
    
    private void loadBrands() {
        // Fetch brands from API
        // On success, update indicator
        ScrollIndicatorHelper.updateIndicatorVisibility(brandsList, scrollIndicator);
    }
}
```

---

## Example 5: WishlistActivity (GridLayout with Custom Scroll)

### Integration Code

```java
package aeu.ratha.beckie_deal_app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import androidx.appcompat.app.AppCompatActivity;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.ScrollStatePreferences;

public class WishlistActivity extends AppCompatActivity {

    private GridLayout wishlistGrid;
    private View scrollIndicator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        
        wishlistGrid = findViewById(R.id.wishlistGrid);
        scrollIndicator = findViewById(R.id.scrollIndicator);
        
        loadWishlist();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Restore scroll position (Y coordinate)
        String savedY = ScrollStatePreferences.getFilterState(this, "wishlist", "scrollY", "0");
        wishlistGrid.setScrollY(Integer.parseInt(savedY));
        
        // Update indicator visibility based on scroll
        updateIndicatorVisibility();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Save scroll position
        ScrollStatePreferences.saveFilterState(this, "wishlist", "scrollY", 
            String.valueOf(wishlistGrid.getScrollY()));
    }
    
    private void loadWishlist() {
        // Load wishlist items
        // Update indicator
        updateIndicatorVisibility();
    }
    
    private void updateIndicatorVisibility() {
        // Show indicator if there's more content to scroll
        int scrollY = wishlistGrid.getScrollY();
        int totalHeight = wishlistGrid.getHeight();
        int childHeight = 0;
        
        for (int i = 0; i < wishlistGrid.getChildCount(); i++) {
            childHeight += wishlistGrid.getChildAt(i).getHeight();
        }
        
        if (scrollY < 50 && childHeight > totalHeight) {
            scrollIndicator.setAlpha(1f);
        } else {
            scrollIndicator.setAlpha(0f);
        }
    }
}
```

---

## Integration Checklist

For each activity, follow these steps:

1. **Layout**
   - [ ] Add scroll indicator FrameLayout
   - [ ] Ensure RecyclerView/ScrollView has proper height
   - [ ] Test on different screen sizes

2. **Activity Code**
   - [ ] Import `ScrollIndicatorHelper`
   - [ ] Find references to scroll views
   - [ ] Call `attachToRecyclerView()` or `attachToNestedScrollView()`
   - [ ] Call `updateIndicatorVisibility()` after data loads

3. **Testing**
   - [ ] Indicator appears on load
   - [ ] Indicator hides when scrolling
   - [ ] Animation is smooth
   - [ ] No crashes on empty lists
   - [ ] Works with pagination/filtering

---

## Common Patterns

### Pattern 1: After Adapter Set
```java
adapter.notifyDataSetChanged();
ScrollIndicatorHelper.updateIndicatorVisibility(recyclerView, indicator);
```

### Pattern 2: Pagination
```java
// Load next page
loadNextPage();

// Update indicator (might be last page now)
ScrollIndicatorHelper.updateIndicatorVisibility(recyclerView, indicator);
```

### Pattern 3: Filter/Sort
```java
applyFilter();
recyclerView.smoothScrollToPosition(0); // Reset to top
ScrollIndicatorHelper.updateIndicatorVisibility(recyclerView, indicator);
```

### Pattern 4: Search Results
```java
if (results.isEmpty()) {
    indicator.setVisibility(View.GONE);
} else {
    ScrollIndicatorHelper.attachToRecyclerView(recyclerView, indicator);
}
```

---

## Troubleshooting

### Indicator doesn't appear
```java
// Debug: Check item count
RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
if (lm != null) {
    Log.d("Indicator", "Items: " + lm.getItemCount());
}

// Call update
ScrollIndicatorHelper.updateIndicatorVisibility(recyclerView, indicator);
```

### Animation stutter
```java
// Reduce animation duration in ScrollIndicatorHelper
private static final long ANIMATION_DURATION = 400L; // Was 600L
```

### Indicator stuck on
```java
// Force hide
indicator.animate().alpha(0f).setDuration(300).start();
```

---

## Next Steps

1. Choose which activities need the indicator
2. Copy the layout pattern from examples above
3. Add code to each activity's onCreate() and onResume()
4. Test scrolling behavior
5. Adjust animation/colors as needed
6. Deploy and monitor user engagement

The scroll indicator encourages users to explore more content by providing clear visual feedback that more products/items are available!
