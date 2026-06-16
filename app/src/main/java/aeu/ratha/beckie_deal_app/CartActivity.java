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
        setupCheckoutButton();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }

    private void setupCheckoutButton() {
        View checkoutButton = findViewById(R.id.checkoutButton);
        if (checkoutButton != null) {
            checkoutButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, PayoutActivity.class);
                startActivity(intent);
            });
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

                // Setup behavior: remove from cart
                favoriteIcon.setImageResource(android.R.drawable.ic_menu_delete);
                favoriteIcon.setColorFilter(0xFFEF4444);

                final int position = i;
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
