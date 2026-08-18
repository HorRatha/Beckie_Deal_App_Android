# Coupon Feature UI Preview

## Checkout Screen with Coupon Section

```
╔══════════════════════════════════════════════════════════╗
║  ←  [Back]          Checkout                  [Profile]  ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  📍 Shipping Address                                     ║
║  ┌────────────────────────────────────────────────────┐ ║
║  │ 🏠  Home Address                          ✏️        │ ║
║  │     123 Tech Avenue, Silicon Valley, CA            │ ║
║  └────────────────────────────────────────────────────┘ ║
║                                                          ║
║  💳 Payment Method                                       ║
║  ┌────────────────────────────────────────────────────┐ ║
║  │ 💳  Credit Card                           ⦿         │ ║
║  │     **** **** **** 4582                            │ ║
║  └────────────────────────────────────────────────────┘ ║
║                                                          ║
║  🏷️  Promo Code                                         ║
║  ┌────────────────────────────────────────────────────┐ ║
║  │ 🏷️  [Enter coupon code...]          [ Apply ]      │ ║
║  │                                                     │ ║
║  │ ╔═════════════════════════════════════════════════╗│ ║
║  │ ║ ✅ WELCOME10              10% OFF            ✕ ║│ ║
║  │ ╚═════════════════════════════════════════════════╝│ ║
║  └────────────────────────────────────────────────────┘ ║
║                                                          ║
║  📋 Order Summary                                        ║
║  ┌────────────────────────────────────────────────────┐ ║
║  │                                                     │ ║
║  │  Subtotal                              $99.99      │ ║
║  │                                                     │ ║
║  │  Coupon Discount                      -$10.00      │ ║
║  │                                                     │ ║
║  │  Shipping Fee                          $15.00      │ ║
║  │  ─────────────────────────────────────────────     │ ║
║  │  Total                                $104.99      │ ║
║  │                                                     │ ║
║  └────────────────────────────────────────────────────┘ ║
║                                                          ║
╠══════════════════════════════════════════════════════════╣
║  ┌────────────────────────────────────────────────────┐ ║
║  │          🛒 Confirm Payment                         │ ║
║  └────────────────────────────────────────────────────┘ ║
╚══════════════════════════════════════════════════════════╝
```

---

## State Variations

### 1️⃣ Initial State (No Coupon Applied)
```
┌──────────────────────────────────────────────┐
│ 🏷️  Promo Code                               │
│ ┌──────────────────────────────────────────┐ │
│ │ 🏷️  [Enter coupon code]    [ Apply ]    │ │
│ └──────────────────────────────────────────┘ │
└──────────────────────────────────────────────┘

Order Summary:
  Subtotal            $99.99
  Shipping Fee        $15.00
  ──────────────────────────
  Total              $114.99
```

### 2️⃣ Coupon Applied Successfully
```
┌──────────────────────────────────────────────┐
│ 🏷️  Promo Code                               │
│ ┌──────────────────────────────────────────┐ │
│ │ 🏷️  [                ]      [ Apply ]    │ │
│ │                                          │ │
│ │ ╔══════════════════════════════════════╗ │ │
│ │ ║ ✅ WELCOME10      10% OFF          ✕ ║ │ │
│ │ ╚══════════════════════════════════════╝ │ │
│ └──────────────────────────────────────────┘ │
└──────────────────────────────────────────────┘

Order Summary:
  Subtotal            $99.99
  Coupon Discount    -$10.00  ⬅️ New!
  Shipping Fee        $15.00
  ──────────────────────────
  Total              $104.99
```

### 3️⃣ Invalid Coupon (Error State)
```
┌──────────────────────────────────────────────┐
│ 🏷️  Promo Code                               │
│ ┌──────────────────────────────────────────┐ │
│ │ 🏷️  [INVALID123]        [ Apply ]       │ │
│ └──────────────────────────────────────────┘ │
│                                              │
│ ❌ Toast: "Invalid coupon code"             │
└──────────────────────────────────────────────┘
```

### 4️⃣ Minimum Purchase Not Met
```
┌──────────────────────────────────────────────┐
│ 🏷️  Promo Code                               │
│ ┌──────────────────────────────────────────┐ │
│ │ 🏷️  [SAVE20]            [ Apply ]       │ │
│ └──────────────────────────────────────────┘ │
│                                              │
│ ⚠️ Toast: "Minimum purchase of $50.00       │
│           required"                          │
└──────────────────────────────────────────────┘
```

---

## Available Coupon Codes

### Try These Codes:

#### 🎁 **WELCOME10**
```
┌─────────────────────────────┐
│ 10% OFF                     │
│ New User Discount           │
│ Min Purchase: $0            │
│                             │
│ Example: $100 → Save $10    │
└─────────────────────────────┘
```

#### 💰 **SAVE20**
```
┌─────────────────────────────┐
│ $20 OFF                     │
│ Orders over $50             │
│ Min Purchase: $50           │
│                             │
│ Example: $75 → Save $20     │
└─────────────────────────────┘
```

#### 🔥 **DEAL50**
```
┌─────────────────────────────┐
│ 50% OFF                     │
│ Big Discount Deal           │
│ Min Purchase: $100          │
│                             │
│ Example: $150 → Save $75    │
└─────────────────────────────┘
```

#### 🚚 **FREESHIP**
```
┌─────────────────────────────┐
│ $15 OFF                     │
│ Free Shipping               │
│ Min Purchase: $30           │
│                             │
│ Example: Shipping $15 → $0  │
└─────────────────────────────┘
```

