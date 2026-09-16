# Shipping Fee Fix - Dynamic Calculation

## Issue Summary
The product detail page showed "Free shipping on orders over $50", but the checkout (PayoutActivity) was charging a hardcoded $15.00 shipping fee regardless of cart total.

## Root Cause
1. **ProductDetailActivity.java** (lines 308, 349): Displayed "Free shipping on orders over $50" in spec rows
2. **ProductMapper.java** (lines 147-158): Generated "Free shipping" tag for featured products or products >= $99
3. **PayoutActivity.java** (line 272): **HARDCODED $15.00 shipping fee** - did not check cart total
4. **OrderDetailActivity.java** (line 293): **HARDCODED $15.00 subtraction** - reverse calculated incorrectly

## Files Changed

### 1. PayoutActivity.java
**Changes:**
- Replaced hardcoded `double shipping = 15.00;` with dynamic `calculateShippingFee(subtotal)`
- Added new method `calculateShippingFee(double cartTotal)` that:
  - Returns `$0.00` (FREE) if cart total >= $50.00
  - Returns `$15.00` (Standard Shipping) if cart total < $50.00
- Updated UI to show "FREE" in green when shipping is $0.00
- Added logging to track which shipping tier is applied

**Logic:**
```java
private double calculateShippingFee(double cartTotal) {
    final double FREE_SHIPPING_THRESHOLD = 50.00;
    
    if (cartTotal >= FREE_SHIPPING_THRESHOLD) {
        return 0.0;  // FREE
    } else {
        return 15.00;  // Standard Shipping
    }
}
```

### 2. activity_payout.xml
**Changes:**
- Added `android:id="@+id/summaryShipping"` to the shipping fee TextView
- Allows dynamic updates from Java code

### 3. OrderDetailActivity.java
**Changes:**
- Replaced hardcoded subtotal calculation with dynamic logic
- Added reverse-engineering logic to determine if shipping was applied to historical orders
- If `subtotal >= $50`, assumes shipping was FREE (0.00)
- If `subtotal < $50`, assumes shipping was $15.00
- Updates shipping TextView to show "FREE" or dollar amount

### 4. activity_order_detail.xml
**Changes:**
- Added `android:id="@+id/detailShipping"` to the shipping fee TextView
- Allows dynamic updates from Java code

## Shipping Fee Rules (Now Consistent Across App)

| Cart Total | Shipping Fee | Display |
|------------|--------------|---------|
| < $50.00   | $15.00      | "$15.00" |
| >= $50.00  | $0.00       | "FREE" (green) |

## Testing Scenarios

### Scenario 1: Cart Total < $50
- **Cart:** 1 item @ $30.00
- **Expected Shipping:** $15.00
- **Total:** $45.00

### Scenario 2: Cart Total >= $50 (Exactly at threshold)
- **Cart:** 2 items @ $25.00 each = $50.00
- **Expected Shipping:** FREE ($0.00)
- **Total:** $50.00

### Scenario 3: Cart Total >= $50 (Above threshold)
- **Cart:** 3 items @ $30.00 each = $90.00
- **Expected Shipping:** FREE ($0.00)
- **Total:** $90.00

### Scenario 4: With Coupon Applied
- **Cart:** $60.00 - $10.00 coupon = $50.00
- **Expected Shipping:** FREE (calculated on subtotal BEFORE shipping)
- **Total:** $50.00

## Backend Integration Notes

Currently using client-side logic. When backend shipping methods API is available:

1. **Fetch shipping methods** from API endpoint (from `BACKEND_BLOCKER_SHIPPING_METHODS.md`):
   ```
   GET /api/shipping-methods
   ```

2. **Select appropriate method** based on cart total:
   - Standard Shipping: $5.00 (5-7 days)
   - Express Shipping: $15.00 (2-3 days)
   - Free Shipping: $0.00 (orders > $50)

3. **Send selected method** in checkout request:
   ```json
   {
     "cart_id": "123",
     "shipping_method_id": 3,
     ...
   }
   ```

## Product Detail Page Consistency

The product detail page shows:
- **Spec Row:** "Shipping: Free shipping on orders over $50"
- **Free Items Tag:** "Free shipping" (for featured products or products >= $99)

This is now **consistent** with checkout behavior.

## Related Files

- `BACKEND_BLOCKER_SHIPPING_METHODS.md` - Backend shipping methods spec
- `CHECKOUT_TESTING_CHECKLIST.md` - Checkout testing documentation
- `ProductMapper.java` - Product-level shipping tag generation
- `ProductDetailActivity.java` - Product detail display

## Verification

✅ **PayoutActivity**: Now calculates shipping dynamically based on cart total
✅ **OrderDetailActivity**: Now reverse-calculates shipping based on order total
✅ **Layout files**: Added IDs for dynamic TextView updates
✅ **Logic consistency**: $50 threshold matches product detail spec row
✅ **UI feedback**: Shows "FREE" in green when shipping is $0.00

## Future Enhancements

1. Add shipping method selector (Standard, Express, Free)
2. Integrate with backend shipping methods API
3. Store shipping fee in Order model for historical accuracy
4. Add estimated delivery dates based on shipping method
5. Support location-based shipping (different rates per region)
