# State Preservation Guide - Beckie Deal App

## Overview

This guide explains the state preservation system implemented in the Beckie Deal app. Users can now navigate away from a list page (scrolled to section 3, for example) and return to find the exact same section still displayed — just like Taobao, Facebook, and other modern apps.

**Implementation Pattern**: Scroll position and filter state are automatically saved via `SharedPreferences` when the user navigates away and restored when they return.

---

## Key Features

### 1. **Automatic Scroll Position Restoration**
When you're browsing products and scroll down to see section 3, then:
- Click on a product to view details
- Click back to return to the list

✅ **The list will scroll back to section 3 automatically**

### 2. **Filter & Sort State Persistence**
- Active filter (by price, rating, year) is remembered
- Sort preference (by price, by rating) is preserved
- Search query is saved and restored

### 3. **Per-Page State Management**
Each page type maintains its own state:
- **ProductListActivity**: Saves state per mode (new_collection vs top_selling)
- **CategoryDetailActivity**: Saves state per category (category_id + brand_id)
- **SearchActivity**: Saves state per search query
- **AllBrandsActivityV2**: Saves brand list scroll position
- **WishlistActivity**: Saves wishlist grid scroll position

### 4. **View Mode Persistence**
Grid vs List view mode is saved separately via `ViewModePreferences` and restored immediately on activity creation.

---

## Architecture

### Core Utility Class: `ScrollStatePreferences.java`

Located in: `app/src/main/java/aeu/ratha/beckie_deal_app/utils/preferences/`

**Key Methods**:

#### Save Scroll Position
```java
ScrollStatePreferences.saveScrollPosition(context, pageKey, layoutManager);
```
- **When**: Call in `onPause()` before user navigates away
- **What**: Saves first visible item index and scroll offset
- **Storage**: SharedPreferences (key: `scroll_pos_<pageKey>`, `first_item_<pageKey>`, etc.)

#### Restore Scroll Position
```java
ScrollStatePreferences.restoreScrollPosition(context, pageKey, layoutManager, recyclerView);
```
- **When**: Call in `onResume()` or after adapter is attached
- **What**: Restores list to previously saved position
- **Timing**: Uses `recyclerView.post()` for smooth animation

#### Save Filter State
```java
ScrollStatePreferences.saveFilterState(context, pageKey, key, value);
```
- **Example**: `saveFilterState(this, "search", "query", "laptop")`
- **Usage**: Persist any string-based preference (filter, sort, search term)

#### Retrieve Filter State
```java
String savedValue = ScrollStatePreferences.getFilterState(context, pageKey, key, defaultValue);
```
- **Returns**: Saved value or defaultValue if not found

#### Clear Page State
```java
ScrollStatePreferences.clearPageState(context, pageKey);
```
- **When**: Call when user performs a fresh search or manual refresh
- **What**: Clears all saved state for that page (scroll, filters, etc.)

---

## Implementation Details by Activity

### ProductListActivity

**Page Key**: `"productList_" + mode` (e.g., `"productList_new_collection"`)

```java
@Override
protected void onResume() {
    super.onResume();
    applyAndRender();
    
    // Restore scroll position after rendering
    if (productsGrid != null && productsGrid.getLayoutManager() instanceof GridLayoutManager) {
        GridLayoutManager layoutManager = (GridLayoutManager) productsGrid.getLayoutManager();
        ScrollStatePreferences.restoreScrollPosition(this, getPageKey(), layoutManager, productsGrid);
    }
}

@Override
protected void onPause() {
    super.onPause();
    
    // Save scroll position before leaving
    if (productsGrid != null && productsGrid.getLayoutManager() instanceof GridLayoutManager) {
        GridLayoutManager layoutManager = (GridLayoutManager) productsGrid.getLayoutManager();
        ScrollStatePreferences.saveScrollPosition(this, getPageKey(), layoutManager);
    }
}
```

**State Preserved**:
- ✅ Scroll position
- ✅ View mode (grid/list) via `ViewModePreferences`
- ✅ Filter state (sort, year, max price)
- ✅ Product data (kept in memory via `allProducts`)

### CategoryDetailActivity

**Page Key**: `"category_" + currentCategoryId + "_" + currentBrandId`

This ensures each category-brand combination has its own saved state.

```java
private String getPageKey() {
    return "category_" + currentCategoryId + "_" + currentBrandId;
}
```

