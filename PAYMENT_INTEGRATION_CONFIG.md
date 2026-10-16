# Payment Integration Configuration Guide

## Quick Setup

### 1. Add Activities to AndroidManifest.xml

```xml
<activity
    android:name=".ui.activities.PayoutActivityV2"
    android:exported="false"
    android:screenOrientation="portrait" />

<activity
    android:name=".ui.activities.AbaPaymentActivity"
    android:exported="false"
    android:screenOrientation="portrait" />

<activity
    android:name=".ui.activities.OrderConfirmationActivity"
    android:exported="false"
    android:screenOrientation="portrait" />
```

### 2. Update CartActivity

Replace existing checkout button logic:

```java
checkoutButton.setOnClickListener(v -> {
    if (AuthHelper.isUserLoggedIn(this)) {
        Intent intent = new Intent(CartActivity.this, PayoutActivityV2.class);
        startActivity(intent);
    } else {
        Toast.makeText(this, "Please login to proceed", Toast.LENGTH_SHORT).show();
    }
});
```

### 3. Configure ABA Credentials

Edit `PayoutActivityV2.java` - `initiateAbaPayment()` method:

```java
String merchantId = "YOUR_MERCHANT_ID";  // Replace with your ABA Merchant ID
String returnUrl = "https://yourapi.com/payment-callback";  // Your backend URL
```

### 4. Add CartRepository Methods

Add to existing `CartRepository`:

```java
public interface CheckoutCallback {
    void onSuccess(CheckoutResponse response);
    void onError(String error);
}

public interface OrderDetailCallback {
    void onSuccess(OrderResponse response);
    void onError(String error);
}

public void checkout(Map<String, Object> checkoutData, CheckoutCallback callback) {
    Call<CheckoutResponse> call = cartApiService.checkout(checkoutData);
    call.enqueue(new Callback<CheckoutResponse>() {
        @Override
        public void onResponse(Call<CheckoutResponse> call, Response<CheckoutResponse> response) {
            if (response.isSuccessful() && response.body() != null) {
                callback.onSuccess(response.body());
            } else {
                callback.onError("Checkout failed");
            }
        }
        
        @Override
        public void onFailure(Call<CheckoutResponse> call, Throwable t) {
            callback.onError(t.getMessage());
        }
    });
}

public void getOrderDetail(long orderId, OrderDetailCallback callback) {
    // Similar implementation
}
```

### 5. Update CartApiService

Add checkout endpoint:

```java
@POST("cart/checkout")
Call<CheckoutResponse> checkout(@Body Map<String, Object> checkoutData);

@GET("orders/{orderId}")
Call<OrderResponse> getOrderDetail(@Path("orderId") long orderId);
```

### 6. Add Data Models

Create new files in `data/models/`:
- `CheckoutResponse.java`
- `OrderResponse.java`
- `Order.java`
- `OrderItem.java`

---

## Files Created

### API Layer
- `data/api/PaymentApiService.java` - All payment endpoints

### Repository Layer
- `data/repositories/PaymentRepository.java` - Payment operations

### UI Layer (Activities)
- `ui/activities/AbaPaymentActivity.java` - WebView payment processor
- `ui/activities/PayoutActivityV2.java` - Multi-step checkout
- `ui/activities/OrderConfirmationActivity.java` - Confirmation screen

### Layouts
- `res/layout/activity_aba_payment.xml`
- `res/layout/activity_payout_v2.xml`
- `res/layout/activity_order_confirmation.xml`

---

## Payment Flow

```
1. User in CartActivity
   ↓
2. Click Checkout → PayoutActivityV2
   ↓
3. Step 1: Enter Shipping Address
   ↓
4. Step 2: Select Shipping & Payment Method
   ↓
5. Step 3: Review Order
   ↓
6. Click "Place Order"
   ├─ If ABA PayWay:
   │  ├─ POST /orders (create)
   │  ├─ POST /payments/aba/purchase (initiate)
   │  └─ Launch AbaPaymentActivity (WebView)
   │     └─ User pays at ABA gateway
   │        └─ Callback received
   │           └─ OrderConfirmationActivity
   │
   └─ If Cash on Delivery:
      ├─ POST /orders (create)
      └─ OrderConfirmationActivity
```

---

