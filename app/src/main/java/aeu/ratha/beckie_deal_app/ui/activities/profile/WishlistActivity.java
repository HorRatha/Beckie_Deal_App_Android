package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.data.repository.WishlistRepository;
import aeu.ratha.beckie_deal_app.data.models.Wishlist;
import aeu.ratha.beckie_deal_app.data.models.WishlistItem;
import aeu.ratha.beckie_deal_app.data.models.WishlistResponse;
import aeu.ratha.beckie_deal_app.data.models.Product;
import aeu.ratha.beckie_deal_app.ui.adapters.ProductCardBinder;
import aeu.ratha.beckie_deal_app.utils.helpers.ProductMapper;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends BaseActivity {

    private static final String TAG = "WishlistActivity";
    private WishlistRepository wishlistRepository;
    private ProgressBar loadingIndicator;
    private List<MainActivity.Product> cachedWishlistProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        wishlistRepository = new WishlistRepository(this);
        loadingIndicator = findViewById(R.id.wishlistLoadingIndicator);

        AppDrawerHelper drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        // Refresh drawer with latest user data from API to ensure sidebar shows correct user across all pages
        drawerHelper.refreshDrawerWithLatestUserData(this);
        setupTopBar();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayWishlist();
    }

    private void setupTopBar() {
        // menuIcon click handled by AppDrawerHelper
        View notificationContainer = findViewById(R.id.notificationContainer);

        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));

        UiAnimations.pressBounce(notificationContainer);
    }

    private void displayWishlist() {
        GridLayout wishlistGrid = findViewById(R.id.wishlistGrid);
        View emptyState = findViewById(R.id.emptyState);
        
        // Check if user is logged in
        boolean isLoggedIn = aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.isUserLoggedIn(this);

        if (!isLoggedIn) {
            // Show login prompt for guest users
            emptyState.setVisibility(View.VISIBLE);
            wishlistGrid.setVisibility(View.GONE);
            setupWishlistEmptyState(false);
            UiAnimations.fadeSlideUp(emptyState, 60);
            return;
        }

        // User is logged in - fetch wishlist from API
        showLoading(true);
        
        wishlistRepository.getWishlist(new WishlistRepository.WishlistCallback() {
            @Override
            public void onSuccess(WishlistResponse response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    
                    Wishlist wishlist = response.getData();
                    if (wishlist == null || wishlist.getItems() == null || wishlist.getItems().isEmpty()) {
                        // Empty wishlist
                        emptyState.setVisibility(View.VISIBLE);
                        wishlistGrid.setVisibility(View.GONE);
                        setupWishlistEmptyState(true);
                        UiAnimations.fadeSlideUp(emptyState, 60);
                    } else {
                        // Convert WishlistItem to MainActivity.Product for display
                        cachedWishlistProducts.clear();
                        for (WishlistItem item : wishlist.getItems()) {
                            Product apiProduct = item.getProduct();
                            if (apiProduct != null) {
                                // Use ProductMapper to properly convert API product to UI product
                                MainActivity.Product displayProduct = ProductMapper.toUiProduct(apiProduct);
                                if (displayProduct != null) {
                                    displayProduct.isInWishlist = true;
                                    displayProduct.wishlistItemId = item.getId(); // Save for deletion
                                    cachedWishlistProducts.add(displayProduct);
                                }
                            }
                        }
                        
                        displayWishlistGrid(cachedWishlistProducts, wishlistGrid, emptyState);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Log.e(TAG, "Failed to load wishlist: " + error);
                    Toast.makeText(WishlistActivity.this, "Failed to load wishlist", Toast.LENGTH_SHORT).show();
                    
                    // Show empty state on error
                    emptyState.setVisibility(View.VISIBLE);
                    wishlistGrid.setVisibility(View.GONE);
                    setupWishlistEmptyState(true);
                });
            }
        });
    }
    
    private void displayWishlistGrid(List<MainActivity.Product> products, GridLayout wishlistGrid, View emptyState) {
        // Responsive columns: 2 on phone, 3 on tablet, 4 on large tablet
        int columns = ScreenUtils.productGridColumns(this);
        wishlistGrid.setColumnCount(columns);
        emptyState.setVisibility(View.GONE);
        wishlistGrid.setVisibility(View.VISIBLE);
        wishlistGrid.removeAllViews();

        for (MainActivity.Product product : products) {
            View productView = LayoutInflater.from(this)
                    .inflate(R.layout.product_card, wishlistGrid, false);

            // Bind all fields via shared binder
            ProductCardBinder.bind(this, productView, product);

            productView.setOnClickListener(v -> {
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("product_id", product.id);
                intent.putExtra("product_name", product.name);
                intent.putExtra("product_price", product.price);
                intent.putExtra("product_image", product.imageUrl);
                intent.putExtra("product_rating", product.rating);
                intent.putExtra("is_in_wishlist", true);
                startActivity(intent);
            });

            ImageView favoriteIcon = productView.findViewById(R.id.favoriteIcon);
            if (favoriteIcon != null) {
                favoriteIcon.setOnClickListener(v -> {
                    removeFromWishlist(product);
                });
            }

            // Each card takes fixed width based on available width divided by columns
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int parentPadding = dp(16) * 2; // LinearLayout has 16dp padding on each side
            int availableWidth = screenWidth - parentPadding;
            int margins = dp(2);
            int cardWidth = (availableWidth / columns) - (margins * 2);
            params.width = cardWidth;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1);
            params.setMargins(margins, margins, margins, margins);
            productView.setLayoutParams(params);

            wishlistGrid.addView(productView);
        }
        
        // Cards now have fixed width, no spacers needed
    }
    
    private void removeFromWishlist(MainActivity.Product product) {
        showLoading(true);
        
        wishlistRepository.removeFromWishlist(product.wishlistItemId, new WishlistRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(WishlistActivity.this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
                    displayWishlist(); // Refresh the list
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Log.e(TAG, "Failed to remove from wishlist: " + error);
                    Toast.makeText(WishlistActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void showLoading(boolean show) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    /**
     * Setup wishlist empty state UI based on whether user is logged in or not.
     * Guest users see login prompt, logged-in users see "start shopping" message.
     */
    private void setupWishlistEmptyState(boolean isLoggedIn) {
        ImageView icon = findViewById(R.id.wishlistEmptyIcon);
        TextView title = findViewById(R.id.wishlistEmptyTitle);
        TextView message = findViewById(R.id.wishlistEmptyMessage);
        View exploreButton = findViewById(R.id.exploreButton);
        View loginButtonsContainer = findViewById(R.id.wishlistLoginButtonsContainer);
        View loginBtn = findViewById(R.id.loginButton);
        View registerBtn = findViewById(R.id.registerButton);
        
        if (!isLoggedIn) {
            // Guest user - show login prompt
            if (icon != null) icon.setImageResource(R.drawable.ic_lock);
            if (title != null) title.setText("Login to View Wishlist");
            if (message != null) message.setText("Sign in to save your favorite products and shop from any device.");
            if (exploreButton != null) exploreButton.setVisibility(View.GONE);
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
            // Logged in user - show regular empty wishlist message
            if (icon != null) icon.setImageResource(R.drawable.ic_favorite);
            if (title != null) title.setText("Your wishlist is empty");
            if (message != null) message.setText("Tap the heart icon on products you love and they'll appear here.");
            if (exploreButton != null) {
                exploreButton.setVisibility(View.VISIBLE);
                exploreButton.setOnClickListener(v -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
                UiAnimations.pressBounce(exploreButton);
            }
            if (loginButtonsContainer != null) loginButtonsContainer.setVisibility(View.GONE);
        }
    }

    private int dp(int dp) {
        return ScreenUtils.dpToPx(this, dp);
    }

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.WISHLIST);
    }
}
