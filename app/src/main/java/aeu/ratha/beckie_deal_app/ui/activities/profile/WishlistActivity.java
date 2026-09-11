package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.WishlistManager;
import aeu.ratha.beckie_deal_app.ui.adapters.ProductCardBinder;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.ProductDetailActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;

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

public class WishlistActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
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
        List<MainActivity.Product> wishlistProducts = WishlistManager.getInstance().getWishlistItems();
        GridLayout wishlistGrid = findViewById(R.id.wishlistGrid);
        View emptyState = findViewById(R.id.emptyState);
        
        // Check if user is logged in
        boolean isLoggedIn = aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.isUserLoggedIn(this);

        if (wishlistProducts == null || wishlistProducts.isEmpty() || !isLoggedIn) {
            emptyState.setVisibility(View.VISIBLE);
            wishlistGrid.setVisibility(View.GONE);
            
            // Update empty state UI based on login status
            setupWishlistEmptyState(isLoggedIn);
            UiAnimations.fadeSlideUp(emptyState, 60);
        } else {
            // Responsive columns: 2 on phone, 3 on tablet, 4 on large tablet
            int columns = ScreenUtils.productGridColumns(this);
            wishlistGrid.setColumnCount(columns);
            emptyState.setVisibility(View.GONE);
            wishlistGrid.setVisibility(View.VISIBLE);
            wishlistGrid.removeAllViews();

            for (MainActivity.Product product : wishlistProducts) {
                product.isInWishlist = true;
                View productView = LayoutInflater.from(this)
                        .inflate(R.layout.product_card, wishlistGrid, false);

                // Bind all fields via shared binder
                ProductCardBinder.bind(this, productView, product);

                productView.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ProductDetailActivity.class);
                    intent.putExtra("product_name",  product.name);
                    intent.putExtra("product_price", product.price);
                    intent.putExtra("product_image", product.imageUrl);
                    intent.putExtra("product_rating",product.rating);
                    intent.putExtra("is_in_wishlist", true);
                    startActivity(intent);
                });

                ImageView favoriteIcon = productView.findViewById(R.id.favoriteIcon);
                if (favoriteIcon != null) {
                    favoriteIcon.setOnClickListener(v -> {
                        WishlistManager.getInstance().removeFromWishlist(product);
                        displayWishlist();
                        Toast.makeText(this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();
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

                wishlistGrid.addView(productView);
            }

            // If items don't fill the last row, add invisible spacers to keep cards at correct size
            int remainder = wishlistProducts.size() % columns;
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
                    wishlistGrid.addView(spacer);
                }
            }
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
        View loginBtn = findViewById(R.id.wishlistLoginButton);
        View registerBtn = findViewById(R.id.wishlistRegisterButton);
        
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
                    startActivity(new Intent(this, aeu.ratha.beckie_deal_app.ui.activities.auth.RegisterActivity.class));
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
