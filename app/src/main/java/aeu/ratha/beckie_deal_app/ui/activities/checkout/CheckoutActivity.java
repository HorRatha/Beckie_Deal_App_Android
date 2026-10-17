package aeu.ratha.beckie_deal_app.ui.activities.checkout;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.components.AnimatedStepSpinnerView;
import aeu.ratha.beckie_deal_app.ui.fragments.checkout.CartReviewFragment;
import aeu.ratha.beckie_deal_app.ui.fragments.checkout.OrderReviewFragment;
import aeu.ratha.beckie_deal_app.ui.fragments.checkout.ShippingAddressFragment;

public class CheckoutActivity extends AppCompatActivity {
    private int currentStep = 0;
    private static final int CART_REVIEW_STEP = 0;
    private static final int SHIPPING_ADDRESS_STEP = 1;
    private static final int PAYMENT_STEP = 2;
    private static final int ORDER_REVIEW_STEP = 3;

    // Progress indicator views
    private FrameLayout step1Circle, step2Circle, step3Circle;
    private TextView step1Number, step2Number, step3Number;
    private View progressLine1, progressLine2;
    private AnimatedStepSpinnerView stepSpinnerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        
        initializeProgressViews();
        
        if (savedInstanceState == null) {
            currentStep = CART_REVIEW_STEP;
            loadFragment(new CartReviewFragment());
            updateProgressIndicator();
        } else {
            currentStep = savedInstanceState.getInt("currentStep", CART_REVIEW_STEP);
            updateProgressIndicator();
        }
    }

    /**
     * Initialize all progress indicator views from the header
     */
    private void initializeProgressViews() {
        // Get header views
        View headerView = findViewById(R.id.checkoutProgressHeader);
        
        step1Circle = headerView.findViewById(R.id.step1Circle);
        step2Circle = headerView.findViewById(R.id.step2Circle);
        step3Circle = headerView.findViewById(R.id.step3Circle);
        
        step1Number = headerView.findViewById(R.id.step1Number);
        step2Number = headerView.findViewById(R.id.step2Number);
        step3Number = headerView.findViewById(R.id.step3Number);
        
        progressLine1 = headerView.findViewById(R.id.progressLine1);
        progressLine2 = headerView.findViewById(R.id.progressLine2);
        
        stepSpinnerView = headerView.findViewById(R.id.stepSpinnerView);
        
        // Attach spinner to Step 1 initially
        if (step1Circle != null && stepSpinnerView != null) {
            step1Circle.post(() -> stepSpinnerView.attachToStep(step1Circle, false));
        }
    }

    /**
     * Update progress indicator based on current step
     */
    private void updateProgressIndicator() {
        switch (currentStep) {
            case CART_REVIEW_STEP:
                updateToStep(1);
                break;
            case SHIPPING_ADDRESS_STEP:
                updateToStep(2);
                break;
            case PAYMENT_STEP:
            case ORDER_REVIEW_STEP:
                updateToStep(3);
                break;
        }
    }

    /**
     * Update progress indicator to highlight the current step
     * @param stepNumber 1, 2, or 3
     */
    private void updateToStep(int stepNumber) {
        // Reset all circles to inactive state
        resetAllSteps();
        
        if (stepNumber >= 1) {
            // Step 1: Active (filled dark circle with checkmark or "1")
            if (step1Circle != null) {
                step1Circle.setBackground(getDrawable(R.drawable.step_completed_circle_bg));
                step1Circle.setBackgroundTintList(null);
            }
            if (step1Number != null) {
                step1Number.setText("✓");
                step1Number.setTextColor(getColor(R.color.white));
            }
        }
        
        if (stepNumber >= 2) {
            // Step 2: Active (filled golden circle)
            if (step2Circle != null) {
                step2Circle.setBackground(getDrawable(R.drawable.step_completed_circle_bg));
                step2Circle.setBackgroundTintList(
                        ColorStateList.valueOf(getColor(R.color.primary))
                );
            }
            if (step2Number != null) {
                step2Number.setText("2");
                step2Number.setTextColor(getColor(R.color.white));
            }
            
            // Progress Line 1: Active (golden)
            if (progressLine1 != null) {
                progressLine1.setBackgroundColor(getColor(R.color.primary));
            }
            
            // Animate spinner to Step 2
            if (stepSpinnerView != null && step2Circle != null) {
                stepSpinnerView.attachToStep(step2Circle, true);
            }
        }
        
        if (stepNumber >= 3) {
            // Step 3: Active (filled golden circle)
            if (step3Circle != null) {
                step3Circle.setBackground(getDrawable(R.drawable.step_completed_circle_bg));
                step3Circle.setBackgroundTintList(
                        ColorStateList.valueOf(getColor(R.color.primary))
                );
            }
            if (step3Number != null) {
                step3Number.setText("3");
                step3Number.setTextColor(getColor(R.color.white));
            }
            
            // Progress Line 2: Active (golden)
            if (progressLine2 != null) {
                progressLine2.setBackgroundColor(getColor(R.color.primary));
            }
            
            // Animate spinner to Step 3
            if (stepSpinnerView != null && step3Circle != null) {
                stepSpinnerView.attachToStep(step3Circle, true);
            }
        }
    }

    /**
     * Reset all step circles to inactive state
     */
    private void resetAllSteps() {
        // Reset Step 1
        if (step1Circle != null) {
            step1Circle.setBackground(getDrawable(R.drawable.step_completed_circle_bg));
            step1Circle.setBackgroundTintList(null);
        }
        if (step1Number != null) {
            step1Number.setText("1");
            step1Number.setTextColor(getColor(R.color.white));
        }
        
        // Reset Step 2
        if (step2Circle != null) {
            step2Circle.setBackground(getDrawable(R.drawable.login_logo_circle_bg));
            step2Circle.setBackgroundTintList(
                    ColorStateList.valueOf(getColor(R.color.button_toggle_inactive))
            );
        }
        if (step2Number != null) {
            step2Number.setText("2");
            step2Number.setTextColor(getColor(R.color.text_muted));
        }
        
        // Reset Step 3
        if (step3Circle != null) {
            step3Circle.setBackground(getDrawable(R.drawable.login_logo_circle_bg));
            step3Circle.setBackgroundTintList(
                    ColorStateList.valueOf(getColor(R.color.button_toggle_inactive))
            );
        }
        if (step3Number != null) {
            step3Number.setText("3");
            step3Number.setTextColor(getColor(R.color.text_muted));
        }
        
        // Reset progress lines
        if (progressLine1 != null) {
            progressLine1.setBackgroundColor(getColor(R.color.button_toggle_inactive));
        }
        if (progressLine2 != null) {
            progressLine2.setBackgroundColor(getColor(R.color.button_toggle_inactive));
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.checkoutContainer, fragment);
        fragmentTransaction.commit();
    }

    public void nextStep() {
        currentStep++;
        updateProgressIndicator();
        switch (currentStep) {
            case SHIPPING_ADDRESS_STEP:
                loadFragment(new ShippingAddressFragment());
                break;
            case PAYMENT_STEP:
                loadFragment(new OrderReviewFragment());
                break;
            case ORDER_REVIEW_STEP:
                // OrderReviewFragment already loaded in PAYMENT_STEP
                break;
        }
    }

    public void previousStep() {
        if (currentStep > CART_REVIEW_STEP) {
            currentStep--;
            updateProgressIndicator();
            switch (currentStep) {
                case CART_REVIEW_STEP:
                    loadFragment(new CartReviewFragment());
                    break;
                case SHIPPING_ADDRESS_STEP:
                    loadFragment(new ShippingAddressFragment());
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentStep", currentStep);
    }
}