**State Preserved**:
- ✅ Scroll position (per category)
- ✅ Brand filter selection
- ✅ Sort/price filters
- ✅ View mode (grid/list)

### SearchActivity

**Page Key**: `"search_" + lastQuery` (e.g., `"search_laptop"`)

Search query itself is saved separately in filter state:
```java
ScrollStatePreferences.saveFilterState(this, "search", "query", lastQuery);
```

**State Preserved**:
- ✅ Search query (restored on app return)
- ✅ Scroll position (per search query)
- ✅ View mode (grid/list)

### AllBrandsActivityV2

**Page Key**: `"brands"`

Simple key since there's only one brand list page.

**State Preserved**:
- ✅ Scroll position
- ✅ Search query (optional, can be added)
- ✅ Pagination state

### WishlistActivity

**Page Key**: `"wishlist"`

Uses `GridLayout` (not RecyclerView), so scroll is saved as Y coordinate:

```java
ScrollStatePreferences.saveFilterState(this, "wishlist", "scrollY", 
        String.valueOf(wishlistGrid.getScrollY()));
```

**State Preserved**:
- ✅ Scroll position (Y coordinate)
- ✅ Wishlist items (kept in memory and refreshed)

---

## How It Works: Step-by-Step Example

### Scenario: User browses products and navigates back

1. **User is on ProductListActivity** (New Collection)
   - Scrolls down to view product #47 (section 3)
   - Product list is now showing items 40-60

2. **User clicks a product card**
   - `onPause()` is called
   - Scroll position is captured: `firstVisibleItem = 45, offset = 120px`
   - Saved to SharedPreferences with key: `first_item_productList_new_collection = 45`

3. **ProductDetailActivity opens**
   - User views product info

4. **User clicks back button**
   - ProductDetailActivity finishes
   - `onResume()` is called on ProductListActivity
   - `restoreScrollPosition()` reads saved position: `firstVisibleItem = 45`
   - GridLayoutManager scrolls to position 45 with smooth animation

5. **Result**
   - ✅ List is back at section 3, showing the same products
   - ✅ Scroll position matches exactly
   - ✅ No jarring jump to top

---

## Configuration by Use Case

### For Activities with RecyclerView (Recommended)

```java
@Override
protected void onResume() {
    super.onResume();
    
    // Any data loading, filtering, etc.
    applyAndRender();
    
    // Always restore scroll AFTER adapter is set and data is ready
    if (recyclerView != null && recyclerView.getLayoutManager() instanceof GridLayoutManager) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        ScrollStatePreferences.restoreScrollPosition(this, "uniquePageKey", layoutManager, recyclerView);
    }
}

@Override
protected void onPause() {
    super.onPause();
    
    // Save state before leaving
    if (recyclerView != null && recyclerView.getLayoutManager() instanceof GridLayoutManager) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        ScrollStatePreferences.saveScrollPosition(this, "uniquePageKey", layoutManager);
    }
}
```

### For Activities with GridLayout (Fallback)

```java
@Override
protected void onResume() {
    super.onResume();
    
    gridLayout.post(() -> {
        String savedY = ScrollStatePreferences.getFilterState(this, "pageKey", "scrollY", "0");
        gridLayout.setScrollY(Integer.parseInt(savedY));
    });
}

@Override
protected void onPause() {
    super.onPause();
    
    ScrollStatePreferences.saveFilterState(this, "pageKey", "scrollY", 
            String.valueOf(gridLayout.getScrollY()));
}
```

---

## Clearing State

### Manual Clear (When User Performs Fresh Action)

```java
// When user starts a new search
public void onSearchClicked(String newQuery) {
    ScrollStatePreferences.clearPageState(this, "search_old_query");
    // Load new search
}

// When user applies new filters
public void onFilterApplied() {
    ScrollStatePreferences.clearPageState(this, getPageKey());
    applyAndRender();
}

// When user pulls-to-refresh
private void onRefresh() {
    ScrollStatePreferences.clearPageState(this, getPageKey());
    reloadData();
}
```

### Automatic Clear (On Process Death)

State is preserved **only during the app session**:
- Close and reopen the app → state is lost (fresh start)
- Navigate away and back → state is preserved ✅

---

## Best Practices

### ✅ Do

