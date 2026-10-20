package aeu.ratha.beckie_deal_app.ui.fragments.checkout;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.api.PaymentApiService;
import aeu.ratha.beckie_deal_app.data.models.PaymentOption;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import aeu.ratha.beckie_deal_app.ui.activities.checkout.CheckoutActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * PaymentMethodFragment
 * Step 2 of 3 in the checkout process
 * Displays available payment methods and allows user to select one
 */
public class PaymentMethodFragment extends Fragment {
    private static final String TAG = "PaymentMethodFragment";

    private MaterialButton backBtn;
    private MaterialButton continueBtn;
    private LinearLayout paymentMethodsContainer;
    private View loadingView;

    private PaymentApiService paymentApiService;
    private String selectedPaymentMethod = null;
    private PaymentOption currentPaymentOptions;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        backBtn = view.findViewById(R.id.backBtn);
        continueBtn = view.findViewById(R.id.continueBtn);
        paymentMethodsContainer = view.findViewById(R.id.paymentMethodsContainer);

        // Set up button listeners
        backBtn.setOnClickListener(v -> onBackPressed());
        continueBtn.setOnClickListener(v -> onContinuePressed());

        // Initialize Retrofit
        initializeRetrofit();

        // Load payment options
        loadPaymentOptions();
    }

    /**
     * Initialize Payment API Service using RetrofitClient
     */
    private void initializeRetrofit() {
        if (getContext() != null) {
            paymentApiService = RetrofitClient.getClient(getContext())
                    .create(PaymentApiService.class);
        }
    }

    /**
     * Load payment options from API
     */
    private void loadPaymentOptions() {
        paymentApiService.getPaymentOptions("mobile").enqueue(new Callback<PaymentOption>() {
            @Override
            public void onResponse(@NonNull Call<PaymentOption> call, @NonNull Response<PaymentOption> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentOption paymentOption = response.body();
                    if ("success".equals(paymentOption.status)) {
                        currentPaymentOptions = paymentOption;
                        displayPaymentMethods(paymentOption.data.methods);
                    } else {
                        showError("Failed to load payment options");
                    }
                } else {
                    showError("Failed to load payment options");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentOption> call, @NonNull Throwable t) {
                Log.e(TAG, "Error loading payment options", t);
                showError("Error loading payment options: " + t.getMessage());
            }
        });
    }

    /**
     * Display payment methods in the UI
     * 
     * @param methods List of available payment methods
     */
    private void displayPaymentMethods(List<PaymentOption.PaymentMethod> methods) {
        paymentMethodsContainer.removeAllViews();

        if (methods == null || methods.isEmpty()) {
            Toast.makeText(getContext(), "No payment methods available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Select the first enabled method by default
        for (PaymentOption.PaymentMethod method : methods) {
            if (method.enabled && selectedPaymentMethod == null) {
                selectedPaymentMethod = method.key;
            }
        }

        // Add payment method cards (both enabled and disabled)
        for (PaymentOption.PaymentMethod method : methods) {
            View paymentCardView = createPaymentMethodCard(method);
            paymentMethodsContainer.addView(paymentCardView);
        }
    }

    /**
     * Create a payment method card view
     * 
     * @param method The payment method to display
     * @return The created view
     */
    private View createPaymentMethodCard(PaymentOption.PaymentMethod method) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cardView = inflater.inflate(R.layout.item_payment_method, paymentMethodsContainer, false);

        RadioButton radioButton = cardView.findViewById(R.id.paymentRadio);
        LinearLayout cardContainer = cardView.findViewById(R.id.paymentCardContainer);

        // Store method key in radio button tag for later retrieval
        radioButton.setTag(method.key);

        // Set payment method name and description
        ((android.widget.TextView) cardView.findViewById(R.id.paymentMethodName)).setText(method.getDisplayName());
        ((android.widget.TextView) cardView.findViewById(R.id.paymentMethodDescription)).setText(method.getDescription());

        // Set icon based on payment method
        int iconResId = getPaymentMethodIcon(method.key);
        if (iconResId != 0) {
            cardView.findViewById(R.id.paymentMethodIcon).setBackground(
                    getContext().getDrawable(iconResId)
            );
        }

        // Handle disabled state
        if (!method.enabled) {
            // Disable interaction
            radioButton.setEnabled(false);
            cardContainer.setEnabled(false);
            cardContainer.setAlpha(0.5f);  // Gray out disabled methods
            
            // Show disabled badge
            android.widget.TextView descView = (android.widget.TextView) cardView.findViewById(R.id.paymentMethodDescription);
            descView.setText(descView.getText() + " (Currently unavailable)");
            
            // Don't allow selection
            radioButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), 
                    method.getDisplayName() + " is currently unavailable", 
                    Toast.LENGTH_SHORT).show();
            });
            cardContainer.setOnClickListener(v -> {
                Toast.makeText(getContext(), 
                    method.getDisplayName() + " is currently unavailable", 
                    Toast.LENGTH_SHORT).show();
            });
            
            return cardView;
        }

        // Set radio button state for enabled methods
        boolean isSelected = method.key.equals(selectedPaymentMethod);
        radioButton.setChecked(isSelected);

        // Apply selection styling if selected
        if (isSelected) {
            cardContainer.setBackground(getContext().getDrawable(R.drawable.payment_method_selected_bg));
        }

        // Handle card click
        cardContainer.setOnClickListener(v -> {
            selectedPaymentMethod = method.key;
            updatePaymentMethodSelection();
        });

        // Handle radio button click
        radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPaymentMethod = method.key;
                updatePaymentMethodSelection();
            }
        });

        return cardView;
    }

    /**
     * Update payment method selection UI
     */
    private void updatePaymentMethodSelection() {
        for (int i = 0; i < paymentMethodsContainer.getChildCount(); i++) {
            View child = paymentMethodsContainer.getChildAt(i);
            LinearLayout container = child.findViewById(R.id.paymentCardContainer);
            RadioButton radio = child.findViewById(R.id.paymentRadio);

            // Skip if disabled
            if (!radio.isEnabled()) {
                continue;
            }

            // Get the method key from the card (stored in radio button tag)
            String methodKey = (String) radio.getTag();

            if (methodKey != null && methodKey.equals(selectedPaymentMethod)) {
                // Apply selected styling
                container.setBackground(getContext().getDrawable(R.drawable.payment_method_selected_bg));
                radio.setChecked(true);
            } else {
                // Apply unselected styling
                container.setBackground(getContext().getDrawable(R.drawable.checkout_step_card_bg));
                radio.setChecked(false);
            }
        }
    }

    /**
     * Get the icon resource for a payment method
     * 
     * @param methodKey The payment method key
     * @return The icon resource ID
     */
    private int getPaymentMethodIcon(String methodKey) {
        switch (methodKey) {
            case "aba_payway":
                return R.drawable.ic_credit_card;
            case "cod":
                return R.drawable.ic_local_offer;
            case "khqr":
                return R.drawable.ic_qr_code;
            default:
                return 0;
        }
    }

    /**
     * Handle back button - return to previous step (shipping address)
     */
    private void onBackPressed() {
        if (getActivity() instanceof CheckoutActivity) {
            ((CheckoutActivity) getActivity()).previousStep();
        }
    }

    /**
     * Handle continue button - proceed to order review
     */
    private void onContinuePressed() {
        if (selectedPaymentMethod == null) {
            Toast.makeText(getContext(), "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Selected payment method: " + selectedPaymentMethod);

        // TODO: Save selected payment method to checkout state
        // saveSelectedPaymentMethod(selectedPaymentMethod);

        // Proceed to next step
        if (getActivity() instanceof CheckoutActivity) {
            ((CheckoutActivity) getActivity()).nextStep();
        }
    }

    /**
     * Show error message
     * 
     * @param message Error message to display
     */
    private void showError(String message) {
        Log.e(TAG, message);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
