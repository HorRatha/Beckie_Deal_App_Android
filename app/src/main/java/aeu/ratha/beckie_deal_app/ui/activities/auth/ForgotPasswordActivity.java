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
import aeu.ratha.beckie_deal_app.ui.components.AnimatedStepSpinnerView;

/**
 * ForgotPasswordActivity handles the forgot password flow with 2 steps:
 * Step 1: User enters email to receive reset code
 * Step 2: User verifies OTP and sets new password
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    // UI Components - Header
    private ImageView backButton;

    // UI Components - Progress Indicator
    private FrameLayout step1Circle;
    private FrameLayout step2Circle;
    private View progressLine;
    private TextView step1Number;
    private TextView step2Number;
    private ImageView step1CheckIcon;
    private ImageView step2CheckIcon;
    private AnimatedStepSpinnerView stepSpinnerView;

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
    private TextView changeEmailLink;

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
        step1Circle = findViewById(R.id.step1Circle);
        step2Circle = findViewById(R.id.step2Circle);
        progressLine = findViewById(R.id.progressLine);
        step1Number = findViewById(R.id.step1Number);
        step2Number = findViewById(R.id.step2Number);
        stepSpinnerView = findViewById(R.id.stepSpinnerView);

        // Attach spinner to Step 1 initially
        if (step1Circle != null && stepSpinnerView != null) {
            step1Circle.post(() -> stepSpinnerView.attachToStep(step1Circle, false));
        }

        // Step 1 Form
        step1EmailForm = findViewById(R.id.step1EmailForm);
        emailInput = findViewById(R.id.emailInput);
        emailErrorMessage = findViewById(R.id.emailErrorMessage);
        sendCodeButton = findViewById(R.id.sendCodeButton);
        backToLoginLink = findViewById(R.id.backToLoginLink);

        // Step 2 Form
        step2OtpForm = findViewById(R.id.step2OtpForm);
        emailDisplayText = findViewById(R.id.emailDisplayText);
        changeEmailLink = findViewById(R.id.changeEmailLink);
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
        
        // Change email link: go back to step 1
        if (changeEmailLink != null) {
            changeEmailLink.setOnClickListener(v -> goBackToStep1());
        }

        // OTP digit auto-focus
        setupOtpInputs();
    }

    /**
     * Go back from Step 2 to Step 1 (for changing email)
     */
    private void goBackToStep1() {
        // Hide step 2
        step2OtpForm.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> {
                    step2OtpForm.setVisibility(View.GONE);
                    
                    // Show step 1
                    step1EmailForm.setVisibility(View.VISIBLE);
                    step1EmailForm.setAlpha(0f);
                    step1EmailForm.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start();
                    
                    // Reset progress indicator
                    resetProgressIndicator();
                    
                    // Clear OTP fields
                    for (EditText digit : otpDigits) {
                        digit.setText("");
                    }
                    
                    // Animate spinner back to Step 1
                    if (stepSpinnerView != null && step1Circle != null) {
                        stepSpinnerView.attachToStep(step1Circle, true);
                    }
                })
                .start();
    }

    /**
     * Setup OTP input fields to auto-focus to next field on input
     * Prevents deletion and handles backspace to go to previous field
     */
    private void setupOtpInputs() {
        for (int i = 0; i < otpDigits.length; i++) {
            final int currentIndex = i;
            
            // Set up text watcher for auto-advance and delete prevention
            otpDigits[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // Auto-advance to next field when a digit is entered
                    if (s.length() == 1 && currentIndex < otpDigits.length - 1) {
                        otpDigits[currentIndex + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
            
            // Set up key listener for backspace handling
            otpDigits[i].setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == android.view.KeyEvent.KEYCODE_DEL && event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
                    // If current field is empty, go back to previous field
                    if (otpDigits[currentIndex].getText().toString().isEmpty() && currentIndex > 0) {
                        otpDigits[currentIndex - 1].requestFocus();
                        otpDigits[currentIndex - 1].setText(""); // Clear previous digit for replacement
                        return true;
                    }
                }
                return false;
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

        // Animate spinner to Step 2
        if (stepSpinnerView != null && step2Circle != null) {
            stepSpinnerView.attachToStep(step2Circle, true);
        }

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
     * Step 1 becomes: Dark brown circle + white checkmark
     * Step 2 becomes: Golden/primary filled circle + white "2"
     */
    private void updateProgressIndicator() {
        // Step 1: Change to dark brown with white checkmark
        if (step1Circle != null) {
            step1Circle.setBackground(getDrawable(R.drawable.step_completed_circle_bg));
            step1Circle.setBackgroundTintList(null);
        }
        
        if (step1Number != null) {
            step1Number.setText("✓");
            step1Number.setTextColor(getColor(R.color.white));
        }
        
        // Step 2: Change background to primary filled circle (same as step 1 but golden)
        if (step2Circle != null) {
            step2Circle.setBackground(getDrawable(R.drawable.step_completed_circle_bg));
            step2Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.primary))
            );
        }
        
        if (step2Number != null) {
            step2Number.setText("2");
            step2Number.setTextColor(getColor(R.color.white));
        }
        
        // Progress line: Change to golden
        if (progressLine != null) {
            progressLine.setBackgroundColor(getColor(R.color.primary));
        }
    }

    /**
     * Reset progress indicator (when going back to step 1)
     * Step 1: Dark brown with white "1"
     * Step 2: Light gray with muted "2"
     * Line: Light gray
     */
    private void resetProgressIndicator() {
        // Step 1: Keep dark brown, show "1"
        if (step1Circle != null) {
            step1Circle.setBackground(getDrawable(R.drawable.step_completed_circle_bg));
        }
        
        if (step1Number != null) {
            step1Number.setText("1");
            step1Number.setTextColor(getColor(R.color.white));
        }
        
        // Step 2: Back to light gray with muted text
        if (step2Circle != null) {
            step2Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.button_toggle_inactive))
            );
        }
        
        if (step2Number != null) {
            step2Number.setTextColor(getColor(R.color.text_muted));
        }
        
        // Progress line: Back to light gray
        if (progressLine != null) {
            progressLine.setBackgroundColor(getColor(R.color.button_toggle_inactive));
        }
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

            // Update step 2 circle to green with checkmark
            updateStep2Completed();

            resetPasswordButton.setEnabled(true);
            Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
            
            // Close activity after short delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                finish();
            }, 1500);
        }, 1500);
    }

    /**
     * Update Step 2 circle to show completion when password reset is successful
     */
    private void updateStep2Completed() {
        if (step2Circle != null) {
            // Change background to green checkmark circle
            step2Circle.setBackground(getDrawable(R.drawable.step_completed_circle_bg));
            step2Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.green_stock))
            );
        }
        
        if (step2Number != null) {
            // Show green checkmark instead of "2"
            step2Number.setText("✓");
            step2Number.setTextColor(getColor(R.color.white));
        }
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
