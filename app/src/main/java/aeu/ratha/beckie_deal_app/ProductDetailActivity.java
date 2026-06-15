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

    private boolean isFavorite = false;
    private ImageView detailFavoriteIcon;
    private MainActivity.Product currentProduct;

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
        
        // Construct a temporary product object for lookup/management
        currentProduct = new MainActivity.Product(name, price, rating, false, imageUrl);
        isFavorite = FavoriteManager.getInstance().isFavorite(currentProduct);

        TextView nameText = findViewById(R.id.detailProductName);
        TextView priceText = findViewById(R.id.detailProductPrice);
        TextView ratingText = findViewById(R.id.detailProductRating);
        ImageView productImage = findViewById(R.id.detailProductImage);
        ImageView brandLogo = findViewById(R.id.detailBrandLogo);
        detailFavoriteIcon = findViewById(R.id.detailFavoriteIcon);

        if (nameText != null && name != null) nameText.setText(name);
        if (priceText != null && price != null) priceText.setText(price);
        if (ratingText != null && rating != null) ratingText.setText(rating);

        if (productImage != null && imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImage);
        }

        // Load Official Brand Logo
        if (brandLogo != null && name != null) {
            String brandLogoUrl = "";
            if (name.contains("MacBook") || name.contains("iPhone")) {
                brandLogoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Apple_logo_black.svg/1665px-Apple_logo_black.svg.png";
            } else if (name.contains("ASUS") || name.contains("ROG")) {
                brandLogoUrl = "https://logos-world.net/wp-content/uploads/2020/11/ROG-Logo.png";
            } else if (name.contains("Samsung")) {
                brandLogoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Samsung_Logo.svg/2560px-Samsung_Logo.svg.png";
            }

            if (!brandLogoUrl.isEmpty()) {
                Glide.with(this).load(brandLogoUrl).into(brandLogo);
            }
        }
        
        updateFavoriteUI();
    }

    private void setupActions() {
        Button addToCartButton = findViewById(R.id.addToCartButton);
        if (addToCartButton != null) {
            addToCartButton.setOnClickListener(v -> {
                Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show();
            });
        }

        Button buyNowButton = findViewById(R.id.buyNowButton);
        if (buyNowButton != null) {
            buyNowButton.setOnClickListener(v -> {
                Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
            });
        }

        View favButton = findViewById(R.id.favoriteButton);
        if (favButton != null) {
            favButton.setOnClickListener(v -> {
                isFavorite = !isFavorite;
                if (isFavorite) {
                    FavoriteManager.getInstance().addFavorite(currentProduct);
                } else {
                    FavoriteManager.getInstance().removeFavorite(currentProduct);
                }
                updateFavoriteUI();
                String msg = isFavorite ? "Added to favorites" : "Removed from favorites";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void updateFavoriteUI() {
        if (detailFavoriteIcon != null) {
            if (isFavorite) {
                detailFavoriteIcon.setImageResource(R.drawable.ic_favorite);
                detailFavoriteIcon.setColorFilter(0xFFEF4444);
            } else {
                detailFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                detailFavoriteIcon.setColorFilter(0xFF251913);
            }
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
