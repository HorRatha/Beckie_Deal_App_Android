package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        setupTopBar();
        loadProductData();
        setupActions();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) backButton.setOnClickListener(v -> finish());

        ImageView shareIcon = findViewById(R.id.shareIcon);
        if (shareIcon != null) shareIcon.setOnClickListener(v -> Toast.makeText(this, "Share clicked", Toast.LENGTH_SHORT).show());

        ImageView cartIcon = findViewById(R.id.cartIcon);
        if (cartIcon != null) cartIcon.setOnClickListener(v -> Toast.makeText(this, "Cart clicked", Toast.LENGTH_SHORT).show());
    }

    private void loadProductData() {
        String name = getIntent().getStringExtra("product_name");
        String price = getIntent().getStringExtra("product_price");
        String imageUrl = getIntent().getStringExtra("product_image");
        String rating = getIntent().getStringExtra("product_rating");

        TextView nameText = findViewById(R.id.detailProductName);
        TextView priceText = findViewById(R.id.detailProductPrice);
        TextView ratingText = findViewById(R.id.detailProductRating);
        ImageView productImage = findViewById(R.id.detailProductImage);

        if (nameText != null && name != null) nameText.setText(name);
        if (priceText != null && price != null) priceText.setText(price);
        if (ratingText != null && rating != null) ratingText.setText(rating);

        if (productImage != null && imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImage);
        }
    }

    private void setupActions() {
        Button addToCartButton = findViewById(R.id.addToCartButton);
        if (addToCartButton != null) {
            addToCartButton.setOnClickListener(v -> {
                Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show();
            });
        }

        View favButton = findViewById(R.id.favoriteButton);
        if (favButton != null) {
            favButton.setOnClickListener(v -> {
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

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
}
