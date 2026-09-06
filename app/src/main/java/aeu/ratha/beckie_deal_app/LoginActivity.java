package aeu.ratha.beckie_deal_app;

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

import aeu.ratha.beckie_deal_app.data.api.AuthApiService;
import aeu.ratha.beckie_deal_app.data.models.LoginRequest;
import aeu.ratha.beckie_deal_app.data.models.LoginResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private AuthApiService authApiService;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent();
        setContentView(R.layout.activity_login);

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

    /** Fades the header wave in, then staggers every form row up from below. */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.loginHeaderWave), 0);

        View formSection = findViewById(R.id.loginFormSection);
        if (formSection instanceof android.view.ViewGroup) {
            UiAnimations.staggerChildrenIn((android.view.ViewGroup) formSection, 150L);
        }
    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        WindowCompat.setDecorFitsSystemWindows(window, false);
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
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        TextView registerLink = findViewById(R.id.registerLink);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        
        // Apply animations
        UiAnimations.pressBounce(loginButton, registerLink, forgotPassword);

        // Login button click listener
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> attemptLogin());
        }

        // Register link click listener
        if (registerLink != null) {
            registerLink.setOnClickListener(v -> {
                startActivity(new Intent(this, RegisterActivity.class));
            });
        }

        // Forgot password click listener
        if (forgotPassword != null) {
            forgotPassword.setOnClickListener(v -> 
                Toast.makeText(this, "Redirecting to password reset...", Toast.LENGTH_SHORT).show());
        }
    }
    
    /**
     * Attempt to login with email and password.
     * Validates input and makes API call.
     */
    private void attemptLogin() {
        // Get input values
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        
        // Validate input
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
        
        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            passwordInput.requestFocus();
            return;
        }
        
        // Show loading state
        setLoadingState(true);
        
        // Create login request
        LoginRequest loginRequest = new LoginRequest(email, password);
        
        // Make API call
        Call<LoginResponse> call = authApiService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoadingState(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    handleLoginSuccess(response.body());
                } else {
                    handleLoginError(response.code(), response.message());
                }
            }
            
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoadingState(false);
                handleNetworkError(t);
            }
        });
    }
    
    /**
     * Handle successful login response.
     */
    private void handleLoginSuccess(LoginResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            LoginResponse.LoginData data = response.getData();
            
            // Save authentication data
            tokenManager.saveAuthData(
                data.getAccessToken(),
                data.getTokenType(),
                data.getExpiresIn(),
                data.getUser()
            );
            
            Log.d(TAG, "Login successful for user: " + data.getUser().getEmail());
            
            // Show success message
            String userName = data.getUser().getFullName();
            Toast.makeText(this, "Welcome back, " + userName + "!", Toast.LENGTH_SHORT).show();
            
            // Navigate to main activity
            navigateToMainActivity();
        } else {
            Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Login failed", Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Handle login error from API.
     */
    private void handleLoginError(int statusCode, String message) {
        Log.e(TAG, "Login error: " + statusCode + " - " + message);
        
        String errorMessage;
        switch (statusCode) {
            case 401:
                errorMessage = "Invalid email or password";
                break;
            case 422:
                errorMessage = "Please check your input and try again";
                break;
            case 500:
                errorMessage = "Server error. Please try again later";
                break;
            default:
                errorMessage = "Login failed. Please try again";
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Handle network error.
     */
    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Network error during login", t);
        
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
        if (loginButton != null) {
            loginButton.setEnabled(!isLoading);
            loginButton.setText(isLoading ? "Logging in..." : "Login");
        }
        
        if (emailInput != null) {
            emailInput.setEnabled(!isLoading);
        }
        
        if (passwordInput != null) {
            passwordInput.setEnabled(!isLoading);
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
