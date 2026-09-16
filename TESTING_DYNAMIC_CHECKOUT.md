# Testing Dynamic Checkout Response

## Quick Test Guide

### Test Scenarios

#### Scenario 1: Normal Checkout Response
**API Response:**
```json
{
  "success": true,
  "data": {
    "order_id": "ORD-2026-123456",
    "status": "pending",
    "total_amount": 1599.00,
    "items_count": 3
  }
}
```

**Expected Display:**
- Order ID: `ORD-2026-123456`
- Status: `Pending` (capitalized)
- Total: `Total: $1599.00`
- Items: `3 items`

---

#### Scenario 2: Flat Response Structure
**API Response:**
```json
{
  "order_id": "ORD-2026-789012",
  "status": "completed",
  "total_amount": 299.99,
  "items_count": 1
}
```

**Expected Display:**
- Order ID: `ORD-2026-789012`
- Status: `Completed`
- Total: `Total: $299.99`
- Items: `1 item` (singular)

---

#### Scenario 3: Missing Optional Fields
**API Response:**
```json
{
  "success": true,
  "data": {
    "order_id": "ORD-2026-345678"
  }
}
```

**Expected Display:**
- Order ID: `ORD-2026-345678`
- Status: `Pending` (default)
- Total: Not displayed (or displays if view missing)
- Items: Not displayed (or displays if view missing)

**Expected Behavior:**
- No crashes
- Gracefully handles missing fields
- Logs: `Order created - ID: ORD-2026-345678, Status: pending, Total: $0.00, Items: 0`

---

#### Scenario 4: Response with Payment URL
**API Response:**
```json
{
  "success": true,
  "data": {
    "order_id": "ORD-2026-999999",
    "payment_url": "https://payment.gateway/checkout/abc123"
  }
}
```

**Expected Behavior:**
1. Detects `payment_url` in response
2. Logs: `Payment URL found, redirecting to: https://payment.gateway/checkout/abc123`
3. Calls `openABAPayment(url)` instead of showing success directly
4. (In production) Opens payment gateway
5. (In mock) Shows mock success after 2 seconds

---

#### Scenario 5: Completely Empty Response
**API Response:**
```json
{}
```

**Expected Display:**
- Order ID: `UNKNOWN` (default)
- Status: `Pending` (default)
- No crashes

---

## Checking Logs

### Android Studio Logcat Filters

**Filter by Checkout Activity:**
```
tag:PayoutActivity
```

**Expected Log Messages:**

**On Checkout Success:**
```
D/PayoutActivity: Checkout response received: {success=true, data={...}}
D/PayoutActivity: Response key: success = true
D/PayoutActivity: Response key: data = {...}
D/PayoutActivity: No payment URL, showing success directly
D/PayoutActivity: Order created - ID: ORD-2026-123456, Status: pending, Total: $1599.00, Items: 3
```

**With Payment URL:**
```
D/PayoutActivity: Payment URL found, redirecting to: https://...
D/PayoutActivity: Opening ABA payment: https://...
```

**On Checkout Error:**
```
E/PayoutActivity: Checkout failed: 400
```

---

## Manual Testing Steps

### Setup
1. Login with test account: `horratha4t5@gmail.com` / `qwer1234`
2. Add items to cart
3. Navigate to Cart → Checkout

### Test Flow
1. **Fill Shipping Info** (if fields exist):
   - Name: Test User
   - Phone: +855 98 999 9999
   - Address: 123 Main Street
   - City: Phnom Penh
   - Postal Code: 12000

2. **Tap "Confirm Order"**
   - Observe loading state
   - Check Logcat for response logs

3. **Verify Success Screen**:
   - Check Order ID displays correctly
   - Check Status shows (if view exists)
   - Check Total shows (if view exists)
   - Check Items count shows (if view exists)

4. **Test Navigation**:
   - Tap "View Orders" → Should navigate to MyOrdersActivity
   - OR tap "Back to Home" → Should navigate to MainActivity

