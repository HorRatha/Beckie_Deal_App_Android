package aeu.ratha.beckie_deal_app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.ImageUrlHelper;

public class MyReviewsAdapter extends ListAdapter<ReviewListItem, RecyclerView.ViewHolder> {

    private static final int T_HEADER = 0;
    private static final int T_PENDING = 1;
    private static final int T_REVIEW = 2;

    public interface OnWriteReviewListener {
        void onWriteReview(ReviewListItem.Pending item, float rating);
    }

    public interface OnEditReviewListener {
        void onEdit(ReviewListItem.ReviewItem item);
    }

    private final OnWriteReviewListener onWriteReview;
    private final OnEditReviewListener onEdit;

    public static final DiffUtil.ItemCallback<ReviewListItem> DIFF = new DiffUtil.ItemCallback<ReviewListItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReviewListItem a, @NonNull ReviewListItem b) {
            if (a instanceof ReviewListItem.Header && b instanceof ReviewListItem.Header) {
                return ((ReviewListItem.Header) a).title.equals(((ReviewListItem.Header) b).title);
            }
            if (a instanceof ReviewListItem.Pending && b instanceof ReviewListItem.Pending) {
                return ((ReviewListItem.Pending) a).id.equals(((ReviewListItem.Pending) b).id);
            }
            if (a instanceof ReviewListItem.ReviewItem && b instanceof ReviewListItem.ReviewItem) {
                return ((ReviewListItem.ReviewItem) a).id.equals(((ReviewListItem.ReviewItem) b).id);
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReviewListItem a, @NonNull ReviewListItem b) {
            if (a instanceof ReviewListItem.Header && b instanceof ReviewListItem.Header) {
                return ((ReviewListItem.Header) a).equals(b);
            }
            if (a instanceof ReviewListItem.Pending && b instanceof ReviewListItem.Pending) {
                return ((ReviewListItem.Pending) a).equals(b);
            }
            if (a instanceof ReviewListItem.ReviewItem && b instanceof ReviewListItem.ReviewItem) {
                return ((ReviewListItem.ReviewItem) a).equals(b);
            }
            return false;
        }
    };

    public MyReviewsAdapter(OnWriteReviewListener onWriteReview, OnEditReviewListener onEdit) {
        super(DIFF);
        this.onWriteReview = onWriteReview;
        this.onEdit = onEdit;
    }

    @Override
    public int getItemViewType(int position) {
        ReviewListItem item = getItem(position);
        if (item instanceof ReviewListItem.Header) return T_HEADER;
        if (item instanceof ReviewListItem.Pending) return T_PENDING;
        return T_REVIEW;
    }

    public boolean isReview(int position) {
        return position >= 0 && position < getItemCount() && getItem(position) instanceof ReviewListItem.ReviewItem;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == T_HEADER) {
            View v = inf.inflate(R.layout.item_section_header, parent, false);
            return new HeaderVH(v);
        } else if (viewType == T_PENDING) {
            View v = inf.inflate(R.layout.item_pending_review, parent, false);
            return new PendingVH(v);
        } else {
            View v = inf.inflate(R.layout.item_review, parent, false);
            return new ReviewVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReviewListItem item = getItem(position);
        if (holder instanceof HeaderVH && item instanceof ReviewListItem.Header) {
            ((HeaderVH) holder).sectionTitle.setText(((ReviewListItem.Header) item).title);
        } else if (holder instanceof PendingVH && item instanceof ReviewListItem.Pending) {
            ((PendingVH) holder).bind((ReviewListItem.Pending) item);
        } else if (holder instanceof ReviewVH && item instanceof ReviewListItem.ReviewItem) {
            ((ReviewVH) holder).bind((ReviewListItem.ReviewItem) item);
        }
    }

    public static class HeaderVH extends RecyclerView.ViewHolder {
        public final TextView sectionTitle;

        public HeaderVH(View itemView) {
            super(itemView);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
        }
    }

    public class PendingVH extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView deliveredDate;
        private final RatingBar ratingBar;
        private final MaterialButton writeReviewBtn;

        public PendingVH(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            deliveredDate = itemView.findViewById(R.id.deliveredDate);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            writeReviewBtn = itemView.findViewById(R.id.writeReviewBtn);
        }

        public void bind(ReviewListItem.Pending item) {
            productName.setText(item.name);
            deliveredDate.setText("Delivered " + item.delivered);
            ratingBar.setRating(0f);
            if (item.image != null && !item.image.trim().isEmpty()) {
                Glide.with(productImage.getContext())
                        .load(ImageUrlHelper.getImageUrlOrFallback(item.image))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .centerCrop()
                        .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.placeholder_image);
            }
            writeReviewBtn.setOnClickListener(v -> {
                if (onWriteReview != null) {
                    onWriteReview.onWriteReview(item, ratingBar.getRating());
                }
            });
            ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                if (fromUser && onWriteReview != null) {
                    onWriteReview.onWriteReview(item, rating);
                }
            });
        }
    }

    public class ReviewVH extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final RatingBar ratingBar;
        private final TextView comment;
        private final ImageButton moreBtn;

        public ReviewVH(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            comment = itemView.findViewById(R.id.comment);
            moreBtn = itemView.findViewById(R.id.moreBtn);
        }

        public void bind(ReviewListItem.ReviewItem item) {
            productName.setText(item.name);
            ratingBar.setRating(item.rating);
            comment.setText(item.comment);
            if (item.image != null && !item.image.trim().isEmpty()) {
                Glide.with(productImage.getContext())
                        .load(ImageUrlHelper.getImageUrlOrFallback(item.image))
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .centerCrop()
                        .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.placeholder_image);
            }
            moreBtn.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.getMenuInflater().inflate(R.menu.menu_review_item, popup.getMenu());
                popup.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.action_edit) {
                        if (onEdit != null) {
                            onEdit.onEdit(item);
                        }
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }
    }
}
