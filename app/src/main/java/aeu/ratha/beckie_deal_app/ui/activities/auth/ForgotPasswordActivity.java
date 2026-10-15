package aeu.ratha.beckie_deal_app.ui.activities.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import aeu.ratha.beckie_deal_app.R;

/**
 * ForgotPasswordActivity handles the forgot password flow with 2 steps:
 * Step 1: User enters email to receive reset code
 * Step 2: User verifies OTP and sets new password
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    // UI Components - Header
    private ImageView backButton;

    // UI Components - Progress Indicator
    private FrameLayout step2Circle;
    private View progressLine;
    private TextView step2CircleText;

    // UI Components - Step 1: Email Form
    private LinearLayout step1EmailForm;
    private EditText emailInput;
    private TextView emailErrorMessage;
    private AppCompatButton sendCodeButton;
    private TextView backToLoginLink;

    // UI Components - Step 2: OTP & Password Form
    private LinearLayout step2OtpForm;
    private TextView emailDisplayText;
    private EditText[] otpDigits = new EditText[6];
    private TextView otpErrorMessage;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private TextView passwordErrorMessage;
    private AppCompatButton resetPasswordButton;
    private TextView resendOtpLink;
    private TextView resendTimer;

    // UI Components - Progress
    private ProgressBar progressBar;

    // Data
    private String currentEmail = "";
    private boolean isResendCooldown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initializeViews();
        setupListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        // Header
        backButton = findViewById(R.id.backButton);

        // Progress Indicator
        step2Circle = findViewById(R.id.step2Circle);
        progressLine = findViewById(R.id.progressLine);
        step2CircleText = step2Circle.findViewById(android.R.id.text1);

        // Step 1 Form
        step1EmailForm = findViewById(R.id.step1EmailForm);
        emailInput = findViewById(R.id.emailInput);
        emailErrorMessage = findViewById(R.id.emailErrorMessage);
        sendCodeButton = findViewById(R.id.sendCodeButton);
        backToLoginLink = findViewById(R.id.backToLoginLink);

        // Step 2 Form
        step2OtpForm = findViewById(R.id.step2OtpForm);
        emailDisplayText = findViewById(R.id.emailDisplayText);
        otpDigits[0] = findViewById(R.id.otpDigit1);
        otpDigits[1] = findViewById(R.id.otpDigit2);
        otpDigits[2] = findViewById(R.id.otpDigit3);
        otpDigits[3] = findViewById(R.id.otpDigit4);
        otpDigits[4] = findViewById(R.id.otpDigit5);
        otpDigits[5] = findViewById(R.id.otpDigit6);
        otpErrorMessage = findViewById(R.id.otpErrorMessage);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        passwordErrorMessage = findViewById(R.id.passwordErrorMessage);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        resendOtpLink = findViewById(R.id.resendOtpLink);
        resendTimer = findViewById(R.id.resendTimer);

        // Progress
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Setup click listeners and text watchers
     */
    private void setupListeners() {
        // Back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Step 1 Listeners
        sendCodeButton.setOnClickListener(v -> sendResetCode());
        backToLoginLink.setOnClickListener(v -> finish());

        // Step 2 Listeners
        resetPasswordButton.setOnClickListener(v -> resetPassword());
        resendOtpLink.setOnClickListener(v -> resendOtp());

        // OTP digit auto-focus
        setupOtpInputs();
    }

    /**
     * Setup OTP input fields to auto-focus to next field
     */
    private void setupOtpInputs() {
        for (int i = 0; i < otpDigits.length; i++) {
            final int currentIndex = i;
            otpDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && currentIndex < otpDigits.length - 1) {
                        otpDigits[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    /**
     * Step 1: Send reset code to email
     * This corresponds to the first API response: {"email": "customer@example.com"}
     */
    private void sendResetCode() {
        String email = emailInput.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            showEmailError("Please enter your email address");
            return;
        }

        if (!isValidEmail(email)) {
            showEmailError("Please enter a valid email address");
            return;
        }

        clearEmailError();
        currentEmail = email;

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        sendCodeButton.setEnabled(false);

        // Simulate API call
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);

            // API Response: {"email": "customer@example.com"}
            // In real implementation, call your API here
            moveToStep2();

            sendCodeButton.setEnabled(true);
            Toast.makeText(this, "Reset code sent to " + email, Toast.LENGTH_SHORT).show();
        }, 1500);
    }

    /**
     * Transition from Step 1 to Step 2
     */
    private void moveToStep2() {
        // Update email display
        emailDisplayText.setText(currentEmail);

        // Update progress indicator
        updateProgressIndicator();

        // Animate transition
        step1EmailForm.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    step1EmailForm.setVisibility(View.GONE);
                    step2OtpForm.setVisibility(View.VISIBLE);
                    step2OtpForm.setAlpha(0f);
                    step2OtpForm.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();
                    otpDigits[0].requestFocus();
                    startResendTimer();
                })
                .start();
    }

    /**
     * Update progress indicator to show step 2 is active
     */
    private void updateProgressIndicator() {
        // Change step 2 circle to primary color
        step2Circle.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(getColor(R.color.primary))
        );
        // Update step 2 text color
        TextView step2Text = step2Circle.findViewWithTag("step2Text");
        if (step2Text != null) {
            step2Text.setTextColor(getColor(R.color.dark_brown));
        }
        // Change progress line to primary color
        progressLine.setBackgroundColor(getColor(R.color.primary));
    }

    /**
     * Step 2: Reset password with OTP verification
     * This corresponds to the second API response: {"email": "customer@example.com", "otp": "123456"}
     */
    private void resetPassword() {
        String otp = getOtpValue();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validation
        clearErrors();

        if (otp.length() != 6) {
            showOtpError("Please enter the complete 6-digit code");
            return;
        }

        if (newPassword.isEmpty()) {
            showPasswordError("Please enter a new password");
            return;
        }

        if (newPassword.length() < 8) {
            showPasswordError("Password must be at least 8 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showPasswordError("Passwords do not match");
            return;
        }

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        resetPasswordButton.setEnabled(false);

        // Simulate API call
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);

            // API Response: {"email": "customer@example.com", "otp": "123456"}
            // In real implementation, call your API here with:
            // - email: currentEmail
            // - otp: otp
            // - newPassword: newPassword

            resetPasswordButton.setEnabled(true);
            Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }, 1500);
    }

    /**
     * Resend OTP to email
     */
    private void resendOtp() {
        if (isResendCooldown) {
            Toast.makeText(this, "Please wait before resending", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        resendOtpLink.setEnabled(false);

        // Simulate API call
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            resendOtpLink.setEnabled(true);

            // Clear OTP fields
            for (EditText digit : otpDigits) {
                digit.setText("");
            }
            otpDigits[0].requestFocus();

            Toast.makeText(this, "New code sent to " + currentEmail, Toast.LENGTH_SHORT).show();
            startResendTimer();
        }, 1000);
    }

    /**
     * Start cooldown timer for resend button
     */
    private void startResendTimer() {
        isResendCooldown = true;
        resendOtpLink.setEnabled(false);
        int secondsRemaining = 60;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            int seconds = secondsRemaining;

            @Override
            public void run() {
                resendTimer.setText("Resend in " + seconds + "s");

                if (seconds > 0) {
                    seconds--;
                    handler.postDelayed(this, 1000);
                } else {
                    isResendCooldown = false;
                    resendOtpLink.setEnabled(true);
                    resendTimer.setText("Resend in 0s");
                }
            }
        });
    }

    /**
     * Get combined OTP value from all digit fields
     */
    private String getOtpValue() {
        StringBuilder otp = new StringBuilder();
        for (EditText digit : otpDigits) {
            otp.append(digit.getText().toString());
        }
        return otp.toString();
    }

    /**
     * Email validation
     */
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Clear all error messages
     */
    private void clearErrors() {
        clearEmailError();
        clearOtpError();
        clearPasswordError();
    }

    private void showEmailError(String message) {
        emailErrorMessage.setText(message);
        emailErrorMessage.setVisibility(View.VISIBLE);
    }

    private void clearEmailError() {
        emailErrorMessage.setVisibility(View.GONE);
    }

    private void showOtpError(String message) {
        otpErrorMessage.setText(message);
        otpErrorMessage.setVisibility(View.VISIBLE);
    }

    private void clearOtpError() {
        otpErrorMessage.setVisibility(View.GONE);
    }

    private void showPasswordError(String message) {
        passwordErrorMessage.setText(message);
        passwordErrorMessage.setVisibility(View.VISIBLE);
    }

    private void clearPasswordError() {
        passwordErrorMessage.setVisibility(View.GONE);
    }
}
