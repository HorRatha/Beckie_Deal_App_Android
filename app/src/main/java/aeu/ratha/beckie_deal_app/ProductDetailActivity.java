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
    private int cartCount = 0;
    private ImageView detailFavoriteIcon;
    private TextView cartBadge;
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
        if (shareIcon != null) {
            shareIcon.setOnClickListener(v -> 
                Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show());
        }

        // Initialize Cart UI components and redirect to CartActivity
        cartBadge = findViewById(R.id.cartBadge);
        View cartContainer = findViewById(R.id.cartContainer);
        if (cartContainer != null) {
            cartContainer.setOnClickListener(v -> {
                startActivity(new Intent(this, CartActivity.class));
            });
        }
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

        if (nameText != null) nameText.setText(name);
        if (priceText != null) priceText.setText(price);
        if (ratingText != null) ratingText.setText(rating);

        if (productImage != null && imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .centerInside()
                    .placeholder(R.drawable.placeholder_image)
                    .into(productImage);
        }

        // Auto-load official brand logo based on product name
        if (brandLogo != null && name != null) {
            String brandLogoUrl = "";
            if (name.contains("MacBook") || name.contains("iPhone")) {
                brandLogoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/f/fa/Apple_logo_black.svg/1665px-Apple_logo_black.svg.png";
            } else if (name.contains("ASUS") || name.contains("ROG")) {
                brandLogoUrl = "https://logos-world.net/wp-content/uploads/2020/11/ROG-Logo.png";
            } else if (name.contains("Samsung")) {
                brandLogoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/24/Samsung_Logo.svg/2560px-Samsung_Logo.svg.png";
            } else if (name.contains("Dell")) {
                brandLogoUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/18/Dell_logo_2016.svg/1200px-Dell_logo_2016.svg.png";
            }

            if (!brandLogoUrl.isEmpty()) {
                brandLogo.setVisibility(View.VISIBLE);
                Glide.with(this).load(brandLogoUrl).into(brandLogo);
            } else {
                brandLogo.setVisibility(View.GONE);
            }
        }
        
        updateFavoriteUI();
    }

    private void setupActions() {
        Button buyNowButton = findViewById(R.id.buyNowButton);
        if (buyNowButton != null) {
            buyNowButton.setOnClickListener(v -> 
                Toast.makeText(this, "Redirecting to secure checkout...", Toast.LENGTH_SHORT).show());
        }

        // Add to Cart Button Logic
        View addToCartButton = findViewById(R.id.addToCartButton);
        if (addToCartButton != null) {
            addToCartButton.setOnClickListener(v -> {
                cartCount++;
                updateCartUI();
                Toast.makeText(this, nameText() + " added to cart!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, isFavorite ? "Added to Wishlist" : "Removed from Wishlist", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private String nameText() {
        TextView nameText = findViewById(R.id.detailProductName);
        return nameText != null ? nameText.getText().toString() : "Product";
    }

    private void updateCartUI() {
        if (cartBadge != null) {
            if (cartCount > 0) {
                cartBadge.setText(String.valueOf(cartCount));
                cartBadge.setVisibility(View.VISIBLE);
            } else {
                cartBadge.setVisibility(View.GONE);
            }
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
            Intent intent = null;
            if (itemId == R.id.nav_home) {
                intent = new Intent(this, MainActivity.class);
            } else if (itemId == R.id.nav_categories) {
                intent = new Intent(this, CategoriesActivity.class);
            } else if (itemId == R.id.nav_favorites) {
                intent = new Intent(this, FavoritesActivity.class);
            } else if (itemId == R.id.nav_about) {
                intent = new Intent(this, AboutActivity.class);
            } else if (itemId == R.id.nav_account) {
                intent = new Intent(this, ProfileActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}
