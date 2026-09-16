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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
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

/**
 * Full-list screen reached from "View All" on New Collection / Top Selling sections.
 * Accepts EXTRA_MODE = "new_collection" | "top_selling".
 * Supports the FilterBottomSheet for Sort / Year / Price filtering.
 */
public class ProductListActivity extends BaseActivity
        implements FilterBottomSheet.FilterListener {

    public static final String EXTRA_MODE          = "list_mode";
    public static final String MODE_NEW_COLLECTION = "new_collection";
    public static final String MODE_TOP_SELLING    = "top_selling";

    private String mode = MODE_NEW_COLLECTION;

    // Filter state
    private String currentSort     = FilterBottomSheet.SORT_DEFAULT;
    private String currentYear     = "All";
    private int    currentMaxPrice = 3000;

    private GridLayout productsGrid;
    private TextView resultsLabel;
    private View filterActiveDot;
    private LinearLayout activeFiltersContainer;
    private View activeFiltersScroll;
    private ImageView viewModeIcon;
    
    private aeu.ratha.beckie_deal_app.data.repository.ProductRepository productRepository;
    private WishlistRepository wishlistRepository;
    private List<MainActivity.Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) mode = MODE_NEW_COLLECTION;

        // Initialize repository
        productRepository = aeu.ratha.beckie_deal_app.data.repository.ProductRepository.getInstance(this);
        wishlistRepository = new WishlistRepository(this);

        productsGrid         = findViewById(R.id.productsGrid);
        resultsLabel         = findViewById(R.id.resultsLabel);
        filterActiveDot      = findViewById(R.id.filterActiveDot);
        activeFiltersContainer = findViewById(R.id.activeFiltersContainer);
        activeFiltersScroll  = findViewById(R.id.activeFiltersScroll);

        // Screen title
        TextView titleView = findViewById(R.id.screenTitle);
        if (titleView != null)
            titleView.setText(MODE_NEW_COLLECTION.equals(mode) ? "New Collection" : "Top Selling");

        // Back
        View backBtn = findViewById(R.id.backButton);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());

        // Filter button → open bottom sheet
        View filterBtn = findViewById(R.id.filterButton);
        if (filterBtn != null) filterBtn.setOnClickListener(v -> openFilterSheet());

        // View mode toggle (grid ⇄ list)
        viewModeIcon = findViewById(R.id.viewModeIcon);
        View viewModeBtn = findViewById(R.id.viewModeButton);
        updateViewModeIcon();
        if (viewModeBtn != null) viewModeBtn.setOnClickListener(v ->
                ViewModeAnimator.transform(productsGrid, viewModeBtn, viewModeIcon, () -> {
                    ViewModePreferences.setListView(this, !ViewModePreferences.isListView(this));
                    updateViewModeIcon();
                    applyAndRender();
                }));

        setupBottomNavigation();
        
        UiAnimations.pressBounce(backBtn, filterBtn, viewModeBtn);
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
        UiAnimations.fadeSlideUp(findViewById(R.id.resultsLabel), 80);
        
        // Fetch products from API
        fetchProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyAndRender();
    }

    // ── Filter sheet ──────────────────────────────────────────────────────────

    private void openFilterSheet() {
        FilterBottomSheet sheet = FilterBottomSheet.newInstance(
                currentSort, currentYear, currentMaxPrice);
        sheet.setFilterListener(this);
        sheet.show(getSupportFragmentManager(), "filter");
    }

    @Override
    public void onFiltersApplied(String sortBy, String year, int maxPrice) {
        currentSort     = sortBy;
        currentYear     = year;
        currentMaxPrice = maxPrice;
        applyAndRender();
    }

    // ── Data + filtering ──────────────────────────────────────────────────────

    private void fetchProducts() {
        if (resultsLabel != null) {
            resultsLabel.setText("Loading products...");
        }
        
        // Fetch all products and filter based on mode
        productRepository.fetchProducts(100, 0, MODE_TOP_SELLING.equals(mode),
            new aeu.ratha.beckie_deal_app.data.repository.ProductRepository.ProductCallback() {
                @Override
                public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
                    allProducts.clear();
                    allProducts.addAll(aeu.ratha.beckie_deal_app.utils.helpers.ProductMapper.toUiProducts(apiProducts));
                    
                    runOnUiThread(() -> applyAndRender());
                }
                
                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(ProductListActivity.this, 
                            "Failed to load products. Please try again.", Toast.LENGTH_SHORT).show();
                        if (resultsLabel != null) {
                            resultsLabel.setText("No products available");
                        }
                    });
                }
            });
    }

    /**
     * "New Collection" = higher priced products (flagship models).
     * "Top Selling" = high rated products (rating ≥ 4.7).
     */
    private List<MainActivity.Product> getBaseProducts() {
        List<MainActivity.Product> filtered = new ArrayList<>();
        
        if (MODE_NEW_COLLECTION.equals(mode)) {
            // New collection: price ≥ $800
            for (MainActivity.Product p : allProducts) {
                double price = parsePrice(p.price);
                if (price >= 800) filtered.add(p);
            }
        } else {
            // Top selling: rating ≥ 4.7
            for (MainActivity.Product p : allProducts) {
                try {
                    double rating = Double.parseDouble(p.rating);
                    if (rating >= 4.7) filtered.add(p);
                } catch (NumberFormatException e) {
                    // Skip products with invalid rating
                }
            }
        }
        return filtered;
    }

    private void applyAndRender() {
        List<MainActivity.Product> products = getBaseProducts();

        // Year filter (simulated: group price ranges into rough "eras" since no real year field)
        if (!"All".equals(currentYear)) {
            products = filterByYear(products, currentYear);
        }

        // Price ceiling
        List<MainActivity.Product> priceFiltered = new ArrayList<>();
        for (MainActivity.Product p : products) {
            if (parsePrice(p.price) <= currentMaxPrice) priceFiltered.add(p);
        }
        products = priceFiltered;

        // Sort
        switch (currentSort) {
            case FilterBottomSheet.SORT_PRICE_LOW:
                Collections.sort(products, (a, b) ->
                        Double.compare(parsePrice(a.price), parsePrice(b.price)));
                break;
            case FilterBottomSheet.SORT_PRICE_HIGH:
                Collections.sort(products, (a, b) ->
                        Double.compare(parsePrice(b.price), parsePrice(a.price)));
                break;
            case FilterBottomSheet.SORT_TOP_RATED:
                Collections.sort(products, (a, b) -> {
                    try {
                        return Double.compare(Double.parseDouble(b.rating), Double.parseDouble(a.rating));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                });
                break;
            default:
                break; // Default order
        }

        // Wishlist state managed by adapter
        
        updateFilterIndicators();
        renderProducts(products);
    }

    /**
     * Simulate year mapping: divide $0–$3000 range into per-year buckets.
     * Each year maps to a rough price tier so the filter does something visible.
     */
    private List<MainActivity.Product> filterByYear(List<MainActivity.Product> list, String year) {
        try {
            int y = Integer.parseInt(year);
            int minP = (y - 2010) * 100;
            int maxP = minP + 400;
            List<MainActivity.Product> result = new ArrayList<>();
            for (MainActivity.Product p : list) {
                double price = parsePrice(p.price);
                if (price >= minP && price <= maxP) result.add(p);
            }
            // Fallback: if band produces nothing, return all (avoid empty list confusion)
            return result.isEmpty() ? list : result;
        } catch (NumberFormatException e) {
            return list;
        }
    }

    private void updateFilterIndicators() {
        boolean hasFilter = !currentSort.equals(FilterBottomSheet.SORT_DEFAULT)
                || !currentYear.equals("All")
                || currentMaxPrice < 3000;

        if (filterActiveDot != null)
            filterActiveDot.setVisibility(hasFilter ? View.VISIBLE : View.GONE);

        if (activeFiltersContainer == null || activeFiltersScroll == null) return;

        activeFiltersContainer.removeAllViews();
        if (!hasFilter) {
            activeFiltersScroll.setVisibility(View.GONE);
            return;
        }
        activeFiltersScroll.setVisibility(View.VISIBLE);

        if (!currentSort.equals(FilterBottomSheet.SORT_DEFAULT))
            addActiveChip(currentSort);
        if (!currentYear.equals("All"))
            addActiveChip("Year: " + currentYear);
        if (currentMaxPrice < 3000)
            addActiveChip("Max $" + currentMaxPrice);
    }

    private void addActiveChip(String label) {
        TextView chip = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 8, 0);
        chip.setLayoutParams(lp);
        chip.setText(label);
        ScreenUtils.applyTextSize(chip, R.dimen.text_size_caption);
        chip.setTextColor(ContextCompat.getColor(this, R.color.primary));
        chip.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_unselected));
        chip.setPadding(dp(12), dp(6), dp(12), dp(6));
        activeFiltersContainer.addView(chip);
    }

    // ── Render grid ───────────────────────────────────────────────────────────

    private void renderProducts(List<MainActivity.Product> products) {
        if (productsGrid == null) return;
        productsGrid.removeAllViews();

        boolean isListView = ViewModePreferences.isListView(this);
        // Responsive columns: 2 on phone, 3 on tablet, 4 on large tablet (1 in list mode)
        int columns = isListView ? 1 : ScreenUtils.productGridColumns(this);
        productsGrid.setColumnCount(columns);
        int cardLayout = isListView ? R.layout.product_card_list : R.layout.product_card;

        if (resultsLabel != null)
            resultsLabel.setText("Showing " + products.size() + " products");

        if (products.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No products match your filters.");
            empty.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            empty.setPadding(dp(32), dp(48), dp(32), dp(48));
            productsGrid.addView(empty);
            return;
        }

        for (final MainActivity.Product product : products) {
            View view = LayoutInflater.from(this)
                    .inflate(cardLayout, productsGrid, false);

            // Bind all fields via shared binder
            ProductCardBinder.bind(this, view, product);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.id);
                intent.putExtra("product_name",   product.name);
                intent.putExtra("product_price",  product.price);
                intent.putExtra("product_image",  product.imageUrl);
                intent.putExtra("product_rating", product.rating);
                intent.putExtra("product_sku", product.sku);
                startActivity(intent);
            });

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
                                    ProductCardBinder.refreshFavIcon(ProductListActivity.this, favIcon, true);
                                    Toast.makeText(ProductListActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                                    favIcon.setEnabled(true);
                                });
                            }
                            
                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(ProductListActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
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
                                        ProductCardBinder.refreshFavIcon(ProductListActivity.this, favIcon, false);
                                        Toast.makeText(ProductListActivity.this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                                        favIcon.setEnabled(true);
                                    });
                                }
                                
                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(ProductListActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
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

            // Set layout params so each card takes exactly 1/columns of the grid width with fixed size
            // Calculate fixed width based on screen width divided by number of columns
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int margins = dp(2);
            int cardWidth = (screenWidth / 2) - (margins * 2);
            params.width = cardWidth;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            params.setMargins(margins, margins, margins, margins);
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace("$", "").replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int dp(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
