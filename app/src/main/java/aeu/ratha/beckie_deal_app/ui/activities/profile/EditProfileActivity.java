package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import aeu.ratha.beckie_deal_app.data.models.User;
import aeu.ratha.beckie_deal_app.data.repository.ProfileRepository;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EditProfileActivity extends BaseActivity {

    private TokenManager tokenManager;
    private ProfileRepository profileRepository;
    private DrawerLayout drawerLayout;

    private TextView firstNameField;
    private TextView lastNameField;
    private TextView emailField;
    private TextView phoneField;
    private MaterialButton saveButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tokenManager = TokenManager.getInstance(this);
        profileRepository = ProfileRepository.getInstance(this);

        drawerLayout = findViewById(R.id.drawerLayout);

        initializeViews();
        setupClickListeners();
        loadUserProfile();
        setupBottomNavigation();
        setupEntranceAnimation();
    }

    private void initializeViews() {
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        emailField = findViewById(R.id.emailField);
        phoneField = findViewById(R.id.phoneField);
        saveButton = findViewById(R.id.saveButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Back button
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
            UiAnimations.pressBounce(backButton);
        }

        // Save button
        if (saveButton != null) {
            saveButton.setOnClickListener(v -> saveProfile());
            UiAnimations.pressBounce(saveButton);
        }
    }

    private void loadUserProfile() {
        if (!tokenManager.isLoggedIn()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch user profile
        profileRepository.getProfile(false, new ProfileRepository.ProfileCallback() {
            @Override
            public void onSuccess(User profile) {
                displayProfile(profile);
            }

            @Override
            public void onFailure(Exception error) {
                Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProfile(User user) {
        if (user == null) return;

        if (firstNameField != null) {
            firstNameField.setText(user.getFirstName() != null ? user.getFirstName() : "");
        }
        if (lastNameField != null) {
            lastNameField.setText(user.getLastName() != null ? user.getLastName() : "");
        }
        if (emailField != null) {
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
        }
        if (phoneField != null) {
            phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
        }
    }

    private void saveProfile() {
        // Placeholder for save functionality
        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
        // TODO: Implement actual profile update API call
    }

    private void setupBottomNavigation() {
        BottomNavHelper.setup(this, BottomNavHelper.NavItem.ACCOUNT);
    }

    private void setupEntranceAnimation() {
        View contentView = findViewById(android.R.id.content);
        if (contentView != null) {
            UiAnimations.fadeSlideDown(contentView, 0);
        }
    }
}
