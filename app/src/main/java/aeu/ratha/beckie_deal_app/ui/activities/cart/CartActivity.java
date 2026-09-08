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
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;
import java.util.Locale;

public class CartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupBottomNavigation();
        setupCouponReminder();
        displayCartItems();
        setupCheckoutButton();
        setupEntranceAnimation();
    }

    /** Fades/slides the static chrome (empty state or checkout bar) into place on load. */
    private void setupEntranceAnimation() {
        View emptyState      = findViewById(R.id.emptyState);
        View couponReminder  = findViewById(R.id.couponReminderCard);
        View checkoutSection = findViewById(R.id.checkoutSection);
        if (emptyState != null && emptyState.getVisibility() == View.VISIBLE) {
            UiAnimations.fadeSlideUp(emptyState, 60);
        }
        if (couponReminder != null && couponReminder.getVisibility() == View.VISIBLE) {
            UiAnimations.fadeSlideUp(couponReminder, 80);
        }
        if (checkoutSection != null && checkoutSection.getVisibility() == View.VISIBLE) {
            UiAnimations.fadeSlideUp(checkoutSection, 100);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayCartItems();
        updateCartBadge();
    }

    private void setupTopBar() {
        // menuIcon click handled by AppDrawerHelper
        View notificationContainer = findViewById(R.id.notificationContainer);

        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        UiAnimations.pressBounce(notificationContainer);
    }

    private void setupCheckoutButton() {
        View checkoutButton = findViewById(R.id.checkoutButton);
        if (checkoutButton != null) {
            checkoutButton.setOnClickListener(v ->
                    startActivity(new Intent(this, PayoutActivity.class)));
            UiAnimations.pressBounce(checkoutButton);
        }
    }

    private void setupCouponReminder() {
        View couponReminderCard = findViewById(R.id.couponReminderCard);
        View couponReminderButton = findViewById(R.id.couponReminderButton);
        
        if (couponReminderButton != null) {
            couponReminderButton.setOnClickListener(v -> {
                // Navigate to checkout where user can apply coupon
                startActivity(new Intent(this, PayoutActivity.class));
            });
            UiAnimations.pressBounce(couponReminderButton);
        }
    }

    private void displayCartItems() {
        List<CartManager.CartItem> cartItems = CartManager.getInstance().getCartItems();
        LinearLayout cartList = findViewById(R.id.cartItemsList);
        View emptyState       = findViewById(R.id.emptyState);
        View couponReminder   = findViewById(R.id.couponReminderCard);
        View checkoutSection  = findViewById(R.id.checkoutSection);
        TextView totalText    = findViewById(R.id.totalPriceText);

        if (cartItems.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            cartList.setVisibility(View.GONE);
            couponReminder.setVisibility(View.GONE);
            checkoutSection.setVisibility(View.GONE);
            View shopBtn = findViewById(R.id.startShoppingButton);
            if (shopBtn != null) {
                shopBtn.setOnClickListener(v -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
                UiAnimations.pressBounce(shopBtn);
            }
        } else {
            emptyState.setVisibility(View.GONE);
            cartList.setVisibility(View.VISIBLE);
            couponReminder.setVisibility(View.VISIBLE);
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
        UiAnimations.pressBounce(decrement, increment, removeBtn);

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
            updateCartBadge();
        });

        // ── Decrement ──────────────────────────────────────────────────────
        decrement.setOnClickListener(btn -> {
            if (item.quantity <= 1) {
                // Last unit — remove from cart entirely
                CartManager.getInstance().removeFromCart(item.product);
                Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
                displayCartItems();   // full re-render to handle empty state
                updateCartBadge();
            } else {
                CartManager.getInstance().decrementQuantity(item.product);
                refreshItemRow(item, quantityTv, subtotalTv);
                updateTotal(findViewById(R.id.totalPriceText));
                updateCartBadge();
            }
        });

        // ── Remove ─────────────────────────────────────────────────────────
        removeBtn.setOnClickListener(btn -> {
            CartManager.getInstance().removeFromCart(item.product);
            Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
            displayCartItems();
            updateCartBadge();
        });

        parent.addView(v);
    }

    /** Update the quantity label and subtotal for a single cart row (no full re-render). */
    private void refreshItemRow(CartManager.CartItem item, TextView quantityTv, TextView subtotalTv) {
        // Zero-padded to two digits so the stepper width never jumps (01, 02 ... 12)
        quantityTv.setText(String.format(Locale.US, "%02d", item.quantity));
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

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.CART);
    }

    private void updateCartBadge() {
        BottomNavHelper.updateCartBadge(this);
    }
}
