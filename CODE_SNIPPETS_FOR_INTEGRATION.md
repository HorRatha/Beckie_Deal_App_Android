# Code Snippets for ABA PayWay Integration

Use these snippets to quickly integrate the payment system into your existing code.

---

## 1. CartActivity - Update Checkout Button

**Replace existing checkout button click listener with:**

```java
// In CartActivity.java - onCreate() or where checkout button is set up

checkoutButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (AuthHelper.isUserLoggedIn(CartActivity.this)) {
            // Check if cart has items
            if (cart != null && cart.items.size() > 0) {
                Intent intent = new Intent(CartActivity.this, PayoutActivityV2.class);
                startActivity(intent);
            } else {
                Toast.makeText(CartActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(CartActivity.this, "Please login to proceed", Toast.LENGTH_SHORT).show();
            // Optional: Navigate to login
            // startActivity(new Intent(CartActivity.this, LoginActivity.class));
        }
    }
});
```

---

## 2. CartApiService - Add Checkout Endpoint

**Add these methods to your CartApiService interface:**

```java
/**
 * Checkout cart - creates order
 * POST /api/v1/cart/checkout
 */
@POST("cart/checkout")
Call<CheckoutResponse> checkout(@Body Map<String, Object> checkoutData);

/**
 * Get order by ID
 * GET /api/v1/orders/{orderId}
 */
@GET("orders/{orderId}")
Call<OrderResponse> getOrderDetail(@Path("orderId") long orderId);

/**
 * Get orders list
 * GET /api/v1/orders
 */
@GET("orders")
Call<OrderListResponse> getOrders(
    @Query("page") int page,
    @Query("per_page") int perPage
);
```

---

## 3. CartRepository - Add Callback Interfaces and Methods

**Add these to your CartRepository class:**

```java
// Add these callback interfaces
public interface CheckoutCallback {
    void onSuccess(CheckoutResponse response);
    void onError(String error);
}

public interface OrderDetailCallback {
    void onSuccess(OrderResponse response);
    void onError(String error);
}

// Add these methods to CartRepository
public void checkout(Map<String, Object> checkoutData, CheckoutCallback callback) {
    Call<CheckoutResponse> call = cartApiService.checkout(checkoutData);
    
    call.enqueue(new Callback<CheckoutResponse>() {
        @Override
        public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                Log.d(TAG, "Checkout successful - Order ID: " + response.body().data.order_id);
                callback.onSuccess(response.body());
            } else {
                Log.e(TAG, "Checkout failed: " + response.code());
                callback.onError("Checkout failed");
            }
        }

        @Override
        public void onFailure(Call<CheckoutResponse> call, Throwable t) {
            Log.e(TAG, "Checkout error", t);
            callback.onError("Network error: " + t.getMessage());
        }
    });
}

public void getOrderDetail(long orderId, OrderDetailCallback callback) {
    Call<OrderResponse> call = cartApiService.getOrderDetail(orderId);
    
    call.enqueue(new Callback<OrderResponse>() {
        @Override
        public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(response.body());
            } else {
                callback.onError("Failed to get order details");
            }
        }

        @Override
        public void onFailure(Call<OrderResponse> call, Throwable t) {
            callback.onError("Network error: " + t.getMessage());
        }
    });
}
```

---

## 4. Create Data Models

**File: CheckoutResponse.java**

```java
package aeu.ratha.beckie_deal_app.data.models;

public class CheckoutResponse {
    public String status;
    public String message;
    public CheckoutData data;
    
    public class CheckoutData {
        public long order_id;
        public String order_number;
        public String status;
        public long total;
        public String created_at;
    }
}
```

**File: OrderResponse.java**

```java
package aeu.ratha.beckie_deal_app.data.models;

import java.util.List;

public class OrderResponse {
    public String status;
    public String message;
    public Order data;
}

class Order {
    public long id;
    public String order_number;
    public String status;
    public long total;
    public long subtotal;
    public long discount_total;
    public String created_at;
    public String expected_delivery_date;
    public String payment_status;
    public String payment_method;
    public String shipping_method;
    public Object shipping_address; // JSON object
    public List<OrderItem> items;
}

class OrderItem {
    public long product_id;
    public String product_name;
    public int quantity;
    public long price;
    public String sku;
}
```

---

## 5. AndroidManifest.xml - Add Activities

**Add these inside the `<application>` tag:**

