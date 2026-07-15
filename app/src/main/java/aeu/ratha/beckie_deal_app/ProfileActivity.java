package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupAuthButtons();
        setupDarkModeSwitch();
        setupNotificationToggle();
        setupSettingsRows();
        setupBottomNavigation();
    }

    private void setupTopBar() {
        // menuIcon click handled by AppDrawerHelper
        View notificationContainer = findViewById(R.id.notificationContainer);

        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
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
        FrameLayout lightOption = findViewById(R.id.lightModeOption);
        FrameLayout darkOption = findViewById(R.id.darkModeOption);
        ImageView lightIcon = findViewById(R.id.lightModeIcon);
        ImageView darkIcon = findViewById(R.id.darkModeIcon);

        if (lightOption == null || darkOption == null) return;

        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNight = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

        // Set initial state without animation
        updateDarkModeToggleUI(lightOption, darkOption, lightIcon, darkIcon, isNight);

        lightOption.setOnClickListener(v -> {
            if (isCurrentlyDark()) {
                updateDarkModeToggleUI(lightOption, darkOption, lightIcon, darkIcon, false);
                applyDarkMode(false);
            }
        });

        darkOption.setOnClickListener(v -> {
            if (!isCurrentlyDark()) {
                updateDarkModeToggleUI(lightOption, darkOption, lightIcon, darkIcon, true);
                applyDarkMode(true);
            }
        });
    }

    private boolean isCurrentlyDark() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void updateDarkModeToggleUI(FrameLayout lightOption, FrameLayout darkOption,
                                         ImageView lightIcon, ImageView darkIcon, boolean isDark) {
        if (isDark) {
            // Dark is active
            darkOption.setBackgroundResource(R.drawable.dark_mode_pill_active);
            lightOption.setBackgroundResource(R.drawable.dark_mode_pill_inactive);
            darkIcon.setColorFilter(ContextCompat.getColor(this, R.color.white_fixed));
            lightIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            // Light is active
            lightOption.setBackgroundResource(R.drawable.dark_mode_pill_active);
            darkOption.setBackgroundResource(R.drawable.dark_mode_pill_inactive);
            lightIcon.setColorFilter(ContextCompat.getColor(this, R.color.white_fixed));
            darkIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
        }
    }

    private void applyDarkMode(boolean isDark) {
        ThemePreferences.setDarkMode(this, isDark);
        int newMode = isDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;
        if (AppCompatDelegate.getDefaultNightMode() != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode);
        }
    }

    private void setupNotificationToggle() {
        FrameLayout onOption = findViewById(R.id.notifOnOption);
        FrameLayout offOption = findViewById(R.id.notifOffOption);
        ImageView onIcon = findViewById(R.id.notifOnIcon);
        ImageView offIcon = findViewById(R.id.notifOffIcon);

        if (onOption == null || offOption == null) return;

        // Default state: ON
        updateNotifToggleUI(onOption, offOption, onIcon, offIcon, true);

        onOption.setOnClickListener(v -> {
            updateNotifToggleUI(onOption, offOption, onIcon, offIcon, true);
            Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show();
        });

        offOption.setOnClickListener(v -> {
            updateNotifToggleUI(onOption, offOption, onIcon, offIcon, false);
            Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateNotifToggleUI(FrameLayout onOption, FrameLayout offOption,
                                      ImageView onIcon, ImageView offIcon, boolean isOn) {
        if (isOn) {
            onOption.setBackgroundResource(R.drawable.notification_pill_active);
            offOption.setBackgroundResource(R.drawable.notification_pill_inactive);
            onIcon.setColorFilter(ContextCompat.getColor(this, R.color.white_fixed));
            offIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            offOption.setBackgroundResource(R.drawable.notification_pill_active);
            onOption.setBackgroundResource(R.drawable.notification_pill_inactive);
            offIcon.setColorFilter(ContextCompat.getColor(this, R.color.white_fixed));
            onIcon.setColorFilter(ContextCompat.getColor(this, R.color.text_secondary));
        }
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
