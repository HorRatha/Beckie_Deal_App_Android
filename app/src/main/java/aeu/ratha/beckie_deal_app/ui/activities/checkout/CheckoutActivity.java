package aeu.ratha.beckie_deal_app.ui.activities.checkout;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.fragments.checkout.CartReviewFragment;
import aeu.ratha.beckie_deal_app.ui.fragments.checkout.ShippingAddressFragment;

public class CheckoutActivity extends AppCompatActivity {
    private int currentStep = 0;
    private static final int CART_REVIEW_STEP = 0;
    private static final int SHIPPING_ADDRESS_STEP = 1;
    private static final int PAYMENT_STEP = 2;
    private static final int ORDER_REVIEW_STEP = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        
        if (savedInstanceState == null) {
            currentStep = CART_REVIEW_STEP;
            loadFragment(new CartReviewFragment());
        } else {
            currentStep = savedInstanceState.getInt("currentStep", CART_REVIEW_STEP);
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
        switch (currentStep) {
            case SHIPPING_ADDRESS_STEP:
                loadFragment(new ShippingAddressFragment());
                break;
            case PAYMENT_STEP:
                // TODO: Load PaymentFragment when created
                break;
            case ORDER_REVIEW_STEP:
                // TODO: Load OrderReviewFragment when created
                break;
        }
    }

    public void previousStep() {
        if (currentStep > CART_REVIEW_STEP) {
            currentStep--;
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
