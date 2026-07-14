package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupAuthButtons();
        setupDarkModeSwitch();
        setupSettingsRows();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        // menuIcon click handled by AppDrawerHelper
        View notificationContainer = findViewById(R.id.notificationContainer);

        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());
    }

    private void setupAuthButtons() {
        MaterialButton loginButton    = findViewById(R.id.loginButton);
        MaterialButton registerButton = findViewById(R.id.registerButton);

        if (loginButton != null) loginButton.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
        if (registerButton != null) registerButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void setupDarkModeSwitch() {
        MaterialSwitch darkModeSwitch = findViewById(R.id.darkModeSwitch);
        if (darkModeSwitch == null) return;

        // Disable saved instance state restoration for this switch to prevent
        // the framework from restoring a stale checked state that conflicts
        // with the actual current night mode.
        darkModeSwitch.setSaveEnabled(false);

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNight = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

        // Null listener first so setChecked() doesn't fire the callback during setup.
        darkModeSwitch.setOnCheckedChangeListener(null);
        darkModeSwitch.setChecked(isNight);

        darkModeSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            // Guard: only change night mode if it's actually different from current.
            int newMode = isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO;
            if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                AppCompatDelegate.setDefaultNightMode(newMode);
            }
        });
    }

    private void setupSettingsRows() {
        LinearLayout ordersRow        = findViewById(R.id.ordersRow);
        LinearLayout notificationsRow = findViewById(R.id.notificationsRow);
        LinearLayout languageRow      = findViewById(R.id.languageRow);
        LinearLayout privacyRow       = findViewById(R.id.privacyRow);
        LinearLayout termsRow         = findViewById(R.id.termsRow);
        LinearLayout aboutUsRow       = findViewById(R.id.aboutUsRow);

        if (ordersRow != null) ordersRow.setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));
        if (notificationsRow != null) notificationsRow.setOnClickListener(v ->
                Toast.makeText(this, "Notification settings", Toast.LENGTH_SHORT).show());
        if (languageRow != null) languageRow.setOnClickListener(v ->
                Toast.makeText(this, "Language settings", Toast.LENGTH_SHORT).show());
        if (privacyRow != null) privacyRow.setOnClickListener(v ->
                Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show());
        if (termsRow != null) termsRow.setOnClickListener(v ->
                Toast.makeText(this, "Terms of Service", Toast.LENGTH_SHORT).show());
        if (aboutUsRow != null) aboutUsRow.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
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

        bottomNav.setSelectedItemId(R.id.nav_account);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_categories) {
                startActivity(new Intent(this, CategoriesActivity.class));
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoritesActivity.class));
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
            } else {
                return true;
            }
            overridePendingTransition(0, 0);
            finish();
            return true;
        });

        updateCartBadge(bottomNav);
    }

    private void updateCartBadge(BottomNavigationView bottomNav) {
        if (bottomNav == null) return;
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
