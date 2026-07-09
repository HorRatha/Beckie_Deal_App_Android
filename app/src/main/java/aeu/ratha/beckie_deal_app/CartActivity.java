package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

public class CartActivity extends BaseActivity {

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
        if (backButton != null) backButton.setOnClickListener(v -> finish());
    }

    private void setupCheckoutButton() {
        View checkoutButton = findViewById(R.id.checkoutButton);
        if (checkoutButton != null) {
            checkoutButton.setOnClickListener(v ->
                    startActivity(new Intent(this, PayoutActivity.class)));
        }
    }

    private void displayCartItems() {
        List<CartManager.CartItem> cartItems = CartManager.getInstance().getCartItems();
        LinearLayout cartList = findViewById(R.id.cartItemsList);
        View emptyState       = findViewById(R.id.emptyState);
        View checkoutSection  = findViewById(R.id.checkoutSection);
        TextView totalText    = findViewById(R.id.totalPriceText);

        if (cartItems.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            cartList.setVisibility(View.GONE);
            checkoutSection.setVisibility(View.GONE);
            View shopBtn = findViewById(R.id.startShoppingButton);
            if (shopBtn != null) shopBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        } else {
            emptyState.setVisibility(View.GONE);
            cartList.setVisibility(View.VISIBLE);
            checkoutSection.setVisibility(View.VISIBLE);
            cartList.removeAllViews();

            for (CartManager.CartItem item : cartItems) {
                inflateCartItem(cartList, item);
            }

            updateTotal(totalText);
        }
    }

    private void inflateCartItem(LinearLayout parent, CartManager.CartItem item) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_cart, parent, false);

        ImageView image       = v.findViewById(R.id.cartItemImage);
        TextView  name        = v.findViewById(R.id.cartItemName);
        TextView  unitPrice   = v.findViewById(R.id.cartItemUnitPrice);
        TextView  quantityTv  = v.findViewById(R.id.cartItemQuantity);
        TextView  subtotalTv  = v.findViewById(R.id.cartItemSubtotal);
        ImageView decrement   = v.findViewById(R.id.cartItemDecrement);
        ImageView increment   = v.findViewById(R.id.cartItemIncrement);
        ImageView removeBtn   = v.findViewById(R.id.cartItemRemove);

        // Bind static data
        name.setText(item.product.name);
        String priceLabel = item.product.price != null ? item.product.price + " / unit" : "";
        unitPrice.setText(priceLabel);

        Glide.with(this)
                .load(item.product.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.placeholder_image)
                .into(image);

        // Bind dynamic data
        refreshItemRow(item, quantityTv, subtotalTv);

        // ── Increment ──────────────────────────────────────────────────────
        increment.setOnClickListener(btn -> {
            CartManager.getInstance().incrementQuantity(item.product);
            refreshItemRow(item, quantityTv, subtotalTv);
            updateTotal(findViewById(R.id.totalPriceText));
        });

        // ── Decrement ──────────────────────────────────────────────────────
        decrement.setOnClickListener(btn -> {
            if (item.quantity <= 1) {
                // Last unit — remove from cart entirely
                CartManager.getInstance().removeFromCart(item.product);
                Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
                displayCartItems();   // full re-render to handle empty state
            } else {
                CartManager.getInstance().decrementQuantity(item.product);
                refreshItemRow(item, quantityTv, subtotalTv);
                updateTotal(findViewById(R.id.totalPriceText));
            }
        });

        // ── Remove ─────────────────────────────────────────────────────────
        removeBtn.setOnClickListener(btn -> {
            CartManager.getInstance().removeFromCart(item.product);
            Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
            displayCartItems();
        });

        parent.addView(v);
    }

    /** Update the quantity label and subtotal for a single cart row (no full re-render). */
    private void refreshItemRow(CartManager.CartItem item, TextView quantityTv, TextView subtotalTv) {
        quantityTv.setText(String.valueOf(item.quantity));
        try {
            String priceStr = item.product.price.replace("$", "").replace(",", "");
            double subtotal = Double.parseDouble(priceStr) * item.quantity;
            subtotalTv.setText(String.format(Locale.US, "$%.2f", subtotal));
        } catch (Exception e) {
            subtotalTv.setText(item.product.price != null ? item.product.price : "");
        }
    }

    /** Refresh the grand total text in the checkout bar. */
    private void updateTotal(TextView totalText) {
        if (totalText == null) return;
        totalText.setText(String.format(Locale.US, "$%.2f",
                CartManager.getInstance().getTotalPrice()));
    }
}
