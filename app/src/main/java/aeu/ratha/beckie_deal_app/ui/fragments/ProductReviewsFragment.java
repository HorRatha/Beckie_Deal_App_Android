package aeu.ratha.beckie_deal_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Review;
import aeu.ratha.beckie_deal_app.data.models.ReviewSummary;
import aeu.ratha.beckie_deal_app.data.repository.ReviewRepository;
import aeu.ratha.beckie_deal_app.ui.adapters.ReviewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for displaying product reviews.
 * Shows review summary (average rating, breakdown) and paginated list of reviews.
 * Typically displayed in product detail page.
 *
 * Args:
 *  - product_id: String (UUID of the product)
 */
public class ProductReviewsFragment extends Fragment implements
        ReviewAdapter.ReviewActionListener,
        ReviewRepository.ProductReviewsCallback {

    private static final String TAG = "ProductReviewsFragment";
    private static final String ARG_PRODUCT_ID = "product_id";

    private String productId;
    private ReviewRepository reviewRepository;
    private ReviewAdapter reviewAdapter;
    private int currentPage = 1;
    private boolean isLoadingMore = false;

    // UI components
    private ProgressBar loadingProgressBar;
    private TextView emptyStateTextView;
    private RecyclerView reviewsRecyclerView;
    private LinearLayout reviewSummaryContainer;
    private RatingBar summaryRatingBar;
    private TextView averageRatingTextView;
    private TextView totalReviewsTextView;

    // Rating breakdown views
    private LinearLayout ratingBreakdownContainer;

    public static ProductReviewsFragment newInstance(String productId) {
        ProductReviewsFragment fragment = new ProductReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        loadReviews();
    }

    private void initializeViews(View view) {
        loadingProgressBar = view.findViewById(R.id.reviews_loading_progress);
        emptyStateTextView = view.findViewById(R.id.reviews_empty_state);
        reviewsRecyclerView = view.findViewById(R.id.reviews_recycler_view);
        reviewSummaryContainer = view.findViewById(R.id.review_summary_container);
        summaryRatingBar = view.findViewById(R.id.summary_rating_bar);
        averageRatingTextView = view.findViewById(R.id.average_rating_text);
        totalReviewsTextView = view.findViewById(R.id.total_reviews_text);
        ratingBreakdownContainer = view.findViewById(R.id.rating_breakdown_container);
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(requireContext(), new ArrayList<>());
        reviewAdapter.setActionListener(this);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewsRecyclerView.setAdapter(reviewAdapter);

        // Add pagination listener
        reviewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    // Load more when near the bottom
                    if (lastVisibleItem >= totalItemCount - 3 && !isLoadingMore) {
                        loadMoreReviews();
                    }
                }
            }
        });
    }

    private void loadReviews() {
        if (productId == null) {
            Log.e(TAG, "Product ID not provided");
            showEmptyState();
            return;
        }

        reviewRepository = ReviewRepository.getInstance(requireContext());
        currentPage = 1;
        reviewAdapter.clear();
        showLoadingState();
        reviewRepository.getProductReviews(productId, currentPage, 10, this);
    }

    private void loadMoreReviews() {
        if (isLoadingMore || reviewRepository == null) return;
        isLoadingMore = true;
        currentPage++;
        reviewRepository.getProductReviews(productId, currentPage, 10, this);
    }

    private void showLoadingState() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        emptyStateTextView.setVisibility(View.GONE);
        reviewsRecyclerView.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        hideLoadingState();
        emptyStateTextView.setVisibility(View.VISIBLE);
        reviewsRecyclerView.setVisibility(View.GONE);
        reviewSummaryContainer.setVisibility(View.GONE);
        emptyStateTextView.setText("No reviews yet. Be the first to review this product!");
    }

    private void showReviewsState() {
        hideLoadingState();
        emptyStateTextView.setVisibility(View.GONE);
        reviewsRecyclerView.setVisibility(View.VISIBLE);
        reviewSummaryContainer.setVisibility(View.VISIBLE);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ReviewRepository.ProductReviewsCallback
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void onProductReviewsLoaded(String productId, List<Review> reviews) {
        Log.d(TAG, "Loaded " + reviews.size() + " reviews for product " + productId);
        isLoadingMore = false;

        if (currentPage == 1) {
            reviewAdapter.setReviews(reviews);
            if (reviews.isEmpty()) {
                showEmptyState();
            } else {
                showReviewsState();
            }
        } else {
            reviewAdapter.addReviews(reviews);
        }
    }

    @Override
    public void onReviewSummary(String productId, ReviewSummary summary) {
        if (summary == null) return;

        // Set average rating and total reviews
        float avgRating = (float) summary.getAverageRating();
        summaryRatingBar.setRating(avgRating);
        averageRatingTextView.setText(String.format("%.1f", avgRating));
        totalReviewsTextView.setText(summary.getTotalReviews() + " Reviews");

        // Display rating breakdown
        displayRatingBreakdown(summary);
    }

    private void displayRatingBreakdown(ReviewSummary summary) {
        ratingBreakdownContainer.removeAllViews();

        if (summary.getRatingBreakdown() == null) return;

        for (aeu.ratha.beckie_deal_app.data.models.ReviewRating rating : summary.getRatingBreakdown()) {
            LinearLayout breakdownRow = new LinearLayout(requireContext());
            breakdownRow.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            breakdownRow.setOrientation(LinearLayout.HORIZONTAL);
            breakdownRow.setPadding(0, 4, 0, 4);

            // Star count
            TextView starCountText = new TextView(requireContext());
            starCountText.setText(rating.getRating() + " ★");
            starCountText.setLayoutParams(new LinearLayout.LayoutParams(40, LinearLayout.LayoutParams.WRAP_CONTENT));
            starCountText.setTextSize(12);
            breakdownRow.addView(starCountText);

            // Progress bar (percentage)
            ProgressBar progressBar = new ProgressBar(requireContext(), null,
                    android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(0,
                    20, 1);
            progressParams.setMarginStart(8);
            progressParams.setMarginEnd(8);
            progressBar.setLayoutParams(progressParams);
            progressBar.setProgress(rating.getPercentage());
            breakdownRow.addView(progressBar);

            // Percentage text
            TextView percentText = new TextView(requireContext());
            percentText.setText(rating.getPercentage() + "%");
            percentText.setLayoutParams(new LinearLayout.LayoutParams(50,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            percentText.setTextSize(12);
            breakdownRow.addView(percentText);

            ratingBreakdownContainer.addView(breakdownRow);
        }
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Error loading reviews: " + error);
        hideLoadingState();
        isLoadingMore = false;
        Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ReviewAdapter.ReviewActionListener
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void onEditReview(Review review) {
        // TODO: Open edit review dialog/activity
        Toast.makeText(requireContext(), "Edit review: " + review.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteReview(Review review) {
        // TODO: Show confirmation dialog and delete
        Toast.makeText(requireContext(), "Delete review: " + review.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