- **Save in `onPause()`**: Capture scroll state before user leaves
- **Restore in `onResume()`**: After data is loaded and adapter is set
- **Use unique page keys**: Prevents different pages from interfering
- **Use `recyclerView.post()` for restoration**: Ensures layout is ready
- **Clear on manual refresh**: Don't preserve stale state if user refreshed

### ❌ Don't

- **Don't restore in `onCreate()`**: Adapter might not be set yet
- **Don't save in `onDestroy()`**: May be called after app process death
- **Don't use generic keys**: Multiple pages could collide
- **Don't skip `onPause()` save**: State won't persist for next visit
- **Don't forget to restore in `onResume()`**: User won't see the saved position

---

## Testing State Preservation

### Test 1: Basic Scroll Restoration
1. Open ProductListActivity
2. Scroll down to middle of list (note the product visible)
3. Click on a product → ProductDetailActivity opens
4. Click back
5. ✅ Verify: List is at the same scroll position

### Test 2: Filter + Scroll Persistence
1. Open CategoryDetailActivity
2. Apply a price filter
3. Scroll down
4. Navigate away and return
5. ✅ Verify: Filter is still active AND scroll position is restored

### Test 3: Multi-Category State
1. Open Category A, scroll to position 30
2. Switch to Category B, scroll to position 50
3. Switch back to Category A
4. ✅ Verify: Category A is at position 30 (not 50)

### Test 4: Search Query Persistence
1. Open SearchActivity
2. Type "laptop"
3. Navigate to product detail
4. Go back
5. ✅ Verify: Search box still shows "laptop"

### Test 5: App Process Death
1. Browse and scroll on ProductListActivity
2. Force close the app (Settings → Apps → Force Stop)
3. Reopen the app
4. ✅ Verify: List starts fresh at top (state intentionally lost)

---

## Files Modified

| File | Changes |
|------|---------|
| `ScrollStatePreferences.java` | **NEW**: Core utility for state persistence |
| `ProductListActivity.java` | Added `onResume()`, `onPause()`, `getPageKey()` |
| `CategoryDetailActivity.java` | Added `onResume()`, `onPause()`, `getPageKey()` |
| `SearchActivity.java` | Added `onResume()`, `onPause()` with query persistence |
| `AllBrandsActivityV2.java` | Added `onResume()`, `onPause()` |
| `WishlistActivity.java` | Added `onResume()`, `onPause()` with Y-coordinate scroll |

---

## Compatibility & Future Enhancements

### Current Implementation
- ✅ Works with `GridLayoutManager` for RecyclerView
- ✅ Works with `GridLayout` for custom layouts
- ✅ Compatible with existing `ViewModePreferences`
- ✅ No dependency on Navigation Component (uses Intent-based navigation)

### Future Enhancements
- [ ] Add `LinearLayoutManager` support
- [ ] Add `StaggeredGridLayoutManager` support
- [ ] Automatic state cleanup after 30 days
- [ ] State backup/restore via cloud sync
- [ ] Selective per-activity enable/disable flag

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Scroll doesn't restore | Ensure `restoreScrollPosition()` is called in `onResume()` AFTER adapter is attached |
| List jumps to top | Check that `recyclerView.post()` is used for restoration (allows layout to settle) |
| Different pages interfere | Use unique page keys combining activity type + filter state |
| Filter not saved | Ensure `saveFilterState()` is called in `onPause()` |
| State lost on app restart | This is intentional — state is session-only, not persistent after process death |

---

## Related Utilities

- **ViewModePreferences.java**: Handles grid/list view mode persistence (separate from scroll state)
- **ThemePreferences.java**: Handles dark mode state
- **ProductRepository.java**: Caches product data in memory during session
- **WishlistManager.java**: Maintains wishlist state in memory

---

## Summary

The state preservation system ensures that users get a modern app experience similar to Taobao, Instagram, and Facebook:

- 🎯 **Scroll Position**: Returns to exact position after navigation
- 🔍 **Filters & Search**: All active filters and search queries are remembered
- 🎨 **View Mode**: Grid/list preference is instantly restored
- 📱 **Per-Page State**: Each page type and filter combination is independent
- ⚡ **Smooth Animation**: Scroll restoration uses `post()` for smooth transitions

**Result**: Users can freely navigate between products and lists without losing their place, creating a seamless and intuitive browsing experience.
