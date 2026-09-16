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
        shippingNameInput = findViewById(R.id.shippingNameInput);
        shippingPhoneInput = findViewById(R.id.shippingPhoneInput);
        shippingAddressInput = findViewById(R.id.shippingAddressInput);
        shippingCityInput = findViewById(R.id.shippingCityInput);
        shippingPostalCodeInput = findViewById(R.id.shippingPostalCodeInput);
        
        // Optional fields
        EditText shippingStateInput = findViewById(R.id.shippingStateInput);
        EditText shippingCountryInput = findViewById(R.id.shippingCountryInput);
        
        // Pre-fill country with default value
        if (shippingCountryInput != null) {
            shippingCountryInput.setText("Cambodia");
        }
        
        // TODO: Load from user profile if available
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
                    
                    // Debug logging to check cart state
                    Log.d(TAG, "Cart loaded - Subtotal (API): $" + currentCart.getSubtotalFromAPI());
                    Log.d(TAG, "Cart loaded - Subtotal (Calculated): $" + currentCart.getTotalPrice());
                    Log.d(TAG, "Cart coupon_code: " + currentCart.getCouponCode());
                    Log.d(TAG, "Cart discount_total: " + currentCart.getDiscountTotal());
                    Log.d(TAG, "Cart grand_total: " + currentCart.getGrandTotal());
                    
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
                
                if (code.trim().isEmpty()) {
                    Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (currentCart == null) {
                    Toast.makeText(this, "Cart not found", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Disable button while processing
                applyCouponButton.setEnabled(false);
                applyCouponButton.setText("Applying...");
                
                String cartId = String.valueOf(currentCart.getId());
                
                // Call API to apply coupon
                CouponManager.getInstance().applyCoupon(code, cartId, this, 
                    new CouponManager.CouponCallback() {
                        @Override
                        public void onSuccess(String couponCode, double discountAmount) {
                            runOnUiThread(() -> {
                                applyCouponButton.setEnabled(true);
                                applyCouponButton.setText("Apply");
                                couponInput.setText("");
                                
                                String message = String.format(Locale.US, 
                                    "Coupon applied! You saved $%.2f", discountAmount);
                                Toast.makeText(PayoutActivity.this, message, Toast.LENGTH_LONG).show();
                                
                                // Reload cart to get updated totals
                                loadCartFromAPI();
                                updateCouponUI();
                            });
                        }
                        
                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> {
                                applyCouponButton.setEnabled(true);
                                applyCouponButton.setText("Apply");
                                Toast.makeText(PayoutActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
            });
        }

        if (removeCouponButton != null) {
            removeCouponButton.setOnClickListener(v -> {
                // Remove coupon from local state
                CouponManager.getInstance().removeCoupon();
                Toast.makeText(this, "Coupon removed", Toast.LENGTH_SHORT).show();
                
                // Reload cart to get updated totals without coupon
                // Note: Backend doesn't have remove endpoint, but cart refresh will clear it
                loadCartFromAPI();
                updateCouponUI();
            });
        }

        updateCouponUI();
    }

    private void updateCouponUI() {
        boolean hasCoupon = CouponManager.getInstance().hasCoupon();
        
        // Show/hide coupon input vs applied coupon card
        if (couponInput != null) couponInput.setVisibility(hasCoupon ? View.GONE : View.VISIBLE);
        Button applyCouponButton = findViewById(R.id.applyCouponButton);
        if (applyCouponButton != null) applyCouponButton.setVisibility(hasCoupon ? View.GONE : View.VISIBLE);
        if (appliedCouponCard != null) appliedCouponCard.setVisibility(hasCoupon ? View.VISIBLE : View.GONE);
        
        if (hasCoupon && appliedCouponCode != null && appliedCouponDiscount != null) {
            // Show applied coupon info from API
            String code = CouponManager.getInstance().getAppliedCouponCode();
            double discount = CouponManager.getInstance().getDiscountAmount();
            
            appliedCouponCode.setText(code != null ? code : "Coupon Applied");
            appliedCouponDiscount.setText(String.format(Locale.US, "-$%.2f", discount));
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
        
        // Get values from cart API (fully dynamic from backend)
        // Use API subtotal instead of client-side calculation
        double subtotal = currentCart.getSubtotalFromAPI();
        
        // Fallback to client calculation if API doesn't provide subtotal
        if (subtotal == 0.0) {
            subtotal = currentCart.getTotalPrice(); // Calculate from items as fallback
        }
        
        // IMPORTANT: Only use discount if a coupon is actually applied
        // The backend's discount_total includes product-level discounts (sale prices)
        // which are already baked into the item prices. We should ONLY show
        // cart-level discounts (coupons) as a separate line item.
        boolean hasCouponApplied = currentCart.getCouponCode() != null && 
                                   !currentCart.getCouponCode().isEmpty();
        
        // Only apply discount from API if there's a coupon
        // Product discounts are already in the subtotal
        double discount = 0.0;
        if (hasCouponApplied) {
            discount = currentCart.getDiscountTotal();
            Log.d(TAG, "Coupon applied: " + currentCart.getCouponCode() + ", Discount: $" + discount);
        } else {
            Log.d(TAG, "No coupon applied, ignoring discount_total from API");
        }
        
        // Calculate shipping dynamically based on cart total
        double shipping = calculateShippingFee(subtotal);
        
        double total = subtotal - discount + shipping;

        if (summarySubtotalText != null) {
            summarySubtotalText.setText(String.format(Locale.US, "$%.2f", subtotal));
        }
        
        // Only show discount row if a coupon is actually applied
        if (discountRow != null && summaryDiscountText != null) {
            if (hasCouponApplied && discount > 0) {
                discountRow.setVisibility(View.VISIBLE);
                summaryDiscountText.setText(String.format(Locale.US, "-$%.2f", discount));
            } else {
                discountRow.setVisibility(View.GONE);
            }
        }
        
        // Update shipping fee display
        TextView shippingFeeText = findViewById(R.id.summaryShipping);
        if (shippingFeeText != null) {
            if (shipping == 0.0) {
                shippingFeeText.setText("FREE");
                shippingFeeText.setTextColor(getResources().getColor(R.color.green_stock, null));
            } else {
                shippingFeeText.setText(String.format(Locale.US, "$%.2f", shipping));
                shippingFeeText.setTextColor(getResources().getColor(R.color.green_stock, null));
            }
        }
        
        if (summaryTotalText != null) {
            summaryTotalText.setText(String.format(Locale.US, "$%.2f", total));
        }
    }
    
    /**
     * Calculate shipping fee dynamically based on cart total.
     * Matches product detail page logic:
     * - FREE for orders >= $50
     * - $15.00 for orders < $50
     * 
     * TODO: Fetch shipping methods from API when available
     * 
     * @param cartTotal Total cart value
     * @return Shipping fee
     */
    private double calculateShippingFee(double cartTotal) {
        // Free shipping threshold (matches product detail spec)
        final double FREE_SHIPPING_THRESHOLD = 50.00;
        
        if (cartTotal >= FREE_SHIPPING_THRESHOLD) {
            Log.d(TAG, String.format(Locale.US, "Free shipping applied (cart total: $%.2f >= $%.2f)", 
                cartTotal, FREE_SHIPPING_THRESHOLD));
            return 0.0;
        } else {
            // Standard shipping fee
            final double STANDARD_SHIPPING = 15.00;
            Log.d(TAG, String.format(Locale.US, "Standard shipping applied (cart total: $%.2f < $%.2f): $%.2f", 
                cartTotal, FREE_SHIPPING_THRESHOLD, STANDARD_SHIPPING));
            return STANDARD_SHIPPING;
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
        
        if (currentCart == null) {
            Toast.makeText(this, "Cart not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading
        View confirmButton = findViewById(R.id.confirmPaymentButton);
        if (confirmButton != null) confirmButton.setEnabled(false);
        
        // Get optional fields
        EditText stateInput = findViewById(R.id.shippingStateInput);
        EditText countryInput = findViewById(R.id.shippingCountryInput);
        
        // Build checkout request matching NEW API structure
        Map<String, Object> checkoutData = new HashMap<>();
        
        // Required: cart_id
        checkoutData.put("cart_id", String.valueOf(currentCart.getId()));
        
        // Required: shipping_method_id (TODO: Get from API, using default for now)
        // NOTE: Backend has no shipping methods, trying without this field
        // checkoutData.put("shipping_method_id", "1");  // Temporarily commented out
        
        // Required: recipient_name
        checkoutData.put("recipient_name", shippingNameInput.getText().toString().trim());
        
        // Required: address_line_1 (was "street" before)
        checkoutData.put("address_line_1", shippingAddressInput.getText().toString().trim());
        
        // Required: city
        checkoutData.put("city", shippingCityInput.getText().toString().trim());
        
        // Required: country
        String country = "Cambodia";  // Default
        if (countryInput != null && !countryInput.getText().toString().trim().isEmpty()) {
            country = countryInput.getText().toString().trim();
        }
        checkoutData.put("country", country);
        
        // Optional: phone
        checkoutData.put("phone", shippingPhoneInput.getText().toString().trim());
        
        // Optional: postal_code
        checkoutData.put("postal_code", shippingPostalCodeInput.getText().toString().trim());
        
        // Optional: state/province
        if (stateInput != null && !stateInput.getText().toString().trim().isEmpty()) {
            checkoutData.put("state", stateInput.getText().toString().trim());
        }
        
        // Optional: address_line_2
        // checkoutData.put("address_line_2", "");
        
        // Optional: payment_method
        checkoutData.put("payment_method", "aba_payway");
        
        // Optional: notes
        checkoutData.put("notes", "");
        
        Log.d(TAG, "Checkout request prepared: " + checkoutData);
        
        // Call checkout API with new structure
        cartRepository.checkout(
            checkoutData,
            new CartRepository.CheckoutCallback() {
                @Override
                public void onSuccess(Map<String, Object> response) {
                    runOnUiThread(() -> {
                        Log.d(TAG, "Checkout response received: " + response);
                        
                        // Log response structure for debugging
                        if (response != null) {
                            for (Map.Entry<String, Object> entry : response.entrySet()) {
                                Log.d(TAG, "Response key: " + entry.getKey() + " = " + entry.getValue());
                            }
                        }
                        
                        // Check if response has payment_url (for ABA PayWay)
                        String paymentUrl = extractString(response, "payment_url", null);
                        
                        // Also check nested data object
                        Map<String, Object> dataMap = extractMap(response, "data");
                        if (dataMap != null) {
                            String nestedPaymentUrl = extractString(dataMap, "payment_url", null);
                            if (nestedPaymentUrl != null) {
                                paymentUrl = nestedPaymentUrl;
                            }
                        }
                        
                        if (paymentUrl != null && !paymentUrl.isEmpty()) {
                            Log.d(TAG, "Payment URL found, redirecting to: " + paymentUrl);
                            openABAPayment(paymentUrl);
                        } else {
                            // Direct success (no payment gateway needed)
                            Log.d(TAG, "No payment URL, showing success directly");
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
        // Check if all required fields are filled
        if (shippingNameInput == null || shippingPhoneInput == null || 
            shippingAddressInput == null || shippingCityInput == null || 
            shippingPostalCodeInput == null) {
            Log.e(TAG, "Shipping input fields not initialized");
            return false;
        }
        
        String name = shippingNameInput.getText().toString().trim();
        String phone = shippingPhoneInput.getText().toString().trim();
        String address = shippingAddressInput.getText().toString().trim();
        String city = shippingCityInput.getText().toString().trim();
        String postalCode = shippingPostalCodeInput.getText().toString().trim();
        
        // Validate each field and show specific error
        if (name.isEmpty()) {
            shippingNameInput.setError("Recipient name is required");
            shippingNameInput.requestFocus();
            return false;
        }
        
        if (phone.isEmpty()) {
            shippingPhoneInput.setError("Phone number is required");
            shippingPhoneInput.requestFocus();
            return false;
        }
        
        if (address.isEmpty()) {
            shippingAddressInput.setError("Street address is required");
            shippingAddressInput.requestFocus();
            return false;
        }
        
        if (city.isEmpty()) {
            shippingCityInput.setError("City is required");
            shippingCityInput.requestFocus();
            return false;
        }
        
        if (postalCode.isEmpty()) {
            shippingPostalCodeInput.setError("Postal code is required");
            shippingPostalCodeInput.requestFocus();
            return false;
        }
        
        return true;
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
            // Create mock response that matches API structure
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("success", true);
            mockResponse.put("message", "Order created successfully");
            
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("order_id", "ORD-2026-" + String.format("%06d", (int)(Math.random() * 1000000)));
            mockData.put("status", "pending");
            
            // Use API subtotal (dynamic from backend)
            double orderTotal = currentCart != null ? currentCart.getSubtotalFromAPI() : 0.0;
            if (orderTotal == 0.0 && currentCart != null) {
                orderTotal = currentCart.getTotalPrice(); // Fallback to calculated
            }
            
            mockData.put("total_amount", orderTotal);
            mockData.put("items_count", currentCart != null ? currentCart.getItemCount() : 0);
            mockData.put("payment_method", "aba_payway");
            mockData.put("created_at", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(new java.util.Date()));
            
            mockResponse.put("data", mockData);
            
            showSuccessDialog(mockResponse);
        }, 2000);
    }

    /**
     * Show success dialog after payment.
     * Dynamically extracts data from the API response.
     */
    private void showSuccessDialog(Map<String, Object> response) {
        // Extract order details from response
        String orderId = extractString(response, "order_id", "UNKNOWN");
        String status = extractString(response, "status", "pending");
        Double totalAmount = extractDouble(response, "total_amount");
        Integer itemsCount = extractInteger(response, "items_count");
        
        // Extract nested data if available
        Map<String, Object> dataMap = extractMap(response, "data");
        if (dataMap != null) {
            // If response has nested "data" object, extract from there
            orderId = extractString(dataMap, "order_id", orderId);
            status = extractString(dataMap, "status", status);
            totalAmount = extractDouble(dataMap, "total_amount", totalAmount);
            itemsCount = extractInteger(dataMap, "items_count", itemsCount);
        }
        
        Log.d(TAG, String.format("Order created - ID: %s, Status: %s, Total: $%.2f, Items: %d", 
            orderId, status, totalAmount != null ? totalAmount : 0.0, itemsCount != null ? itemsCount : 0));
        
        // Animate to success state
        View processingOverlay = findViewById(R.id.processingOverlay);
        View successView = findViewById(R.id.successView);
        TextView successOrderId = findViewById(R.id.successOrderId);
        Button viewOrdersButton = findViewById(R.id.viewOrdersButton);
        Button backToHomeButton = findViewById(R.id.backToHomeButton);
        
        // Optional views that may not exist in layout yet - will be null if not found
        // These can be added to activity_payout.xml later for enhanced display
        final TextView successOrderStatus;
        final TextView successOrderTotal;
        final TextView successOrderItems;
        
        // Try to find optional views by ID (returns null if not in R.id)
        int statusId = getResources().getIdentifier("successOrderStatus", "id", getPackageName());
        int totalId = getResources().getIdentifier("successOrderTotal", "id", getPackageName());
        int itemsId = getResources().getIdentifier("successOrderItems", "id", getPackageName());
        
        successOrderStatus = (statusId != 0) ? findViewById(statusId) : null;
        successOrderTotal = (totalId != 0) ? findViewById(totalId) : null;
        successOrderItems = (itemsId != 0) ? findViewById(itemsId) : null;

        if (processingOverlay != null) processingOverlay.setVisibility(View.VISIBLE);

        final String finalOrderId = orderId;
        final String finalStatus = status;
        final Double finalTotal = totalAmount;
        final Integer finalItemsCount = itemsCount;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (processingOverlay != null) processingOverlay.setVisibility(View.GONE);
            if (successView != null) {
                successView.setVisibility(View.VISIBLE);
                UiAnimations.popIn(successView, 0);
            }
            
            // Display order ID
            if (successOrderId != null) {
                successOrderId.setText(finalOrderId);
            }
            
            // Display order status (if view exists)
            if (successOrderStatus != null) {
                successOrderStatus.setText(String.format("Status: %s", 
                    capitalizeFirst(finalStatus)));
            }
            
            // Display total amount (if view exists)
            if (successOrderTotal != null && finalTotal != null) {
                successOrderTotal.setText(String.format(Locale.US, "Total: $%.2f", finalTotal));
            }
            
            // Display items count (if view exists)
            if (successOrderItems != null && finalItemsCount != null) {
                successOrderItems.setText(String.format(Locale.US, "%d item%s", 
                    finalItemsCount, finalItemsCount != 1 ? "s" : ""));
            }
            
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
    
    /**
     * Safely extract String from Map.
     */
    private String extractString(Map<String, Object> map, String key, String defaultValue) {
        if (map == null || !map.containsKey(key)) return defaultValue;
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * Safely extract Double from Map.
     */
    private Double extractDouble(Map<String, Object> map, String key) {
        return extractDouble(map, key, null);
    }
    
    private Double extractDouble(Map<String, Object> map, String key, Double defaultValue) {
        if (map == null || !map.containsKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value == null) return defaultValue;
        
        try {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                return Double.parseDouble((String) value);
            }
        } catch (NumberFormatException e) {
            Log.w(TAG, "Failed to parse double from: " + value, e);
        }
        return defaultValue;
    }
    
    /**
     * Safely extract Integer from Map.
     */
    private Integer extractInteger(Map<String, Object> map, String key) {
        return extractInteger(map, key, null);
    }
    
    private Integer extractInteger(Map<String, Object> map, String key, Integer defaultValue) {
        if (map == null || !map.containsKey(key)) return defaultValue;
        Object value = map.get(key);
        if (value == null) return defaultValue;
        
        try {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
        } catch (NumberFormatException e) {
            Log.w(TAG, "Failed to parse integer from: " + value, e);
        }
        return defaultValue;
    }
    
    /**
     * Safely extract nested Map from Map.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractMap(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }
    
    /**
     * Capitalize first letter of string.
     */
    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
