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
    private ImageView viewModeIcon;

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
        View notificationContainer = findViewById(R.id.notificationContainer);

        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
    }

    private void setupSearchBar() {
        View searchBar = findViewById(R.id.searchBar);
        if (searchBar != null) searchBar.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
    }

    private void setupHeroBanner() {
        // Hero banner data: 3 slides
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

        bannerViewPager = findViewById(R.id.bannerViewPager);
        setupBannerCarousel(
                bannerViewPager,
                findViewById(R.id.bannerArrowLeft),
                findViewById(R.id.bannerArrowRight),
                findViewById(R.id.bannerDotsContainer),
                heroBannerTexts, heroBannerBgs, null,
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
                holder.background.setBackgroundResource(backgrounds[position]);
                holder.tag.setText(texts[position][0]);
                holder.title.setText(texts[position][1]);
                holder.subtitle.setText(texts[position][2]);

                // Support image banners (future-proof)
                if (imageUrls != null && imageUrls[position] != null && !imageUrls[position].isEmpty()) {
                    holder.bannerImage.setVisibility(View.VISIBLE);
                    Glide.with(holder.itemView.getContext())
                            .load(imageUrls[position])
                            .centerInside()
                            .into(holder.bannerImage);
                } else {
                    holder.bannerImage.setVisibility(View.GONE);
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
            boolean newListView = !ViewModePreferences.isListView(this);
            ViewModePreferences.setListView(this, newListView);
            updateViewModeIcon();

            RecyclerView productsGrid = findViewById(R.id.productsGrid);
            if (productsGrid != null) {
                GridLayoutManager layoutManager = (GridLayoutManager) productsGrid.getLayoutManager();
                if (layoutManager != null) layoutManager.setSpanCount(newListView ? 1 : 2);
            }
            if (productAdapter != null) productAdapter.setListMode(newListView);
        });
    }

    /** Swap the toggle button's icon to reflect the current mode (shows the icon for the OTHER mode to switch to). */
    private void updateViewModeIcon() {
        if (viewModeIcon == null) return;
        boolean isListView = ViewModePreferences.isListView(this);
        viewModeIcon.setImageResource(isListView ? R.drawable.grid_view_24 : R.drawable.event_list_24);
    }

    private void setupNewArrivalsBanner() {
        // New Arrivals banner carousel: 3 slides (different content from hero)
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

        newArrivalsBannerPager = findViewById(R.id.newArrivalsBannerViewPager);
        setupBannerCarousel(
                newArrivalsBannerPager,
                findViewById(R.id.newArrivalsArrowLeft),
                findViewById(R.id.newArrivalsArrowRight),
                findViewById(R.id.newArrivalsDotsContainer),
                newArrivalTexts, newArrivalBgs, null,
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
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        updateCartBadge();
    }

    private void updateCartBadge() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;
        int count = CartManager.getInstance().getTotalUnits();
        if (count > 0) {
            com.google.android.material.badge.BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_cart);
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(ContextCompat.getColor(this, R.color.red_error));
            badge.setBadgeTextColor(ContextCompat.getColor(this, R.color.white_fixed));
        } else {
            bottomNav.removeBadge(R.id.nav_cart);
        }
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
