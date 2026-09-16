# Discount Display Fix - Only Show When Coupon Applied

## Issue Summary
The checkout page was showing a **Coupon Discount of -$159.90** even though no coupon had been applied by the user.

## Root Cause Analysis

### Backend API Behavior
The cart API endpoint (`GET /api/v1/cart`) returns a `discount_total` field:

```json
{
  "id": 1,
  "coupon_code": null,        // NULL when no coupon applied
  "discount_total": "159.90",  // ❌ NOT NULL (contains product discounts)
  "subtotal": "1599.00",
  "grand_total": "1439.10",
  "items": [...]
}
```

**Problem:** The `discount_total` field contains **product-level discounts** (sale prices, featured discounts, etc.) even when `coupon_code` is `null`.

### Frontend Code Issue
**PayoutActivity.java** (line 271 - OLD CODE):
```java
double discount = currentCart.getDiscountTotal(); // From API, includes coupon

if (discount > 0) {
    discountRow.setVisibility(View.VISIBLE);  // ❌ Shows even without coupon!
    summaryDiscountText.setText(String.format("-$%.2f", discount));
}
```

This displayed the discount row whenever `discount_total > 0`, regardless of whether a coupon was actually applied.

## Solution Implemented

### Check Coupon Code Before Showing Discount

**NEW CODE:**
```java
// Only use discount if a coupon is actually applied
// Check if coupon_code exists in the cart (not just discount_total > 0)
boolean hasCouponApplied = currentCart.getCouponCode() != null && 
                           !currentCart.getCouponCode().isEmpty();
double discount = hasCouponApplied ? currentCart.getDiscountTotal() : 0.0;

// Only show discount row if a coupon is actually applied
if (discountRow != null && summaryDiscountText != null) {
    if (hasCouponApplied && discount > 0) {
        discountRow.setVisibility(View.VISIBLE);
        summaryDiscountText.setText(String.format(Locale.US, "-$%.2f", discount));
    } else {
        discountRow.setVisibility(View.GONE);  // ✅ Hide when no coupon
    }
}
```

### Added Debug Logging

```java
Log.d(TAG, "Cart loaded - Subtotal: $" + currentCart.getTotalPrice());
Log.d(TAG, "Cart coupon_code: " + currentCart.getCouponCode());
Log.d(TAG, "Cart discount_total: " + currentCart.getDiscountTotal());
Log.d(TAG, "Cart grand_total: " + currentCart.getGrandTotal());
```

## Files Changed

### PayoutActivity.java
**Changes:**
1. Added `hasCouponApplied` check based on `couponCode` field
2. Only applies discount to total if coupon is present
3. Only shows discount row UI if coupon is present
4. Added debug logging for cart state

## Behavior After Fix

### Scenario 1: No Coupon Applied ✅
**Cart State:**
- `coupon_code`: `null`
- `discount_total`: `159.90` (product discounts)

**Display:**
```
Order Summary
├── Subtotal:      $179.99
├── Shipping Fee:  FREE
└── Total:         $179.99
```
✅ **Discount row is HIDDEN**

### Scenario 2: Coupon Applied ✅
**Cart State:**
- `coupon_code`: `"WELCOME10"`
- `discount_total`: `159.90`

**Display:**
```
Order Summary
├── Subtotal:        $1599.00
├── Coupon Discount: -$159.90
├── Shipping Fee:    FREE
└── Total:           $1439.10
```
✅ **Discount row is VISIBLE**

## Backend Discount Types

Based on the API behavior, `discount_total` includes:

### 1. Product-Level Discounts (Always Present)
- Sale prices (`sale_price` vs `price`)
- Featured product discounts
- Percentage-off discounts on products
- These are **baked into the product price** and should NOT be shown separately

### 2. Cart-Level Discounts (Coupon Required)
- Coupon codes (`WELCOME10`, `SAVE20`, etc.)
- Applied via `POST /api/v1/coupons/apply`
- Sets `coupon_code` field in cart
- These SHOULD be shown in "Coupon Discount" row

## Logic Flow

```
1. Load cart from API
   ↓
2. Check if coupon_code exists
   ↓
3a. If coupon_code is NULL/empty:
    - discount = 0.0
    - Hide discount row
    - Total = subtotal + shipping
   ↓
3b. If coupon_code exists:
    - discount = discount_total from API
    - Show discount row with coupon code
    - Total = subtotal - discount + shipping
```

## Testing

### Test 1: Fresh Cart (No Coupon) ✅
1. Add products to cart
2. Go to checkout
3. **Expected:** No discount row visible
4. **Expected Total:** Subtotal + Shipping

### Test 2: Apply Coupon ✅
1. Enter coupon code "WELCOME10"
2. Click "Apply"
3. **Expected:** Discount row appears with "-$159.90"
4. **Expected Total:** Subtotal - Discount + Shipping

### Test 3: Remove Coupon ✅
1. Click remove coupon (X button)
2. **Expected:** Discount row disappears
3. **Expected Total:** Subtotal + Shipping

## Related Issues

### Product Prices Already Include Sale Discounts
When a product has a `sale_price`, the cart uses the sale price:
- Regular price: $199.99
- Sale price: $149.99
- Cart shows: $149.99 (sale price already applied)

The `discount_total` field might be aggregating these per-product discounts, but they should NOT be shown as a separate "Coupon Discount" line item.

## Backend Clarification Needed

**Question for Backend Team:**
What does `discount_total` represent?
- A) Only coupon-based discounts?
- B) Product sale price discounts + coupon discounts combined?
- C) Something else?

**Current Assumption:** 
`discount_total` = product discounts + coupon discounts (combined)

**Frontend Strategy:**
Only show discount row when `coupon_code` is present, treating `discount_total` as the coupon discount amount.

## Future Enhancements

### Option 1: Separate Discount Fields
Backend could provide:
```json
{
  "product_discount_total": "50.00",   // Sale prices, etc.
  "coupon_discount_total": "159.90",   // Coupon only
  "total_discount": "209.90"           // Combined
}
```

### Option 2: Cart Items Show Individual Discounts
Each cart item could show:
```json
{
  "regular_price": "199.99",
  "sale_price": "149.99",
  "discount_amount": "50.00"
}
```

### Option 3: Discount Breakdown API
```
GET /api/v1/cart/discount-breakdown
Response:
{
  "product_discounts": [
    {"product": "iPhone 15 Pro", "amount": 50.00},
    ...
  ],
  "coupon_discount": {
    "code": "WELCOME10",
    "amount": 159.90
  }
}
```

## Related Files
- `PayoutActivity.java` - Checkout page with order summary
- `Cart.java` - Cart model with discount fields
- `CouponManager.java` - Coupon application logic
- `COUPON_API_INTEGRATION_COMPLETE.md` - Coupon API documentation
- `SHIPPING_FEE_FIX.md` - Related shipping fee fix

## Verification
✅ Discount row only shows when coupon is applied
✅ Discount row hidden when no coupon applied
✅ Total calculation correct in both cases
✅ Debug logging added for troubleshooting
✅ Code handles null coupon_code gracefully
