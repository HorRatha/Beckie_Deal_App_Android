package aeu.ratha.beckie_deal_app.utils.managers;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class FavoriteManager {
    private static FavoriteManager instance;
    private List<MainActivity.Product> favorites;

    private FavoriteManager() {
        favorites = new ArrayList<>();
    }

    public static FavoriteManager getInstance() {
        if (instance == null) {
            instance = new FavoriteManager();
        }
        return instance;
    }

    public List<MainActivity.Product> getFavorites() {
        return favorites;
    }

    public void addFavorite(MainActivity.Product product) {
        if (!isFavorite(product)) {
            product.isFavorite = true;
            favorites.add(product);
        }
    }

    public void removeFavorite(MainActivity.Product product) {
        MainActivity.Product toRemove = null;
        for (MainActivity.Product p : favorites) {
            if (p.name.equals(product.name)) {
                toRemove = p;
                break;
            }
        }
        if (toRemove != null) {
            toRemove.isFavorite = false;
            favorites.remove(toRemove);
        }
    }

    public boolean isFavorite(MainActivity.Product product) {
        for (MainActivity.Product p : favorites) {
            if (p.name.equals(product.name)) {
                return true;
            }
        }
        return false;
    }
}
