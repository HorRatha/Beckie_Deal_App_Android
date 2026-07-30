package aeu.ratha.beckie_deal_app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;

/**
 * Shared helper for setting up the custom bottom navigation bar with smooth
 * expandable animation. Only the selected item shows its label; others collapse to icon-only.
 * The bar maintains constant width—items intelligently reflow as selection changes.
 */
public final class BottomNavHelper {

    private BottomNavHelper() {}

    public enum NavItem { HOME, CATEGORIES, FAVORITES, CART, ACCOUNT }

    /**
     * Wire up the custom bottom nav in any activity.
     *
     * @param activity        The activity containing the nav layout
     * @param currentItem     Which item should be selected (current screen)
     */
    public static void setup(Activity activity, NavItem currentItem) {
        ViewGroup homeFrame = activity.findViewById(R.id.navItemHome);
        ViewGroup categoriesFrame = activity.findViewById(R.id.navItemCategories);
        ViewGroup favoritesFrame = activity.findViewById(R.id.navItemFavorites);
        ViewGroup cartFrame = activity.findViewById(R.id.navItemCart);
        ViewGroup accountFrame = activity.findViewById(R.id.navItemAccount);

        if (homeFrame == null) return;

        // Set initial selected state (no animation on first load)
        selectItem(activity, currentItem, false);

        // Wire click handlers
        homeFrame.setOnClickListener(v -> {
            if (currentItem != NavItem.HOME) {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        categoriesFrame.setOnClickListener(v -> {
            if (currentItem != NavItem.CATEGORIES) {
                activity.startActivity(new Intent(activity, CategoriesActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        favoritesFrame.setOnClickListener(v -> {
            if (currentItem != NavItem.FAVORITES) {
                activity.startActivity(new Intent(activity, FavoritesActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        cartFrame.setOnClickListener(v -> {
            if (currentItem != NavItem.CART) {
                activity.startActivity(new Intent(activity, CartActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        accountFrame.setOnClickListener(v -> {
            if (currentItem != NavItem.ACCOUNT) {
                activity.startActivity(new Intent(activity, ProfileActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        // Optional: add press bounce to item frames
        UiAnimations.pressBounce(homeFrame, categoriesFrame, favoritesFrame, cartFrame, accountFrame);
    }

    /**
     * Select a navigation item with optional animation.
     *
     * @param context    Activity context
     * @param item       Item to select
     * @param animate    Whether to animate the transition
     */
    private static void selectItem(Context context, NavItem item, boolean animate) {
        int[] ids = {
                R.id.navItemHome, R.id.navItemCategories, R.id.navItemFavorites,
                R.id.navItemCart, R.id.navItemAccount
        };
        int[] pillIds = {
                R.id.navPillHome, R.id.navPillCategories, R.id.navPillFavorites,
                R.id.navPillCart, R.id.navPillAccount
        };
        int[] iconIds = {
                R.id.navIconHome, R.id.navIconCategories, R.id.navIconFavorites,
                R.id.navIconCart, R.id.navIconAccount
        };
        int[] labelIds = {
                R.id.navLabelHome, R.id.navLabelCategories, R.id.navLabelFavorites,
                R.id.navLabelCart, R.id.navLabelAccount
        };

        int selectedIndex = item.ordinal();

        for (int i = 0; i < ids.length; i++) {
            View itemFrame = ((Activity) context).findViewById(ids[i]);
            View pill = ((Activity) context).findViewById(pillIds[i]);
            ImageView icon = ((Activity) context).findViewById(iconIds[i]);
            TextView label = ((Activity) context).findViewById(labelIds[i]);

            if (itemFrame == null || pill == null || icon == null || label == null) continue;

            boolean isSelected = (i == selectedIndex);
            pill.setSelected(isSelected);

            // Adjust layout weight for dynamic clustering with balanced distribution
            LinearLayout.LayoutParams itemParams = (LinearLayout.LayoutParams) itemFrame.getLayoutParams();
            if (isSelected) {
                // Selected item gets more space to display full label
                itemParams.weight = 1.5f;
            } else {
                // Unselected items share remaining space equally for better balance
                itemParams.weight = 1.0f;
            }
            itemFrame.setLayoutParams(itemParams);

            if (isSelected) {
                // Selected: show label, orange icon (bold), orange pill bg
                icon.setImageTintList(ContextCompat.getColorStateList(context, R.color.nav_glass_active));
                label.setTextColor(ContextCompat.getColor(context, R.color.nav_glass_active));
                // Trigger checked state so animated-selector plays thin→bold transition
                icon.setImageState(new int[]{android.R.attr.state_checked}, true);

                if (animate && !ValueAnimator.areAnimatorsEnabled()) animate = false;

                if (animate) {
                    animateLabelIn(label);
                } else {
                    label.setVisibility(View.VISIBLE);
                    label.setAlpha(1f);
                    label.setScaleX(1f);
                    label.setScaleY(1f);
                    // Set pill to wrap content so it expands
                    ViewGroup.LayoutParams params = pill.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    pill.setLayoutParams(params);
                }
            } else {
                // Unselected: hide label, gray icon (thin)
                icon.setImageTintList(ContextCompat.getColorStateList(context, R.color.nav_glass_inactive));
                // Clear checked state so animated-selector plays bold→thin transition
                icon.setImageState(new int[]{-android.R.attr.state_checked}, true);

                if (animate) {
                    animateLabelOut(label);
                } else {
                    label.setVisibility(View.GONE);
                    label.setAlpha(0f);
                    // Set pill to fixed icon-only width
                    ViewGroup.LayoutParams params = pill.getLayoutParams();
                    params.width = (int) (46 * context.getResources().getDisplayMetrics().density);
                    pill.setLayoutParams(params);
                }
            }
        }
    }

    /**
     * Animate label fading/scaling in. The pill expands automatically via wrap_content.
     */
    private static void animateLabelIn(TextView label) {
        label.setVisibility(View.VISIBLE);
        label.setAlpha(0f);
        label.setScaleX(0.7f);
        label.setScaleY(0.7f);

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(label, "alpha", 0f, 1f);
        alphaAnim.setDuration(200L);
        alphaAnim.setStartDelay(40L);

        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(label, "scaleX", 0.7f, 1.04f, 1f);
        scaleXAnim.setDuration(240L);
        scaleXAnim.setStartDelay(20L);
        scaleXAnim.setInterpolator(new OvershootInterpolator(1.2f));

        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(label, "scaleY", 0.7f, 1.04f, 1f);
        scaleYAnim.setDuration(240L);
        scaleYAnim.setStartDelay(20L);
        scaleYAnim.setInterpolator(new OvershootInterpolator(1.2f));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnim, scaleXAnim, scaleYAnim);
        set.start();
    }

    /**
     * Animate label fading/scaling out. The pill collapses automatically via wrap_content.
     */
    private static void animateLabelOut(TextView label) {
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(label, "alpha", 1f, 0f);
        alphaAnim.setDuration(160L);

        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(label, "scaleX", 1f, 0.7f);
        scaleXAnim.setDuration(180L);
        scaleXAnim.setInterpolator(new DecelerateInterpolator(1.2f));

        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(label, "scaleY", 1f, 0.7f);
        scaleYAnim.setDuration(180L);
        scaleYAnim.setInterpolator(new DecelerateInterpolator(1.2f));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnim, scaleXAnim, scaleYAnim);
        set.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                label.setVisibility(View.GONE);
            }
        });
        set.start();
    }
}
