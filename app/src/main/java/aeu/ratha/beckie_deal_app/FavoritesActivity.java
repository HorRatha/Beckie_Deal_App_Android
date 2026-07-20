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
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class FavoritesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayFavorites();
    }

    private void setupTopBar() {
        // menuIcon click handled by AppDrawerHelper
        View notificationContainer = findViewById(R.id.notificationContainer);

        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        UiAnimations.pressBounce(notificationContainer);
    }

    private void displayFavorites() {
        List<MainActivity.Product> favoriteProducts = FavoriteManager.getInstance().getFavorites();
        GridLayout favoritesGrid = findViewById(R.id.favoritesGrid);
        View emptyState = findViewById(R.id.emptyState);

        if (favoriteProducts == null || favoriteProducts.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            favoritesGrid.setVisibility(View.GONE);
            View exploreButton = findViewById(R.id.exploreButton);
            exploreButton.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
            UiAnimations.pressBounce(exploreButton);
            UiAnimations.fadeSlideUp(emptyState, 60);
        } else {
            // Responsive columns: 2 on phone, 3 on tablet, 4 on large tablet
            int columns = ScreenUtils.productGridColumns(this);
            favoritesGrid.setColumnCount(columns);
            emptyState.setVisibility(View.GONE);
            favoritesGrid.setVisibility(View.VISIBLE);
            favoritesGrid.removeAllViews();

            for (MainActivity.Product product : favoriteProducts) {
                product.isFavorite = true;
                View productView = LayoutInflater.from(this)
                        .inflate(R.layout.product_card, favoritesGrid, false);

                // Bind all fields via shared binder
                ProductCardBinder.bind(this, productView, product);

                productView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ProductDetailActivity.class);
                    intent.putExtra("product_name",  product.name);
                    intent.putExtra("product_price", product.price);
                    intent.putExtra("product_image", product.imageUrl);
                    intent.putExtra("product_rating",product.rating);
                    intent.putExtra("is_favorite",   true);
                    startActivity(intent);
                });

                ImageView favoriteIcon = productView.findViewById(R.id.favoriteIcon);
                if (favoriteIcon != null) {
                    favoriteIcon.setOnClickListener(v -> {
                        FavoriteManager.getInstance().removeFavorite(product);
                        displayFavorites();
                        Toast.makeText(this, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    });
                }

                // Each card takes equal column weight but is capped to max column width
                // This prevents a single item from stretching full width
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(dp(2), dp(2), dp(2), dp(2));
                productView.setLayoutParams(params);

                favoritesGrid.addView(productView);
            }

            // If items don't fill the last row, add invisible spacers to keep cards at correct size
            int remainder = favoriteProducts.size() % columns;
            if (remainder != 0) {
                int spacersNeeded = columns - remainder;
                for (int i = 0; i < spacersNeeded; i++) {
                    View spacer = new View(this);
                    GridLayout.LayoutParams spacerParams = new GridLayout.LayoutParams();
                    spacerParams.width = 0;
                    spacerParams.height = 0;
                    spacerParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                    spacerParams.setMargins(dp(2), 0, dp(2), 0);
                    spacer.setLayoutParams(spacerParams);
                    favoritesGrid.addView(spacer);
                }
            }
        }
    }

    private int dp(int dp) {
        return ScreenUtils.dpToPx(this, dp);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav == null) return;

        bottomNav.setBackground(null);
        bottomNav.setBackgroundTintList(null);
        bottomNav.setItemActiveIndicatorColor(android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(this, R.color.nav_indicator_fill)));

        android.content.res.ColorStateList navTint =
                androidx.core.content.ContextCompat.getColorStateList(this, R.color.bottom_nav_color);
        if (navTint != null) { bottomNav.setItemIconTintList(navTint); bottomNav.setItemTextColor(navTint); }

        bottomNav.setSelectedItemId(R.id.nav_favorites);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_categories) {
                startActivity(new Intent(this, CategoriesActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_favorites) {
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            } else if (itemId == R.id.nav_account) {
                startActivity(new Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0); finish(); return true;
            }
            return false;
        });

        // Cart badgez
        int count = CartManager.getInstance().getTotalUnits();
        if (count > 0) {
            com.google.android.material.badge.BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_cart);
            badge.setVisible(true);
            badge.setNumber(count);
            badge.setBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.red_error));
            badge.setBadgeTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.white_fixed));
        } else {
            bottomNav.removeBadge(R.id.nav_cart);
        }
    }
}
