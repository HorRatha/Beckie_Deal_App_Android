package aeu.ratha.beckie_deal_app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.activities.payment.KhqrPaymentActivity;
import aeu.ratha.beckie_deal_app.ui.activities.payment.PaymentWebViewActivity;
import aeu.ratha.beckie_deal_app.utils.AuthHelper;

/**
 * Enhanced Payout Activity - 3-Step Checkout with ABA PayWay (Debit Card & KHQR) and COD support.
 * Step 1: Shipping Address
 * Step 2: Shipping & Payment Method (ABA PayWay, KHQR, COD)
 * Step 3: Order Review & Placement
 */
public class PayoutActivityV2 extends AppCompatActivity {
    
    private static final String TAG = "PayoutActivityV2";
    private static final int REQUEST_ABA_PAYMENT = 1002;
    
    // UI Components
    private ViewFlipper viewFlipper;
    private ProgressBar progressBar;
    
    // Step 1
    private EditText editName, editPhone, editAddress, editCity, editPostalCode, editCountry;
    private Button btnStep1Next;
    
    // Step 2
    private RadioGroup radioGroupShipping;
    private MaterialCardView cardAbaPayWay, cardKhqr, cardCashOnDelivery;
    private Button btnStep2Back, btnStep2Next;
    
    // Step 3
    private TextView textOrderSummary, textShippingAddress, textTotalAmount;
    private Button btnStep3Back, btnPlaceOrder;
    
