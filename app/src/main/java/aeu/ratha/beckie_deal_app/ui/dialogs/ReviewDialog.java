package aeu.ratha.beckie_deal_app.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Review;
import aeu.ratha.beckie_deal_app.data.models.ReviewCreateRequest;
import aeu.ratha.beckie_deal_app.data.repository.ReviewRepository;

/**
 * Dialog for creating or editing a product review.
 * Shows rating picker, title field, comment field.
 *
 * Args:
 *  - product_id: String (UUID of product)
 *  - order_id: long (Order ID)
 *  - review: Review (optional, for editing)
 */
public class ReviewDialog extends DialogFragment implements
        ReviewRepository.ReviewCreateCallback {

    private static final String TAG = "ReviewDialog";
    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_ORDER_ID = "order_id";
    private static final String ARG_REVIEW = "review";

    private String productId;
    private long orderId;
    private Review existingReview;  // null for create, populated for edit

    private ReviewRepository reviewRepository;
    private ReviewDialogListener dialogListener;

    private RatingBar ratingRatingBar;
    private EditText titleEditText;
    private EditText commentEditText;
    private ProgressBar submitProgressBar;
    private Button submitButton;

    public static ReviewDialog newInstance(String productId, long orderId) {
        ReviewDialog dialog = new ReviewDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        args.putLong(ARG_ORDER_ID, orderId);
        dialog.setArguments(args);
        return dialog;
    }

    public static ReviewDialog newInstance(String productId, long orderId, Review review) {
        ReviewDialog dialog = new ReviewDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        args.putLong(ARG_ORDER_ID, orderId);
        dialog.setArguments(args);
        dialog.existingReview = review;
        return dialog;
    }

    public void setDialogListener(@Nullable ReviewDialogListener listener) {
        this.dialogListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
            orderId = getArguments().getLong(ARG_ORDER_ID);
        }
        reviewRepository = ReviewRepository.getInstance(requireContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        
        // Create view
        android.view.View dialogView = requireActivity().getLayoutInflater()
                .inflate(R.layout.dialog_review, null);
        
        initializeViews(dialogView);
        setupListeners();
        
        // Populate existing data if editing
        if (existingReview != null) {
            populateExistingReview();
        }

        builder.setView(dialogView)
                .setTitle(existingReview == null ? "Write a Review" : "Edit Review")
                .setNegativeButton("Cancel", (dialog, which) -> dismiss());

        return builder.create();
    }

    private void initializeViews(android.view.View view) {
        ratingRatingBar = view.findViewById(R.id.dialog_rating_bar);
        titleEditText = view.findViewById(R.id.dialog_title_edit);
        commentEditText = view.findViewById(R.id.dialog_comment_edit);
        submitProgressBar = view.findViewById(R.id.dialog_submit_progress);
        submitButton = view.findViewById(R.id.dialog_submit_button);
    }

    private void setupListeners() {
        submitButton.setOnClickListener(v -> submitReview());
    }

    private void populateExistingReview() {
        if (existingReview == null) return;
        ratingRatingBar.setRating(existingReview.getRating());
        titleEditText.setText(existingReview.getTitle());
        commentEditText.setText(existingReview.getComment());
    }

    private void submitReview() {
        // Validate input
        int rating = (int) ratingRatingBar.getRating();
        String title = titleEditText.getText().toString().trim();
        String comment = commentEditText.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(requireContext(), "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }

        if (comment.isEmpty()) {
            commentEditText.setError("Comment is required");
            return;
        }

        // Show loading
        submitProgressBar.setVisibility(android.view.View.VISIBLE);
        submitButton.setEnabled(false);

        ReviewCreateRequest request = new ReviewCreateRequest(
                productId, orderId, rating, title, comment);

        if (existingReview == null) {
            // Create new review
            reviewRepository.createReview(request, this);
        } else {
            // Update existing review
            reviewRepository.updateReview(existingReview.getId(), request, this);
        }
    }

    private void hideLoading() {
        submitProgressBar.setVisibility(android.view.View.GONE);
        submitButton.setEnabled(true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ReviewRepository.ReviewCreateCallback
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void onReviewCreated(Review review) {
        Log.d(TAG, "Review created/updated successfully");
        hideLoading();
        Toast.makeText(requireContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
        
        if (dialogListener != null) {
            dialogListener.onReviewSubmitted(review);
        }
        
        dismiss();
    }

    @Override
    public void onError(String error) {
        Log.e(TAG, "Error submitting review: " + error);
        hideLoading();
        Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback interface for review dialog events.
     */
    public interface ReviewDialogListener {
        void onReviewSubmitted(Review review);
    }
}
