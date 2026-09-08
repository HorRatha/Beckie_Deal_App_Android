package aeu.ratha.beckie_deal_app.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class to persist and restore the user's preferred product list
 * display mode (2-column grid vs single-column list) using SharedPreferences.
 * Modeled after {@link ThemePreferences}.
 */
public final class ViewModePreferences {

    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_LIST_VIEW = "product_list_view_mode";

    private ViewModePreferences() {}

    /** Save whether the single-column list view is enabled (false = grid, the default). */
    public static void setListView(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_LIST_VIEW, enabled).apply();
    }

    /** True if the user has chosen the single-column list view. Defaults to false (grid). */
    public static boolean isListView(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_LIST_VIEW, false);
    }
}
