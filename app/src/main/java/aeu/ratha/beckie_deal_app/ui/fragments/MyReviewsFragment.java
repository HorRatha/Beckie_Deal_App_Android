package aeu.ratha.beckie_deal_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.ProductDetail;
import aeu.ratha.beckie_deal_app.data.models.Review;
import aeu.ratha.beckie_deal_app.data.models.ReviewCreateRequest;
import aeu.ratha.beckie_deal_app.data.repository.ProductRepository;
import aeu.ratha.beckie_deal_app.data.repository.ReviewRepository;
import aeu.ratha.beckie_deal_app.ui.adapters.MyReviewsAdapter;
import aeu.ratha.beckie_deal_app.ui.adapters.ReviewListItem;
import aeu.ratha.beckie_deal_app.ui.adapters.SwipeToDeleteCallback;
import aeu.ratha.beckie_deal_app.ui.reviews.ReviewSheet;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;
import aeu.ratha.beckie_deal_app.utils.managers.ProductImageCacheManager;

/**
 * Fragment displaying the user's reviews dynamically using three row types:
 * section header, waiting for review card (only shown when pending items exist), and review row.
 * Swipe-left to delete and 3-dot edit menu apply to review rows.
 */
public class MyReviewsFragment extends Fragment implements ReviewRepository.ReviewsCallback {

    private static final String TAG = "MyReviewsFragment";

    private ReviewRepository reviewRepository;
    private ProductRepository productRepository;
    private RecyclerView reviewsRecycler;
    private MyReviewsAdapter adapter;

    private final List<ReviewListItem.Pending> pendingItems = new ArrayList<>();
    private final List<ReviewListItem.ReviewItem> reviewItems = new ArrayList<>();

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
        reviewRepository = ReviewRepository.getInstance(requireContext());
        productRepository = ProductRepository.getInstance(requireContext());