```xml
<!-- Checkout Activity -->
<activity
    android:name=".ui.activities.PayoutActivityV2"
    android:exported="false"
    android:screenOrientation="portrait"
    android:windowSoftInputMode="adjustResize" />

<!-- Payment Activity -->
<activity
    android:name=".ui.activities.AbaPaymentActivity"
    android:exported="false"
    android:screenOrientation="portrait" />

<!-- Order Confirmation Activity -->
<activity
    android:name=".ui.activities.OrderConfirmationActivity"
    android:exported="false"
    android:screenOrientation="portrait" />
```

---

## 6. String Resources - Add to strings.xml

**Add these string definitions:**

```xml
<!-- Checkout Strings -->
<string name="step_1_shipping">Shipping Address</string>
<string name="step_2_payment">Shipping &amp; Payment</string>
<string name="step_3_review">Review Order</string>
<string name="continue_checkout">Continue</string>
<string name="back">Back</string>

<!-- Shipping -->
<string name="shipping_method">Shipping Method</string>
<string name="standard_delivery">Standard Delivery (5-7 days)</string>
<string name="express_delivery">Express Delivery (2-3 days)</string>

<!-- Payment Methods -->
<string name="payment_method">Payment Method</string>
<string name="aba_payway">ABA PayWay</string>
<string name="card_payment">Pay with Debit/Credit Card</string>
<string name="cash_on_delivery">Cash on Delivery</string>
<string name="pay_on_delivery">Pay when you receive</string>

<!-- Form Fields -->
<string name="full_name">Full Name</string>
<string name="phone_number">Phone Number</string>
<string name="address">Street Address</string>
<string name="city">City</string>
<string name="postal_code">Postal Code</string>
<string name="country">Country</string>

<!-- Order Review -->
<string name="review_order">Review Your Order</string>
<string name="order_summary">Order Summary</string>
<string name="shipping_address">Shipping Address</string>
<string name="place_order">Place Order</string>

<!-- Payment -->
<string name="aba_payment">ABA Payment</string>
<string name="loading_payment">Loading payment...</string>
<string name="secure_payment_notice">🔒 This is a secure payment gateway</string>

<!-- Confirmation -->
<string name="thank_you">Thank You!</string>
<string name="confirmation_email">A confirmation email has been sent to your email address</string>
<string name="track_order">Track Order</string>
<string name="continue_shopping">Continue Shopping</string>

<!-- Other -->
<string name="aba_bank">ABA Bank</string>
<string name="success">Success</string>
<string name="security">Security</string>
</string>
<string name="cash_delivery">Cash Delivery</string>
<string name="selected">Selected</string>
```

---

## 7. AbaPaymentActivity Configuration

**Key configuration in AbaPaymentActivity.java:**

```java
/**
 * Configure WebView according to ABA guidelines
 */
@SuppressLint("SetJavaScriptEnabled")
private void configureWebView() {
    WebSettings settings = webView.getSettings();
    
    // Enable JavaScript (required for ABA payment gateway)
    settings.setJavaScriptEnabled(true);
    settings.setDomStorageEnabled(true);
    settings.setDatabaseEnabled(true);
    
    // Security settings
    settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    settings.setUseWideViewPort(true);
    settings.setLoadWithOverviewMode(true);
    
    // User agent for mobile
    settings.setUserAgentString("Mozilla/5.0 (Linux; Android " + android.os.Build.VERSION.RELEASE + 
        "; " + android.os.Build.MODEL + ") AppleWebKit/537.36");
    
    // Enable cookies
    android.webkit.CookieManager.getInstance().setAcceptCookie(true);
    
    // Disable zoom
    settings.setSupportZoom(false);
}

/**
 * Payment callback detection
 */
private boolean isSuccessCallback(String url) {
    return url.contains("success") || 
           url.contains("confirmed") || 
           url.contains("approved") ||
           url.contains("status=1") ||
           url.contains("success=true");
}

private boolean isFailureCallback(String url) {
    return url.contains("failed") || 
           url.contains("declined") || 
           url.contains("rejected") ||
           url.contains("status=0");
}
```

---

## 8. PayoutActivityV2 - Checkout Data Building

**Key method in PayoutActivityV2.java:**

