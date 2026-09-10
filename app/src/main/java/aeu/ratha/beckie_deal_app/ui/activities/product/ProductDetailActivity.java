package aeu.ratha.beckie_deal_app.ui.activities.product;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

import aeu.ratha.beckie_deal_app.R;


import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.content.Intent;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.graphics.Color;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.os.Bundle;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.view.LayoutInflater;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.view.View;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.ImageView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.LinearLayout;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.TextView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.CartManager;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.components.ImageFullscreenActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductDetailActivity extends BaseActivity {

    private static final String TAG = "ProductDetailActivity";
    
    private boolean isFavorite = false;
    private ImageView detailFavoriteIcon;
    private MainActivity.Product currentProduct;

    // Gallery state
    private int currentImageIndex = 0;
    private List<String> imageUrls = new ArrayList<>();
    private ImageView mainImage;
    private View[] dots = new View[4];
    private MaterialCardView[] thumbCards = new MaterialCardView[4];
    private ImageView[] thumbImages = new ImageView[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        
        setupTopBar();
        loadProductData();
        setupActions();
        setupBottomNavigation();
        setupEntranceAnimation();
    }

    /** Fades/slides the gallery, thumbnail row, info block and bottom action bar into place. */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
        UiAnimations.fadeSlideUp(findViewById(R.id.galleryContainer), 60);
        UiAnimations.fadeSlideUp(findViewById(R.id.thumbnailRow), 110);
        UiAnimations.fadeSlideUp(findViewById(R.id.productInfoBlock), 150);
        UiAnimations.fadeSlideUp(findViewById(R.id.bottomActionLayout), 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartUI();
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private void setupTopBar() {
        ImageView back = findViewById(R.id.backButton);
        if (back != null) back.setOnClickListener(v -> finish());

        ImageView share = findViewById(R.id.shareIcon);
        if (share != null) share.setOnClickListener(v ->
                Toast.makeText(this, "Link copied!", Toast.LENGTH_SHORT).show());

        View notificationContainer = findViewById(R.id.notificationContainer);
        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        UiAnimations.pressBounce(back, share, notificationContainer);
    }

    private void updateCartUI() {
        BottomNavHelper.updateCartBadge(this);
    }

    // ── Product data ──────────────────────────────────────────────────────────

    private void loadProductData() {
        // Get product data from intent
        int productId = getIntent().getIntExtra("product_id", 0);
        String name     = getIntent().getStringExtra("product_name");
        String price    = getIntent().getStringExtra("product_price");
        String imageUrl = getIntent().getStringExtra("product_image");
        String rating   = getIntent().getStringExtra("product_rating");
        String sku      = getIntent().getStringExtra("product_sku");
        
        if (name == null) name = "Product";
        if (rating == null) rating = "4.9";

        currentProduct = new MainActivity.Product(name, price, rating, false, imageUrl);
        isFavorite = WishlistManager.getInstance().isInWishlist(currentProduct);

        // Bind basic info
        setTextSafe(R.id.detailProductName, name);
        setTextSafe(R.id.detailProductPrice, price != null ? price : "");
        setTextSafe(R.id.detailProductRating, rating);
        setTextSafe(R.id.reviewCount, "(2.5k reviews)"); // Static for now

        // Description - build from available data
        String description = "High-quality product with excellent features and reliable performance. ";
        if (sku != null && !sku.isEmpty()) {
            description += "SKU: " + sku + ". ";
        }
        description += "Backed by manufacturer warranty and dedicated customer support. Check specifications below for detailed information.";
        setTextSafe(R.id.detailProductDescription, description);

        // Gallery - use the product image
        List<String> imgs = new ArrayList<>();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            imgs.add(imageUrl);
            // Add same image 3 more times as placeholders
            for (int i = 0; i < 3; i++) imgs.add(imageUrl);
        } else {
            // Fallback placeholder
            String fallback = "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=500&h=500&fit=crop";
            for (int i = 0; i < 4; i++) imgs.add(fallback);
        }
        setupGallery(imgs);

        // Tags
        List<String> tags = new ArrayList<>();
        tags.add("Premium Quality");
        tags.add("Fast Shipping");
        if (sku != null && !sku.isEmpty()) tags.add("SKU: " + sku);
        tags.add("1 Year Warranty");
        setupSpecTags(tags);

        // Spec cards
        List<SpecCard> specCards = new ArrayList<>();
        specCards.add(new SpecCard("QUALITY", "Premium", R.drawable.widgets_24));
        specCards.add(new SpecCard("WARRANTY", "1 Year", R.drawable.devices_24));
        specCards.add(new SpecCard("SHIPPING", "Fast", R.drawable.sports_esports_24));
        specCards.add(new SpecCard("SUPPORT", "24/7", R.drawable.mobile_24));
        setupSpecCards(specCards);

        // Spec rows
        Map<String, String> specRows = new LinkedHashMap<>();
        if (sku != null && !sku.isEmpty()) {
            specRows.put("SKU", sku);
        }
        specRows.put("Condition", "Brand New");
        specRows.put("Warranty", "1 Year Manufacturer Warranty");
        specRows.put("Shipping", "Free shipping on orders over $50");
        specRows.put("Return Policy", "30-day money back guarantee");
        specRows.put("Customer Support", "24/7 assistance available");
        setupSpecRows(specRows);

        detailFavoriteIcon = findViewById(R.id.detailWishlistIcon);
        updateFavoriteUI();
    }

    private void setTextSafe(int id, String text) {
        TextView tv = findViewById(id);
        if (tv != null && text != null) tv.setText(text);
    }

    // ── Gallery ───────────────────────────────────────────────────────────────

    private void setupGallery(List<String> urls) {
        imageUrls = urls;
        mainImage = findViewById(R.id.detailProductImage);

        dots[0] = findViewById(R.id.dot1);
        dots[1] = findViewById(R.id.dot2);
        dots[2] = findViewById(R.id.dot3);
        dots[3] = findViewById(R.id.dot4);

        thumbCards[0]  = findViewById(R.id.thumb1);
        thumbCards[1]  = findViewById(R.id.thumb2);
        thumbCards[2]  = findViewById(R.id.thumb3);
        thumbCards[3]  = findViewById(R.id.thumb4);
        thumbImages[0] = findViewById(R.id.thumbImg1);
        thumbImages[1] = findViewById(R.id.thumbImg2);
        thumbImages[2] = findViewById(R.id.thumbImg3);
        thumbImages[3] = findViewById(R.id.thumbImg4);

        // Load all thumbs
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            String url = i < urls.size() ? urls.get(i) : urls.get(0);
            if (thumbImages[i] != null) {
                Glide.with(this).load(url).centerCrop()
                     .placeholder(R.drawable.placeholder_image).into(thumbImages[i]);
            }
            if (thumbCards[i] != null) {
                thumbCards[i].setOnClickListener(v -> selectImage(idx));
            }
        }

        // Prev / Next arrows
        View prevBtn = findViewById(R.id.galleryPrev);
        View nextBtn = findViewById(R.id.galleryNext);
        if (prevBtn != null) prevBtn.setOnClickListener(v ->
                selectImage((currentImageIndex - 1 + imageUrls.size()) % imageUrls.size()));
        if (nextBtn != null) nextBtn.setOnClickListener(v ->
                selectImage((currentImageIndex + 1) % imageUrls.size()));
        UiAnimations.pressBounce(prevBtn, nextBtn);
        UiAnimations.pressBounce(thumbCards[0], thumbCards[1], thumbCards[2], thumbCards[3]);

        // Tap main image or fullscreen button → open ImageFullscreenActivity
        View fsBtn = findViewById(R.id.galleryFullscreen);
        View.OnClickListener openFullscreen = v -> {
            Intent intent = new Intent(this, ImageFullscreenActivity.class);
            intent.putStringArrayListExtra("image_urls", new ArrayList<>(imageUrls));
            intent.putExtra("start_index", currentImageIndex);
            startActivity(intent);
        };
        if (mainImage != null) mainImage.setOnClickListener(openFullscreen);
        if (fsBtn    != null) fsBtn.setOnClickListener(openFullscreen);

        selectImage(0);
    }

    private void selectImage(int index) {
        currentImageIndex = index;
        String url = index < imageUrls.size() ? imageUrls.get(index) : imageUrls.get(0);

        if (mainImage != null) {
            Glide.with(this).load(url)
                 .placeholder(R.drawable.placeholder_image)
                 .into(mainImage);
        }

        // Update dots — active becomes a wide pill (20dp), inactive a small circle (6dp)
        for (int i = 0; i < 4; i++) {
            if (dots[i] == null) continue;
            android.view.ViewGroup.LayoutParams lp = dots[i].getLayoutParams();
            if (i == index) {
                lp.width = dpToPx(20);
                dots[i].setLayoutParams(lp);
                dots[i].setBackgroundResource(R.drawable.dot_active);
            } else {
                lp.width = dpToPx(6);
                dots[i].setLayoutParams(lp);
                dots[i].setBackgroundResource(R.drawable.dot_inactive);
            }
        }

        // Update thumb borders
        for (int i = 0; i < 4; i++) {
            if (thumbCards[i] == null) continue;
            thumbCards[i].setStrokeWidth(i == index ? dpToPx(2) : dpToPx(1));
            thumbCards[i].setStrokeColor(i == index
                    ? Color.parseColor("#F97316")
                    : Color.parseColor("#EEEEEE"));
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ── Spec tags (pill chips) ────────────────────────────────────────────────

    private void setupSpecTags(List<String> tags) {
        LinearLayout row = findViewById(R.id.specTagsRow);
        if (row == null) return;
        row.removeAllViews();

        // Two rows wrapped manually
        LinearLayout line1 = new LinearLayout(this);
        line1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lineParams.bottomMargin = dpToPx(6);
        line1.setLayoutParams(lineParams);

        LinearLayout line2 = new LinearLayout(this);
        line2.setOrientation(LinearLayout.HORIZONTAL);
        line2.setLayoutParams(lineParams);

        row.addView(line1);
        row.addView(line2);

        for (int i = 0; i < tags.size(); i++) {
            TextView chip = new TextView(this);
            chip.setText(tags.get(i));
            ScreenUtils.applyTextSize(chip, R.dimen.text_size_caption);
            chip.setTextColor(Color.parseColor("#F97316"));
            chip.setBackground(ContextCompat.getDrawable(this, R.drawable.badge_pill));
            chip.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    Color.parseColor("#FFF0E6")));
            int h = dpToPx(10); int v = dpToPx(5);
            chip.setPadding(h, v, h, v);
            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            cp.setMarginEnd(dpToPx(8));
            chip.setLayoutParams(cp);

            if (i < 2) line1.addView(chip);
            else        line2.addView(chip);
        }
    }

    // ── 2x2 Spec cards ────────────────────────────────────────────────────────

    private void setupSpecCards(List<SpecCard> cards) {
        android.widget.GridLayout grid = findViewById(R.id.specCardsGrid);
        if (grid == null) return;
        grid.removeAllViews();

        for (int i = 0; i < Math.min(cards.size(), 4); i++) {
            SpecCard sc = cards.get(i);
            View v = LayoutInflater.from(this)
                    .inflate(R.layout.item_spec_card, grid, false);
            TextView label = v.findViewById(R.id.specCardLabel);
            TextView value = v.findViewById(R.id.specCardValue);
            ImageView icon = v.findViewById(R.id.specCardIcon);

            if (label != null) label.setText(sc.label);
            if (value != null) value.setText(sc.value);
            if (icon  != null) icon.setImageResource(sc.iconRes);
            grid.addView(v);
        }
    }

    // ── Detail spec rows ─────────────────────────────────────────────────────

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
                dp.setMarginStart(dpToPx(16));
                dp.setMarginEnd(dpToPx(16));
                divider.setLayoutParams(dp);
                divider.setBackgroundColor(Color.parseColor("#F0F0F0"));
                container.addView(divider);
            }
            View row = LayoutInflater.from(this)
                    .inflate(R.layout.item_spec_row, container, false);
            TextView key = row.findViewById(R.id.specKey);
            TextView val = row.findViewById(R.id.specValue);
            if (key != null) key.setText(entry.getKey());
            if (val != null) val.setText(entry.getValue());
            container.addView(row);
            first = false;
        }
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    // Tracks quantity chosen on the detail screen (resets each time the screen opens)
    private int selectedQuantity = 1;

    private void setupActions() {
        // ── Quantity stepper ──────────────────────────────────────────────────
        TextView quantityValue = findViewById(R.id.quantityValue);
        View decrement = findViewById(R.id.quantityDecrement);
        View increment = findViewById(R.id.quantityIncrement);

        if (quantityValue != null) quantityValue.setText(String.format(java.util.Locale.US, "%02d", selectedQuantity));

        if (decrement != null) decrement.setOnClickListener(v -> {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                if (quantityValue != null) quantityValue.setText(String.format(java.util.Locale.US, "%02d", selectedQuantity));
            }
        });

        if (increment != null) increment.setOnClickListener(v -> {
            selectedQuantity++;
            if (quantityValue != null) quantityValue.setText(String.format(java.util.Locale.US, "%02d", selectedQuantity));
        });

        UiAnimations.pressBounce(decrement, increment);

        // ── Add to cart ───────────────────────────────────────────────────────
        View addToCart = findViewById(R.id.addToCartButton);
        if (addToCart != null) addToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct, selectedQuantity);
            updateCartUI();
            String msg = selectedQuantity == 1
                    ? "Added to cart!"
                    : selectedQuantity + "× added to cart!";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        View favBtn = findViewById(R.id.wishlistButton);
        if (favBtn != null) favBtn.setOnClickListener(v -> {
            isFavorite = !isFavorite;
            if (isFavorite) WishlistManager.getInstance().addToWishlist(currentProduct);
            else            WishlistManager.getInstance().removeFromWishlist(currentProduct);
            updateFavoriteUI();
        });

        UiAnimations.pressBounce(addToCart, favBtn);
    }

    private void updateFavoriteUI() {
        if (detailFavoriteIcon == null) return;
        detailFavoriteIcon.setImageResource(
                isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        detailFavoriteIcon.setColorFilter(ContextCompat.getColor(this,
                isFavorite ? R.color.primary : R.color.text_primary));
    }

    // ── Bottom nav ────────────────────────────────────────────────────────────

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.HOME);
    }

    // ── Per-product data ──────────────────────────────────────────────────────
    
    /**
     * @deprecated This method contains HARDCODED MOCK DATA and should NOT be used.
     * Use fetchProductFromAPI() instead to get real data from the backend.
     * This method is kept only for backward compatibility with old intent-based calls.
     */
    @Deprecated
    private ProductInfo getProductInfo(String name, String fallbackImg) {
        android.util.Log.w(TAG, "⚠️ WARNING: Using deprecated getProductInfo() with hardcoded data!");
        String n = name != null ? name.toLowerCase() : "";

        // Default 4 images (vary angles using different Unsplash IDs)
        List<String> imgs = new ArrayList<>();

        if (n.contains("macbook air")) {
            imgs.add("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1542393545-10f5cde2c810?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=500&h=500&fit=crop");
            return new ProductInfo(imgs,
                "Supercharged by M2, the MacBook Air features a strikingly thin design and exceptional speed. Designed for portability, it provides a silent, fanless experience with up to 18 hours of battery life.",
                tags("Apple M2 Chip", "13.6\" Liquid Retina", "8GB Unified Memory", "256GB SSD"),
                specCards(
                    card("CHIP",    "Apple M2",    R.drawable.devices_24),
                    card("MEMORY",  "8GB Unified", R.drawable.widgets_24),
                    card("STORAGE", "256GB SSD",   R.drawable.sports_esports_24),
                    card("DISPLAY", "13.6\" Retina",R.drawable.devices_24)
                ),
                specRows("CPU","8-Core CPU","GPU","8-Core GPU","Camera","1080p FaceTime HD","Weight","2.7 Pounds","Battery","Up to 18 hrs"));

        } else if (n.contains("macbook pro")) {
            imgs.add("https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1542393545-10f5cde2c810?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=500&h=500&fit=crop");
            return new ProductInfo(imgs,
                "The MacBook Pro with M3 Pro chip delivers breakthrough performance for demanding workflows. With a stunning Liquid Retina XDR display and all-day battery, it redefines the professional laptop.",
                tags("M3 Pro Chip", "14.2\" XDR Display", "18GB Unified", "512GB SSD"),
                specCards(
                    card("CHIP",    "Apple M3 Pro", R.drawable.devices_24),
                    card("MEMORY",  "18GB Unified", R.drawable.widgets_24),
                    card("STORAGE", "512GB SSD",    R.drawable.sports_esports_24),
                    card("DISPLAY", "14.2\" XDR",   R.drawable.devices_24)
                ),
                specRows("CPU","11-Core CPU","GPU","14-Core GPU","Camera","1080p FaceTime HD","Weight","3.5 Pounds","Battery","Up to 17 hrs"));

        } else if (n.contains("iphone 15 pro max")) {
            imgs.add("https://images.unsplash.com/photo-1592286927505-1fed6c3d2c16?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1605236453806-6ff36851218e?w=500&h=500&fit=crop");
            return new ProductInfo(imgs,
                "iPhone 15 Pro Max. Forged in titanium with the A17 Pro chip, a customizable Action button, and the most powerful iPhone camera system ever.",
                tags("A17 Pro Chip", "6.7\" Super Retina", "48MP Main Camera", "USB-C"),
                specCards(
                    card("CHIP",    "A17 Pro",    R.drawable.mobile_24),
                    card("CAMERA",  "48 MP",      R.drawable.widgets_24),
                    card("STORAGE", "256GB",      R.drawable.sports_esports_24),
                    card("DISPLAY", "6.7\" OLED", R.drawable.mobile_24)
                ),
                specRows("Processor","A17 Pro","RAM","8GB","Battery","4,422 mAh","Camera","48+12+12 MP","Weight","221 g"));

        } else if (n.contains("iphone")) {
            imgs.add("https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1592286927505-1fed6c3d2c16?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1510557880182-3d4d3cba35a5?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500&h=500&fit=crop");
            return new ProductInfo(imgs,
                "iPhone 15 Pro. The first iPhone to feature an aerospace-grade titanium design. Featuring the A17 Pro chip with a 16-core Neural Engine.",
                tags("A17 Pro Chip", "6.1\" Super Retina", "48MP Camera", "Titanium"),
                specCards(
                    card("CHIP",    "A17 Pro",    R.drawable.mobile_24),
                    card("CAMERA",  "48 MP",      R.drawable.widgets_24),
                    card("STORAGE", "128GB",      R.drawable.sports_esports_24),
                    card("DISPLAY", "6.1\" OLED", R.drawable.mobile_24)
                ),
                specRows("Processor","A17 Pro","RAM","8GB","Battery","3,274 mAh","Camera","48+12+12 MP","Weight","187 g"));

        } else if (n.contains("samsung s24 ultra")) {
            imgs.add("https://images.unsplash.com/photo-1511707267537-b85faf00021e?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1585060544812-6b45742d762f?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1580910051074-3eb694886505?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1605236453806-6ff36851218e?w=500&h=500&fit=crop");
            return new ProductInfo(imgs,
                "Galaxy S24 Ultra. The ultimate Galaxy AI smartphone with a built-in S Pen, 200MP camera, and Snapdragon 8 Gen 3 — crafted in titanium for maximum durability.",
                tags("Snapdragon 8 Gen 3", "6.8\" QHD+ AMOLED", "200MP Camera", "S Pen"),
                specCards(
                    card("CHIP",    "SD 8 Gen 3", R.drawable.mobile_24),
                    card("CAMERA",  "200 MP",     R.drawable.widgets_24),
                    card("STORAGE", "256GB",      R.drawable.sports_esports_24),
                    card("DISPLAY", "6.8\" QHD+", R.drawable.mobile_24)
                ),
                specRows("Processor","Snapdragon 8 Gen 3","RAM","12GB","Battery","5,000 mAh","Camera","200+12+10+10 MP","Weight","232 g"));

        } else if (n.contains("rog strix") || n.contains("rog ally")) {
            imgs.add("https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1527814050087-3793815479db?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1580234811497-9df7fd2f357e?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1612287230202-1ff1d85d1bdf?w=500&h=500&fit=crop");
            return new ProductInfo(imgs,
                "ROG Strix G16 — built for domination. Powered by Intel Core i9 HX and NVIDIA RTX 4080, featuring a 16\" QHD 240Hz display for buttery-smooth gaming.",
                tags("Core i9 HX", "RTX 4080", "16\" QHD 240Hz", "32GB DDR5"),
                specCards(
                    card("CPU",     "Core i9 HX",  R.drawable.devices_24),
                    card("GPU",     "RTX 4080",     R.drawable.sports_esports_24),
                    card("RAM",     "32GB DDR5",    R.drawable.widgets_24),
                    card("DISPLAY", "16\" 240Hz",   R.drawable.devices_24)
                ),
                specRows("Processor","Intel Core i9-13980HX","Graphics","NVIDIA RTX 4080","RAM","32GB DDR5","Storage","1TB NVMe SSD","Display","16\" QHD 240Hz","Weight","2.3 kg"));

        } else if (n.contains("galaxy watch") || n.contains("apple watch")) {
            imgs.add("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1434494878577-86c23bcb06b9?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500&h=500&fit=crop");
            imgs.add("https://images.unsplash.com/photo-1579586337278-3befd40fd17a?w=500&h=500&fit=crop");
            return new ProductInfo(imgs,
                "Advanced health monitoring with ECG, blood oxygen sensor, and crash detection. Features an always-on Retina display with sapphire crystal glass.",
                tags("Always-On Display", "ECG + SpO2", "Crash Detection", "WR50m"),
                specCards(
                    card("DISPLAY", "Always-On",  R.drawable.devices_wearables_24),
                    card("HEALTH",  "ECG+SpO2",   R.drawable.widgets_24),
                    card("BATTERY", "36 Hours",   R.drawable.devices_wearables_24),
                    card("WATER",   "50m Resist", R.drawable.devices_wearables_24)
                ),
                specRows("Chip","S9 SiP","Display","Always-On Retina","Battery","Up to 36 hrs","Water Resist","WR50m","Sensors","ECG, SpO2, Temperature","Weight","51.5 g"));

        } else {
            // Generic fallback
            if (fallbackImg != null) {
                imgs.add(fallbackImg);
                imgs.add("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&h=500&fit=crop");
                imgs.add("https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=500&h=500&fit=crop");
                imgs.add("https://images.unsplash.com/photo-1542393545-10f5cde2c810?w=500&h=500&fit=crop");
            } else {
                for (int i = 0; i < 4; i++) imgs.add("https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&h=500&fit=crop");
            }
            return new ProductInfo(imgs,
                "Premium quality product built with the latest technology. Designed for performance, durability, and an exceptional user experience.",
                tags("Premium Build", "Latest Tech", "1-Year Warranty", "Brand New"),
                specCards(
                    card("CONDITION", "Brand New",  R.drawable.widgets_24),
                    card("WARRANTY",  "12 Months",  R.drawable.widgets_24),
                    card("DELIVERY",  "2-3 Days",   R.drawable.devices_24),
                    card("RETURNS",   "30 Days",    R.drawable.sports_esports_24)
                ),
                specRows("Condition","Brand New","Warranty","12 Months","Delivery","2-3 Business Days","Returns","30-Day Policy"));
        }
    }

    // ── Helper builders ───────────────────────────────────────────────────────

    private List<String> tags(String... t) {
        List<String> l = new ArrayList<>();
        for (String s : t) l.add(s);
        return l;
    }

    private List<SpecCard> specCards(SpecCard... cards) {
        List<SpecCard> l = new ArrayList<>();
        for (SpecCard c : cards) l.add(c);
        return l;
    }

    private SpecCard card(String label, String value, int icon) {
        return new SpecCard(label, value, icon);
    }

    private Map<String, String> specRows(String... pairs) {
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < pairs.length; i += 2) m.put(pairs[i], pairs[i + 1]);
        return m;
    }

    // ── Data classes ──────────────────────────────────────────────────────────

    static class ProductInfo {
        List<String> images;
        String description;
        String reviewCount;
        List<String> tags;
        List<SpecCard> specCards;
        Map<String, String> specRows;

        ProductInfo(List<String> images, String description, List<String> tags,
                    List<SpecCard> specCards, Map<String, String> specRows) {
            this.images = images;
            this.description = description;
            this.reviewCount = "120";
            this.tags = tags;
            this.specCards = specCards;
            this.specRows = specRows;
        }
    }

    static class SpecCard {
        String label, value;
        int iconRes;
        SpecCard(String label, String value, int iconRes) {
            this.label = label; this.value = value; this.iconRes = iconRes;
        }
    }
}
