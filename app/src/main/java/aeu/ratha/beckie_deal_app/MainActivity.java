package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

    private List<Product> products;
    private List<Category> categories;
    private AppDrawerHelper drawerHelper;
    private ProductGridAdapter productAdapter;

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

    // Banner carousel
    private ViewPager2 bannerViewPager;
    private Handler bannerAutoScrollHandler;
    private Runnable bannerAutoScrollRunnable;
    private boolean isBannerTouched = false;
    private static final long BANNER_SCROLL_DELAY = 4500; // 4.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeMockData();
        drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupSearchBar();
        setupHeroBanner();
        setupCategories();
        setupProducts();
        setupNewArrivalsBanner();
        setupNewCollectionSection();
        setupTopSellingSection();
        setupBottomNavigation();
        setupAutoScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupProducts();
        startAutoScroll();
        startBannerAutoScroll();
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

    private void initializeMockData() {
        // Build the flat product list from the full catalogue (all categories + brands)
        products = new ArrayList<>();
        for (java.util.Map<String, List<Product>> brandMap
                : CategoryDetailActivity.CATALOGUE.values()) {
            for (List<Product> list : brandMap.values()) {
                products.addAll(list);
            }
        }

        // Sync existing favorites
        for (Product p : products) {
            if (p.isFavorite) FavoriteManager.getInstance().addFavorite(p);
        }

        categories = new ArrayList<>();
        categories.add(new Category("Laptops", R.drawable.ic_cat_laptops, false));
        categories.add(new Category("Phones", R.drawable.ic_cat_phones, false));
        categories.add(new Category("Accessories", R.drawable.ic_cat_accessories, false));
        categories.add(new Category("Gaming", R.drawable.ic_cat_gaming, true));
        categories.add(new Category("Wearables", R.drawable.ic_cat_wearables, false));
    }

    private void setupTopBar() {
        // menuIcon click is handled by AppDrawerHelper
        View cartContainer = findViewById(R.id.cartContainer);
        TextView cartBadge = findViewById(R.id.cartBadge);

        if (cartContainer != null) cartContainer.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        updateCartBadge(cartBadge);
    }

    private void updateCartBadge(TextView badge) {
        if (badge == null) return;
        int count = CartManager.getInstance().getTotalUnits();
        badge.setText(String.valueOf(count));
        badge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    private void setupSearchBar() {
        View searchBar = findViewById(R.id.searchBar);
        if (searchBar != null) searchBar.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
    }

    private void setupHeroBanner() {
        bannerViewPager = findViewById(R.id.bannerViewPager);
        ImageView arrowLeft = findViewById(R.id.bannerArrowLeft);
        ImageView arrowRight = findViewById(R.id.bannerArrowRight);
        LinearLayout dotsContainer = findViewById(R.id.bannerDotsContainer);

        if (bannerViewPager == null) return;

        // Banner data: 3 slides
        int[][] bannerData = {
                // {backgroundDrawableRes, tag, title, subtitle}
                // We'll use int arrays for drawables and String arrays for text
        };

        String[][] bannerTexts = {
                {"🔥 HOT DEAL", "Summer Sale", "Up to 50% off on all items"},
                {"⚡ LIMITED", "New Arrivals", "Latest tech gadgets are here"},
                {"🎁 EXCLUSIVE", "Flash Deal", "Free shipping on orders $99+"}
        };

        int[] bannerBackgrounds = {
                R.drawable.banner_gradient_1,
                R.drawable.banner_gradient_2,
                R.drawable.banner_gradient_3
        };

        // Set up adapter
        bannerViewPager.setAdapter(new RecyclerView.Adapter<BannerViewHolder>() {
            @NonNull
            @Override
            public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_banner, parent, false);
                return new BannerViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
                holder.background.setBackgroundResource(bannerBackgrounds[position]);
                holder.tag.setText(bannerTexts[position][0]);
                holder.title.setText(bannerTexts[position][1]);
                holder.subtitle.setText(bannerTexts[position][2]);
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        // Dot indicators
        View[] dots = new View[3];
        for (int i = 0; i < 3; i++) {
            dots[i] = new View(this);
            int size = (i == 0) ? dpToPx(8) : dpToPx(6);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            dots[i].setLayoutParams(lp);
            dots[i].setBackgroundResource(i == 0 ? R.drawable.dot_active : R.drawable.dot_inactive);
            dotsContainer.addView(dots[i]);
        }

        // Page change listener for dots
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
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

        // Arrow navigation
        if (arrowLeft != null) {
            arrowLeft.setOnClickListener(v -> {
                int current = bannerViewPager.getCurrentItem();
                if (current > 0) {
                    bannerViewPager.setCurrentItem(current - 1, true);
                } else {
                    bannerViewPager.setCurrentItem(2, true); // wrap to last
                }
                resetBannerAutoScroll();
            });
        }

        if (arrowRight != null) {
            arrowRight.setOnClickListener(v -> {
                int current = bannerViewPager.getCurrentItem();
                if (current < 2) {
                    bannerViewPager.setCurrentItem(current + 1, true);
                } else {
                    bannerViewPager.setCurrentItem(0, true); // wrap to first
                }
                resetBannerAutoScroll();
            });
        }

        // Auto-scroll setup
        bannerAutoScrollHandler = new Handler(Looper.getMainLooper());
        bannerAutoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerViewPager == null || isBannerTouched) return;
                int next = (bannerViewPager.getCurrentItem() + 1) % 3;
                bannerViewPager.setCurrentItem(next, true);
                bannerAutoScrollHandler.postDelayed(this, BANNER_SCROLL_DELAY);
            }
        };

        // Pause auto-scroll on touch
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    isBannerTouched = true;
                    stopBannerAutoScroll();
                } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    isBannerTouched = false;
                    startBannerAutoScroll();
                }
            }
        });
    }

    private void startBannerAutoScroll() {
        if (bannerAutoScrollHandler != null && bannerAutoScrollRunnable != null) {
            bannerAutoScrollHandler.removeCallbacks(bannerAutoScrollRunnable);
            bannerAutoScrollHandler.postDelayed(bannerAutoScrollRunnable, BANNER_SCROLL_DELAY);
        }
    }

    private void stopBannerAutoScroll() {
        if (bannerAutoScrollHandler != null && bannerAutoScrollRunnable != null) {
            bannerAutoScrollHandler.removeCallbacks(bannerAutoScrollRunnable);
        }
    }

    private void resetBannerAutoScroll() {
        stopBannerAutoScroll();
        startBannerAutoScroll();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        View background;
        TextView tag, title, subtitle;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.bannerBackground);
            tag = itemView.findViewById(R.id.bannerTag);
            title = itemView.findViewById(R.id.bannerTitle);
            subtitle = itemView.findViewById(R.id.bannerSubtitle);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void setupCategories() {
        TextView viewAllCategories = findViewById(R.id.viewAllCategories);
        if (viewAllCategories != null) {
            viewAllCategories.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CategoriesActivity.class)));
        }

        LinearLayout categoriesContainer = findViewById(R.id.categoriesContainer);
        if (categoriesContainer == null) return;
        categoriesContainer.removeAllViews();

        for (Category category : categories) {
            View categoryView = LayoutInflater.from(this).inflate(R.layout.category_item, categoriesContainer, false);
            MaterialCardView categoryCard = categoryView.findViewById(R.id.categoryCard);
            ImageView categoryIcon = categoryView.findViewById(R.id.categoryIcon);
            TextView categoryName = categoryView.findViewById(R.id.categoryName);

            categoryIcon.setImageResource(category.iconResId);
            categoryName.setText(category.name);

            if (category.isHighlighted) {
                categoryCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.badge_bg));
                categoryCard.setStrokeColor(ContextCompat.getColor(this, R.color.primary));
            }

            categoryView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CategoryDetailActivity.class);
                String heroUrl = "";
                if      (category.name.equals("Laptops"))     heroUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&h=400&fit=crop";
                else if (category.name.equals("Phones"))      heroUrl = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=400&fit=crop";
                else if (category.name.equals("Accessories")) heroUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&h=400&fit=crop";
                else if (category.name.equals("Gaming"))      heroUrl = "https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=600&h=400&fit=crop";
                else if (category.name.equals("Wearables"))   heroUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&h=400&fit=crop";

                // Map home-screen category names to detail-page names
                String detailName = category.name.equals("Phones") ? "Smartphones" : category.name;

                intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY,   detailName);
                intent.putExtra(CategoryDetailActivity.EXTRA_HERO_IMAGE, heroUrl);
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

        if (productAdapter == null) {
            // First setup: create the adapter and attach a 2-column GridLayoutManager.
            // nestedScrollingEnabled=false (set in XML) lets the outer NestedScrollView
            // control the scroll, so the whole page scrolls as one — smooth and natural.
            GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
            productsGrid.setLayoutManager(layoutManager);
            productAdapter = new ProductGridAdapter(this, products);
            productsGrid.setAdapter(productAdapter);
        } else {
            // onResume path: just refresh to pick up favorite changes
            productAdapter.notifyDataSetChanged();
        }
    }

    private void setupNewArrivalsBanner() {
        View discoverButton = findViewById(R.id.discoverButton);
        if (discoverButton != null) {
            discoverButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductListActivity.class);
                intent.putExtra(ProductListActivity.EXTRA_MODE, ProductListActivity.MODE_NEW_COLLECTION);
                startActivity(intent);
            });
        }
    }

    // ── New Collection horizontal scroll ──────────────────────────────────────

    private void setupNewCollectionSection() {
        TextView viewAll = findViewById(R.id.viewAllNewCollection);
        if (viewAll != null) viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductListActivity.class);
            intent.putExtra(ProductListActivity.EXTRA_MODE, ProductListActivity.MODE_NEW_COLLECTION);
            startActivity(intent);
        });

        LinearLayout container = findViewById(R.id.newCollectionContainer);
        if (container == null) return;
        container.removeAllViews();

        // New Collection = high-end products (price >= $800), show first 5
        List<Product> newItems = new ArrayList<>();
        for (java.util.Map<String, List<Product>> brandMap
                : CategoryDetailActivity.CATALOGUE.values()) {
            for (List<Product> list : brandMap.values()) {
                for (Product p : list) {
                    double price = parsePrice(p.price);
                    if (price >= 800) newItems.add(p);
                }
            }
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

        LinearLayout container = findViewById(R.id.topSellingContainer);
        if (container == null) return;
        container.removeAllViews();

        // Top Selling = rating >= 4.7, show first 6
        List<Product> topItems = new ArrayList<>();
        for (java.util.Map<String, List<Product>> brandMap
                : CategoryDetailActivity.CATALOGUE.values()) {
            for (List<Product> list : brandMap.values()) {
                for (Product p : list) {
                    if (Double.parseDouble(p.rating) >= 4.7) topItems.add(p);
                }
            }
        }
        // Sort by rating descending
        java.util.Collections.sort(topItems, (a, b) ->
                Double.compare(Double.parseDouble(b.rating), Double.parseDouble(a.rating)));

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
                product.isFavorite ? R.color.red_error : R.color.text_secondary));

        Glide.with(this).load(product.imageUrl).centerCrop()
             .placeholder(R.drawable.placeholder_image).into(imgView);

        view.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra("product_name",   product.name);
            intent.putExtra("product_price",  product.price);
            intent.putExtra("product_image",  product.imageUrl);
            intent.putExtra("product_rating", product.rating);
            startActivity(intent);
        });

        favIcon.setOnClickListener(v -> {
            product.isFavorite = !product.isFavorite;
            if (product.isFavorite) FavoriteManager.getInstance().addFavorite(product);
            else FavoriteManager.getInstance().removeFavorite(product);
            favIcon.setImageResource(product.isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            favIcon.setColorFilter(ContextCompat.getColor(this,
                    product.isFavorite ? R.color.red_error : R.color.text_secondary));
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
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        // Glass style: strip Material3's colorSurfaceContainer tint so the pill drawable shows through
        bottomNav.setBackground(null);
        bottomNav.setBackgroundTintList(null);

        // Active indicator tint: translucent primary orange
        bottomNav.setItemActiveIndicatorColor(
                android.content.res.ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.nav_indicator_fill)));

        // Icon & text tint from the color selector
        android.content.res.ColorStateList navTint =
                ContextCompat.getColorStateList(this, R.color.bottom_nav_color);
        if (navTint != null) {
            bottomNav.setItemIconTintList(navTint);
            bottomNav.setItemTextColor(navTint);
        }

        // ── Navigation ─────────────────────────────────────────────────────
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_categories) {
                startActivity(new Intent(this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    public static class Product {
        String name, price, rating, imageUrl;
        String sku;           // e.g. "BECKIE6036"
        boolean inStock;      // true = Available In Stock
        int warrantyYears;    // e.g. 1
        String specs;         // short bullet specs, newline-separated, e.g. "-13.6\" Liquid Retina\n-Weight: 1.24Kg"
        String freeItems;     // e.g. "Free 8 items"
        boolean isFavorite;

        // Minimal constructor (legacy — no SKU/stock/warranty/specs)
        Product(String n, String p, String r, boolean f, String i) {
            name=n; price=p; rating=r; isFavorite=f; imageUrl=i;
            inStock=true; warrantyYears=1; sku=""; specs=""; freeItems="";
        }

        // Full constructor
        Product(String n, String p, String r, boolean f, String i,
                String sku, boolean inStock, int warranty, String specs, String freeItems) {
            name=n; price=p; rating=r; isFavorite=f; imageUrl=i;
            this.sku=sku; this.inStock=inStock; this.warrantyYears=warranty;
            this.specs=specs; this.freeItems=freeItems;
        }
    }

    private static class Category {
        String name; int iconResId; boolean isHighlighted;
        Category(String n, int i, boolean h) { name=n; iconResId=i; isHighlighted=h; }
    }
}
