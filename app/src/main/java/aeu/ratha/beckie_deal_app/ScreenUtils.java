package aeu.ratha.beckie_deal_app;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.GridLayout;

/**
 * Utility class for responsive layout helpers.
 * Uses dimen resources so phone / sw600dp / sw900dp qualifiers drive the values.
 */
public final class ScreenUtils {

    private ScreenUtils() {}

    /** Returns the smallest screen width in dp (basis for breakpoints). */
    public static int getSmallestWidthDp(Context ctx) {
        return ctx.getResources().getConfiguration().smallestScreenWidthDp;
    }

    /** Number of columns for a 2-up product grid (2 phone, 3 tablet, 4 large tablet). */
    public static int productGridColumns(Context ctx) {
        int sw = getSmallestWidthDp(ctx);
        if (sw >= 900) return 4;
        if (sw >= 600) return 3;
        return 2;
    }

    /** Number of columns for the brands grid (3 phone, 4 tablet, 5 large tablet). */
    public static int brandsGridColumns(Context ctx) {
        int sw = getSmallestWidthDp(ctx);
        if (sw >= 900) return 4;
        if (sw >= 600) return 3;
        return 2;
    }

    /** Number of columns for the "All Categories" grid (2 phone, 3 tablet). */
    public static int categoryGridColumns(Context ctx) {
        int sw = getSmallestWidthDp(ctx);
        if (sw >= 600) return 3;
        return 2;
    }

    /** Width in px for horizontal-scroll product cards, read from dimens. */
    public static int hCardWidthPx(Context ctx) {
        return (int) ctx.getResources().getDimension(R.dimen.h_card_width);
    }

    /** Height in px for horizontal-scroll product cards, read from dimens. */
    public static int hCardHeightPx(Context ctx) {
        return (int) ctx.getResources().getDimension(R.dimen.h_card_height);
    }

    /** Height in px for horizontal-scroll card image area. */
    public static int hCardImageHeightPx(Context ctx) {
        return (int) ctx.getResources().getDimension(R.dimen.h_card_image_height);
    }

    /**
     * Apply column count to an existing GridLayout.
     * Also sets row/column weights so cards fill evenly regardless of column count.
     */
    public static void applyGridColumns(GridLayout grid, int columns) {
        grid.setColumnCount(columns);
    }

    /** Convert dp to pixels. */
    public static int dpToPx(Context ctx, float dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }

    /** True when the device is in landscape orientation. */
    public static boolean isLandscape(Context ctx) {
        return ctx.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;
    }

    /** True when this is a tablet (sw >= 600dp). */
    public static boolean isTablet(Context ctx) {
        return getSmallestWidthDp(ctx) >= 600;
    }
}