        reviewsRecycler = view.findViewById(R.id.reviewsRecycler);
        reviewsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        setupAdapterAndSwipe();
        loadDynamicPendingItems();
        loadReviews();
    }

    /**
     * Loads pending reviews dynamically from delivered orders in OrderManager.
     * If no delivered items are pending review, pendingItems remains empty and
     * the "Waiting for your review" section is hidden automatically.
     */
    private void loadDynamicPendingItems() {
        pendingItems.clear();
        List<OrderManager.Order> completedOrders = OrderManager.getInstance().getCompletedOrders();
        if (completedOrders != null) {
            for (OrderManager.Order order : completedOrders) {
                if (order.status == OrderManager.OrderStatus.DELIVERED && order.items != null) {
                    int itemIdx = 0;
                    for (OrderManager.OrderItem item : order.items) {
                        itemIdx++;
                        pendingItems.add(new ReviewListItem.Pending(
                                order.orderId + "_" + itemIdx,
                                "p_" + item.productName.toLowerCase().replaceAll("[^a-z0-9]", "_"),
                                item.productName,
                                order.date != null ? order.date : "Recently",
                                item.productImageUrl
                        ));
                    }
                }
            }
        }
    }

    private void setupAdapterAndSwipe() {
        adapter = new MyReviewsAdapter(
                this::onWriteReview,
                this::onEditReview
        );
        reviewsRecycler.setAdapter(adapter);

        ItemTouchHelper helper = new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(), adapter, pos -> {
            List<ReviewListItem> currentList = new ArrayList<>(adapter.getCurrentList());
            if (pos < 0 || pos >= currentList.size()) return;

            ReviewListItem item = currentList.get(pos);
            if (!(item instanceof ReviewListItem.ReviewItem)) return;

            ReviewListItem.ReviewItem removed = (ReviewListItem.ReviewItem) item;
            reviewItems.remove(removed);
            rebuildAndSubmitList();

            Snackbar.make(reviewsRecycler, "Review deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> {
                        if (!reviewItems.contains(removed)) {
                            reviewItems.add(Math.min(pos, reviewItems.size()), removed);
                            rebuildAndSubmitList();
                        }
                    })
                    .addCallback(new Snackbar.Callback() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event != DISMISS_EVENT_ACTION) {
                                try {
                                    long reviewId = Long.parseLong(removed.id);
                                    reviewRepository.deleteReview(reviewId, removed.productId, new ReviewRepository.DeleteReviewCallback() {
                                        @Override
                                        public void onReviewDeleted() {
                                            Log.d(TAG, "Review deleted on backend: " + removed.id);
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.e(TAG, "Error deleting review: " + error);
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.d(TAG, "Local item removed: " + removed.id);
                                }
                            }
                        }
                    })
                    .show();
        }));
        helper.attachToRecyclerView(reviewsRecycler);
    }

    private void onWriteReview(ReviewListItem.Pending pendingItem, float rating) {
        ReviewSheet.Data sd = new ReviewSheet.Data();
        sd.isEdit = false;
        sd.productName = pendingItem.name;
        sd.productImageUrl = pendingItem.image;
        sd.subText = "Delivered " + pendingItem.delivered;
        sd.rating = (int) rating;

        ReviewSheet.show(requireActivity(), sd, (sheet, rRating, title, comment, tags) -> {
            sheet.setLoading(true);
            String finalComment = ReviewSheet.commentWithTags(comment, tags);
            ReviewCreateRequest request = new ReviewCreateRequest(
                    pendingItem.productId, 1L, rRating, title, finalComment);

            reviewRepository.createReview(request, new ReviewRepository.ReviewCreateCallback() {
                @Override
                public void onReviewCreated(Review createdReview) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        sheet.dismiss();
                        Toast.makeText(requireContext(), "Review submitted!", Toast.LENGTH_SHORT).show();
                        pendingItems.remove(pendingItem);
                        reviewItems.add(0, new ReviewListItem.ReviewItem(
                                String.valueOf(createdReview.getId()),
                                pendingItem.productId,
                                pendingItem.name,
                                rRating,
                                finalComment,
                                pendingItem.image
                        ));
                        rebuildAndSubmitList();
                    });
                }

                @Override
                public void onError(String error) {
                    if (!isAdded()) return;
                    requireActivity().runOnUiThread(() -> {
                        sheet.setLoading(false);
                        Toast.makeText(requireContext(), "Submitted review locally", Toast.LENGTH_SHORT).show();
                        sheet.dismiss();
                        pendingItems.remove(pendingItem);
                        reviewItems.add(0, new ReviewListItem.ReviewItem(
                                "r_" + System.currentTimeMillis(),
                                pendingItem.productId,
                                pendingItem.name,
                                rRating,
                                finalComment,
                                pendingItem.image
                        ));
                        rebuildAndSubmitList();
                    });
                }
            });
        });
    }

    private void onEditReview(ReviewListItem.ReviewItem reviewItem) {
        ReviewSheet.Data sd = new ReviewSheet.Data();
        sd.isEdit = true;
        sd.productName = reviewItem.name;
        sd.productImageUrl = reviewItem.image;
        sd.rating = (int) reviewItem.rating;
        sd.comment = reviewItem.comment;

        ReviewSheet.show(requireActivity(), sd, (sheet, rRating, title, comment, tags) -> {
            sheet.setLoading(true);
            String finalComment = ReviewSheet.commentWithTags(comment, tags);

            try {
                long reviewId = Long.parseLong(reviewItem.id);
                ReviewCreateRequest request = new ReviewCreateRequest(
                        reviewItem.productId, 1L, rRating, title, finalComment);
                reviewRepository.updateReview(reviewId, request, new ReviewRepository.ReviewCreateCallback() {
                    @Override
                    public void onReviewCreated(Review updatedReview) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            sheet.dismiss();
                            Toast.makeText(requireContext(), "Review updated!", Toast.LENGTH_SHORT).show();
                            updateLocalReview(reviewItem, rRating, finalComment);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            sheet.dismiss();
                            Toast.makeText(requireContext(), "Updated review locally", Toast.LENGTH_SHORT).show();
                            updateLocalReview(reviewItem, rRating, finalComment);
                        });
                    }
                });
            } catch (Exception e) {
                sheet.dismiss();
                Toast.makeText(requireContext(), "Updated review locally", Toast.LENGTH_SHORT).show();
                updateLocalReview(reviewItem, rRating, finalComment);
            }
        });
    }

    private void updateLocalReview(ReviewListItem.ReviewItem oldItem, float newRating, String newComment) {
        int idx = reviewItems.indexOf(oldItem);
        if (idx >= 0) {
            reviewItems.set(idx, new ReviewListItem.ReviewItem(
                    oldItem.id, oldItem.productId, oldItem.name, newRating, newComment, oldItem.image
            ));
            rebuildAndSubmitList();
        }
    }

    private void loadReviews() {
        reviewRepository.getMyReviews(1, 20, this);
    }

    @Override
    public void onReviewsLoaded(List<Review> loadedReviews) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            reviewItems.clear();
            if (loadedReviews != null && !loadedReviews.isEmpty()) {
                for (Review r : loadedReviews) {
                    String productId = r.getProductId() != null ? r.getProductId() : "prod_" + r.getId();
                    String cachedImage = ProductImageCacheManager.getCachedImageUrl(requireContext(), productId);
                    String productName = (r.getTitle() != null && !r.getTitle().trim().isEmpty()) ? r.getTitle() : "Product #" + productId;

                    ReviewListItem.ReviewItem item = new ReviewListItem.ReviewItem(
                            String.valueOf(r.getId()),
                            productId,
                            productName,
                            r.getRating(),
                            r.getComment() != null ? r.getComment() : "",
                            cachedImage
                    );
                    reviewItems.add(item);

                    // Dynamically fetch product details and image thumbnail if missing
                    fetchProductDetailsForReview(productId);
                }
            }
            rebuildAndSubmitList();
        });
    }

    private void fetchProductDetailsForReview(String productId) {
        if (productId == null || productId.startsWith("prod_")) return;
        productRepository.fetchProductDetail(productId, new ProductRepository.SingleProductCallback() {
            @Override
            public void onSuccess(ProductDetail detail) {
                if (!isAdded() || detail == null) return;
                requireActivity().runOnUiThread(() -> {
                    String name = detail.getName();
                    String thumb = detail.getThumbnail();
                    if (thumb != null && !thumb.trim().isEmpty()) {
                        ProductImageCacheManager.cacheImageUrl(requireContext(), productId, thumb);
                    }
                    updateReviewProductInfo(productId, name, thumb);
                });
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, "Error fetching product detail for ID: " + productId + " -> " + error);
            }
        });
    }

    private void updateReviewProductInfo(String productId, String name, String imageUrl) {
        boolean updated = false;
        for (int i = 0; i < reviewItems.size(); i++) {
            ReviewListItem.ReviewItem item = reviewItems.get(i);
            if (productId.equals(item.productId)) {
                String newName = (name != null && !name.trim().isEmpty()) ? name : item.name;
                String newImg = (imageUrl != null && !imageUrl.trim().isEmpty()) ? imageUrl : item.image;
                reviewItems.set(i, new ReviewListItem.ReviewItem(
                        item.id, item.productId, newName, item.rating, item.comment, newImg
                ));
                updated = true;
            }
        }
        if (updated) {
            rebuildAndSubmitList();
        }
    }

    @Override
    public void onError(String error) {
        if (!isAdded()) return;
        requireActivity().runOnUiThread(() -> {
            Log.e(TAG, "Error loading reviews: " + error);
            rebuildAndSubmitList();
        });
    }

    private void rebuildAndSubmitList() {
        List<ReviewListItem> fullList = new ArrayList<>();

        if (!pendingItems.isEmpty()) {
            fullList.add(new ReviewListItem.Header("Waiting for your review (" + pendingItems.size() + ")"));
            fullList.addAll(pendingItems);
        }

        fullList.add(new ReviewListItem.Header("Your reviews (" + reviewItems.size() + ")"));
        fullList.addAll(reviewItems);

        adapter.submitList(fullList);
    }
}
