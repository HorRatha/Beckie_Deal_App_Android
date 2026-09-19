package aeu.ratha.beckie_deal_app.ui.activities.product;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.data.models.Brand;
import aeu.ratha.beckie_deal_app.data.models.Category;
import aeu.ratha.beckie_deal_app.data.repository.BrandRepository;
import aeu.ratha.beckie_deal_app.data.repository.CategoryRepository;
import aeu.ratha.beckie_deal_app.R;


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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends BaseActivity {

    private static final String TAG = "CategoriesActivity";
    private static final int BRANDS_PREVIEW_COUNT = 6;
    
    private List<BrandDisplay> brandsList = new ArrayList<>();
    private List<CategoryItem> categoriesList = new ArrayList<>();
    private GridLayout brandsGrid;
    private GridLayout allCategoriesGrid;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        initRepository();
        fetchFeaturedBrands(); // Fetch from API
        fetchAllCategories(); // Fetch categories from API
        setupBottomNavigation();
        setupEntranceAnimation();
    }

    private void setupTopBar() {
        // menuIcon click handled by AppDrawerHelper
        ImageView searchIcon = findViewById(R.id.searchIcon);
        View notificationContainer = findViewById(R.id.notificationContainer);
        View viewAllBrands = findViewById(R.id.viewAllBrands);

        if (searchIcon != null) searchIcon.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));
        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
        if (viewAllBrands != null) viewAllBrands.setOnClickListener(v ->
                startActivity(new Intent(this, AllBrandsActivity.class)));

        UiAnimations.pressBounce(searchIcon, notificationContainer, viewAllBrands);
    }

    /** Fades/slides the hero header and section headers into place — brand/category
     *  grids themselves are left untouched (grid-of-cards, kept static). */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideUp(findViewById(R.id.laptopBrandsSectionHeader), 80);
        UiAnimations.fadeSlideUp(findViewById(R.id.allCategoriesTitle), 140);
    }
    
    /**
     * Initialize brand and category repositories.
     */
    private void initRepository() {
        brandRepository = BrandRepository.getInstance(this);
        categoryRepository = CategoryRepository.getInstance(this);
    }
    
    /**
     * Fetch featured/famous brands from API.
     * Endpoint: GET /master/brands?limit=20&offset=0
     */
    private void fetchFeaturedBrands() {
        Log.d(TAG, "=== FETCHING BRANDS FROM API ===");
        
        // Fetch all brands from API
        brandRepository.fetchBrands(20, 0, false, new BrandRepository.BrandCallback() {
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
     * Get brand logo drawable ID by brand slug.
     */
    private Integer getBrandLogo(String slug) {
        if (slug == null) return null;
        String name = slug.toLowerCase();
        // Use new logos first
        if (name.equals("apple")) return R.drawable.apple;
        if (name.equals("dell")) return R.drawable.dell;
        if (name.equals("hp")) return R.drawable.hp;
        if (name.equals("lenovo")) return R.drawable.lenovo;
        if (name.equals("msi")) return R.drawable.msi;
        if (name.equals("nvidia")) return R.drawable.nvidia;
        if (name.equals("rog")) return R.drawable.republicofgamers;
        // Fallback to old logos if available
        if (name.equals("razer")) return R.drawable.ic_brand_razer;
        if (name.equals("asus")) return R.drawable.ic_brand_asus;
        if (name.equals("acer")) return R.drawable.ic_brand_acer;
        if (name.equals("microsoft")) return R.drawable.ic_brand_microsoft;
        if (name.equals("samsung")) return R.drawable.ic_brand_samsung;
        return null;
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

    private void setupLaptopBrands() {
        brandsGrid = findViewById(R.id.laptopBrandsGrid);
        // Set column count to 3 for 3 cards per row
        if (brandsGrid != null) brandsGrid.setColumnCount(3);

        TextView brandsSubtitle = findViewById(R.id.brandsAvailableSubtitle);
        
        // Create 6 static brands with logos (2 rows x 3 columns)
        List<BrandDisplay> staticBrands = new ArrayList<>();
        
        // Create static Brand objects
        Brand apple = new Brand(1, "Apple", "apple");
        apple.setActive(true);
        staticBrands.add(new BrandDisplay(apple, 0, false));
        
        Brand dell = new Brand(2, "Dell", "dell");
        dell.setActive(true);
        staticBrands.add(new BrandDisplay(dell, 0, false));
        
        Brand hp = new Brand(3, "HP", "hp");
        hp.setActive(true);
        staticBrands.add(new BrandDisplay(hp, 0, false));
        
        Brand lenovo = new Brand(4, "Lenovo", "lenovo");
        lenovo.setActive(true);
        staticBrands.add(new BrandDisplay(lenovo, 0, false));
        
        Brand msi = new Brand(5, "MSI", "msi");
        msi.setActive(true);
        staticBrands.add(new BrandDisplay(msi, 0, false));
        
        Brand rog = new Brand(6, "ROG", "rog");
        rog.setActive(true);
        staticBrands.add(new BrandDisplay(rog, 0, false));
        
        // Update subtitle
        if (brandsSubtitle != null) {
            brandsSubtitle.setText(staticBrands.size() + " brands available");
        }

        // Render the static brands
        renderFilteredBrands(staticBrands);
    }

    /**
     * Render filtered brands list (for Laptop Brands section).
     */
    private void renderFilteredBrands(List<BrandDisplay> filteredBrands) {
        if (brandsGrid == null) return;
        brandsGrid.removeAllViews();

        for (int i = 0; i < filteredBrands.size(); i++) {
            BrandDisplay brandDisplay = filteredBrands.get(i);
            Brand brand = brandDisplay.brand;
            
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_brand_square, brandsGrid, false);

            com.google.android.material.card.MaterialCardView card = view.findViewById(R.id.brandCard);
            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView nameText = view.findViewById(R.id.brandName);
            View checkBadge = view.findViewById(R.id.selectedCheckBadge);

            nameText.setText(brand.getName());

            // Display brand logo with dark mode support
            Integer logoResId = getBrandLogo(brand.getSlug());
            if (logoResId != null) {
                if (isDarkModeEnabled()) {
                    if (brand.getSlug().toLowerCase().equals("apple")) {
                        brandIcon.setImageResource(R.drawable.apple_white);
                    } else {
                        brandIcon.setImageResource(logoResId);
                        brandIcon.setColorFilter(ContextCompat.getColor(this, R.color.white_fixed));
                    }
                } else {
                    brandIcon.setImageResource(logoResId);
                }
                brandIcon.setVisibility(View.VISIBLE);
            } else {
                brandIcon.setVisibility(View.GONE);
            }

            if (brandDisplay.isSelected) {
                int primaryColor = ContextCompat.getColor(this, R.color.primary);
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.brand_selected_bg));
                card.setStrokeWidth(0);
                card.setCardElevation(4f);
                nameText.setTextColor(primaryColor);
                if (checkBadge != null) checkBadge.setVisibility(View.VISIBLE);
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));
                card.setStrokeWidth(0);
                card.setCardElevation(1f);
                nameText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                if (checkBadge != null) checkBadge.setVisibility(View.GONE);
            }

            final int index = i;
            final List<BrandDisplay> finalFilteredBrands = filteredBrands;
            View.OnClickListener onBrandClick = v -> {
                for (int j = 0; j < finalFilteredBrands.size(); j++)
                    finalFilteredBrands.get(j).isSelected = (j == index);
                renderFilteredBrands(finalFilteredBrands);
                
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
            card.setOnClickListener(onBrandClick);

            brandsGrid.addView(view);
        }
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

            com.google.android.material.card.MaterialCardView card = view.findViewById(R.id.brandCard);
            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView nameText = view.findViewById(R.id.brandName);
            View checkBadge = view.findViewById(R.id.selectedCheckBadge);

            nameText.setText(brand.getName());

            // Display brand logo if available
            Integer logoResId = getBrandLogo(brand.getSlug());
            if (logoResId != null) {
                // Check if dark mode and apple logo, use white version
                if (brand.getSlug().toLowerCase().equals("apple") && isDarkModeEnabled()) {
                    brandIcon.setImageResource(R.drawable.apple_white);
                } else if (logoResId.equals(R.drawable.apple) && isDarkModeEnabled()) {
                    brandIcon.setImageResource(R.drawable.apple_white);
                } else {
                    brandIcon.setImageResource(logoResId);
                }
                brandIcon.setVisibility(View.VISIBLE);
            } else {
                brandIcon.setVisibility(View.GONE);
            }

            int[] tint = BRAND_TINTS[i % BRAND_TINTS.length];
            int tintFg = ContextCompat.getColor(this, tint[1]);

            if (brandDisplay.isSelected) {
                int primaryColor = ContextCompat.getColor(this, R.color.primary);
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.brand_selected_bg));
                card.setStrokeWidth(dpToPx(2));
                card.setStrokeColor(primaryColor);
                card.setCardElevation(4f);
                nameText.setTextColor(primaryColor);
                if (checkBadge != null) checkBadge.setVisibility(View.VISIBLE);
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));
                card.setStrokeWidth(dpToPx(2));
                card.setStrokeColor(tintFg);
                card.setCardElevation(1f);
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
            card.setOnClickListener(onBrandClick);

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
        
        categoryRepository.fetchActiveCategories(20, 0, new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories, int total) {
                Log.d(TAG, "✓ API SUCCESS: Received " + categories.size() + " categories");
                
                categoriesList.clear();
                
                if (categories != null) {
                    for (Category category : categories) {
                        if (category.isActive()) {
                            int iconRes = getCategoryIcon(category.getName());
                            String imageUrl = getCategoryImageUrl(category);
                            String countText = category.getProductsCount() > 0 
                                ? category.getProductsCount() + " Items" 
                                : "0 Items";
                            
                            Log.d(TAG, "  - " + category.getName() + 
                                " (ID: " + category.getId() + 
                                ", products: " + category.getProductsCount() + 
                                ", featured: " + category.isFeatured() + ")");
                            
                            categoriesList.add(new CategoryItem(
                                category.getName(),
                                countText,
                                imageUrl,
                                iconRes,
                                category.isFeatured(),
                                category.getId(),
                                category.getSlug()
                            ));
                        }
                    }
                }
                
                Log.d(TAG, "=== RENDERING " + categoriesList.size() + " CATEGORIES ===");
                setupAllCategories();
            }
            
            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "✗ API ERROR: " + errorMessage);
                initFallbackCategories();
            }
        });
    }
    
    /**
     * Map category name from API to appropriate icon resource.
     * This converts API category names to UI icons.
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
     * Render all categories in UI grid from API data.
     * This method is called after fetchAllCategories() completes successfully.
     */
    private void setupAllCategories() {
        allCategoriesGrid = findViewById(R.id.allCategoriesGrid);
        if (allCategoriesGrid == null) return;
        // Responsive column count
        allCategoriesGrid.setColumnCount(ScreenUtils.categoryGridColumns(this));

        allCategoriesGrid.removeAllViews();
        
        // Render each category from API
        for (int i = 0; i < categoriesList.size(); i++) {
            CategoryItem item = categoriesList.get(i);
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_category_grid, allCategoriesGrid, false);

            ImageView bgImage         = view.findViewById(R.id.categoryBgImage);
            TextView  title           = view.findViewById(R.id.categoryTitle);
            TextView  count           = view.findViewById(R.id.categoryItemsCount);
            ImageView icon            = view.findViewById(R.id.categorySmallIcon);
            View      hotBadge        = view.findViewById(R.id.hotBadge);

            title.setText(item.name);
            count.setText(item.count);
            icon.setImageResource(item.iconRes);
            if (item.isHot) hotBadge.setVisibility(View.VISIBLE);

            Glide.with(this)
                    .load(item.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(bgImage);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(this, CategoryDetailActivity.class);
                intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY,   item.name);
                intent.putExtra(CategoryDetailActivity.EXTRA_HERO_IMAGE, item.imageUrl);
                intent.putExtra(CategoryDetailActivity.EXTRA_ITEM_COUNT, item.count);
                intent.putExtra("CATEGORY_ID", item.categoryId);
                intent.putExtra("CATEGORY_SLUG", item.categorySlug);
                startActivity(intent);
            });
            allCategoriesGrid.addView(view);
        }
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
        int iconRes;
        boolean isHot;

        CategoryItem(String name, String count, String imageUrl, int iconRes, boolean isHot, 
                    int categoryId, String categorySlug) {  // categoryId is now int
            this.name = name; 
            this.count = count; 
            this.imageUrl = imageUrl;
            this.iconRes = iconRes; 
            this.isHot = isHot;
            this.categoryId = categoryId;
            this.categorySlug = categorySlug;
        }
    }
}
