package aeu.ratha.beckie_deal_app.ui.activities.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;
import aeu.ratha.beckie_deal_app.ui.adapters.ProductCardBinder;
import aeu.ratha.beckie_deal_app.ui.components.FilterBottomSheet;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.animations.ViewModeAnimator;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.data.repository.WishlistRepository;
import aeu.ratha.beckie_deal_app.data.models.WishlistItemResponse;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.preferences.ViewModePreferences;

public class CategoryDetailActivity extends BaseActivity {

    public static final String EXTRA_CATEGORY   = "category_name";
    public static final String EXTRA_HERO_IMAGE = "category_image_url";
    public static final String EXTRA_ITEM_COUNT = "category_item_count";
    public static final String EXTRA_BRAND_FILTER = "brand_filter";
    
    private static final String TAG = "CategoryDetailActivity";

    private String currentBrandFilter = "All";
    private String currentCategoryName = "";
    private int currentCategoryId = -1;      // Category ID from API
    private int currentBrandId = -1;         // Brand ID from API (when filtered)
    private String currentCategorySlug = ""; // Category slug from API
    private String currentBrandSlug = "";    // Brand slug from API
    private GridLayout productsGrid;
    private TextView resultsLabel;
    private LinearLayout brandFilterContainer;
    private ImageView viewModeIcon;
    
    private aeu.ratha.beckie_deal_app.data.repository.ProductRepository productRepository;
    private WishlistRepository wishlistRepository;
    private List<MainActivity.Product> allProducts = new ArrayList<>(); // All products from API
    private List<MainActivity.Product> filteredProducts = new ArrayList<>(); // Filtered/sorted products

    // Filter state (from FilterBottomSheet)
    private String currentSort     = FilterBottomSheet.SORT_DEFAULT;
    private String currentYear     = "All";
    private int    currentMaxPrice = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        // Initialize repository
        productRepository = aeu.ratha.beckie_deal_app.data.repository.ProductRepository.getInstance(this);
        wishlistRepository = new WishlistRepository(this);

        // Get intent extras
        currentCategoryName = getIntent().getStringExtra(EXTRA_CATEGORY);
        String heroImageUrl  = getIntent().getStringExtra(EXTRA_HERO_IMAGE);
        String itemCount     = getIntent().getStringExtra(EXTRA_ITEM_COUNT);
        String brandFilter   = getIntent().getStringExtra(EXTRA_BRAND_FILTER);
        
        // Get API IDs and slugs (new parameters from updated callers)
        currentCategoryId = getIntent().getIntExtra("CATEGORY_ID", -1);
        currentCategorySlug = getIntent().getStringExtra("CATEGORY_SLUG");
        currentBrandId = getIntent().getIntExtra("BRAND_ID", -1);
        currentBrandSlug = getIntent().getStringExtra("BRAND_SLUG");
        
        if (currentCategoryName == null) currentCategoryName = "Category";
        if (brandFilter != null) currentBrandFilter = brandFilter;

        productsGrid         = findViewById(R.id.detailProductsGrid);
        resultsLabel         = findViewById(R.id.resultsLabel);
        brandFilterContainer = findViewById(R.id.brandFilterContainer);

        setupTopBar();
        setupHero(heroImageUrl, itemCount);
        // setupBrandFilters() will be called after products are fetched

        // Wire Filter & Sort button
        View sortBtn = findViewById(R.id.sortButton);
        if (sortBtn != null) sortBtn.setOnClickListener(v -> openFilterSheet());

        // View mode toggle (grid ⇄ list)
        viewModeIcon = findViewById(R.id.viewModeIcon);
        View viewModeBtn = findViewById(R.id.viewModeButton);
        updateViewModeIcon();
        if (viewModeBtn != null) viewModeBtn.setOnClickListener(v ->
                ViewModeAnimator.transform(productsGrid, viewModeBtn, viewModeIcon, () -> {
                    ViewModePreferences.setListView(this, !ViewModePreferences.isListView(this));
                    updateViewModeIcon();
                    applyFiltersAndRender(); // Use filtered data, don't re-fetch
                }));
        UiAnimations.pressBounce(sortBtn, viewModeBtn);

