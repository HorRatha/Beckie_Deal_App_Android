package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        mode = getIntent().getStringExtra(EXTRA_MODE);
        if (mode == null) mode = MODE_NEW_COLLECTION;

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
        if (viewModeBtn != null) viewModeBtn.setOnClickListener(v -> {
            ViewModePreferences.setListView(this, !ViewModePreferences.isListView(this));
            updateViewModeIcon();
            applyAndRender();
        });

        setupBottomNavigation();
        applyAndRender();
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

    private List<MainActivity.Product> getAllProducts() {
        // Pull from the same catalogue used by CategoryDetailActivity
        List<MainActivity.Product> all = new ArrayList<>();
        for (java.util.Map<String, List<MainActivity.Product>> brandMap
                : CategoryDetailActivity.CATALOGUE.values()) {
            for (List<MainActivity.Product> list : brandMap.values())
                all.addAll(list);
        }
        return all;
    }

    /**
     * "New Collection" = we treat higher price as newer/flagship model (mock data has no date).
     * In a real app you'd add a releaseYear field. Here we simulate: products with price ≥ $800 =
     * new collection; products with rating ≥ 4.7 = top selling.
     */
    private List<MainActivity.Product> getBaseProducts() {
        List<MainActivity.Product> base = getAllProducts();
        List<MainActivity.Product> filtered = new ArrayList<>();
        if (MODE_NEW_COLLECTION.equals(mode)) {
            for (MainActivity.Product p : base) {
                double price = parsePrice(p.price);
                if (price >= 800) filtered.add(p);
            }
        } else {
            // top_selling: rating ≥ 4.7
            for (MainActivity.Product p : base) {
                double rating = Double.parseDouble(p.rating);
                if (rating >= 4.7) filtered.add(p);
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
                Collections.sort(products, (a, b) ->
                        Double.compare(Double.parseDouble(b.rating), Double.parseDouble(a.rating)));
                break;
            default:
                break; // Default order
        }

        // Sync favorites
        for (MainActivity.Product p : products)
            p.isFavorite = FavoriteManager.getInstance().isFavorite(p);

        updateFilterIndicators();
        renderProducts(products);
    }

    /**
     * Simulate year mapping: divide $0–$3000 range into per-year buckets.
     * Each year maps to a rough price tier so the filter does something visible.
     * 2010→$0-200, ..., 2024→$1200-1600, 2025/2026→$1600+
     */
    private List<MainActivity.Product> filterByYear(List<MainActivity.Product> list, String year) {
        int y = Integer.parseInt(year);
        // Each year = price band of ~200 dollars starting from 2010=$0
        int minP = (y - 2010) * 100;
        int maxP = minP + 400;
        List<MainActivity.Product> result = new ArrayList<>();
        for (MainActivity.Product p : list) {
            double price = parsePrice(p.price);
            if (price >= minP && price <= maxP) result.add(p);
        }
        // Fallback: if band produces nothing, return all (avoid empty list confusion)
        return result.isEmpty() ? list : result;
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
        chip.setTextSize(12f);
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
                intent.putExtra("product_name",   product.name);
                intent.putExtra("product_price",  product.price);
                intent.putExtra("product_image",  product.imageUrl);
                intent.putExtra("product_rating", product.rating);
                startActivity(intent);
            });

            ImageView favIcon = view.findViewById(R.id.favoriteIcon);
            if (favIcon != null) {
                favIcon.setOnClickListener(v -> {
                    product.isFavorite = !product.isFavorite;
                    if (product.isFavorite) FavoriteManager.getInstance().addFavorite(product);
                    else                    FavoriteManager.getInstance().removeFavorite(product);
                    ProductCardBinder.refreshFavIcon(this, favIcon, product.isFavorite);
                    Toast.makeText(this,
                            product.isFavorite ? "Added to Favorites" : "Removed from Favorites",
                            Toast.LENGTH_SHORT).show();
                });
            }

            // Set layout params so each card takes exactly 1/columns of the grid width
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(dp(2), dp(2), dp(2), dp(2));
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
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackground(null);
        bottomNav.setBackgroundTintList(null);
        bottomNav.setItemActiveIndicatorColor(android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.nav_indicator_fill)));

        android.content.res.ColorStateList navTint =
                ContextCompat.getColorStateList(this, R.color.bottom_nav_color);
        if (navTint != null) { bottomNav.setItemIconTintList(navTint); bottomNav.setItemTextColor(navTint); }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if      (id == R.id.nav_home)       startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_categories) startActivity(new Intent(this, CategoriesActivity.class));
            else if (id == R.id.nav_favorites)  startActivity(new Intent(this, FavoritesActivity.class));
            else if (id == R.id.nav_cart)       startActivity(new Intent(this, CartActivity.class));
            else if (id == R.id.nav_account)    startActivity(new Intent(this, ProfileActivity.class));
            else return true;
            overridePendingTransition(0, 0);
            finish();
            return true;
        });

        // Cart badge
        int cartCount = CartManager.getInstance().getTotalUnits();
        if (cartCount > 0) {
            com.google.android.material.badge.BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_cart);
            badge.setVisible(true);
            badge.setNumber(cartCount);
            badge.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.red_error));
            badge.setBadgeTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white_fixed));
        } else {
            bottomNav.removeBadge(R.id.nav_cart);
        }
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
