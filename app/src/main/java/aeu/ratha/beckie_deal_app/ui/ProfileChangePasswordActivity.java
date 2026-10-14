package aeu.ratha.beckie_deal_app.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.api.ProfileApiService;
import aeu.ratha.beckie_deal_app.data.models.ChangePasswordRequest;
import aeu.ratha.beckie_deal_app.data.models.ChangePasswordResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileChangePasswordActivity extends AppCompatActivity {

    // UI Components
    private ImageView backButton;
    private ImageView notificationIcon;
    private MaterialButton changePasswordButton;
    private MaterialButton cancelButton;
    
    // Input Fields
    private TextInputEditText currentPasswordInput;
    private TextInputEditText newPasswordInput;
    private TextInputEditText confirmPasswordInput;
    
    // Password Strength Components
    private View passwordStrengthContainer;
    private View strengthBar1, strengthBar2, strengthBar3, strengthBar4;
    private TextView strengthText;
    
    // Password Requirements
    private View req1Check, req2Check, req3Check;
    
    // Loading state
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change_password);

        initializeViews();
        setupListeners();
        setupPasswordValidation();
    }

    private void initializeViews() {
        // Top bar
        backButton = findViewById(R.id.backButton);
        notificationIcon = findViewById(R.id.notificationIcon);
        
        // Input fields
        currentPasswordInput = findViewById(R.id.currentPasswordInput);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        
        // Password strength indicator
        passwordStrengthContainer = findViewById(R.id.passwordStrengthContainer);
        strengthBar1 = findViewById(R.id.strengthBar1);
        strengthBar2 = findViewById(R.id.strengthBar2);
        strengthBar3 = findViewById(R.id.strengthBar3);
        strengthBar4 = findViewById(R.id.strengthBar4);
        strengthText = findViewById(R.id.strengthText);
        
        // Password requirements
        req1Check = findViewById(R.id.req1Check);
        req2Check = findViewById(R.id.req2Check);
        req3Check = findViewById(R.id.req3Check);
        
        // Buttons
        changePasswordButton = findViewById(R.id.changePasswordButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());
        
        // Cancel button
        cancelButton.setOnClickListener(v -> finish());
        
        // Change password button
        changePasswordButton.setOnClickListener(v -> validateAndChangePassword());
        
        // Notification icon (optional navigation)
        notificationIcon.setOnClickListener(v -> {
            // Navigate to notifications if needed
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupPasswordValidation() {
        // Monitor new password for strength and requirements
        newPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                updatePasswordStrength(password);
                updatePasswordRequirements(password);
                
                // Show/hide strength indicator when typing
                if (password.length() > 0) {
                    passwordStrengthContainer.setVisibility(View.VISIBLE);
                } else {
                    passwordStrengthContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void updatePasswordStrength(String password) {
        int strength = calculatePasswordStrength(password);
        
        // Reset all bars
        int barColor = getResources().getColor(R.color.text_muted, null);
        strengthBar1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(barColor));
        strengthBar2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(barColor));
        strengthBar3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(barColor));
        strengthBar4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(barColor));
        
        if (strength == 1) {
            // Weak
            strengthBar1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.red_error, null)));
            strengthText.setText(R.string.password_strength_weak);
            strengthText.setTextColor(getResources().getColor(R.color.red_error, null));
        } else if (strength == 2) {
            // Fair
            strengthBar1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.coupon_accent, null)));
            strengthBar2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.coupon_accent, null)));
            strengthText.setText(R.string.password_strength_fair);
            strengthText.setTextColor(getResources().getColor(R.color.coupon_accent, null));
        } else if (strength == 3) {
            // Good
            strengthBar1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.yellow_star, null)));
            strengthBar2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.yellow_star, null)));
            strengthBar3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.yellow_star, null)));
            strengthText.setText(R.string.password_strength_good);
            strengthText.setTextColor(getResources().getColor(R.color.yellow_star, null));
        } else if (strength >= 4) {
            // Strong
            strengthBar1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.green_stock, null)));
            strengthBar2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.green_stock, null)));
            strengthBar3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.green_stock, null)));
            strengthBar4.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.green_stock, null)));
            strengthText.setText(R.string.password_strength_strong);
            strengthText.setTextColor(getResources().getColor(R.color.green_stock, null));
        }
    }

    private int calculatePasswordStrength(String password) {
        if (password.length() < 8) return 1;
        
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};:'\",.<>?/].*");
        
        int strength = 1;
        if (hasLower && hasUpper) strength = 2;
        if (hasLower && hasUpper && hasDigit) strength = 3;
        if (hasLower && hasUpper && hasDigit && hasSpecial) strength = 4;
        
        return strength;
    }

    private void updatePasswordRequirements(String password) {
        // Requirement 1: At least 8 characters
        boolean req1 = password.length() >= 8;
        updateRequirementCheckbox(req1Check, req1);
        
        // Requirement 2: Contains uppercase and lowercase
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean req2 = hasLower && hasUpper;
        updateRequirementCheckbox(req2Check, req2);
        
        // Requirement 3: Contains numbers and special characters
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};:'\",.<>?/].*");
        boolean req3 = hasDigit && hasSpecial;
        updateRequirementCheckbox(req3Check, req3);
    }

    private void updateRequirementCheckbox(View checkView, boolean met) {
        if (met) {
            checkView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.green_stock, null)));
        } else {
            checkView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(R.color.text_muted, null)));
        }
    }

    private void validateAndChangePassword() {
        // Get values
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        
        // Validation
        if (currentPassword.isEmpty()) {
            showError("Please enter your current password");
            return;
        }
        
        if (newPassword.isEmpty()) {
            showError("Please enter a new password");
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            showError("Please confirm your new password");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            showError("New passwords do not match");
            return;
        }
        
        if (currentPassword.equals(newPassword)) {
            showError("New password must be different from current password");
            return;
        }
        
        if (newPassword.length() < 8) {
            showError("Password must be at least 8 characters long");
            return;
        }
        
        // Call API
        changePassword(currentPassword, newPassword);
    }

    private void changePassword(String currentPassword, String newPassword) {
        if (isLoading) return;
        
        isLoading = true;
        changePasswordButton.setEnabled(false);
        changePasswordButton.setText("Changing...");
        
        try {
            ProfileApiService apiService = RetrofitClient.getClient(this).create(ProfileApiService.class);
            ChangePasswordRequest request = new ChangePasswordRequest(
                    currentPassword,
                    newPassword,
                    newPassword // confirmation
            );
            
            Call<ChangePasswordResponse> call = apiService.changePassword(request);
            call.enqueue(new Callback<ChangePasswordResponse>() {
                @Override
                public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                    isLoading = false;
                    changePasswordButton.setEnabled(true);
                    changePasswordButton.setText(R.string.btn_change_password);
                    
                    if (response.isSuccessful() && response.body() != null) {
                        ChangePasswordResponse result = response.body();
                        
                        if (result.isSuccess()) {
                            showSuccess("Password changed successfully!");
                            // Finish activity after 1.5 seconds
                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
                                    () -> finish(),
                                    1500
                            );
                        } else {
                            showError(result.getMessage());
                        }
                    } else {
                        showError("Failed to change password. Please try again.");
                    }
                }
                
                @Override
                public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                    isLoading = false;
                    changePasswordButton.setEnabled(true);
                    changePasswordButton.setText(R.string.btn_change_password);
                    showError("Error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            isLoading = false;
            changePasswordButton.setEnabled(true);
            changePasswordButton.setText(R.string.btn_change_password);
            showError("Error: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
