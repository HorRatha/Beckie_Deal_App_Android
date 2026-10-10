package aeu.ratha.beckie_deal_app.ui.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.api.AuthApiService;
import aeu.ratha.beckie_deal_app.data.models.LoginRequest;
import aeu.ratha.beckie_deal_app.data.models.VerifyEmailOtpRequest;
import aeu.ratha.beckie_deal_app.data.models.VerifyEmailOtpResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import aeu.ratha.beckie_deal_app.security.TokenManager;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for email OTP verification after user registration.
 * Displays a 6-digit OTP input form with auto-advance between fields.
 */
public class VerifyEmailOtpActivity extends BaseActivity {
    private static final String TAG = "VerifyEmailOtpActivity";
    private static final int RESEND_TIMER_SECONDS = 60;
    
    // UI Components
    private ImageView backButton;
    private TextView emailDisplayText;
    private EditText otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6;
    private androidx.appcompat.widget.AppCompatButton verifyButton;
    private TextView resendOtpLink;
    private TextView resendTimer;
    private TextView errorMessage;
    private ProgressBar verifyProgressBar;
    
    // Data
    private String userEmail;
    private AuthApiService authApiService;
    private TokenManager tokenManager;
    private CountDownTimer resendCountdownTimer;
    private boolean isResendEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeStatusBarTransparent();
        setContentView(R.layout.activity_verify_email_otp);

        // Initialize API service and TokenManager
        authApiService = RetrofitClient.getClient(this).create(AuthApiService.class);
        tokenManager = TokenManager.getInstance(this);

        // Get email from intent
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");
        
        if (userEmail == null || userEmail.isEmpty()) {
            Log.e(TAG, "Email not provided in intent");
            Toast.makeText(this, "Error: Email not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize UI
        initializeViews();
        setupListeners();
        setupOtpFields();
        setupEntranceAnimation();
        startResendTimer();
    }

    private void makeStatusBarTransparent() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(android.graphics.Color.TRANSPARENT);
        WindowCompat.setDecorFitsSystemWindows(window, false);
    }

    private void initializeViews() {
        backButton = findViewById(R.id.backButton);
        emailDisplayText = findViewById(R.id.emailDisplayText);
        otpDigit1 = findViewById(R.id.otpDigit1);
        otpDigit2 = findViewById(R.id.otpDigit2);
        otpDigit3 = findViewById(R.id.otpDigit3);
        otpDigit4 = findViewById(R.id.otpDigit4);
        otpDigit5 = findViewById(R.id.otpDigit5);
        otpDigit6 = findViewById(R.id.otpDigit6);
        verifyButton = findViewById(R.id.verifyButton);
        resendOtpLink = findViewById(R.id.resendOtpLink);
        resendTimer = findViewById(R.id.resendTimer);
        errorMessage = findViewById(R.id.errorMessage);
        verifyProgressBar = findViewById(R.id.verifyProgressBar);

        // Set email display
        if (emailDisplayText != null) {
            emailDisplayText.setText(userEmail);
        }

        // Apply animations
        UiAnimations.pressBounce(verifyButton, resendOtpLink);
    }

