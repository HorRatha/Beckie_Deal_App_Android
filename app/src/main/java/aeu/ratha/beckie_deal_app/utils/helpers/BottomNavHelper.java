package aeu.ratha.beckie_deal_app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

        // Wire click handlers with animation
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

        // Initialize cart badge
        updateCartBadge(activity);
    }

    /**
     * Update the cart badge to show the number of items in cart.
     * Minimal design - displays count as plain text next to icon/label.
     *
     * @param activity The activity containing the bottom nav
     */
    public static void updateCartBadge(Activity activity) {
        TextView cartBadge = activity.findViewById(R.id.cartBadge);
        if (cartBadge == null) return;

        int itemCount = CartManager.getInstance().getTotalUnits();
        String currentText = cartBadge.getText().toString();
        String newText = itemCount > 99 ? "(99+)" : "(" + itemCount + ")";
        
        if (itemCount > 0) {
            boolean wasVisible = cartBadge.getVisibility() == View.VISIBLE;
            boolean countChanged = !currentText.equals(newText);
            
            cartBadge.setText(newText);
            cartBadge.setVisibility(View.VISIBLE);
            
            // Animate only if badge just appeared or count increased
            if (!wasVisible || countChanged) {
                animateBadgePop(cartBadge);
            }
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }

    /**
     * Subtle pop animation for cart badge - professional micro-interaction.
     */
    private static void animateBadgePop(View badge) {
        badge.setScaleX(0.3f);
        badge.setScaleY(0.3f);
        badge.setAlpha(0f);
        
        badge.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(300L)
            .setInterpolator(new OvershootInterpolator(2.0f))
            .start();
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

        if (animate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !ValueAnimator.areAnimatorsEnabled()) {
            animate = false;
        }

        for (int i = 0; i < ids.length; i++) {
            View itemFrame = ((Activity) context).findViewById(ids[i]);
            View pill = ((Activity) context).findViewById(pillIds[i]);
            ImageView icon = ((Activity) context).findViewById(iconIds[i]);
            TextView label = ((Activity) context).findViewById(labelIds[i]);

            if (itemFrame == null || pill == null || icon == null || label == null) continue;

            boolean isSelected = (i == selectedIndex);
            pill.setSelected(isSelected);

            // Set weight immediately (no animation on weight to avoid jank)
            LinearLayout.LayoutParams itemParams = (LinearLayout.LayoutParams) itemFrame.getLayoutParams();
            itemParams.weight = isSelected ? 1.5f : 1.0f;
            itemFrame.setLayoutParams(itemParams);

            if (isSelected) {
                // Selected: show label with slide animation, orange icon (bold), orange pill bg
                icon.setImageTintList(ContextCompat.getColorStateList(context, R.color.nav_glass_active));
                label.setTextColor(ContextCompat.getColor(context, R.color.nav_glass_active));
                // Trigger checked state so animated-selector plays thin→bold transition
                icon.setImageState(new int[]{android.R.attr.state_checked}, true);

                if (animate) {
                    animateLabelSlideIn(label);
                } else {
                    label.setVisibility(View.VISIBLE);
                    label.setAlpha(1f);
                    label.setScaleX(1f);
                    label.setScaleY(1f);
                    label.setTranslationX(0f);
                }
            } else {
                // Unselected: hide label with slide animation, gray icon (thin)
                icon.setImageTintList(ContextCompat.getColorStateList(context, R.color.nav_glass_inactive));
                // Clear checked state so animated-selector plays bold→thin transition
                icon.setImageState(new int[]{-android.R.attr.state_checked}, true);

                if (animate) {
                    animateLabelSlideOut(label);
                } else {
                    label.setVisibility(View.GONE);
                    label.setAlpha(0f);
                    label.setTranslationX(0f);
                }
            }
        }
    }

    /**
     * Animate label sliding in from left with fade and hardware acceleration.
     */
    private static void animateLabelSlideIn(TextView label) {
        label.setVisibility(View.VISIBLE);
        label.setAlpha(0f);
        label.setScaleX(0.9f);
        label.setScaleY(0.9f);
        label.setTranslationX(-12f);
        
        // Enable hardware acceleration for smooth animation
        label.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(label, "alpha", 0f, 1f);
        alphaAnim.setDuration(200L);
        alphaAnim.setStartDelay(20L);
        alphaAnim.setInterpolator(new DecelerateInterpolator(1.0f));

        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(label, "translationX", -12f, 0f);
        translateAnim.setDuration(240L);
        translateAnim.setInterpolator(new DecelerateInterpolator(1.5f));

        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(label, "scaleX", 0.9f, 1f);
        scaleXAnim.setDuration(220L);
        scaleXAnim.setInterpolator(new DecelerateInterpolator(1.2f));

        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(label, "scaleY", 0.9f, 1f);
        scaleYAnim.setDuration(220L);
        scaleYAnim.setInterpolator(new DecelerateInterpolator(1.2f));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnim, translateAnim, scaleXAnim, scaleYAnim);
        set.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                label.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
        set.start();
    }

    /**
     * Animate label sliding out to left with fade and hardware acceleration.
     */
    private static void animateLabelSlideOut(TextView label) {
        // Enable hardware acceleration for smooth animation
        label.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(label, "alpha", 1f, 0f);
        alphaAnim.setDuration(160L);
        alphaAnim.setInterpolator(new DecelerateInterpolator(1.0f));

        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(label, "translationX", 0f, -10f);
        translateAnim.setDuration(180L);
        translateAnim.setInterpolator(new DecelerateInterpolator(1.2f));

        ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(label, "scaleX", 1f, 0.9f);
        scaleXAnim.setDuration(160L);
        scaleXAnim.setInterpolator(new DecelerateInterpolator(1.0f));

        ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(label, "scaleY", 1f, 0.9f);
        scaleYAnim.setDuration(160L);
        scaleYAnim.setInterpolator(new DecelerateInterpolator(1.0f));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(alphaAnim, translateAnim, scaleXAnim, scaleYAnim);
        set.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                label.setVisibility(View.GONE);
                label.setTranslationX(0f);
                label.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
        set.start();
    }
}
