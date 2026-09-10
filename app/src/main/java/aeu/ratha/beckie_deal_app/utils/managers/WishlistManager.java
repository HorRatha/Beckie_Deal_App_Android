package aeu.ratha.beckie_deal_app.utils.managers;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class WishlistManager {
    private static WishlistManager instance;
    private List<MainActivity.Product> wishlistItems;

    private WishlistManager() {
        wishlistItems = new ArrayList<>();
    }

    public static WishlistManager getInstance() {
        if (instance == null) {
            instance = new WishlistManager();
        }
        return instance;
    }

    public List<MainActivity.Product> getWishlistItems() {
        return wishlistItems;
    }

    public void addToWishlist(MainActivity.Product product) {
        if (!isInWishlist(product)) {
            product.isInWishlist = true;
            wishlistItems.add(product);
        }
    }

    public void removeFromWishlist(MainActivity.Product product) {
        MainActivity.Product toRemove = null;
        for (MainActivity.Product p : wishlistItems) {
            if (p.name.equals(product.name)) {
                toRemove = p;
                break;
            }
        }
        if (toRemove != null) {
            toRemove.isInWishlist = false;
            wishlistItems.remove(toRemove);
        }
    }

    public boolean isInWishlist(MainActivity.Product product) {
        for (MainActivity.Product p : wishlistItems) {
            if (p.name.equals(product.name)) {
                return true;
            }
        }
        return false;
    }
}
