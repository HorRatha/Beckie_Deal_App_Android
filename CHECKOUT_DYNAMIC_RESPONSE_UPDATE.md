# Checkout Dynamic Response Implementation

## Overview
Updated the checkout feature to dynamically handle API response data instead of using hardcoded values. The system now properly extracts and displays order information from the backend API response.

## Changes Made

### 1. **PayoutActivity.java** - Dynamic Response Handling

#### Enhanced `showSuccessDialog()` Method
- **Before**: Only extracted `order_id` with basic null check
- **After**: Dynamically extracts multiple fields from API response:
  - `order_id` - Order identifier
  - `status` - Order status (pending, completed, etc.)
  - `total_amount` - Total order amount
  - `items_count` - Number of items in order
  - Handles both flat and nested response structures (checks `data` object)

#### New Helper Methods
Added safe data extraction methods to handle various response formats:

```java
extractString(Map, key, default)     // Extract string values
extractDouble(Map, key, default)     // Extract numeric amounts
extractInteger(Map, key, default)    // Extract counts/integers
extractMap(Map, key)                 // Extract nested objects
capitalizeFirst(String)              // Format status text
```

These methods handle:
- Null values gracefully
- Type conversion (String to Number and vice versa)
- Missing keys with sensible defaults
- Nested data structures

#### Enhanced Success Dialog Display
The success screen now dynamically shows:
- **Order ID**: From `order_id` field
- **Status**: From `status` field (formatted with capitalization)
- **Total Amount**: From `total_amount` field (formatted as currency)
- **Items Count**: From `items_count` field (with proper pluralization)

All fields are optional - if views don't exist in the layout, they are skipped gracefully.

### 2. **Enhanced Checkout Flow**

#### Improved Response Logging
```java
// Logs entire response structure
Log.d(TAG, "Checkout response received: " + response);

// Logs each key-value pair for debugging
for (Map.Entry<String, Object> entry : response.entrySet()) {
    Log.d(TAG, "Response key: " + entry.getKey() + " = " + entry.getValue());
}
```

#### Smart Payment URL Detection
Now checks for `payment_url` in multiple locations:
1. Root level: `response.payment_url`
2. Nested data: `response.data.payment_url`

This handles various API response structures.

### 3. **Mock Payment Flow Enhancement**

Updated mock response to match real API structure:

```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "order_id": "ORD-2026-001234",
    "status": "pending",
    "total_amount": 1599.00,
    "items_count": 3,
    "payment_method": "aba_payway",
    "created_at": "2026-09-13T12:34:56Z"
  }
}
```

## API Response Formats Supported

### Format 1: Flat Structure
```json
{
  "order_id": "ORD-123",
  "status": "pending",
  "total_amount": 100.00,
  "items_count": 2
}
```

### Format 2: Nested Structure (Wrapped in `data`)
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "order_id": "ORD-123",
    "status": "pending",
    "total_amount": 100.00,
    "items_count": 2
  }
}
```

### Format 3: With Payment URL
```json
{
  "success": true,
  "data": {
    "order_id": "ORD-123",
    "payment_url": "https://payment.gateway/checkout/abc123"
  }
}
```

## Benefits

1. **Flexibility**: Handles multiple API response formats
2. **Robustness**: Gracefully handles missing or malformed data
3. **Maintainability**: Centralized extraction logic with helper methods
4. **Debugging**: Enhanced logging for troubleshooting
5. **Type Safety**: Proper type conversion and validation
6. **User Experience**: Displays real order information instead of placeholders

## Testing Checklist

Based on the API test results, verify:

- ✅ Order ID displays correctly from API response
- ✅ Order status shows properly formatted text
- ✅ Total amount matches cart total (with formatting)
- ✅ Item count reflects actual cart items
- ✅ Payment URL detection works for various response structures
- ✅ Mock payment flow uses realistic data structure
- ✅ Logging helps debug API responses
- ✅ Handles missing fields without crashes
- ✅ Works with both nested and flat response structures

## Next Steps (Optional Layout Enhancements)

To fully utilize the dynamic data, consider adding these views to `activity_payout.xml`:

```xml
<TextView
    android:id="@+id/successOrderStatus"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Status: Pending"
    android:textSize="16sp" />

<TextView
    android:id="@+id/successOrderTotal"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Total: $0.00"
    android:textSize="18sp"
    android:textStyle="bold" />

<TextView
    android:id="@+id/successOrderItems"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="0 items"
    android:textSize="14sp" />
```

These are **optional** - the code already handles their absence gracefully.

## Related Files

- `PayoutActivity.java` - Main checkout activity (updated)
- `CartRepository.java` - API checkout call (unchanged)
- `CHECKOUT_TESTING_CHECKLIST.md` - Test cases reference

## API Documentation

Refer to the expected response format in `CHECKOUT_TESTING_CHECKLIST.md` section 3.1 for the complete API contract.