    // Data
    private String selectedPaymentMethod = "khqr";
    private String selectedShippingMethod = "standard";
    private int shippingMethodId = 1;
    private String shippingName, shippingPhone, shippingAddress, shippingCity, shippingPostalCode, shippingCountry;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout_v2);
        
        if (!AuthHelper.isUserLoggedIn(this)) {
            Toast.makeText(this, "Please login to proceed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initializeViews();
        setupClickListeners();
        
        // Default select KHQR as per official design
        selectPaymentMethod("khqr", cardKhqr);
    }
    
    private void initializeViews() {
        viewFlipper = findViewById(R.id.flipper_checkout);
        progressBar = findViewById(R.id.progress_loading);
        
        // Step 1
        editName = findViewById(R.id.edit_name);
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        editCity = findViewById(R.id.edit_city);
        editPostalCode = findViewById(R.id.edit_postal_code);
        editCountry = findViewById(R.id.edit_country);
        btnStep1Next = findViewById(R.id.btn_step1_next);
        
        // Step 2
        radioGroupShipping = findViewById(R.id.radio_shipping_methods);
        cardAbaPayWay = findViewById(R.id.card_aba_payway);
        cardKhqr = findViewById(R.id.card_khqr);
        cardCashOnDelivery = findViewById(R.id.card_cod);
        btnStep2Back = findViewById(R.id.btn_step2_back);
        btnStep2Next = findViewById(R.id.btn_step2_next);
        
        // Step 3
        textOrderSummary = findViewById(R.id.text_order_summary);
        textShippingAddress = findViewById(R.id.text_shipping_address);
        textTotalAmount = findViewById(R.id.text_total_amount);
        btnStep3Back = findViewById(R.id.btn_step3_back);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
    }
    
    private void setupClickListeners() {
        btnStep1Next.setOnClickListener(v -> validateAndProceedStep1());
        btnStep2Back.setOnClickListener(v -> viewFlipper.setDisplayedChild(0));
        btnStep2Next.setOnClickListener(v -> validateAndProceedStep2());
        btnStep3Back.setOnClickListener(v -> viewFlipper.setDisplayedChild(1));
        btnPlaceOrder.setOnClickListener(v -> processCheckout());
        
        cardAbaPayWay.setOnClickListener(v -> selectPaymentMethod("aba_payway", cardAbaPayWay));
        cardKhqr.setOnClickListener(v -> selectPaymentMethod("khqr", cardKhqr));
        cardCashOnDelivery.setOnClickListener(v -> selectPaymentMethod("cod", cardCashOnDelivery));
    }
    
    private void validateAndProceedStep1() {
        if (!validateShippingAddress()) return;
        
        shippingName = editName.getText().toString().trim();
        shippingPhone = editPhone.getText().toString().trim();
        shippingAddress = editAddress.getText().toString().trim();
        shippingCity = editCity.getText().toString().trim();
        shippingPostalCode = editPostalCode.getText().toString().trim();
        shippingCountry = editCountry.getText().toString().trim();
        
        viewFlipper.setDisplayedChild(1);
    }
    
    private void validateAndProceedStep2() {
        int selectedShippingId = radioGroupShipping.getCheckedRadioButtonId();
        if (selectedShippingId != -1) {
            RadioButton selectedRadio = findViewById(selectedShippingId);
            if (selectedRadio != null && selectedRadio.getTag() != null) {
                selectedShippingMethod = selectedRadio.getTag().toString();
            }
        }
        updateOrderSummary();
        viewFlipper.setDisplayedChild(2);
    }
    
    private void selectPaymentMethod(String method, MaterialCardView selectedCard) {
        selectedPaymentMethod = method;
        
        // Reset all card strokes
        cardAbaPayWay.setStrokeWidth(1);
        cardKhqr.setStrokeWidth(1);
        cardCashOnDelivery.setStrokeWidth(1);
        
        // Highlight selected
        selectedCard.setStrokeWidth(2);
        
        String displayName = "ABA PayWay (Card)";
        if ("khqr".equals(method)) {
            displayName = "ABA KHQR";
        } else if ("cod".equals(method)) {
            displayName = "Cash on Delivery";
        }
        Toast.makeText(this, "Selected: " + displayName, Toast.LENGTH_SHORT).show();
    }
    
    private void updateOrderSummary() {
        textOrderSummary.setText("Order Summary:\nSubtotal: $75.00\nShipping: $2.00\nPayment Method: " + selectedPaymentMethod.toUpperCase());
        
        StringBuilder address = new StringBuilder();
        address.append(shippingName).append("\n");
        address.append(shippingPhone).append("\n");
        address.append(shippingAddress).append("\n");
        address.append(shippingCity).append(", ").append(shippingCountry);
        textShippingAddress.setText(address.toString());
        
        textTotalAmount.setText("Total: $77.00");
    }
    
    private void processCheckout() {
        showProgress(true);
        btnPlaceOrder.setEnabled(false);
        Toast.makeText(this, "Creating order (#127)...", Toast.LENGTH_SHORT).show();
        
        // Order #127, Total $77.00
        long orderId = 127L;
        double totalAmount = 77.00;
        
        if ("khqr".equals(selectedPaymentMethod)) {
            showProgress(false);
            Intent intent = new Intent(this, KhqrPaymentActivity.class);
            intent.putExtra(KhqrPaymentActivity.EXTRA_ORDER_ID, orderId);
            intent.putExtra(KhqrPaymentActivity.EXTRA_AMOUNT, totalAmount);
            startActivity(intent);
            finish();
        } else if ("aba_payway".equals(selectedPaymentMethod)) {
            showProgress(false);
            // Launch WebView for ABA PayWay debit card payment
            Intent intent = new Intent(this, PaymentWebViewActivity.class);
            intent.putExtra(PaymentWebViewActivity.EXTRA_PAYMENT_URL, "https://pay.ababank.com/payway/checkout?id=test_txn_127&amount=" + totalAmount);
            startActivityForResult(intent, REQUEST_ABA_PAYMENT);
        } else {
            // Cash on Delivery
            showProgress(false);
            handleOrderSuccess(orderId, "Order placed successfully with Cash on Delivery!");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ABA_PAYMENT) {
            if (resultCode == RESULT_OK) {
                handleOrderSuccess(127L, "Debit Card Payment Approved! Order confirmed.");
            } else {
                Toast.makeText(this, "Payment cancelled or failed", Toast.LENGTH_SHORT).show();
                btnPlaceOrder.setEnabled(true);
            }
        }
    }
    
    private void handleOrderSuccess(long orderId, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra("order_id", orderId);
        startActivity(intent);
        finish();
    }
    
    private boolean validateShippingAddress() {
        if (editName.getText().toString().isEmpty()) {
            editName.setError("Name required");
            return false;
        }
        if (editPhone.getText().toString().isEmpty()) {
            editPhone.setError("Phone required");
            return false;
        }
        if (editAddress.getText().toString().isEmpty()) {
            editAddress.setError("Address required");
            return false;
        }
        if (editCity.getText().toString().isEmpty()) {
            editCity.setError("City required");
            return false;
        }
        if (editPostalCode.getText().toString().isEmpty()) {
            editPostalCode.setError("Postal code required");
            return false;
        }
        if (editCountry.getText().toString().isEmpty()) {
            editCountry.setError("Country required");
            return false;
        }
        return true;
    }
    
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    @Override
    public void onBackPressed() {
        int currentChild = viewFlipper.getDisplayedChild();
        if (currentChild > 0) {
            viewFlipper.setDisplayedChild(currentChild - 1);
        } else {
            super.onBackPressed();
        }
    }
}
