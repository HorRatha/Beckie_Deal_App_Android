package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayFavorites();
    }

    private void setupTopBar() {
        // menuIcon click handled by AppDrawerHelper
        View cartContainer = findViewById(R.id.cartContainer);
        TextView cartBadge = findViewById(R.id.cartBadge);

        if (cartContainer != null) cartContainer.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        int count = CartManager.getInstance().getTotalUnits();
        if (cartBadge != null) {
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void displayFavorites() {
        List<MainActivity.Product> favoriteProducts = FavoriteManager.getInstance().getFavorites();
        GridLayout favoritesGrid = findViewById(R.id.favoritesGrid);
        View emptyState = findViewById(R.id.emptyState);

        if (favoriteProducts == null || favoriteProducts.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            favoritesGrid.setVisibility(View.GONE);
            findViewById(R.id.exploreButton).setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        } else {
            // Always 2 columns
            favoritesGrid.setColumnCount(2);
            emptyState.setVisibility(View.GONE);
            favoritesGrid.setVisibility(View.VISIBLE);
            favoritesGrid.removeAllViews();

            for (MainActivity.Product product : favoriteProducts) {
                product.isFavorite = true;
                View productView = LayoutInflater.from(this)
                        .inflate(R.layout.product_card, favoritesGrid, false);

                // Bind all fields via shared binder
                ProductCardBinder.bind(this, productView, product);

                productView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ProductDetailActivity.class);
                    intent.putExtra("product_name",  product.name);
                    intent.putExtra("product_price", product.price);
                    intent.putExtra("product_image", product.imageUrl);
                    intent.putExtra("product_rating",product.rating);
                    intent.putExtra("is_favorite",   true);
                    startActivity(intent);
                });

                ImageView favoriteIcon = productView.findViewById(R.id.favoriteIcon);
                if (favoriteIcon != null) {
                    favoriteIcon.setOnClickListener(v -> {
                        FavoriteManager.getInstance().removeFavorite(product);
                        displayFavorites();
                        Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    });
                }

                favoritesGrid.addView(productView);
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        com.google.android.material.card.MaterialCardView container = findViewById(R.id.bottomNavigationContainer);
        if (container != null) container.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.surface));

        bottomNav.setSelectedItemId(R.id.nav_favorites);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_categories) {
                startActivity(new Intent(this, CategoriesActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_favorites) {
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
}
