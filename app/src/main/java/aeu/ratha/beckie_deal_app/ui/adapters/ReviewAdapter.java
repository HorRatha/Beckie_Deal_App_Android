package aeu.ratha.beckie_deal_app.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Review;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying product reviews in a list.
 * Shows review rating, title, comment, customer name, and date.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String TAG = "ReviewAdapter";
    private final Context context;
    private final List<Review> reviews;
    private ReviewActionListener actionListener;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    public void setActionListener(@Nullable ReviewActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review, position);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    /**
     * Update the adapter with new reviews list.
     */
    public void setReviews(@NonNull List<Review> newReviews) {
        this.reviews.clear();
        this.reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    /**
     * Add reviews to the existing list (for pagination).
     */
    public void addReviews(@NonNull List<Review> newReviews) {
        int startPosition = reviews.size();
        reviews.addAll(newReviews);
        notifyItemRangeInserted(startPosition, newReviews.size());
    }

    /**
     * Clear all reviews from the adapter.
     */
    public void clear() {
        int size = reviews.size();
        reviews.clear();
        notifyItemRangeRemoved(0, size);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ViewHolder
    // ─────────────────────────────────────────────────────────────────────────

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final RatingBar ratingBar;
        private final TextView titleTextView;
        private final TextView commentTextView;
        private final TextView customerNameTextView;
        private final TextView dateTextView;
        private final ImageView customerAvatarImageView;
        private final TextView verifiedBadgeTextView;
        private final TextView helpfulCountTextView;
        private final View editButton;
        private final View deleteButton;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar = itemView.findViewById(R.id.review_rating_bar);
            titleTextView = itemView.findViewById(R.id.review_title);
            commentTextView = itemView.findViewById(R.id.review_comment);
            customerNameTextView = itemView.findViewById(R.id.review_customer_name);
            dateTextView = itemView.findViewById(R.id.review_date);
            customerAvatarImageView = itemView.findViewById(R.id.review_customer_avatar);
            verifiedBadgeTextView = itemView.findViewById(R.id.review_verified_badge);
            helpfulCountTextView = itemView.findViewById(R.id.review_helpful_count);
            editButton = itemView.findViewById(R.id.review_edit_button);
            deleteButton = itemView.findViewById(R.id.review_delete_button);
        }

        public void bind(Review review, int position) {
            // Rating
            ratingBar.setRating(review.getRating());

            // Title and comment
            titleTextView.setText(review.getTitle());
            commentTextView.setText(review.getComment());

            // Customer info
            if (review.getCustomer() != null) {
                customerNameTextView.setText(review.getCustomer().getName());
                loadCustomerAvatar(review.getCustomer().getAvatar());
            } else {
                customerNameTextView.setText("Anonymous");
            }

            // Date
            dateTextView.setText(formatDate(review.getCreatedAt()));

            // Verified purchase badge
            if (review.isVerifiedPurchase()) {
                verifiedBadgeTextView.setVisibility(View.VISIBLE);
                verifiedBadgeTextView.setText("✓ Verified Purchase");
            } else {
                verifiedBadgeTextView.setVisibility(View.GONE);
            }

            // Helpful count
            if (review.getHelpfulCount() > 0) {
                helpfulCountTextView.setVisibility(View.VISIBLE);
                helpfulCountTextView.setText(review.getHelpfulCount() + " helpful");
            } else {
                helpfulCountTextView.setVisibility(View.GONE);
            }

            // Action buttons (edit/delete) - typically hidden unless it's the user's own review
            editButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditReview(review);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteReview(review);
                }
            });
        }

        private void loadCustomerAvatar(String avatarUrl) {
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(customerAvatarImageView);
            } else {
                customerAvatarImageView.setImageResource(R.drawable.ic_profile_placeholder);
            }
        }

        private String formatDate(String dateString) {
            try {
                if (dateString == null || dateString.isEmpty()) {
                    return "";
                }
                // Parse ISO 8601 date
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                Date date = isoFormat.parse(dateString.replace("Z", "+0000"));
                
                // Format as relative date or short format
                SimpleDateFormat displayFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                return displayFormat.format(date);
            } catch (Exception e) {
                Log.e(TAG, "Error formatting date: " + dateString, e);
                return dateString;
            }
        }
    }

    /**
     * Callback interface for review actions (edit, delete).
     */
    public interface ReviewActionListener {
        void onEditReview(Review review);
        void onDeleteReview(Review review);
    }
}
