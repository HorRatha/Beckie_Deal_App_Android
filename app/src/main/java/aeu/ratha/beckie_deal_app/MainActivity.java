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
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.Collections;
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
        setupNewCollectionSection();
        setupTopSellingSection();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupProducts();
    }

    private void initializeMockData() {
        products = new ArrayList<>();
        products.add(new Product("MacBook Air M2", "$1,199.00", "4.9", false, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=500&h=500&fit=crop"));
        products.add(new Product("iPhone 15 Pro", "$999.00", "4.8", false, "https://images.unsplash.com/photo-1592286927505-1fed6c3d2c16?w=500&h=500&fit=crop"));
        products.add(new Product("ASUS ROG Strix", "$1,499.00", "4.7", false, "https://images.unsplash.com/photo-1527814050087-3793815479db?w=500&h=500&fit=crop"));
        products.add(new Product("Samsung S24 Ultra", "$1,299.00", "4.9", false, "https://images.unsplash.com/photo-1511707267537-b85faf00021e?w=500&h=500&fit=crop"));

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
        ImageView menuIcon = findViewById(R.id.menuIcon);
        View cartContainer = findViewById(R.id.cartContainer);
        TextView cartBadge = findViewById(R.id.cartBadge);

        if (menuIcon != null) menuIcon.setOnClickListener(v -> Toast.makeText(this, "Menu opened", Toast.LENGTH_SHORT).show());
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
        if (searchBar != null) searchBar.setOnClickListener(v -> Toast.makeText(this, "Search products...", Toast.LENGTH_SHORT).show());
    }

    private void setupHeroBanner() {
        Button shopNowButton = findViewById(R.id.shopNowButton);
        if (shopNowButton != null) shopNowButton.setOnClickListener(v -> Toast.makeText(this, "Opening Summer Sale!", Toast.LENGTH_SHORT).show());
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
        GridLayout productsGrid = findViewById(R.id.productsGrid);
        if (productsGrid == null) return;
        productsGrid.removeAllViews();
        // Responsive column count
        productsGrid.setColumnCount(ScreenUtils.productGridColumns(this));

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            product.isFavorite = FavoriteManager.getInstance().isFavorite(product);

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
                favoriteIcon.setImageResource(R.drawable.ic_favorite);
                favoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.red_error));
            } else {
                favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                favoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
            }

            final int position = i;
            productView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
                intent.putExtra("product_name", products.get(position).name);
                intent.putExtra("product_price", products.get(position).price);
                intent.putExtra("product_image", products.get(position).imageUrl);
                intent.putExtra("product_rating", products.get(position).rating);
                intent.putExtra("is_favorite", products.get(position).isFavorite);
                startActivity(intent);
            });

            favoriteIcon.setOnClickListener(v -> {
                product.isFavorite = !product.isFavorite;
                if (product.isFavorite) {
                    FavoriteManager.getInstance().addFavorite(product);
                    favoriteIcon.setImageResource(R.drawable.ic_favorite);
                    favoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.red_error));
                    Toast.makeText(this, "Added to Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    FavoriteManager.getInstance().removeFavorite(product);
                    favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                    favoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
                    Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                }
            });

            productsGrid.addView(productView);
        }
    }

    private void setupNewArrivalsBanner() {
        Button discoverButton = findViewById(R.id.discoverButton);
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

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        // Force transparent — Material3 theme overrides XML backgroundTint via colorSurfaceContainer
        bottomNav.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        bottomNav.setItemActiveIndicatorColor(
                android.content.res.ColorStateList.valueOf(
                        androidx.core.content.ContextCompat.getColor(this, R.color.badge_bg)));

        // Also force the card container to pure white so no theme tint leaks through
        com.google.android.material.card.MaterialCardView container =
                findViewById(R.id.bottomNavigationContainer);
        if (container != null) {
            container.setCardBackgroundColor(
                    androidx.core.content.ContextCompat.getColor(this, R.color.surface));
        }

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
        boolean isFavorite;
        Product(String n, String p, String r, boolean f, String i) { name=n; price=p; rating=r; isFavorite=f; imageUrl=i; }
    }

    private static class Category {
        String name; int iconResId; boolean isHighlighted;
        Category(String n, int i, boolean h) { name=n; iconResId=i; isHighlighted=h; }
    }
}
