package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends BaseActivity {

    // Stagger timing for the settings-list entrance, matching the side
    // drawer's row animation so the app feels consistent page to page.
    private static final long ITEM_STAGGER_MS  = 40L;
    private static final long ITEM_DURATION_MS = 280L;
    private static final float ITEM_START_TRANS_DP = 40f;

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
        setupEntranceAnimation();
    }

    // ── Entrance animation ────────────────────────────────────────────────

    /** Pops the avatar in, drops the header card into place, then staggers
     *  the settings cards up from below — mirrors the drawer's open animation. */
    private void setupEntranceAnimation() {
        float density = getResources().getDisplayMetrics().density;
        float startTransPx = ITEM_START_TRANS_DP * density;

        View headerCard    = findViewById(R.id.profileHeaderCard);
        View avatarOverlay = findViewById(R.id.avatarOverlay);
        View editAvatar    = findViewById(R.id.editAvatarButton);

        if (headerCard != null) {
            headerCard.setAlpha(0f);
            headerCard.setTranslationY(-startTransPx * 0.6f);
            headerCard.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(ITEM_DURATION_MS)
                    .setInterpolator(new DecelerateInterpolator(1.6f))
                    .start();
        }

        if (avatarOverlay != null) {
            avatarOverlay.setAlpha(0f);
            avatarOverlay.setScaleX(0.4f);
            avatarOverlay.setScaleY(0.4f);
            avatarOverlay.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(120)
                    .setDuration(340)
                    .setInterpolator(new OvershootInterpolator(2.2f))
                    .start();
        }

        if (editAvatar != null) {
            editAvatar.setAlpha(0f);
            editAvatar.setScaleX(0.4f);
            editAvatar.setScaleY(0.4f);
            editAvatar.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(220)
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator(2.2f))
                    .start();
        }

        // Stagger every direct child of the scrolling body (section labels,
        // the Account/Preferences/Legal cards and the version footer) —
        // no layout changes needed since we just walk the existing tree.
        NestedScrollView contentScroll = findViewById(R.id.contentScroll);
        if (contentScroll != null && contentScroll.getChildCount() > 0) {
            View listRoot = contentScroll.getChildAt(0);
            if (listRoot instanceof ViewGroup) {
                ViewGroup list = (ViewGroup) listRoot;
                long baseDelay = 200L;
                for (int i = 0; i < list.getChildCount(); i++) {
                    View child = list.getChildAt(i);
                    child.setAlpha(0f);
                    child.setTranslationY(startTransPx);
                    child.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setStartDelay(baseDelay + i * ITEM_STAGGER_MS)
                            .setDuration(ITEM_DURATION_MS)
                            .setInterpolator(new DecelerateInterpolator(1.6f))
                            .start();
                }
            }
        }
    }

    /** Adds a bouncy scale-down/overshoot-back press effect on top of any
     *  existing ripple/click behaviour a view already has. */
    private void addPressBounce(View... views) {
        for (View v : views) {
            if (v == null) continue;
            v.setOnTouchListener((view, event) -> {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        view.animate()
                                .scaleX(0.96f)
                                .scaleY(0.96f)
                                .setDuration(100)
                                .setInterpolator(new DecelerateInterpolator())
                                .start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(220)
                                .setInterpolator(new OvershootInterpolator(3f))
                                .start();
                        break;
                    default:
                        break;
                }
                return false; // let clicks/ripples still fire
            });
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

        addPressBounce(notificationContainer, editAvatarButton);
    }

    private void setupAuthButtons() {
        MaterialButton loginButton    = findViewById(R.id.loginButton);
        MaterialButton registerButton = findViewById(R.id.registerButton);

        if (loginButton != null) loginButton.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
        if (registerButton != null) registerButton.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        addPressBounce(loginButton, registerButton);
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

        addPressBounce(lightOption, darkOption);
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

        addPressBounce(onOption, offOption);
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

        addPressBounce(ordersRow, notificationsRow, languageRow, privacyRow, termsRow, aboutUsRow);
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
