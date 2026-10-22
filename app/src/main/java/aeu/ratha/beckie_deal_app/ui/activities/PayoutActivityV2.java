package aeu.ratha.beckie_deal_app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.AuthHelper;

/**
 * Enhanced Payout Activity - 3-Step Checkout
 * Step 1: Shipping Address
 * Step 2: Shipping & Payment Method
 * Step 3: Order Review
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
    private MaterialCardView cardAbaPayWay, cardCashOnDelivery;
    private Button btnStep2Back, btnStep2Next;
    
    // Step 3
    private TextView textOrderSummary, textShippingAddress, textTotalAmount;
    private Button btnStep3Back, btnPlaceOrder;
    
    // Data
    private String selectedPaymentMethod = "aba_payway";
    private String selectedShippingMethod = "standard";
    private int shippingMethodId = 0;  // ID for API submission (set when COD is selected)
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
    }
    
    /**
     * Initialize all UI components
     */
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
    
    /**
     * Set up click listeners
     */
    private void setupClickListeners() {
        btnStep1Next.setOnClickListener(v -> validateAndProceedStep1());
        btnStep2Back.setOnClickListener(v -> viewFlipper.setDisplayedChild(0));
        btnStep2Next.setOnClickListener(v -> validateAndProceedStep2());
        btnStep3Back.setOnClickListener(v -> viewFlipper.setDisplayedChild(1));
        btnPlaceOrder.setOnClickListener(v -> processCheckout());
        
        cardAbaPayWay.setOnClickListener(v -> selectPaymentMethod("aba_payway", cardAbaPayWay, cardCashOnDelivery));
        cardCashOnDelivery.setOnClickListener(v -> selectPaymentMethod("cod", cardCashOnDelivery, cardAbaPayWay));
    }
    
    /**
     * Validate and proceed from Step 1
     */
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
    
    /**
     * Validate and proceed from Step 2
     */
    private void validateAndProceedStep2() {
        int selectedShippingId = radioGroupShipping.getCheckedRadioButtonId();
        if (selectedShippingId == -1) {
            Toast.makeText(this, "Please select a shipping method", Toast.LENGTH_SHORT).show();
            return;
        }
        
        RadioButton selectedRadio = findViewById(selectedShippingId);
        selectedShippingMethod = selectedRadio.getTag().toString();
        updateOrderSummary();
        viewFlipper.setDisplayedChild(2);
    }
    
    /**
     * Select payment method
     */
    private void selectPaymentMethod(String method, MaterialCardView selectedCard, MaterialCardView unselectedCard) {
        selectedPaymentMethod = method;
        selectedCard.setStrokeWidth(2);
        unselectedCard.setStrokeWidth(1);
        
        // Auto-set shipping_method_id to 1 when COD is selected
        if ("cod".equals(method)) {
            shippingMethodId = 1;
        }
        
        Toast.makeText(this, "Payment: " + method, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * Update order summary
     */
    private void updateOrderSummary() {
        textOrderSummary.setText("Order Summary:\nSubtotal: $0.00\nTotal: $0.00");
        
        StringBuilder address = new StringBuilder();
        address.append(shippingName).append("\n");
        address.append(shippingPhone).append("\n");
        address.append(shippingAddress).append("\n");
        address.append(shippingCity).append(", ").append(shippingCountry);
        textShippingAddress.setText(address.toString());
        
        textTotalAmount.setText("Total: $0.00");
    }
    
    /**
     * Process checkout
     */
    private void processCheckout() {
        showProgress(true);
        btnPlaceOrder.setEnabled(false);
        Toast.makeText(this, "Processing order...", Toast.LENGTH_SHORT).show();
        
        handleOrderSuccess(123, "Order placed successfully!");
    }
    
    /**
     * Handle successful order
     */
    private void handleOrderSuccess(long orderId, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        intent.putExtra("order_id", orderId);
        startActivity(intent);
        finish();
    }
    
    /**
     * Validate shipping address
     */
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
    
    /**
     * Show/hide loading
     */
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
