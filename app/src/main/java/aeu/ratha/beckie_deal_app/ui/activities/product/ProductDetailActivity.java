package aeu.ratha.beckie_deal_app.ui.activities.product;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.ProductDetail;
import aeu.ratha.beckie_deal_app.data.models.ProductVariant;
import aeu.ratha.beckie_deal_app.data.repository.CartRepository;
import aeu.ratha.beckie_deal_app.data.repository.ProductRepository;
import aeu.ratha.beckie_deal_app.data.repository.WishlistRepository;
import aeu.ratha.beckie_deal_app.data.models.CartItemResponse;
import aeu.ratha.beckie_deal_app.data.models.WishlistItemResponse;
import aeu.ratha.beckie_deal_app.ui.activities.cart.CartActivity;
import aeu.ratha.beckie_deal_app.ui.activities.cart.PayoutActivity;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.ChatIntegration;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Product detail screen.
 *
 * Data flow:
 *  1. Intent extras provide basic info (id, name, price, image) for immediate display.
 *  2. fetchProductDetail() calls GET /products/{uuid} to load full data:
 *       - Real description / short_description / meta_description
 *       - Actual gallery images from images[]
 *       - Variants (RAM / storage / colour chips)
 *       - Category and brand
 *       - Stock, dimensions, SKU, barcode
 */
public class ProductDetailActivity extends BaseActivity {

    private static final String TAG = "ProductDetailActivity";

    // ── State ─────────────────────────────────────────────────────────────────
    private boolean isFavorite = false;

    /** Basic product info received from the calling activity via Intent. */
    private MainActivity.Product currentProduct;

    /** Full detail loaded from API — null until the fetch completes. */
    private ProductDetail productDetail;

    /** Selected variant index (-1 = no variant / base product). */
    private int selectedVariantIndex = -1;

    private ProductRepository productRepository;
    private WishlistRepository wishlistRepository;
    private CartRepository cartRepository;

    // ── Gallery ───────────────────────────────────────────────────────────────
    private int currentImageIndex = 0;
    private List<String> imageUrls = new ArrayList<>();
    private ImageView mainImage;
    private View[] dots = new View[4];
    private MaterialCardView[] thumbCards = new MaterialCardView[4];
    private ImageView[] thumbImages = new ImageView[4];

    // ── Quantity ──────────────────────────────────────────────────────────────
    private int selectedQuantity = 1;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productRepository = ProductRepository.getInstance(this);
        wishlistRepository = new WishlistRepository(this);
        cartRepository     = new CartRepository(this);

        setupTopBar();
        displayIntentData();       // Show basic data immediately from Intent
        fetchProductDetail();      // Then fetch full data from API
        setupActions();
        setupBottomNavigation();
        setupEntranceAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Setup scroll-to-top button
        androidx.core.widget.NestedScrollView detailScrollView = findViewById(R.id.detailScrollView);
        android.widget.FrameLayout scrollToTopButton = findViewById(R.id.scrollToTopButton);
        aeu.ratha.beckie_deal_app.utils.ScrollToTopHelper.attachToNestedScrollView(detailScrollView, scrollToTopButton);
        
