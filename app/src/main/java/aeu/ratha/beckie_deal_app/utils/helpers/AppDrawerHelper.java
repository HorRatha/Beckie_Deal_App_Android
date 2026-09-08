package aeu.ratha.beckie_deal_app.utils.helpers;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.activities.product.CategoriesActivity;
import aeu.ratha.beckie_deal_app.ui.activities.cart.MyOrdersActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.FavoritesActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.AboutActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.ProfileActivity;
import aeu.ratha.beckie_deal_app.ui.activities.auth.LoginActivity;
import aeu.ratha.beckie_deal_app.utils.managers.FavoriteManager;
import aeu.ratha.beckie_deal_app.utils.preferences.ThemePreferences;

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

    // Starting point for the "pop" — how small/faded the panel is right
    // as it begins sliding into view. 1f/1f would be a plain flat slide.
    private static final float POP_MIN_SCALE = 0.90f;
    private static final float POP_MIN_ALPHA = 0.55f;

    // Stagger timing for the menu-item entrance animation.
    private static final long ITEM_STAGGER_MS   = 35L;
    private static final long ITEM_DURATION_MS  = 260L;
    private static final float ITEM_START_TRANS = -50f; // dp, converted below

    private final AppCompatActivity activity;
    private final DrawerLayout drawerLayout;

    // Every clickable row inside the drawer, in visual order — used to
    // stagger the entrance animation when the drawer opens.
    private final List<View> drawerItemViews = new ArrayList<>();
    private boolean itemsEntranceAnimated = false;

    private AppDrawerHelper(AppCompatActivity activity, int drawerLayoutId, int menuIconId) {
        this.activity     = activity;
        this.drawerLayout = activity.findViewById(drawerLayoutId);

        if (drawerLayout != null) {
            // Never leave the drawer locked on any page
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Cheap scrim so opening doesn't force an expensive full-content
            // redraw gradient every frame.
            drawerLayout.setScrimColor(0x99000000);

            // Anchor scaling to the left edge (where the drawer is docked)
            // instead of its center, so the pop grows outward from the
            // edge it's attached to rather than from the middle of the
            // panel — that's what makes it read as "sliding in", not
            // "zooming in".
            View panel = getDrawerPanel();
            if (panel != null) {
                panel.setPivotX(0f);
                panel.setScaleX(POP_MIN_SCALE);
                panel.setScaleY(POP_MIN_SCALE);
                panel.setAlpha(POP_MIN_ALPHA);
            }

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

                    // Layer the slide with a gentle scale + fade "pop" so
                    // the panel feels like it's growing into place rather
                    // than just sliding flatly across the screen.
                    float scale = POP_MIN_SCALE + (1f - POP_MIN_SCALE) * slideOffset;
                    drawerView.setScaleX(scale);
                    drawerView.setScaleY(scale);
                    drawerView.setAlpha(POP_MIN_ALPHA + (1f - POP_MIN_ALPHA) * slideOffset);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    drawerView.setScaleX(1f);
                    drawerView.setScaleY(1f);
                    drawerView.setAlpha(1f);
                    clearLayers(drawerView);
                    animateItemsEntrance();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    drawerView.setScaleX(POP_MIN_SCALE);
                    drawerView.setScaleY(POP_MIN_SCALE);
                    drawerView.setAlpha(POP_MIN_ALPHA);
                    clearLayers(drawerView);
                    resetItemsForNextEntrance();
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

        // On activity recreate (e.g. triggered by toggling dark mode while the
        // drawer is open), Android's default view-state restoration silently
        // reopens the DrawerLayout without ever running an actual slide
        // animation — so onDrawerOpened() above never fires. Without this,
        // the panel stays stuck at its "about to open" pose (90% scale, 55%
        // alpha) and every row stays off-screen/invisible, which looks like
        // a frozen/half-rendered drawer. Once restoration settles, check for
        // that case and snap everything to its fully-open state.
        if (drawerLayout != null) {
            drawerLayout.post(() -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    View panel = getDrawerPanel();
                    if (panel != null) {
                        panel.setScaleX(1f);
                        panel.setScaleY(1f);
                        panel.setAlpha(1f);
                    }
                    showItemsImmediately();
                }
            });
        }
    }

    /** Factory — call this from every activity's onCreate(). */
    public static AppDrawerHelper attach(AppCompatActivity activity,
                                         int drawerLayoutId,
                                         int menuIconId) {
        return new AppDrawerHelper(activity, drawerLayoutId, menuIconId);
    }

    /** 
     * Refresh drawer content - call this after login/logout to update user info.
     * This can be called from any activity that has the drawer attached.
     */
    public void refreshDrawerContent() {
        setupDrawerContent();
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

    /** The drawer's own child — the panel with gravity=start (layout_drawer.xml root). */
    private View getDrawerPanel() {
        if (drawerLayout == null) return null;
        for (int i = 0; i < drawerLayout.getChildCount(); i++) {
            View child = drawerLayout.getChildAt(i);
            if (((DrawerLayout.LayoutParams) child.getLayoutParams()).gravity
                    == GravityCompat.START) {
                return child;
            }
        }
        return null;
    }

    // ── Entrance animation for the menu rows ──────────────────────────────

    /** Slide + fade each drawer row in, staggered top-to-bottom. */
    private void animateItemsEntrance() {
        if (itemsEntranceAnimated || drawerItemViews.isEmpty()) return;
        itemsEntranceAnimated = true;

        for (int i = 0; i < drawerItemViews.size(); i++) {
            View item = drawerItemViews.get(i);
            item.animate()
                    .translationX(0f)
                    .alpha(1f)
                    .setStartDelay(i * ITEM_STAGGER_MS)
                    .setDuration(ITEM_DURATION_MS)
                    .setInterpolator(new DecelerateInterpolator(1.6f))
                    .start();
        }
    }

    /** Snap every row straight to its post-entrance state, no animation — used when the
     *  drawer is restored already-open (e.g. after a dark-mode-triggered recreate). */
    private void showItemsImmediately() {
        itemsEntranceAnimated = true;
        for (View item : drawerItemViews) {
            item.animate().cancel();
            item.setTranslationX(0f);
            item.setAlpha(1f);
        }
    }

    /** Put every row back to its pre-entrance state so the stagger can replay on next open. */
    private void resetItemsForNextEntrance() {
        itemsEntranceAnimated = false;
        float startTransPx = ITEM_START_TRANS * activity.getResources().getDisplayMetrics().density;
        for (View item : drawerItemViews) {
            item.animate().cancel();
            item.setTranslationX(startTransPx);
            item.setAlpha(0f);
        }
    }

    /** Registers a row for the staggered entrance and gives it a bouncy press-down feel. */
    private void prepareDrawerItem(View item) {
        if (item == null) return;

        float startTransPx = ITEM_START_TRANS * activity.getResources().getDisplayMetrics().density;
        item.setTranslationX(startTransPx);
        item.setAlpha(0f);
        drawerItemViews.add(item);

        // Bounce the row down slightly on press and back up with an
        // overshoot on release, on top of the existing ripple background.
        item.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate()
                            .scaleX(0.96f)
                            .scaleY(0.96f)
                            .setDuration(100)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(220)
                            .setInterpolator(new OvershootInterpolator(3f))
                            .start();
                    break;
                default:
                    break;
            }
            return false; // let the click listener + ripple still fire
        });
    }

    // ── Wire all items inside layout_drawer.xml ──────────────────────────

    private void setupDrawerContent() {
        if (drawerLayout == null) return;

        // ── Header: populate user info ───────────────────────────────────
        TextView welcomeBack = drawerLayout.findViewById(R.id.drawerWelcomeBack);
        TextView userName  = drawerLayout.findViewById(R.id.drawerUserName);
        TextView userEmail = drawerLayout.findViewById(R.id.drawerUserEmail);
        
        // Get TokenManager to check if user is logged in
        aeu.ratha.beckie_deal_app.security.TokenManager tokenManager = 
            aeu.ratha.beckie_deal_app.security.TokenManager.getInstance(activity);
        
        android.util.Log.d("AppDrawerHelper", "Setting up drawer - User logged in: " + tokenManager.isLoggedIn());
        
        if (tokenManager.isLoggedIn()) {
            // User is logged in - show their info with welcome message
            String name = tokenManager.getUserName();
            String email = tokenManager.getUserEmail();
            
            android.util.Log.d("AppDrawerHelper", "User name: " + name);
            android.util.Log.d("AppDrawerHelper", "User email: " + email);
            
            if (welcomeBack != null) {
                welcomeBack.setVisibility(View.VISIBLE);
            }
            if (userName != null) {
                userName.setText(name != null ? name : "User");
            }
            if (userEmail != null) {
                userEmail.setText(email != null ? email : "");
            }
        } else {
            // User is not logged in - show guest
            if (welcomeBack != null) {
                welcomeBack.setVisibility(View.GONE);
            }
            if (userName != null) userName.setText("Guest User");
            if (userEmail != null) userEmail.setText("Sign in to your account");
        }

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
        prepareDrawerItem(darkRow);

        // ── Navigation items ─────────────────────────────────────────────

        View homeItem = drawerLayout.findViewById(R.id.drawerHome);
        if (homeItem != null) homeItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof MainActivity)) {
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });
        prepareDrawerItem(homeItem);

        View categoriesItem = drawerLayout.findViewById(R.id.drawerCategories);
        if (categoriesItem != null) categoriesItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof CategoriesActivity)) {
                activity.startActivity(new Intent(activity, CategoriesActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });
        prepareDrawerItem(categoriesItem);

        View ordersItem = drawerLayout.findViewById(R.id.drawerOrders);
        if (ordersItem != null) ordersItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof MyOrdersActivity)) {
                activity.startActivity(new Intent(activity, MyOrdersActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });
        prepareDrawerItem(ordersItem);

        View favItem = drawerLayout.findViewById(R.id.drawerFavorites);
        if (favItem != null) favItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof FavoritesActivity)) {
                activity.startActivity(new Intent(activity, FavoritesActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });
        prepareDrawerItem(favItem);

        View notifItem = drawerLayout.findViewById(R.id.drawerNotifications);
        if (notifItem != null) notifItem.setOnClickListener(v -> {
            closeDrawer();
            activity.startActivity(new Intent(activity, NotificationsActivity.class));
        });
        prepareDrawerItem(notifItem);

        View languageItem = drawerLayout.findViewById(R.id.drawerLanguage);
        if (languageItem != null) languageItem.setOnClickListener(v -> {
            closeDrawer();
            Toast.makeText(activity, "Language settings — coming soon", Toast.LENGTH_SHORT).show();
        });
        prepareDrawerItem(languageItem);

        View aboutItem = drawerLayout.findViewById(R.id.drawerAbout);
        if (aboutItem != null) aboutItem.setOnClickListener(v -> {
            closeDrawer();
            if (!(activity instanceof AboutActivity)) {
                activity.startActivity(new Intent(activity, AboutActivity.class));
                activity.overridePendingTransition(0, 0);
            }
        });
        prepareDrawerItem(aboutItem);

        View loginItem = drawerLayout.findViewById(R.id.drawerLogin);
        View logoutItem = drawerLayout.findViewById(R.id.drawerLogout);
        
        if (tokenManager.isLoggedIn()) {
            // User is logged in - hide login button, show logout button
            if (loginItem != null) {
                loginItem.setVisibility(View.GONE);
            }
            if (logoutItem != null) {
                logoutItem.setVisibility(View.VISIBLE);
                logoutItem.setOnClickListener(v -> {
                    closeDrawer();
                    // Show confirmation dialog before logout
                    new android.app.AlertDialog.Builder(activity)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout", (dialog, which) -> {
                            // Clear token and user data
                            tokenManager.logout();
                            
                            // Redirect to MainActivity
                            Intent intent = new Intent(activity, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            activity.startActivity(intent);
                            activity.finish();
                            
                            Toast.makeText(activity, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                });
                prepareDrawerItem(logoutItem);
            }
        } else {
            // User is not logged in - show login button, hide logout button
            if (loginItem != null) {
                loginItem.setVisibility(View.VISIBLE);
                loginItem.setOnClickListener(v -> {
                    closeDrawer();
                    if (!(activity instanceof ProfileActivity)) {
                        activity.startActivity(new Intent(activity, ProfileActivity.class));
                        activity.overridePendingTransition(0, 0);
                    }
                });
                prepareDrawerItem(loginItem);
            }
            if (logoutItem != null) {
                logoutItem.setVisibility(View.GONE);
            }
        }
    }
}
