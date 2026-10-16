package aeu.ratha.beckie_deal_app.ui.fragments.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.activities.checkout.CheckoutActivity;

/**
 * OrderReviewFragment
 * Step 3 of 3 in the checkout process
 * Displays a complete review of:
 * - Order Items
 * - Shipping Address
 * - Payment Method
 * - Order Summary (Subtotal, Shipping, Total)
 *
 * User can edit any of these sections or place the order
 */
public class OrderReviewFragment extends Fragment {

    private MaterialButton backBtn;
    private MaterialButton placeOrderBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backBtn = view.findViewById(R.id.backBtn);
        placeOrderBtn = view.findViewById(R.id.placeOrderBtn);

        // Set up button listeners
        backBtn.setOnClickListener(v -> onBackPressed());
        placeOrderBtn.setOnClickListener(v -> onPlaceOrderPressed());

        // TODO: Load actual order data from cart/checkout state
        // setupOrderItems();
        // setupShippingAddress();
        // setupPaymentMethod();
        // updateOrderSummary();
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
     * Handle place order button - process the order
     */
    private void onPlaceOrderPressed() {
        // TODO: Implement order placement
        // 1. Validate all data
        // 2. Make API call to create order
        // 3. Handle success/error responses
        // 4. Navigate to order confirmation screen

        Toast.makeText(getContext(), "Place Order pressed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Load order items from cart data
     * This should be populated from the CartReviewFragment's cart state
     */
    private void setupOrderItems() {
        // TODO: Get cart items from shared state or arguments
        // Set up RecyclerView with order items
    }

    /**
     * Load shipping address from ShippingAddressFragment data
     */
    private void setupShippingAddress() {
        // TODO: Get shipping address from shared state or arguments
        // Display in the shipping section
    }

    /**
     * Display the selected payment method
     */
    private void setupPaymentMethod() {
        // TODO: Get payment method from state
        // Display payment information
    }

    /**
     * Calculate and display order totals
     */
    private void updateOrderSummary() {
        // TODO: Calculate subtotal, shipping, and total
        // Update the display
    }

}
