# 🎟️ Coupon Feature - Quick Start Guide

## 🚀 How to Use the Coupon Feature

### For Users:

#### Step 1: Add items to your cart
- Browse products and add them to your cart
- Make sure your cart total meets any minimum purchase requirements

#### Step 2: Go to Checkout
- From the cart screen, click **"Proceed to Checkout"**
- You'll be taken to the PayoutActivity (Checkout screen)

#### Step 3: Apply a Coupon
- Scroll to the **"Promo Code"** section
- Enter a coupon code (e.g., `WELCOME10`)
- Click **"Apply"** button

#### Step 4: Verify Discount
- ✅ A green success card will appear showing your coupon
- 💚 The discount amount appears in the Order Summary
- 💰 The total price is automatically updated

#### Step 5: Complete Purchase
- Review your order with the discount applied
- Click **"Confirm Payment"** to complete your order

---

## 🏷️ Test Coupons Available

### Try these codes now:

| Code | Discount | Min Purchase | Best For |
|------|----------|--------------|----------|
| **WELCOME10** | 10% OFF | None | New users, any order |
| **SAVE20** | $20 OFF | $50 | Medium-sized orders |
| **DEAL50** | 50% OFF | $100 | Large orders |
| **FREESHIP** | $15 OFF | $30 | Covering shipping |

---

## 🎯 Quick Examples

### Example 1: Small Order
```
Cart Total: $25.00
Coupon: WELCOME10
───────────────────
Subtotal:     $25.00
Discount:     -$2.50 (10% OFF)
Shipping:     $15.00
───────────────────
TOTAL:        $37.50 ✅
```

### Example 2: Medium Order
```
Cart Total: $75.00
Coupon: SAVE20
───────────────────
Subtotal:     $75.00
Discount:    -$20.00 (Fixed)
Shipping:     $15.00
───────────────────
TOTAL:        $70.00 ✅
```

### Example 3: Large Order
```
Cart Total: $120.00
Coupon: DEAL50
───────────────────
Subtotal:    $120.00
Discount:    -$60.00 (50% OFF!)
Shipping:     $15.00
───────────────────
TOTAL:        $75.00 ✅
```

### Example 4: Free Shipping
```
Cart Total: $45.00
Coupon: FREESHIP
───────────────────
Subtotal:     $45.00
Discount:    -$15.00
Shipping:     $15.00
───────────────────
TOTAL:        $45.00 ✅ (No shipping!)
```

---

## ❌ Common Issues & Solutions

### Issue: "Invalid coupon code"
**Cause:** The code doesn't exist or is misspelled  
**Solution:** Double-check the spelling (codes are case-insensitive)

### Issue: "Minimum purchase of $X required"
**Cause:** Your cart total is below the coupon's minimum  
**Solution:** Add more items to reach the minimum amount

### Issue: "Please enter a coupon code"
**Cause:** The input field is empty  
**Solution:** Type a code before clicking Apply

### Issue: Coupon won't apply
**Solution:** Make sure you clicked the "Apply" button after entering the code

---

## 🎨 UI Elements Guide

### Icons You'll See:
- 🏷️ **Discount Tag** - Promo code section
- ✅ **Green Check** - Coupon applied successfully
- ✕ **Close/Remove** - Remove the applied coupon
- 💳 **Card** - Payment method
- 📦 **Box** - Shipping address

