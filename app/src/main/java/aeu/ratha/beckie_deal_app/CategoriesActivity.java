package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        setupTopBar();
        setupLaptopBrands();
        setupAllCategories();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        ImageView menuIcon = findViewById(R.id.menuIcon);
        ImageView searchIcon = findViewById(R.id.searchIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);

        if (menuIcon != null) menuIcon.setOnClickListener(v -> Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());
        if (searchIcon != null) searchIcon.setOnClickListener(v -> Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show());
        if (cartIcon != null) cartIcon.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });
    }

    private void setupLaptopBrands() {
        GridLayout brandsGrid = findViewById(R.id.laptopBrandsGrid);
        TextView viewAllLaptops = findViewById(R.id.viewAllLaptops);
        
        if (viewAllLaptops != null) {
            viewAllLaptops.setOnClickListener(v -> {
                Intent intent = new Intent(this, AllBrandsActivity.class);
                startActivity(intent);
            });
        }

        if (brandsGrid == null) return;
        
        List<Brand> brands = new ArrayList<>();
        brands.add(new Brand("ASUS", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/ASUS_Logo.svg/2560px-ASUS_Logo.svg.png", false));
        brands.add(new Brand("ROG", "https://logos-world.net/wp-content/uploads/2020/11/ROG-Logo.png", true));
        brands.add(new Brand("Apple", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Apple_logo_black.svg/1665px-Apple_logo_black.svg.png", false));
        brands.add(new Brand("Dell", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Dell_logo_2016.svg/1200px-Dell_logo_2016.svg.png", false));
        brands.add(new Brand("HP", "https://upload.wikimedia.org/wikipedia/commons/thumb/a/ad/HP_logo_2012.svg/1200px-HP_logo_2012.svg.png", false));
        brands.add(new Brand("MSI", "https://upload.wikimedia.org/wikipedia/en/thumb/f/ff/MSI_Dragon_Logo.svg/1200px-MSI_Dragon_Logo.svg.png", false));

        brandsGrid.removeAllViews();
        for (Brand brand : brands) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_brand_square, brandsGrid, false);
            
            MaterialCardView card = view.findViewById(R.id.brandCard);
            ImageView brandIcon = view.findViewById(R.id.brandIcon);
            TextView iconText = view.findViewById(R.id.brandIconText);
            TextView nameText = view.findViewById(R.id.brandName);

            if (brand.imageUrl != null && !brand.imageUrl.isEmpty()) {
                brandIcon.setVisibility(View.VISIBLE);
                iconText.setVisibility(View.GONE);
                Glide.with(this)
                        .load(brand.imageUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .into(brandIcon);
            } else {
                brandIcon.setVisibility(View.GONE);
                iconText.setVisibility(View.VISIBLE);
                iconText.setText(brand.name.substring(0, Math.min(brand.name.length(), 2)).toUpperCase());
            }
            
            nameText.setText(brand.name);

            if (brand.isSelected) {
                card.setCardBackgroundColor(0xFFF97316); // Orange
                brandIcon.setColorFilter(Color.WHITE);
                iconText.setTextColor(Color.WHITE);
                nameText.setTextColor(Color.WHITE);
                card.setStrokeWidth(0);
            }

            // Make brand card clickable
            view.setOnClickListener(v -> {
                Toast.makeText(this, "Showing products for " + brand.name, Toast.LENGTH_SHORT).show();
                // Future: startActivity(new Intent(this, ProductListActivity.class).putExtra("brand", brand.name));
            });

            brandsGrid.addView(view);
        }
    }

    private void setupAllCategories() {
        GridLayout allCategoriesGrid = findViewById(R.id.allCategoriesGrid);
        if (allCategoriesGrid == null) return;
        
        List<CategoryItem> categories = new ArrayList<>();
        categories.add(new CategoryItem("Laptops", "124 Items", R.drawable.ic_cat_laptops, true));
        categories.add(new CategoryItem("Smartphones", "210 Items", R.drawable.ic_cat_phones, false));
        categories.add(new CategoryItem("Accessories", "456 Items", R.drawable.ic_cat_accessories, false));
        categories.add(new CategoryItem("Audio", "89 Items", android.R.drawable.ic_lock_silent_mode, false)); 
        categories.add(new CategoryItem("Gaming", "167 Items", R.drawable.ic_cat_gaming, false));
        categories.add(new CategoryItem("Wearables", "54 Items", R.drawable.ic_cat_wearables, false));

        allCategoriesGrid.removeAllViews();
        for (CategoryItem item : categories) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_category_grid, allCategoriesGrid, false);
            
            TextView title = view.findViewById(R.id.categoryTitle);
            TextView count = view.findViewById(R.id.categoryItemsCount);
            ImageView icon = view.findViewById(R.id.categorySmallIcon);
            View hotBadge = view.findViewById(R.id.hotBadge);

            title.setText(item.name);
            count.setText(item.count);
            icon.setImageResource(item.iconRes);
            
            if (item.isHot) {
                hotBadge.setVisibility(View.VISIBLE);
            }

            // Make category card clickable
            view.setOnClickListener(v -> {
                Toast.makeText(this, "Opening " + item.name + " category", Toast.LENGTH_SHORT).show();
                // Future: startActivity(new Intent(this, ProductListActivity.class).putExtra("category", item.name));
            });

            allCategoriesGrid.addView(view);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;
        
        bottomNav.setSelectedItemId(R.id.nav_categories);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_categories) {
                return true;
            } else if (itemId == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_about) {
                startActivity(new Intent(this, AboutActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    public static class Brand {
        String name;
        String imageUrl;
        boolean isSelected;

        Brand(String name, String imageUrl, boolean isSelected) {
            this.name = name;
            this.imageUrl = imageUrl;
            this.isSelected = isSelected;
        }
    }

    private static class CategoryItem {
        String name;
        String count;
        int iconRes;
        boolean isHot;

        CategoryItem(String name, String count, int iconRes, boolean isHot) {
            this.name = name;
            this.count = count;
            this.iconRes = iconRes;
            this.isHot = isHot;
        }
    }
}
