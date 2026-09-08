package aeu.ratha.beckie_deal_app.ui.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.api.AuthApiService;
import aeu.ratha.beckie_deal_app.data.models.RegisterRequest;
import aeu.ratha.beckie_deal_app.data.models.RegisterResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";
    
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private Button registerButton;
    private AuthApiService authApiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent();
        setContentView(R.layout.activity_register);

        // Initialize API service and TokenManager
        authApiService = RetrofitClient.getClient(this).create(AuthApiService.class);
        tokenManager = TokenManager.getInstance(this);
        
        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateToMainActivity();
            return;
        }

        setupTopBar();
        setupForm();
        setupEntranceAnimation();
    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

    /** Fades the header wave in, then staggers every form row up from below. */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.registerHeaderWave), 0);

        View formSection = findViewById(R.id.registerFormSection);
        if (formSection instanceof android.view.ViewGroup) {
            UiAnimations.staggerChildrenIn((android.view.ViewGroup) formSection, 150L);
        }
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        UiAnimations.pressBounce(backButton);
    }

    private void setupForm() {
        // Initialize views
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        TextView loginLink = findViewById(R.id.loginLink);
        View termsCheckbox = findViewById(R.id.termsCheckbox);
        
        // Apply animations
        UiAnimations.pressBounce(registerButton, loginLink, termsCheckbox);

        // Register button click listener
        if (registerButton != null) {
            registerButton.setOnClickListener(v -> attemptRegister());
        }

        // Login link click listener
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }
    
    /**
     * Attempt to register new user account.
     * Validates input and makes API call.
     */
    private void attemptRegister() {
        // Get input values
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();
        
        // Validate input
        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            firstNameInput.requestFocus();
            return;
        }
        
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            lastNameInput.requestFocus();
            return;
        }
        
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email");
            emailInput.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }
        
        if (password.length() < 8) {
            passwordInput.setError("Password must be at least 8 characters");
            passwordInput.requestFocus();
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError("Please confirm your password");
            confirmPasswordInput.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            confirmPasswordInput.requestFocus();
            return;
        }
        
        // Show loading state
        setLoadingState(true);
        
        // Combine first and last name into full name for backend
        String fullName = firstName + " " + lastName;
        
        // Create register request - backend expects "name" not "first_name" + "last_name"
        RegisterRequest registerRequest = new RegisterRequest(email, fullName, password, confirmPassword);
        
        // Make API call
        Call<RegisterResponse> call = authApiService.register(registerRequest);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                setLoadingState(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    handleRegisterSuccess(response.body());
                } else {
                    handleRegisterError(response.code(), response.message());
                }
            }
            
            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                setLoadingState(false);
                handleNetworkError(t);
            }
        });
    }
    
    /**
     * Handle successful registration response.
     */
    private void handleRegisterSuccess(RegisterResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            RegisterResponse.RegisterData data = response.getData();
            
            // Save authentication data
            tokenManager.saveAuthData(
                data.getAccessToken(),
                data.getTokenType(),
                data.getExpiresIn(),
                data.getUser()
            );
            
            Log.d(TAG, "Registration successful for user: " + data.getUser().getEmail());
            
            // Show success message
            String userName = data.getUser().getFullName();
            Toast.makeText(this, "Welcome, " + userName + "! Account created successfully.", Toast.LENGTH_SHORT).show();
            
            // Navigate to main activity
            navigateToMainActivity();
        } else {
            Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Registration failed", Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Handle registration error from API.
     */
    private void handleRegisterError(int statusCode, String message) {
        Log.e(TAG, "Registration error: " + statusCode + " - " + message);
        
        String errorMessage;
        switch (statusCode) {
            case 422:
                errorMessage = "Email already exists or invalid input. Please check and try again";
                break;
            case 500:
                errorMessage = "Server error. Please try again later";
                break;
            default:
                errorMessage = "Registration failed. Please try again";
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Handle network error.
     */
    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Network error during registration", t);
        
        String errorMessage = "Network error. Please check your internet connection";
        if (t.getMessage() != null && t.getMessage().contains("timeout")) {
            errorMessage = "Connection timeout. Please try again";
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Set loading state (disable/enable button and inputs).
     */
    private void setLoadingState(boolean isLoading) {
        if (registerButton != null) {
            registerButton.setEnabled(!isLoading);
            registerButton.setText(isLoading ? "Creating Account..." : "Create Account");
        }
        
        if (firstNameInput != null) {
            firstNameInput.setEnabled(!isLoading);
        }
        
        if (lastNameInput != null) {
            lastNameInput.setEnabled(!isLoading);
        }
        
        if (emailInput != null) {
            emailInput.setEnabled(!isLoading);
        }
        
        if (passwordInput != null) {
            passwordInput.setEnabled(!isLoading);
        }
        
        if (confirmPasswordInput != null) {
            confirmPasswordInput.setEnabled(!isLoading);
        }
    }
    
    /**
     * Navigate to MainActivity and clear back stack.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
