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
import aeu.ratha.beckie_deal_app.data.models.ApiOrder;
import aeu.ratha.beckie_deal_app.data.repository.ProfileRepository;
import aeu.ratha.beckie_deal_app.data.repository.OrderRepository;
import aeu.ratha.beckie_deal_app.data.repository.ReviewRepository;
import aeu.ratha.beckie_deal_app.data.models.Review;
import java.util.List;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.avatar.AvatarManager;
import okhttp3.MultipartBody;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

public class ProfileActivity extends BaseActivity {
    
    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int REQUEST_CODE_CROP_IMAGE = 101;
    private static final int PERMISSION_REQUEST_CODE_MEDIA_IMAGES = 102;
    
    private TokenManager tokenManager;
    private ProfileRepository profileRepository;
    private AppDrawerHelper drawerHelper;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBottomNavigation();
        fetchAndSetupUserProfile();
        updateActiveOrdersCount();
        BottomNavHelper.updateCartBadge(this);
    }

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
     * Checks for necessary permissions before launching the picker.
     */
    private void openImagePicker() {
        // Check if we need to request permissions (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: Request READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE_MEDIA_IMAGES);
                return;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6+ (below 13): Request READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE_MEDIA_IMAGES);
                return;
            }
        }
        
        // Permission granted (or not needed), launch image picker
        launchImagePicker();
    }
    
    /**
     * Launch the image picker intent.
     * Called after permissions are verified.
     */
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void startImageCrop(android.net.Uri sourceUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(sourceUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 512);
            cropIntent.putExtra("outputY", 512);
            cropIntent.putExtra("return-data", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(cropIntent, REQUEST_CODE_CROP_IMAGE);
        } catch (Exception e) {
            // Fallback: If system crop intent is not available on device, use automatic square crop & upload
            android.util.Log.w("ProfileActivity", "System crop not available, using automatic center crop: " + e.getMessage());
            uploadAvatarImage(sourceUri);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            android.net.Uri selectedImage = data.getData();
            if (selectedImage != null) {
                startImageCrop(selectedImage);
            }
        } else if (requestCode == REQUEST_CODE_CROP_IMAGE && resultCode == RESULT_OK && data != null) {
            android.os.Bundle extras = data.getExtras();
            if (extras != null && extras.get("data") instanceof android.graphics.Bitmap) {
                android.graphics.Bitmap croppedBitmap = (android.graphics.Bitmap) extras.get("data");
                uploadAvatarBitmap(croppedBitmap);
            } else if (data.getData() != null) {
                uploadAvatarImage(data.getData());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE_MEDIA_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, launch image picker
                launchImagePicker();
            } else {
                // Permission denied
                Toast.makeText(this, "Permission required to access photos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadAvatarBitmap(android.graphics.Bitmap bitmap) {
        try {
            android.graphics.Bitmap scaledBitmap = scaleBitmap(bitmap, 512);
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, baos);
            byte[] imageBytes = baos.toByteArray();

            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(
                    imageBytes,
                    okhttp3.MediaType.parse("image/jpeg")
            );

            okhttp3.MultipartBody.Part imagePart = okhttp3.MultipartBody.Part.createFormData(
                    "image",
                    "avatar.jpg",
                    requestBody
            );

            Toast.makeText(this, "Uploading cropped avatar...", Toast.LENGTH_SHORT).show();

            profileRepository.uploadAvatar(imagePart, new ProfileRepository.AvatarUploadCallback() {
                @Override
                public void onSuccess(User profile) {
                    android.util.Log.d("ProfileActivity", "Avatar uploaded successfully");
                    displayUserProfile(profile);
                    if (drawerHelper != null) {
                        drawerHelper.refreshDrawerContent();
                    }
                    Toast.makeText(ProfileActivity.this, "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception error) {
                    android.util.Log.e("ProfileActivity", "Avatar upload failed: " + error.getMessage());
                    Toast.makeText(ProfileActivity.this, "Failed to upload avatar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            android.util.Log.e("ProfileActivity", "Error preparing bitmap upload: " + e.getMessage(), e);
            Toast.makeText(this, "Error preparing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Upload selected avatar image to server with automatic 1:1 square crop and compression.
     */
    private void uploadAvatarImage(android.net.Uri imageUri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream == null) {
                Toast.makeText(this, "Unable to read image", Toast.LENGTH_SHORT).show();
                return;
            }
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            if (bitmap == null) {
                Toast.makeText(this, "Unable to decode image", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Crop to 1:1 square (center crop)
            android.graphics.Bitmap squareBitmap = cropToSquare(bitmap);

            // 2. Scale down to max 512x512 pixels for optimal profile upload size
            android.graphics.Bitmap scaledBitmap = scaleBitmap(squareBitmap, 512);

            // 3. Compress to JPEG bytes (quality 85)
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, baos);
            byte[] imageBytes = baos.toByteArray();

            // Create request body from compressed bytes
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
            Toast.makeText(this, "Uploading cropped avatar...", Toast.LENGTH_SHORT).show();

            // Upload avatar
            profileRepository.uploadAvatar(imagePart, new ProfileRepository.AvatarUploadCallback() {
                @Override
                public void onSuccess(User profile) {
                    android.util.Log.d("ProfileActivity", "Avatar uploaded successfully");
                    displayUserProfile(profile);
                    
                    // Refresh drawer to show updated avatar
                    if (drawerHelper != null) {
                        drawerHelper.refreshDrawerContent();
                    }
                    
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

    private android.graphics.Bitmap cropToSquare(android.graphics.Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        int newSize = Math.min(width, height);
        int x = (width - newSize) / 2;
        int y = (height - newSize) / 2;
        return android.graphics.Bitmap.createBitmap(source, x, y, newSize, newSize);
    }

    private android.graphics.Bitmap scaleBitmap(android.graphics.Bitmap source, int maxDim) {
        int width = source.getWidth();
        int height = source.getHeight();
        if (width <= maxDim && height <= maxDim) return source;
        float ratio = (float) width / height;
        int newWidth, newHeight;
        if (ratio > 1) {
            newWidth = maxDim;
            newHeight = Math.round(maxDim / ratio);
        } else {
            newHeight = maxDim;
            newWidth = Math.round(maxDim * ratio);
        }
        return android.graphics.Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    /**
     * Fetch user profile from API and display it dynamically.
     * Always fetches fresh data from API (no cache on activity open).
     * Falls back to TokenManager data if API call fails.
     * For guests: Shows DiceBear avatar generated from device ID.
     */
    private void fetchAndSetupUserProfile() {
        TextView userNameText = findViewById(R.id.userName);
        TextView userEmailText = findViewById(R.id.userEmail);
        
        if (!tokenManager.isLoggedIn()) {
            // User not logged in - show Guest User with DiceBear avatar
            if (userNameText != null) {
                userNameText.setText("Guest User");
            }
            if (userEmailText != null) {
                userEmailText.setText("Sign in to your account");
            }
            
            // Load guest avatar using WebView
            String guestAvatarUrl = AvatarManager.getAvatarUrl(this, null);
            setupAvatarWebView(guestAvatarUrl);
            return;
        }

        // Show cached name/email from TokenManager immediately instead of "Loading..."
        String cachedName = tokenManager.getUserName();
        String cachedEmail = tokenManager.getUserEmail();
        if (userNameText != null) {
            userNameText.setText(cachedName != null && !cachedName.isEmpty() ? cachedName : "My Account");
        }
        if (userEmailText != null) {
            userEmailText.setText(cachedEmail != null ? cachedEmail : "");
        }

        // Fetch profile from API in background (force refresh - useCache=false for dynamic updates)
        profileRepository.getProfile(false, new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(User profile) {
                android.util.Log.d("ProfileActivity", "Profile fetched dynamically: " + profile.getEmail());
                displayUserProfile(profile);
                
                // Update drawer with fresh user info
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
     * Shows DiceBear SVG as default avatar unless user has uploaded a profile picture.
     */
    private void displayUserProfile(User user) {
        TextView userNameText = findViewById(R.id.userName);
        TextView userEmailText = findViewById(R.id.userEmail);
        
        String displayName = user.getFullName();
        String email = user.getEmail();
        String userAvatarUrl = user.getAvatarUrl(); // Get user's uploaded avatar URL
        
        // Debug logging
        android.util.Log.d("ProfileActivity", "=== Profile Data Debug ===");
        android.util.Log.d("ProfileActivity", "Email: " + email);
        android.util.Log.d("ProfileActivity", "FirstName: " + user.getFirstName());
        android.util.Log.d("ProfileActivity", "LastName: " + user.getLastName());
        android.util.Log.d("ProfileActivity", "FullName: " + displayName);
        android.util.Log.d("ProfileActivity", "Avatar URL: " + userAvatarUrl);
        android.util.Log.d("ProfileActivity", "========================");
        
        if (userNameText != null) {
            userNameText.setText(displayName != null ? displayName : "User");
        }
        if (userEmailText != null) {
            userEmailText.setText(email != null ? email : "");
        }
        
        // Load avatar: if user has uploaded one, use it; otherwise use DiceBear
        String avatarUrl = AvatarManager.getAvatarUrl(this, userAvatarUrl);
        setupAvatarWebView(avatarUrl);
    }

    /**
     * Fall back to TokenManager data if API fetch fails.
     * Uses DiceBear SVG as default avatar.
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
        
        // Load DiceBear avatar as default (no user avatar URL available)
        String avatarUrl = AvatarManager.getAvatarUrl(this, null);
        setupAvatarWebView(avatarUrl);
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
        // Use centralized logout from AuthHelper for secure state clearing
        // This method handles:
        // 1. Clearing JWT tokens
        // 2. Clearing all saved scroll positions
        // 3. Clearing session tracking data
        // 4. Restarting app with clean task stack
        aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.logout(this);
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

    private void updateActiveOrdersCount() {
        TextView activeOrdersCount = findViewById(R.id.activeOrdersCount);
        if (activeOrdersCount == null) return;

        // Set initial mock fallback count ("3") instantly so there is no loading flash or 0
        activeOrdersCount.setText("3");

        if (tokenManager != null && tokenManager.isLoggedIn()) {
            OrderRepository orderRepository = new OrderRepository(this);
            orderRepository.fetchAllOrders(new OrderRepository.OrdersCallback() {
                @Override
                public void onSuccess(List<ApiOrder> orders) {
                    int activeCount = 0;
                    if (orders != null) {
                        for (ApiOrder o : orders) {
                            if (o.isActive()) activeCount++;
                        }
                    }
                    if (activeOrdersCount != null) {
                        activeOrdersCount.setText(String.valueOf(activeCount));
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (activeOrdersCount != null) activeOrdersCount.setText("3");
                }
            });
        } else {
            activeOrdersCount.setText("0");
        }
    }

    private void updateReviewsCount() {
        TextView reviewSubtitle = findViewById(R.id.reviewSubtitle);
        if (reviewSubtitle == null) return;

        reviewSubtitle.setText("My reviews");

        if (tokenManager != null && tokenManager.isLoggedIn()) {
            ReviewRepository reviewRepository = ReviewRepository.getInstance(this);
            reviewRepository.getMyReviews(1, 20, new ReviewRepository.ReviewsCallback() {
                @Override
                public void onReviewsLoaded(List<Review> reviews) {
                    int count = reviews != null ? reviews.size() : 0;
                    if (reviewSubtitle != null) {
                        reviewSubtitle.setText(count + " written");
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    if (reviewSubtitle != null) {
                        reviewSubtitle.setText("My reviews");
                    }
                }
            });
        } else {
            reviewSubtitle.setText("My reviews");
        }
    }

    private void setupSettingsRows() {
        MaterialCardView myOrdersBox      = findViewById(R.id.myOrdersBox);
        MaterialCardView myCouponsBox     = findViewById(R.id.myCouponsBox);
        MaterialCardView editProfileBox = findViewById(R.id.editProfileBox);
        View myReviewsCard              = findViewById(R.id.myReviewsCard);
        LinearLayout aboutUsRow       = findViewById(R.id.aboutUsRow);
        LinearLayout privacyRow       = findViewById(R.id.privacyRow);
        LinearLayout termsRow         = findViewById(R.id.termsRow);
        
        TextView couponsCount = findViewById(R.id.couponsCount);
        
        // Update counts dynamically
        updateActiveOrdersCount();
        updateReviewsCount();
        if (couponsCount != null) couponsCount.setText("4 coupons");

        // Whatever ordersRow and couponsRow used to do, now on the tiles
        if (myOrdersBox != null) myOrdersBox.setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));
        if (myCouponsBox != null) myCouponsBox.setOnClickListener(v ->
                startActivity(new Intent(this, CouponsActivity.class)));
        if (editProfileBox != null) editProfileBox.setOnClickListener(v -> {
            startActivity(new Intent(this, EditProfileActivity.class));
        });
        if (myReviewsCard != null) myReviewsCard.setOnClickListener(v ->
                startActivity(new Intent(this, MyReviewsActivity.class)));
        if (aboutUsRow != null) aboutUsRow.setOnClickListener(v ->
                startActivity(new Intent(this, AboutActivity.class)));
        if (privacyRow != null) privacyRow.setOnClickListener(v ->
                startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        if (termsRow != null) termsRow.setOnClickListener(v ->
                startActivity(new Intent(this, TermsActivity.class)));

        UiAnimations.pressBounce(myOrdersBox, myCouponsBox, editProfileBox, myReviewsCard, aboutUsRow, privacyRow, termsRow);
        
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
     * Load avatar image using WebView to display DiceBear SVG avatars.
     * WebView renders SVG perfectly and supports all parameters.
     * 
     * @param avatarUrl DiceBear SVG URL
     */
    private void setupAvatarWebView(String avatarUrl) {
        WebView avatarWebView = findViewById(R.id.avatarWebView);
        if (avatarWebView == null) {
            android.util.Log.w("ProfileActivity", "avatarWebView not found in layout");
            return;
        }
        
        try {
            // Configure WebView settings for proper rendering
            avatarWebView.getSettings().setJavaScriptEnabled(true);
            avatarWebView.getSettings().setDomStorageEnabled(true);
            
            // Set WebViewClient to prevent opening browser
            avatarWebView.setWebViewClient(new WebViewClient());
            
            // Cache bust query parameter so new image displays immediately
            String cacheBustUrl = avatarUrl;
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                cacheBustUrl += (avatarUrl.contains("?") ? "&" : "?") + "v=" + System.currentTimeMillis();
            }

            // Load HTML with SVG that scales to fill the entire WebView
            // Avatar is positioned slightly lower and zoomed in
            String htmlContent = "<!DOCTYPE html>" +
                "<html><head>" +
                "<meta charset=\"utf-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<style>" +
                "  * { margin: 0; padding: 0; box-sizing: border-box; }" +
                "  body { width: 100%; height: 100%; display: flex; align-items: flex-end; justify-content: center; background: transparent; padding-bottom: 12px; }" +
                "  img { width: 110%; height: 110%; object-fit: contain; max-width: none; max-height: none; }" +
                "</style>" +
                "</head><body>" +
                "  <img src=\"" + cacheBustUrl + "\" alt=\"avatar\"/>" +
                "</body></html>";
            
            // Load the HTML content with proper base URL handling
            avatarWebView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
            
            android.util.Log.d("ProfileActivity", "Avatar loaded in WebView with full container fit: " + avatarUrl);
        } catch (Exception e) {
            android.util.Log.e("ProfileActivity", "Error setting up avatar WebView: " + e.getMessage());
            // Show fallback icon
            showAvatarFallback();
        }
    }
    
    /**
     * Show fallback ImageView if WebView fails to load.
     */
    private void showAvatarFallback() {
        ImageView avatarIcon = findViewById(R.id.avatarIcon);
        if (avatarIcon != null) {
            avatarIcon.setVisibility(View.VISIBLE);
            android.util.Log.d("ProfileActivity", "Showing avatar fallback icon");
        }
    }
}
