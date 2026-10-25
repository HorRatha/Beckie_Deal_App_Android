package aeu.ratha.beckie_deal_app.ui.activities.cart;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Cart;
import aeu.ratha.beckie_deal_app.data.models.CartItem;
import aeu.ratha.beckie_deal_app.data.models.CartItemResponse;
import aeu.ratha.beckie_deal_app.data.models.CartResponse;
import aeu.ratha.beckie_deal_app.data.models.Product;
import aeu.ratha.beckie_deal_app.data.repository.CartRepository;
import aeu.ratha.beckie_deal_app.data.repository.ProductRepository;
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
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CartActivity extends BaseActivity {

    private CartRepository cartRepository;
    private Cart currentCart;

    /** NEW: one tile in "You may also like". Map your own Product objects into this. */
    public static class SuggestedProduct {
        public final String id;
        public final String name;
        public final double price;
        public final String imageUrl;

        public SuggestedProduct(String id, String name, double price, String imageUrl) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
        }
    }

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

    private static Cart cachedCart = null;

    /**
     * Clear the static cart cache (call this after successful checkout).
     * ⚠️ CRITICAL: CartActivity uses a static cache that persists across activity lifecycle.
     * If this cache is not cleared after checkout, stale cart data will be shown.
     */
    public static void clearStaticCache() {
        Log.d("CartActivity", "Clearing static cart cache");
        cachedCart = null;
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

        // Render memory-cached cart instantly if available (0ms delay)
        if (cachedCart != null && !cachedCart.isEmpty()) {
            currentCart = cachedCart;
            displayCartItems(currentCart);
        }

        cartRepository.getCart(new CartRepository.CartCallback() {
            @Override
            public void onSuccess(CartResponse response) {
                if (response.isSuccess() && response.hasData()) {
                    currentCart = response.getData();
                    if (currentCart == null || currentCart.isEmpty()) {
                        cachedCart = null;
                        displayCartItems(null);
                    } else {
                        cachedCart = currentCart;
                        displayCartItems(currentCart);
                    }
                    updateCartBadge();
                } else {
                    cachedCart = null;
                    displayCartItems(null);
                }
            }

            @Override
            public void onError(String error) {
                if (cachedCart == null) {
                    displayCartItems(null);
                }
            }
        });
    }

    private void displayCartItems(Cart cart) {
        LinearLayout cartList = findViewById(R.id.cartItemsList);
        View emptyState       = findViewById(R.id.emptyState);
        View couponReminder   = findViewById(R.id.couponReminderCard);
        View checkoutSection  = findViewById(R.id.checkoutSection);
        TextView totalText    = findViewById(R.id.totalPriceText);
        TextView subtitle     = findViewById(R.id.cartSubtitle);   // NEW
        View suggestions      = findViewById(R.id.suggestionsSection); // NEW

        // Check if user is logged in
        boolean isLoggedIn = aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.isUserLoggedIn(this);

        if (cart == null || cart.isEmpty() || !isLoggedIn) {
            emptyState.setVisibility(View.VISIBLE);
            cartList.setVisibility(View.GONE);
            couponReminder.setVisibility(View.GONE);
            checkoutSection.setVisibility(View.GONE);
            if (subtitle != null) subtitle.setVisibility(View.GONE);       // NEW
            if (suggestions != null) suggestions.setVisibility(View.GONE); // NEW

            // Update empty state UI based on login status
            setupEmptyState(isLoggedIn);
        } else {
            emptyState.setVisibility(View.GONE);
            cartList.setVisibility(View.VISIBLE);
            couponReminder.setVisibility(View.VISIBLE);
            checkoutSection.setVisibility(View.VISIBLE);
            cartList.removeAllViews();

            // ── Deduplicate items by product ID ────────────────────────────────
            java.util.Map<String, CartItem> deduplicatedItems = new java.util.LinkedHashMap<>();

            for (CartItem item : cart.getItems()) {
                aeu.ratha.beckie_deal_app.data.models.Product product = item.getProduct();
                String productId = (product != null && product.getId() != null)
                        ? product.getId()
                        : null;

                if (productId != null && deduplicatedItems.containsKey(productId)) {
                    // Merge quantities: sum up quantities for duplicate product
                    CartItem existing = deduplicatedItems.get(productId);
                    existing.setQuantity(existing.getQuantity() + item.getQuantity());
                } else if (productId != null) {
                    // New product - add it
                    deduplicatedItems.put(productId, item);
                }
            }

            // Display deduplicated items
            int totalUnits = 0; // NEW
            for (CartItem item : deduplicatedItems.values()) {
                inflateCartItem(cartList, item);
                totalUnits += item.getQuantity(); // NEW
            }

            // NEW: "2 items · Swipe left to remove"
            if (subtitle != null) {
                subtitle.setText(totalUnits + (totalUnits == 1 ? " item" : " items")
                        + " · Swipe left to remove");
                subtitle.setVisibility(View.VISIBLE);
            }

            updateTotal(totalText, cart);
            loadSuggestions(deduplicatedItems.keySet()); // NEW
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
        View      deletePanel   = v.findViewById(R.id.cartItemDeletePanel); // NEW
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

        // ── Product Click Navigation ────────────────────────────────────────
        View.OnClickListener goToProductDetail = btn -> {
            if (product != null && product.getId() != null) {
                Intent intent = new Intent(CartActivity.this, aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                startActivity(intent);
            }
        };

        if (image != null) {
            image.setOnClickListener(goToProductDetail);
            UiAnimations.pressBounce(image);
        }

        if (name != null) {
            name.setOnClickListener(goToProductDetail);
        }

        // Bind dynamic data
        refreshItemRow(item, quantityTv, subtotalTv);

        // ── Increment ──────────────────────────────────────────────────────
        increment.setOnClickListener(btn -> {
            int newQuantity = item.getQuantity() + 1;
            updateCartItemQuantityOptimistic(item, newQuantity, quantityTv, subtotalTv);
        });

        // ── Decrement ──────────────────────────────────────────────────────
        decrement.setOnClickListener(btn -> {
            if (item.getQuantity() <= 1) {
                // Last unit — remove from cart entirely
                removeCartItemOptimistic(item);
            } else {
                int newQuantity = item.getQuantity() - 1;
                updateCartItemQuantityOptimistic(item, newQuantity, quantityTv, subtotalTv);
            }
        });

        // ── Remove (small trash icon) ──────────────────────────────────────
        removeBtn.setOnClickListener(btn -> {
            removeCartItemOptimistic(item);
        });

        // ── Remove (swipe left, then tap Delete) ── NEW ────────────────────
        if (deletePanel != null) {
            deletePanel.setOnClickListener(btn -> removeCartItemOptimistic(item));
        }

        parent.addView(v);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // NEW: "You may also like"
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Load suggested products to show in "You may also like" section.
     * Fetches featured products and filters out items already in cart.
     */
    private void loadSuggestions(Set<String> cartProductIds) {
        ProductRepository productRepository = ProductRepository.getInstance(this);
        
        // Fetch featured products (limit to 10, starting from offset 0)
        productRepository.fetchProducts(10, 0, true, new ProductRepository.ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                List<SuggestedProduct> suggestions = new ArrayList<>();
                
                // Convert to SuggestedProduct, skipping items already in cart
                for (Product p : products) {
                    if (cartProductIds.contains(p.getId())) {
                        continue; // Skip products already in cart
                    }
                    
                    double price = (p.hasDiscount() && p.getSalePriceAsDouble() > 0)
                            ? p.getSalePriceAsDouble()
                            : p.getPriceAsDouble();
                    
                    suggestions.add(new SuggestedProduct(
                            p.getId(),
                            p.getName(),
                            price,
                            p.getThumbnail()
                    ));
                }
                
                // Show suggestions if we have any
                if (!suggestions.isEmpty()) {
                    runOnUiThread(() -> showSuggestions(suggestions));
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Silently fail - suggestions are optional
                android.util.Log.e("CartActivity", "Failed to load suggestions: " + errorMessage);
            }
        });
    }

    private void showSuggestions(List<SuggestedProduct> items) {
        View section = findViewById(R.id.suggestionsSection);
        LinearLayout row = findViewById(R.id.suggestionsRow);
        if (section == null || row == null) return;

        row.removeAllViews();
        if (items == null || items.isEmpty()) {
            section.setVisibility(View.GONE);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (SuggestedProduct p : items) {
            View tile = inflater.inflate(R.layout.item_suggestion, row, false);

            ImageView img = tile.findViewById(R.id.suggestionImage);
            TextView name = tile.findViewById(R.id.suggestionName);
            TextView price = tile.findViewById(R.id.suggestionPrice);
            TextView stock = tile.findViewById(R.id.suggestionStock);
            View add = tile.findViewById(R.id.suggestionAdd);

            name.setText(p.name);
            price.setText(String.format(Locale.US, "$%.2f", p.price));
            
            // Set stock status
            if (stock != null) {
                stock.setText("In Stock");
            }
            
            Glide.with(this)
                    .load(ImageUrlHelper.getImageUrlOrFallback(p.imageUrl))
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_image)
                    .into(img);

            View.OnClickListener openDetail = btn -> {
                Intent intent = new Intent(CartActivity.this,
                        aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity.class);
                intent.putExtra("product_id", p.id);
                startActivity(intent);
            };
            tile.setOnClickListener(openDetail);

            // Quick add button: add to cart with quantity 1
            add.setOnClickListener(v -> {
                cartRepository.addToCart(p.id, 1, new CartRepository.CartItemCallback() {
                    @Override
                    public void onSuccess(CartItemResponse response) {
                        Toast.makeText(CartActivity.this, p.name + " added to cart", Toast.LENGTH_SHORT).show();
                        loadCartFromAPI(); // Reload cart to show updated items
                        updateCartBadge();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(CartActivity.this, "Failed to add: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
            
            UiAnimations.pressBounce(add);

            row.addView(tile);
        }
        section.setVisibility(View.VISIBLE);
    }

    /**
     * Update cart item quantity via API with instant optimistic UI update.
     */
    private void updateCartItemQuantityOptimistic(CartItem item, int newQuantity, TextView quantityTv, TextView subtotalTv) {
        int oldQuantity = item.getQuantity();
        item.setQuantity(newQuantity);

        // Instant local update (0ms latency)
        refreshItemRow(item, quantityTv, subtotalTv);
        TextView totalText = findViewById(R.id.totalPriceText);
        updateTotal(totalText, currentCart);
        updateCartBadge();

        cartRepository.updateQuantity(item.getId(), newQuantity, new CartRepository.CartItemCallback() {
            @Override
            public void onSuccess(CartItemResponse response) {
                updateCartBadge();
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    item.setQuantity(oldQuantity);
                    refreshItemRow(item, quantityTv, subtotalTv);
                    updateTotal(totalText, currentCart);
                    updateCartBadge();
                    Toast.makeText(CartActivity.this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Remove item from cart via API with instant optimistic UI update.
     */
    private void removeCartItemOptimistic(CartItem item) {
        if (currentCart != null && currentCart.getItems() != null) {
            currentCart.getItems().remove(item);
            cachedCart = currentCart;
        }

        // Instant UI update (0ms latency)
        displayCartItems(currentCart);
        updateCartBadge();
        Toast.makeText(CartActivity.this, "Removed from cart", Toast.LENGTH_SHORT).show();

        cartRepository.removeFromCart(item.getId(), new CartRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                updateCartBadge();
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    loadCartFromAPI();
                    Toast.makeText(CartActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                });
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
