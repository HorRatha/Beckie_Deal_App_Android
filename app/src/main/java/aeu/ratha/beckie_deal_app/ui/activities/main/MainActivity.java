package aeu.ratha.beckie_deal_app.ui.activities.main;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.repository.CategoryRepository;
import aeu.ratha.beckie_deal_app.data.repository.WishlistRepository;
import aeu.ratha.beckie_deal_app.data.repository.CartRepository;
import aeu.ratha.beckie_deal_app.data.models.WishlistItemResponse;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoriesActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductListActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;
import aeu.ratha.beckie_deal_app.ui.adapters.ProductGridAdapter;
import aeu.ratha.beckie_deal_app.ui.adapters.PromotionBannerAdapter;
import aeu.ratha.beckie_deal_app.ui.adapters.SlideBannerAdapter;
import aeu.ratha.beckie_deal_app.data.models.Slide;
import aeu.ratha.beckie_deal_app.data.repository.SlideRepository;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.animations.ViewModeAnimator;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.preferences.ViewModePreferences;
import aeu.ratha.beckie_deal_app.utils.preferences.ScrollStatePreferences;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    
    private List<Product> products;
    private List<CategoryDisplay> categoryDisplayList; // UI display list
    private List<aeu.ratha.beckie_deal_app.data.models.Category> apiCategories; // API data
    private AppDrawerHelper drawerHelper;
    private ProductGridAdapter productAdapter;
    private ImageView viewModeIcon;
    private CategoryRepository categoryRepository;
    private aeu.ratha.beckie_deal_app.data.repository.ProductRepository productRepository;
    private WishlistRepository wishlistRepository;
    private CartRepository cartRepository;
    private aeu.ratha.beckie_deal_app.data.repository.PromotionRepository promotionRepository;
    private SlideRepository slideRepository;
    private SlideBannerAdapter slideBannerAdapter;
    // Auto-scroll for horizontal sections
    private Handler autoScrollHandler;
    private HorizontalScrollView newCollectionScroll;
    private boolean isNewCollectionTouched = false;
    private int newCollectionScrollIndex = 0;
    private static final long AUTO_SCROLL_PAUSE = 2500; // 2.5 seconds
    private static final int SCROLL_DURATION = 400; // smooth scroll duration in ms

    // Banner carousels
    // New Arrivals banner removed - now only using Slides and Promotions banners
    
    // Promotion banner auto-scroll
    private ViewPager2 promotionBannerPager;
    private Handler promotionAutoScrollHandler;
    private Runnable promotionAutoScrollRunnable;
    
    // Slides banner auto-scroll
    private Handler slideAutoScrollHandler;
    private Runnable slideAutoScrollRunnable;
    private boolean isPromotionTouched = false;
    
    private static final long BANNER_SCROLL_DELAY = 4500; // 4.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeData();
        initRepository();
        fetchCategories(); // Fetch from API
        fetchProducts(); // Fetch from API
        fetchSlides(); // Fetch carousel slides from API
        fetchPromotions(); // Fetch from API - promotions displayed automatically when ready
        drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        // Refresh drawer with latest user data from API to ensure sidebar shows correct user across all pages
        drawerHelper.refreshDrawerWithLatestUserData(this);
        setupTopBar();
        setupSearchBar();
        setupPromotionsCarousel(); // Setup arrow controls (data displayed when fetched)
        setupProducts();
        setupNewCollectionSection();
        setupCouponSection();
        setupBottomNavigation();
        setupAutoScroll();
        setupEntranceAnimation();
    }

    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
        UiAnimations.fadeSlideUp(findViewById(R.id.searchBar), 60);
        UiAnimations.fadeSlideUp(findViewById(R.id.slidesBannerCard), 80);
        UiAnimations.fadeSlideUp(findViewById(R.id.categoriesSectionHeader), 150);
        UiAnimations.fadeSlideUp(findViewById(R.id.newCollectionSectionHeader), 190);
        UiAnimations.fadeSlideUp(findViewById(R.id.promotionBannerCard), 210);
        UiAnimations.fadeSlideUp(findViewById(R.id.featuredProductsHeader), 270);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupProducts();
        
        // Setup scroll-to-top button
        androidx.core.widget.NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        android.widget.FrameLayout scrollToTopButton = findViewById(R.id.scrollToTopButton);
        aeu.ratha.beckie_deal_app.utils.ScrollToTopHelper.attachToNestedScrollView(nestedScrollView, scrollToTopButton);
        
        startAutoScroll();
        startPromotionAutoScroll();
        updateCartBadge();
        
        // Don't restore scroll here - products are still loading from API
        // Scroll will be restored in setupProducts() after data is fetched
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
        stopPromotionAutoScroll();
        
        // Save scroll position for the entire page (NestedScrollView)
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
        
        if (scrollView != null) {
            int currentScrollY = scrollView.getScrollY();
            android.util.Log.d(TAG, "onPause - Saving homepage scrollY: " + currentScrollY);
            ScrollStatePreferences.saveFilterState(this, "homepage", "scrollY", 
                    String.valueOf(currentScrollY));
        }
    }
    
    /**
     * Restore scroll position after products are rendered.
     * Called at the end of setupProducts() after adapter is set.
     */
    private void restoreScrollPositionAfterRender() {
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.nestedScrollView);
        if (scrollView == null) return;
        
        String savedScrollY = ScrollStatePreferences.getFilterState(this, "homepage", "scrollY", "0");
        android.util.Log.d(TAG, "restoreScrollPosition - Reading saved scrollY: " + savedScrollY);
        
        try {
            int scrollY = Integer.parseInt(savedScrollY);
            if (scrollY > 0) {
                // Use ViewTreeObserver to ensure layout is complete
                scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        android.util.Log.d(TAG, "restoreScrollPosition - Setting scrollY to: " + scrollY);
                        scrollView.scrollTo(0, scrollY);
                        android.util.Log.d(TAG, "restoreScrollPosition - After scroll: " + scrollView.getScrollY());
                    }
                });
            }
        } catch (NumberFormatException e) {
            android.util.Log.e(TAG, "restoreScrollPosition - Error: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoScroll();
        stopPromotionAutoScroll();
        
        // Stop slide auto-scroll
        if (slideAutoScrollHandler != null && slideAutoScrollRunnable != null) {
            slideAutoScrollHandler.removeCallbacks(slideAutoScrollRunnable);
            slideAutoScrollHandler = null;
            slideAutoScrollRunnable = null;
        }
    }

    private void initializeData() {
        // Initialize empty product list (will be populated from API)
        products = new ArrayList<>();

        // Initialize empty category display list (will be populated from API)
        categoryDisplayList = new ArrayList<>();
        apiCategories = new ArrayList<>();
    }
    
    /**
     * Initialize repositories.
     */
    private void initRepository() {
        categoryRepository = CategoryRepository.getInstance(this);
        productRepository = aeu.ratha.beckie_deal_app.data.repository.ProductRepository.getInstance(this);
        wishlistRepository = new WishlistRepository(this);
        cartRepository = new CartRepository(this);
        promotionRepository = new aeu.ratha.beckie_deal_app.data.repository.PromotionRepository(this);
        slideRepository = new SlideRepository(this);
    }
    
    /**
     * Fetch products from API.
     * Endpoint: GET /api/v1/products?limit=100&offset=0
     */
    private void fetchProducts() {
        Log.d(TAG, "=== FETCHING ALL PRODUCTS FROM API ===");
        
        // Fetch more products for homepage
        // NOTE: API has per_page limit (~100 max), so use reasonable value
        // The pagination system will fetch all pages automatically
        // PERF: Enable cache to show cached products instantly while fetching fresh data
        productRepository.fetchProducts(20, 0, true, 
            new aeu.ratha.beckie_deal_app.data.repository.ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
                Log.d(TAG, "✓ API SUCCESS: Received " + apiProducts.size() + " products (requested: 300, actual total: " + total + ")");
                
                // Convert API products to UI products
                products.clear();
                products.addAll(aeu.ratha.beckie_deal_app.utils.helpers.ProductMapper.toUiProducts(apiProducts));
                
                Log.d(TAG, "=== CONVERTED TO " + products.size() + " UI PRODUCTS ===");
                
                // If we got fewer products than requested, might need to fetch more
                if (apiProducts.size() < total) {
                    Log.d(TAG, "ℹ️ Note: API returned only " + apiProducts.size() + 
                            " products. Requested 300 but got fewer. " +
                            "Backend may need pagination improvements.");
                }
                
                // Sync wishlist state with actual user's wishlist (if logged in)
                syncWishlistState();
                
                // Update UI on main thread
                runOnUiThread(() -> {
                    setupNewCollectionSection();
                    setupTopSellingSection();
                    setupProducts(); // Refresh product grid
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "✗ API ERROR: " + errorMessage);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, 
                        "Failed to load products. Please check your connection.", 
                        Toast.LENGTH_SHORT).show();
                    // Initialize with empty list
                    products.clear();
                    setupNewCollectionSection();
                    setupTopSellingSection();
                    setupProducts();
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
            for (MainActivity.Product product : products) {
                product.isInWishlist = false;
                product.wishlistItemId = 0;
            }
            Log.d(TAG, "User not logged in - all products marked as not in wishlist");
            return;
        }
        
        // User is logged in - fetch their wishlist
        wishlistRepository.getWishlist(new WishlistRepository.WishlistCallback() {
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
                    for (MainActivity.Product product : products) {
                        if (wishlistMap.containsKey(product.id)) {
                            product.isInWishlist = true;
                            product.wishlistItemId = wishlistMap.get(product.id);
                            Log.d(TAG, "Product in wishlist: " + product.name + " (ID: " + product.id + ")");
                        } else {
                            product.isInWishlist = false;
                            product.wishlistItemId = 0;
                        }
                    }
                    
                    Log.d(TAG, "✓ Wishlist state synced for " + products.size() + " products");
                    
                    // Refresh the product grid adapter to show updated wishlist state
                    runOnUiThread(() -> {
                        if (productAdapter != null) {
                            productAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to sync wishlist state: " + error);
                // On error, default all to not in wishlist
                for (MainActivity.Product product : products) {
                    product.isInWishlist = false;
                    product.wishlistItemId = 0;
                }
            }
        });
    }
    
    /**
     * Fetch categories from API.
     * Endpoint: GET /api/v1/master/categories?limit=20&offset=0
     */
    private void fetchCategories() {
        Log.d(TAG, "=== FETCHING CURATED CATEGORIES ===");
        
        // Use fast cached category fetch without N+1 product count queries
        categoryRepository.fetchCategories(false, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Category> categories, int total) {
                if (categories == null || categories.isEmpty()) {
                    setupCategories(); // Render empty
                    return;
                }
                
                Log.d(TAG, "✓ Received " + categories.size() + " categories");
                
                apiCategories.clear();
                apiCategories.addAll(categories);
                
                // Efficient filtering with duplicate detection
                categoryDisplayList.clear();
                java.util.Set<Integer> usedOrders = new java.util.HashSet<>(9);
                
                for (aeu.ratha.beckie_deal_app.data.models.Category category : categories) {
                    CuratedCategoryInfo info = getCuratedCategoryInfo(category.getName());
                    
                    if (info != null && usedOrders.add(info.displayOrder)) {
                        categoryDisplayList.add(new CategoryDisplay(
                            category, info.imageResId, info.displayName, info.displayOrder));
                    }
                }
                
                // Guarantee "Laptop" curated category displays on frontend
                // if no category explicitly matched Laptop from API
                if (usedOrders.add(2)) {
                    aeu.ratha.beckie_deal_app.data.models.Category fallbackLaptop = 
                        new aeu.ratha.beckie_deal_app.data.models.Category(2, "Laptop", "laptops");
                    categoryDisplayList.add(new CategoryDisplay(
                        fallbackLaptop, R.drawable.laptop, "Laptop", 2));
                }
                
                // Single sort operation
                java.util.Collections.sort(categoryDisplayList, (a, b) -> 
                    Integer.compare(a.displayOrder, b.displayOrder));
                
                Log.d(TAG, "✓ Filtered to " + categoryDisplayList.size() + " curated categories");
                setupCategories();
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "✗ Error: " + errorMessage);
                categoryDisplayList.clear();
                setupCategories();
            }
        });
    }
    
    /**
     * Map curated category names to webp images and display order.
     * Returns null if category is not in the curated list.
     * 
     * Curated categories in order:
     * 1. accessories, 2. laptop, 3. macbook, 4. gaming, 5. smartphone, 
     * 6. tablet, 7. storage, 8. network, 9. monitor
     */
    private CuratedCategoryInfo getCuratedCategoryInfo(String categoryName) {
        String name = categoryName.toLowerCase().trim();
        
        Log.d(TAG, "Matching category: '" + categoryName + "' -> '" + name + "'");
        
        // Check most specific patterns first to avoid false matches
        
        // Check for MacBook FIRST (before generic laptop check)
        if (name.contains("macbook") || (name.contains("mac") && name.contains("book"))) {
            Log.d(TAG, "  ✓ Matched as MacBook");
            return new CuratedCategoryInfo("MacBook", R.drawable.macbook_pro, 3);
        }
        
        // Define curated categories with their display order and image resource
        if (name.contains("accessor")) {
            Log.d(TAG, "  ✓ Matched as Accessories");
            return new CuratedCategoryInfo("Accessories", R.drawable.accessories, 1);
        } else if ((name.contains("laptop") || name.contains("computer") || name.contains("desktop") ||
                    name.contains("asus") || name.contains("dell") || name.contains("hp") ||
                    name.contains("lenovo") || name.contains("acer") || name.contains("msi") ||
                    name.contains("notebook") || name.contains("ultrabook") || name.contains("workstation")) && !name.contains("gaming")) {
            Log.d(TAG, "  ✓ Matched as Laptop");
            return new CuratedCategoryInfo("Laptop", R.drawable.laptop, 2);
        } else if (name.contains("gaming")) {
            Log.d(TAG, "  ✓ Matched as Gaming");
            return new CuratedCategoryInfo("Gaming", R.drawable.gaming_laptop, 4);
        } else if (name.contains("phone") || name.contains("mobile") || name.contains("smartphone")) {
            Log.d(TAG, "  ✓ Matched as Smartphone");
            return new CuratedCategoryInfo("Smartphone", R.drawable.smartphone, 5);
        } else if (name.contains("tablet") || name.contains("ipad")) {
            Log.d(TAG, "  ✓ Matched as Tablet");
            return new CuratedCategoryInfo("Tablet", R.drawable.tablet, 6);
        } else if (name.contains("storage") || name.contains("drive") || name.contains("ssd")) {
            Log.d(TAG, "  ✓ Matched as Storage");
            return new CuratedCategoryInfo("Storage", R.drawable.storage, 7);
        } else if (name.contains("network") || name.contains("wifi") || name.contains("router")) {
            Log.d(TAG, "  ✓ Matched as Network");
            return new CuratedCategoryInfo("Network", R.drawable.networking, 8);
        } else if (name.contains("monitor") || name.contains("display")) {
            Log.d(TAG, "  ✓ Matched as Monitor");
            return new CuratedCategoryInfo("Monitor", R.drawable.monitor, 9);
        }
        
        Log.d(TAG, "  ✗ No match for curated category");
        return null; // Not a curated category
    }
    
    /**
     * Helper class to store curated category information
     */
    private static class CuratedCategoryInfo {
        String displayName;
        int imageResId;
        int displayOrder;
        
        CuratedCategoryInfo(String displayName, int imageResId, int displayOrder) {
            this.displayName = displayName;
            this.imageResId = imageResId;
            this.displayOrder = displayOrder;
        }
    }
    
    /**
     * Handle API error - show empty state or error message.
     */
    private void initFallbackCategories() {
        Log.e(TAG, "Failed to load categories from API");
        categoryDisplayList.clear();
        
        // Show error message to user
        runOnUiThread(() -> {
            Toast.makeText(this, "Failed to load categories. Please check your connection.", 
                Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Fetch slides/carousel promotions from backend API.
     * Endpoint: GET /api/v1/slides
     */
    private void fetchSlides() {
        Log.d(TAG, "=== FETCHING SLIDES FROM API ===");
        
        slideRepository.getSlides(new SlideRepository.SlideCallback() {
            @Override
            public void onSuccess(java.util.List<Slide> slides) {
                Log.d(TAG, "✓ Loaded " + slides.size() + " slides from API");
                runOnUiThread(() -> {
                    displaySlidesCarousel(slides);
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "✗ Failed to fetch slides: " + errorMessage);
                runOnUiThread(() -> {
                    ViewPager2 slidePager = findViewById(R.id.promotionBannerViewPager);
                    if (slidePager != null) {
                        slidePager.setVisibility(View.GONE);
                        Log.e(TAG, "Hiding slides banner due to error");
                    }
                });
            }
        });
    }

    /**
     * Display slides/carousel promotions on homepage.
     */
    private void displaySlidesCarousel(java.util.List<Slide> slides) {
        ViewPager2 slidesPager = findViewById(R.id.slidesBannerViewPager);
        LinearLayout dotsContainer = findViewById(R.id.slidesBannerDotsContainer);
        
        Log.d(TAG, "displaySlidesCarousel called with " + (slides != null ? slides.size() : "null") + " slides");
        
        if (slidesPager == null) {
            Log.e(TAG, "❌ Slides pager is NULL! Cannot find R.id.slidesBannerViewPager");
            return;
        }
        
        if (slides == null || slides.isEmpty()) {
            Log.e(TAG, "❌ Slides list is null or empty!");
            slidesPager.setVisibility(View.GONE);
            return;
        }
        
        try {
            slideBannerAdapter = new SlideBannerAdapter(this, slides);
            slidesPager.setAdapter(slideBannerAdapter);
            slidesPager.setVisibility(View.VISIBLE);
            
            Log.d(TAG, "✓ Slides adapter set successfully, item count: " + slideBannerAdapter.getItemCount());
            
            if (dotsContainer != null) {
                setupSlideDots(slidesPager, dotsContainer, slideBannerAdapter.getItemCount());
            }
            
            setupSlideAutoScroll(slidesPager, slideBannerAdapter.getItemCount());
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting slides adapter: " + e.getMessage(), e);
        }
    }
    
    /**
     * Setup dot indicators for slides carousel.
     */
    private void setupSlideDots(ViewPager2 viewPager, LinearLayout dotsContainer, int count) {
        dotsContainer.removeAllViews();
        
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(12, 12);
            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.dot_indicator);
            
            int index = i;
            dot.setOnClickListener(v -> viewPager.setCurrentItem(index));
            dotsContainer.addView(dot);
        }
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsContainer.getChildCount(); i++) {
                    View dot = dotsContainer.getChildAt(i);
                    dot.setAlpha(i == position ? 1.0f : 0.4f);
                }
            }
        });
    }
    
    /**
     * Setup auto-scroll for slides carousel.
     */
    private void setupSlideAutoScroll(ViewPager2 viewPager, int itemCount) {
        // Stop any existing auto-scroll
        if (slideAutoScrollHandler != null && slideAutoScrollRunnable != null) {
            slideAutoScrollHandler.removeCallbacks(slideAutoScrollRunnable);
        }
        
        slideAutoScrollHandler = new Handler(Looper.getMainLooper());
        slideAutoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (viewPager != null && viewPager.getAdapter() != null) {
                        int currentItem = viewPager.getCurrentItem();
                        viewPager.setCurrentItem((currentItem + 1) % itemCount, true);
                        slideAutoScrollHandler.postDelayed(this, 5000);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in slide auto-scroll: " + e.getMessage());
                }
            }
        };
        
        slideAutoScrollHandler.postDelayed(slideAutoScrollRunnable, 5000);
        
        viewPager.setOnTouchListener((v, event) -> {
            if (slideAutoScrollHandler != null && slideAutoScrollRunnable != null) {
                slideAutoScrollHandler.removeCallbacks(slideAutoScrollRunnable);
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    slideAutoScrollHandler.postDelayed(slideAutoScrollRunnable, 5000);
                }
            }
            return false;
        });
    }

    /**
     * Fetch active promotions from backend API.
     * Endpoint: GET /api/v1/promotions
     */
    private void fetchPromotions() {
        Log.d(TAG, "=== FETCHING PROMOTIONS FROM API ===");
        
        promotionRepository.getPromotions(new aeu.ratha.beckie_deal_app.data.repository.PromotionRepository.PromotionCallback() {
            @Override
            public void onSuccess(java.util.List<aeu.ratha.beckie_deal_app.data.models.Promotion> promotions) {
                Log.d(TAG, "✓ Loaded " + promotions.size() + " promotions from API");
                runOnUiThread(() -> {
                    displayPromotionsCarousel(promotions);
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "✗ Failed to fetch promotions: " + errorMessage);
                runOnUiThread(() -> {
                    // Show error state
                    ViewPager2 promotionPager = findViewById(R.id.promotionBannerViewPager);
                    if (promotionPager != null) {
                        promotionPager.setVisibility(View.GONE);
                        Log.e(TAG, "Hiding promotions banner due to error");
                    }
                });
            }
        });
    }

    /**
     * Setup promotions banner carousel section on home screen.
     * Note: Adapter is created in displayPromotionsCarousel after data is fetched
     */
    private void setupPromotionsCarousel() {
        ViewPager2 promotionPager = findViewById(R.id.promotionBannerViewPager);
        
        if (promotionPager == null) {
            Log.w(TAG, "Promotion banner UI elements not found in layout");
            return;
        }
        
        Log.d(TAG, "✓ Promotion banner setup complete (auto-scroll enabled)");
    }
    
    /**
     * Setup dot indicators for promotion carousel.
     */
    private void setupPromotionDots(ViewPager2 viewPager, LinearLayout dotsContainer, int count) {
        dotsContainer.removeAllViews();
        
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            dot.setLayoutParams(new LinearLayout.LayoutParams(8, 8));
            dot.setBackgroundResource(R.drawable.dot_indicator);
            dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                i == 0 ? getResources().getColor(R.color.primary, getTheme()) 
                       : getResources().getColor(R.color.text_muted, getTheme())));
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);
            dotsContainer.addView(dot);
        }
        
        // Update dots on page change
        viewPager.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsContainer.getChildCount(); i++) {
                    View dot = dotsContainer.getChildAt(i);
                    dot.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                        i == position ? getResources().getColor(R.color.primary, getTheme())
                                     : getResources().getColor(R.color.text_muted, getTheme())));
                }
            }
        });
    }

    /**
     * Display promotions in the banner carousel (called when data is fetched).
     */
    private void displayPromotionsCarousel(java.util.List<aeu.ratha.beckie_deal_app.data.models.Promotion> promotions) {
        promotionBannerPager = findViewById(R.id.promotionBannerViewPager);
        LinearLayout dotsContainer = findViewById(R.id.promotionBannerDotsContainer);
        
        Log.d(TAG, "displayPromotionsCarousel called with " + (promotions != null ? promotions.size() : "null") + " promotions");
        
        if (promotionBannerPager == null) {
            Log.e(TAG, "❌ Promotion pager is NULL! Cannot find R.id.promotionBannerViewPager");
            return;
        }
        
        if (promotions == null || promotions.isEmpty()) {
            Log.e(TAG, "❌ Promotions list is null or empty!");
            // Set red background so we know it's empty
            promotionBannerPager.setBackgroundColor(0xFFFF0000);
            return;
        }
        
        Log.d(TAG, "✓ Creating adapter with " + promotions.size() + " promotions");
        for (aeu.ratha.beckie_deal_app.data.models.Promotion p : promotions) {
            Log.d(TAG, "  Promotion: " + p.getCode() + " - " + p.getName());
        }
        
        // Create and set adapter
        try {
            PromotionBannerAdapter adapter = new PromotionBannerAdapter(this, promotions);
            promotionBannerPager.setAdapter(adapter);
            promotionBannerPager.setVisibility(View.VISIBLE);
            promotionBannerPager.setBackgroundColor(0x00000000); // Transparent
            
            Log.d(TAG, "✓ Adapter set successfully, item count: " + adapter.getItemCount());
            
            // Setup dot indicators
            if (dotsContainer != null) {
                setupPromotionDots(promotionBannerPager, dotsContainer, adapter.getItemCount());
                Log.d(TAG, "✓ Dots setup complete");
            }
            
            // Setup auto-scroll with pause on touch
            setupPromotionAutoScroll(promotionBannerPager, adapter.getItemCount());
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting adapter: " + e.getMessage(), e);
            promotionBannerPager.setBackgroundColor(0xFFFFFF00); // Yellow for error
        }
    }
    
    /**
     * Setup auto-scroll for promotion banner carousel (4.5 seconds delay, same as other banners).
     */
    private void setupPromotionAutoScroll(ViewPager2 promotionPager, int count) {
        if (promotionPager == null || count <= 1) return;
        
        // Auto-scroll for promotions (same delay as new arrivals banner)
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (promotionPager == null) return;
                if (isPromotionTouched) return;
                int next = (promotionPager.getCurrentItem() + 1) % count;
                promotionPager.setCurrentItem(next, true);
                handler.postDelayed(this, BANNER_SCROLL_DELAY);
            }
        };

        promotionAutoScrollHandler = handler;
        promotionAutoScrollRunnable = runnable;

        // Start auto-scroll
        handler.postDelayed(runnable, BANNER_SCROLL_DELAY);

        // Pause on user drag
        promotionPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    isPromotionTouched = true;
                    if (promotionAutoScrollHandler != null && promotionAutoScrollRunnable != null) {
                        promotionAutoScrollHandler.removeCallbacks(promotionAutoScrollRunnable);
                    }
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    isPromotionTouched = false;
                    resetPromotionAutoScroll();
                }
            }
        });
        
        Log.d(TAG, "✓ Promotion auto-scroll setup complete (delay: " + BANNER_SCROLL_DELAY + "ms)");
    }
    
    /**
     * Reset promotion banner auto-scroll after user stops interacting.
     */
    private void resetPromotionAutoScroll() {
        if (promotionAutoScrollHandler != null && promotionAutoScrollRunnable != null) {
            promotionAutoScrollHandler.removeCallbacks(promotionAutoScrollRunnable);
            promotionAutoScrollHandler.postDelayed(promotionAutoScrollRunnable, BANNER_SCROLL_DELAY);
        }
    }
    
    /**
     * Start promotion banner auto-scroll (called on resume).
     */
    private void startPromotionAutoScroll() {
        if (!isPromotionTouched) {
            resetPromotionAutoScroll();
        }
    }
    
    /**
     * Stop promotion banner auto-scroll (called on pause).
     */
    private void stopPromotionAutoScroll() {
        if (promotionAutoScrollHandler != null && promotionAutoScrollRunnable != null) {
            promotionAutoScrollHandler.removeCallbacks(promotionAutoScrollRunnable);
        }
    }

    /**
     * Parse ISO 8601 date string to short date format (e.g., "Oct 13").
     */
    private String parseExpiryDate(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "Unlimited";
        }
        
        try {
            // Parse ISO date like "2026-10-13T01:34:03+00:00"
            java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", 
                java.util.Locale.getDefault());
            java.util.Date date = isoFormat.parse(isoDate);
            
            // Format as "Oct 13"
            java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("MMM dd", 
                java.util.Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse date: " + isoDate, e);
            return "Soon";
        }
    }

    private void setupTopBar() {
        // Configure logo display based on light/dark mode
        ImageView logoImage = findViewById(R.id.logoImage);
        if (logoImage != null) {
            int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            boolean isLight = nightMode != android.content.res.Configuration.UI_MODE_NIGHT_YES;
            
            if (isLight) {
                // In light mode: display the launcher icon with optimized scaling
                logoImage.setImageResource(R.mipmap.ic_launcher);
                logoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                // In dark mode: display the launcher icon as-is
                logoImage.setImageResource(R.mipmap.ic_launcher);
                logoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }
        
        // menuIcon click is handled by AppDrawerHelper
        View notificationContainer = findViewById(R.id.notificationContainer);

        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        UiAnimations.pressBounce(notificationContainer);
    }

    private void setupSearchBar() {
        View searchBar = findViewById(R.id.searchBar);
        if (searchBar != null) searchBar.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
        UiAnimations.pressBounce(searchBar);
    }



    /**
     * Reusable carousel setup — works for any banner section.
     * Pass imageUrls as null for gradient-only banners, or provide URLs for image-based banners.
     */
    private void setupBannerCarousel(ViewPager2 viewPager, ImageView arrowLeft, ImageView arrowRight,
                                     LinearLayout dotsContainer, String[][] texts, int[] backgrounds,
                                     String[] imageUrls, boolean isHero) {
        if (viewPager == null) return;

        int count = texts.length;

        viewPager.setAdapter(new RecyclerView.Adapter<BannerViewHolder>() {
            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_banner, parent, false);
                return new BannerViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
                holder.tag.setText(texts[position][0]);
                holder.title.setText(texts[position][1]);
                holder.subtitle.setText(texts[position][2]);

                // Load banner images from Pinterest/Unsplash
                if (imageUrls != null && imageUrls[position] != null && !imageUrls[position].isEmpty()) {
                    holder.bannerImage.setVisibility(View.VISIBLE);
                    holder.background.setVisibility(View.GONE);
                    Glide.with(holder.itemView.getContext())
                            .load(imageUrls[position])
                            .centerCrop()
                            .placeholder(backgrounds[position])
                            .error(backgrounds[position])
                            .into(holder.bannerImage);
                } else {
                    // Fallback to gradient backgrounds
                    holder.bannerImage.setVisibility(View.GONE);
                    holder.background.setVisibility(View.VISIBLE);
                    holder.background.setBackgroundResource(backgrounds[position]);
                }
            }

            @Override
            public int getItemCount() {
                return count;
            }
        });

        // Dot indicators
        if (dotsContainer != null) {
            dotsContainer.removeAllViews();
            View[] dots = new View[count];
            for (int i = 0; i < count; i++) {
                dots[i] = new View(this);
                int size = (i == 0) ? dpToPx(8) : dpToPx(6);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                lp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
                dots[i].setLayoutParams(lp);
                dots[i].setBackgroundResource(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
                dotsContainer.addView(dots[i]);
            }

            viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < dots.length; i++) {
                        int size = (i == position) ? dpToPx(8) : dpToPx(6);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                        lp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
                        dots[i].setLayoutParams(lp);
                        dots[i].setBackgroundResource(i == position
                                ? R.drawable.dot_active : R.drawable.dot_inactive);
                    }
                }
            });
        }

        // Arrow navigation
        if (arrowLeft != null) {
            arrowLeft.setOnClickListener(v -> {
                int current = viewPager.getCurrentItem();
                viewPager.setCurrentItem(current > 0 ? current - 1 : count - 1, true);
            });
        }
        if (arrowRight != null) {
            arrowRight.setOnClickListener(v -> {
                int current = viewPager.getCurrentItem();
                viewPager.setCurrentItem(current < count - 1 ? current + 1 : 0, true);
            });
        }
        UiAnimations.pressBounce(arrowLeft, arrowRight);
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        View background;
        ImageView bannerImage;
        TextView tag, title, subtitle;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.bannerBackground);
            bannerImage = itemView.findViewById(R.id.bannerImage);
            tag = itemView.findViewById(R.id.bannerTag);
            title = itemView.findViewById(R.id.bannerTitle);
            subtitle = itemView.findViewById(R.id.bannerSubtitle);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    /**
     * Render categories in UI from API data - Horizontal scrollable layout.
     * This method is called after fetchCategories() completes successfully.
     */
    private void setupCategories() {
        LinearLayout categoriesContainer = findViewById(R.id.categoriesContainer);
        if (categoriesContainer == null) return;
        
        // Skip if no categories to display
        if (categoryDisplayList.isEmpty()) {
            categoriesContainer.removeAllViews();
            return;
        }
        
        // Setup header only once
        TextView viewAllCategories = findViewById(R.id.viewAllCategories);
        if (viewAllCategories != null) {
            viewAllCategories.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CategoriesActivity.class)));
            UiAnimations.pressBounce(viewAllCategories);
        }

        categoriesContainer.removeAllViews();
        categoriesContainer.setOrientation(LinearLayout.HORIZONTAL);

        // Cache layout params
        final LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        itemParams.setMargins(3, 0, 3, 0);

        // Render all category cards efficiently
        for (CategoryDisplay categoryDisplay : categoryDisplayList) {
            View categoryView = LayoutInflater.from(this).inflate(R.layout.category_item, categoriesContainer, false);
            categoryView.setLayoutParams(itemParams);

            // Setup card styling - transparent background, no border, no elevation
            MaterialCardView categoryCard = (MaterialCardView) categoryView.findViewById(R.id.categoryCard);
            categoryCard.setCardBackgroundColor(android.graphics.Color.TRANSPARENT);
            categoryCard.setStrokeWidth(0);
            categoryCard.setCardElevation(0f);

            // Set image with scaleType fitCenter so transparent PNG/WEBP renders cleanly
            ImageView categoryIcon = (ImageView) categoryView.findViewById(R.id.categoryIcon);
            categoryIcon.setImageResource(categoryDisplay.imageResId);
            categoryIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            // Set category name
            TextView categoryName = (TextView) categoryView.findViewById(R.id.categoryName);
            categoryName.setText(categoryDisplay.displayName);

            // Setup click listener (use local reference to avoid repeated field access)
            final aeu.ratha.beckie_deal_app.data.models.Category category = categoryDisplay.category;
            final String displayName = categoryDisplay.displayName;
            
            categoryView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CategoryDetailActivity.class);
                intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY, displayName);
                intent.putExtra(CategoryDetailActivity.EXTRA_HERO_IMAGE, category.getImage());
                intent.putExtra("CATEGORY_ID", category.getId());
                intent.putExtra("CATEGORY_SLUG", category.getSlug());
                startActivity(intent);
            });

            categoriesContainer.addView(categoryView);
        }
    }

    private void setupProducts() {
        RecyclerView productsGrid = findViewById(R.id.productsGrid);
        if (productsGrid == null) return;

        // PERF: Enable RecyclerView high-performance flags
        productsGrid.setHasFixedSize(true);
        productsGrid.setItemViewCacheSize(20);
        productsGrid.setItemAnimator(null);
        productsGrid.setNestedScrollingEnabled(false);

        boolean isListView = ViewModePreferences.isListView(this);
        int gridColumns = isListView ? 1 : ScreenUtils.productGridColumns(this);

        if (productAdapter == null) {
            // First setup: create the adapter and attach a GridLayoutManager.
            // nestedScrollingEnabled=false (set in XML) lets the outer NestedScrollView
            // control the scroll, so the whole page scrolls as one — smooth and natural.
            GridLayoutManager layoutManager = new GridLayoutManager(this, gridColumns);
            productsGrid.setLayoutManager(layoutManager);
            productAdapter = new ProductGridAdapter(this, products, wishlistRepository, cartRepository);
            productAdapter.setListMode(isListView);
            productsGrid.setAdapter(productAdapter);
            
            // Restore scroll position AFTER adapter is set
            productsGrid.post(() -> restoreScrollPositionAfterRender());
        } else {
            // onResume path: apply current view mode + refresh to pick up wishlist changes
            GridLayoutManager layoutManager = (GridLayoutManager) productsGrid.getLayoutManager();
            if (layoutManager != null) layoutManager.setSpanCount(gridColumns);
            productAdapter.setListMode(isListView);
            productAdapter.notifyDataSetChanged();
            
            // Restore scroll position AFTER data is refreshed
            productsGrid.post(() -> restoreScrollPositionAfterRender());
        }

        setupViewModeToggle();
    }

    /** Wires the grid/list toggle button next to "Featured Products". */
    private void setupViewModeToggle() {
        viewModeIcon = findViewById(R.id.viewModeIcon);
        View viewModeBtn = findViewById(R.id.viewModeButton);
        updateViewModeIcon();
        if (viewModeBtn != null) viewModeBtn.setOnClickListener(v -> {
            RecyclerView productsGrid = findViewById(R.id.productsGrid);
            ViewModeAnimator.transform(productsGrid, viewModeBtn, viewModeIcon, () -> {
                boolean newListView = !ViewModePreferences.isListView(this);
                ViewModePreferences.setListView(this, newListView);
                updateViewModeIcon();

                if (productsGrid != null) {
                    GridLayoutManager layoutManager = (GridLayoutManager) productsGrid.getLayoutManager();
                    if (layoutManager != null) {
                        int columns = newListView ? 1 : ScreenUtils.productGridColumns(this);
                        layoutManager.setSpanCount(columns);
                    }
                }
                if (productAdapter != null) productAdapter.setListMode(newListView);
            });
        });
        UiAnimations.pressBounce(viewModeBtn);
    }

    /** Swap the toggle button's icon to reflect the current mode (shows the icon for the OTHER mode to switch to). */
    private void updateViewModeIcon() {
        if (viewModeIcon == null) return;
        boolean isListView = ViewModePreferences.isListView(this);
        viewModeIcon.setImageResource(isListView ? R.drawable.grid_view_24 : R.drawable.event_list_24);
    }

    // ── New Collection horizontal scroll ──────────────────────────────────────

    private void setupNewCollectionSection() {
        TextView viewAll = findViewById(R.id.viewAllNewCollection);
        if (viewAll != null) viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductListActivity.class);
            intent.putExtra(ProductListActivity.EXTRA_MODE, ProductListActivity.MODE_NEW_COLLECTION);
            startActivity(intent);
        });
        UiAnimations.pressBounce(viewAll);

        LinearLayout container = findViewById(R.id.newCollectionContainer);
        if (container == null) return;
        container.removeAllViews();

        // New Collection = high-end products (price >= $800) from API, show first 6
        List<Product> newItems = new ArrayList<>();
        for (Product p : products) {
            double price = parsePrice(p.price);
            if (price >= 800) newItems.add(p);
        }
        // Sort by price descending (newest/flagship feel)
        java.util.Collections.sort(newItems, (a, b) ->
                Double.compare(parsePrice(b.price), parsePrice(a.price)));

        int limit = Math.min(newItems.size(), 6);
        for (int i = 0; i < limit; i++) {
            addHorizontalProductCard(container, newItems.get(i));
        }
    }

    // ── Coupon Section ────────────────────────────────────────────────────────
    // DEPRECATED - Coupons now shown in promotions carousel
    
    @Deprecated
    private void setupCouponSection() {
        // Removed - promotions carousel now displays all promotions with codes
    }

    // ── Top Selling horizontal scroll ─────────────────────────────────────────
    // DEPRECATED - Top selling section removed from home page
    
    @Deprecated
    private void setupTopSellingSection() {
        // Removed - top selling section no longer displayed on home page
    }

    private void addHorizontalProductCard(LinearLayout container, Product product) {
        View view = LayoutInflater.from(this)
                .inflate(R.layout.item_product_horizontal, container, false);

        ImageView imgView   = view.findViewById(R.id.productImage);
        ImageView favIcon   = view.findViewById(R.id.favoriteIcon);
        TextView  nameView  = view.findViewById(R.id.productName);
        TextView  priceView = view.findViewById(R.id.productPrice);
        TextView  ratingView= view.findViewById(R.id.productRating);

        nameView.setText(product.name);
        priceView.setText(product.price);
        ratingView.setText(product.rating);

        // Check wishlist status via API
        product.isInWishlist = false; // Default
        if (product.id != null && !product.id.isEmpty()) {
            wishlistRepository.checkProductInWishlist(product.id, new WishlistRepository.CheckCallback() {
                @Override
                public void onResult(boolean inWishlist) {
                    runOnUiThread(() -> {
                        product.isInWishlist = inWishlist;
                        favIcon.setImageResource(inWishlist ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                        favIcon.setColorFilter(ContextCompat.getColor(MainActivity.this,
                                inWishlist ? R.color.primary : R.color.text_secondary));
                    });
                }
                
                @Override
                public void onError(String error) {
                    // Keep default false
                }
            });
        }
        
        favIcon.setImageResource(product.isInWishlist ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        favIcon.setColorFilter(ContextCompat.getColor(this,
                product.isInWishlist ? R.color.primary : R.color.text_secondary));

        Glide.with(this).load(product.imageUrl).centerCrop()
             .placeholder(R.drawable.placeholder_image).into(imgView);

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

        favIcon.setOnClickListener(v -> {
            // Check authentication before modifying wishlist
            if (!aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.requireAuthForWishlist((android.app.Activity) this)) {
                return; // Prompt shown, operation cancelled
            }
            
            if (product.id == null || product.id.isEmpty()) {
                Toast.makeText(this, "Product ID not available", Toast.LENGTH_SHORT).show();
                return;
            }
            
            favIcon.setEnabled(false);
            
            if (!product.isInWishlist) {
                // Add to wishlist
                wishlistRepository.addToWishlist(product.id, 1, new WishlistRepository.WishlistItemCallback() {
                    @Override
                    public void onSuccess(WishlistItemResponse response) {
                        runOnUiThread(() -> {
                            product.isInWishlist = true;
                            if (response.getData() != null) {
                                product.wishlistItemId = response.getData().getId();
                            }
                            favIcon.setImageResource(R.drawable.ic_favorite);
                            favIcon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.primary));
                            Toast.makeText(MainActivity.this, "Added to Wishlist", Toast.LENGTH_SHORT).show();
                            favIcon.setEnabled(true);
                        });
                    }
                    
                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Failed to add", Toast.LENGTH_SHORT).show();
                            favIcon.setEnabled(true);
                        });
                    }
                });
            } else {
                // Remove from wishlist
                if (product.wishlistItemId > 0) {
                    wishlistRepository.removeFromWishlist(product.wishlistItemId, new WishlistRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                product.isInWishlist = false;
                                product.wishlistItemId = 0;
                                favIcon.setImageResource(R.drawable.ic_favorite_border);
                                favIcon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.text_secondary));
                                Toast.makeText(MainActivity.this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                                favIcon.setEnabled(true);
                            });
                        }
                        
                        @Override
                        public void onError(String error) {
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "Failed to remove", Toast.LENGTH_SHORT).show();
                                favIcon.setEnabled(true);
                            });
                        }
                    });
                } else {
                    product.isInWishlist = false;
                    favIcon.setImageResource(R.drawable.ic_favorite_border);
                    favIcon.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.text_secondary));
                    favIcon.setEnabled(true);
                }
            }
        });

        container.addView(view);
    }

    private double parsePrice(String price) {
        try {
            return Double.parseDouble(price.replace("$", "").replace(",", ""));
        } catch (NumberFormatException e) { return 0; }
    }

    // ── Auto-scroll logic ─────────────────────────────────────────────────────

    private void setupAutoScroll() {
        autoScrollHandler = new Handler(Looper.getMainLooper());

        newCollectionScroll = findViewById(R.id.newCollectionScroll);

        // Pause auto-scroll when user touches, resume when released
        if (newCollectionScroll != null) {
            newCollectionScroll.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isNewCollectionTouched = true;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isNewCollectionTouched = false;
                        // Resume after a pause from where user left
                        autoScrollHandler.postDelayed(newCollectionRunnable, AUTO_SCROLL_PAUSE);
                        break;
                }
                return false; // allow normal scroll handling
            });
        }

        startAutoScroll();
    }

    private void startAutoScroll() {
        if (autoScrollHandler == null) return;
        autoScrollHandler.removeCallbacks(newCollectionRunnable);
        autoScrollHandler.postDelayed(newCollectionRunnable, AUTO_SCROLL_PAUSE);
    }

    private void stopAutoScroll() {
        if (autoScrollHandler == null) return;
        autoScrollHandler.removeCallbacks(newCollectionRunnable);
    }

    private final Runnable newCollectionRunnable = new Runnable() {
        @Override
        public void run() {
            if (isNewCollectionTouched || newCollectionScroll == null) return;

            LinearLayout container = findViewById(R.id.newCollectionContainer);
            if (container == null || container.getChildCount() == 0) return;

            int childCount = container.getChildCount();
            newCollectionScrollIndex++;

            // If we've scrolled past the last item, smoothly scroll back to start
            if (newCollectionScrollIndex >= childCount) {
                newCollectionScrollIndex = 0;
                newCollectionScroll.smoothScrollTo(0, 0);
            } else {
                View child = container.getChildAt(newCollectionScrollIndex);
                newCollectionScroll.smoothScrollTo(child.getLeft(), 0);
            }

            // Schedule next scroll
            autoScrollHandler.postDelayed(this, AUTO_SCROLL_PAUSE);
        }
    };

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.HOME);
    }

    private void updateCartBadge() {
        BottomNavHelper.updateCartBadge(this);
    }

    public static class Product {
        public String id;            // Product ID (UUID) from API
        public String name, price, rating, imageUrl;
        public String sku;           // e.g. "BECKIE6036"
        public boolean inStock;      // true = Available In Stock
        public int warrantyYears;    // e.g. 1
        public String specs;         // short bullet specs, newline-separated, e.g. "-13.6\" Liquid Retina\n-Weight: 1.24Kg"
        public String freeItems;     // e.g. "Free 8 items"
        public boolean isInWishlist;
        public int wishlistItemId;   // Wishlist item ID for API removal

        // Minimal constructor (legacy — no SKU/stock/warranty/specs)
        public Product(String n, String p, String r, boolean w, String i) {
            this("", n, p, r, w, i, "", true, 1, "", "");
        }
        
        // Simplified constructor with ID only (for wishlist display)
        public Product(String id, String n, String p, String i, String r) {
            this(id, n, p, r, false, i, "", true, 1, "", "");
        }

        // Full constructor (without ID - legacy)
        public Product(String n, String p, String r, boolean w, String i,
                String sku, boolean inStock, int warranty, String specs, String freeItems) {
            this("", n, p, r, w, i, sku, inStock, warranty, specs, freeItems);
        }
        
        // Complete constructor (with ID)
        public Product(String id, String n, String p, String r, boolean w, String i,
                String sku, boolean inStock, int warranty, String specs, String freeItems) {
            this.id = id;
            name=n; price=p; rating=r; isInWishlist=w; imageUrl=i;
            this.sku=sku; this.inStock=inStock; this.warrantyYears=warranty;
            this.specs=specs; this.freeItems=freeItems;
        }
    }
    /**
     * Wrapper class combining API Category with UI display properties.
     */
    private static class CategoryDisplay {
        aeu.ratha.beckie_deal_app.data.models.Category category;
        int imageResId;  // Changed from iconResId to imageResId (webp image)
        String displayName;
        int displayOrder;
        
        CategoryDisplay(aeu.ratha.beckie_deal_app.data.models.Category category, 
                       int imageResId, String displayName, int displayOrder) {
            this.category = category;
            this.imageResId = imageResId;
            this.displayName = displayName;
            this.displayOrder = displayOrder;
        }
    }
}
