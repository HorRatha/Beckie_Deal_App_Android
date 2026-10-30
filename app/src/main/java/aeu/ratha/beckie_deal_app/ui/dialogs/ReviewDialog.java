package aeu.ratha.beckie_deal_app.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.ProductDetail;
import aeu.ratha.beckie_deal_app.data.models.Review;
import aeu.ratha.beckie_deal_app.data.models.ReviewCreateRequest;
import aeu.ratha.beckie_deal_app.data.repository.ProductRepository;
import aeu.ratha.beckie_deal_app.data.repository.ReviewRepository;
import aeu.ratha.beckie_deal_app.utils.helpers.ImageUrlHelper;
import aeu.ratha.beckie_deal_app.utils.managers.ProductImageCacheManager;

/**
 * BottomSheetDialogFragment for creating or editing a product review.
 * Modern UI matching the rest of the application.
 */
public class ReviewDialog extends BottomSheetDialogFragment implements
        ReviewRepository.ReviewCreateCallback {

    private static final String TAG = "ReviewDialog";
    private static final String ARG_PRODUCT_ID = "product_id";
    private static final String ARG_ORDER_ID = "order_id";

    private static final String[] RATING_WORDS = {"", "Poor", "Fair", "Good", "Very good", "Excellent"};

    private String productId;
    private long orderId;
    private Review existingReview;  // null for create, populated for edit

    private ReviewRepository reviewRepository;
    private ReviewDialogListener dialogListener;

    private TextView titleTextView;
    private View closeButton;
    private RatingBar ratingRatingBar;
    private TextView ratingLabel;
    private EditText titleEditText;
    private EditText commentEditText;
    private TextView commentCounter;
    private ProgressBar submitProgressBar;
    private View submitButton;

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
        setStyle(STYLE_NORMAL, R.style.ThemeOverlay_App_BottomSheetFlat);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
            orderId = getArguments().getLong(ARG_ORDER_ID);
        }
        reviewRepository = ReviewRepository.getInstance(requireContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        dialog.setOnShowListener(d -> {
            FrameLayout sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (sheet != null) {
                BottomSheetBehavior<FrameLayout> b = BottomSheetBehavior.from(sheet);
                b.setState(BottomSheetBehavior.STATE_EXPANDED);
                b.setSkipCollapsed(true);
            }
        });
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupListeners();

        if (existingReview != null) {
            populateExistingReview();
        }
    }

    private void initializeViews(View view) {
        titleTextView = view.findViewById(R.id.dialog_title);
        closeButton = view.findViewById(R.id.dialog_close);
        ratingRatingBar = view.findViewById(R.id.dialog_rating_bar);
        ratingLabel = view.findViewById(R.id.dialog_rating_label);
        titleEditText = view.findViewById(R.id.dialog_title_edit);
        commentEditText = view.findViewById(R.id.dialog_comment_edit);
        commentCounter = view.findViewById(R.id.dialog_comment_counter);
        submitProgressBar = view.findViewById(R.id.dialog_submit_progress);
        submitButton = view.findViewById(R.id.dialog_submit_button);

        ImageView productImage = view.findViewById(R.id.dialog_product_image);
        TextView productName = view.findViewById(R.id.dialog_product_name);
        TextView productSub = view.findViewById(R.id.dialog_product_sub);

        if (titleTextView != null) {
            titleTextView.setText(existingReview == null ? "Write a review" : "Edit review");
        }

        if (orderId > 0 && productSub != null) {
            productSub.setText("Order #" + orderId);
        }

        if (productId != null) {
            String cachedImg = ProductImageCacheManager.getCachedImageUrl(requireContext(), productId);
            if (cachedImg != null && !cachedImg.trim().isEmpty() && productImage != null) {
                Glide.with(this)
                        .load(ImageUrlHelper.getImageUrlOrFallback(cachedImg))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .centerCrop()
                        .into(productImage);
            }

            ProductRepository.getInstance(requireContext()).fetchProductDetail(productId, new ProductRepository.SingleProductCallback() {
                @Override
                public void onSuccess(ProductDetail detail) {
                    if (!isAdded() || detail == null) return;
                    requireActivity().runOnUiThread(() -> {
                        if (productName != null && detail.getName() != null) {
                            productName.setText(detail.getName());
                        }
                        if (productImage != null && detail.getThumbnail() != null && !detail.getThumbnail().trim().isEmpty()) {
                            String thumb = ImageUrlHelper.getImageUrlOrFallback(detail.getThumbnail());
                            ProductImageCacheManager.cacheImageUrl(requireContext(), productId, thumb);
                            Glide.with(ReviewDialog.this)
                                    .load(thumb)
                                    .placeholder(R.drawable.placeholder_image)
                                    .error(R.drawable.placeholder_image)
                                    .centerCrop()
                                    .into(productImage);
                        }
                    });
                }

                @Override
                public void onError(String error) {}
            });
        }
    }

    private void setupListeners() {
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dismiss());
        }

        if (ratingRatingBar != null) {
            ratingRatingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                int r = Math.max(0, Math.min(5, Math.round(rating)));
                if (ratingLabel != null) {
                    ratingLabel.setText(RATING_WORDS[r]);
                }
            });
        }

        if (commentEditText != null && commentCounter != null) {
            commentEditText.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    commentCounter.setText(s.length() + " / 500");
                }
                @Override public void afterTextChanged(android.text.Editable s) {}
            });
        }

        if (submitButton != null) {
            submitButton.setOnClickListener(v -> submitReview());
        }
    }

    private void populateExistingReview() {
        if (existingReview == null) return;
        if (ratingRatingBar != null) {
            ratingRatingBar.setRating(existingReview.getRating());
            int r = Math.max(0, Math.min(5, existingReview.getRating()));
            if (ratingLabel != null) {
                ratingLabel.setText(RATING_WORDS[r]);
            }
        }
        if (titleEditText != null) titleEditText.setText(existingReview.getTitle());
        if (commentEditText != null) commentEditText.setText(existingReview.getComment());
    }

    private void submitReview() {
        int rating = ratingRatingBar != null ? Math.round(ratingRatingBar.getRating()) : 0;
        String title = titleEditText != null ? titleEditText.getText().toString().trim() : "";
        String comment = commentEditText != null ? commentEditText.getText().toString().trim() : "";

        if (rating == 0) {
            Toast.makeText(requireContext(), "Please select a star rating", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        if (submitProgressBar != null) submitProgressBar.setVisibility(View.VISIBLE);
        if (submitButton != null) submitButton.setVisibility(View.INVISIBLE);
        setCancelable(false);

        ReviewCreateRequest request = new ReviewCreateRequest(
                productId, orderId, rating, title, comment);

        if (existingReview == null) {
            reviewRepository.createReview(request, this);
        } else {
            reviewRepository.updateReview(existingReview.getId(), request, this);
        }
    }

    private void hideLoading() {
        if (submitProgressBar != null) submitProgressBar.setVisibility(View.GONE);
        if (submitButton != null) submitButton.setVisibility(View.VISIBLE);
        setCancelable(true);
    }

    @Override
    public void onReviewCreated(Review review) {
        Log.d(TAG, "Review submitted successfully");
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

    public interface ReviewDialogListener {
        void onReviewSubmitted(Review review);
    }
}
