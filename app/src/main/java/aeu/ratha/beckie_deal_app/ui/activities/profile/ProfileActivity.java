package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import aeu.ratha.beckie_deal_app.security.TokenManager;

public class ProfileActivity extends BaseActivity {
    
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = TokenManager.getInstance(this);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        setupUserProfile();
        setupAuthButtons();
        setupDarkModeSwitch();
        setupNotificationToggle();
        setupSettingsRows();
        setupBottomNavigation();
        setupEntranceAnimation();
    }

    // ── Entrance animation ────────────────────────────────────────────────

    /** Pops the avatar in, drops the header card into place, then staggers
     *  the settings cards up from below — mirrors the drawer's open animation. */
    private void setupEntranceAnimation() {
        View headerCard    = findViewById(R.id.profileHeaderCard);
        View avatarOverlay = findViewById(R.id.avatarOverlay);
        View editAvatar    = findViewById(R.id.editAvatarButton);

        UiAnimations.fadeSlideDown(headerCard, 0);
        UiAnimations.popIn(avatarOverlay, 120);
        UiAnimations.popIn(editAvatar, 220);

        // Stagger every direct child of the scrolling body (section labels,
        // the Account/Preferences/Legal cards and the version footer) —
        // no layout changes needed since we just walk the existing tree.
        NestedScrollView contentScroll = findViewById(R.id.contentScroll);
        if (contentScroll != null && contentScroll.getChildCount() > 0) {
            View listRoot = contentScroll.getChildAt(0);
            if (listRoot instanceof ViewGroup) {
                UiAnimations.staggerChildrenIn((ViewGroup) listRoot, 200L);
            }
        }
    }

    private void setupTopBar() {
        // menuIcon click handled by AppDrawerHelper
        View notificationContainer = findViewById(R.id.notificationContainer);
        View editAvatarButton      = findViewById(R.id.editAvatarButton);

        if (notificationContainer != null) notificationContainer.setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
        if (editAvatarButton != null) editAvatarButton.setOnClickListener(v ->
                Toast.makeText(this, "Edit photo — coming soon", Toast.LENGTH_SHORT).show());

        UiAnimations.pressBounce(notificationContainer, editAvatarButton);
    }

    private void setupUserProfile() {
        TextView userNameText = findViewById(R.id.userName);
        TextView userEmailText = findViewById(R.id.userEmail);
        
        if (tokenManager.isLoggedIn()) {
            // User is logged in - show their info
            String name = tokenManager.getUserName();
            String email = tokenManager.getUserEmail();
            
            android.util.Log.d("ProfileActivity", "TokenManager name: " + name);
            android.util.Log.d("ProfileActivity", "TokenManager email: " + email);
            
            if (userNameText != null) {
                userNameText.setText(name != null ? name : "User");
            }
            if (userEmailText != null) {
                userEmailText.setText(email != null ? email : "");
            }
        }
    }

    private void setupAuthButtons() {
        MaterialButton loginButton    = findViewById(R.id.loginButton);
        MaterialButton registerButton = findViewById(R.id.registerButton);
        MaterialButton logoutButton   = findViewById(R.id.logoutButton);
        MaterialButton floatingLogoutButton = findViewById(R.id.floatingLogoutButton);

        boolean isLoggedIn = tokenManager.isLoggedIn();
        android.util.Log.d("ProfileActivity", "User logged in: " + isLoggedIn);

        if (isLoggedIn) {
            // User is logged in - hide all auth buttons in the header section
            if (loginButton != null) loginButton.setVisibility(View.GONE);
            if (registerButton != null) registerButton.setVisibility(View.GONE);
            if (logoutButton != null) logoutButton.setVisibility(View.GONE);
            
            // Show floating logout button at bottom right
            if (floatingLogoutButton != null) {
                floatingLogoutButton.setVisibility(View.VISIBLE);
                floatingLogoutButton.setOnClickListener(v -> handleLogout());
                UiAnimations.pressBounce(floatingLogoutButton);
                android.util.Log.d("ProfileActivity", "Floating logout button set to VISIBLE");
            } else {
                android.util.Log.e("ProfileActivity", "floatingLogoutButton is NULL!");
            }
        } else {
            // User is NOT logged in - show login/register in header, hide logout everywhere
            if (logoutButton != null) logoutButton.setVisibility(View.GONE);
            if (floatingLogoutButton != null) floatingLogoutButton.setVisibility(View.GONE);
            
            if (loginButton != null) {
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setOnClickListener(v ->
                        startActivity(new Intent(this, LoginActivity.class)));
                UiAnimations.pressBounce(loginButton);
            }
            if (registerButton != null) {
                registerButton.setVisibility(View.VISIBLE);
                registerButton.setOnClickListener(v ->
                        startActivity(new Intent(this, RegisterActivity.class)));
                UiAnimations.pressBounce(registerButton);
            }
        }
    }
    
    private void handleLogout() {
        // Clear all authentication data
        tokenManager.logout();
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate to home screen (MainActivity) instead of login
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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

        UiAnimations.pressBounce(lightOption, darkOption);
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

        UiAnimations.pressBounce(onOption, offOption);
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
        LinearLayout couponsRow       = findViewById(R.id.couponsRow);
        LinearLayout notificationsRow = findViewById(R.id.notificationsRow);
        LinearLayout languageRow      = findViewById(R.id.languageRow);
        LinearLayout privacyRow       = findViewById(R.id.privacyRow);
        LinearLayout termsRow         = findViewById(R.id.termsRow);
        LinearLayout aboutUsRow       = findViewById(R.id.aboutUsRow);

        if (ordersRow != null) ordersRow.setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));
        if (couponsRow != null) couponsRow.setOnClickListener(v ->
                startActivity(new Intent(this, CouponsActivity.class)));
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

        UiAnimations.pressBounce(ordersRow, couponsRow, notificationsRow, languageRow, privacyRow, termsRow, aboutUsRow);
    }

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.ACCOUNT);
    }

    private void updateCartBadge(BottomNavigationView ignored) {
        BottomNavHelper.updateCartBadge(this);
    }
}
