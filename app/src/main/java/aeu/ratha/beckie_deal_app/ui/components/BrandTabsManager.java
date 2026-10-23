package aeu.ratha.beckie_deal_app.ui.components;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Brand;
import aeu.ratha.beckie_deal_app.data.models.Product;
import aeu.ratha.beckie_deal_app.data.models.CartItemResponse;
import aeu.ratha.beckie_deal_app.data.repository.ProductRepository;
import aeu.ratha.beckie_deal_app.data.repository.CartRepository;
import aeu.ratha.beckie_deal_app.utils.helpers.ImageUrlHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity;

/**
 * Manager for Brand Tabs + Preview section
 * Handles brand tab selection, product preview loading, and UI state management
 */
public class BrandTabsManager {

    private Activity activity;
    private LinearLayout brandTabsContainer;
    private HorizontalScrollView brandTabsScroll;
    private LinearLayout previewProductsContainer;
    private LinearLayout loadingState;
    private LinearLayout emptyState;
    private TextView previewHeaderText;
    private TextView seeAllBtn;
    private TextView brandTabsSubtitle;

    private List<Brand> brands;
    private Brand selectedBrand;
    private int selectedBrandIndex = 0;

    public BrandTabsManager(Activity activity, LinearLayout brandTabsContainer) {
        this.activity = activity;
        this.brandTabsContainer = brandTabsContainer;
    }

    /**
     * Initialize the brand tabs section with given brands
     */
    public void initializeBrandTabs(List<Brand> brands, View parentView) {
        this.brands = brands;
        if (brands == null || brands.isEmpty()) return;

        // Get UI elements from parent view
        brandTabsScroll = parentView.findViewById(R.id.brandTabsScroll);
        previewProductsContainer = parentView.findViewById(R.id.previewProductsContainer);
        loadingState = parentView.findViewById(R.id.loadingState);
        emptyState = parentView.findViewById(R.id.emptyState);
        previewHeaderText = parentView.findViewById(R.id.previewHeaderText);
        seeAllBtn = parentView.findViewById(R.id.seeAllBtn);
        brandTabsSubtitle = parentView.findViewById(R.id.brandTabsSubtitle);

        // Update subtitle
        if (brandTabsSubtitle != null) {
            brandTabsSubtitle.setText(brands.size() + " brands available");
        }

        // Clear existing views before rendering tabs
        brandTabsContainer.removeAllViews();

        // Create and add brand tabs
        for (int i = 0; i < brands.size(); i++) {
            Brand brand = brands.get(i);
            View tabView = createBrandTab(brand, i);
            brandTabsContainer.addView(tabView);
        }

        // Select first brand by default
        if (!brands.isEmpty()) {
            selectBrand(0);
        }

        // Pre-fetch products for all brand tabs in parallel for 0ms tab switching
        preloadAllBrandProducts();
    }

    /**
     * Create a single brand tab view
     */
    private View createBrandTab(Brand brand, int index) {
        View tabView = LayoutInflater.from(activity).inflate(R.layout.item_brand_tab, brandTabsContainer, false);
        
        ImageView logo = tabView.findViewById(R.id.brandTabLogo);
        View indicator = tabView.findViewById(R.id.brandTabIndicator);
        View placeholder = tabView.findViewById(R.id.brandTabPlaceholder);

        // Set brand logo
        setLogoWithColor(logo, brand.getSlug(), brand.getName());

        // Set content description for accessibility
        logo.setContentDescription(brand.getName() + " logo");

        // Handle tab click
        tabView.setOnClickListener(v -> {
            selectBrand(index);
        });

        // Store brand reference in tab view
        tabView.setTag(brand);

        return tabView;
    }

    private final java.util.Map<String, List<Product>> brandProductsCache = new java.util.HashMap<>();

