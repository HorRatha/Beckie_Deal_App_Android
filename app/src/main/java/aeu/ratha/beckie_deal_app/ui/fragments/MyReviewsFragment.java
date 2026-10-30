package aeu.ratha.beckie_deal_app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Review;
import aeu.ratha.beckie_deal_app.data.models.ReviewCreateRequest;
import aeu.ratha.beckie_deal_app.data.repository.ReviewRepository;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity;
import aeu.ratha.beckie_deal_app.ui.reviews.MyReviewBinder;
import aeu.ratha.beckie_deal_app.ui.reviews.MyReviewData;
import aeu.ratha.beckie_deal_app.ui.reviews.ReviewSheet;

import java.util.List;
import java.util.Locale;

/**
 * Fragment for displaying the current user's reviews (My Reviews)
 * using MyReviewBinder and ReviewSheet UI.
 */
public class MyReviewsFragment extends Fragment implements
        ReviewRepository.ReviewsCallback {

    private static final String TAG = "MyReviewsFragment";

    private ReviewRepository reviewRepository;
    private ProgressBar loadingProgressBar;
    private TextView emptyStateTextView;
    private LinearLayout reviewsList;
    private TextView reviewsSummary;

    public static MyReviewsFragment newInstance() {
        return new MyReviewsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_reviews, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        loadReviews();
    }

    private void initializeViews(View view) {
        loadingProgressBar = view.findViewById(R.id.my_reviews_loading_progress);
        emptyStateTextView = view.findViewById(R.id.my_reviews_empty_state);
        reviewsList = view.findViewById(R.id.reviewsList);
        reviewsSummary = view.findViewById(R.id.reviewsSummary);
    }

    private void loadReviews() {
        reviewRepository = ReviewRepository.getInstance(requireContext());
        if (loadingProgressBar != null) loadingProgressBar.setVisibility(View.VISIBLE);
        if (emptyStateTextView != null) emptyStateTextView.setVisibility(View.GONE);
        if (reviewsList != null) reviewsList.setVisibility(View.GONE);

        reviewRepository.getMyReviews(1, 20, this);
    }

    @Override
    public void onReviewsLoaded(List<Review> reviews) {
        Log.d(TAG, "Loaded " + (reviews != null ? reviews.size() : 0) + " user reviews");
        if (!isAdded()) return;

        requireActivity().runOnUiThread(() -> {
            if (loadingProgressBar != null) loadingProgressBar.setVisibility(View.GONE);

            if (reviews == null || reviews.isEmpty()) {
                if (emptyStateTextView != null) emptyStateTextView.setVisibility(View.VISIBLE);
                if (reviewsList != null) reviewsList.setVisibility(View.GONE);
                if (reviewsSummary != null) reviewsSummary.setText("0 reviews");
                return;
            }

            if (emptyStateTextView != null) emptyStateTextView.setVisibility(View.GONE);
            if (reviewsList != null) reviewsList.setVisibility(View.VISIBLE);

            // Summary line
            double sum = 0;
            for (Review r : reviews) sum += r.getRating();
            String summary = reviews.size() + (reviews.size() == 1 ? " review" : " reviews")
                    + String.format(Locale.US, " · average %.1f ★", sum / reviews.size());
            if (reviewsSummary != null) reviewsSummary.setText(summary);

            reviewsList.removeAllViews();
            for (Review r : reviews) {
                MyReviewData d = new MyReviewData();
                d.reviewId = String.valueOf(r.getId());
                d.productId = r.getProductId();
                d.productName = "Product #" + r.getProductId();
                d.productImageUrl = null;
                d.rating = r.getRating();
                d.title = r.getTitle();
                d.comment = r.getComment();
                d.dateText = r.getCreatedAt() != null ? r.getCreatedAt() : "Recently";
                d.verified = r.isVerifiedPurchase();

                View cardView = MyReviewBinder.inflateAndBind(requireContext(), reviewsList, d, new MyReviewBinder.Listener() {
                    @Override
                    public void onOpenProduct(MyReviewData data) {
                        Intent i = new Intent(requireActivity(), ProductDetailActivity.class);
                        i.putExtra("product_id", data.productId);
                        startActivity(i);
                    }

                    @Override
                    public void onEdit(MyReviewData data) {
                        ReviewSheet.Data sd = new ReviewSheet.Data();
                        sd.isEdit = true;
                        sd.productName = data.productName;
                        sd.productImageUrl = data.productImageUrl;
                        sd.rating = data.rating;
                        sd.title = data.title;
                        sd.comment = data.comment;
                        ReviewSheet.show(requireActivity(), sd, (sheet, rating, title, comment, tags) -> {
                            sheet.setLoading(true);
                            String finalComment = ReviewSheet.commentWithTags(comment, tags);
                            ReviewCreateRequest request = new ReviewCreateRequest(
                                    data.productId, 1L, rating, title, finalComment);
                            reviewRepository.updateReview(Long.parseLong(data.reviewId), request, new ReviewRepository.ReviewCreateCallback() {
                                @Override
                                public void onReviewCreated(Review updatedReview) {
                                    if (isAdded()) {
                                        requireActivity().runOnUiThread(() -> {
                                            sheet.dismiss();
                                            Toast.makeText(requireContext(), "Review updated successfully", Toast.LENGTH_SHORT).show();
                                            loadReviews();
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
                    }

                    @Override
                    public void onDelete(MyReviewData data) {
                        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Delete this review?")
                                .setMessage("This can't be undone.")
                                .setNegativeButton("Cancel", null)
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    reviewRepository.deleteReview(Long.parseLong(data.reviewId), data.productId, new ReviewRepository.DeleteReviewCallback() {
                                        @Override
                                        public void onReviewDeleted() {
                                            if (isAdded()) {
                                                requireActivity().runOnUiThread(() -> {
                                                    Toast.makeText(requireContext(), "Review deleted", Toast.LENGTH_SHORT).show();
                                                    loadReviews();
                                                });
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            if (isAdded()) {
                                                requireActivity().runOnUiThread(() -> {
                                                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                                });
                                            }
                                        }
                                    });
                                })
                                .show();
                    }
                });

                reviewsList.addView(cardView);
            }
        });
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Error loading reviews: " + error);
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            if (loadingProgressBar != null) loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
        });
    }
}
