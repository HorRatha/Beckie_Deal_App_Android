package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * AppDrawerHelper
 *
 * Attaches the side-drawer to any Activity that uses a DrawerLayout root.
 * Call AppDrawerHelper.attach(activity, drawerId, menuIconId) in onCreate()
 * after setContentView().
 *
 * The drawer panel (layout_drawer.xml) is already embedded inside each
 * activity's DrawerLayout — this helper just wires all the click listeners.
 */
public class AppDrawerHelper {

    private final AppCompatActivity activity;
    private final DrawerLayout drawerLayout;

    private AppDrawerHelper(AppCompatActivity activity, int drawerLayoutId, int menuIconId) {
        this.activity     = activity;
        this.drawerLayout = activity.findViewById(drawerLayoutId);

        if (drawerLayout != null) {
            // Never leave the drawer locked on any page
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Cheap scrim so opening doesn't force an expensive full-content
            // redraw gradient every frame.
            drawerLayout.setScrimColor(0x99000000);

            // DrawerLayout re-runs draw() on the drawer panel AND the main
            // content view on every animation frame while sliding. With a
            // heavy content tree (ViewPager2 banners, Glide images, nested
            // ConstraintLayouts) that redraw work drops frames and the slide
            // looks janky/stuttery. Caching both views to hardware layers
            // during the slide makes the animation just move a cached
            // bitmap on the GPU instead of re-drawing the tree every frame.
            drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    if (drawerView.getLayerType() != View.LAYER_TYPE_HARDWARE) {
                        drawerView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }
                    View content = getContentView();
                    if (content != null && content.getLayerType() != View.LAYER_TYPE_HARDWARE) {
                        content.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    }
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    clearLayers(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    clearLayers(drawerView);
                }

                private void clearLayers(View drawerView) {
                    drawerView.setLayerType(View.LAYER_TYPE_NONE, null);
                    View content = getContentView();
                    if (content != null) content.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            });
        }

        // Hamburger icon opens the drawer
        if (menuIconId != 0) {
            View menuIcon = activity.findViewById(menuIconId);
            if (menuIcon != null) {
                menuIcon.setClickable(true);
                menuIcon.setFocusable(true);
                menuIcon.setOnClickListener(v -> openDrawer());
            }
        }

        setupDrawerContent();
    }

    /** Factory — call this from every activity's onCreate(). */
    public static AppDrawerHelper attach(AppCompatActivity activity,
                                         int drawerLayoutId,
                                         int menuIconId) {
        return new AppDrawerHelper(activity, drawerLayoutId, menuIconId);
    }

    public void openDrawer() {
        if (drawerLayout == null) return;
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) return;
        drawerLayout.openDrawer(GravityCompat.START, true);
    }

    public void closeDrawer() {
        if (drawerLayout != null) drawerLayout.closeDrawer(GravityCompat.START, true);
    }

    /** The DrawerLayout's first non-drawer child — i.e. the main screen content. */
    private View getContentView() {
        if (drawerLayout == null) return null;
        for (int i = 0; i < drawerLayout.getChildCount(); i++) {
            View child = drawerLayout.getChildAt(i);
            if (((DrawerLayout.LayoutParams) child.getLayoutParams()).gravity
                    != GravityCompat.START) {
                return child;
            }
        }
        return null;
    }

    // ── Wire all items inside layout_drawer.xml ──────────────────────────

    private void setupDrawerContent() {
        if (drawerLayout == null) return;

        // ── Header: populate user info (guest for now) ───────────────────
        TextView userName  = drawerLayout.findViewById(R.id.drawerUserName);
        TextView userEmail = drawerLayout.findViewById(R.id.drawerUserEmail);
        if (userName  != null) userName.setText("Guest User");
        if (userEmail != null) userEmail.setText("Sign in to your account");

        // ── Live favorites badge ─────────────────────────────────────────
        TextView favBadge = drawerLayout.findViewById(R.id.drawerFavBadge);
        if (favBadge != null) {
            int favCount = FavoriteManager.getInstance().getFavorites().size();
            if (favCount > 0) {
                favBadge.setText(String.valueOf(favCount));
                favBadge.setVisibility(View.VISIBLE);
            } else {
                favBadge.setVisibility(View.GONE);
            }
        }

        // ── Dark mode switch: reflect current state ──────────────────────
        MaterialSwitch darkSwitch = drawerLayout.findViewById(R.id.drawerDarkModeSwitch);
        if (darkSwitch != null) {
            int nightMode = activity.getResources().getConfiguration().uiMode
                    & Configuration.UI_MODE_NIGHT_MASK;
            boolean isNight = nightMode == Configuration.UI_MODE_NIGHT_YES;

            // Prevent saved instance state from restoring a stale value
            darkSwitch.setSaveEnabled(false);

            // Clear any previous listener BEFORE calling setChecked so it
            // doesn't fire during initial setup and trigger a recreation loop.
            darkSwitch.setOnCheckedChangeListener(null);
            darkSwitch.setChecked(isNight);

            // Attach the listener AFTER the initial state is set.
            darkSwitch.setOnCheckedChangeListener((btn, checked) -> {
                // Save preference so it persists across app restarts.
                ThemePreferences.setDarkMode(activity, checked);

                int newMode = checked
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_NO;
                if (AppCompatDelegate.getDefaultNightMode() != newMode) {
                    AppCompatDelegate.setDefaultNightMode(newMode);
                }
            });
        }

        // Tapping the dark-mode row also toggles the switch
        View darkRow = drawerLayout.findViewById(R.id.drawerDarkMode);
        if (darkRow != null && darkSwitch != null) {
            final MaterialSwitch switchRef = darkSwitch;
            darkRow.setOnClickListener(v -> switchRef.setChecked(!switchRef.isChecked()));
        }

        // ── Navigation items ─────────────────────────────────────────────

        View homeItem = drawerLayout.findViewById(R.id.drawerHome);
        if (homeItem != null) homeItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof MainActivity)) {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        View categoriesItem = drawerLayout.findViewById(R.id.drawerCategories);
        if (categoriesItem != null) categoriesItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof CategoriesActivity)) {
                activity.startActivity(new Intent(activity, CategoriesActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        View ordersItem = drawerLayout.findViewById(R.id.drawerOrders);
        if (ordersItem != null) ordersItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof MyOrdersActivity)) {
                activity.startActivity(new Intent(activity, MyOrdersActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        View favItem = drawerLayout.findViewById(R.id.drawerFavorites);
        if (favItem != null) favItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof FavoritesActivity)) {
                activity.startActivity(new Intent(activity, FavoritesActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        View notifItem = drawerLayout.findViewById(R.id.drawerNotifications);
        if (notifItem != null) notifItem.setOnClickListener(v -> {
            closeDrawer();
            activity.startActivity(new Intent(activity, NotificationsActivity.class));
        });

        View languageItem = drawerLayout.findViewById(R.id.drawerLanguage);
        if (languageItem != null) languageItem.setOnClickListener(v -> {
            closeDrawer();
            Toast.makeText(activity, "Language settings — coming soon", Toast.LENGTH_SHORT).show();
        });

        View aboutItem = drawerLayout.findViewById(R.id.drawerAbout);
        if (aboutItem != null) aboutItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof AboutActivity)) {
                activity.startActivity(new Intent(activity, AboutActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });

        View loginItem = drawerLayout.findViewById(R.id.drawerLogin);
        if (loginItem != null) loginItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof ProfileActivity)) {
                activity.startActivity(new Intent(activity, ProfileActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });
    }
}
