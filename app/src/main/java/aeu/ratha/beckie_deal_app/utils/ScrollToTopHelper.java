package aeu.ratha.beckie_deal_app.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.FrameLayout;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ScrollToTopHelper - Manages scroll-to-top button with integrated progress ring
 * 
 * Features:
 * - Shows button when user scrolls down, hides when at top
 * - Updates progress ring around button to show scroll position (0-100%)
 * - Clicking the button smoothly scrolls back to the top
 * 
 * Button Layout Expected:
 * FrameLayout (56x56 dp) containing:
 *   - ProgressBar (id: android:R.id.background) - Gray background ring
 *   - ProgressBar (id: android:R.id.progress) - Orange progress ring
 *   - MaterialCardView (40x40 dp) - Orange button with up arrow
 * 
 * Usage:
 * - For NestedScrollView: ScrollToTopHelper.attachToNestedScrollView(scrollView, buttonFrame)
 * - For RecyclerView: ScrollToTopHelper.attachToRecyclerView(recyclerView, buttonFrame)
 */
public class ScrollToTopHelper {

    private static final long SCROLL_ANIMATION_DURATION = 500L;
    private static final int SCROLL_THRESHOLD = 100; // Show button after scrolling 100dp

    /**
     * Attach scroll-to-top button with progress ring to NestedScrollView
     * Shows button when scrolled down, hides when at top
     * Updates progress ring as user scrolls
     */
    public static void attachToNestedScrollView(NestedScrollView scrollView, FrameLayout scrollToTopButton) {
        if (scrollView == null || scrollToTopButton == null) return;

        // Ensure button is fully hidden initially
        scrollToTopButton.setVisibility(View.GONE);
        scrollToTopButton.setAlpha(0f);

        // Get progress ring views
        ProgressBar progressRingBackground = scrollToTopButton.findViewById(android.R.id.background);
        ProgressBar progressRing = scrollToTopButton.findViewById(android.R.id.progress);

        // Listen to scroll changes
        scrollView.setOnScrollChangeListener((androidx.core.widget.NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // Calculate total scrollable height
            int scrollableHeight = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
            int scrollProgress = scrollableHeight > 0 ? (int) ((scrollY / (float) scrollableHeight) * 100) : 0;
            scrollProgress = Math.max(0, Math.min(100, scrollProgress)); // Clamp 0-100

            // Update progress ring
            if (progressRing != null) {
                progressRing.setProgress(scrollProgress);
            }

            // Show button if scrolled down more than threshold
            if (scrollY > SCROLL_THRESHOLD) {
                if (scrollToTopButton.getVisibility() != View.VISIBLE) {
                    fadeInButton(scrollToTopButton);
                }
            } else {
                if (scrollToTopButton.getVisibility() == View.VISIBLE) {
                    fadeOutButton(scrollToTopButton);
                }
            }
        });

        // Click listener to scroll back to top
        scrollToTopButton.setOnClickListener(v -> scrollToTop(scrollView));
    }

