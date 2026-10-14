package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.preferences.ThemePreferences;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.activities.auth.LoginActivity;
import aeu.ratha.beckie_deal_app.ui.activities.cart.MyOrdersActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.CouponsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.NotificationsActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.AboutActivity;
import aeu.ratha.beckie_deal_app.ui.activities.profile.TermsActivity;
import android.content.Intent;
import android.content.res.ColorStateList;
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
import com.google.android.material.card.MaterialCardView;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import aeu.ratha.beckie_deal_app.data.models.User;
import aeu.ratha.beckie_deal_app.data.repository.ProfileRepository;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import okhttp3.MultipartBody;
import androidx.drawerlayout.widget.DrawerLayout;

public class ProfileActivity extends BaseActivity {
    
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    
    private TokenManager tokenManager;
    private ProfileRepository profileRepository;
    private AppDrawerHelper drawerHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = TokenManager.getInstance(this);
        profileRepository = ProfileRepository.getInstance(this);

        drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        setupTopBar();
        fetchAndSetupUserProfile();
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
                openImagePicker());

        UiAnimations.pressBounce(notificationContainer, editAvatarButton);
    }

    /**
     * Open image picker for avatar selection.
     */
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            android.net.Uri selectedImage = data.getData();
            if (selectedImage != null) {
                uploadAvatarImage(selectedImage);
            }
        }
    }

    /**
     * Upload selected avatar image to server.
     */
    private void uploadAvatarImage(android.net.Uri imageUri) {
        try {
            // Get file from URI
            java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Unable to read image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create request body from input stream
            byte[] imageBytes = readInputStreamBytes(inputStream);
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                    imageBytes,
                    okhttp3.MediaType.parse("image/jpeg")
            );

            // Create multipart body
            okhttp3.MultipartBody.Part imagePart = okhttp3.MultipartBody.Part.createFormData(
                    "image",
                    "avatar.jpg",
                    requestBody
            );

            // Show loading
            Toast.makeText(this, "Uploading avatar...", Toast.LENGTH_SHORT).show();

            // Upload avatar
            profileRepository.uploadAvatar(imagePart, new ProfileRepository.AvatarUploadCallback() {
                @Override
                public void onSuccess(User profile) {
                    android.util.Log.d("ProfileActivity", "Avatar uploaded successfully");
                    displayUserProfile(profile);
                    Toast.makeText(ProfileActivity.this, "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception error) {
                    android.util.Log.e("ProfileActivity", "Avatar upload failed: " + error.getMessage());
                    Toast.makeText(ProfileActivity.this, "Failed to upload avatar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            android.util.Log.e("ProfileActivity", "Error preparing avatar upload: " + e.getMessage(), e);
            Toast.makeText(this, "Error preparing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetch user profile from API and display it dynamically.
     * Always fetches fresh data from API (no cache on activity open).
     * Falls back to TokenManager data if API call fails.
     */
    private void fetchAndSetupUserProfile() {
        TextView userNameText = findViewById(R.id.userName);
        TextView userEmailText = findViewById(R.id.userEmail);
        
        if (!tokenManager.isLoggedIn()) {
            // User not logged in - show Guest User message
            if (userNameText != null) {
                userNameText.setText("Guest User");
            }
            if (userEmailText != null) {
                userEmailText.setText("Sign in to your account");
            }
            return;
        }

        // Show loading state
        if (userNameText != null) {
            userNameText.setText("Loading...");
        }

        // Fetch profile from API (force refresh - useCache=false for dynamic updates)
        profileRepository.getProfile(false, new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(User profile) {
                android.util.Log.d("ProfileActivity", "Profile fetched dynamically: " + profile.getEmail());
                displayUserProfile(profile);
                
                // Update drawer with fresh user info
                // This ensures the sidebar shows the correct name
                updateDrawerUserInfo(profile);
            }

            @Override
            public void onFailure(Exception error) {
                android.util.Log.e("ProfileActivity", "Profile fetch failed: " + error.getMessage());
                // Fall back to TokenManager data
                fallbackToTokenManagerData();
            }
        });
    }

    /**
     * Update the sidebar drawer with user information from profile.
     * This method should be called whenever we have fresh user data from the API.
     */
    private void updateDrawerUserInfo(User user) {
        if (user == null) {
            android.util.Log.e("ProfileActivity", "updateDrawerUserInfo called with null user");
            return;
        }
        
        String displayName = user.getFullName();
        String email = user.getEmail();
        
        android.util.Log.d("ProfileActivity", "=== Updating drawer via AppDrawerHelper ===");
        android.util.Log.d("ProfileActivity", "Display Name: " + displayName);
        android.util.Log.d("ProfileActivity", "Email: " + email);
        
        // Use AppDrawerHelper to update the drawer
        if (drawerHelper != null) {
            drawerHelper.updateDrawerUserInfo(displayName, email);
            android.util.Log.d("ProfileActivity", "✅ Drawer updated via AppDrawerHelper");
        } else {
            android.util.Log.e("ProfileActivity", "❌ drawerHelper is null");
        }
    }

    /**
     * Display user profile data in UI.
     */
    private void displayUserProfile(User user) {
        TextView userNameText = findViewById(R.id.userName);
        TextView userEmailText = findViewById(R.id.userEmail);
        
        String displayName = user.getFullName();
        String email = user.getEmail();
        
        // Debug logging to understand data structure
        android.util.Log.d("ProfileActivity", "=== Profile Data Debug ===");
        android.util.Log.d("ProfileActivity", "Email: " + email);
        android.util.Log.d("ProfileActivity", "FirstName: " + user.getFirstName());
        android.util.Log.d("ProfileActivity", "LastName: " + user.getLastName());
        android.util.Log.d("ProfileActivity", "Name: " + user.getName());
        android.util.Log.d("ProfileActivity", "FullName: " + displayName);
        android.util.Log.d("ProfileActivity", "========================");
        
        if (userNameText != null) {
            userNameText.setText(displayName != null ? displayName : "User");
        }
        if (userEmailText != null) {
            userEmailText.setText(email != null ? email : "");
        }
    }

    /**
     * Fall back to TokenManager data if API fetch fails.
     */
    private void fallbackToTokenManagerData() {
        TextView userNameText = findViewById(R.id.userName);
        TextView userEmailText = findViewById(R.id.userEmail);
        
        String name = tokenManager.getUserName();
        String email = tokenManager.getUserEmail();
        
        android.util.Log.d("ProfileActivity", "=== Using TokenManager Fallback ===");
        android.util.Log.d("ProfileActivity", "Name from TokenManager: " + name);
        android.util.Log.d("ProfileActivity", "Email from TokenManager: " + email);
        android.util.Log.d("ProfileActivity", "====================================");
        
        if (userNameText != null) {
            userNameText.setText(name != null ? name : "User");
        }
        if (userEmailText != null) {
            userEmailText.setText(email != null ? email : "");
        }
    }

    private void setupAuthButtons() {
        MaterialButton loginButton    = findViewById(R.id.loginButton);
        MaterialButton registerButton = findViewById(R.id.registerButton);
        MaterialButton logoutButton   = findViewById(R.id.logoutButton);

        boolean isLoggedIn = tokenManager.isLoggedIn();
        android.util.Log.d("ProfileActivity", "User logged in: " + isLoggedIn);

        if (isLoggedIn) {
            // User IS logged in - show logout button, hide login/register
            if (loginButton != null) {
                loginButton.setVisibility(View.GONE);
            }
            if (registerButton != null) {
                registerButton.setVisibility(View.GONE);
            }
            if (logoutButton != null) {
                logoutButton.setVisibility(View.VISIBLE);
                logoutButton.setOnClickListener(v -> handleLogout());
                UiAnimations.pressBounce(logoutButton);
            }
        } else {
            // User is NOT logged in - show login/register, hide logout
            if (loginButton != null) {
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setOnClickListener(v ->
                        startActivity(new Intent(this, LoginActivity.class)));
                UiAnimations.pressBounce(loginButton);
            }
            if (registerButton != null) {
                registerButton.setVisibility(View.VISIBLE);
                registerButton.setOnClickListener(v -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("openRegister", true); // Open register tab
                    startActivity(intent);
                });
                UiAnimations.pressBounce(registerButton);
            }
            if (logoutButton != null) {
                logoutButton.setVisibility(View.GONE);
            }
        }
    }
    
    private void handleLogout() {
        // Clear all authentication data
        tokenManager.logout();
        
        // Clear cached user profile data (this is critical!)
        profileRepository.clearCache();
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate to home screen (MainActivity) instead of login
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupDarkModeSwitch() {
        FrameLayout lightModeOption = findViewById(R.id.lightModeOption);
        FrameLayout darkModeOption = findViewById(R.id.darkModeOption);
        ImageView lightModeIcon = findViewById(R.id.lightModeIcon);
        ImageView darkModeIcon = findViewById(R.id.darkModeIcon);
        
        if (lightModeOption == null || darkModeOption == null) {
            return; // Elements not present in current layout
        }
        
        // Set initial state based on current theme
        boolean isDark = isCurrentlyDark();
        updateDarkModeToggleUI(lightModeOption, darkModeOption, lightModeIcon, darkModeIcon, isDark);
        
        // Light mode click listener
        lightModeOption.setOnClickListener(v -> {
            applyDarkMode(false);
            updateDarkModeToggleUI(lightModeOption, darkModeOption, lightModeIcon, darkModeIcon, false);
        });
        
        // Dark mode click listener
        darkModeOption.setOnClickListener(v -> {
            applyDarkMode(true);
            updateDarkModeToggleUI(lightModeOption, darkModeOption, lightModeIcon, darkModeIcon, true);
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
            // Dark mode is active
            darkOption.setBackground(ContextCompat.getDrawable(this, R.drawable.dark_mode_pill_active));
            lightOption.setBackground(ContextCompat.getDrawable(this, R.drawable.dark_mode_pill_inactive));
            darkIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white_fixed)));
            lightIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.text_secondary)));
        } else {
            // Light mode is active
            lightOption.setBackground(ContextCompat.getDrawable(this, R.drawable.dark_mode_pill_active));
            darkOption.setBackground(ContextCompat.getDrawable(this, R.drawable.dark_mode_pill_inactive));
            lightIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white_fixed)));
            darkIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.text_secondary)));
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
        // Notification toggle UI not present in current layout
    }

    private void updateNotifToggleUI(FrameLayout onOption, FrameLayout offOption,
                                      ImageView onIcon, ImageView offIcon, boolean isOn) {
        // Not used in current layout
    }

    private void setupSettingsRows() {
        MaterialCardView myOrdersBox      = findViewById(R.id.myOrdersBox);
        MaterialCardView myCouponsBox     = findViewById(R.id.myCouponsBox);
        MaterialCardView editProfileBox = findViewById(R.id.editProfileBox);
        LinearLayout aboutUsRow       = findViewById(R.id.aboutUsRow);
        LinearLayout privacyRow       = findViewById(R.id.privacyRow);
        LinearLayout termsRow         = findViewById(R.id.termsRow);
        
        // Numbers now live on the tiles
        TextView activeOrdersCount = findViewById(R.id.activeOrdersCount);
        TextView couponsCount = findViewById(R.id.couponsCount);
        
        // Update counts (use your actual count logic)
        if (activeOrdersCount != null) activeOrdersCount.setText("3"); // Replace with actual count
        if (couponsCount != null) couponsCount.setText("4"); // Replace with actual count

        // Whatever ordersRow and couponsRow used to do, now on the tiles
        if (myOrdersBox != null) myOrdersBox.setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));
        if (myCouponsBox != null) myCouponsBox.setOnClickListener(v ->
                startActivity(new Intent(this, CouponsActivity.class)));
        if (editProfileBox != null) editProfileBox.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });
        if (aboutUsRow != null) aboutUsRow.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
        if (privacyRow != null) privacyRow.setOnClickListener(v ->
                startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        if (termsRow != null) termsRow.setOnClickListener(v ->
                startActivity(new Intent(this, TermsActivity.class)));

        UiAnimations.pressBounce(myOrdersBox, myCouponsBox, editProfileBox, aboutUsRow, privacyRow, termsRow);
        
        // Show the tiles only when logged in
        View loggedInTiles = findViewById(R.id.loggedInTiles);
        if (loggedInTiles != null) {
            loggedInTiles.setVisibility(tokenManager.isLoggedIn() ? View.VISIBLE : View.GONE);
        }
    }

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.ACCOUNT);
    }

    private void updateCartBadge(BottomNavigationView ignored) {
        BottomNavHelper.updateCartBadge(this);
    }

    /**
     * Read all bytes from an InputStream in a way that's compatible with older Android API levels.
     * Avoids using readAllBytes() which requires API 33.
     */
    private byte[] readInputStreamBytes(java.io.InputStream inputStream) throws java.io.IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384]; // 16KB buffer
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
