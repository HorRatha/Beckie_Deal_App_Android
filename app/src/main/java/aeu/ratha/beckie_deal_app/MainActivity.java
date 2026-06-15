package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity {

    private List<Product> products;
    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeMockData();
        setupTopBar();
        setupSearchBar();
        setupHeroBanner();
        setupCategories();
        setupProducts();
        setupNewArrivalsBanner();
        setupBottomNavigation();
    }

    private void initializeMockData() {
        products = new ArrayList<>();
        products.add(new Product("MacBook Air M2", "$1,199.00", "4.9", true, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop"));
        products.add(new Product("iPhone 15 Pro", "$999.00", "4.8", false, "https://images.unsplash.com/photo-1592286927505-1fed6c3d2c16?w=500&h=500&fit=crop"));
        products.add(new Product("ASUS ROG Strix", "$1,499.00", "4.7", false, "https://images.unsplash.com/photo-1527814050087-3793815479db?w=500&h=500&fit=crop"));
        products.add(new Product("Samsung S24 Ultra", "$1,299.00", "4.9", false, "https://images.unsplash.com/photo-1511707267537-b85faf00021e?w=500&h=500&fit=crop"));

        categories = new ArrayList<>();
        categories.add(new Category("Laptops", R.drawable.ic_cat_laptops, false));
        categories.add(new Category("Phones", R.drawable.ic_cat_phones, false));
        categories.add(new Category("Accessories", R.drawable.ic_cat_accessories, false));
        categories.add(new Category("Gaming", R.drawable.ic_cat_gaming, true));
        categories.add(new Category("Wearables", R.drawable.ic_cat_wearables, false));
    }

    private void setupTopBar() {
        ImageView menuIcon = findViewById(R.id.menuIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);
        menuIcon.setOnClickListener(v -> Toast.makeText(this, "Menu opened", Toast.LENGTH_SHORT).show());
        cartIcon.setOnClickListener(v -> Toast.makeText(this, "Shopping Cart", Toast.LENGTH_SHORT).show());
    }

    private void setupSearchBar() {
        findViewById(R.id.searchBar).setOnClickListener(v -> Toast.makeText(this, "Search products...", Toast.LENGTH_SHORT).show());
    }

    private void setupHeroBanner() {
        Button shopNowButton = findViewById(R.id.shopNowButton);
        shopNowButton.setOnClickListener(v -> Toast.makeText(this, "Opening Summer Sale!", Toast.LENGTH_SHORT).show());
    }

    private void setupCategories() {
        TextView viewAllCategories = findViewById(R.id.viewAllCategories);
        if (viewAllCategories != null) {
            viewAllCategories.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CategoriesActivity.class)));
        }

        LinearLayout categoriesContainer = findViewById(R.id.categoriesContainer);
        categoriesContainer.removeAllViews();

        for (Category category : categories) {
            View categoryView = LayoutInflater.from(this).inflate(R.layout.category_item, categoriesContainer, false);
            MaterialCardView categoryCard = categoryView.findViewById(R.id.categoryCard);
            ImageView categoryIcon = categoryView.findViewById(R.id.categoryIcon);
            TextView categoryName = categoryView.findViewById(R.id.categoryName);

            categoryIcon.setImageResource(category.iconResId);
            categoryName.setText(category.name);

            if (category.isHighlighted) {
                categoryCard.setCardBackgroundColor(0xFFFEF2F2);
                categoryCard.setStrokeColor(0xFFFECACA);
            }

            categoryView.setOnClickListener(v -> {
                if (category.name.equals("Laptops")) {
                    startActivity(new Intent(MainActivity.this, CategoriesActivity.class));
                } else {
                    Toast.makeText(this, "Category: " + category.name, Toast.LENGTH_SHORT).show();
                }
            });

            categoriesContainer.addView(categoryView);
        }
    }

    private void setupProducts() {
        GridLayout productsGrid = findViewById(R.id.productsGrid);
        productsGrid.removeAllViews();

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            View productView = LayoutInflater.from(this).inflate(R.layout.product_card, productsGrid, false);

            ImageView productImage = productView.findViewById(R.id.productImage);
            ImageView favoriteIcon = productView.findViewById(R.id.favoriteIcon);
            TextView productName = productView.findViewById(R.id.productName);
            TextView productPrice = productView.findViewById(R.id.productPrice);
            TextView productRating = productView.findViewById(R.id.productRating);

            productName.setText(product.name);
            productPrice.setText(product.price);
            productRating.setText(product.rating + " (1.2k)");

            Glide.with(this).load(product.imageUrl).centerCrop().placeholder(R.drawable.placeholder_image).into(productImage);

            if (product.isFavorite) {
                favoriteIcon.setImageResource(android.R.drawable.btn_star_big_on);
                favoriteIcon.setColorFilter(0xFFEF4444);
            }

            final int position = i;
            productView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_name", products.get(position).name);
                intent.putExtra("product_price", products.get(position).price);
                intent.putExtra("product_image", products.get(position).imageUrl);
                intent.putExtra("product_rating", products.get(position).rating);
                startActivity(intent);
            });

            favoriteIcon.setOnClickListener(v -> {
                products.get(position).isFavorite = !products.get(position).isFavorite;
                if (products.get(position).isFavorite) {
                    favoriteIcon.setImageResource(android.R.drawable.btn_star_big_on);
                    favoriteIcon.setColorFilter(0xFFEF4444);
                } else {
                    favoriteIcon.setImageResource(android.R.drawable.btn_star_big_off);
                    favoriteIcon.setColorFilter(0xFF8C7164);
                }
            });

            productsGrid.addView(productView);
        }
    }

    private void setupNewArrivalsBanner() {
        Button discoverButton = findViewById(R.id.discoverButton);
        discoverButton.setOnClickListener(v -> Toast.makeText(this, "Discover New Arrivals!", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
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

    private static class Product {
        String name, price, rating, imageUrl;
        boolean isFavorite;
        Product(String n, String p, String r, boolean f, String i) { name=n; price=p; rating=r; isFavorite=f; imageUrl=i; }
    }

    private static class Category {
        String name; int iconResId; boolean isHighlighted;
        Category(String n, int i, boolean h) { name=n; iconResId=i; isHighlighted=h; }
    }
}
