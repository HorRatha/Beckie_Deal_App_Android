package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProductDetailActivity extends AppCompatActivity {

    private boolean isFavorite = false;
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

    @Override
    protected void onResume() {
        super.onResume();
        updateCartUI();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) backButton.setOnClickListener(v -> finish());

        ImageView shareIcon = findViewById(R.id.shareIcon);
        if (shareIcon != null) {
            shareIcon.setOnClickListener(v -> 
                Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show());
        }

        cartBadge = findViewById(R.id.cartBadge);
        View cartContainer = findViewById(R.id.cartContainer);
        if (cartContainer != null) {
            cartContainer.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        }
    }

    private void loadProductData() {
        String name = getIntent().getStringExtra("product_name");
        String price = getIntent().getStringExtra("product_price");
        String imageUrl = getIntent().getStringExtra("product_image");
        String rating = getIntent().getStringExtra("product_rating");
        
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
            Glide.with(this).load(imageUrl).centerInside().placeholder(R.drawable.placeholder_image).into(productImage);
        }

        if (brandLogo != null && name != null) {
            String lowerName = name.toLowerCase();
            brandLogo.setVisibility(View.VISIBLE);
            
            if (lowerName.contains("asus")) {
                brandLogo.setImageResource(R.drawable.ic_brand_asus);
            } else if (lowerName.contains("apple") || lowerName.contains("macbook") || lowerName.contains("iphone")) {
                brandLogo.setImageResource(R.drawable.ic_brand_apple);
            } else if (lowerName.contains("msi")) {
                brandLogo.setImageResource(R.drawable.ic_brand_msi);
            } else if (lowerName.contains("rog")) {
                brandLogo.setImageResource(R.drawable.ic_brand_rog);
            } else if (lowerName.contains("samsung")) {
                brandLogo.setImageResource(R.drawable.ic_brand_samsung);
            } else if (lowerName.contains("dell")) {
                brandLogo.setImageResource(R.drawable.ic_brand_dell);
            } else {
                brandLogo.setVisibility(View.GONE);
            }
        }
        
        updateFavoriteUI();
    }

    private void setupActions() {
        View addToCartButton = findViewById(R.id.addToCartButton);
        if (addToCartButton != null) {
            addToCartButton.setOnClickListener(v -> {
                CartManager.getInstance().addToCart(currentProduct);
                updateCartUI();
                Toast.makeText(this, "Added to cart!", Toast.LENGTH_SHORT).show();
            });
        }

        View favButton = findViewById(R.id.favoriteButton);
        if (favButton != null) {
            favButton.setOnClickListener(v -> {
                isFavorite = !isFavorite;
                if (isFavorite) FavoriteManager.getInstance().addFavorite(currentProduct);
                else FavoriteManager.getInstance().removeFavorite(currentProduct);
                updateFavoriteUI();
            });
        }
    }

    private void updateCartUI() {
        if (cartBadge != null) {
            int count = CartManager.getInstance().getCartCount();
            cartBadge.setText(String.valueOf(count));
            cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void updateFavoriteUI() {
        if (detailFavoriteIcon != null) {
            detailFavoriteIcon.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            detailFavoriteIcon.setColorFilter(ContextCompat.getColor(this, isFavorite ? R.color.red_error : R.color.text_primary));
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        com.google.android.material.card.MaterialCardView container = findViewById(R.id.bottomNavigationContainer);
        if (container != null) container.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.surface));

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) startActivity(new Intent(this, MainActivity.class));
            else if (itemId == R.id.nav_categories) startActivity(new Intent(this, CategoriesActivity.class));
            else if (itemId == R.id.nav_favorites) startActivity(new Intent(this, FavoritesActivity.class));
            else if (itemId == R.id.nav_about) startActivity(new Intent(this, AboutActivity.class));
            else if (itemId == R.id.nav_account) startActivity(new Intent(this, ProfileActivity.class));
            finish();
            return true;
        });
    }
}
