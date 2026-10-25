package aeu.ratha.beckie_deal_app.ui.activities.product;

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
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.adapters.ProductCardBinder;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.animations.ViewModeAnimator;
import aeu.ratha.beckie_deal_app.data.repository.WishlistRepository;
import aeu.ratha.beckie_deal_app.data.models.WishlistItemResponse;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.preferences.ViewModePreferences;
import aeu.ratha.beckie_deal_app.utils.preferences.ScrollStatePreferences;

/**
 * Full-screen search activity.
 * Searches products from API by product name, brand, category, specs, or SKU.
 * 
 * State Preservation:
 * - Scroll position is saved/restored for each search query
 * - Search query is preserved across navigation
 * - View mode (grid/list) is persisted via ViewModePreferences
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
    private ImageView viewModeIcon;
    private String    lastQuery = "";
    
    private aeu.ratha.beckie_deal_app.data.repository.ProductRepository productRepository;
    private WishlistRepository wishlistRepository;
    private List<MainActivity.Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize repository
        productRepository = aeu.ratha.beckie_deal_app.data.repository.ProductRepository.getInstance(this);
        wishlistRepository = new WishlistRepository(this);

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

        UiAnimations.pressBounce(backBtn, clearButton);
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);

        aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.setup(this, aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.NavItem.HOME);
        aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.updateCartBadge(this);

        // View mode toggle (grid ⇄ list)
        viewModeIcon = findViewById(R.id.viewModeIcon);
        View viewModeBtn = findViewById(R.id.viewModeButton);
        updateViewModeIcon();
        if (viewModeBtn != null) viewModeBtn.setOnClickListener(v ->
                ViewModeAnimator.transform(resultsGrid, viewModeBtn, viewModeIcon, () -> {
                    ViewModePreferences.setListView(this, !ViewModePreferences.isListView(this));
                    updateViewModeIcon();
                    performSearch(lastQuery);
                }));
        UiAnimations.pressBounce(viewModeBtn);

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
        
        // Load all products for search
        loadProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Restore the saved search query from SharedPreferences
        String savedQuery = ScrollStatePreferences.getFilterState(this, "search", "query", "");
        if (!savedQuery.isEmpty() && searchEditText.getText().toString().isEmpty()) {
            android.util.Log.d("SearchActivity", "onResume - Restoring search query: " + savedQuery);
            searchEditText.setText(savedQuery);
            // Setting text will trigger TextWatcher → performSearch → renderResults → scroll restore
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // Save search query and scroll position
        if (!lastQuery.isEmpty()) {
            android.util.Log.d("SearchActivity", "onPause - Saving search query: " + lastQuery);
            ScrollStatePreferences.saveFilterState(this, "search", "query", lastQuery);
            saveSearchScrollPosition(lastQuery);
        }
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    private void loadProducts() {
        // Fetch all products from API for searching
        productRepository.fetchProducts(200, 0, false,
            new aeu.ratha.beckie_deal_app.data.repository.ProductRepository.ProductCallback() {
                @Override
                public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
                    allProducts.clear();
                    allProducts.addAll(aeu.ratha.beckie_deal_app.utils.helpers.ProductMapper.toUiProducts(apiProducts));
                    
                    // Sync wishlist state
                    syncWishlistState();
                }
                
                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(SearchActivity.this, 
                            "Failed to load products for search.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
    }
    
    /**
     * Sync wishlist state for all loaded products.
     * Fetches the user's wishlist and marks which products are in it.
     * If user is not logged in, all products remain not-in-wishlist.
     */
    private void syncWishlistState() {
        // Check if user is logged in
        if (!aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.isUserLoggedIn(this)) {
            // User not logged in - all products are not in wishlist
            for (MainActivity.Product product : allProducts) {
                product.isInWishlist = false;
                product.wishlistItemId = 0;
            }
            return;
        }
        
        // User is logged in - fetch their wishlist
        wishlistRepository.getWishlist(new aeu.ratha.beckie_deal_app.data.repository.WishlistRepository.WishlistCallback() {
            @Override
            public void onSuccess(aeu.ratha.beckie_deal_app.data.models.WishlistResponse response) {
                if (response != null && response.getData() != null) {
                    aeu.ratha.beckie_deal_app.data.models.Wishlist wishlist = response.getData();
                    
                    // Create a map of product IDs in the wishlist for quick lookup
                    java.util.Map<String, Integer> wishlistMap = new java.util.HashMap<>();
                    if (wishlist.getItems() != null) {
                        for (aeu.ratha.beckie_deal_app.data.models.WishlistItem item : wishlist.getItems()) {
                            if (item.getProduct() != null && item.getProduct().getId() != null) {
                                wishlistMap.put(item.getProduct().getId(), item.getId());
                            }
                        }
                    }
                    
                    // Update each product's wishlist state
                    for (MainActivity.Product product : allProducts) {
                        if (wishlistMap.containsKey(product.id)) {
                            product.isInWishlist = true;
                            product.wishlistItemId = wishlistMap.get(product.id);
                        } else {
                            product.isInWishlist = false;
                            product.wishlistItemId = 0;
                        }
                    }
                    
                    // Refresh search results if any exist
                    runOnUiThread(() -> {
                        if (!lastQuery.isEmpty()) {
                            performSearch(lastQuery);  // Re-render search results with updated wishlist state
                        }
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                // On error, default all to not in wishlist
                for (MainActivity.Product product : allProducts) {
                    product.isInWishlist = false;
                    product.wishlistItemId = 0;
                }
            }
        });
    }

    // ── Search logic ──────────────────────────────────────────────────────────

    private void performSearch(String query) {
        lastQuery = query;
        if (query.isEmpty()) {
            resultsScrollView.setVisibility(View.GONE);
            showEmptyState(true, "Search for products",
                    "Try searching by product name, brand, or category");
            resultsLabel.setText("Type to search products...");
            return;
        }

        String lower = query.toLowerCase();
        List<MainActivity.Product> results = new ArrayList<>();

        // Search through all products
        for (MainActivity.Product p : allProducts) {
            if (matches(p, lower)) {
                results.add(p);
            }
        }

        renderResults(results, query);
    }

    /**
     * Returns true if the product matches the query against any searchable field.
     */
    private boolean matches(MainActivity.Product p, String lower) {
        if (p.name  != null && p.name.toLowerCase().contains(lower))     return true;
        if (p.sku   != null && p.sku.toLowerCase().contains(lower))       return true;
        if (p.specs != null && p.specs.toLowerCase().contains(lower))     return true;
        // Note: Brand and category info could be added to Product model if needed
        return false;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    private void renderResults(List<MainActivity.Product> results, String query) {
        resultsGrid.removeAllViews();

        boolean isListView = ViewModePreferences.isListView(this);
        int columns = isListView ? 1 : ScreenUtils.productGridColumns(this);
        resultsGrid.setColumnCount(columns);
        int cardLayout = isListView ? R.layout.product_card_list : R.layout.product_card;

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

        for (MainActivity.Product product : results) {
            View view = LayoutInflater.from(this)
                    .inflate(cardLayout, resultsGrid, false);

            // Wishlist state managed by icon click handler

            // Bind all card fields
            ProductCardBinder.bind(this, view, product);

            // Tap → product detail
            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.id);
                intent.putExtra("product_name",     product.name);
                intent.putExtra("product_price",    product.price);
                intent.putExtra("product_image",    product.imageUrl);
                intent.putExtra("product_rating",   product.rating);
                intent.putExtra("is_in_wishlist",   product.isInWishlist);
                intent.putExtra("product_sku",      product.sku);
                intent.putExtra("product_specs",    product.specs);
                intent.putExtra("product_warranty", product.warrantyYears);
                intent.putExtra("product_in_stock", product.inStock);
                intent.putExtra("product_free_items", product.freeItems);
                startActivity(intent);
            });

            // Favourite toggle with API
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
                                    ProductCardBinder.refreshFavIcon(SearchActivity.this, favIcon, true);
                                    Toast.makeText(SearchActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                                    favIcon.setEnabled(true);
                                });
                            }
                            
                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(SearchActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
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
                                        ProductCardBinder.refreshFavIcon(SearchActivity.this, favIcon, false);
                                        Toast.makeText(SearchActivity.this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                                        favIcon.setEnabled(true);
                                    });
                                }
                                
                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(SearchActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
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

            // Set layout params so each card takes fixed width based on screen width divided by columns
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int gap = (int) (2 * getResources().getDisplayMetrics().density);
            int cardWidth = (screenWidth / columns) - (gap * 2);
            params.width = cardWidth;
            params.height = (int) (380 * getResources().getDisplayMetrics().density); // Match the fixed height in XML
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            params.setMargins(gap, gap, gap, gap);
            view.setLayoutParams(params);

            resultsGrid.addView(view);
        }
        
        // Restore scroll position AFTER all views are added and laid out
        restoreSearchScrollPosition(query);
    }
    
    /**
     * Restore scroll position for search results after views are rendered.
     * Uses ViewTreeObserver to ensure layout pass is complete.
     */
    private void restoreSearchScrollPosition(String query) {
        if (resultsScrollView == null) return;
        
        String savedScrollY = ScrollStatePreferences.getFilterState(this, "search_" + query, "scrollY", "0");
        android.util.Log.d("SearchActivity", "restoreScroll - Reading saved scrollY for query '" + query + "': " + savedScrollY);
        
        try {
            int scrollY = Integer.parseInt(savedScrollY);
            if (scrollY > 0) {
                // Use ViewTreeObserver to wait for layout pass
                resultsScrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                        new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        resultsScrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        android.util.Log.d("SearchActivity", "restoreScroll - Setting scrollY to: " + scrollY);
                        resultsScrollView.scrollTo(0, scrollY);
                    }
                });
            }
        } catch (NumberFormatException e) {
            android.util.Log.e("SearchActivity", "restoreScroll - Error: " + e.getMessage());
        }
    }

    /**
     * Save scroll position when user navigates away.
     */
    private void saveSearchScrollPosition(String query) {
        if (resultsScrollView != null && !query.isEmpty()) {
            int scrollY = resultsScrollView.getScrollY();
            android.util.Log.d("SearchActivity", "saveScroll - Saving scrollY for query '" + query + "': " + scrollY);
            ScrollStatePreferences.saveFilterState(this, "search_" + query, "scrollY", String.valueOf(scrollY));
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

    /** Swap the toggle button's icon to reflect the current mode (shows the icon for the OTHER mode to switch to). */
    private void updateViewModeIcon() {
        if (viewModeIcon == null) return;
        boolean isListView = ViewModePreferences.isListView(this);
        viewModeIcon.setImageResource(isListView ? R.drawable.grid_view_24 : R.drawable.event_list_24);
    }
}