## API Endpoints Required

### Create Order
```
POST /api/v1/orders
Body: {
  shipping_address: {...},
  shipping_method_id: "standard",
  payment_method: "aba_payway" | "cod",
  coupon_code: "optional",
  notes: "optional"
}
```

### Initiate ABA Payment
```
POST /api/v1/payments/aba/purchase
Body: {
  order_id: 123,
  amount: 50000,
  description: "Order #123",
  merchant_id: "...",
  return_url: "...",
  currency: "USD"
}
```

### Get Order Detail
```
GET /api/v1/orders/{orderId}
```

---

## Environment Variables (Optional but Recommended)

Create `BuildConfig` constants:

```java
public class PaymentConfig {
    public static final String ABA_MERCHANT_ID = "YOUR_MERCHANT_ID";
    public static final String ABA_RETURN_URL = "https://yourapi.com/payment-callback";
    public static final long PAYMENT_TIMEOUT_MS = 5 * 60 * 1000; // 5 minutes
}
```

---

## Testing Payloads

### Create Order Request
```json
{
  "shipping_address": {
    "name": "John Doe",
    "phone": "855-12-345-678",
    "address": "123 Main St",
    "city": "Phnom Penh",
    "postal_code": "12000",
    "country": "Cambodia"
  },
  "shipping_method_id": "standard",
  "payment_method": "aba_payway",
  "coupon_code": "",
  "notes": ""
}
```

### ABA Payment Initiation
```json
{
  "order_id": 1,
  "amount": 50000,
  "description": "Order #1",
  "merchant_id": "DEMO",
  "return_url": "https://yourapi.com/payment-callback",
  "currency": "USD"
}
```

---

## Debugging

### Enable Logging

In `RetrofitClient`:

```java
HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Show full response

OkHttpClient.Builder builder = new OkHttpClient.Builder()
    .addInterceptor(logging);
```

### Check WebView Logs

```java
WebView.setWebContentsDebuggingEnabled(true); // In AbaPaymentActivity onCreate
```

### Verify Callbacks

Add logs in `AbaPaymentWebViewClient`:

```java
@Override
public boolean shouldOverrideUrlLoading(WebView view, String url) {
    Log.d(TAG, "Callback URL: " + url);  // Check what URL is being called
    // ... rest of code ...
}
```

---

## Common Issues & Fixes

### WebView not loading payment page
- Check internet connectivity
- Verify HTTPS certificate is valid
- Enable JavaScript in WebView settings

### Payment callback not received
- Verify return_url matches your backend
- Check if WebView is intercepting callback correctly
- Ensure HTTPS is used everywhere

### Order creation fails
- Verify all required fields in checkout data
- Check authentication token is valid
- Verify API endpoint is correct

### "Merchant ID invalid" error
- Update merchant ID in PayoutActivityV2
- Get correct ID from ABA Dashboard
- Verify merchant account is active

---

## Security Checklist

- ✅ All API calls over HTTPS
- ✅ JWT token included in requests (via AuthInterceptor)
- ✅ Card details never stored locally
- ✅ WebView JavaScript enabled only for payment gateway
- ✅ User must be logged in to checkout
- ✅ Payment timeout implemented
- ✅ Error messages don't expose sensitive data
- ✅ Callback URL validated before processing

---

## Next Steps

1. **Register with ABA**
   - Visit https://www.ababank.com
   - Apply for merchant account
   - Get Merchant ID and API credentials

2. **Set up Backend Webhook**
   - Create endpoint: `/payment-callback`
   - Handle payment status updates
   - Update order status in database

3. **Test in Sandbox**
   - Use test merchant credentials
   - Use test payment cards from ABA
   - Verify all scenarios (success/fail/cancel)

4. **Go Live**
   - Update to production credentials
   - Test with real transactions (small amount)
   - Monitor payment logs

---

## Support

**ABA Support:**
- Website: https://www.ababank.com
- Developer Portal: https://developer.ababank.com
- Email: support@ababank.com
- Phone: +855-23-910-910

**Beckie Deal:**
- Development: Follow guidelines in this document
- Testing: Use sandbox credentials first
- Production: Migrate to live credentials

---

**Last Updated:** October 15, 2026
**Version:** 1.0
**Status:** Ready for Implementation
