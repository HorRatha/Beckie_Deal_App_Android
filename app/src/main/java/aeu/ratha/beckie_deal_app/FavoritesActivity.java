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
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        setupTopBar();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayFavorites();
    }

    private void setupTopBar() {
        ImageView menuIcon = findViewById(R.id.menuIcon);
        ImageView cartIcon = findViewById(R.id.cartIcon);

        if (menuIcon != null) menuIcon.setOnClickListener(v -> Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());
        if (cartIcon != null) cartIcon.setOnClickListener(v -> Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show());
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
            emptyState.setVisibility(View.GONE);
            favoritesGrid.setVisibility(View.VISIBLE);
            favoritesGrid.removeAllViews();

            for (MainActivity.Product product : favoriteProducts) {
                View productView = LayoutInflater.from(this).inflate(R.layout.product_card, favoritesGrid, false);

                ImageView productImage = productView.findViewById(R.id.productImage);
                ImageView favoriteIcon = productView.findViewById(R.id.favoriteIcon);
                TextView productName = productView.findViewById(R.id.productName);
                TextView productPrice = productView.findViewById(R.id.productPrice);
                TextView productRating = productView.findViewById(R.id.productRating);

                productName.setText(product.name);
                productPrice.setText(product.price);
                productRating.setText(product.rating + " (1.2k)");

                Glide.with(this).load(product.imageUrl).centerCrop().placeholder(R.drawable.placeholder_image).into(productImage);

                favoriteIcon.setImageResource(R.drawable.ic_favorite);
                favoriteIcon.setColorFilter(0xFFEF4444);

                productView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ProductDetailActivity.class);
                    intent.putExtra("product_name", product.name);
                    intent.putExtra("product_price", product.price);
                    intent.putExtra("product_image", product.imageUrl);
                    intent.putExtra("product_rating", product.rating);
                    intent.putExtra("is_favorite", true);
                    startActivity(intent);
                });

                favoriteIcon.setOnClickListener(v -> {
                    FavoriteManager.getInstance().removeFavorite(product);
                    displayFavorites();
                    Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                });

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
