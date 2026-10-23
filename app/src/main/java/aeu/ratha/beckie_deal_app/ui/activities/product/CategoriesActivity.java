package aeu.ratha.beckie_deal_app.ui.activities.product;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.data.models.Brand;
import aeu.ratha.beckie_deal_app.data.models.Category;
import aeu.ratha.beckie_deal_app.data.repository.BrandRepository;
import aeu.ratha.beckie_deal_app.data.repository.CategoryRepository;
import aeu.ratha.beckie_deal_app.data.repository.ProductRepository;
import aeu.ratha.beckie_deal_app.data.models.Product;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.components.BrandTabsManager;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.content.Intent;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.graphics.Color;

import android.widget.ImageView;
import android.content.res.Configuration;
import java.util.ArrayList;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.os.Bundle;
import android.util.Log;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.view.LayoutInflater;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.view.View;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.GridLayout;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.ImageView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.TextView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.ui.activities.product.SearchActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoryDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.AllBrandsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.widget.Toast;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import aeu.ratha.beckie_deal_app.utils.helpers.CategoryIconMapper;

public class CategoriesActivity extends BaseActivity {

    private static final String TAG = "CategoriesActivity";
    private static final int BRANDS_PREVIEW_COUNT = 6;
    
    private List<BrandDisplay> brandsList = new ArrayList<>();
    private List<CategoryItem> categoriesList = new ArrayList<>();
    private GridLayout brandsGrid;
    private LinearLayout categoriesColumnLeft;
    private LinearLayout categoriesColumnRight;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        AppDrawerHelper drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        // Refresh drawer with latest user data from API to ensure sidebar shows correct user across all pages
        drawerHelper.refreshDrawerWithLatestUserData(this);
        setupTopBar();
        initRepository();
        fetchFeaturedBrands(); // Fetch from API
        fetchAllCategories(); // Fetch categories from API
        setupBottomNavigation();
        setupEntranceAnimation();
    }

    private void setupTopBar() {
        // Configure logo display based on light/dark mode
        ImageView logoImage = findViewById(R.id.logoImage);
        if (logoImage != null) {
            int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            boolean isLight = nightMode != android.content.res.Configuration.UI_MODE_NIGHT_YES;
            
            if (isLight) {
                // In light mode: display the launcher icon
                logoImage.setImageResource(R.mipmap.ic_launcher);
                logoImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } else {
                // In dark mode: display the launcher icon as-is
                logoImage.setImageResource(R.mipmap.ic_launcher);
                logoImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        }
        
        // menuIcon click handled by AppDrawerHelper
        View searchBar = findViewById(R.id.searchBar);
        View notificationContainer = findViewById(R.id.notificationContainer);
        View viewAllBrands = findViewById(R.id.viewAllBrandsBtn);

        if (searchBar != null) searchBar.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
        if (viewAllBrands != null) viewAllBrands.setOnClickListener(v ->
                startActivity(new Intent(this, AllBrandsActivity.class)));

        UiAnimations.pressBounce(searchBar, notificationContainer, viewAllBrands);
    }

    /** Fades/slides the hero header and section headers into place — brand/category
     *  grids themselves are left untouched (grid-of-cards, kept static). */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideUp(findViewById(R.id.brandTabsSectionHeader), 80);
        UiAnimations.fadeSlideUp(findViewById(R.id.allCategoriesTitle), 140);
    }
    
    /**
     * Initialize brand and category repositories.
     */
    private void initRepository() {
        brandRepository = BrandRepository.getInstance(this);
        categoryRepository = CategoryRepository.getInstance(this);
        productRepository = ProductRepository.getInstance(this);
    }
    
    /**
     * Fetch featured/famous brands from API.
     * Endpoint: GET /master/brands?limit=20&offset=0
     */
    private void fetchFeaturedBrands() {
        Log.d(TAG, "=== FETCHING BRANDS FROM API ===");
        
        // Fetch all brands from API
        // PERF: Enable cache to show cached brands instantly while fetching fresh data
        brandRepository.fetchBrands(20, 0, true, new BrandRepository.BrandCallback() {
            @Override
            public void onSuccess(List<Brand> brands, int total) {
                Log.d(TAG, "✓ API SUCCESS: Received " + brands.size() + " brands from API");
                
                brandsList.clear();
                
                // Add first 6 active brands (no hardcoded filtering)
                for (Brand brand : brands) {
                    if (brand.isActive()) {
                        int drawableId = getBrandDrawable(brand.getName());
                        brandsList.add(new BrandDisplay(brand, drawableId, false));
                        
                        Log.d(TAG, "  - " + brand.getName() + 
                            " (ID: " + brand.getId() + 
                            ", products: " + brand.getProductsCount() + 
                            ", featured: " + brand.isFeatured() + ")");
                        
                        if (brandsList.size() >= BRANDS_PREVIEW_COUNT) break;
                    }
                }
                
                Log.d(TAG, "=== RENDERING " + brandsList.size() + " BRANDS ===");
                setupLaptopBrands();
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "✗ API ERROR: " + errorMessage);
                initFallbackBrands();
                setupLaptopBrands();
            }
        });
    }
    
    /**
     * Get drawable resource ID for a brand name from API.
     * Maps brand names to local icon resources.
     */
    private int getBrandDrawable(String brandName) {
        String name = brandName.toLowerCase();
        if (name.contains("asus")) return R.drawable.ic_brand_asus;
        if (name.contains("rog")) return R.drawable.ic_brand_rog;
        if (name.contains("apple")) return R.drawable.ic_brand_apple;
        if (name.contains("razer")) return R.drawable.ic_brand_razer;
        if (name.contains("msi")) return R.drawable.ic_brand_msi;
        if (name.contains("dell")) return R.drawable.ic_brand_dell;
        if (name.contains("hp")) return R.drawable.ic_brand_hp;
        if (name.contains("lenovo")) return R.drawable.ic_brand_lenovo;
        if (name.contains("samsung")) return R.drawable.ic_brand_samsung;
        if (name.contains("acer")) return R.drawable.ic_brand_acer;
        if (name.contains("microsoft")) return R.drawable.ic_brand_microsoft;
        return 0; // No icon
    }

    /**
     * Get brand logo drawable ID by brand slug or name.
     */
    private Integer getBrandLogo(String slug) {
        if (slug == null) return R.drawable.apple;
        String name = slug.toLowerCase();
        if (name.contains("apple") || name.contains("mac")) {
            return isDarkModeEnabled() ? R.drawable.apple_white : R.drawable.apple;
        }
        if (name.contains("dell") || name.contains("alienware")) return R.drawable.dell;
        if (name.contains("hp") || name.contains("hewlett") || name.contains("omen")) return R.drawable.hp;
        if (name.contains("lenovo") || name.contains("thinkpad") || name.contains("legion")) return R.drawable.lenovo;
        if (name.contains("msi")) return R.drawable.msi_official_red_glossy;
        if (name.contains("rog") || name.contains("republic")) return R.drawable.republicofgamers;
        if (name.contains("asus")) return R.drawable.asus;
        if (name.contains("razer")) return R.drawable.ic_brand_razer;
        if (name.contains("samsung")) return R.drawable.ic_brand_samsung;
        return R.drawable.apple;
    }

    /**
     * Check if dark mode is enabled.
     */
    private boolean isDarkModeEnabled() {
        int nightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES;
    }

    /**
     * Handle API error - show empty state or error message.
     */
    private void initFallbackBrands() {
        Log.e(TAG, "Failed to load brands from API");
        brandsList.clear();
        
        // Show error message to user
        runOnUiThread(() -> {
            Toast.makeText(this, "Failed to load brands. Please check your connection.", 
                Toast.LENGTH_SHORT).show();
        });
    }

    private BrandTabsManager brandTabsManager;

    private void setupLaptopBrands() {
        // Get the brand tabs container and initialize the manager
        LinearLayout brandTabsContainer = findViewById(R.id.brandTabsContainer);
        
        if (brandTabsContainer == null) {
            Log.e(TAG, "Brand tabs container not found!");
            return;
        }

        // Create and initialize the brand tabs manager
        brandTabsManager = new BrandTabsManager(this, brandTabsContainer);

        // Prepare 6 laptop brands in exact requested order: Apple, Asus, MSI, Dell, HP, Lenovo
        List<Brand> brandsForTabs = buildLaptopBrandsList(brandsList);

        // Get the parent view containing all the brand tabs elements
        View parentSection = (View) brandTabsContainer.getParent();
        if (parentSection != null) {
            parentSection = (View) parentSection.getParent();
        }
        
        if (parentSection == null) {
            parentSection = findViewById(R.id.brandTabsSectionHeader);
            if (parentSection != null) {
                parentSection = (View) parentSection.getParent().getParent();
            }
        }
        
        // Initialize the brand tabs manager with the section view
        if (parentSection != null) {
            brandTabsManager.initializeBrandTabs(brandsForTabs, parentSection);
        }
    }

    private List<Brand> buildLaptopBrandsList(List<BrandDisplay> apiBrandsList) {
        List<Brand> result = new ArrayList<>();

        // Target 6 brands in exact requested order:
        // 1. Apple, 2. Asus, 3. MSI, 4. Dell, 5. HP, 6. Lenovo
        String[][] targetBrands = new String[][]{
            {"Apple", "apple", "12"},
            {"Asus", "asus", "10"},
            {"MSI", "msi", "6"},
            {"Dell", "dell", "8"},
            {"HP", "hp", "10"},
            {"Lenovo", "lenovo", "9"}
        };

        for (int i = 0; i < targetBrands.length; i++) {
            String name = targetBrands[i][0];
            String slug = targetBrands[i][1];
            int defaultCount = Integer.parseInt(targetBrands[i][2]);

            Brand foundBrand = findBrandInApiList(apiBrandsList, slug, name);
            if (foundBrand != null) {
                result.add(foundBrand);
            } else {
                Brand fallback = new Brand(i + 1, name, slug);
                fallback.setActive(true);
                fallback.setProductsCount(defaultCount);
                result.add(fallback);
            }
        }

        return result;
    }

    private Brand findBrandInApiList(List<BrandDisplay> apiBrandsList, String targetSlug, String targetName) {
        if (apiBrandsList == null) return null;
        for (BrandDisplay bd : apiBrandsList) {
            if (bd == null || bd.brand == null) continue;
            Brand b = bd.brand;
            String bName = b.getName() != null ? b.getName().toLowerCase() : "";
            String bSlug = b.getSlug() != null ? b.getSlug().toLowerCase() : "";
            if (bSlug.equals(targetSlug) || bName.equals(targetName.toLowerCase()) || 
                bSlug.contains(targetSlug) || bName.contains(targetName.toLowerCase())) {
                return b;
            }
        }
        return null;
    }

    /** Rotating tint pairs (background, foreground) applied to brand icon containers
     *  so the grid reads as colorful instead of every card sharing the same cream circle. */
    private static final int[][] BRAND_TINTS = {
            {R.color.brand_tint_bg_1, R.color.brand_tint_fg_1},
            {R.color.brand_tint_bg_2, R.color.brand_tint_fg_2},
            {R.color.brand_tint_bg_3, R.color.brand_tint_fg_3},
            {R.color.brand_tint_bg_4, R.color.brand_tint_fg_4},
            {R.color.brand_tint_bg_5, R.color.brand_tint_fg_5},
            {R.color.brand_tint_bg_6, R.color.brand_tint_fg_6},
    };

    /** How many brands to preview on this page before "View All" (keeps a clean
     *  2-column grid of 3 even rows). The full list lives in AllBrandsActivity. */

    private void renderBrands() {
        if (brandsGrid == null) return;
        brandsGrid.removeAllViews();

        int previewCount = Math.min(brandsList.size(), BRANDS_PREVIEW_COUNT);
        for (int i = 0; i < previewCount; i++) {
            BrandDisplay brandDisplay = brandsList.get(i);
            Brand brand = brandDisplay.brand;
            
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_brand_square, brandsGrid, false);

            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView nameText = view.findViewById(R.id.brandName);
            View checkBadge = view.findViewById(R.id.selectedCheckBadge);

            nameText.setText(brand.getName());

            // Display brand logo if available
            Integer logoResId = getBrandLogo(brand.getSlug());
            if (logoResId == null && brand.getName() != null) {
                logoResId = getBrandLogo(brand.getName());
            }
            if (logoResId != null) {
                brandIcon.setImageResource(logoResId);
                brandIcon.setVisibility(View.VISIBLE);
            } else {
                brandIcon.setVisibility(View.GONE);
            }

            int[] tint = BRAND_TINTS[i % BRAND_TINTS.length];
            int tintFg = ContextCompat.getColor(this, tint[1]);

            if (brandDisplay.isSelected) {
                int primaryColor = ContextCompat.getColor(this, R.color.primary);
                nameText.setTextColor(primaryColor);
                if (checkBadge != null) checkBadge.setVisibility(View.VISIBLE);
            } else {
                nameText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                if (checkBadge != null) checkBadge.setVisibility(View.GONE);
            }

            final int index = i;
            View.OnClickListener onBrandClick = v -> {
                for (int j = 0; j < brandsList.size(); j++)
                    brandsList.get(j).isSelected = (j == index);
                renderBrands();
                
                // Open CategoryDetailActivity pre-filtered to this brand
                String category = brandToCategory(brand.getName());
                int total = brand.getProductsCount();
                
                Intent intent = new Intent(this, CategoryDetailActivity.class);
                intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY,     category);
                intent.putExtra(CategoryDetailActivity.EXTRA_HERO_IMAGE,   heroImageFor(category));
                intent.putExtra(CategoryDetailActivity.EXTRA_ITEM_COUNT,   total + " Items");
                intent.putExtra(CategoryDetailActivity.EXTRA_BRAND_FILTER, brand.getName());
                intent.putExtra("BRAND_ID", brand.getId());
                intent.putExtra("BRAND_SLUG", brand.getSlug());
                startActivity(intent);
            };
            view.setOnClickListener(onBrandClick);

            brandsGrid.addView(view);
        }
    }

    /** Maps a brand to the catalogue category where it actually has products.
     *  Gaming brands (ROG, MSI, Razer) resolve to "Gaming". */
    private String brandToCategory(String brand) {
        switch (brand) {
            case "ROG":
            case "MSI":
            case "Razer":   return "Gaming";
            case "Samsung": return "Smartphones";
            default:        return "Laptops";
        }
    }

    /** Hero banner image per category for the detail screen. */
    private String heroImageFor(String category) {
        switch (category) {
            case "Gaming":
                return "https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=600&h=400&fit=crop";
            case "Smartphones":
                return "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=400&fit=crop";
            default:
                return "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&h=400&fit=crop";
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * Fetch all categories from API.
     * Endpoint: GET /api/v1/master/categories?limit=20&offset=0
     */
    private void fetchAllCategories() {
        Log.d(TAG, "=== FETCHING ALL CATEGORIES FROM API ===");
        
        categoryRepository.fetchActiveCategories(new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories, int total) {
                Log.d(TAG, "✓ API SUCCESS: Received " + categories.size() + " categories with product counts");
                
                categoriesList.clear();
                
                if (categories != null) {
                    for (Category category : categories) {
                        if (category.isActive()) {
                            int iconRes = CategoryIconMapper.getIconForCategory(category);
                            String imageUrl = getCategoryImageUrl(category);
                            int productCount = category.getProductsCount();
                            String countText = productCount > 0 
                                ? productCount + " Items" 
                                : "0 Items";
                            
                            Log.d(TAG, "  - " + category.getName() + 
                                " (ID: " + category.getId() + 
                                ", products: " + category.getProductsCount() + 
                                ", featured: " + category.isFeatured() + 
                                ", children: " + category.getChildrenCount() + ")");
                            
                            categoriesList.add(new CategoryItem(
                                category.getName(),
                                countText,
                                productCount,
                                imageUrl,
                                iconRes,
                                category.isFeatured(),
                                category.getId(),
                                category.getSlug()
                            ));
                        }
                    }
                }
                
                // Sort categories by product count (descending) - most products first
                categoriesList.sort((a, b) -> Integer.compare(b.countInt, a.countInt));
                
                Log.d(TAG, "=== RENDERING " + categoriesList.size() + " CATEGORIES (sorted by count) ===");
                setupAllCategories();
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "✗ API ERROR: " + errorMessage);
                initFallbackCategories();
            }
        });
    }
    
    private int extractCountFromString(String countText) {
        // Extract number from strings like "12 Items" or "0 Items"
        if (countText == null || countText.isEmpty()) return 0;
        String[] parts = countText.split(" ");
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int getCategoryIcon(String categoryName) {
        String name = categoryName.toLowerCase();
        if (name.contains("laptop") || name.contains("computer") || name.contains("asus") || name.contains("vivobook")) return R.drawable.devices_24;
        if (name.contains("phone") || name.contains("mobile")) return R.drawable.mobile_24;
        if (name.contains("accessor") || name.contains("fashion")) return R.drawable.widgets_24;
        if (name.contains("gaming") || name.contains("game")) return R.drawable.sports_esports_24;
        if (name.contains("wearable") || name.contains("watch")) return R.drawable.devices_wearables_24;
        if (name.contains("monitor") || name.contains("display")) return R.drawable.devices_24;
        if (name.contains("storage") || name.contains("drive")) return R.drawable.widgets_24;
        if (name.contains("network")) return R.drawable.widgets_24;
        if (name.contains("audio") || name.contains("sound")) return R.drawable.widgets_24;
        return R.drawable.widgets_24; // Default icon
    }
    
    /**
     * Get image URL for category from API.
     */
    private String getCategoryImageUrl(Category category) {
        return category.getImage();
    }
    
    /**
     * Handle API error - show empty state or error message.
     */
    private void initFallbackCategories() {
        Log.e(TAG, "Failed to load categories from API");
        categoriesList.clear();
        
        // Show error message to user
        runOnUiThread(() -> {
            Toast.makeText(this, "Failed to load categories. Please check your connection.", 
                Toast.LENGTH_SHORT).show();
        });
        
        // Still call setup to show empty state
        setupAllCategories();
    }
    
    /**
     * Render all categories using stacked wallet card layout from API data.
     * This method is called after fetchAllCategories() completes successfully.
     */
    private void setupAllCategories() {
        LinearLayout container = findViewById(R.id.allCategoriesStack);
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.contentScroll);
        
        if (container == null || scrollView == null) return;

        // Create the stack controller
        CategoryStackController stack = new CategoryStackController(
                this,
                container,
                scrollView,
                // ProductLoader: load products for a category
                (category, callback) -> {
                    Log.d(TAG, "Loading products for category: " + category.name + " (ID: " + category.id + ")");
                    
                    // Convert category ID to integer
                    try {
                        int categoryId = Integer.parseInt(category.id);
                        
                        // Fetch products from API for this category
                        productRepository.fetchProductsByCategory(
                            categoryId,
                            20,  // limit: fetch up to 20 products
                            0,   // offset: start from 0
                            new ProductRepository.ProductCallback() {
                                @Override
                                public void onSuccess(List<Product> products, int total) {
                                    Log.d(TAG, "✓ Loaded " + products.size() + " products for: " + category.name);
                                    
                                    // Convert Product list to ProductUi list
                                    List<CategoryStackController.ProductUi> uiProducts = new ArrayList<>();
                                    for (Product p : products) {
                                        // Format price (use sale price if available, otherwise regular price)
                                        String price = p.hasDiscount() ? p.getFormattedPrice() : p.getPrice();
                                        
                                        uiProducts.add(new CategoryStackController.ProductUi(
                                            p.getId(),                    // product ID
                                            p.getName(),                  // product name
                                            price,                        // price as subtitle
                                            p.getThumbnail()              // thumbnail URL
                                        ));
                                        
                                        Log.d(TAG, "  → " + p.getName() + " - " + price);
                                    }
                                    
                                    // Call the callback with converted products
                                    callback.onLoaded(uiProducts);
                                }
                                
                                @Override
                                public void onError(String errorMessage) {
                                    Log.e(TAG, "✗ Error loading products for " + category.name + ": " + errorMessage);
                                    callback.onError();
                                }
                            }
                        );
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid category ID: " + category.id);
                        callback.onError();
                    }
                },
                // Listener: handle category and product clicks
                new CategoryStackController.Listener() {
                    @Override
                    public void onOpenCategory(CategoryStackController.CategoryUi category) {
                        // User tapped "View all N items" button - open category detail
                        Intent intent = new Intent(CategoriesActivity.this, CategoryDetailActivity.class);
                        intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY, category.name);
                        // Find the corresponding CategoryItem to get the full data
                        for (CategoryItem item : categoriesList) {
                            if (item.name.equals(category.name)) {
                                intent.putExtra(CategoryDetailActivity.EXTRA_HERO_IMAGE, item.imageUrl);
                                intent.putExtra(CategoryDetailActivity.EXTRA_ITEM_COUNT, item.count);
                                intent.putExtra("CATEGORY_ID", item.categoryId);
                                intent.putExtra("CATEGORY_SLUG", item.categorySlug);
                                break;
                            }
                        }
                        startActivity(intent);
                    }

                    @Override
                    public void onProductClick(CategoryStackController.CategoryUi category, 
                                             CategoryStackController.ProductUi product) {
                        // User tapped a product - open product detail
                        Intent intent = new Intent(CategoriesActivity.this, ProductDetailActivity.class);
                        intent.putExtra("PRODUCT_ID", product.id);
                        startActivity(intent);
                    }
                }
        );

        // Set image loader using Glide
        stack.setImageLoader((iv, url) -> Glide.with(CategoriesActivity.this)
                .load(url)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(iv));

        // Convert CategoryItem list to CategoryUi for the stack controller
        List<CategoryStackController.CategoryUi> stackCategories = new ArrayList<>();
        for (CategoryItem item : categoriesList) {
            stackCategories.add(new CategoryStackController.CategoryUi(
                    String.valueOf(item.categoryId),
                    item.name,
                    item.countInt,
                    item.iconRes,
                    item.imageUrl
            ));
        }

        // Bind the categories to the stack
        stack.bind(stackCategories);
    }

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.CATEGORIES);
    }

    // ── Data classes ──────────────────────────────────────────────────────────

    /**
     * Wrapper class to combine API Brand with display properties.
     */
    private static class BrandDisplay {
        Brand brand;           // API brand data
        int imageResId;        // Drawable resource ID
        boolean isSelected;    // Selection state
        
        BrandDisplay(Brand brand, int imageResId, boolean isSelected) {
            this.brand = brand;
            this.imageResId = imageResId;
            this.isSelected = isSelected;
        }
    }

    private static class CategoryItem {
        String name, count, imageUrl, categorySlug;
        int categoryId;  // Changed to int to match Category.getId()
        int countInt;
        int iconRes;
        boolean isHot;

        CategoryItem(String name, String count, int countInt, String imageUrl, int iconRes, boolean isHot, 
                    int categoryId, String categorySlug) {
            this.name = name; 
            this.count = count; 
            this.countInt = countInt;
            this.imageUrl = imageUrl;
            this.iconRes = iconRes; 
            this.isHot = isHot;
            this.categoryId = categoryId;
            this.categorySlug = categorySlug;
        }
    }
}