```java
/**
 * Build checkout request data
 */
private Map<String, Object> buildCheckoutData() {
    Map<String, Object> checkoutData = new HashMap<>();
    
    // Shipping Address
    Map<String, String> shippingAddressMap = new HashMap<>();
    shippingAddressMap.put("name", shippingName);
    shippingAddressMap.put("phone", shippingPhone);
    shippingAddressMap.put("address", shippingAddress);
    shippingAddressMap.put("city", shippingCity);
    shippingAddressMap.put("postal_code", shippingPostalCode);
    shippingAddressMap.put("country", shippingCountry);
    
    checkoutData.put("shipping_address", shippingAddressMap);
    checkoutData.put("shipping_method_id", selectedShippingMethod);
    checkoutData.put("payment_method", selectedPaymentMethod);
    checkoutData.put("coupon_code", currentCart.coupon_code != null ? currentCart.coupon_code : "");
    checkoutData.put("notes", "");
    
    return checkoutData;
}

/**
 * Initiate ABA Payment
 */
private void initiateAbaPayment(long orderId) {
    showProgress(true);
    
    long amount = currentCart.grand_total;
    String description = "Order #" + orderId;
    String merchantId = "YOUR_MERCHANT_ID"; // TODO: Update this
    String returnUrl = "https://yourapi.com/payment-callback"; // TODO: Update this
    
    paymentRepository.initiateAbaPayment(orderId, amount, description, merchantId, returnUrl,
        new PaymentRepository.AbaPaymentCallback() {
            @Override
            public void onSuccess(PaymentApiService.AbaPaymentResponse response) {
                showProgress(false);
                launchAbaPaymentActivity(response.data.redirect_url, response.data.transaction_id, orderId);
            }
            
            @Override
            public void onError(String error) {
                showProgress(false);
                Toast.makeText(PayoutActivityV2.this, "Payment error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
}
```

---

## 9. Activity Result Handling

**In PayoutActivityV2.java:**

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    if (requestCode == REQUEST_ABA_PAYMENT) {
        if (resultCode == AbaPaymentActivity.RESULT_PAYMENT_SUCCESS) {
            String transactionId = data.getStringExtra("transaction_id");
            long orderId = data.getLongExtra("order_id", 0);
            handleOrderSuccess(orderId, "Payment successful! Your order is confirmed.");
        } 
        else if (resultCode == AbaPaymentActivity.RESULT_PAYMENT_FAILED) {
            String errorMessage = data.getStringExtra("error_message");
            handleOrderFailure("Payment failed: " + errorMessage);
        } 
        else if (resultCode == AbaPaymentActivity.RESULT_PAYMENT_CANCELLED) {
            handleOrderFailure("Payment cancelled. Please try again.");
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

private void handleOrderFailure(String errorMessage) {
    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    btnPlaceOrder.setEnabled(true);
    showProgress(false);
}
```

---

## 10. Complete Test Scenario

**Example test flow:**

```java
// 1. User adds product to cart
addToCart(productId, quantity);

// 2. User navigates to checkout
Intent intent = new Intent(CartActivity.this, PayoutActivityV2.class);
startActivity(intent);

// 3. Checkout Flow
// Step 1: Enter shipping address
//   Name: John Doe
//   Phone: 855-12-345-678
//   Address: 123 Main St
//   City: Phnom Penh
//   Postal: 12000
//   Country: Cambodia

// Step 2: Select methods
//   Shipping: Standard (5-7 days)
//   Payment: ABA PayWay

// Step 3: Review and place order
//   Total: $50.00
//   Click "Place Order"

// 4. Backend creates order
// Order created with ID: 123

// 5. Backend initiates ABA payment
// POST /payments/aba/purchase
// Returns: redirect_url

// 6. AbaPaymentActivity loads payment gateway
// User enters card details

// 7. Payment processed (success)
// Callback URL triggered with status=success

// 8. OrderConfirmationActivity displayed
// Shows order #123 confirmation

// 9. User can track order or continue shopping
```

---

## Troubleshooting Snippets

**Check if user is logged in before checkout:**

```java
if (!AuthHelper.isUserLoggedIn(context)) {
    Toast.makeText(context, "Please login to continue", Toast.LENGTH_SHORT).show();
    startActivity(new Intent(context, LoginActivity.class));
    return;
}
```

**Enable WebView debugging:**

```java
if (BuildConfig.DEBUG) {
    WebView.setWebContentsDebuggingEnabled(true);
}
```

**Log payment flow:**

```java
Log.d(TAG, "Payment Flow:");
Log.d(TAG, "1. Order ID: " + orderId);
Log.d(TAG, "2. Amount: " + amount);
Log.d(TAG, "3. Merchant: " + merchantId);
Log.d(TAG, "4. Return URL: " + returnUrl);
```

---

**These snippets are ready to use. Copy and adapt them to your existing code.**
