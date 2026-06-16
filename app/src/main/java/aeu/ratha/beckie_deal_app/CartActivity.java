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
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setupTopBar();
        displayCartItems();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void displayCartItems() {
        List<MainActivity.Product> cartItems = CartManager.getInstance().getCartItems();
        GridLayout cartGrid = findViewById(R.id.cartItemsGrid);
        View emptyState = findViewById(R.id.emptyState);
        View checkoutSection = findViewById(R.id.checkoutSection);
        TextView totalPriceText = findViewById(R.id.totalPriceText);

        if (cartItems.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            cartGrid.setVisibility(View.GONE);
            checkoutSection.setVisibility(View.GONE);
            findViewById(R.id.startShoppingButton).setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        } else {
            emptyState.setVisibility(View.GONE);
            cartGrid.setVisibility(View.VISIBLE);
            checkoutSection.setVisibility(View.VISIBLE);
            cartGrid.removeAllViews();

            for (int i = 0; i < cartItems.size(); i++) {
                MainActivity.Product product = cartItems.get(i);
                View productView = LayoutInflater.from(this).inflate(R.layout.product_card, cartGrid, false);

                ImageView productImage = productView.findViewById(R.id.productImage);
                ImageView favoriteIcon = productView.findViewById(R.id.favoriteIcon);
                TextView productName = productView.findViewById(R.id.productName);
                TextView productPrice = productView.findViewById(R.id.productPrice);
                TextView productRating = productView.findViewById(R.id.productRating);

                productName.setText(product.name);
                productPrice.setText(product.price);
                productRating.setText(product.rating + " (1.2k)");

                Glide.with(this).load(product.imageUrl).centerCrop().placeholder(R.drawable.placeholder_image).into(productImage);

                // Setup favorite icon state
                boolean isFav = FavoriteManager.getInstance().isFavorite(product);
                if (isFav) {
                    favoriteIcon.setImageResource(R.drawable.ic_favorite);
                    favoriteIcon.setColorFilter(0xFFEF4444);
                } else {
                    favoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                    favoriteIcon.setColorFilter(0xFF8C7164);
                }

                final int position = i;
                // Change favorite behavior to "remove from cart" for this specific screen? 
                // No, let's keep it as favorite, but maybe add a delete button?
                // For now, let's make clicking the favorite icon remove it from cart just to have a "remove" function
                favoriteIcon.setOnClickListener(v -> {
                    CartManager.getInstance().removeFromCart(cartItems.get(position));
                    displayCartItems();
                    Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
                });

                cartGrid.addView(productView);
            }

            totalPriceText.setText(String.format(Locale.US, "$%.2f", CartManager.getInstance().getTotalPrice()));
        }
    }
}
