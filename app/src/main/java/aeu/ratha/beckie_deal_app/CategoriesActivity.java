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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private List<Brand> brandsList = new ArrayList<>();
    private GridLayout brandsGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        setupTopBar();
        initBrandsData();
        setupLaptopBrands();
        setupAllCategories();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        ImageView menuIcon = findViewById(R.id.menuIcon);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);
        TextView viewAllBrands = findViewById(R.id.viewAllBrands);

        if (menuIcon != null) menuIcon.setOnClickListener(v ->
                Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());
        if (searchIcon != null) searchIcon.setOnClickListener(v ->
                Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show());
        if (cartIcon != null) cartIcon.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));
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
    }

    private void setupLaptopBrands() {
        brandsGrid = findViewById(R.id.laptopBrandsGrid);
        renderBrands();
    }

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

            nameText.setText(brand.name);

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

            if (brand.isSelected) {
                int orange = Color.parseColor("#F97316");
                card.setCardBackgroundColor(orange);
                nameText.setTextColor(Color.WHITE);
                card.setStrokeWidth(0);
                if (iconContainer != null) {
                    iconContainer.setCardBackgroundColor(orange);
                    iconContainer.setStrokeWidth(0);
                }
                brandIcon.setColorFilter(Color.WHITE);
                iconText.setTextColor(Color.WHITE);
            } else {
                card.setCardBackgroundColor(Color.WHITE);
                nameText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
                card.setStrokeWidth(1);
                card.setStrokeColor(Color.parseColor("#EEEEEE"));
                if (iconContainer != null) {
                    iconContainer.setCardBackgroundColor(Color.WHITE);
                    iconContainer.setStrokeWidth(1);
                    iconContainer.setStrokeColor(Color.parseColor("#EEEEEE"));
                }
                brandIcon.clearColorFilter();
                iconText.setTextColor(ContextCompat.getColor(this, R.color.primary));
            }

            final int index = i;
            view.setOnClickListener(v -> {
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
            });

            brandsGrid.addView(view);
        }
    }

    private void setupAllCategories() {
        GridLayout allCategoriesGrid = findViewById(R.id.allCategoriesGrid);
        if (allCategoriesGrid == null) return;

        List<CategoryItem> categories = new ArrayList<>();
        categories.add(new CategoryItem("Laptops",     "124 Items",
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=600&h=400&fit=crop",
                R.drawable.ic_cat_laptops, true));
        categories.add(new CategoryItem("Smartphones", "210 Items",
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=600&h=400&fit=crop",
                R.drawable.ic_cat_phones, false));
        categories.add(new CategoryItem("Accessories", "456 Items",
                "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&h=400&fit=crop",
                R.drawable.ic_cat_accessories, false));
        categories.add(new CategoryItem("Audio",       "89 Items",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600&h=400&fit=crop",
                R.drawable.ic_cat_accessories, false));
        categories.add(new CategoryItem("Gaming",      "167 Items",
                "https://images.unsplash.com/photo-1593305841991-05c297ba4575?w=600&h=400&fit=crop",
                R.drawable.ic_cat_gaming, false));
        categories.add(new CategoryItem("Wearables",   "54 Items",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&h=400&fit=crop",
                R.drawable.ic_cat_wearables, false));

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

        bottomNav.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        com.google.android.material.card.MaterialCardView container = findViewById(R.id.bottomNavigationContainer);
        if (container != null) container.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.surface));

        bottomNav.setSelectedItemId(R.id.nav_categories);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if      (id == R.id.nav_home)      startActivity(new Intent(this, MainActivity.class));
            else if (id == R.id.nav_favorites) startActivity(new Intent(this, FavoritesActivity.class));
            else if (id == R.id.nav_about)     startActivity(new Intent(this, AboutActivity.class));
            else if (id == R.id.nav_account)   startActivity(new Intent(this, ProfileActivity.class));
            else return true;
            overridePendingTransition(0, 0);
            finish();
            return true;
        });
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
