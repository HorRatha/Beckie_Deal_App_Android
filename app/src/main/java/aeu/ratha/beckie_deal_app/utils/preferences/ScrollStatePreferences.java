package aeu.ratha.beckie_deal_app.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Utility class to persist and restore RecyclerView scroll positions and filter states
 * using SharedPreferences. This enables smooth state preservation across navigation.
 * 
 * Usage:
 *   Save: ScrollStatePreferences.saveScrollPosition(context, "productList", layoutManager, recyclerView);
 *   Restore: ScrollStatePreferences.restoreScrollPosition(context, "productList", layoutManager, recyclerView);
 */
public final class ScrollStatePreferences {

    private static final String PREFS_NAME = "scroll_state_prefs";
    private static final String SCROLL_POS_PREFIX = "scroll_pos_";
    private static final String FIRST_ITEM_PREFIX = "first_item_";
    private static final String OFFSET_PREFIX = "offset_";

    private ScrollStatePreferences() {}

    /**
     * Save scroll position from GridLayoutManager.
     * Stores first visible item index and offset to top.
     * Call in onPause() or when navigating away.
     *
     * @param context Context for SharedPreferences access
     * @param pageKey Unique identifier for this page (e.g., "productList", "search", "category_5")
     * @param layoutManager The GridLayoutManager from RecyclerView
     */
    public static void saveScrollPosition(Context context, String pageKey, 
                                          androidx.recyclerview.widget.GridLayoutManager layoutManager) {
        if (layoutManager == null) return;
        
        try {
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            android.view.View view = layoutManager.findViewByPosition(firstVisibleItem);
            int offset = 0;
            
            if (view != null) {
                offset = view.getTop();
            }
            
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit()
                .putInt(FIRST_ITEM_PREFIX + pageKey, firstVisibleItem)
                .putInt(OFFSET_PREFIX + pageKey, offset)
                .apply();
                
        } catch (Exception e) {
            android.util.Log.e("ScrollStatePreferences", "Error saving scroll state: " + e.getMessage());
        }
    }

    /**
     * Restore scroll position to GridLayoutManager.
     * Call in onResume() or after adapter is set.
     * Should be called AFTER RecyclerView adapter is attached.
     *
     * @param context Context for SharedPreferences access
     * @param pageKey Same key used in saveScrollPosition()
     * @param layoutManager The GridLayoutManager from RecyclerView
     * @param recyclerView The RecyclerView itself (for post-scroll operations)
     */
    public static void restoreScrollPosition(Context context, String pageKey,
                                             androidx.recyclerview.widget.GridLayoutManager layoutManager,
                                             androidx.recyclerview.widget.RecyclerView recyclerView) {
        if (layoutManager == null || recyclerView == null) return;
        
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int firstVisibleItem = prefs.getInt(FIRST_ITEM_PREFIX + pageKey, -1);
            
            if (firstVisibleItem == -1) {
                // No saved state — scroll to top
                layoutManager.scrollToPosition(0);
                return;
            }
            
            // Scroll to saved position with slight delay for smooth animation
            recyclerView.post(() -> {
                layoutManager.scrollToPositionWithOffset(firstVisibleItem, 0);
            });
            
        } catch (Exception e) {
            android.util.Log.e("ScrollStatePreferences", "Error restoring scroll state: " + e.getMessage());
        }
    }

    /**
     * Save filter/sort state as key-value pairs.
     * Use for filter state, search query, sort preference, etc.
     *
     * @param context Context for SharedPreferences access
     * @param pageKey Unique identifier for this page
     * @param key Filter parameter name (e.g., "sort", "maxPrice", "searchQuery")
     * @param value Filter value (String or numeric as String)
     */
    public static void saveFilterState(Context context, String pageKey, String key, String value) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String compositeKey = pageKey + "_" + key;
            prefs.edit().putString(compositeKey, value).apply();
        } catch (Exception e) {
            android.util.Log.e("ScrollStatePreferences", "Error saving filter state: " + e.getMessage());
        }
    }

    /**
     * Retrieve saved filter state.
     *
     * @param context Context for SharedPreferences access
     * @param pageKey Same pageKey used in saveFilterState()
     * @param key Filter parameter name
     * @param defaultValue Value to return if not found
     * @return Saved filter value or defaultValue
     */
    public static String getFilterState(Context context, String pageKey, String key, String defaultValue) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String compositeKey = pageKey + "_" + key;
            return prefs.getString(compositeKey, defaultValue);
        } catch (Exception e) {
            android.util.Log.e("ScrollStatePreferences", "Error retrieving filter state: " + e.getMessage());
            return defaultValue;
        }
    }

    /**
     * Clear all saved state for a specific page.
     * Call when user performs a fresh search or clears filters.
     *
     * @param context Context for SharedPreferences access
     * @param pageKey The pageKey to clear
     */
    public static void clearPageState(Context context, String pageKey) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            // Remove scroll state
            editor.remove(FIRST_ITEM_PREFIX + pageKey);
            editor.remove(OFFSET_PREFIX + pageKey);
            
            // Remove any associated filter keys (optional cleanup)
            // More aggressive: clear all keys with this pageKey prefix
            for (String key : prefs.getAll().keySet()) {
                if (key.startsWith(pageKey)) {
                    editor.remove(key);
                }
            }
            
            editor.apply();
        } catch (Exception e) {
            android.util.Log.e("ScrollStatePreferences", "Error clearing page state: " + e.getMessage());
        }
    }
}