    /** Pre-fetch products for all brand tabs in background for 0ms tab switching */
    public void preloadAllBrandProducts() {
        if (brands == null) return;
        ProductRepository productRepo = ProductRepository.getInstance(activity);
        for (Brand b : brands) {
            String bName = b.getName() != null ? b.getName() : "";
            String key = (b.getId() + "_" + bName).toLowerCase();
            if (!brandProductsCache.containsKey(key)) {
                fetchBrandProductsInternal(productRepo, b, bName, key, true);
            }
        }
    }

    /**
     * Select a brand and update UI
     */
    public void selectBrand(int index) {
        if (index < 0 || index >= brands.size()) return;

        // Prevent redundant work if same tab re-selected
        if (selectedBrandIndex == index && previewProductsContainer != null && previewProductsContainer.getChildCount() > 0) {
            return;
        }

        selectedBrandIndex = index;
        selectedBrand = brands.get(index);

        // Update tab indicators instantly
        updateTabIndicators();

        // Load products instantly (0ms if cached in memory)
        loadBrandProducts(selectedBrand);
    }

    /**
     * Update visual indicator for selected tab
     */
    private void updateTabIndicators() {
        for (int i = 0; i < brandTabsContainer.getChildCount(); i++) {
            View tabView = brandTabsContainer.getChildAt(i);
            View indicator = tabView.findViewById(R.id.brandTabIndicator);
            View placeholder = tabView.findViewById(R.id.brandTabPlaceholder);
            ImageView logo = tabView.findViewById(R.id.brandTabLogo);
            
            if (i == selectedBrandIndex) {
                indicator.setVisibility(View.VISIBLE);
                placeholder.setVisibility(View.GONE);
                // Keep full color for selected tab
            } else {
                indicator.setVisibility(View.GONE);
                placeholder.setVisibility(View.VISIBLE);
                // Dim color for unselected tab (will implement with tint)
            }
        }

        // Scroll to selected tab
        if (brandTabsScroll != null) {
            View selectedTab = brandTabsContainer.getChildAt(selectedBrandIndex);
            brandTabsScroll.post(() -> {
                brandTabsScroll.smoothScrollTo(
                    selectedTab.getLeft() - 20,
                    0
                );
            });
        }
    }

    /**
     * Fade out products with animation
     */
    private void fadeOutProducts(Runnable onComplete) {
        if (previewProductsContainer == null) return;

        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(previewProductsContainer, "alpha", 1f, 0.5f);
        fadeOut.setDuration(150);
        fadeOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        fadeOut.start();
    }

    /**
     * Fade in products with animation
     */
    private void fadeInProducts() {
        if (previewProductsContainer == null) return;

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(previewProductsContainer, "alpha", 0.5f, 1f);
        fadeIn.setDuration(150);
        fadeIn.start();
    }

    /**
     * Load products for the selected brand from the API response (0ms if cached)
     */
    public void loadBrandProducts(Brand brand) {
        if (brand == null) return;

        String brandName = brand.getName() != null ? brand.getName() : "";
        String cacheKey = (brand.getId() + "_" + brandName).toLowerCase();

        // Update header
        if (previewHeaderText != null) {
            previewHeaderText.setText(brandName + " products");
        }

        // Setup "See all" button
        if (seeAllBtn != null) {
            seeAllBtn.setOnClickListener(v -> openCategoryDetail(brand));
        }

        // ⚡ INSTANT MEMORY CACHE DISPLAY (0ms LATENCY) ⚡
        if (brandProductsCache.containsKey(cacheKey)) {
            List<Product> cachedList = brandProductsCache.get(cacheKey);
            if (loadingState != null) loadingState.setVisibility(View.GONE);
            if (emptyState != null) emptyState.setVisibility(View.GONE);
            if (previewProductsContainer != null) previewProductsContainer.setVisibility(View.VISIBLE);

            if (previewHeaderText != null && cachedList != null) {
                previewHeaderText.setText(brandName + " - " + cachedList.size() + " products");
            }
            displayProducts(cachedList);
            return;
        }

        if (loadingState != null) loadingState.setVisibility(View.VISIBLE);
        if (emptyState != null) emptyState.setVisibility(View.GONE);
        if (previewProductsContainer != null) {
            previewProductsContainer.removeAllViews();
            previewProductsContainer.setVisibility(View.GONE);
        }

        ProductRepository productRepo = ProductRepository.getInstance(activity);
        fetchBrandProductsInternal(productRepo, brand, brandName, cacheKey, false);
    }