        BottomNavHelper.updateCartBadge(this);
    }

    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
        UiAnimations.fadeSlideUp(findViewById(R.id.galleryContainer), 60);
        UiAnimations.fadeSlideUp(findViewById(R.id.thumbnailRow), 110);
        UiAnimations.fadeSlideUp(findViewById(R.id.productInfoBlock), 150);
        UiAnimations.fadeSlideUp(findViewById(R.id.bottomActionLayout), 100);
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private void setupTopBar() {
        ImageView back = findViewById(R.id.backButton);
        if (back != null) back.setOnClickListener(v -> finish());

        View notificationContainer = findViewById(R.id.notificationContainer);
        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        UiAnimations.pressBounce(back, notificationContainer);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 1 — show data from Intent immediately so the screen isn't blank
    // ─────────────────────────────────────────────────────────────────────────

    private void displayIntentData() {
        String productId = getIntent().getStringExtra("product_id");
        String name      = getIntent().getStringExtra("product_name");
        String price     = getIntent().getStringExtra("product_price");
        String imageUrl  = getIntent().getStringExtra("product_image");
        String rating    = getIntent().getStringExtra("product_rating");
        String sku       = getIntent().getStringExtra("product_sku");

        if (productId != null && !productId.isEmpty()) {
            loadProductReviews(productId);
        }

        if (name == null)   name   = "Product";
        if (rating == null) rating = "4.5";

        currentProduct = new MainActivity.Product(name, price, rating, false, imageUrl);
        currentProduct.id  = productId;
        currentProduct.sku = sku;

        // Set name and price immediately
        setTextSafe(R.id.detailProductName,  name);
        setTextSafe(R.id.detailProductPrice, price != null ? price : "");
        setTextSafe(R.id.detailProductRating, rating);
        setTextSafe(R.id.reviewCount, "(loading...)");

        // Set up gallery with the thumbnail from Intent while full data loads
        List<String> imgs = new ArrayList<>();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imgs.add(imageUrl);
            imgs.add(imageUrl);
            imgs.add(imageUrl);
            imgs.add(imageUrl);
        } else {
            String fb = "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500&h=500&fit=crop";
            for (int i = 0; i < 4; i++) imgs.add(fb);
        }
        setupGallery(imgs);
    }

    private void loadProductReviews(String productId) {
        if (productId != null && !productId.isEmpty()) {
            try {
                if (getSupportFragmentManager().findFragmentById(R.id.productReviewsContainer) == null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.productReviewsContainer, aeu.ratha.beckie_deal_app.ui.fragments.ProductReviewsFragment.newInstance(productId))
                            .commit();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to load ProductReviewsFragment", e);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Step 2 — fetch full product detail from API
    // ─────────────────────────────────────────────────────────────────────────

    private void fetchProductDetail() {
        String productId = currentProduct != null ? currentProduct.id : null;
        if (productId == null || productId.isEmpty()) {
            Log.w(TAG, "No product ID — cannot fetch detail");
            showStaticFallback();
            return;
        }

        // Check if detail already cached
        ProductDetail cached = productRepository.getCachedDetail(productId);
        if (cached != null) {
            populateFromDetail(cached);
            return;
        }

        Log.d(TAG, "Fetching product detail for " + productId);
        productRepository.fetchProductDetail(productId, new ProductRepository.SingleProductCallback() {
            @Override
            public void onSuccess(ProductDetail detail) {
                runOnUiThread(() -> populateFromDetail(detail));
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Detail fetch failed: " + errorMessage);
                runOnUiThread(() -> showStaticFallback());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Populate UI from full ProductDetail
    // ─────────────────────────────────────────────────────────────────────────

    private void populateFromDetail(ProductDetail detail) {
        this.productDetail = detail;

        if (detail.getId() != null && !detail.getId().isEmpty()) {
            loadProductReviews(detail.getId());
        }

        androidx.fragment.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.productReviewsContainer);
        if (fragment instanceof aeu.ratha.beckie_deal_app.ui.fragments.ProductReviewsFragment) {
            java.util.List<aeu.ratha.beckie_deal_app.data.models.Review> bundledReviews = detail.getReviews();
            aeu.ratha.beckie_deal_app.data.models.ReviewSummary summary = new aeu.ratha.beckie_deal_app.data.models.ReviewSummary();
            summary.setAverageRating(detail.getAverageRatingAsFloat());
            summary.setTotalReviews(bundledReviews != null ? bundledReviews.size() : 0);
            ((aeu.ratha.beckie_deal_app.ui.fragments.ProductReviewsFragment) fragment).setReviews(bundledReviews, summary);
        }

        // ── Name & price ──────────────────────────────────────────────────
        setTextSafe(R.id.detailProductName, detail.getName());
        setTextSafe(R.id.detailProductPrice, detail.getFormattedPrice());

        // Rating: use real average_rating if > 0, else keep the intent value
        float rating = detail.getAverageRatingAsFloat();
        if (rating > 0) {
            setTextSafe(R.id.detailProductRating, String.format("%.1f", rating));
        }
        setTextSafe(R.id.reviewCount,
                "(" + detail.getSalesCount() + " sold · " + detail.getViewsCount() + " views)");

        // ── Stock status ──────────────────────────────────────────────────
        // The stockLabel lives inside a MaterialCardView that has its own green background.
        // We update the parent card's background colour so the pill changes colour properly.
        TextView stockLabel = findViewById(R.id.stockLabel);
        if (stockLabel != null) {
            stockLabel.setText(detail.isInStock() ? "In Stock" : "Out of Stock");
            // Walk up to the parent MaterialCardView and change its background
            android.view.View parent = (android.view.View) stockLabel.getParent(); // LinearLayout
            if (parent != null) {
                android.view.View grandParent = (android.view.View) parent.getParent(); // MaterialCardView
                if (grandParent instanceof com.google.android.material.card.MaterialCardView) {
                    ((com.google.android.material.card.MaterialCardView) grandParent)
                            .setCardBackgroundColor(ContextCompat.getColor(this,
                                    detail.isInStock() ? R.color.primary : R.color.red_error));
                }
            }
        }

        // ── Gallery — real images from API ────────────────────────────────
        List<String> galleryUrls = detail.getGalleryUrls();
        if (!galleryUrls.isEmpty()) {
            // FIXED: Only pad if we have fewer than 4 images
            // If only 1 image, keep 1. If 2 images, keep 2. Don't duplicate!
            if (galleryUrls.size() < 4) {
                // Only pad if needed (less than 4)
                while (galleryUrls.size() < 4) {
                    galleryUrls.add(galleryUrls.get(0));  // Pad with first image only
                }
            }
            setupGallery(galleryUrls);
        }

        // ── Description ───────────────────────────────────────────────────
        setTextSafe(R.id.detailProductDescription, detail.getBestDescription());

        // ── Spec tags (pill chips) ────────────────────────────────────────
        List<String> tags = new ArrayList<>();
        if (detail.isFeatured())  tags.add("Featured");
        if (detail.hasDiscount()) tags.add(detail.getDiscountPercentage() + "% Off");
        if (detail.getSku() != null && !detail.getSku().isEmpty()) tags.add("SKU: " + detail.getSku());
        if (detail.getBrand()    != null) tags.add(detail.getBrand().getName());
        if (detail.getCategory() != null) tags.add(detail.getCategory().getName());
        if (tags.isEmpty()) { tags.add("Premium Quality"); tags.add("Fast Shipping"); }
        setupSpecTags(tags);

        // ── Spec cards (2×2 grid) ─────────────────────────────────────────
        List<SpecCard> specCards = new ArrayList<>();
        // Always show 4 cards in 2x2 grid
        specCards.add(new SpecCard("BRAND", 
                detail.getBrand() != null ? detail.getBrand().getName() : "Unknown", 
                R.drawable.widgets_24));
        specCards.add(new SpecCard("CATEGORY", 
                detail.getCategory() != null ? detail.getCategory().getName() : "Uncategorized", 
                R.drawable.devices_24));
        specCards.add(new SpecCard("STOCK", 
                detail.isInStock() ? "In Stock" : "Out of Stock", 
                R.drawable.sports_esports_24));
        specCards.add(new SpecCard("TYPE", 
                detail.isDigital() ? "Digital" : "Physical", 
                R.drawable.mobile_24));
        setupSpecCards(specCards);

        // ── Spec rows (table) ─────────────────────────────────────────────
        Map<String, String> specRows = new LinkedHashMap<>();
        if (detail.getSku()     != null && !detail.getSku().isEmpty())     specRows.put("SKU",     detail.getSku());
        if (detail.getBarcode() != null && !detail.getBarcode().isEmpty()) specRows.put("Barcode", detail.getBarcode());
        if (detail.getBrand()   != null) specRows.put("Brand",    detail.getBrand().getName());
        if (detail.getCategory()!= null) specRows.put("Category", detail.getCategory().getName());
        if (detail.getWeight()  != null && !detail.getWeight().isEmpty())  specRows.put("Weight",  detail.getWeight() + " kg");
        if (detail.getLength()  != null && !detail.getLength().isEmpty())  specRows.put("Length",  detail.getLength() + " cm");
        if (detail.getWidth()   != null && !detail.getWidth().isEmpty())   specRows.put("Width",   detail.getWidth()  + " cm");
        if (detail.getHeight()  != null && !detail.getHeight().isEmpty())  specRows.put("Height",  detail.getHeight() + " cm");
        if (detail.isTrackInventory()) specRows.put("Stock Qty", String.valueOf(detail.getStockQuantity()));
        specRows.put("Condition",    "Brand New");
        specRows.put("Product Type", detail.isDigital() ? "Digital Product" : "Physical Product");
        if (detail.hasDiscount()) {
            specRows.put("Regular Price", "$" + detail.getPrice());
            specRows.put("Sale Price",    "$" + detail.getSalePrice());
            specRows.put("Discount",      detail.getDiscountPercentage() + "% Off");
        }
        if (detail.getMetaTitle() != null && !detail.getMetaTitle().isEmpty())
            specRows.put("Meta Title", detail.getMetaTitle());
        specRows.put("Warranty",      "1 Year Manufacturer Warranty");
        specRows.put("Shipping",      detail.isDigital() ? "Instant Delivery" : "Free shipping on orders over $50");
        specRows.put("Return Policy", "30-day money back guarantee");
        setupSpecRows(specRows);

        // ── Variants ──────────────────────────────────────────────────────
        setupVariants(detail.getVariants());

        Log.d(TAG, "✓ UI populated from API detail for: " + detail.getName());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Fallback when API call fails
    // ─────────────────────────────────────────────────────────────────────────

    private void showStaticFallback() {
        String name = currentProduct != null ? currentProduct.name : "Product";
        String sku  = currentProduct != null ? currentProduct.sku  : "";

        setTextSafe(R.id.reviewCount, "(reviews unavailable)");

        String description = "Quality product with excellent features and reliable performance.";
        if (sku != null && !sku.isEmpty()) description += " SKU: " + sku + ".";
        setTextSafe(R.id.detailProductDescription, description);

        List<String> tags = new ArrayList<>();
        tags.add("Premium Quality"); tags.add("Fast Shipping");
        if (sku != null && !sku.isEmpty()) tags.add("SKU: " + sku);
        tags.add("1 Year Warranty");
        setupSpecTags(tags);

        List<SpecCard> cards = new ArrayList<>();
        cards.add(new SpecCard("QUALITY",  "Premium",  R.drawable.widgets_24));
        cards.add(new SpecCard("WARRANTY", "1 Year",   R.drawable.devices_24));
        cards.add(new SpecCard("SHIPPING", "Fast",     R.drawable.sports_esports_24));
        cards.add(new SpecCard("SUPPORT",  "24/7",     R.drawable.mobile_24));
        setupSpecCards(cards);

        Map<String, String> rows = new LinkedHashMap<>();
        if (sku != null && !sku.isEmpty()) rows.put("SKU", sku);
        rows.put("Condition",    "Brand New");
        rows.put("Warranty",     "1 Year Manufacturer Warranty");
        rows.put("Shipping",     "Free shipping on orders over $50");
        rows.put("Return Policy","30-day money back guarantee");
        setupSpecRows(rows);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Variants
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Renders a horizontal row of variant chip buttons (e.g. "16GB/1TB/Gray").
     * Tapping a variant updates the displayed price.
     */
    private void setupVariants(List<ProductVariant> variants) {
        LinearLayout variantRow = findViewById(R.id.variantChipsRow);
        android.widget.HorizontalScrollView variantScroll = findViewById(R.id.variantScrollView);

        if (variantRow == null || variants == null || variants.isEmpty()) {
            if (variantScroll != null) variantScroll.setVisibility(View.GONE);
            return;
        }

        variantScroll.setVisibility(View.VISIBLE);
        variantRow.removeAllViews();

        // Sort: default first, then by sort_order
        List<ProductVariant> sorted = new ArrayList<>(variants);
        sorted.sort((a, b) -> {
            if (a.isDefault() && !b.isDefault()) return -1;
            if (!a.isDefault() && b.isDefault())  return  1;
            return Integer.compare(a.getSortOrder(), b.getSortOrder());
        });

        for (int i = 0; i < sorted.size(); i++) {
            final int idx = i;
            final ProductVariant variant = sorted.get(i);

            TextView chip = new TextView(this);
            chip.setText(variant.getAttributeSummary());
            ScreenUtils.applyTextSize(chip, R.dimen.text_size_caption);

            int hPad = dpToPx(10), vPad = dpToPx(6);
            chip.setPadding(hPad, vPad, hPad, vPad);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dpToPx(8));
            chip.setLayoutParams(lp);

            boolean isSelected = (selectedVariantIndex == -1 && variant.isDefault())
                    || selectedVariantIndex == i;
            applyVariantChipStyle(chip, isSelected);

            chip.setOnClickListener(v -> {
                selectedVariantIndex = idx;
                // Refresh all chip styles
                for (int j = 0; j < variantRow.getChildCount(); j++) {
                    View child = variantRow.getChildAt(j);
                    if (child instanceof TextView) {
                        applyVariantChipStyle((TextView) child, j == idx);
                    }
                }
                // Update displayed price
                setTextSafe(R.id.detailProductPrice,
                        "$" + String.format("%.2f", variant.getPriceAsDouble()));
                Log.d(TAG, "Variant selected: " + variant.getName());
            });

            variantRow.addView(chip);
        }

        // Select default variant's price on load
        for (ProductVariant v : sorted) {
            if (v.isDefault()) {
                setTextSafe(R.id.detailProductPrice,
                        "$" + String.format("%.2f", v.getPriceAsDouble()));
                break;
            }
        }
    }

    private void applyVariantChipStyle(TextView chip, boolean selected) {
        if (selected) {
            chip.setTextColor(Color.WHITE);
            chip.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_rect));
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.primary)));
        } else {
            chip.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            chip.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_rect));
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    Color.parseColor("#F0F0F0")));
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Gallery
    // ─────────────────────────────────────────────────────────────────────────

    private void setupGallery(List<String> urls) {
        imageUrls = urls;
        mainImage = findViewById(R.id.detailProductImage);

        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);

        thumbCards[0]  = findViewById(R.id.thumb1);  thumbImages[0] = findViewById(R.id.thumbImg1);
        thumbCards[1]  = findViewById(R.id.thumb2);  thumbImages[1] = findViewById(R.id.thumbImg2);
        thumbCards[2]  = findViewById(R.id.thumb3);  thumbImages[2] = findViewById(R.id.thumbImg3);
        thumbCards[3]  = findViewById(R.id.thumb4);  thumbImages[3] = findViewById(R.id.thumbImg4);

        // FIXED: Only show thumbnails for actual images we have
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            
            if (i < urls.size()) {
                // We have an image for this slot
                String url = urls.get(i);
                if (thumbImages[i] != null) {
                    Glide.with(this).load(url).centerCrop()
                         .placeholder(R.drawable.placeholder_image).into(thumbImages[i]);
                }
                if (thumbCards[i] != null) {
                    thumbCards[i].setOnClickListener(v -> selectImage(idx));
                    thumbCards[i].setVisibility(View.VISIBLE);  // Show thumbnail
                }
                if (dots[i] != null) {
                    dots[i].setVisibility(View.VISIBLE);  // Show dot
                }
            } else {
                // No image for this slot - hide it
                if (thumbCards[i] != null) thumbCards[i].setVisibility(View.GONE);
                if (dots[i] != null) dots[i].setVisibility(View.GONE);
            }
        }

        View prevBtn = findViewById(R.id.galleryPrev);
        View nextBtn = findViewById(R.id.galleryNext);
        
        // Only show prev/next buttons if we have multiple images
        if (urls.size() <= 1) {
            if (prevBtn != null) prevBtn.setVisibility(View.GONE);
            if (nextBtn != null) nextBtn.setVisibility(View.GONE);
        } else {
            if (prevBtn != null) {
                prevBtn.setVisibility(View.VISIBLE);
                prevBtn.setOnClickListener(v ->
                        selectImage((currentImageIndex - 1 + imageUrls.size()) % imageUrls.size()));
            }
            if (nextBtn != null) {
                nextBtn.setVisibility(View.VISIBLE);
                nextBtn.setOnClickListener(v ->
                        selectImage((currentImageIndex + 1) % imageUrls.size()));
            }
        }
        
        UiAnimations.pressBounce(prevBtn, nextBtn);
        UiAnimations.pressBounce(thumbCards[0], thumbCards[1], thumbCards[2], thumbCards[3]);

        View.OnClickListener openFs = v -> {
            Intent intent = new Intent(this, ImageFullscreenActivity.class);
            intent.putStringArrayListExtra("image_urls", new ArrayList<>(imageUrls));
            intent.putExtra("start_index", currentImageIndex);
            startActivity(intent);
        };
        View fsBtn = findViewById(R.id.galleryFullscreen);
        if (mainImage != null) mainImage.setOnClickListener(openFs);
        if (fsBtn     != null) fsBtn.setOnClickListener(openFs);

        selectImage(0);
    }

    private void selectImage(int index) {
        if (imageUrls.isEmpty()) return;
        currentImageIndex = index % imageUrls.size();  // Ensure index is within bounds
        String url = imageUrls.get(currentImageIndex);

        if (mainImage != null) {
            Glide.with(this).load(url)
                 .placeholder(R.drawable.placeholder_image).into(mainImage);
        }

        // FIXED: Only update dots/thumbnails for images that actually exist
        for (int i = 0; i < 4; i++) {
            if (i < imageUrls.size()) {
                // Show dot/thumbnail only if we have this image
                if (dots[i] != null) {
                    dots[i].setVisibility(View.VISIBLE);
                    android.view.ViewGroup.LayoutParams lp = dots[i].getLayoutParams();
                    if (i == currentImageIndex) {
                        lp.width = dpToPx(20);
                        dots[i].setBackgroundResource(R.drawable.dot_active);
                    } else {
                        lp.width = dpToPx(6);
                        dots[i].setBackgroundResource(R.drawable.dot_inactive);
                    }
                    dots[i].setLayoutParams(lp);
                }
                
                if (thumbCards[i] != null) {
                    thumbCards[i].setVisibility(View.VISIBLE);
                    thumbCards[i].setStrokeWidth(i == currentImageIndex ? dpToPx(2) : dpToPx(1));
                    thumbCards[i].setStrokeColor(i == currentImageIndex
                            ? Color.parseColor("#F97316") : Color.parseColor("#EEEEEE"));
                }
            } else {
                // Hide dot/thumbnail if we don't have this image
                if (dots[i] != null) dots[i].setVisibility(View.GONE);
                if (thumbCards[i] != null) thumbCards[i].setVisibility(View.GONE);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Spec tags (pill chips)
    // ─────────────────────────────────────────────────────────────────────────

    private void setupSpecTags(List<String> tags) {
        LinearLayout row = findViewById(R.id.specTagsRow);
        if (row == null) return;
        row.removeAllViews();

        LinearLayout line1 = newHorizontalRow(6);
        LinearLayout line2 = newHorizontalRow(0);
        row.addView(line1);
        row.addView(line2);

        for (int i = 0; i < tags.size(); i++) {
            TextView chip = new TextView(this);
            chip.setText(tags.get(i));
            ScreenUtils.applyTextSize(chip, R.dimen.text_size_caption);
            chip.setTextColor(Color.parseColor("#F97316"));
            chip.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_rect));
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    Color.parseColor("#FFF0E6")));
            int h = dpToPx(10), v = dpToPx(5);
            chip.setPadding(h, v, h, v);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            cp.setMarginEnd(dpToPx(8));
            chip.setLayoutParams(cp);
            (i < 2 ? line1 : line2).addView(chip);
        }
    }

    private LinearLayout newHorizontalRow(int bottomMarginDp) {
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dpToPx(bottomMarginDp);
        ll.setLayoutParams(lp);
        return ll;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Spec cards (2×2 grid)
    // ─────────────────────────────────────────────────────────────────────────

    private void setupSpecCards(List<SpecCard> cards) {
        android.widget.GridLayout grid = findViewById(R.id.specCardsGrid);
        if (grid == null) {
            Log.e(TAG, "specCardsGrid not found!");
            return;
        }
        grid.removeAllViews();
        
        Log.d(TAG, "Setting up " + cards.size() + " spec cards");
        
        for (int i = 0; i < Math.min(cards.size(), 4); i++) {
            SpecCard sc = cards.get(i);
            View v = LayoutInflater.from(this).inflate(R.layout.item_spec_card, grid, false);
            
            // Create GridLayout.LayoutParams
            android.widget.GridLayout.LayoutParams glp = new android.widget.GridLayout.LayoutParams();
            glp.rowSpec = android.widget.GridLayout.spec(i / 2, 1f);
            glp.columnSpec = android.widget.GridLayout.spec(i % 2, 1f);
            glp.width = 0;
            glp.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
            glp.setMargins(6, 6, 6, 6);
            v.setLayoutParams(glp);
            
            TextView label = v.findViewById(R.id.specCardLabel);
            TextView value = v.findViewById(R.id.specCardValue);
            ImageView icon = v.findViewById(R.id.specCardIcon);
            if (label != null) label.setText(sc.label);
            if (value != null) value.setText(sc.value);
            if (icon  != null) icon.setImageResource(sc.iconRes);
            grid.addView(v);
            
            Log.d(TAG, "Added spec card " + (i+1) + ": " + sc.label + " - " + sc.value);
        }
        
        Log.d(TAG, "Total spec cards added: " + grid.getChildCount());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Spec rows (table)
    // ─────────────────────────────────────────────────────────────────────────

    private void setupSpecRows(Map<String, String> rows) {
        LinearLayout container = findViewById(R.id.specRowsContainer);
        if (container == null) return;
        container.removeAllViews();

        boolean first = true;
        for (Map.Entry<String, String> entry : rows.entrySet()) {
            if (!first) {
                View divider = new View(this);
                LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
                dp.setMarginStart(dpToPx(16)); dp.setMarginEnd(dpToPx(16));
                divider.setLayoutParams(dp);
                divider.setBackgroundColor(Color.parseColor("#F0F0F0"));
                container.addView(divider);
            }
            View r = LayoutInflater.from(this).inflate(R.layout.item_spec_row, container, false);
            TextView key = r.findViewById(R.id.specKey);
            TextView val = r.findViewById(R.id.specValue);
            if (key != null) key.setText(entry.getKey());
            if (val != null) val.setText(entry.getValue());
            container.addView(r);
            first = false;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Actions (quantity stepper, Add to Cart, Buy Now, Wishlist)
    // ─────────────────────────────────────────────────────────────────────────

    private void setupActions() {
        // ── Collapsible sections ──────────────────────────────────────────────
        setupCollapsibleSections();

        // ── Quantity stepper ──────────────────────────────────────────────
        TextView quantityValue = findViewById(R.id.quantityValue);
        View decrement = findViewById(R.id.quantityDecrement);
        View increment = findViewById(R.id.quantityIncrement);

        if (quantityValue != null) quantityValue.setText(String.valueOf(selectedQuantity));

        if (decrement != null) decrement.setOnClickListener(v -> {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                if (quantityValue != null) quantityValue.setText(String.valueOf(selectedQuantity));
            }
        });
        if (increment != null) increment.setOnClickListener(v -> {
            selectedQuantity++;
            if (quantityValue != null) quantityValue.setText(String.valueOf(selectedQuantity));
        });
        UiAnimations.pressBounce(decrement, increment);

        // ── Add to Cart ───────────────────────────────────────────────────
        View addToCart = findViewById(R.id.addToCartButton);
        if (addToCart != null) addToCart.setOnClickListener(v -> {
            if (!AuthHelper.requireAuthForCart(this)) return;
            String pid = resolveProductId();
            if (pid == null) {
                Toast.makeText(this, "Product info missing. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Instant optimistic feedback (0ms latency)
            String msg = selectedQuantity == 1 ? "Added to cart!"
                    : selectedQuantity + "× added to cart!";
            View root = findViewById(android.R.id.content);
            if (root == null) root = v;
            try {
                Snackbar snackbar = Snackbar.make(root, msg, Snackbar.LENGTH_LONG)
                        .setAction("View Cart ♥", view -> startActivity(
                                new Intent(ProductDetailActivity.this, CartActivity.class)))
                        .setActionTextColor(getResources().getColor(R.color.primary, null));
                snackbar.show();
            } catch (Exception e) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
            BottomNavHelper.updateCartBadge(ProductDetailActivity.this);

            cartRepository.addToCart(pid, selectedQuantity, new CartRepository.CartItemCallback() {
                @Override public void onSuccess(CartItemResponse response) {
                    runOnUiThread(() -> BottomNavHelper.updateCartBadge(ProductDetailActivity.this));
                }
                @Override public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this,
                            "Failed to update cart on server", Toast.LENGTH_SHORT).show());
                }
            });
        });

        // ── Buy Now ───────────────────────────────────────────────────────
        View buyNow = findViewById(R.id.buyNowButton);
        if (buyNow != null) buyNow.setOnClickListener(v -> {
            if (!AuthHelper.requireAuthForPurchase(this)) return;
            String pid = resolveProductId();
            if (pid == null) {
                Toast.makeText(this, "Product info missing. Please try again.", Toast.LENGTH_SHORT).show();
                return;
            }
            cartRepository.addToCart(pid, selectedQuantity, new CartRepository.CartItemCallback() {
                @Override public void onSuccess(CartItemResponse response) {
                    runOnUiThread(() -> {
                        Intent intent = new Intent(ProductDetailActivity.this, PayoutActivity.class);
                        intent.putExtra("buy_now_mode", true);
                        startActivity(intent);
                    });
                }
                @Override public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(ProductDetailActivity.this,
                            "Failed to proceed to checkout", Toast.LENGTH_SHORT).show());
                }
            });
        });

        UiAnimations.pressBounce(addToCart, buyNow);

        // ── Chat Buttons ──────────────────────────────────────────────────────
        setupChatButtons();
    }

    /**
     * Setup Facebook, Facebook Messenger, and Telegram chat button listeners
     */
    private void setupChatButtons() {
        com.google.android.material.button.MaterialButton facebookButton = 
            findViewById(R.id.facebookButton);
        com.google.android.material.button.MaterialButton messengerButton = 
            findViewById(R.id.messengerButton);
        com.google.android.material.button.MaterialButton telegramButton = 
            findViewById(R.id.telegramButton);

        if (facebookButton != null) {
            facebookButton.setOnClickListener(v -> {
                ChatIntegration.openFacebook(ProductDetailActivity.this);
            });
        }

        if (messengerButton != null) {
            messengerButton.setOnClickListener(v -> {
                String productName = currentProduct != null ? currentProduct.name : "this product";
                ChatIntegration.openFacebookMessenger(ProductDetailActivity.this, productName);
            });
        }

        if (telegramButton != null) {
            telegramButton.setOnClickListener(v -> {
                String productName = currentProduct != null ? currentProduct.name : "this product";
                ChatIntegration.openTelegram(ProductDetailActivity.this, productName);
            });
        }

        // Add press animation
        UiAnimations.pressBounce(facebookButton, messengerButton, telegramButton);
    }

    /** Returns the product UUID for cart/checkout operations. */
    private String resolveProductId() {
        if (productDetail != null && productDetail.getId() != null) return productDetail.getId();
        if (currentProduct != null && currentProduct.id != null && !currentProduct.id.isEmpty())
            return currentProduct.id;
        return null;
    }

    // ── Bottom nav ────────────────────────────────────────────────────────────

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.HOME);
    }

    // ── Collapsible Sections ───────────────────────────────────────────────────

    private void setupCollapsibleSections() {
        // Description section
        View descHeader = findViewById(R.id.accDescHeader);
        View descBody = findViewById(R.id.accDescBody);
        View descIcon = findViewById(R.id.accDescIcon);

        if (descHeader != null && descBody != null && descIcon != null) {
            descHeader.setOnClickListener(v -> {
                boolean isVisible = descBody.getVisibility() == View.VISIBLE;
                toggleCollapsibleSection(descBody, descIcon, isVisible);
                Log.d(TAG, "Description section toggled: " + (!isVisible ? "expanded" : "collapsed"));
            });
            // Start expanded
            descBody.setVisibility(View.VISIBLE);
            if (descIcon instanceof TextView) {
                ((TextView) descIcon).setText("−");
            }
        }

        // Technical Specifications section
        View specHeader = findViewById(R.id.accSpecHeader);
        View specBody = findViewById(R.id.accSpecBody);
        View specIcon = findViewById(R.id.accSpecIcon);

        if (specHeader != null && specBody != null && specIcon != null) {
            specHeader.setOnClickListener(v -> {
                boolean isVisible = specBody.getVisibility() == View.VISIBLE;
                toggleCollapsibleSection(specBody, specIcon, isVisible);
                Log.d(TAG, "Specs section toggled: " + (!isVisible ? "expanded" : "collapsed"));
            });
            // Start expanded
            specBody.setVisibility(View.VISIBLE);
            if (specIcon instanceof TextView) {
                ((TextView) specIcon).setText("−");
            }
        }
    }

    /**
     * Toggles section visibility with smooth expand/collapse animation
     * @param contentView The content view to animate
     * @param iconView The icon to rotate
     * @param isCurrentlyVisible Whether the section is currently visible
     */
    private void toggleCollapsibleSection(View contentView, View iconView, boolean isCurrentlyVisible) {
        if (isCurrentlyVisible) {
            // Collapse with animation
            collapseSection(contentView, iconView);
        } else {
            // Expand with animation
            expandSection(contentView, iconView);
        }
    }

    private void expandSection(View contentView, View iconView) {
        contentView.measure(
                View.MeasureSpec.makeMeasureSpec(contentView.getWidth(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        int targetHeight = contentView.getMeasuredHeight();
        
        // Set initial state
        contentView.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams lp = contentView.getLayoutParams();
        lp.height = 0;
        contentView.setLayoutParams(lp);

        // Animate height from 0 to target
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(0, targetHeight);
        animator.setDuration(350);
        animator.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = contentView.getLayoutParams();
            params.height = animatedValue;
            contentView.setLayoutParams(params);
        });
        animator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override public void onAnimationStart(android.animation.Animator animation) {}
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                ViewGroup.LayoutParams params = contentView.getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                contentView.setLayoutParams(params);
            }
            @Override public void onAnimationCancel(android.animation.Animator animation) {}
            @Override public void onAnimationRepeat(android.animation.Animator animation) {}
        });

        // Rotate/fade icon
        animateIcon(iconView, false);
        animator.start();
    }

    private void collapseSection(View contentView, View iconView) {
        int initialHeight = contentView.getMeasuredHeight();

        // Animate height from current to 0
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(initialHeight, 0);
        animator.setDuration(350);
        animator.setInterpolator(new android.view.animation.AccelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams params = contentView.getLayoutParams();
            params.height = animatedValue;
            contentView.setLayoutParams(params);
        });
        animator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override public void onAnimationStart(android.animation.Animator animation) {}
            @Override public void onAnimationEnd(android.animation.Animator animation) {
                contentView.setVisibility(View.GONE);
            }
            @Override public void onAnimationCancel(android.animation.Animator animation) {}
            @Override public void onAnimationRepeat(android.animation.Animator animation) {}
        });

        // Rotate/fade icon
        animateIcon(iconView, true);
        animator.start();
    }

    private void animateIcon(View iconView, boolean isCollapsing) {
        if (!(iconView instanceof ImageView) && !(iconView instanceof TextView)) {
            return;
        }

        if (iconView instanceof TextView) {
            // For text icon, use alpha fade and text change
            TextView textIcon = (TextView) iconView;
            String newText = isCollapsing ? "+" : "−";
            
            android.animation.ObjectAnimator alphaOut = android.animation.ObjectAnimator.ofFloat(
                    iconView, "alpha", 1f, 0.3f);
            alphaOut.setDuration(150);
            alphaOut.addListener(new android.animation.Animator.AnimatorListener() {
                @Override public void onAnimationStart(android.animation.Animator animation) {}
                @Override public void onAnimationEnd(android.animation.Animator animation) {
                    textIcon.setText(newText);
                    android.animation.ObjectAnimator alphaIn = android.animation.ObjectAnimator.ofFloat(
                            iconView, "alpha", 0.3f, 1f);
                    alphaIn.setDuration(150);
                    alphaIn.start();
                }
                @Override public void onAnimationCancel(android.animation.Animator animation) {}
                @Override public void onAnimationRepeat(android.animation.Animator animation) {}
            });
            alphaOut.start();
        } else {
            // For image icon, use rotation
            float fromRotation = isCollapsing ? 180f : 0f;
            float toRotation = isCollapsing ? 0f : 180f;
            
            android.animation.ObjectAnimator rotationAnimator = android.animation.ObjectAnimator.ofFloat(
                    iconView, "rotation", fromRotation, toRotation);
            rotationAnimator.setDuration(350);
            rotationAnimator.setInterpolator(new android.view.animation.DecelerateInterpolator());
            rotationAnimator.start();
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setTextSafe(int id, String text) {
        TextView tv = findViewById(id);
        if (tv != null && text != null) tv.setText(text);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ── Data classes ──────────────────────────────────────────────────────────

    static class SpecCard {
        String label, value;
        int iconRes;
        SpecCard(String label, String value, int iconRes) {
            this.label = label; this.value = value; this.iconRes = iconRes;
        }
    }
}
