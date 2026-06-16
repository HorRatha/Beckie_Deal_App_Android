package aeu.ratha.beckie_deal_app;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<MainActivity.Product> cartItems;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public List<MainActivity.Product> getCartItems() {
        return cartItems;
    }

    public void addToCart(MainActivity.Product product) {
        // For now, just add it. In a real app, you might increment quantity if it exists.
        cartItems.add(product);
    }

    public void removeFromCart(MainActivity.Product product) {
        cartItems.remove(product);
    }

    public int getCartCount() {
        return cartItems.size();
    }

    public double getTotalPrice() {
        double total = 0;
        for (MainActivity.Product item : cartItems) {
            try {
                String priceStr = item.price.replace("$", "").replace(",", "");
                total += Double.parseDouble(priceStr);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
        return total;
    }
}
