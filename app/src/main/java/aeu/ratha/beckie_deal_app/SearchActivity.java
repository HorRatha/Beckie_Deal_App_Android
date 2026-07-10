package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Full-screen search activity.
 *
 * Searches the entire {@link CategoryDetailActivity#CATALOGUE} against:
 *  - product name
 *  - brand name
 *  - category name
 *  - specs / SKU text
 *
 * Results are shown in the same 2-column product-card grid used everywhere else.
 * Launched by clicking the search bar on MainActivity or the search icon on
 * CategoriesActivity.
 */
public class SearchActivity extends BaseActivity {

    private EditText  searchEditText;
    private ImageView clearButton;
    private TextView  resultsLabel;
    private GridLayout resultsGrid;
    private View      resultsScrollView;
    private LinearLayout emptyState;
    private TextView  emptyTitle;
    private TextView  emptySubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText   = findViewById(R.id.searchEditText);
        clearButton      = findViewById(R.id.clearButton);
        resultsLabel     = findViewById(R.id.resultsLabel);
        resultsGrid      = findViewById(R.id.resultsGrid);
        resultsScrollView = findViewById(R.id.resultsScrollView);
        emptyState       = findViewById(R.id.emptyState);
        emptyTitle       = findViewById(R.id.emptyTitle);
        emptySubtitle    = findViewById(R.id.emptySubtitle);

        // Back button
        View backBtn = findViewById(R.id.backButton);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());

        // Clear button
        clearButton.setOnClickListener(v -> {
            searchEditText.setText("");
            searchEditText.requestFocus();
        });

        // Live search as the user types
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();
                clearButton.setVisibility(q.isEmpty() ? View.GONE : View.VISIBLE);
                performSearch(q);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // "Search" key on the software keyboard dismisses the keyboard and runs the search
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                hideKeyboard();
                performSearch(searchEditText.getText().toString().trim());
                return true;
            }
            return false;
        });

        // Show the initial empty/hint state
        showEmptyState(true, "Search for products",
                "Try searching by product name, brand, or category");

        // Auto-open keyboard
        searchEditText.requestFocus();
        searchEditText.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }, 100);
    }

    // ── Search logic ──────────────────────────────────────────────────────────

    private void performSearch(String query) {
        if (query.isEmpty()) {
            resultsScrollView.setVisibility(View.GONE);
            showEmptyState(true, "Search for products",
                    "Try searching by product name, brand, or category");
            resultsLabel.setText("Type to search products...");
            return;
        }

        String lower = query.toLowerCase();
        List<ProductResult> results = new ArrayList<>();

        // Walk the full catalogue: category → brand → products
        for (Map.Entry<String, Map<String, List<MainActivity.Product>>> catEntry
                : CategoryDetailActivity.CATALOGUE.entrySet()) {

            String categoryName = catEntry.getKey();

            for (Map.Entry<String, List<MainActivity.Product>> brandEntry
                    : catEntry.getValue().entrySet()) {

                String brandName = brandEntry.getKey();

                for (MainActivity.Product p : brandEntry.getValue()) {
                    if (matches(p, brandName, categoryName, lower)) {
                        results.add(new ProductResult(p, brandName, categoryName));
                    }
                }
            }
        }

        renderResults(results, query);
    }

    /**
     * Returns true if the product matches the query against any searchable field.
     */
    private boolean matches(MainActivity.Product p, String brand, String category, String lower) {
        if (p.name  != null && p.name.toLowerCase().contains(lower))     return true;
        if (brand   != null && brand.toLowerCase().contains(lower))       return true;
        if (category != null && category.toLowerCase().contains(lower))   return true;
        if (p.sku   != null && p.sku.toLowerCase().contains(lower))       return true;
        if (p.specs != null && p.specs.toLowerCase().contains(lower))     return true;
        return false;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    private void renderResults(List<ProductResult> results, String query) {
        resultsGrid.removeAllViews();
        resultsGrid.setColumnCount(2);

        if (results.isEmpty()) {
            resultsScrollView.setVisibility(View.GONE);
            showEmptyState(true, "No results for \"" + query + "\"",
                    "Try a different search term or browse by category");
            resultsLabel.setText("0 results for \"" + query + "\"");
            return;
        }

        emptyState.setVisibility(View.GONE);
        resultsScrollView.setVisibility(View.VISIBLE);
        resultsLabel.setText(results.size() + (results.size() == 1 ? " result" : " results")
                + " for \"" + query + "\"");

        for (ProductResult result : results) {
            final MainActivity.Product product = result.product;

            View view = LayoutInflater.from(this)
                    .inflate(R.layout.product_card, resultsGrid, false);

            // Sync favourite state
            product.isFavorite = FavoriteManager.getInstance().isFavorite(product);

            // Bind all card fields
            ProductCardBinder.bind(this, view, product);

            // Tap → product detail
            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("product_name",     product.name);
                intent.putExtra("product_price",    product.price);
                intent.putExtra("product_image",    product.imageUrl);
                intent.putExtra("product_rating",   product.rating);
                intent.putExtra("is_favorite",      product.isFavorite);
                intent.putExtra("product_sku",      product.sku);
                intent.putExtra("product_specs",    product.specs);
                intent.putExtra("product_warranty", product.warrantyYears);
                intent.putExtra("product_in_stock", product.inStock);
                intent.putExtra("product_free_items", product.freeItems);
                startActivity(intent);
            });

            // Favourite toggle
            ImageView favIcon = view.findViewById(R.id.favoriteIcon);
            if (favIcon != null) {
                favIcon.setOnClickListener(v -> {
                    product.isFavorite = !product.isFavorite;
                    if (product.isFavorite) FavoriteManager.getInstance().addFavorite(product);
                    else                    FavoriteManager.getInstance().removeFavorite(product);
                    ProductCardBinder.refreshFavIcon(this, favIcon, product.isFavorite);
                });
            }

            // Set layout params so each card takes exactly half the grid width (2 per row)
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(0, 0, 0, 0);
            view.setLayoutParams(params);

            resultsGrid.addView(view);
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private void showEmptyState(boolean show, String title, String subtitle) {
        emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        if (emptyTitle   != null) emptyTitle.setText(title);
        if (emptySubtitle != null) emptySubtitle.setText(subtitle);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    // ── Lightweight result wrapper ────────────────────────────────────────────

    private static class ProductResult {
        final MainActivity.Product product;
        final String brand;
        final String category;

        ProductResult(MainActivity.Product product, String brand, String category) {
            this.product  = product;
            this.brand    = brand;
            this.category = category;
        }
    }
}
