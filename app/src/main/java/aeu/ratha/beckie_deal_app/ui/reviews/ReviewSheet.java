package aeu.ratha.beckie_deal_app.ui.reviews;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.ImageUrlHelper;

/** "Write a review" bottom sheet. Replaces the old dialog. */
public final class ReviewSheet {

    public static class Data {
        public String productName;        // null = hide the product row
        public String productImageUrl;
        public String subText;            // "Order #WZS3K · Delivered Oct 22"
        public int rating;                // 0 for a new review
        public String title;
        public String comment;
        public List<String> selectedTags; // tags already chosen (edit mode)
        public boolean isEdit;            // true = "Edit your review" / "Save changes"
    }

    public interface Listener {
        /** Call sheet.setLoading(true) first, then sheet.dismiss() on success or setLoading(false) on error. */
        void onSubmit(ReviewSheet sheet, int rating, String title, String comment, List<String> tags);
    }

    private static final String[] DEFAULT_TAGS = {"Quality", "Value for money", "Fast delivery", "As described"};
    private static final String[] RATING_WORDS = {"", "Poor", "Fair", "Good", "Very good", "Excellent"};

    private final Activity activity;
    private final BottomSheetDialog dialog;
    private final View root;
    private final RatingBar ratingBar;
    private final TextView ratingLabel;
    private final ChipGroup tagsGroup;
    private final EditText titleEdit;
    private final EditText commentEdit;
    private final View submitButton;
    private final ProgressBar progress;

    private ReviewSheet(Activity activity, Data data, Listener listener) {
        this.activity = activity;
        dialog = new BottomSheetDialog(activity, R.style.ThemeOverlay_App_BottomSheetFlat);
        root = LayoutInflater.from(activity).inflate(R.layout.dialog_review, null, false);
        dialog.setContentView(root);

        ratingBar = root.findViewById(R.id.dialog_rating_bar);
        ratingLabel = root.findViewById(R.id.dialog_rating_label);
        tagsGroup = root.findViewById(R.id.dialog_tags_group);
        titleEdit = root.findViewById(R.id.dialog_title_edit);
        commentEdit = root.findViewById(R.id.dialog_comment_edit);
        submitButton = root.findViewById(R.id.dialog_submit_button);
        progress = root.findViewById(R.id.dialog_submit_progress);
        TextView counter = root.findViewById(R.id.dialog_comment_counter);

        // Title and button text
        ((TextView) root.findViewById(R.id.dialog_title))
                .setText(data.isEdit ? "Edit your review" : "Write a review");
        ((TextView) submitButton).setText(data.isEdit ? "Save changes" : "Submit review");

        // Product row
        View productRow = root.findViewById(R.id.dialog_product_row);
        if (data.productName == null) {
            productRow.setVisibility(View.GONE);
        } else {
            ((TextView) root.findViewById(R.id.dialog_product_name)).setText(data.productName);
            TextView sub = root.findViewById(R.id.dialog_product_sub);
            if (data.subText == null || data.subText.isEmpty()) sub.setVisibility(View.GONE);
            else sub.setText(data.subText);
            if (data.productImageUrl != null) {
                Glide.with(activity)
                        .load(ImageUrlHelper.getImageUrlOrFallback(data.productImageUrl))
                        .placeholder(R.drawable.placeholder_image)
                        .into((ImageView) root.findViewById(R.id.dialog_product_image));
            }
        }

        // Rating
        ratingBar.setOnRatingBarChangeListener((bar, value, fromUser) ->
                ratingLabel.setText(RATING_WORDS[Math.max(0, Math.min(5, Math.round(value)))]));
        ratingBar.setRating(data.rating);
        if (data.rating > 0) {
            ratingLabel.setText(RATING_WORDS[Math.max(0, Math.min(5, data.rating))]);
        }

        // Tags
        LayoutInflater inflater = LayoutInflater.from(activity);
        for (String tag : DEFAULT_TAGS) {
            Chip chip = (Chip) inflater.inflate(R.layout.item_review_tag_chip, tagsGroup, false);
            chip.setText(tag);
            chip.setChecked(data.selectedTags != null && data.selectedTags.contains(tag));
            tagsGroup.addView(chip);
        }

        // Fields
        if (data.title != null) titleEdit.setText(data.title);
        if (data.comment != null) commentEdit.setText(data.comment);
        counter.setText(commentEdit.length() + " / 500");
        commentEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                counter.setText(s.length() + " / 500");
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Close + submit
        root.findViewById(R.id.dialog_close).setOnClickListener(v -> dialog.dismiss());
        submitButton.setOnClickListener(v -> {
            int rating = Math.round(ratingBar.getRating());
            if (rating < 1) {
                Toast.makeText(activity, "Please choose a star rating", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < tagsGroup.getChildCount(); i++) {
                Chip c = (Chip) tagsGroup.getChildAt(i);
                if (c.isChecked()) tags.add(c.getText().toString());
            }
            listener.onSubmit(this, rating,
                    titleEdit.getText().toString().trim(),
                    commentEdit.getText().toString().trim(),
                    tags);
        });

        // Open fully, and resize when the keyboard shows
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
    }

    public static ReviewSheet show(Activity activity, Data data, Listener listener) {
        ReviewSheet s = new ReviewSheet(activity, data, listener);
        s.dialog.show();
        return s;
    }

    public void setLoading(boolean loading) {
        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        submitButton.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
        dialog.setCancelable(!loading);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    /**
     * Use this ONLY if your review API has no "tags" field: it adds the chosen tags
     * to the end of the comment so they are not lost.
     */
    public static String commentWithTags(String comment, List<String> tags) {
        if (tags == null || tags.isEmpty()) return comment;
        return (comment == null || comment.isEmpty() ? "" : comment + "\n\n")
                + "Liked: " + String.join(" · ", tags);
    }
}
