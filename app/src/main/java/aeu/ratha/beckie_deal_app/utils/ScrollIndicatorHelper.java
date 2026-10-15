package aeu.ratha.beckie_deal_app.utils;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ScrollIndicatorHelper - Manages scroll indicator visibility and animation
 * 
 * Shows a floating indicator when scrollable content is available,
 * hides it when user scrolls, and provides smooth animations.
 * 
 * Usage:
 * - For NestedScrollView: ScrollIndicatorHelper.attachToNestedScrollView(scrollView, indicator)
 * - For RecyclerView: ScrollIndicatorHelper.attachToRecyclerView(recyclerView, indicator)
 */
public class ScrollIndicatorHelper {

    private static final long ANIMATION_DURATION = 600L;
    private static final float TRANSLATE_DISTANCE = 8f;

    /**
     * Attach scroll indicator to NestedScrollView
     * Shows indicator when there's more content below, hides when scrolled
     */
    public static void attachToNestedScrollView(NestedScrollView scrollView, View indicator) {
        if (scrollView == null || indicator == null) return;

        // Start animation on first show
        startBounceAnimation(indicator.findViewById(android.R.id.icon));

        scrollView.setOnScrollChangeListener((androidx.core.widget.NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Check if there's more content to scroll
            View child = scrollView.getChildAt(scrollView.getChildCount() - 1);
            if (child != null) {
                int diff = (child.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                
                // Hide indicator if scrolled down significantly or reached bottom
                if (scrollY > 100 || diff <= 0) {
                    fadeOutIndicator(indicator);
                } else {
                    fadeInIndicator(indicator);
                }
            }
        });

        // Initial check
        scrollView.post(() -> {
            View child = scrollView.getChildAt(scrollView.getChildCount() - 1);
            if (child != null) {
                int diff = (child.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
                if (diff > 0) {
                    fadeInIndicator(indicator);
                }
            }
        });
    }

    /**
     * Attach scroll indicator to RecyclerView
     * Shows indicator when there's more content below, hides when scrolled
     */
    public static void attachToRecyclerView(RecyclerView recyclerView, View indicator) {
        if (recyclerView == null || indicator == null) return;

        // Start animation on first show
        startBounceAnimation(indicator.findViewById(android.R.id.icon));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int lastVisiblePosition;
                
                // Support different layout managers
                if (layoutManager instanceof androidx.recyclerview.widget.LinearLayoutManager) {
                    lastVisiblePosition = ((androidx.recyclerview.widget.LinearLayoutManager) layoutManager)
                            .findLastVisibleItemPosition();
                } else if (layoutManager instanceof androidx.recyclerview.widget.GridLayoutManager) {
                    lastVisiblePosition = ((androidx.recyclerview.widget.GridLayoutManager) layoutManager)
                            .findLastVisibleItemPosition();
                } else if (layoutManager instanceof androidx.recyclerview.widget.StaggeredGridLayoutManager) {
                    int[] positions = ((androidx.recyclerview.widget.StaggeredGridLayoutManager) layoutManager)
                            .findLastVisibleItemPositions(null);
                    lastVisiblePosition = positions.length > 0 ? positions[0] : -1;
                } else {
                    return;
                }

                int totalItemCount = layoutManager.getItemCount();
                int scrollDistance = recyclerView.computeVerticalScrollOffset();

                // Hide indicator if scrolled down or if viewing last item
                if (scrollDistance > 100 || lastVisiblePosition >= totalItemCount - 1) {
                    fadeOutIndicator(indicator);
                } else if (totalItemCount > 0) {
                    fadeInIndicator(indicator);
                }
            }
        });

        // Initial check
        recyclerView.post(() -> {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null && layoutManager.getItemCount() > 3) {
                fadeInIndicator(indicator);
            }
        });
    }

    /**
     * Continuous bounce animation for the scroll indicator arrow
     */
    private static void startBounceAnimation(View arrowView) {
        if (arrowView == null) return;

        ObjectAnimator animator = ObjectAnimator.ofFloat(
                arrowView,
                "translationY",
                0f,
                TRANSLATE_DISTANCE,
                0f
        );
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.start();
    }

    /**
     * Fade in the scroll indicator with animation
     */
    private static void fadeInIndicator(View indicator) {
        if (indicator.getAlpha() == 1f) return;

        indicator.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
    }

    /**
     * Fade out the scroll indicator with animation
     */
    private static void fadeOutIndicator(View indicator) {
        if (indicator.getAlpha() == 0f) return;

        indicator.animate()
                .alpha(0f)
                .setDuration(300)
                .start();
    }

    /**
     * Show or hide indicator based on scroll position
     * Call this after data changes (pagination, filtering, etc.)
     */
    public static void updateIndicatorVisibility(RecyclerView recyclerView, View indicator) {
        if (recyclerView == null || indicator == null) return;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null && layoutManager.getItemCount() > 3) {
            fadeInIndicator(indicator);
        } else {
            fadeOutIndicator(indicator);
        }
    }
}
