package aeu.ratha.beckie_deal_app.ui.activities.auth;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.RotateAnimation;
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
 * ForgotPasswordActivity handles the forgot password flow with 3 steps:
 * Step 1: User enters email to receive reset code
 * Step 2: User verifies OTP code
 * Step 3: User sets new password
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    // UI Components - Header
    private ImageView backButton;

    // UI Components - Progress Indicators
    private FrameLayout step1Circle;
    private FrameLayout step2Circle;
    private FrameLayout step3Circle;
    private TextView step1Number;
    private TextView step2Number;
    private TextView step3Number;
    private View progressLine1;
    private View progressLine2;

    // UI Components - Step 1: Email Form
    private LinearLayout step1EmailForm;
    private EditText emailInput;
    private TextView emailErrorMessage;
    private AppCompatButton sendCodeButton;

    // UI Components - Step 2: OTP Verification Form
    private LinearLayout step2OtpForm;
    private TextView emailDisplayText;
    private EditText[] otpDigits = new EditText[6];
    private TextView otpErrorMessage;
    private AppCompatButton verifyCodeButton;
    private TextView resendOtpLink;
    private TextView resendTimer;
    private TextView changeEmailLink;

    // UI Components - Step 3: Password Reset Form
    private LinearLayout step3PasswordForm;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private TextView passwordErrorMessage;
    private AppCompatButton resetPasswordButton;

    // UI Components - Progress
    private ProgressBar progressBar;

    // Data
    private String currentEmail = "";
    private String currentOtp = "";
    private boolean isResendCooldown = false;
    private int currentStep = 1; // Track current step

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

        // Progress Indicators (3 steps with numbers)
        step1Circle = findViewById(R.id.step1Circle);
        step2Circle = findViewById(R.id.step2Circle);
        step3Circle = findViewById(R.id.step3Circle);
        step1Number = findViewById(R.id.step1Number);
        step2Number = findViewById(R.id.step2Number);
        step3Number = findViewById(R.id.step3Number);
        progressLine1 = findViewById(R.id.progressLine1);
        progressLine2 = findViewById(R.id.progressLine2);

        // Step 1 Form
        step1EmailForm = findViewById(R.id.step1EmailForm);
        emailInput = findViewById(R.id.emailInput);
        emailErrorMessage = findViewById(R.id.emailErrorMessage);
        sendCodeButton = findViewById(R.id.sendCodeButton);

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
        verifyCodeButton = findViewById(R.id.verifyCodeButton);
        resendOtpLink = findViewById(R.id.resendOtpLink);
        resendTimer = findViewById(R.id.resendTimer);

        // Step 3 Form
        step3PasswordForm = findViewById(R.id.step3PasswordForm);
        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        passwordErrorMessage = findViewById(R.id.passwordErrorMessage);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        // Progress
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Setup click listeners and text watchers
     */
    private void setupListeners() {
        // Back button - go back to previous step or close activity
        backButton.setOnClickListener(v -> onBackPressed());

        // Step 1 Listeners
        sendCodeButton.setOnClickListener(v -> sendResetCode());

        // Step 2 Listeners
        verifyCodeButton.setOnClickListener(v -> verifyOtp());
        resendOtpLink.setOnClickListener(v -> resendOtp());
        changeEmailLink.setOnClickListener(v -> goBackToStep1());

        // Step 3 Listeners
        resetPasswordButton.setOnClickListener(v -> resetPassword());

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
                    
                    // Update progress indicators (step 1 is active)
                    updateProgressIndicators(1);
                    
                    // Clear OTP fields
                    for (EditText digit : otpDigits) {
                        digit.setText("");
                    }
                    
                    currentStep = 1;
                })
                .start();
    }

    /**
     * Navigate to next step with animation
     */
    private void navigateToStep(int nextStep) {
        final LinearLayout currentForm;
        final LinearLayout nextForm;
        
        switch (currentStep) {
            case 1:
                currentForm = step1EmailForm;
                break;
            case 2:
                currentForm = step2OtpForm;
                break;
            default:
                currentForm = null;
        }
        
        switch (nextStep) {
            case 2:
                nextForm = step2OtpForm;
                break;
            case 3:
                nextForm = step3PasswordForm;
                break;
            default:
                nextForm = null;
        }
        
        if (currentForm != null && nextForm != null) {
            // Fade out current form
            currentForm.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        currentForm.setVisibility(View.GONE);
                        
                        // Show next form
                        nextForm.setVisibility(View.VISIBLE);
                        nextForm.setAlpha(0f);
                        nextForm.animate()
                                .alpha(1f)
                                .setDuration(300)
                                .start();
                        
                        // Update progress indicators
                        updateProgressIndicators(nextStep);
                        
                        currentStep = nextStep;
                        
                        // Auto-focus first input of next step
                        if (nextStep == 2 && otpDigits[0] != null) {
                            otpDigits[0].requestFocus();
                            startResendTimer();
                        } else if (nextStep == 3 && newPasswordInput != null) {
                            newPasswordInput.requestFocus();
                        }
                    })
                    .start();
        }
    }

    /**
     * Update progress indicators based on current step
     * Highlights active step with dark brown color and animates the circle with spin effect
     */
    private void updateProgressIndicators(int activeStep) {
        // Step 1
        if (activeStep >= 1) {
            step1Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.dark_brown))
            );
            step1Number.setTextColor(getColor(R.color.white));
            if (activeStep == 1) {
                animateStepCircle(step1Circle);
            }
        } else {
            step1Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.button_toggle_inactive))
            );
            step1Number.setTextColor(getColor(R.color.dark_brown));
        }
        
        // Step 2
        if (activeStep >= 2) {
            step2Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.dark_brown))
            );
            step2Number.setTextColor(getColor(R.color.white));
            if (activeStep == 2) {
                animateStepCircle(step2Circle);
            }
        } else {
            step2Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.button_toggle_inactive))
            );
            step2Number.setTextColor(getColor(R.color.dark_brown));
        }
        
        // Step 3
        if (activeStep >= 3) {
            step3Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.dark_brown))
            );
            step3Number.setTextColor(getColor(R.color.white));
            if (activeStep == 3) {
                animateStepCircle(step3Circle);
            }
        } else {
            step3Circle.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(getColor(R.color.button_toggle_inactive))
            );
            step3Number.setTextColor(getColor(R.color.dark_brown));
        }
        
        // Update progress lines
        if (activeStep >= 2) {
            progressLine1.setBackgroundColor(getColor(R.color.dark_brown));
        } else {
            progressLine1.setBackgroundColor(getColor(R.color.button_toggle_inactive));
        }
        
        if (activeStep >= 3) {
            progressLine2.setBackgroundColor(getColor(R.color.dark_brown));
        } else {
            progressLine2.setBackgroundColor(getColor(R.color.button_toggle_inactive));
        }
    }

    /**
     * Animate the step circle with a spinning effect
     */
    private void animateStepCircle(FrameLayout circle) {
        RotateAnimation rotate = new RotateAnimation(
                0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(600);
        rotate.setRepeatCount(0);
        circle.startAnimation(rotate);
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
            sendCodeButton.setEnabled(true);

            // Move to Step 2
            emailDisplayText.setText(currentEmail);
            navigateToStep(2);

            Toast.makeText(this, "Reset code sent to " + email, Toast.LENGTH_SHORT).show();
        }, 1500);
    }

    /**
     * Step 2: Verify OTP code and move to Step 3
     */
    private void verifyOtp() {
        String otp = getOtpValue();

        // Validation
        clearErrors();

        if (otp.length() != 6) {
            showOtpError("Please enter the complete 6-digit code");
            return;
        }

        currentOtp = otp;

        // Show progress
        progressBar.setVisibility(View.VISIBLE);
        verifyCodeButton.setEnabled(false);

        // Simulate API call
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            verifyCodeButton.setEnabled(true);

            // Move to Step 3 (Password Reset)
            navigateToStep(3);

            Toast.makeText(this, "Code verified successfully!", Toast.LENGTH_SHORT).show();
        }, 1500);
    }

    /**
     * Step 3: Reset password
     */
    private void resetPassword() {
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validation
        clearErrors();

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

            // API Call would include:
            // - email: currentEmail
            // - otp: currentOtp
            // - newPassword: newPassword

            resetPasswordButton.setEnabled(true);
            Toast.makeText(this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
            
            // Close activity after short delay
            new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1500);
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
