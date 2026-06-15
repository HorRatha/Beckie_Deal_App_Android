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
        if (cartIcon != null) cartIcon.setOnClickListener(v -> Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show());
    }

    private void setupLaptopBrands() {
        GridLayout brandsGrid = findViewById(R.id.laptopBrandsGrid);
        if (brandsGrid == null) return;
        
        List<Brand> brands = new ArrayList<>();
        brands.add(new Brand("ASUS", "ASUS", false));
        brands.add(new Brand("ROG", "ROG", true));
        brands.add(new Brand("MacBook", "iOS", false));
        brands.add(new Brand("TUF", "TUF", false));
        brands.add(new Brand("Dell", "Dell", false));
        brands.add(new Brand("HP", "HP", false));

        for (Brand brand : brands) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_brand_square, brandsGrid, false);
            
            MaterialCardView card = view.findViewById(R.id.brandCard);
            TextView iconText = view.findViewById(R.id.brandIconText);
            TextView nameText = view.findViewById(R.id.brandName);

            iconText.setText(brand.iconText);
            nameText.setText(brand.name);

            if (brand.isSelected) {
                card.setCardBackgroundColor(0xFFF97316); // Orange
                iconText.setTextColor(Color.WHITE);
                nameText.setTextColor(Color.WHITE);
                card.setStrokeWidth(0);
            }

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

    private static class Brand {
        String name;
        String iconText;
        boolean isSelected;

        Brand(String name, String iconText, boolean isSelected) {
            this.name = name;
            this.iconText = iconText;
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
