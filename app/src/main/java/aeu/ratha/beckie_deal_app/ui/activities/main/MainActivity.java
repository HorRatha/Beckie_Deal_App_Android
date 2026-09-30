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
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.animations.ViewModeAnimator;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.preferences.ViewModePreferences;

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

    // Auto-scroll for horizontal sections
    private Handler autoScrollHandler;
    private HorizontalScrollView newCollectionScroll;
    private boolean isNewCollectionTouched = false;
    private int newCollectionScrollIndex = 0;
    private static final long AUTO_SCROLL_PAUSE = 2500; // 2.5 seconds
    private static final int SCROLL_DURATION = 400; // smooth scroll duration in ms

    // Banner carousels
    private ViewPager2 newArrivalsBannerPager;
    private Handler newArrivalsAutoScrollHandler;
    private Runnable newArrivalsAutoScrollRunnable;
    private boolean isNewArrivalsTouched = false;
    
    // Promotion banner auto-scroll
    private ViewPager2 promotionBannerPager;
    private Handler promotionAutoScrollHandler;
    private Runnable promotionAutoScrollRunnable;
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
        fetchPromotions(); // Fetch from API - promotions displayed automatically when ready
        drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupSearchBar();
        setupPromotionsCarousel(); // Setup arrow controls (data displayed when fetched)
        setupProducts();
        setupNewArrivalsBanner();
        setupNewCollectionSection();
        setupCouponSection();
        setupBottomNavigation();
        setupAutoScroll();
        setupEntranceAnimation();
    }

    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
        UiAnimations.fadeSlideUp(findViewById(R.id.searchBar), 60);
        UiAnimations.fadeSlideUp(findViewById(R.id.promotionBannerCard), 80);
        UiAnimations.fadeSlideUp(findViewById(R.id.categoriesSectionHeader), 150);
        UiAnimations.fadeSlideUp(findViewById(R.id.newCollectionSectionHeader), 190);
        UiAnimations.fadeSlideUp(findViewById(R.id.newArrivalsBannerViewPager), 210);
        UiAnimations.fadeSlideUp(findViewById(R.id.featuredProductsHeader), 270);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupProducts();
        startAutoScroll();
        startBannerAutoScroll();
        startPromotionAutoScroll();
        updateCartBadge();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
        stopBannerAutoScroll();
        stopPromotionAutoScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoScroll();
        stopBannerAutoScroll();
        stopPromotionAutoScroll();
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
    }
    
    /**
     * Fetch products from API.
     * Endpoint: GET /api/v1/products?limit=100&offset=0
     */
    private void fetchProducts() {
        Log.d(TAG, "=== FETCHING PRODUCTS FROM API ===");
        
        // Fetch all active products (limit 100 for now)
        productRepository.fetchProducts(100, 0, false, 
            new aeu.ratha.beckie_deal_app.data.repository.ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts, int total) {
                Log.d(TAG, "✓ API SUCCESS: Received " + apiProducts.size() + " products (total: " + total + ")");
                
                // Convert API products to UI products
                products.clear();
                products.addAll(aeu.ratha.beckie_deal_app.utils.helpers.ProductMapper.toUiProducts(apiProducts));
                
                Log.d(TAG, "=== CONVERTED TO " + products.size() + " UI PRODUCTS ===");
                
                // Wishlist state will be loaded on-demand when needed
                // No need to sync here - ProductGridAdapter handles it
                
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
     * Fetch categories from API.
     * Endpoint: GET /api/v1/master/categories?limit=20&offset=0
     */
    private void fetchCategories() {
        Log.d(TAG, "=== FETCHING CATEGORIES FROM API ===");
        
        categoryRepository.fetchActiveCategories(20, 0, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Category> categories, int total) {
                Log.d(TAG, "✓ API SUCCESS: Received " + categories.size() + " categories");
                
                apiCategories.clear();
                categoryDisplayList.clear();
                
                if (categories != null) {
                    apiCategories.addAll(categories);
                    
                    // Map API categories to UI display format
                    for (aeu.ratha.beckie_deal_app.data.models.Category category : categories) {
                        int iconRes = getCategoryIcon(category.getName());
                        int accentIconRes = getCategoryAccentIcon(category.getName());
                        boolean isHighlighted = category.isFeatured();
                        
                        Log.d(TAG, "  - " + category.getName() + " (ID: " + category.getId() + 
                            ", slug: " + category.getSlug() + ", featured: " + isHighlighted + ")");
                        
                        categoryDisplayList.add(new CategoryDisplay(
                            category, 
                            iconRes, 
                            accentIconRes,
                            isHighlighted
                        ));
                    }
                }
                
                Log.d(TAG, "=== RENDERING " + categoryDisplayList.size() + " CATEGORIES ===");
                setupCategories();
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "✗ API ERROR: " + errorMessage);
                initFallbackCategories();
                setupCategories();
            }
        });
    }
    
    /**
     * Map category name from API to appropriate icon resource.
     * This converts API category names like "Computer Components" to UI icons.
     */
    private int getCategoryIcon(String categoryName) {
        String name = categoryName.toLowerCase();
        if (name.contains("laptop") || name.contains("computer")) return R.drawable.devices_24;
        if (name.contains("phone") || name.contains("mobile")) return R.drawable.mobile_24;
        if (name.contains("accessor")) return R.drawable.widgets_24;
        if (name.contains("gaming") || name.contains("game")) return R.drawable.sports_esports_24;
        if (name.contains("wearable") || name.contains("watch")) return R.drawable.devices_wearables_24;
        if (name.contains("monitor") || name.contains("display")) return R.drawable.devices_24;
        if (name.contains("storage") || name.contains("drive")) return R.drawable.widgets_24;
        if (name.contains("network")) return R.drawable.widgets_24;
        return R.drawable.widgets_24; // Default icon
    }
    
    /**
     * Get decorative accent icon for category.
     * These are visual enhancements shown on category cards.
     */
    private int getCategoryAccentIcon(String categoryName) {
        String name = categoryName.toLowerCase();
        if (name.contains("laptop") || name.contains("computer")) return R.drawable.category_accent_laptop;
        if (name.contains("phone") || name.contains("mobile")) return R.drawable.category_accent_phone;
        if (name.contains("accessor")) return R.drawable.category_accent_accessories;
        if (name.contains("gaming") || name.contains("game")) return R.drawable.category_accent_gaming;
        if (name.contains("wearable") || name.contains("watch")) return R.drawable.category_accent_wearables;
        return R.drawable.decorative_sparkle_outline; // Default
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



    private void startBannerAutoScroll() {
        if (newArrivalsAutoScrollHandler != null && newArrivalsAutoScrollRunnable != null) {
            newArrivalsAutoScrollHandler.removeCallbacks(newArrivalsAutoScrollRunnable);
            newArrivalsAutoScrollHandler.postDelayed(newArrivalsAutoScrollRunnable, BANNER_SCROLL_DELAY);
        }
    }

    private void stopBannerAutoScroll() {
        if (newArrivalsAutoScrollHandler != null && newArrivalsAutoScrollRunnable != null) {
            newArrivalsAutoScrollHandler.removeCallbacks(newArrivalsAutoScrollRunnable);
        }
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
                resetCarouselAutoScroll(isHero);
            });
        }
        if (arrowRight != null) {
            arrowRight.setOnClickListener(v -> {
                int current = viewPager.getCurrentItem();
                viewPager.setCurrentItem(current < count - 1 ? current + 1 : 0, true);
                resetCarouselAutoScroll(isHero);
            });
        }
        UiAnimations.pressBounce(arrowLeft, arrowRight);

        // Auto-scroll for new arrivals (hero banner removed)
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager == null) return;
                if (isNewArrivalsTouched) return;
                int next = (viewPager.getCurrentItem() + 1) % count;
                viewPager.setCurrentItem(next, true);
                handler.postDelayed(this, BANNER_SCROLL_DELAY);
            }
        };

        newArrivalsAutoScrollHandler = handler;
        newArrivalsAutoScrollRunnable = runnable;

        // Pause on user drag
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    isNewArrivalsTouched = true;
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    isNewArrivalsTouched = false;
                    resetCarouselAutoScroll(false);
                }
            }
        });
    }

    private void resetCarouselAutoScroll(boolean isHero) {
        // Only new arrivals banner exists now (hero banner removed)
        if (newArrivalsAutoScrollHandler != null && newArrivalsAutoScrollRunnable != null) {
            newArrivalsAutoScrollHandler.removeCallbacks(newArrivalsAutoScrollRunnable);
            newArrivalsAutoScrollHandler.postDelayed(newArrivalsAutoScrollRunnable, BANNER_SCROLL_DELAY);
        }
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
        TextView viewAllCategories = findViewById(R.id.viewAllCategories);
        if (viewAllCategories != null) {
            viewAllCategories.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CategoriesActivity.class)));
        }
        UiAnimations.pressBounce(viewAllCategories);

        LinearLayout categoriesContainer = findViewById(R.id.categoriesContainer);
        if (categoriesContainer == null) return;
        categoriesContainer.removeAllViews();

        // Render categories in horizontal scrollable layout (like chips)
        categoriesContainer.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < categoryDisplayList.size(); i++) {
            final CategoryDisplay categoryDisplay = categoryDisplayList.get(i);
            final aeu.ratha.beckie_deal_app.data.models.Category category = categoryDisplay.category;

            View categoryView = LayoutInflater.from(this).inflate(R.layout.category_item, categoriesContainer, false);
            
            // Set wrap_content for horizontal layout
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            categoryView.setLayoutParams(itemParams);

            MaterialCardView categoryCard = categoryView.findViewById(R.id.categoryCard);
            ImageView categoryIcon = categoryView.findViewById(R.id.categoryIcon);
            TextView categoryName = categoryView.findViewById(R.id.categoryName);
            ImageView decorativeIcon = categoryView.findViewById(R.id.categoryDecorativeIcon);

            categoryIcon.setImageResource(categoryDisplay.iconResId);
            categoryName.setText(category.getName());

            // Apply highlight style for featured categories
            if (categoryDisplay.isHighlighted) {
                categoryCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.badge_bg));
                categoryCard.setStrokeColor(ContextCompat.getColor(this, R.color.primary));
                categoryCard.setStrokeWidth(dpToPx(2));
            }

            categoryView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CategoryDetailActivity.class);
                // Use category image from API
                String heroUrl = category.getImage();
                
                // Map home-screen category names to detail-page names
                String detailName = category.getName().equals("Phones") ? "Smartphones" : category.getName();

                intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY,   detailName);
                intent.putExtra(CategoryDetailActivity.EXTRA_HERO_IMAGE, heroUrl);
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

        // Wishlist state managed by ProductGridAdapter
        // No need to sync here

        boolean isListView = ViewModePreferences.isListView(this);

        if (productAdapter == null) {
            // First setup: create the adapter and attach a GridLayoutManager.
            // nestedScrollingEnabled=false (set in XML) lets the outer NestedScrollView
            // control the scroll, so the whole page scrolls as one — smooth and natural.
            GridLayoutManager layoutManager = new GridLayoutManager(this, isListView ? 1 : 2);
            productsGrid.setLayoutManager(layoutManager);
            productAdapter = new ProductGridAdapter(this, products, wishlistRepository, cartRepository);
            productAdapter.setListMode(isListView);
            productsGrid.setAdapter(productAdapter);
        } else {
            // onResume path: apply current view mode + refresh to pick up wishlist changes
            GridLayoutManager layoutManager = (GridLayoutManager) productsGrid.getLayoutManager();
            if (layoutManager != null) layoutManager.setSpanCount(isListView ? 1 : 2);
            productAdapter.setListMode(isListView);
            productAdapter.notifyDataSetChanged();
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
                    if (layoutManager != null) layoutManager.setSpanCount(newListView ? 1 : 2);
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

    private void setupNewArrivalsBanner() {
        // New Arrivals banner carousel: 3 slides with Pinterest-style images
        String[][] newArrivalTexts = {
                {"✦ JUST LANDED", "New Arrivals", "Fresh tech drops, every week"},
                {"🚀 TRENDING", "Top Picks", "Most popular this month"},
                {"💎 PREMIUM", "Exclusive Deals", "Members-only discounts inside"}
        };
        int[] newArrivalBgs = {
                R.drawable.banner_gradient_4,
                R.drawable.banner_gradient_5,
                R.drawable.banner_gradient_6
        };
        // Pinterest-style banner images for new arrivals
        String[] newArrivalImages = {
                "https://images.unsplash.com/photo-1468495244123-6c6c332eeece?w=800&h=400&fit=crop&q=80",
                "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=800&h=400&fit=crop&q=80",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&h=400&fit=crop&q=80"
        };

        newArrivalsBannerPager = findViewById(R.id.newArrivalsBannerViewPager);
        setupBannerCarousel(
                newArrivalsBannerPager,
                findViewById(R.id.newArrivalsArrowLeft),
                findViewById(R.id.newArrivalsArrowRight),
                findViewById(R.id.newArrivalsDotsContainer),
                newArrivalTexts, newArrivalBgs, newArrivalImages,
                false // not hero banner
        );
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
        int iconResId;
        int accentIconResId;
        boolean isHighlighted;
        
        CategoryDisplay(aeu.ratha.beckie_deal_app.data.models.Category category, 
                       int iconResId, int accentIconResId, boolean isHighlighted) {
            this.category = category;
            this.iconResId = iconResId;
            this.accentIconResId = accentIconResId;
            this.isHighlighted = isHighlighted;
        }
    }
}