    private void setupListeners() {
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        if (verifyButton != null) {
            verifyButton.setOnClickListener(v -> attemptVerifyOtp());
        }

        if (resendOtpLink != null) {
            resendOtpLink.setOnClickListener(v -> {
                if (isResendEnabled) {
                    resendOtp();
                } else {
                    Toast.makeText(this, "Please wait before requesting a new code", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Setup OTP input fields with auto-advance logic.
     * When a digit is entered, automatically move to the next field.
     */
    private void setupOtpFields() {
        EditText[] otpFields = {otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6};

        for (int i = 0; i < otpFields.length; i++) {
            final int currentIndex = i;
            final EditText currentField = otpFields[i];
            final EditText nextField = (i < otpFields.length - 1) ? otpFields[i + 1] : null;
            final EditText prevField = (i > 0) ? otpFields[i - 1] : null;

            currentField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1) {
                        // Move to next field
                        if (nextField != null) {
                            nextField.requestFocus();
                        } else {
                            // Last field filled - attempt verification
                            currentField.clearFocus();
                            hideKeyboard(currentField);
                        }
                        // Clear error message when user starts typing
                        if (errorMessage != null) {
                            errorMessage.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            // Handle backspace/delete to move to previous field
            currentField.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL) {
                    if (currentField.getText().length() == 0 && prevField != null) {
                        prevField.requestFocus();
                        prevField.setText("");
                    }
                }
                return false;
            });
        }
    }

    /**
     * Setup entrance animation for the screen.
     */
    private void setupEntranceAnimation() {
        View header = findViewById(R.id.verifyHeaderWave);
        if (header != null) {
            UiAnimations.fadeSlideDown(header, 0);
        }
    }

    /**
     * Attempt to verify OTP with backend.
     */
    private void attemptVerifyOtp() {
        // Collect OTP from all fields
        String otp = otpDigit1.getText().toString() +
                otpDigit2.getText().toString() +
                otpDigit3.getText().toString() +
                otpDigit4.getText().toString() +
                otpDigit5.getText().toString() +
                otpDigit6.getText().toString();

        // Validate OTP
        if (otp.length() != 6) {
            showError("Please enter all 6 digits");
            return;
        }

        if (!otp.matches("\\d{6}")) {
            showError("OTP must contain only numbers");
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Create verification request
        VerifyEmailOtpRequest verifyRequest = new VerifyEmailOtpRequest(userEmail, otp);

        // Make API call
        Call<VerifyEmailOtpResponse> call = authApiService.verifyEmailOtp(verifyRequest);
        call.enqueue(new Callback<VerifyEmailOtpResponse>() {
            @Override
            public void onResponse(Call<VerifyEmailOtpResponse> call, Response<VerifyEmailOtpResponse> response) {
                setLoadingState(false);

                if (response.isSuccessful() && response.body() != null) {
                    handleVerificationSuccess(response.body());
                } else {
                    handleVerificationError(response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<VerifyEmailOtpResponse> call, Throwable t) {
                setLoadingState(false);
                handleNetworkError(t);
            }
        });
    }

    /**
     * Handle successful OTP verification.
     */
    private void handleVerificationSuccess(VerifyEmailOtpResponse response) {
        if (response.isSuccess() && response.getData() != null) {
            VerifyEmailOtpResponse.VerifyOtpData data = response.getData();

            // Save authentication data
            tokenManager.saveAuthData(
                    data.getAccessToken(),
                    data.getTokenType(),
                    data.getExpiresIn(),
                    data.getUser()
            );

            Log.d(TAG, "Email verification successful for: " + userEmail);

            if (data.getUser() != null) {
                Toast.makeText(this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
                showWelcomeMessageAndNavigate(data.getUser().getFullName());
            } else {
                Toast.makeText(this, "Email verified! Welcome!", Toast.LENGTH_SHORT).show();
                navigateToMainActivity();
            }
        } else {
            Toast.makeText(this, response.getMessage() != null ? response.getMessage() : "Verification failed", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handle verification error from API.
     */
    private void handleVerificationError(int statusCode, String message) {
        Log.e(TAG, "Verification error: " + statusCode + " - " + message);

        String errorMsg;
        switch (statusCode) {
            case 400:
                errorMsg = "Invalid or expired OTP";
                break;
            case 422:
                errorMsg = "OTP verification failed. Please check and try again";
                break;
            case 500:
                errorMsg = "Server error. Please try again later";
                break;
            default:
                errorMsg = "Verification failed. Please try again";
        }

        showError(errorMsg);
        clearOtpFields();
        otpDigit1.requestFocus();
    }

    /**
     * Handle network error.
     */
    private void handleNetworkError(Throwable t) {
        Log.e(TAG, "Network error during verification", t);

        String errorMsg = "Network error. Please check your internet connection";
        if (t.getMessage() != null && t.getMessage().contains("timeout")) {
            errorMsg = "Connection timeout. Please try again";
        }

        showError(errorMsg);
    }

    /**
     * Resend OTP to user's email.
     */
    private void resendOtp() {
        setLoadingState(true);

        // Create resend request
        LoginRequest resendRequest = new LoginRequest(userEmail, "");

        Call<aeu.ratha.beckie_deal_app.data.models.LoginResponse> call = authApiService.resendEmailOtp(resendRequest);
        call.enqueue(new Callback<aeu.ratha.beckie_deal_app.data.models.LoginResponse>() {
            @Override
            public void onResponse(Call<aeu.ratha.beckie_deal_app.data.models.LoginResponse> call,
                                 Response<aeu.ratha.beckie_deal_app.data.models.LoginResponse> response) {
                setLoadingState(false);

                if (response.isSuccessful()) {
                    Toast.makeText(VerifyEmailOtpActivity.this, "OTP resent successfully! Check your email", Toast.LENGTH_SHORT).show();
                    clearOtpFields();
                    otpDigit1.requestFocus();
                    startResendTimer();
                } else {
                    Toast.makeText(VerifyEmailOtpActivity.this, "Failed to resend OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<aeu.ratha.beckie_deal_app.data.models.LoginResponse> call, Throwable t) {
                setLoadingState(false);
                Toast.makeText(VerifyEmailOtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Start countdown timer for resend OTP functionality.
     */
    private void startResendTimer() {
        isResendEnabled = false;

        if (resendCountdownTimer != null) {
            resendCountdownTimer.cancel();
        }

        resendCountdownTimer = new CountDownTimer(RESEND_TIMER_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                if (resendTimer != null) {
                    resendTimer.setText("Resend in " + secondsRemaining + "s");
                }
            }

            @Override
            public void onFinish() {
                isResendEnabled = true;
                if (resendTimer != null) {
                    resendTimer.setText("You can now resend the code");
                }
                if (resendOtpLink != null) {
                    resendOtpLink.setAlpha(1.0f);
                }
            }
        };

        resendCountdownTimer.start();

        if (resendOtpLink != null) {
            resendOtpLink.setAlpha(0.5f);
        }
    }

    /**
     * Show error message on screen.
     */
    private void showError(String message) {
        if (errorMessage != null) {
            errorMessage.setText(message);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Clear all OTP input fields.
     */
    private void clearOtpFields() {
        otpDigit1.setText("");
        otpDigit2.setText("");
        otpDigit3.setText("");
        otpDigit4.setText("");
        otpDigit5.setText("");
        otpDigit6.setText("");
    }

    /**
     * Set loading state (disable/enable UI elements).
     */
    private void setLoadingState(boolean isLoading) {
        if (verifyButton != null) {
            verifyButton.setEnabled(!isLoading);
            verifyButton.setText(isLoading ? "Verifying..." : "Verify");
        }

        if (resendOtpLink != null) {
            resendOtpLink.setEnabled(!isLoading);
        }

        if (verifyProgressBar != null) {
            verifyProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }

        // Disable OTP input fields during loading
        EditText[] otpFields = {otpDigit1, otpDigit2, otpDigit3, otpDigit4, otpDigit5, otpDigit6};
        for (EditText field : otpFields) {
            field.setEnabled(!isLoading);
        }
    }

    /**
     * Show welcome message and navigate to main activity.
     */
    private void showWelcomeMessageAndNavigate(String userName) {
        if (userName != null && !userName.isEmpty()) {
            Toast.makeText(this, "Welcome, " + userName + "!", Toast.LENGTH_SHORT).show();
        }
        navigateToMainActivity();
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
     * Hide keyboard from EditText view.
     */
    private void hideKeyboard(EditText editText) {
        android.view.inputmethod.InputMethodManager imm =
                (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resendCountdownTimer != null) {
            resendCountdownTimer.cancel();
        }
    }
}