        // Fetch products from API
        fetchProducts();
        
        setupBottomNavigation();
        setupEntranceAnimation();
    }

    /** Fades/slides the hero, brand filter row and results row into place —
     *  detailProductsGrid (the product cards) is left untouched on purpose. */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.categoryHeroCard), 0);
        UiAnimations.fadeSlideUp(brandFilterContainer, 100);
        UiAnimations.fadeSlideUp(findViewById(R.id.resultsRow), 150);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private void setupTopBar() {
        TextView titleView = findViewById(R.id.categoryTitleText);
        if (titleView != null) titleView.setText(currentCategoryName);

        ImageView backBtn = findViewById(R.id.backButton);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());

        View notificationContainer = findViewById(R.id.notificationContainer);
        if (notificationContainer != null)
            notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        UiAnimations.pressBounce(backBtn, notificationContainer);
    }

    // ── Hero banner ───────────────────────────────────────────────────────────

    private void setupHero(String heroImageUrl, String itemCount) {
        ImageView heroImage = findViewById(R.id.categoryHeroImage);
        TextView heroTitle  = findViewById(R.id.heroTitle);
        TextView heroSub    = findViewById(R.id.heroSubtitle);

        if (heroTitle != null) heroTitle.setText(currentCategoryName);
        if (heroSub   != null) heroSub.setText(itemCount != null ? itemCount : "");

        if (heroImage != null && heroImageUrl != null && !heroImageUrl.isEmpty()) {
            Glide.with(this).load(heroImageUrl).centerCrop()
                 .placeholder(R.drawable.placeholder_image).into(heroImage);
        }
    }

    // ── Brand filter chips ────────────────────────────────────────────────────

    private void setupBrandFilters() {
        if (brandFilterContainer == null) return;

        List<String> chips = new ArrayList<>();
        chips.add("All");
        
        // Extract unique brand names from fetched products
        java.util.Set<String> uniqueBrands = new java.util.LinkedHashSet<>();
        for (MainActivity.Product product : allProducts) {
            // Extract brand from product name or SKU (since UI products don't have direct brand field)
            // The brand is typically the first word in the product name
            if (product.name != null && !product.name.isEmpty()) {
                String[] parts = product.name.split(" ");
                if (parts.length > 0) {
                    uniqueBrands.add(parts[0]);
                }
            }
        }
        chips.addAll(uniqueBrands);

        brandFilterContainer.removeAllViews();
        for (String brand : chips) {
            View chip = LayoutInflater.from(this)
                    .inflate(R.layout.item_brand_chip, brandFilterContainer, false);
            TextView chipText = chip.findViewById(R.id.chipText);
            if (chipText != null) chipText.setText(brand);

            applyChipStyle((MaterialCardView) chip, chipText, brand.equals(currentBrandFilter));

            chip.setOnClickListener(v -> {
                currentBrandFilter = brand;
                setupBrandFilters();
                applyFiltersAndRender(); // Re-filter with new brand selection
            });
            UiAnimations.pressBounce(chip);
            brandFilterContainer.addView(chip);
        }
    }

    private void applyChipStyle(MaterialCardView card, TextView text, boolean selected) {
        card.setSelected(selected);
        card.setCardBackgroundColor(ContextCompat.getColor(
                this, selected ? R.color.primary : R.color.surface));
        card.setStrokeWidth(selected
                ? 0
                : getResources().getDimensionPixelSize(R.dimen.chip_stroke_width));
        card.setStrokeColor(ContextCompat.getColor(this, R.color.border_color));
        if (text != null) {
            text.setTextColor(ContextCompat.getColor(
                    this, selected ? R.color.on_primary : R.color.text_secondary));
        }
    }

    // ── Products grid ─────────────────────────────────────────────────────────

    /**
     * Fetch products from API based on category ID and optional brand ID.
     */
    private void fetchProducts() {
        android.util.Log.d(TAG, "=== FETCHING PRODUCTS FROM API ===");
        android.util.Log.d(TAG, "Category: " + currentCategoryName + " (ID: " + currentCategoryId + ")");
        android.util.Log.d(TAG, "Brand filter: " + currentBrandFilter + " (ID: " + currentBrandId + ")");
        
        // Show loading state
        if (resultsLabel != null) {
            resultsLabel.setText("Loading products...");
        }
        
        // Determine which API call to make based on filters
        if (currentCategoryId > 0 && currentBrandId > 0) {
            // Fetch by category AND brand
            productRepository.fetchProductsByCategoryAndBrand(currentCategoryId, currentBrandId, 100, 0,
                new aeu.ratha.beckie_deal_app.data.repository.ProductRepository.ProductCallback() {
                    @Override
                    public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
                        handleProductsFetched(apiProducts, total);
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        handleProductsError(errorMessage);
                    }
                });
        } else if (currentCategoryId > 0) {
            // Fetch by category only
            productRepository.fetchProductsByCategory(currentCategoryId, 100, 0,
                new aeu.ratha.beckie_deal_app.data.repository.ProductRepository.ProductCallback() {
                    @Override
                    public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
                        handleProductsFetched(apiProducts, total);
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        handleProductsError(errorMessage);
                    }
                });
        } else if (currentBrandId > 0) {
            // Fetch by brand only
            productRepository.fetchProductsByBrand(currentBrandId, 100, 0,
                new aeu.ratha.beckie_deal_app.data.repository.ProductRepository.ProductCallback() {
                    @Override
                    public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
                        handleProductsFetched(apiProducts, total);
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        handleProductsError(errorMessage);
                    }
                });
        } else {
            // Fallback: fetch all products (shouldn't normally happen)
            android.util.Log.w(TAG, "No category or brand ID provided, fetching all products");
            productRepository.fetchProducts(100, 0, false,
                new aeu.ratha.beckie_deal_app.data.repository.ProductRepository.ProductCallback() {
                    @Override
                    public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
                        handleProductsFetched(apiProducts, total);
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        handleProductsError(errorMessage);
                    }
                });
        }
    }
    
    /**
     * Handle successful product fetch from API.
     */
    private void handleProductsFetched(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
        android.util.Log.d(TAG, "✓ API SUCCESS: Received " + apiProducts.size() + " products (total: " + total + ")");
        
        // Convert API products to UI products
        allProducts.clear();
        allProducts.addAll(aeu.ratha.beckie_deal_app.utils.helpers.ProductMapper.toUiProducts(apiProducts));
        
        android.util.Log.d(TAG, "=== CONVERTED TO " + allProducts.size() + " UI PRODUCTS ===");
        
        // Apply filters and render on UI thread
        runOnUiThread(() -> {
            setupBrandFilters(); // Update brand chips with brands from fetched products
            applyFiltersAndRender();
        });
    }
    
    /**
     * Handle error fetching products from API.
     */
    private void handleProductsError(String errorMessage) {
        android.util.Log.e(TAG, "✗ API ERROR: " + errorMessage);
        
        runOnUiThread(() -> {
            Toast.makeText(this, "Failed to load products. Please try again.", Toast.LENGTH_SHORT).show();
            
            if (resultsLabel != null) {
                resultsLabel.setText("No products available");
            }
        });
    }
    
    /**
     * Apply current filters (brand, year, price, sort) to allProducts and render.
     * This replaces the old loadProducts() method.
     */
    private void applyFiltersAndRender() {
        List<MainActivity.Product> filtered = new ArrayList<>(allProducts);
        
        // Apply brand filter (if not fetched by brand already)
        if (currentBrandId <= 0 && !"All".equals(currentBrandFilter)) {
            List<MainActivity.Product> brandFiltered = new ArrayList<>();
            for (MainActivity.Product p : filtered) {
                // Match by product name or SKU containing brand name
                if (p.name != null && p.name.toLowerCase().contains(currentBrandFilter.toLowerCase())) {
                    brandFiltered.add(p);
                } else if (p.sku != null && p.sku.toLowerCase().contains(currentBrandFilter.toLowerCase())) {
                    brandFiltered.add(p);
                }
            }
            if (!brandFiltered.isEmpty()) filtered = brandFiltered;
        }

        // Apply year filter (price-band simulation since products have no real year field)
        if (!"All".equals(currentYear)) {
            int y = Integer.parseInt(currentYear);
            int minP = (y - 2010) * 100;
            int maxP = minP + 400;
            List<MainActivity.Product> yearFiltered = new ArrayList<>();
            for (MainActivity.Product p : filtered) {
                double price = parsePrice(p.price);
                if (price >= minP && price <= maxP) yearFiltered.add(p);
            }
            if (!yearFiltered.isEmpty()) filtered = yearFiltered;
        }

        // Apply max price filter
        List<MainActivity.Product> priceFiltered = new ArrayList<>();
        for (MainActivity.Product p : filtered) {
            if (parsePrice(p.price) <= currentMaxPrice) priceFiltered.add(p);
        }
        filtered = priceFiltered;

        // Apply sort
        switch (currentSort) {
            case FilterBottomSheet.SORT_PRICE_LOW:
                Collections.sort(filtered, (a, b) ->
                        Double.compare(parsePrice(a.price), parsePrice(b.price)));
                break;
            case FilterBottomSheet.SORT_PRICE_HIGH:
                Collections.sort(filtered, (a, b) ->
                        Double.compare(parsePrice(b.price), parsePrice(a.price)));
                break;
            case FilterBottomSheet.SORT_TOP_RATED:
                Collections.sort(filtered, (a, b) -> {
                    try {
                        return Double.compare(Double.parseDouble(b.rating), Double.parseDouble(a.rating));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                });
                break;
            default:
                break;
        }

        // Wishlist state managed by click handlers
        
        filteredProducts = filtered;

        // Build results label
        if (resultsLabel != null) {
            StringBuilder label = new StringBuilder();
            label.append(filtered.size()).append(" products");
            if (!"All".equals(currentBrandFilter)) label.append(" · ").append(currentBrandFilter);
            if (!currentSort.equals(FilterBottomSheet.SORT_DEFAULT))
                label.append(" · ").append(currentSort);
            if (!currentYear.equals("All")) label.append(" · ").append(currentYear);
            if (currentMaxPrice < 3000) label.append(" · ≤$").append(currentMaxPrice);
            resultsLabel.setText(label.toString());
        }

        renderProducts(filtered);
    }

    /** Open the filter bottom sheet. */
    private void openFilterSheet() {
        FilterBottomSheet sheet = FilterBottomSheet.newInstance(
                currentSort, currentYear, currentMaxPrice);
        sheet.setFilterListener((sortBy, year, maxPrice) -> {
            currentSort     = sortBy;
            currentYear     = year;
            currentMaxPrice = maxPrice;
            applyFiltersAndRender(); // Re-filter and render with new filter settings
        });
        sheet.show(getSupportFragmentManager(), "filter");
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace("$", "").replace(",", ""));
        } catch (NumberFormatException e) { return 0; }
    }

    private void renderProducts(List<MainActivity.Product> products) {
        if (productsGrid == null) return;
        productsGrid.removeAllViews();

        boolean isListView = ViewModePreferences.isListView(this);
        // Responsive columns: 2 on phone, 3 on tablet, 4 on large tablet (1 in list mode)
        int columns = isListView ? 1 : ScreenUtils.productGridColumns(this);
        productsGrid.setColumnCount(columns);
        int cardLayout = isListView ? R.layout.product_card_list : R.layout.product_card;

        if (products.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No products found.");
            empty.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            empty.setPadding(32, 64, 32, 64);
            productsGrid.addView(empty);
            return;
        }

        for (int i = 0; i < products.size(); i++) {
            final MainActivity.Product product = products.get(i);
            View view = LayoutInflater.from(this)
                    .inflate(cardLayout, productsGrid, false);

            // Bind all fields via shared binder
            ProductCardBinder.bind(this, view, product);

            // Open product detail on card tap
            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.id);
                intent.putExtra("product_name", product.name);
                intent.putExtra("product_price", product.price);
                intent.putExtra("product_rating", product.rating);
                intent.putExtra("product_image", product.imageUrl);
                intent.putExtra("product_sku", product.sku);
                startActivity(intent);
            });

            // Toggle favorite on heart tap
            ImageView favIcon = view.findViewById(R.id.favoriteIcon);
            if (favIcon != null) {
                favIcon.setOnClickListener(v -> {
                    if (!aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.requireAuthForWishlist(this)) {
                        return;
                    }
                    
                    if (product.id == null || product.id.isEmpty()) {
                        Toast.makeText(this, "Product ID not available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    favIcon.setEnabled(false);
                    
                    if (!product.isInWishlist) {
                        wishlistRepository.addToWishlist(product.id, 1, new WishlistRepository.WishlistItemCallback() {
                            @Override
                            public void onSuccess(WishlistItemResponse response) {
                                runOnUiThread(() -> {
                                    product.isInWishlist = true;
                                    if (response.getData() != null) {
                                        product.wishlistItemId = response.getData().getId();
                                    }
                                    ProductCardBinder.refreshFavIcon(CategoryDetailActivity.this, favIcon, true);
                                    Toast.makeText(CategoryDetailActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                                    favIcon.setEnabled(true);
                                });
                            }
                            
                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(CategoryDetailActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                                    favIcon.setEnabled(true);
                                });
                            }
                        });
                    } else {
                        if (product.wishlistItemId > 0) {
                            wishlistRepository.removeFromWishlist(product.wishlistItemId, new WishlistRepository.SimpleCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        product.isInWishlist = false;
                                        product.wishlistItemId = 0;
                                        ProductCardBinder.refreshFavIcon(CategoryDetailActivity.this, favIcon, false);
                                        Toast.makeText(CategoryDetailActivity.this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                                        favIcon.setEnabled(true);
                                    });
                                }
                                
                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(CategoryDetailActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                                        favIcon.setEnabled(true);
                                    });
                                }
                            });
                        } else {
                            product.isInWishlist = false;
                            ProductCardBinder.refreshFavIcon(this, favIcon, false);
                            favIcon.setEnabled(true);
                        }
                    }
                });
            }

            // Set layout params so each card takes exactly half the grid width (2 per row)
            // Calculate fixed width based on screen width divided by 2
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int gap = (int) (2 * getResources().getDisplayMetrics().density);
            int cardWidth = (screenWidth / 2) - (gap * 2);
            params.width = cardWidth;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            params.setMargins(gap, gap, gap, gap);
            view.setLayoutParams(params);

            productsGrid.addView(view);
        }
    }

    /** Swap the toggle button's icon to reflect the current mode (shows the icon for the OTHER mode to switch to). */
    private void updateViewModeIcon() {
        if (viewModeIcon == null) return;
        boolean isListView = ViewModePreferences.isListView(this);
        viewModeIcon.setImageResource(isListView ? R.drawable.grid_view_24 : R.drawable.event_list_24);
    }

    // ── Bottom navigation ─────────────────────────────────────────────────────

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.HOME);
    }
}