    /**
     * Attach scroll-to-top button with progress ring to RecyclerView
     * Shows button when scrolled down, hides when at top
     * Updates progress ring as user scrolls
     */
    public static void attachToRecyclerView(RecyclerView recyclerView, FrameLayout scrollToTopButton) {
        if (recyclerView == null || scrollToTopButton == null) return;

        // Initially hidden
        scrollToTopButton.setAlpha(0f);
        scrollToTopButton.setVisibility(View.GONE);

        // Get progress ring views
        ProgressBar progressRingBackground = scrollToTopButton.findViewById(android.R.id.background);
        ProgressBar progressRing = scrollToTopButton.findViewById(android.R.id.progress);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Calculate scroll progress
                int scrollOffset = recyclerView.computeVerticalScrollOffset();
                int scrollExtent = recyclerView.computeVerticalScrollExtent();
                int scrollRange = recyclerView.computeVerticalScrollRange();

                int scrollProgress = scrollRange > 0 ? (int) ((scrollOffset / (float) (scrollRange - scrollExtent)) * 100) : 0;
                scrollProgress = Math.max(0, Math.min(100, scrollProgress)); // Clamp 0-100

                // Update progress ring
                if (progressRing != null) {
                    progressRing.setProgress(scrollProgress);
                }

                // Show button if scrolled down more than threshold
                if (scrollOffset > SCROLL_THRESHOLD) {
                    if (scrollToTopButton.getVisibility() != View.VISIBLE) {
                        fadeInButton(scrollToTopButton);
                    }
                } else {
                    if (scrollToTopButton.getVisibility() == View.VISIBLE) {
                        fadeOutButton(scrollToTopButton);
                    }
                }
            }
        });

        // Click listener to scroll back to top
        scrollToTopButton.setOnClickListener(v -> scrollToTop(recyclerView));
    }

    /**
     * Scroll NestedScrollView back to top with smooth animation
     */
    private static void scrollToTop(NestedScrollView scrollView) {
        if (scrollView == null) return;
        scrollView.smoothScrollTo(0, 0);
    }

    /**
     * Scroll RecyclerView back to top with smooth animation
     */
    private static void scrollToTop(RecyclerView recyclerView) {
        if (recyclerView == null) return;
        recyclerView.smoothScrollToPosition(0);
    }

    /**
     * Fade in the scroll-to-top button
     */
    private static void fadeInButton(View button) {
        if (button.getAlpha() == 1f) return;

        button.setVisibility(View.VISIBLE);
        button.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    /**
     * Fade out the scroll-to-top button
     */
    private static void fadeOutButton(View button) {
        if (button.getAlpha() == 0f) return;

        button.animate()
                .alpha(0f)
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(300)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> button.setVisibility(View.GONE))
                .start();
    }

    /**
     * Update button visibility and progress ring based on current scroll position
     */
    public static void updateButtonState(RecyclerView recyclerView, FrameLayout scrollToTopButton) {
        if (recyclerView == null || scrollToTopButton == null) return;

        // Calculate scroll progress
        int scrollOffset = recyclerView.computeVerticalScrollOffset();
        int scrollExtent = recyclerView.computeVerticalScrollExtent();
        int scrollRange = recyclerView.computeVerticalScrollRange();

        int scrollProgress = scrollRange > 0 ? (int) ((scrollOffset / (float) (scrollRange - scrollExtent)) * 100) : 0;
        scrollProgress = Math.max(0, Math.min(100, scrollProgress)); // Clamp 0-100

        // Update progress ring
        ProgressBar progressRing = scrollToTopButton.findViewById(android.R.id.progress);
        if (progressRing != null) {
            progressRing.setProgress(scrollProgress);
        }

        // Update button visibility
        if (scrollOffset > SCROLL_THRESHOLD) {
            fadeInButton(scrollToTopButton);
        } else {
            fadeOutButton(scrollToTopButton);
        }
    }

    /**
     * Update button visibility and progress ring based on current scroll position
     */
    public static void updateButtonState(NestedScrollView scrollView, FrameLayout scrollToTopButton) {
        if (scrollView == null || scrollToTopButton == null) return;

        int scrollY = scrollView.getScrollY();

        // Calculate total scrollable height
        int scrollableHeight = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();
        int scrollProgress = scrollableHeight > 0 ? (int) ((scrollY / (float) scrollableHeight) * 100) : 0;
        scrollProgress = Math.max(0, Math.min(100, scrollProgress)); // Clamp 0-100

        // Update progress ring
        ProgressBar progressRing = scrollToTopButton.findViewById(android.R.id.progress);
        if (progressRing != null) {
            progressRing.setProgress(scrollProgress);
        }

        // Update button visibility
        if (scrollY > SCROLL_THRESHOLD) {
            fadeInButton(scrollToTopButton);
        } else {
            fadeOutButton(scrollToTopButton);
        }
    }
}