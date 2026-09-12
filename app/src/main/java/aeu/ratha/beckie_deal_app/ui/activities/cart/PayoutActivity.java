package aeu.ratha.beckie_deal_app.ui.activities.cart;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Cart;
import aeu.ratha.beckie_deal_app.data.models.CartResponse;
import aeu.ratha.beckie_deal_app.data.repository.CartRepository;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.managers.CouponManager;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PayoutActivity extends BaseActivity {

    private static final String TAG = "PayoutActivity";
    
    private CartRepository cartRepository;
    private Cart currentCart;
    
    private TextView summarySubtotalText;
    private TextView summaryDiscountText;
    private TextView summaryTotalText;
    private LinearLayout discountRow;
    private EditText couponInput;
    private MaterialCardView appliedCouponCard;
    private TextView appliedCouponCode;
    private TextView appliedCouponDiscount;
    
    // Shipping info fields
    private EditText shippingNameInput;
    private EditText shippingPhoneInput;
    private EditText shippingAddressInput;
    private EditText shippingCityInput;
    private EditText shippingPostalCodeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payout);

        // Check authentication - checkout requires login
        if (!aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.isUserLoggedIn(this)) {
            aeu.ratha.beckie_deal_app.utils.helpers.AuthHelper.showCustomLoginPrompt(
                this, 
                "Login Required", 
                "Please login to complete your purchase."
            );
            finish();
            return;
        }

        cartRepository = new CartRepository(this);

        setupTopBar();
        setupShippingFields();
        setupCouponSection();
        setupOrderSummary();
        setupConfirmButton();
        setupEntranceAnimation();
        
        // Load cart from API
        loadCartFromAPI();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
        UiAnimations.pressBounce(backButton);
    }

    /** Staggers the shipping/payment/summary cards into place, and fades in the header. */
    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.appBarLayout), 0);
        androidx.core.widget.NestedScrollView scroll = findViewById(R.id.payoutContentScroll);
        if (scroll != null && scroll.getChildCount() > 0) {
            View listRoot = scroll.getChildAt(0);
            if (listRoot instanceof android.view.ViewGroup) {
                UiAnimations.staggerChildrenIn((android.view.ViewGroup) listRoot, 80L);
            }
        }
        UiAnimations.fadeSlideUp(findViewById(R.id.bottomActionLayout), 200);
    }
    
    /**
     * Setup shipping information fields.
     */
    private void setupShippingFields() {
        // TODO: Add these fields to activity_payout.xml
        // shippingNameInput = findViewById(R.id.shippingNameInput);
        // shippingPhoneInput = findViewById(R.id.shippingPhoneInput);
        // shippingAddressInput = findViewById(R.id.shippingAddressInput);
        // shippingCityInput = findViewById(R.id.shippingCityInput);
        // shippingPostalCodeInput = findViewById(R.id.shippingPostalCodeInput);
        
        // Pre-fill with user data if available
        // TODO: Load from user profile
        
        // Note: These fields might not exist in the layout yet
        // They will be added when the UI is updated
    }

    /**
     * Load cart from API to get current items and prices.
     */
    private void loadCartFromAPI() {
        cartRepository.getCart(new CartRepository.CartCallback() {
            @Override
            public void onSuccess(CartResponse response) {
                if (response.isSuccess() && response.hasData()) {
                    currentCart = response.getData();
                    runOnUiThread(() -> {
                        updateOrderSummary();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(PayoutActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Failed to load cart: " + error);
                    Toast.makeText(PayoutActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void setupCouponSection() {
        couponInput = findViewById(R.id.couponInput);
        Button applyCouponButton = findViewById(R.id.applyCouponButton);
        appliedCouponCard = findViewById(R.id.appliedCouponCard);
        appliedCouponCode = findViewById(R.id.appliedCouponCode);
        appliedCouponDiscount = findViewById(R.id.appliedCouponDiscount);
        ImageView removeCouponButton = findViewById(R.id.removeCouponButton);

        UiAnimations.pressBounce(applyCouponButton, removeCouponButton);

        if (applyCouponButton != null) {
            applyCouponButton.setOnClickListener(v -> {
                String code = couponInput.getText().toString();
                double subtotal = currentCart != null ? currentCart.getTotalPrice() : 0.0;
                
                CouponManager.CouponResult result = 
                    CouponManager.getInstance().applyCoupon(code, subtotal);
                
                if (result.success) {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
                    updateCouponUI();
                    updateOrderSummary();
                } else {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (removeCouponButton != null) {
            removeCouponButton.setOnClickListener(v -> {
                CouponManager.getInstance().removeCoupon();
                Toast.makeText(this, "Coupon removed", Toast.LENGTH_SHORT).show();
                updateCouponUI();
                updateOrderSummary();
            });
        }

        updateCouponUI();
    }

    private void updateCouponUI() {
        boolean hasCoupon = CouponManager.getInstance().hasCoupon();
        
        if (couponInput != null) couponInput.setVisibility(hasCoupon ? View.GONE : View.VISIBLE);
        if (appliedCouponCard != null) appliedCouponCard.setVisibility(hasCoupon ? View.VISIBLE : View.GONE);
        
        if (hasCoupon && appliedCouponCode != null && appliedCouponDiscount != null) {
            // Show applied coupon info
            // TODO: Get coupon details from CouponManager when available
            appliedCouponCode.setText("Coupon Applied");
            appliedCouponDiscount.setText("Discount");
        }
    }

    private void setupOrderSummary() {
        summarySubtotalText = findViewById(R.id.summarySubtotal);
        summaryDiscountText = findViewById(R.id.summaryDiscount);
        summaryTotalText = findViewById(R.id.summaryTotal);
        discountRow = findViewById(R.id.discountRow);
        
        updateOrderSummary();
    }

    private void updateOrderSummary() {
        if (currentCart == null) return;
        
        double subtotal = currentCart.getTotalPrice();
        double discount = CouponManager.getInstance().calculateDiscount(subtotal);
        double shipping = 15.00;
        double total = subtotal - discount + shipping;

        if (summarySubtotalText != null) {
            summarySubtotalText.setText(String.format(Locale.US, "$%.2f", subtotal));
        }
        
        if (discountRow != null && summaryDiscountText != null) {
            if (discount > 0) {
                discountRow.setVisibility(View.VISIBLE);
                summaryDiscountText.setText(String.format(Locale.US, "-$%.2f", discount));
            } else {
                discountRow.setVisibility(View.GONE);
            }
        }
        
        if (summaryTotalText != null) {
            summaryTotalText.setText(String.format(Locale.US, "$%.2f", total));
        }
    }

    private void setupConfirmButton() {
        Button confirmButton = findViewById(R.id.confirmPaymentButton);
        if (confirmButton != null) {
            confirmButton.setOnClickListener(v -> processCheckout());
            UiAnimations.pressBounce(confirmButton);
        }
    }

    /**
     * Process checkout - calls backend API to create order and get payment URL.
     */
    private void processCheckout() {
        // Validate shipping information
        if (!validateShippingInfo()) {
            Toast.makeText(this, "Please fill in all shipping information", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading
        View confirmButton = findViewById(R.id.confirmPaymentButton);
        if (confirmButton != null) confirmButton.setEnabled(false);
        
        // Prepare shipping address
        Map<String, Object> shippingAddress = new HashMap<>();
        if (shippingNameInput != null) {
            shippingAddress.put("name", shippingNameInput.getText().toString());
            shippingAddress.put("phone", shippingPhoneInput.getText().toString());
            shippingAddress.put("address", shippingAddressInput.getText().toString());
            shippingAddress.put("city", shippingCityInput != null ? shippingCityInput.getText().toString() : "");
            shippingAddress.put("postal_code", shippingPostalCodeInput != null ? shippingPostalCodeInput.getText().toString() : "");
        } else {
            // Use default values if fields don't exist
            shippingAddress.put("name", "Customer");
            shippingAddress.put("phone", "+855");
            shippingAddress.put("address", "Phnom Penh");
            shippingAddress.put("city", "Phnom Penh");
            shippingAddress.put("postal_code", "12000");
        }
        
        // Call checkout API
        cartRepository.checkout(
            shippingAddress,
            "aba_payway",  // Payment method
            "",  // Notes
            new CartRepository.CheckoutCallback() {
                @Override
                public void onSuccess(Map<String, Object> response) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "Checkout response: " + response);
                        
                        // Check if response has payment_url (for ABA PayWay)
                        if (response.containsKey("payment_url")) {
                            String paymentUrl = (String) response.get("payment_url");
                            openABAPayment(paymentUrl);
                        } else {
                            // Direct success (no payment gateway needed)
                            showSuccessDialog(response);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        if (confirmButton != null) confirmButton.setEnabled(true);
                        Log.e(TAG, "Checkout failed: " + error);
                        Toast.makeText(PayoutActivity.this, 
                            "Checkout failed: " + error, Toast.LENGTH_LONG).show();
                    });
                }
            }
        );
    }

    /**
     * Validate shipping information.
     */
    private boolean validateShippingInfo() {
        // If shipping fields don't exist in layout, skip validation for now
        if (shippingNameInput == null || shippingPhoneInput == null || shippingAddressInput == null) {
            return true;  // Allow checkout without shipping info for now
        }
        
        return !shippingNameInput.getText().toString().trim().isEmpty()
            && !shippingPhoneInput.getText().toString().trim().isEmpty()
            && !shippingAddressInput.getText().toString().trim().isEmpty();
    }

    /**
     * Open ABA PayWay payment page in WebView.
     * TODO: Create PaymentWebViewActivity for production.
     */
    private void openABAPayment(String paymentUrl) {
        Log.d(TAG, "Opening ABA payment: " + paymentUrl);
        
        // TODO: For production, create PaymentWebViewActivity
        // Intent intent = new Intent(this, PaymentWebViewActivity.class);
        // intent.putExtra("payment_url", paymentUrl);
        // startActivityForResult(intent, PAYMENT_REQUEST_CODE);
        
        // For now, show mock success after delay
        Toast.makeText(this, "Opening payment gateway...", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("order_id", "ORDER-" + System.currentTimeMillis());
            mockResponse.put("status", "success");
            showSuccessDialog(mockResponse);
        }, 2000);
    }

    /**
     * Show success dialog after payment.
     */
    private void showSuccessDialog(Map<String, Object> response) {
        String orderId = response.get("order_id") != null ? 
            response.get("order_id").toString() : "UNKNOWN";
        
        // Animate to success state
        View processingOverlay = findViewById(R.id.processingOverlay);
        View successView = findViewById(R.id.successView);
        TextView successOrderId = findViewById(R.id.successOrderId);
        Button viewOrdersButton = findViewById(R.id.viewOrdersButton);
        Button backToHomeButton = findViewById(R.id.backToHomeButton);

        if (processingOverlay != null) processingOverlay.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (processingOverlay != null) processingOverlay.setVisibility(View.GONE);
            if (successView != null) {
                successView.setVisibility(View.VISIBLE);
                UiAnimations.popIn(successView, 0);
            }
            
            if (successOrderId != null) successOrderId.setText(orderId);
            
            if (viewOrdersButton != null) {
                viewOrdersButton.setOnClickListener(v -> {
                    startActivity(new Intent(this, MyOrdersActivity.class));
                    finish();
                });
                UiAnimations.pressBounce(viewOrdersButton);
            }
            
            if (backToHomeButton != null) {
                backToHomeButton.setOnClickListener(v -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
                UiAnimations.pressBounce(backToHomeButton);
            }

            // Clear coupon
            CouponManager.getInstance().removeCoupon();
        }, 3000);
    }
}