    private void fetchBrandProductsInternal(ProductRepository productRepo, Brand brand, String brandName, String cacheKey, boolean isBackgroundSync) {
        String keyword = getSearchKeywordForBrand(brandName, brand.getSlug());

        productRepo.searchProducts(keyword, 10, 0, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                activity.runOnUiThread(() -> {
                    if (!isBackgroundSync && loadingState != null) loadingState.setVisibility(View.GONE);
                    if (products != null && !products.isEmpty()) {
                        brandProductsCache.put(cacheKey, products);
                        if (selectedBrand != null && selectedBrand.getId() == brand.getId()) {
                            if (previewHeaderText != null) {
                                previewHeaderText.setText(brandName + " - " + products.size() + " products");
                            }
                            displayProducts(products);
                        }
                    } else {
                        fetchProductsByBrandIdInternal(productRepo, brand, brandName, cacheKey, isBackgroundSync);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                activity.runOnUiThread(() -> {
                    fetchProductsByBrandIdInternal(productRepo, brand, brandName, cacheKey, isBackgroundSync);
                });
            }
        });
    }

    private void fetchProductsByBrandIdInternal(ProductRepository productRepo, Brand brand, String brandName, String cacheKey, boolean isBackgroundSync) {
        productRepo.fetchProductsByBrand(brand.getId(), 10, 0, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                activity.runOnUiThread(() -> {
                    if (!isBackgroundSync && loadingState != null) loadingState.setVisibility(View.GONE);
                    if (products != null && !products.isEmpty()) {
                        brandProductsCache.put(cacheKey, products);
                        if (selectedBrand != null && selectedBrand.getId() == brand.getId()) {
                            if (previewHeaderText != null) {
                                previewHeaderText.setText(brandName + " - " + products.size() + " products");
                            }
                            displayProducts(products);
                        }
                    } else if (!isBackgroundSync && selectedBrand != null && selectedBrand.getId() == brand.getId()) {
                        if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                activity.runOnUiThread(() -> {
                    if (!isBackgroundSync && selectedBrand != null && selectedBrand.getId() == brand.getId()) {
                        if (loadingState != null) loadingState.setVisibility(View.GONE);
                        if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private String getSearchKeywordForBrand(String name, String slug) {
        String str = (name + " " + (slug != null ? slug : "")).toLowerCase();
        if (str.contains("apple") || str.contains("mac")) return "mac";
        if (str.contains("asus") || str.contains("rog")) return "asus";
        if (str.contains("msi")) return "msi";
        if (str.contains("dell") || str.contains("alienware")) return "dell";
        if (str.contains("hp") || str.contains("hewlett")) return "hp";
        if (str.contains("lenovo") || str.contains("thinkpad")) return "lenovo";
        return name;
    }

    /**
     * Populate preview with suggestion card design
     */
    public void displayProducts(List<Product> products) {
        if (previewProductsContainer == null) return;
        previewProductsContainer.removeAllViews();

        if (products == null || products.isEmpty()) {
            if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
            if (previewProductsContainer != null) previewProductsContainer.setVisibility(View.GONE);
            fadeInProducts();
            return;
        }

        if (emptyState != null) emptyState.setVisibility(View.GONE);
        if (loadingState != null) loadingState.setVisibility(View.GONE);
        previewProductsContainer.setVisibility(View.VISIBLE);

        LayoutInflater inflater = LayoutInflater.from(activity);

        for (Product product : products) {
            View cardView = inflater.inflate(R.layout.item_suggestion, previewProductsContainer, false);

            ImageView image = cardView.findViewById(R.id.suggestionImage);
            TextView name = cardView.findViewById(R.id.suggestionName);
            TextView price = cardView.findViewById(R.id.suggestionPrice);
            TextView stock = cardView.findViewById(R.id.suggestionStock);
            View addButton = cardView.findViewById(R.id.suggestionAdd);

            if (name != null) name.setText(product.getName());
            if (price != null) {
                String formattedPrice = product.getFormattedPrice();
                if (formattedPrice != null && formattedPrice.startsWith("$$")) {
                    formattedPrice = formattedPrice.substring(1);
                }
                price.setText(formattedPrice);
            }
            if (stock != null) stock.setText(product.isInStock() ? "In Stock" : "Out of Stock");

            if (image != null) {
                String imageUrl = ImageUrlHelper.getImageUrlOrFallback(product.getThumbnail());
                Glide.with(activity)
                    .load(imageUrl)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(image);
            }

            cardView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                activity.startActivity(intent);
            });

            if (addButton != null) {
                addButton.setOnClickListener(v -> {
                    CartRepository cartRepo = new CartRepository(activity);
                    cartRepo.addToCart(product.getId(), 1, new CartRepository.CartItemCallback() {
                        @Override
                        public void onSuccess(CartItemResponse response) {
                            Toast.makeText(activity, product.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                            BottomNavHelper.updateCartBadge(activity);
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(activity, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }

            previewProductsContainer.addView(cardView);
        }

        fadeInProducts();
    }

    /**
     * Set logo with proper colors for light/dark mode
     */
    private void setLogoWithColor(ImageView logoView, String brandSlug, String brandName) {
        Integer logoResId = getBrandLogoResource(brandSlug, brandName);
        if (logoResId != null) {
            logoView.setImageResource(logoResId);
            logoView.setVisibility(View.VISIBLE);
        } else {
            logoView.setVisibility(View.GONE);
        }
    }

    /**
     * Get brand logo resource ID based on brand slug and name
     */
    private Integer getBrandLogoResource(String slug, String name) {
        String str = "";
        if (slug != null) str += slug.toLowerCase() + " ";
        if (name != null) str += name.toLowerCase();

        if (str.contains("apple") || str.contains("mac")) return R.drawable.apple;
        if (str.contains("asus") || str.contains("rog") || str.contains("republic")) return R.drawable.asus;
        if (str.contains("msi")) return R.drawable.msi;
        if (str.contains("dell") || str.contains("alienware")) return R.drawable.dell;
        if (str.contains("hp") || str.contains("hewlett") || str.contains("omen")) return R.drawable.hp;
        if (str.contains("lenovo") || str.contains("thinkpad") || str.contains("legion")) return R.drawable.lenovo;
        if (str.contains("acer")) return R.drawable.ic_brand_acer;
        if (str.contains("samsung")) return R.drawable.ic_brand_samsung;
        if (str.contains("razer")) return R.drawable.ic_brand_razer;

        if (str.contains("app")) return R.drawable.apple;
        if (str.contains("asu")) return R.drawable.asus;
        if (str.contains("ms")) return R.drawable.msi;
        if (str.contains("del")) return R.drawable.dell;
        if (str.contains("len")) return R.drawable.lenovo;

        return R.drawable.apple;
    }

    /**
     * Open category detail activity for the brand
     */
    private void openCategoryDetail(Brand brand) {
        Intent intent = new Intent(activity, CategoryDetailActivity.class);
        intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY, "Laptops");
        intent.putExtra(CategoryDetailActivity.EXTRA_BRAND_FILTER, brand.getName());
        intent.putExtra("BRAND_ID", brand.getId());
        intent.putExtra("BRAND_SLUG", brand.getSlug());
        activity.startActivity(intent);
    }

    /**
     * Get the currently selected brand
     */
    public Brand getSelectedBrand() {
        return selectedBrand;
    }

    /**
     * Get all brands
     */
    public List<Brand> getBrands() {
        return brands;
    }

}