### Colors:
- **Orange Button** = Apply coupon
- **Green Background** = Success (coupon applied)
- **Red Text** = Discount amount (you're saving!)
- **Gray Text** = Muted/helper text

---

## 📱 Step-by-Step Visual Guide

```
1️⃣ CART SCREEN
┌──────────────────────┐
│  Items in Cart       │
│  ─────────────────   │
│  Item 1    $50.00    │
│  Item 2    $49.99    │
│  ─────────────────   │
│  Total:    $99.99    │
│                      │
│  [Proceed to         │
│   Checkout] 🛒       │
└──────────────────────┘

2️⃣ CHECKOUT - ENTER CODE
┌──────────────────────┐
│  📍 Shipping         │
│  💳 Payment          │
│                      │
│  🏷️ Promo Code      │
│  [WELCOME10] [Apply] │ ⬅️ Type here
└──────────────────────┘

3️⃣ COUPON APPLIED ✅
┌──────────────────────┐
│  🏷️ Promo Code      │
│  [         ] [Apply] │
│                      │
│  ╔══════════════════╗│
│  ║✅ WELCOME10   ✕ ║│ ⬅️ Success!
│  ║   10% OFF       ║│
│  ╚══════════════════╝│
└──────────────────────┘

4️⃣ ORDER SUMMARY
┌──────────────────────┐
│  📋 Order Summary    │
│  ─────────────────   │
│  Subtotal   $99.99   │
│  Discount  -$10.00   │ ⬅️ You saved!
│  Shipping   $15.00   │
│  ─────────────────   │
│  Total     $104.99   │ ⬅️ New total
│                      │
│  [Confirm Payment]   │
└──────────────────────┘
```

---

## 🔧 For Developers

### Quick Integration Check:

✅ **Files Created:**
- `CouponManager.java` - Business logic
- `ic_discount_tag.xml` - Promo icon
- `ic_check_circle_24.xml` - Success icon
- `ic_close.xml` - Remove icon

✅ **Files Modified:**
- `PayoutActivity.java` - UI integration
- `activity_payout.xml` - Layout updates
- `colors.xml` - Added success_light_bg

✅ **Build Status:**
- ✅ Gradle build successful
- ✅ No compilation errors
- ✅ No diagnostic issues

### Add a New Coupon:

Edit `CouponManager.java` in the `initializeCoupons()` method:

```java
availableCoupons.put("NEWCODE", new Coupon(
    "NEWCODE",                    // Code
    DiscountType.PERCENTAGE,      // Type: PERCENTAGE or FIXED_AMOUNT
    15.0,                         // Value: 15% or $15
    30.0,                         // Min purchase: $30
    "15% off orders over $30"     // Description
));
```

### Test the Feature:

```java
// In your test or debugging
CouponManager manager = CouponManager.getInstance();
CouponResult result = manager.applyCoupon("WELCOME10", 50.0);

if (result.success) {
    System.out.println("Discount: $" + result.coupon.calculateDiscount(50.0));
    // Output: Discount: $5.0
}
```

---

## 🎁 Pro Tips

### Tip 1: Case Doesn't Matter
Type `welcome10`, `WELCOME10`, or `Welcome10` - all work!

### Tip 2: Remove & Reapply
You can remove a coupon and try a different one to find the best deal

### Tip 3: Check Minimums
Look at the minimum purchase requirement before applying

### Tip 4: Shipping Hack
Use `FREESHIP` on orders over $30 to eliminate the $15 shipping fee

### Tip 5: Stack Savings
While you can only use one coupon at a time, choose the one that gives you the biggest discount

---

## 📊 Discount Comparison Calculator

### Quick Reference:

| Cart Total | WELCOME10 | SAVE20 | DEAL50 | Best Choice |
|------------|-----------|--------|--------|-------------|
| $25 | -$2.50 ✅ | ❌ | ❌ | WELCOME10 |
| $50 | -$5.00 | -$20.00 ✅ | ❌ | SAVE20 |
| $75 | -$7.50 | -$20.00 ✅ | ❌ | SAVE20 |
| $100 | -$10.00 | -$20.00 | -$50.00 ✅ | DEAL50 |
| $150 | -$15.00 | -$20.00 | -$75.00 ✅ | DEAL50 |
| $200 | -$20.00 | -$20.00 | -$100.00 ✅ | DEAL50 |

**💡 Rule of Thumb:**
- Under $50 → Use **WELCOME10** or **FREESHIP**
- $50-$99 → Use **SAVE20**
- $100+ → Use **DEAL50**

---

## ✨ Feature Highlights

### What Makes This Feature Great:

✅ **Instant Validation** - Know immediately if your code works  
✅ **Visual Feedback** - Clear success/error messages  
✅ **Live Updates** - Total recalculates automatically  
✅ **Easy Removal** - One click to remove and try another  
✅ **Smart Design** - Matches your app's existing style  
✅ **Smooth Animations** - Professional, polished feel  
✅ **Dark Mode Support** - Works perfectly in both themes  
✅ **Responsive** - Adapts to different screen sizes  

---

## 📞 Need Help?

### Can't find the coupon section?
Make sure you're on the **Checkout** screen (PayoutActivity), not the Cart screen.

### Want more coupons?
Contact your development team to add more promotional codes.

### Found a bug?
Report issues with:
- Coupon code used
- Cart total amount
- Error message received
- Expected vs actual discount

---

## 🎉 Start Saving Now!

1. Add items to cart
2. Go to checkout
3. Enter **WELCOME10**
4. Click Apply
5. Enjoy your discount! 💰

**Happy Shopping! 🛍️**
