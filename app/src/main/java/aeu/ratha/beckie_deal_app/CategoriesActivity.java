package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
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

    private List<Brand> brandsList = new ArrayList<>();
    private GridLayout brandsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        initBrandsData();
        setupLaptopBrands();
        setupAllCategories();
        setupBottomNavigation();
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
    }

    private void initBrandsData() {
        brandsList.clear();
        brandsList.add(new Brand("Apple",     R.drawable.ic_brand_apple,     false));
        brandsList.add(new Brand("ASUS",      R.drawable.ic_brand_asus,      true));
        brandsList.add(new Brand("Dell",      R.drawable.ic_brand_dell,      false));
        brandsList.add(new Brand("HP",        R.drawable.ic_brand_hp,        false));
        brandsList.add(new Brand("Lenovo",    R.drawable.ic_brand_lenovo,    false));
        brandsList.add(new Brand("Acer",      R.drawable.ic_brand_acer,      false));
        brandsList.add(new Brand("Microsoft", R.drawable.ic_brand_microsoft, false));
        brandsList.add(new Brand("Samsung",   R.drawable.ic_brand_samsung,   false));
        brandsList.add(new Brand("MSI",       R.drawable.ic_brand_msi,       false));
    }

    private void setupLaptopBrands() {
        brandsGrid = findViewById(R.id.laptopBrandsGrid);
        // Responsive column count
        if (brandsGrid != null) brandsGrid.setColumnCount(ScreenUtils.brandsGridColumns(this));
        renderBrands();
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

    private void renderBrands() {
        if (brandsGrid == null) return;
        brandsGrid.removeAllViews();

        for (int i = 0; i < brandsList.size(); i++) {
            Brand brand = brandsList.get(i);
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_brand_square, brandsGrid, false);

            MaterialCardView card          = view.findViewById(R.id.brandCard);
            MaterialCardView iconContainer = view.findViewById(R.id.iconContainer);
            ImageView brandIcon            = view.findViewById(R.id.brandIcon);
            TextView  iconText             = view.findViewById(R.id.brandIconText);
            TextView  nameText             = view.findViewById(R.id.brandName);
            TextView  countText            = view.findViewById(R.id.brandProductCount);
            View      checkBadge           = view.findViewById(R.id.selectedCheckBadge);

            nameText.setText(brand.name);

            // Show product count for each brand
            if (countText != null) {
                int productCount = getBrandProductCount(brand.name);
                if (productCount > 0) {
                    countText.setText(productCount + " items");
                    countText.setVisibility(View.VISIBLE);
                }
            }

            if (brand.imageResId != 0) {
                brandIcon.setVisibility(View.VISIBLE);
                iconText.setVisibility(View.GONE);
                brandIcon.setImageResource(brand.imageResId);
            } else {
                brandIcon.setVisibility(View.GONE);
                iconText.setVisibility(View.VISIBLE);
                iconText.setText(brand.name.substring(0,
                        Math.min(brand.name.length(), 3)).toUpperCase());
            }

            int[] tint = BRAND_TINTS[i % BRAND_TINTS.length];
            int tintBg = ContextCompat.getColor(this, tint[0]);
            int tintFg = ContextCompat.getColor(this, tint[1]);

            if (brand.isSelected) {
                int primaryColor = ContextCompat.getColor(this, R.color.primary);
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.brand_selected_bg));
                card.setStrokeWidth(dpToPx(2));
                card.setStrokeColor(primaryColor);
                card.setCardElevation(8f);
                nameText.setTextColor(primaryColor);
                if (countText != null) countText.setTextColor(primaryColor);
                if (iconContainer != null) iconContainer.setCardBackgroundColor(primaryColor);
                brandIcon.setColorFilter(ContextCompat.getColor(this, R.color.white_fixed));
                iconText.setTextColor(ContextCompat.getColor(this, R.color.white_fixed));
                if (checkBadge != null) checkBadge.setVisibility(View.VISIBLE);
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.surface));
                card.setStrokeWidth(dpToPx(1));
                card.setStrokeColor(ContextCompat.getColor(this, R.color.border_color));
                card.setCardElevation(3f);
                nameText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                if (countText != null) countText.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
                if (iconContainer != null) iconContainer.setCardBackgroundColor(tintBg);
                brandIcon.clearColorFilter();
                iconText.setTextColor(tintFg);
                if (checkBadge != null) checkBadge.setVisibility(View.GONE);
            }

            final int index = i;
            View.OnClickListener onBrandClick = v -> {
                for (int j = 0; j < brandsList.size(); j++)
                    brandsList.get(j).isSelected = (j == index);
                renderBrands();
                // Open CategoryDetailActivity pre-filtered to this brand
                Intent intent = new Intent(this, CategoryDetailActivity.class);
                intent.putExtra(CategoryDetailActivity.EXTRA_CATEGORY,     "Laptops");
                intent.putExtra(CategoryDetailActivity.EXTRA_HERO_IMAGE,   "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&h=400&fit=crop");
                intent.putExtra(CategoryDetailActivity.EXTRA_ITEM_COUNT,   "124 Items");
                intent.putExtra(CategoryDetailActivity.EXTRA_BRAND_FILTER, brand.name);
                startActivity(intent);
            };
            // Card is the clickable element (has ripple foreground); the root FrameLayout
            // only exists to float the checkmark badge, so it must not intercept the click.
            card.setOnClickListener(onBrandClick);

            brandsGrid.addView(view);
        }
    }

    /** Count products for a brand across all categories in the catalogue */
    private int getBrandProductCount(String brandName) {
        int count = 0;
        for (java.util.Map.Entry<String, java.util.Map<String, java.util.List<MainActivity.Product>>> catEntry
                : CategoryDetailActivity.CATALOGUE.entrySet()) {
            java.util.List<MainActivity.Product> products = catEntry.getValue().get(brandName);
            if (products != null) count += products.size();
        }
        return count;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setupAllCategories() {
        GridLayout allCategoriesGrid = findViewById(R.id.allCategoriesGrid);
        if (allCategoriesGrid == null) return;
        // Responsive column count
        allCategoriesGrid.setColumnCount(ScreenUtils.categoryGridColumns(this));

        List<CategoryItem> categories = new ArrayList<>();
        categories.add(new CategoryItem("Laptops",     "124 Items",
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&h=400&fit=crop",
                R.drawable.devices_24, true));
        categories.add(new CategoryItem("Smartphones", "210 Items",
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=400&fit=crop",
                R.drawable.mobile_24, false));
        categories.add(new CategoryItem("Accessories", "456 Items",
                "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&h=400&fit=crop",
                R.drawable.widgets_24, false));
        categories.add(new CategoryItem("Audio",       "89 Items",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=400&fit=crop",
                R.drawable.widgets_24, false));
        categories.add(new CategoryItem("Gaming",      "167 Items",
                "https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=600&h=400&fit=crop",
                R.drawable.sports_esports_24, false));
        categories.add(new CategoryItem("Wearables",   "54 Items",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&h=400&fit=crop",
                R.drawable.devices_wearables_24, false));

        allCategoriesGrid.removeAllViews();
        for (CategoryItem item : categories) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.item_category_grid, allCategoriesGrid, false);

            ImageView bgImage  = view.findViewById(R.id.categoryBgImage);
            TextView  title    = view.findViewById(R.id.categoryTitle);
            TextView  count    = view.findViewById(R.id.categoryItemsCount);
            ImageView icon     = view.findViewById(R.id.categorySmallIcon);
            View      hotBadge = view.findViewById(R.id.hotBadge);

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
                startActivity(intent);
            });
            allCategoriesGrid.addView(view);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackground(null);
        bottomNav.setBackgroundTintList(null);
        bottomNav.setItemActiveIndicatorColor(android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(this, R.color.nav_indicator_fill)));

        android.content.res.ColorStateList navTint =
                androidx.core.content.ContextCompat.getColorStateList(this, R.color.bottom_nav_color);
        if (navTint != null) { bottomNav.setItemIconTintList(navTint); bottomNav.setItemTextColor(navTint); }

        bottomNav.setSelectedItemId(R.id.nav_categories);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if      (id == R.id.nav_home)      startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_favorites) startActivity(new Intent(this, FavoritesActivity.class));
            else if (id == R.id.nav_cart)      startActivity(new Intent(this, CartActivity.class));
            else if (id == R.id.nav_account)   startActivity(new Intent(this, ProfileActivity.class));
            else return true;
            overridePendingTransition(0, 0);
            finish();
            return true;
        });

        // Cart badge
        int count = CartManager.getInstance().getTotalUnits();
        if (count > 0) {
            com.google.android.material.badge.BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_cart);
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.red_error));
            badge.setBadgeTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white_fixed));
        } else {
            bottomNav.removeBadge(R.id.nav_cart);
        }
    }

    // ── Data classes ──────────────────────────────────────────────────────────

    public static class Brand {
        String name, imageUrl;
        int    imageResId;
        boolean isSelected;

        Brand(String name, String imageUrl, boolean isSelected) {
            this.name = name; this.imageUrl = imageUrl; this.isSelected = isSelected;
        }
        Brand(String name, int imageResId, boolean isSelected) {
            this.name = name; this.imageResId = imageResId; this.isSelected = isSelected;
        }
    }

    private static class CategoryItem {
        String name, count, imageUrl;
        int    iconRes;
        boolean isHot;

        CategoryItem(String name, String count, String imageUrl, int iconRes, boolean isHot) {
            this.name = name; this.count = count; this.imageUrl = imageUrl;
            this.iconRes = iconRes; this.isHot = isHot;
        }
    }
}
