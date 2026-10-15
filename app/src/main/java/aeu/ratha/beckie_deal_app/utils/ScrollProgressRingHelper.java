package aeu.ratha.beckie_deal_app.utils;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ScrollProgressRingHelper - Manages scroll progress ring indicator
 * 
 * Shows a circular progress ring that fills as user scrolls down.
 * Displays percentage of scroll progress (0-100%).
 * Modern, elegant design showing scroll depth.
 * 
 * Usage:
 * - For NestedScrollView: ScrollProgressRingHelper.attachToNestedScrollView(scrollView, container)
 * - For RecyclerView: ScrollProgressRingHelper.attachToRecyclerView(recyclerView, container)
 */
public class ScrollProgressRingHelper {

    /**
     * Attach scroll progress ring to NestedScrollView
     * Updates progress ring as user scrolls
     */
    public static void attachToNestedScrollView(NestedScrollView scrollView, View ringContainer) {
        if (scrollView == null || ringContainer == null) return;

        ProgressBar progressRing = ringContainer.findViewById(android.R.id.progress);
        ProgressBar backgroundRing = ringContainer.findViewById(android.R.id.background);
        TextView progressText = ringContainer.findViewById(android.R.id.text1);

        if (progressRing == null || backgroundRing == null || progressText == null) return;

        // Listen to scroll changes
        scrollView.setOnScrollChangeListener((androidx.core.widget.NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            updateProgressRing(scrollView, progressRing, progressText);
        });

        // Initial update
        scrollView.post(() -> updateProgressRing(scrollView, progressRing, progressText));
    }

    /**
     * Attach scroll progress ring to RecyclerView
     * Updates progress ring as user scrolls
     */
    public static void attachToRecyclerView(RecyclerView recyclerView, View ringContainer) {
        if (recyclerView == null || ringContainer == null) return;

        ProgressBar progressRing = ringContainer.findViewById(android.R.id.progress);
        ProgressBar backgroundRing = ringContainer.findViewById(android.R.id.background);
        TextView progressText = ringContainer.findViewById(android.R.id.text1);

        if (progressRing == null || backgroundRing == null || progressText == null) return;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateProgressRing(recyclerView, progressRing, progressText);
            }
        });

        // Initial update
        recyclerView.post(() -> updateProgressRing(recyclerView, progressRing, progressText));
    }

    /**
     * Update progress ring based on NestedScrollView scroll position
     */
    private static void updateProgressRing(NestedScrollView scrollView, ProgressBar progressRing, TextView progressText) {
        if (scrollView == null) return;

        // Get scroll bounds
        View child = scrollView.getChildAt(scrollView.getChildCount() - 1);
        if (child == null) return;

        int scrollViewHeight = scrollView.getHeight();
        int totalScrollableHeight = child.getBottom() - scrollViewHeight;
        int currentScroll = scrollView.getScrollY();

        // Calculate progress percentage (0-100)
        int progress = totalScrollableHeight > 0 ? (int) ((currentScroll * 100) / totalScrollableHeight) : 0;
        progress = Math.min(100, Math.max(0, progress)); // Clamp between 0-100

        // Update progress ring
        progressRing.setProgress(progress);

        // Update percentage text
        if (progressText != null) {
            progressText.setText(progress + "%");
        }
    }

    /**
     * Update progress ring based on RecyclerView scroll position
     */
    private static void updateProgressRing(RecyclerView recyclerView, ProgressBar progressRing, TextView progressText) {
        if (recyclerView == null) return;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) return;

        int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) return;

        int lastVisiblePosition;

        // Get last visible item position based on layout manager type
        if (layoutManager instanceof androidx.recyclerview.widget.LinearLayoutManager) {
            lastVisiblePosition = ((androidx.recyclerview.widget.LinearLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
        } else if (layoutManager instanceof androidx.recyclerview.widget.GridLayoutManager) {
            lastVisiblePosition = ((androidx.recyclerview.widget.GridLayoutManager) layoutManager)
                    .findLastVisibleItemPosition();
        } else if (layoutManager instanceof androidx.recyclerview.widget.StaggeredGridLayoutManager) {
            int[] positions = ((androidx.recyclerview.widget.StaggeredGridLayoutManager) layoutManager)
                    .findLastVisibleItemPositions(null);
            lastVisiblePosition = positions.length > 0 ? positions[0] : 0;
        } else {
            lastVisiblePosition = 0;
        }

        // Calculate progress percentage (0-100)
        int progress = itemCount > 0 ? (int) (((lastVisiblePosition + 1) * 100) / itemCount) : 0;
        progress = Math.min(100, Math.max(0, progress)); // Clamp between 0-100

        // Update progress ring
        progressRing.setProgress(progress);

        // Update percentage text
        if (progressText != null) {
            progressText.setText(progress + "%");
        }
    }
}