---

## Code Verification Checklist

✅ **Helper Methods Exist:**
- `extractString(Map, key, default)`
- `extractDouble(Map, key, default)`
- `extractInteger(Map, key, default)`
- `extractMap(Map, key)`
- `capitalizeFirst(String)`

✅ **Response Handling:**
- Checks flat structure first
- Checks nested `data` object
- Uses default values for missing fields
- Logs extracted values

✅ **Payment URL Detection:**
- Checks `response.payment_url`
- Checks `response.data.payment_url`
- Logs when payment URL found
- Falls back to direct success

✅ **Display Logic:**
- Null-checks all views
- Formats currency properly (`$%.2f`)
- Pluralizes items count (`1 item` vs `2 items`)
- Capitalizes status text

---

## Expected vs Actual Response

### From Checklist (Section 3.1)

**Expected API Response:**
```json
{
  "success": true,
  "data": {
    "order_id": "ORD-2026-001234",
    "status": "pending",
    "total_amount": 1599.00,
    "items_count": 1,
    "shipping_address": {
      "recipient_name": "Test User",
      "street": "123 Main Street",
      "city": "Phnom Penh",
      "state": "Kandal",
      "postal_code": "12000",
      "country": "Cambodia",
      "phone": "+855 (98) 999-9999"
    },
    "payment_method": "card",
    "notes": "Test checkout order",
    "created_at": "2026-09-13T12:34:56Z"
  },
  "message": "Order created successfully"
}
```

**Fields Used by App:**
- ✅ `data.order_id` → Display order number
- ✅ `data.status` → Display order status
- ✅ `data.total_amount` → Display total
- ✅ `data.items_count` → Display item count
- ⚠️ `data.shipping_address` → Not displayed (could be added)
- ⚠️ `data.payment_method` → Not displayed (could be added)
- ⚠️ `data.created_at` → Not displayed (could be added)

---

## Troubleshooting

### Issue: "UNKNOWN" Order ID
**Cause:** Response doesn't contain `order_id` field
**Check:** 
- API response in Logcat
- Backend endpoint returning correct structure

### Issue: Status not showing
**Cause:** `successOrderStatus` view doesn't exist in layout
**Solution:** Either add view to `activity_payout.xml` or ignore (gracefully handled)

### Issue: Total showing $0.00
**Cause:** 
- `total_amount` missing from response
- OR `total_amount` is string instead of number
**Check:** Response logs for field type

### Issue: App crashes on checkout
**Cause:** Unexpected response structure
**Check:** 
- Logcat for `ClassCastException`
- Response structure in logs
- Helper methods handle type conversion

---

## Success Criteria

✅ App handles all response formats without crashing
✅ Order ID always displays (defaults to "UNKNOWN")
✅ Optional fields display when available, hide when missing
✅ Logs show complete response structure
✅ Payment URL detection works for nested/flat structures
✅ Currency formatting is correct ($1599.00, not $1599)
✅ Item pluralization is correct (1 item, 2 items)
✅ Navigation buttons work correctly

---

## Next Enhancement Ideas

1. **Add More Display Fields:**
   ```xml
   <TextView android:id="@+id/successPaymentMethod" />
   <TextView android:id="@+id/successCreatedAt" />
   ```

2. **Format Date:**
   ```java
   String createdAt = extractString(dataMap, "created_at");
   // Format to: "Sep 13, 2026 12:34 PM"
   ```

3. **Show Shipping Address:**
   ```java
   Map<String, Object> shippingAddr = extractMap(dataMap, "shipping_address");
   // Display in success screen
   ```

4. **Add Order Summary:**
   - Show each item in order
   - Show subtotal, tax, shipping breakdown

---

**Last Updated:** 2026-09-13
**Related Files:** 
- `PayoutActivity.java`
- `CHECKOUT_TESTING_CHECKLIST.md`
- `CHECKOUT_DYNAMIC_RESPONSE_UPDATE.md`
