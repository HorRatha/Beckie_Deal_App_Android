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
import aeu.ratha.beckie_deal_app.data.models.ReviewCreateRequest;
import aeu.ratha.beckie_deal_app.data.models.ReviewSummary;
import aeu.ratha.beckie_deal_app.data.repository.ReviewRepository;
import aeu.ratha.beckie_deal_app.ui.adapters.ReviewAdapter;
import aeu.ratha.beckie_deal_app.ui.reviews.ReviewSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for displaying product reviews on the product detail page.
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

    private List<Review> bundledReviews;
    private ReviewSummary bundledSummary;

    // UI components
    private ProgressBar loadingProgressBar;
    private TextView emptyStateTextView;
    private View reviewsContentContainer;
    private RecyclerView reviewsRecyclerView;
    private LinearLayout reviewSummaryContainer;
    private RatingBar summaryRatingBar;
    private TextView averageRatingTextView;
    private TextView totalReviewsTextView;
    private LinearLayout ratingBreakdownContainer;
    private View writeReviewButton;

    public static ProductReviewsFragment newInstance(String productId) {
        ProductReviewsFragment fragment = new ProductReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setReviews(List<Review> reviews, ReviewSummary summary) {
        this.bundledReviews = reviews;
        this.bundledSummary = summary;
        if (isAdded() && getView() != null) {
            applyBundledReviews();
        }
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
        setupWriteReviewButton();

        if (bundledReviews != null) {
            applyBundledReviews();
        } else {
            loadReviews();
        }
    }

    private void initializeViews(View view) {
        loadingProgressBar = view.findViewById(R.id.reviews_loading_progress);
        emptyStateTextView = view.findViewById(R.id.reviews_empty_state);
        reviewsContentContainer = view.findViewById(R.id.reviews_content_container);
        reviewsRecyclerView = view.findViewById(R.id.reviews_recycler_view);
        reviewSummaryContainer = view.findViewById(R.id.review_summary_container);
        summaryRatingBar = view.findViewById(R.id.summary_rating_bar);
        averageRatingTextView = view.findViewById(R.id.average_rating_text);
        totalReviewsTextView = view.findViewById(R.id.total_reviews_text);
        ratingBreakdownContainer = view.findViewById(R.id.rating_breakdown_container);
        writeReviewButton = view.findViewById(R.id.write_review_button);
    }

    private void setupRecyclerView() {
        reviewAdapter = new ReviewAdapter(requireContext(), new ArrayList<>());
        reviewAdapter.setActionListener(this);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        reviewsRecyclerView.setAdapter(reviewAdapter);

        reviewsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();
                    if (lastVisibleItem >= totalItemCount - 3 && !isLoadingMore && bundledReviews == null) {
                        loadMoreReviews();
                    }
                }
            }
        });
    }

    private void setupWriteReviewButton() {
        if (writeReviewButton != null) {
            writeReviewButton.setOnClickListener(v -> {
                ReviewSheet.Data d = new ReviewSheet.Data();
                d.productName = "Product";
                d.subText = "Write your review";
                ReviewSheet.show(requireActivity(), d, (sheet, rating, title, comment, tags) -> {
                    sheet.setLoading(true);
                    String finalComment = ReviewSheet.commentWithTags(comment, tags);
                    ReviewCreateRequest request = new ReviewCreateRequest(
                            productId, 0L, rating, title, finalComment);
                    ReviewRepository.getInstance(requireContext()).createReview(request, new ReviewRepository.ReviewCreateCallback() {
                        @Override
                        public void onReviewCreated(Review review) {
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    sheet.dismiss();
                                    Toast.makeText(requireContext(), "Review submitted successfully! Pending approval.", Toast.LENGTH_LONG).show();
                                });
                            }
                        }

                        @Override
                        public void onError(String error) {
                            if (isAdded()) {
                                requireActivity().runOnUiThread(() -> {
                                    sheet.setLoading(false);
                                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    });
                });
            });
        }
    }

    private void applyBundledReviews() {
        if (bundledReviews != null && !bundledReviews.isEmpty()) {
            reviewAdapter.setReviews(bundledReviews);
            showReviewsState();
            if (bundledSummary != null) {
                onReviewSummary(productId, bundledSummary);
            } else {
                double sum = 0;
                for (Review r : bundledReviews) sum += r.getRating();
                double avg = sum / bundledReviews.size();
                averageRatingTextView.setText(String.format(Locale.US, "%.1f", avg));
                totalReviewsTextView.setText(bundledReviews.size() + " Reviews");
                summaryRatingBar.setRating((float) avg);
            }
        } else {
            showEmptyState();
        }
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
        if (loadingProgressBar != null) loadingProgressBar.setVisibility(View.VISIBLE);
        if (emptyStateTextView != null) emptyStateTextView.setVisibility(View.GONE);
        if (reviewsContentContainer != null) reviewsContentContainer.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        if (loadingProgressBar != null) loadingProgressBar.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        hideLoadingState();
        if (emptyStateTextView != null) {
            emptyStateTextView.setVisibility(View.VISIBLE);
            emptyStateTextView.setText("No reviews yet. Be the first to review this product!");
        }
        if (reviewsContentContainer != null) reviewsContentContainer.setVisibility(View.GONE);
    }

    private void showReviewsState() {
        hideLoadingState();
        if (emptyStateTextView != null) emptyStateTextView.setVisibility(View.GONE);
        if (reviewsContentContainer != null) reviewsContentContainer.setVisibility(View.VISIBLE);
        if (reviewsRecyclerView != null) reviewsRecyclerView.setVisibility(View.VISIBLE);
        if (reviewSummaryContainer != null) reviewSummaryContainer.setVisibility(View.VISIBLE);
    }

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
        if (summary == null || summaryRatingBar == null) return;

        float avgRating = (float) summary.getAverageRating();
        summaryRatingBar.setRating(avgRating);
        averageRatingTextView.setText(String.format(Locale.US, "%.1f", avgRating));
        totalReviewsTextView.setText(summary.getTotalReviews() + " Reviews");

        displayRatingBreakdown(summary);
    }

    private void displayRatingBreakdown(ReviewSummary summary) {
        if (ratingBreakdownContainer == null) return;
        ratingBreakdownContainer.removeAllViews();

        if (summary.getRatingBreakdown() == null) return;

        for (aeu.ratha.beckie_deal_app.data.models.ReviewRating rating : summary.getRatingBreakdown()) {
            LinearLayout breakdownRow = new LinearLayout(requireContext());
            breakdownRow.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            breakdownRow.setOrientation(LinearLayout.HORIZONTAL);
            breakdownRow.setPadding(0, 4, 0, 4);

            TextView starCountText = new TextView(requireContext());
            starCountText.setText(rating.getRating() + " ★");
            starCountText.setLayoutParams(new LinearLayout.LayoutParams(40, LinearLayout.LayoutParams.WRAP_CONTENT));
            starCountText.setTextSize(12);
            breakdownRow.addView(starCountText);

            ProgressBar progressBar = new ProgressBar(requireContext(), null,
                    android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(0, 20, 1);
            progressParams.setMarginStart(8);
            progressParams.setMarginEnd(8);
            progressBar.setLayoutParams(progressParams);
            progressBar.setProgress(rating.getPercentage());
            breakdownRow.addView(progressBar);

            TextView percentText = new TextView(requireContext());
            percentText.setText(rating.getPercentage() + "%");
            percentText.setLayoutParams(new LinearLayout.LayoutParams(50, LinearLayout.LayoutParams.WRAP_CONTENT));
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

    @Override
    public void onEditReview(Review review) {
        Toast.makeText(requireContext(), "Edit review: " + review.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteReview(Review review) {
        Toast.makeText(requireContext(), "Delete review: " + review.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
