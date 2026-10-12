package aeu.ratha.beckie_deal_app.ui.activities.auth;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.api.AuthApiService;
import aeu.ratha.beckie_deal_app.data.models.LoginRequest;
import aeu.ratha.beckie_deal_app.data.models.LoginResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button signInToggle;
    private Button registerToggle;
    private LinearLayout loginFormSection;
    private LinearLayout registerFormSection;
    
    // Register form fields
    private EditText regNameInput;
    private EditText regEmailInput;
    private EditText regPasswordInput;
    private Button registerButton;
    
    private boolean isLoginMode = true;
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
        
        // Check if should open register tab
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("openRegister", false)) {
            // Open register form after a short delay to let animations complete
            findViewById(R.id.loginFormSection).postDelayed(() -> switchToRegisterForm(), 300);
        }
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
        // Initialize login views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signInToggle = findViewById(R.id.signInToggle);
        registerToggle = findViewById(R.id.registerToggle);
        loginFormSection = findViewById(R.id.loginFormSection);
        registerFormSection = findViewById(R.id.registerFormSection);
        TextView registerLink = findViewById(R.id.registerLink);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        TextView loginLink = findViewById(R.id.loginLink);
        
        // Initialize register views
        regNameInput = findViewById(R.id.nameInput);
        regEmailInput = findViewById(R.id.regEmailInput);
        regPasswordInput = findViewById(R.id.regPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        
        // Apply animations
        UiAnimations.pressBounce(loginButton, registerLink, forgotPassword, registerButton, loginLink);

        // Setup toggle button listeners
        if (signInToggle != null) {
            signInToggle.setOnClickListener(v -> switchToLoginForm());
        }

        if (registerToggle != null) {
            registerToggle.setOnClickListener(v -> switchToRegisterForm());
        }

        // Login button click listener
        if (loginButton != null) {
            loginButton.setOnClickListener(v -> attemptLogin());
        }

        // Register button click listener
        if (registerButton != null) {
            registerButton.setOnClickListener(v -> attemptRegister());
        }

        // Register link click listener
        if (registerLink != null) {
            registerLink.setOnClickListener(v -> switchToRegisterForm());
        }

        // Forgot password click listener
        if (forgotPassword != null) {
            forgotPassword.setOnClickListener(v -> 
                Toast.makeText(this, "Redirecting to password reset...", Toast.LENGTH_SHORT).show());
        }

        // Login link click listener (from register form)
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> switchToLoginForm());
        }

        // Set initial button states (Sign In activated by default)
        if (signInToggle != null) {
            signInToggle.setActivated(true);
            applyToggleButtonColors(signInToggle, true);
            signInToggle.refreshDrawableState();
            Log.d(TAG, "Initial: Sign In button ACTIVATED (chocolate black)");
        }
        if (registerToggle != null) {
            registerToggle.setActivated(false);
            applyToggleButtonColors(registerToggle, false);
            registerToggle.refreshDrawableState();
            Log.d(TAG, "Initial: Register button NOT ACTIVATED (white)");
        }
    }
    
    /**
     * Apply text color and background tint to toggle buttons programmatically.
     * @param button The button to apply colors to
     * @param isActive Whether the button is in active state
     */
    private void applyToggleButtonColors(Button button, boolean isActive) {
        if (button == null) return;
        
        try {
            // CRITICAL: Disable Material3 background tinting to allow custom drawables
            button.setBackgroundTintList(null);
            
            // Set text colors directly
            if (isActive) {
                button.setTextColor(0xFFFFFFFF); // White for active (chocolate black background)
                Log.d(TAG, button.getText() + " - Set to ACTIVE: WHITE text on chocolate black bg");
            } else {
                button.setTextColor(0xFF3E2723); // Chocolate black for inactive (white background)
                Log.d(TAG, button.getText() + " - Set to INACTIVE: CHOCOLATE BLACK text on white bg");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying toggle button colors", e);
            // Fallback on error
            button.setBackgroundTintList(null); // Still disable tinting
            if (isActive) {
                button.setTextColor(0xFFFFFFFF); // White for active
            } else {
                button.setTextColor(0xFF3E2723); // Chocolate black for inactive
            }
        }
    }
    
    
    /**
     * Switch to Login form with smooth animation.
     */
    private void switchToLoginForm() {
        if (isLoginMode) return; // Already in login mode
        
        isLoginMode = true;
        
        // Toggle button states - Sign In activated (chocolate black), Register not activated
        if (signInToggle != null) {
            signInToggle.setActivated(true);
            applyToggleButtonColors(signInToggle, true);
            signInToggle.refreshDrawableState(); // Force refresh
            Log.d(TAG, "Sign In button ACTIVATED (chocolate black #3E2723)");
            Toast.makeText(this, "Sign In Activated", Toast.LENGTH_SHORT).show();
        }
        if (registerToggle != null) {
            registerToggle.setActivated(false);
            applyToggleButtonColors(registerToggle, false);
            registerToggle.refreshDrawableState(); // Force refresh
            Log.d(TAG, "Register button NOT ACTIVATED (white)");
        }

        // Animate form transition
        if (registerFormSection != null && loginFormSection != null) {
            // Slide out register form to the right
            registerFormSection.startAnimation(android.view.animation.AnimationUtils.loadAnimation(
                this, R.anim.slide_out_right));
            
            // After register slides out, slide in login from left
            registerFormSection.postDelayed(() -> {
                registerFormSection.setVisibility(View.GONE);
                loginFormSection.setVisibility(View.VISIBLE);
                loginFormSection.startAnimation(android.view.animation.AnimationUtils.loadAnimation(
                    this, R.anim.slide_in_left));
            }, 150);
        }
    }

    /**
     * Switch to Register form with smooth animation.
     */
    private void switchToRegisterForm() {
        if (!isLoginMode) return; // Already in register mode
        
        isLoginMode = false;
        
        // Toggle button states - Register activated (chocolate black), Sign In not activated
        if (signInToggle != null) {
            signInToggle.setActivated(false);
            applyToggleButtonColors(signInToggle, false);
            signInToggle.refreshDrawableState(); // Force refresh
            Log.d(TAG, "Sign In button NOT ACTIVATED (white)");
        }
        if (registerToggle != null) {
            registerToggle.setActivated(true);
            applyToggleButtonColors(registerToggle, true);
            registerToggle.refreshDrawableState(); // Force refresh
            Log.d(TAG, "Register button ACTIVATED (chocolate black #3E2723)");
            Toast.makeText(this, "Register Activated", Toast.LENGTH_SHORT).show();
        }

        // Animate form transition
        if (loginFormSection != null && registerFormSection != null) {
            // Slide out login form to the left
            loginFormSection.startAnimation(android.view.animation.AnimationUtils.loadAnimation(
                this, R.anim.slide_out_left));
            
            // After login slides out, slide in register from right
            loginFormSection.postDelayed(() -> {
                loginFormSection.setVisibility(View.GONE);
                registerFormSection.setVisibility(View.VISIBLE);
                registerFormSection.startAnimation(android.view.animation.AnimationUtils.loadAnimation(
                    this, R.anim.slide_in_right));
            }, 150);
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
            
            // Save authentication data (token first)
            tokenManager.saveAuthData(
                data.getAccessToken(),
                data.getTokenType(),
                data.getExpiresIn(),
                data.getUser()
            );
            
            // Log success
            if (data.getUser() != null) {
                Log.d(TAG, "Login successful for user: " + data.getUser().getEmail());
                showWelcomeMessageAndNavigate(data.getUser().getFullName());
            } else {
                // User data not in login response, fetch it
                Log.d(TAG, "Login successful - token saved, fetching user profile");
                fetchUserProfileAndNavigate();
            }
        } else {
            Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Login failed", Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Fetch user profile after login when it's not included in login response.
     */
    private void fetchUserProfileAndNavigate() {
        Log.d(TAG, "Fetching user profile from /auth/user");
        Call<aeu.ratha.beckie_deal_app.data.models.User> call = authApiService.getUserProfile();
        call.enqueue(new Callback<aeu.ratha.beckie_deal_app.data.models.User>() {
            @Override
            public void onResponse(Call<aeu.ratha.beckie_deal_app.data.models.User> call, 
                                 Response<aeu.ratha.beckie_deal_app.data.models.User> response) {
                Log.d(TAG, "Profile response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    aeu.ratha.beckie_deal_app.data.models.User user = response.body();
                    
                    Log.d(TAG, "User data received - Email: " + user.getEmail());
                    Log.d(TAG, "User firstName: " + user.getFirstName());
                    Log.d(TAG, "User lastName: " + user.getLastName());
                    Log.d(TAG, "User name: " + user.getName());
                    Log.d(TAG, "User fullName: " + user.getFullName());
                    
                    // Update user info in TokenManager
                    String accessToken = tokenManager.getAccessToken();
                    String tokenType = tokenManager.getTokenType();
                    int expiresIn = tokenManager.getExpiresIn();
                    tokenManager.saveAuthData(accessToken, tokenType, expiresIn, user);
                    
                    showWelcomeMessageAndNavigate(user.getFullName());
                } else {
                    // Profile fetch failed, but login was successful - still navigate
                    Log.w(TAG, "Failed to fetch user profile - Status code: " + response.code());
                    showWelcomeMessageAndNavigate("User");
                }
            }
            
            @Override
            public void onFailure(Call<aeu.ratha.beckie_deal_app.data.models.User> call, Throwable t) {
                // Profile fetch failed, but login was successful - still navigate
                Log.e(TAG, "Error fetching user profile: " + t.getMessage(), t);
                showWelcomeMessageAndNavigate("User");
            }
        });
    }
    
    /**
     * Show welcome message and navigate to main activity.
     */
    private void showWelcomeMessageAndNavigate(String userName) {
        Toast.makeText(this, "Welcome back, " + userName + "!", Toast.LENGTH_SHORT).show();
        navigateToMainActivity();
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
    
    /**
     * Attempt to register new user account.
     * Validates input and makes API call.
     */
    private void attemptRegister() {
        // Get input values
        String name = regNameInput.getText().toString().trim();
        String email = regEmailInput.getText().toString().trim();
        String password = regPasswordInput.getText().toString().trim();
        
        // Validate input
        if (name.isEmpty()) {
            regNameInput.setError("Full name is required");
            regNameInput.requestFocus();
            return;
        }
        
        if (email.isEmpty()) {
            regEmailInput.setError("Email is required");
            regEmailInput.requestFocus();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            regEmailInput.setError("Please enter a valid email");
            regEmailInput.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            regPasswordInput.setError("Password is required");
            regPasswordInput.requestFocus();
            return;
        }
        
        if (password.length() < 8) {
            regPasswordInput.setError("Password must be at least 8 characters");
            regPasswordInput.requestFocus();
            return;
        }
        
        // Show loading state
        setRegisterLoadingState(true);
        
        // Split name into first and last name
        String[] nameParts = name.trim().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        // Create register request with all required fields
        // Backend requires: first_name, last_name, name, email, password, password_confirmation
        aeu.ratha.beckie_deal_app.data.models.RegisterRequest registerRequest = 
            new aeu.ratha.beckie_deal_app.data.models.RegisterRequest(email, firstName, lastName, name, password, password);
        
        // Make API call
        Call<aeu.ratha.beckie_deal_app.data.models.RegisterResponse> call = authApiService.register(registerRequest);
        call.enqueue(new Callback<aeu.ratha.beckie_deal_app.data.models.RegisterResponse>() {
            @Override
            public void onResponse(Call<aeu.ratha.beckie_deal_app.data.models.RegisterResponse> call, 
                                 Response<aeu.ratha.beckie_deal_app.data.models.RegisterResponse> response) {
                setRegisterLoadingState(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    handleRegisterSuccess(response.body());
                } else {
                    handleRegisterError(response.code(), response.message());
                }
            }
            
            @Override
            public void onFailure(Call<aeu.ratha.beckie_deal_app.data.models.RegisterResponse> call, Throwable t) {
                setRegisterLoadingState(false);
                handleRegisterNetworkError(t);
            }
        });
    }
    
    /**
     * Handle successful registration response.
     */
    private void handleRegisterSuccess(aeu.ratha.beckie_deal_app.data.models.RegisterResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            aeu.ratha.beckie_deal_app.data.models.RegisterResponse.RegisterData data = response.getData();
            
            if (data.getUser() == null) {
                Log.e(TAG, "Registration response has no user data");
                Toast.makeText(this, "Registration successful but user data missing", Toast.LENGTH_LONG).show();
                return;
            }
            
            String userEmail = data.getUser().getEmail();
            
            Log.d(TAG, "Registration successful for user: " + userEmail);
            Log.d(TAG, "Email verification required: " + data.isEmailVerificationRequired());
            
            // DO NOT save the token yet if email verification is required
            if (data.isEmailVerificationRequired()) {
                Log.d(TAG, "Skipping token save - email verification required");
                Log.d(TAG, "Attempting to navigate to OTP verification...");
                
                // Show message about OTP verification
                Toast.makeText(this, "Registration successful! Check your email for verification code.", Toast.LENGTH_LONG).show();
                
                try {
                    // Navigate to OTP verification screen WITHOUT saving token
                    Intent intent = new Intent(LoginActivity.this, VerifyEmailOtpActivity.class);
                    intent.putExtra("email", userEmail);
                    Log.d(TAG, "Starting VerifyEmailOtpActivity with email: " + userEmail);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "Successfully started OTP verification activity");
                } catch (Exception e) {
                    Log.e(TAG, "Error starting OTP verification activity", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                // Email verification NOT required - save token and proceed
                Log.d(TAG, "Email verification not required - saving token");
                tokenManager.saveAuthData(
                    data.getAccessToken(),
                    data.getTokenType(),
                    data.getExpiresIn(),
                    data.getUser()
                );
                
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
                showWelcomeMessageAndNavigate(data.getUser().getFullName());
            }
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
     * Handle network error during registration.
     */
    private void handleRegisterNetworkError(Throwable t) {
        Log.e(TAG, "Network error during registration", t);
        
        String errorMessage = "Network error. Please check your internet connection";
        if (t.getMessage() != null && t.getMessage().contains("timeout")) {
            errorMessage = "Connection timeout. Please try again";
        }
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
    
    /**
     * Set loading state for register form (disable/enable button and inputs).
     */
    private void setRegisterLoadingState(boolean isLoading) {
        if (registerButton != null) {
            registerButton.setEnabled(!isLoading);
            registerButton.setText(isLoading ? "Creating Account..." : "Sign Up");
        }
        
        if (regNameInput != null) {
            regNameInput.setEnabled(!isLoading);
        }
        
        if (regEmailInput != null) {
            regEmailInput.setEnabled(!isLoading);
        }
        
        if (regPasswordInput != null) {
            regPasswordInput.setEnabled(!isLoading);
        }
    }
}
