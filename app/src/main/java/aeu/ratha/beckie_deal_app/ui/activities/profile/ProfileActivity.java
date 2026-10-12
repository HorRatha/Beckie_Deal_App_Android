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
import aeu.ratha.beckie_deal_app.data.models.User;
import aeu.ratha.beckie_deal_app.data.repository.ProfileRepository;
import okhttp3.MultipartBody;

public class ProfileActivity extends BaseActivity {
    
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    
    private TokenManager tokenManager;
    private ProfileRepository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tokenManager = TokenManager.getInstance(this);
        profileRepository = ProfileRepository.getInstance(this);

        AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
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
        if (!tokenManager.isLoggedIn()) {
            return; // User not logged in, skip profile fetch
        }

        // Show loading state
        TextView userNameText = findViewById(R.id.userName);
        TextView userEmailText = findViewById(R.id.userEmail);
        
        if (userNameText != null) {
            userNameText.setText("Loading...");
        }

        // Fetch profile from API (force refresh - useCache=false for dynamic updates)
        profileRepository.getProfile(false, new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(User profile) {
                android.util.Log.d("ProfileActivity", "Profile fetched dynamically: " + profile.getEmail());
                displayUserProfile(profile);
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
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate to home screen (MainActivity) instead of login
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupDarkModeSwitch() {
        // Dark mode UI not present in current layout
    }

    private boolean isCurrentlyDark() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    private void updateDarkModeToggleUI(FrameLayout lightOption, FrameLayout darkOption,
                                         ImageView lightIcon, ImageView darkIcon, boolean isDark) {
        // Not used in current layout
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
        LinearLayout editProfileRow   = findViewById(R.id.editProfileRow);
        LinearLayout ordersRow        = findViewById(R.id.ordersRow);
        LinearLayout couponsRow       = findViewById(R.id.couponsRow);
        LinearLayout aboutUsRow       = findViewById(R.id.aboutUsRow);
        LinearLayout privacyRow       = findViewById(R.id.privacyRow);
        LinearLayout termsRow         = findViewById(R.id.termsRow);

        if (editProfileRow != null) editProfileRow.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Profile - Coming Soon", Toast.LENGTH_SHORT).show();
            // TODO: Navigate to EditProfileActivity when implemented
            // startActivity(new Intent(this, EditProfileActivity.class));
        });
        if (ordersRow != null) ordersRow.setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));
        if (couponsRow != null) couponsRow.setOnClickListener(v ->
                startActivity(new Intent(this, CouponsActivity.class)));
        if (aboutUsRow != null) aboutUsRow.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
        if (privacyRow != null) privacyRow.setOnClickListener(v ->
                startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        if (termsRow != null) termsRow.setOnClickListener(v ->
                startActivity(new Intent(this, TermsActivity.class)));

        UiAnimations.pressBounce(editProfileRow, ordersRow, couponsRow, aboutUsRow, privacyRow, termsRow);
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
