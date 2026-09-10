package aeu.ratha.beckie_deal_app.ui.activities.main;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.animations.ViewModeAnimator;
import aeu.ratha.beckie_deal_app.utils.preferences.ViewModePreferences;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.managers.FavoriteManager;
import aeu.ratha.beckie_deal_app.ui.adapters.ProductGridAdapter;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductListActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoriesActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;
import aeu.ratha.beckie_deal_app.data.repository.CategoryRepository;

import android.content.Intent;
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

    // Auto-scroll for horizontal sections
    private Handler autoScrollHandler;
    private HorizontalScrollView newCollectionScroll;
    private HorizontalScrollView topSellingScroll;
    private boolean isNewCollectionTouched = false;
    private boolean isTopSellingTouched = false;
    private int newCollectionScrollIndex = 0;
    private int topSellingScrollIndex = 0;
    private static final long AUTO_SCROLL_PAUSE = 2500; // 2.5 seconds
    private static final int SCROLL_DURATION = 400; // smooth scroll duration in ms

    // Banner carousels
    private ViewPager2 bannerViewPager;
    private ViewPager2 newArrivalsBannerPager;
    private Handler bannerAutoScrollHandler;
    private Runnable bannerAutoScrollRunnable;
    private Handler newArrivalsAutoScrollHandler;
    private Runnable newArrivalsAutoScrollRunnable;
    private boolean isBannerTouched = false;
    private boolean isNewArrivalsTouched = false;
    private static final long BANNER_SCROLL_DELAY = 4500; // 4.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeData();
        initRepository();
        fetchCategories(); // Fetch from API
        fetchProducts(); // Fetch from API
        drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupSearchBar();
        setupHeroBanner();
        setupProducts();
        setupNewArrivalsBanner();
        setupNewCollectionSection();
        setupTopSellingSection();
        setupBottomNavigation();
        setupAutoScroll();
        setupEntranceAnimation();
    }

    /** Fades/slides the top bar, search bar, hero banner and section headers into
     *  place on load. productsGrid (the RecyclerView of product cards) is left
     *  completely untouched — same for the horizontal product-card rows and
     *  category chips, since those render product-like cards too. */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
        UiAnimations.fadeSlideUp(findViewById(R.id.searchBar), 60);
        UiAnimations.fadeSlideUp(findViewById(R.id.bannerCard), 100);
        UiAnimations.fadeSlideUp(findViewById(R.id.categoriesSectionHeader), 150);
        UiAnimations.fadeSlideUp(findViewById(R.id.newCollectionSectionHeader), 190);
        UiAnimations.fadeSlideUp(findViewById(R.id.topSellingSectionHeader), 230);
        UiAnimations.fadeSlideUp(findViewById(R.id.featuredProductsHeader), 270);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupProducts();
        startAutoScroll();
        startBannerAutoScroll();
        updateCartBadge();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScroll();
        stopBannerAutoScroll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAutoScroll();
        stopBannerAutoScroll();
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
                
                // Sync favorites
                for (Product p : products) {
                    p.isFavorite = FavoriteManager.getInstance().isFavorite(p);
                }
                
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

    private void setupTopBar() {
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

    private void setupHeroBanner() {
        // Hero banner data: 3 slides with Pinterest-style images
        String[][] heroBannerTexts = {
                {"🔥 HOT DEAL", "Summer Sale", "Up to 50% off on all items"},
                {"⚡ LIMITED", "New Arrivals", "Latest tech gadgets are here"},
                {"🎁 EXCLUSIVE", "Flash Deal", "Free shipping on orders $99+"}
        };
        int[] heroBannerBgs = {
                R.drawable.banner_gradient_1,
                R.drawable.banner_gradient_2,
                R.drawable.banner_gradient_3
        };
        // Pinterest-style banner images
        String[] heroBannerImages = {
                "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800&h=400&fit=crop&q=80",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&h=400&fit=crop&q=80",
                "https://images.unsplash.com/photo-1491553895911-0055eca6402d?w=800&h=400&fit=crop&q=80"
        };

        bannerViewPager = findViewById(R.id.bannerViewPager);
        setupBannerCarousel(
                bannerViewPager,
                findViewById(R.id.bannerArrowLeft),
                findViewById(R.id.bannerArrowRight),
                findViewById(R.id.bannerDotsContainer),
                heroBannerTexts, heroBannerBgs, heroBannerImages,
                true // isHeroBanner flag
        );
    }

    private void startBannerAutoScroll() {
        if (bannerAutoScrollHandler != null && bannerAutoScrollRunnable != null) {
            bannerAutoScrollHandler.removeCallbacks(bannerAutoScrollRunnable);
            bannerAutoScrollHandler.postDelayed(bannerAutoScrollRunnable, BANNER_SCROLL_DELAY);
        }
        if (newArrivalsAutoScrollHandler != null && newArrivalsAutoScrollRunnable != null) {
            newArrivalsAutoScrollHandler.removeCallbacks(newArrivalsAutoScrollRunnable);
            newArrivalsAutoScrollHandler.postDelayed(newArrivalsAutoScrollRunnable, BANNER_SCROLL_DELAY);
        }
    }

    private void stopBannerAutoScroll() {
        if (bannerAutoScrollHandler != null && bannerAutoScrollRunnable != null) {
            bannerAutoScrollHandler.removeCallbacks(bannerAutoScrollRunnable);
        }
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

        // Auto-scroll
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (viewPager == null) return;
                if (isHero && isBannerTouched) return;
                if (!isHero && isNewArrivalsTouched) return;
                int next = (viewPager.getCurrentItem() + 1) % count;
                viewPager.setCurrentItem(next, true);
                handler.postDelayed(this, BANNER_SCROLL_DELAY);
            }
        };

        if (isHero) {
            bannerAutoScrollHandler = handler;
            bannerAutoScrollRunnable = runnable;
        } else {
            newArrivalsAutoScrollHandler = handler;
            newArrivalsAutoScrollRunnable = runnable;
        }

        // Pause on user drag
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    if (isHero) isBannerTouched = true;
                    else isNewArrivalsTouched = true;
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    if (isHero) isBannerTouched = false;
                    else isNewArrivalsTouched = false;
                    resetCarouselAutoScroll(isHero);
                }
            }
        });
    }

    private void resetCarouselAutoScroll(boolean isHero) {
        if (isHero) {
            if (bannerAutoScrollHandler != null && bannerAutoScrollRunnable != null) {
                bannerAutoScrollHandler.removeCallbacks(bannerAutoScrollRunnable);
                bannerAutoScrollHandler.postDelayed(bannerAutoScrollRunnable, BANNER_SCROLL_DELAY);
            }
        } else {
            if (newArrivalsAutoScrollHandler != null && newArrivalsAutoScrollRunnable != null) {
                newArrivalsAutoScrollHandler.removeCallbacks(newArrivalsAutoScrollRunnable);
                newArrivalsAutoScrollHandler.postDelayed(newArrivalsAutoScrollRunnable, BANNER_SCROLL_DELAY);
            }
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

        // Sync favorite state for all items
        for (Product p : products) {
            p.isFavorite = FavoriteManager.getInstance().isFavorite(p);
        }

        boolean isListView = ViewModePreferences.isListView(this);

        if (productAdapter == null) {
            // First setup: create the adapter and attach a GridLayoutManager.
            // nestedScrollingEnabled=false (set in XML) lets the outer NestedScrollView
            // control the scroll, so the whole page scrolls as one — smooth and natural.
            GridLayoutManager layoutManager = new GridLayoutManager(this, isListView ? 1 : 2);
            productsGrid.setLayoutManager(layoutManager);
            productAdapter = new ProductGridAdapter(this, products);
            productAdapter.setListMode(isListView);
            productsGrid.setAdapter(productAdapter);
        } else {
            // onResume path: apply current view mode + refresh to pick up favorite changes
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

    // ── Top Selling horizontal scroll ─────────────────────────────────────────

    private void setupTopSellingSection() {
        TextView viewAll = findViewById(R.id.viewAllTopSelling);
        if (viewAll != null) viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductListActivity.class);
            intent.putExtra(ProductListActivity.EXTRA_MODE, ProductListActivity.MODE_TOP_SELLING);
            startActivity(intent);
        });
        UiAnimations.pressBounce(viewAll);

        LinearLayout container = findViewById(R.id.topSellingContainer);
        if (container == null) return;
        container.removeAllViews();

        // Top Selling = highest rated products (rating >= 4.7) from API, show first 6
        List<Product> topItems = new ArrayList<>();
        for (Product p : products) {
            try {
                double rating = Double.parseDouble(p.rating);
                if (rating >= 4.7) topItems.add(p);
            } catch (NumberFormatException e) {
                // Skip products with invalid rating
            }
        }
        // Sort by rating descending
        java.util.Collections.sort(topItems, (a, b) -> {
            try {
                return Double.compare(Double.parseDouble(b.rating), Double.parseDouble(a.rating));
            } catch (NumberFormatException e) {
                return 0;
            }
        });

        int limit = Math.min(topItems.size(), 6);
        for (int i = 0; i < limit; i++) {
            addHorizontalProductCard(container, topItems.get(i));
        }
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

        product.isFavorite = FavoriteManager.getInstance().isFavorite(product);
        favIcon.setImageResource(product.isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        favIcon.setColorFilter(ContextCompat.getColor(this,
                product.isFavorite ? R.color.primary : R.color.text_secondary));

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
            product.isFavorite = !product.isFavorite;
            if (product.isFavorite) FavoriteManager.getInstance().addFavorite(product);
            else FavoriteManager.getInstance().removeFavorite(product);
            favIcon.setImageResource(product.isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            favIcon.setColorFilter(ContextCompat.getColor(this,
                    product.isFavorite ? R.color.primary : R.color.text_secondary));
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
        topSellingScroll = findViewById(R.id.topSellingScroll);

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

        if (topSellingScroll != null) {
            topSellingScroll.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isTopSellingTouched = true;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isTopSellingTouched = false;
                        autoScrollHandler.postDelayed(topSellingRunnable, AUTO_SCROLL_PAUSE);
                        break;
                }
                return false;
            });
        }

        startAutoScroll();
    }

    private void startAutoScroll() {
        if (autoScrollHandler == null) return;
        autoScrollHandler.removeCallbacks(newCollectionRunnable);
        autoScrollHandler.removeCallbacks(topSellingRunnable);
        autoScrollHandler.postDelayed(newCollectionRunnable, AUTO_SCROLL_PAUSE);
        autoScrollHandler.postDelayed(topSellingRunnable, AUTO_SCROLL_PAUSE);
    }

    private void stopAutoScroll() {
        if (autoScrollHandler == null) return;
        autoScrollHandler.removeCallbacks(newCollectionRunnable);
        autoScrollHandler.removeCallbacks(topSellingRunnable);
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

    private final Runnable topSellingRunnable = new Runnable() {
        @Override
        public void run() {
            if (isTopSellingTouched || topSellingScroll == null) return;

            LinearLayout container = findViewById(R.id.topSellingContainer);
            if (container == null || container.getChildCount() == 0) return;

            int childCount = container.getChildCount();
            topSellingScrollIndex++;

            // If we've scrolled past the last item, smoothly scroll back to start
            if (topSellingScrollIndex >= childCount) {
                topSellingScrollIndex = 0;
                topSellingScroll.smoothScrollTo(0, 0);
            } else {
                View child = container.getChildAt(topSellingScrollIndex);
                topSellingScroll.smoothScrollTo(child.getLeft(), 0);
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
        public int id;               // Product ID from API
        public String name, price, rating, imageUrl;
        public String sku;           // e.g. "BECKIE6036"
        public boolean inStock;      // true = Available In Stock
        public int warrantyYears;    // e.g. 1
        public String specs;         // short bullet specs, newline-separated, e.g. "-13.6\" Liquid Retina\n-Weight: 1.24Kg"
        public String freeItems;     // e.g. "Free 8 items"
        public boolean isFavorite;

        // Minimal constructor (legacy — no SKU/stock/warranty/specs)
        public Product(String n, String p, String r, boolean f, String i) {
            this(0, n, p, r, f, i, "", true, 1, "", "");
        }

        // Full constructor (without ID - legacy)
        public Product(String n, String p, String r, boolean f, String i,
                String sku, boolean inStock, int warranty, String specs, String freeItems) {
            this(0, n, p, r, f, i, sku, inStock, warranty, specs, freeItems);
        }
        
        // Complete constructor (with ID)
        public Product(int id, String n, String p, String r, boolean f, String i,
                String sku, boolean inStock, int warranty, String specs, String freeItems) {
            this.id = id;
            name=n; price=p; rating=r; isFavorite=f; imageUrl=i;
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
