package aeu.ratha.beckie_deal_app.ui.activities.cart;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Cart;
import aeu.ratha.beckie_deal_app.data.models.CartItem;
import aeu.ratha.beckie_deal_app.data.models.CartItemResponse;
import aeu.ratha.beckie_deal_app.data.models.CartResponse;
import aeu.ratha.beckie_deal_app.data.repository.CartRepository;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ImageUrlHelper;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.activities.cart.PayoutActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

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

    private CartRepository cartRepository;
    private Cart currentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRepository = new CartRepository(this);

        AppDrawerHelper drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        // Refresh drawer with latest user data from API to ensure sidebar shows correct user across all pages
        drawerHelper.refreshDrawerWithLatestUserData(this);
        setupTopBar();
        setupBottomNavigation();
        setupCouponReminder();
        setupCheckoutButton();
        loadCartFromAPI();
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
        loadCartFromAPI();
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

    /**
     * Load cart from API (dynamic data).
     */
    private void loadCartFromAPI() {
        // Check if user is logged in
        boolean isLoggedIn = aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.isUserLoggedIn(this);
        
        if (!isLoggedIn) {
            // Show login prompt
            displayCartItems(null);
            return;
        }

        // Show loading (optional - if layout has loading indicator)
        // ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);
        // if (loadingIndicator != null) loadingIndicator.setVisibility(View.VISIBLE);

        cartRepository.getCart(new CartRepository.CartCallback() {
            @Override
            public void onSuccess(CartResponse response) {
                // if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                
                if (response.isSuccess() && response.hasData()) {
                    currentCart = response.getData();
                    displayCartItems(currentCart);
                } else {
                    displayCartItems(null);
                }
            }

            @Override
            public void onError(String error) {
                // if (loadingIndicator != null) loadingIndicator.setVisibility(View.GONE);
                Toast.makeText(CartActivity.this, "Failed to load cart: " + error, Toast.LENGTH_SHORT).show();
                displayCartItems(null);
            }
        });
    }

    private void displayCartItems(Cart cart) {
        LinearLayout cartList = findViewById(R.id.cartItemsList);
        View emptyState       = findViewById(R.id.emptyState);
        View couponReminder   = findViewById(R.id.couponReminderCard);
        View checkoutSection  = findViewById(R.id.checkoutSection);
        TextView totalText    = findViewById(R.id.totalPriceText);
        
        // Check if user is logged in
        boolean isLoggedIn = aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.isUserLoggedIn(this);

        if (cart == null || cart.isEmpty() || !isLoggedIn) {
            emptyState.setVisibility(View.VISIBLE);
            cartList.setVisibility(View.GONE);
            couponReminder.setVisibility(View.GONE);
            checkoutSection.setVisibility(View.GONE);
            
            // Update empty state UI based on login status
            setupEmptyState(isLoggedIn);
        } else {
            emptyState.setVisibility(View.GONE);
            cartList.setVisibility(View.VISIBLE);
            couponReminder.setVisibility(View.VISIBLE);
            checkoutSection.setVisibility(View.VISIBLE);
            cartList.removeAllViews();

            for (CartItem item : cart.getItems()) {
                inflateCartItem(cartList, item);
            }

            updateTotal(totalText, cart);
        }
    }
    
    /**
     * Setup empty state UI based on whether user is logged in or not.
     * Guest users see login prompt, logged-in users see "start shopping" message.
     */
    private void setupEmptyState(boolean isLoggedIn) {
        ImageView icon = findViewById(R.id.emptyStateIcon);
        TextView title = findViewById(R.id.emptyStateTitle);
        TextView message = findViewById(R.id.emptyStateMessage);
        View startShoppingBtn = findViewById(R.id.startShoppingButton);
        View loginButtonsContainer = findViewById(R.id.loginButtonsContainer);
        View loginBtn = findViewById(R.id.loginButton);
        View registerBtn = findViewById(R.id.registerButton);
        
        if (!isLoggedIn) {
            // Guest user - show login prompt
            if (icon != null) icon.setImageResource(R.drawable.ic_lock);
            if (title != null) title.setText("Login to Cart");
            if (message != null) message.setText("Sign in to see items you've added and continue shopping.");
            if (startShoppingBtn != null) startShoppingBtn.setVisibility(View.GONE);
            if (loginButtonsContainer != null) loginButtonsContainer.setVisibility(View.VISIBLE);
            
            if (loginBtn != null) {
                loginBtn.setOnClickListener(v -> {
                    startActivity(new Intent(this, aeu.ratha.beckie_deal_app.ui.activities.auth.LoginActivity.class));
                });
                UiAnimations.pressBounce(loginBtn);
            }
            
            if (registerBtn != null) {
                registerBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(this, aeu.ratha.beckie_deal_app.ui.activities.auth.LoginActivity.class);
                    intent.putExtra("openRegister", true); // Open register tab
                    startActivity(intent);
                });
                UiAnimations.pressBounce(registerBtn);
            }
        } else {
            // Logged in user - show regular empty cart message
            if (icon != null) icon.setImageResource(R.drawable.shopping_cart_24);
            if (title != null) title.setText("Your cart is empty");
            if (message != null) message.setText("Looks like you haven't added anything yet.");
            if (startShoppingBtn != null) {
                startShoppingBtn.setVisibility(View.VISIBLE);
                startShoppingBtn.setOnClickListener(v -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
                UiAnimations.pressBounce(startShoppingBtn);
            }
            if (loginButtonsContainer != null) loginButtonsContainer.setVisibility(View.GONE);
        }
    }

    private void inflateCartItem(LinearLayout parent, CartItem item) {
        View v = LayoutInflater.from(this).inflate(R.layout.item_cart, parent, false);

        ImageView image         = v.findViewById(R.id.cartItemImage);
        TextView  name          = v.findViewById(R.id.cartItemName);
        TextView  skuTv         = v.findViewById(R.id.cartItemSku);
        TextView  unitPrice     = v.findViewById(R.id.cartItemUnitPrice);
        TextView  originalPrice = v.findViewById(R.id.cartItemOriginalPrice);
        TextView  outOfStock    = v.findViewById(R.id.cartItemOutOfStock);
        TextView  quantityTv    = v.findViewById(R.id.cartItemQuantity);
        TextView  subtotalTv    = v.findViewById(R.id.cartItemSubtotal);
        ImageView decrement     = v.findViewById(R.id.cartItemDecrement);
        ImageView increment     = v.findViewById(R.id.cartItemIncrement);
        ImageView removeBtn     = v.findViewById(R.id.cartItemRemove);
        UiAnimations.pressBounce(decrement, increment, removeBtn);

        // ── Product name ───────────────────────────────────────────────────
        name.setText(item.getProductName());

        // ── SKU ────────────────────────────────────────────────────────────
        aeu.ratha.beckie_deal_app.data.models.Product product = item.getProduct();
        if (product != null && product.getSku() != null && !product.getSku().isEmpty()) {
            skuTv.setText("SKU: " + product.getSku());
            skuTv.setVisibility(View.VISIBLE);
        } else {
            skuTv.setVisibility(View.GONE);
        }

        // ── Price: show effective price; reveal strikethrough when on sale ─
        double effectivePrice = (product != null && product.hasDiscount())
                ? product.getSalePriceAsDouble()
                : item.getPriceAsDouble();
        unitPrice.setText(String.format(Locale.US, "$%.2f / unit", effectivePrice));

        if (product != null && product.hasDiscount()) {
            originalPrice.setText(String.format(Locale.US, "$%.2f", product.getPriceAsDouble()));
            originalPrice.setPaintFlags(originalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            originalPrice.setVisibility(View.VISIBLE);
        } else {
            originalPrice.setVisibility(View.GONE);
        }

        // ── Stock badge ────────────────────────────────────────────────────
        boolean inStock = product == null || product.isInStock();
        outOfStock.setVisibility(inStock ? View.GONE : View.VISIBLE);

        // ── Thumbnail ──────────────────────────────────────────────────────
        String imageUrl = ImageUrlHelper.getImageUrlOrFallback(item.getProductThumbnail());
        Glide.with(this)
                .load(imageUrl)
                .centerInside()
                .placeholder(R.drawable.placeholder_image)
                .into(image);

        // Bind dynamic data
        refreshItemRow(item, quantityTv, subtotalTv);

        // ── Increment ──────────────────────────────────────────────────────
        increment.setOnClickListener(btn -> {
            int newQuantity = item.getQuantity() + 1;
            updateCartItemQuantity(item.getId(), newQuantity);
        });

        // ── Decrement ──────────────────────────────────────────────────────
        decrement.setOnClickListener(btn -> {
            if (item.getQuantity() <= 1) {
                // Last unit — remove from cart entirely
                removeCartItem(item.getId());
            } else {
                int newQuantity = item.getQuantity() - 1;
                updateCartItemQuantity(item.getId(), newQuantity);
            }
        });

        // ── Remove ─────────────────────────────────────────────────────────
        removeBtn.setOnClickListener(btn -> {
            removeCartItem(item.getId());
        });

        parent.addView(v);
    }

    /**
     * Update cart item quantity via API.
     */
    private void updateCartItemQuantity(int cartItemId, int newQuantity) {
        // Show loading (optional)
        
        cartRepository.updateQuantity(cartItemId, newQuantity, new CartRepository.CartItemCallback() {
            @Override
            public void onSuccess(CartItemResponse response) {
                Toast.makeText(CartActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                loadCartFromAPI(); // Reload cart
                updateCartBadge();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CartActivity.this, "Failed to update: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Remove item from cart via API.
     */
    private void removeCartItem(int cartItemId) {
        // Show loading (optional)
        
        cartRepository.removeFromCart(cartItemId, new CartRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CartActivity.this, "Removed from cart", Toast.LENGTH_SHORT).show();
                loadCartFromAPI(); // Reload cart
                updateCartBadge();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(CartActivity.this, "Failed to remove: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Update the quantity label and subtotal for a single cart row (no full re-render). */
    private void refreshItemRow(CartItem item, TextView quantityTv, TextView subtotalTv) {
        // Zero-padded to two digits so the stepper width never jumps (01, 02 ... 12)
        quantityTv.setText(String.format(Locale.US, "%02d", item.getQuantity()));
        subtotalTv.setText(String.format(Locale.US, "$%.2f", item.getSubtotal()));
    }

    /** Refresh the grand total text in the checkout bar. */
    private void updateTotal(TextView totalText, Cart cart) {
        if (totalText == null || cart == null) return;
        
        // Use API subtotal (dynamic from backend)
        double total = cart.getSubtotalFromAPI();
        
        // Fallback to client calculation if API doesn't provide subtotal
        if (total == 0.0) {
            total = cart.getTotalPrice(); // Calculate from items as fallback
        }
        
        totalText.setText(String.format(Locale.US, "$%.2f", total));
    }

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.CART);
    }

    private void updateCartBadge() {
        BottomNavHelper.updateCartBadge(this);
    }
}