---

## Color Scheme

### Light Mode
```
┌─────────────────────────────────────┐
│ Background:      #F8FAFC (Gray)     │
│ Surface:         #FFFFFF (White)    │
│ Primary:         #F97316 (Orange)   │
│ Success BG:      #F0FDF4 (L.Green)  │
│ Success Text:    #22C55E (Green)    │
│ Error Text:      #EF4444 (Red)      │
│ Text Primary:    #1E293B (Dk.Gray)  │
│ Text Muted:      #94A3B8 (Lt.Gray)  │
│ Border:          #E2E8F0 (Lt.Gray)  │
└─────────────────────────────────────┘
```

### Dark Mode
```
┌─────────────────────────────────────┐
│ Background:      #0F1419 (Dk.Blue)  │
│ Surface:         #1A2332 (Dk.Slate) │
│ Primary:         #FB923C (Orange)   │
│ Success BG:      #0F2A22 (Dk.Green) │
│ Success Text:    #34D399 (Green)    │
│ Error Text:      #F87171 (Red)      │
│ Text Primary:    #F1F5F9 (White)    │
│ Text Muted:      #64748B (Gray)     │
│ Border:          #2D3B4E (Dk.Gray)  │
└─────────────────────────────────────┘
```

---

## Animation Flow

### Entrance Animation (On Page Load)
```
Frame 1: [Fade: 0%]   ⚪
Frame 2: [Fade: 50%]  ⚪↓
Frame 3: [Fade: 100%] ⚪↓↓
         └─ Slide up with fade
```

### Apply Coupon Animation
```
Step 1: User clicks "Apply"
        ↓
Step 2: Validation (instant)
        ↓
Step 3: Success card slides down
        ╔═══════════════════╗
        ║ ✅ CODE  -$10  ✕ ║  ← Fade + Slide
        ╚═══════════════════╝
        ↓
Step 4: Discount row appears
        Coupon Discount  -$10.00  ← Fade + Slide
        ↓
Step 5: Total updates (instant)
```

### Remove Coupon Animation
```
Step 1: User clicks ✕
        ↓
Step 2: Success card fades out
        ║ ✅ CODE  -$10  ✕ ║ → [Fade out]
        ↓
Step 3: Discount row fades out
        Coupon Discount  -$10.00 → [Fade out]
        ↓
Step 4: Total updates
```

---

## User Interaction Flow

```
┌─────────────────────────────────────────────┐
│           USER JOURNEY                      │
└─────────────────────────────────────────────┘

1. Add items to cart
   └─→ Cart: $99.99
   
2. Proceed to Checkout
   └─→ PayoutActivity
   
3. View Promo Code section
   └─→ See input field & Apply button
   
4. Enter coupon code: "WELCOME10"
   └─→ Input: [WELCOME10]
   
5. Click "Apply"
   └─→ Validation...
   
6. ✅ Success!
   ├─→ Toast: "Coupon applied! You saved $10.00"
   ├─→ Green card appears with code
   ├─→ Discount shows in summary: -$10.00
   └─→ Total updates: $99.99 → $104.99
   
7. (Optional) Remove coupon
   └─→ Click ✕ button
       ├─→ Card disappears
       ├─→ Discount removed
       └─→ Total: $114.99
   
8. Confirm Payment
   └─→ Order saved with discount
       └─→ Coupon cleared for next use
```

---

## Responsive Behavior

### Portrait Mode (Default)
```
┌────────────────────┐
│  Promo Code        │
│ ┌────────────────┐ │
│ │🏷️ [Code] [Apply]│ │
│ └────────────────┘ │
└────────────────────┘
```

### Landscape Mode (Wider)
```
┌──────────────────────────────────────────┐
│  Promo Code                              │
│ ┌──────────────────────────────────────┐ │
│ │ 🏷️  [Enter coupon...]     [ Apply ] │ │
│ └──────────────────────────────────────┘ │
└──────────────────────────────────────────┘
```

---

## Edge Cases Handled

✅ **Empty input** - Button press shows error  
✅ **Whitespace only** - Trimmed and validated  
✅ **Lowercase codes** - Auto-converted to uppercase  
✅ **Multiple applications** - Replaces previous coupon  
✅ **Cart updates** - Coupon revalidated on amount change  
✅ **Payment success** - Coupon automatically cleared  
✅ **Navigation** - Coupon state persists in activity  
✅ **Discount > Subtotal** - Capped at subtotal amount  

---

## Accessibility Features

♿ **Content Descriptions** - All icons have descriptions  
♿ **Touch Targets** - Minimum 48dp touch areas  
♿ **Color Contrast** - WCAG AA compliant  
♿ **Text Size** - Uses scalable dimensions  
♿ **Focus Order** - Logical tab navigation  
♿ **Screen Readers** - Proper labels and announcements  

---

## Summary

The coupon feature provides a **beautiful**, **functional**, and **user-friendly** way to apply discounts. The design seamlessly integrates with the existing Beckie-Deal UI, maintaining visual consistency while adding significant value to the shopping experience.

**Visual Highlights:**
- 🎨 Clean, modern design
- ✨ Smooth animations
- 🟢 Clear success indicators
- 🔴 Helpful error messages
- 📱 Responsive layout
- 🌓 Light & dark theme support

**Try it out in your app!**
