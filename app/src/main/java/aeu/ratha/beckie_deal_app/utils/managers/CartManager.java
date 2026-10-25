package aeu.ratha.beckie_deal_app.utils.managers;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class CartManager {

    // ── CartItem wrapper ──────────────────────────────────────────────────────

    public static class CartItem {
        public final MainActivity.Product product;
        public int quantity;

        CartItem(MainActivity.Product product, int quantity) {
            this.product  = product;
            this.quantity = quantity;
        }
    }

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /** Add one unit of a product. If it already exists, increment quantity. */
    public void addToCart(MainActivity.Product product) {
        addToCart(product, 1);
    }

    /** Add a specific quantity. If it already exists, increment by that amount. */
    public void addToCart(MainActivity.Product product, int qty) {
        CartItem existing = findItem(product);
        if (existing != null) {
            existing.quantity += qty;
        } else {
            cartItems.add(new CartItem(product, qty));
        }
    }

    /** Remove one unit. If quantity reaches 0, remove the entry entirely. */
    public void decrementQuantity(MainActivity.Product product) {
        CartItem item = findItem(product);
        if (item == null) return;
        item.quantity--;
        if (item.quantity <= 0) cartItems.remove(item);
    }

    /** Increase quantity of an existing cart item by 1. */
    public void incrementQuantity(MainActivity.Product product) {
        CartItem item = findItem(product);
        if (item != null) item.quantity++;
    }

    /** Remove the item entirely regardless of quantity. */
    public void removeFromCart(MainActivity.Product product) {
        CartItem item = findItem(product);
        if (item != null) cartItems.remove(item);
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    /** Total number of distinct products in cart. */
    public int getCartCount() {
        return cartItems.size();
    }

    /** Total number of units across all items. */
    public int getTotalUnits() {
        int total = 0;
        for (CartItem item : cartItems) total += item.quantity;
        return total;
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            try {
                String priceStr = item.product.price.replace("$", "").replace(",", "");
                total += Double.parseDouble(priceStr) * item.quantity;
            } catch (Exception e) {
                // ignore parsing errors
            }
        }
        return total;
    }

    /** Clear all items from the cart (call after successful checkout). */
    public void clearCart() {
        cartItems.clear();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Find an existing CartItem by product name (name is the stable identifier). */
    private CartItem findItem(MainActivity.Product product) {
        if (product == null) return null;
        for (CartItem item : cartItems) {
            if (item.product.name != null && item.product.name.equals(product.name)) {
                return item;
            }
        }
        return null;
    }
}
