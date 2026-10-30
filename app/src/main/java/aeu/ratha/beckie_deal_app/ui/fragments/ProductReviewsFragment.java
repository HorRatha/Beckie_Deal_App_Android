package aeu.ratha.beckie_deal_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

    // UI components
    private ProgressBar loadingProgressBar;
    private TextView emptyStateTextView;
    private View reviewsContentContainer;
    private RecyclerView reviewsRecyclerView;

    public static ProductReviewsFragment newInstance(String productId) {
        ProductReviewsFragment fragment = new ProductReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setReviews(List<Review> reviews, ReviewSummary summary) {
        this.bundledReviews = reviews;
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

    private void applyBundledReviews() {
        if (bundledReviews != null && !bundledReviews.isEmpty()) {
            reviewAdapter.setReviews(bundledReviews);
            showReviewsState();
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
        // Summary container removed per user request
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
