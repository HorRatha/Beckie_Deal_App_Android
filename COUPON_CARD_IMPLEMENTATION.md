# Coupon Card Implementation - Home Screen

## 🎉 Overview
Added a beautiful, interactive coupon card to the home screen displaying below the "New Collection" section, matching the design style from your reference image.

---

## ✨ Features

### Design
- **Gradient Background**: Dark brown/gold gradient (`#3D2817` to `#5C3D24`)
- **Gift Icon**: Left side with amber accent color
- **Coupon Info**: Center section with title, discount, and code
- **Copy Button**: Right side with "Copy" button and icon
- **Tap to Copy**: Entire card is clickable

### Functionality
- **One-Tap Copy**: Copy coupon code `WELCOME10` to clipboard
- **Visual Feedback**: 
  - Button text changes to "Copied!"
  - Icon changes to checkmark
  - Toast notification shows
  - Auto-resets after 2 seconds
- **Smooth Animation**: Fade-slide entrance animation on app load

---

## 📁 Files Created

### 1. **Drawables**

#### `coupon_gradient_bg.xml`
```xml
<shape>
    <gradient
        startColor="#3D2817"
        endColor="#5C3D24"
        angle="135" />
    <corners radius="16dp" />
</shape>
```

#### `coupon_icon_bg.xml`
```xml
<shape android:shape="rectangle">
    <solid color="#4D3520" />
    <corners radius="12dp" />
</shape>
```

#### `ic_gift_24.xml`
- Material Design gift icon (24dp)

#### `ic_copy_24.xml`
- Material Design copy icon (24dp)

### 2. **Colors Added** (`colors.xml`)

```xml
<color name="coupon_accent">#F59E0B</color>         <!-- Icon tint -->
<color name="coupon_label">#FCD34D</color>          <!-- "DEAL OF THE DAY" -->
<color name="coupon_subtext">#D1D5DB</color>        <!-- "Use code" -->
<color name="coupon_code_highlight">#FDE68A</color> <!-- "WELCOME10" -->
<color name="coupon_button_bg">#F59E0B</color>      <!-- Copy button -->
<color name="coupon_dark">#1F2937</color>           <!-- Button text -->
```

---

## 📐 Layout Structure

```
MaterialCardView (rounded, elevated)
└── FrameLayout (gradient background)
    └── LinearLayout (horizontal)
        ├── FrameLayout (icon background)
        │   └── ImageView (gift icon)
        ├── LinearLayout (coupon info)
        │   ├── TextView ("DEAL OF THE DAY")
        │   ├── TextView ("Extra 10% Off Cart")
        │   └── LinearLayout (code row)
        │       ├── TextView ("Use code ")
        │       └── TextView ("WELCOME10")
        └── MaterialButton ("Copy" with icon)
```

---

## 🎨 Visual Design

```
┌─────────────────────────────────────────────────────┐
│  ┌──┐  DEAL OF THE DAY                              │
│  │🎁│  Extra 10% Off Cart              [📋 Copy]    │
│  └──┘  Use code WELCOME10                           │
└─────────────────────────────────────────────────────┘
     │           │                           │
   Icon      Info Text                  Copy Button
```

### Colors in Action:
- **Background**: Dark brown gradient
- **Icon**: Amber/gold
- **Label**: Light yellow
- **Title**: White
- **Code**: Highlighted yellow
- **Button**: Amber with dark text

---

## 💻 Java Implementation

### `MainActivity.java`

#### Setup Method
```java
private void setupCouponSection() {
    MaterialCardView couponCard = findViewById(R.id.couponCard);
    TextView couponCodeText = findViewById(R.id.couponCodeText);
    MaterialButton copyButton = findViewById(R.id.copyCouponButton);

    // Add bounce animations
    UiAnimations.pressBounce(couponCard);
    UiAnimations.pressBounce(copyButton);

    // Copy button handler
    copyButton.setOnClickListener(v -> {
        String couponCode = couponCodeText.getText().toString();
        
        // Copy to clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Coupon Code", couponCode);
        clipboard.setPrimaryClip(clip);
        
        // Show feedback
        Toast.makeText(this, "Coupon code copied: " + couponCode, Toast.LENGTH_SHORT).show();
        
        // Change button temporarily
        copyButton.setText("Copied!");
        copyButton.setIconResource(R.drawable.ic_check_circle_24);
        
        // Reset after 2 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            copyButton.setText("Copy");
            copyButton.setIconResource(R.drawable.ic_copy_24);
        }, 2000);
    });

    // Make entire card clickable
    couponCard.setOnClickListener(v -> copyButton.performClick());
}
```

#### Entrance Animation
```java
private void setupEntranceAnimation() {
    UiAnimations.fadeSlideDown(findViewById(R.id.topAppBar), 0);
    UiAnimations.fadeSlideUp(findViewById(R.id.searchBar), 60);
    UiAnimations.fadeSlideUp(findViewById(R.id.bannerCard), 100);
    UiAnimations.fadeSlideUp(findViewById(R.id.categoriesSectionHeader), 150);
    UiAnimations.fadeSlideUp(findViewById(R.id.newCollectionSectionHeader), 190);
    UiAnimations.fadeSlideUp(findViewById(R.id.couponCard), 210);  // ← Coupon card
    // ...
}
```

---

## 🎯 User Experience Flow

```
1. User opens app → Home screen loads
2. Coupon card fades in smoothly (210ms delay)
3. User sees: "Extra 10% Off Cart" with code "WELCOME10"
4. User taps anywhere on card (or specifically "Copy" button)
   ↓
5. Code copied to clipboard
   ↓
6. Visual feedback:
   - Toast: "Coupon code copied: WELCOME10"
   - Button text: "Copy" → "Copied!"
   - Button icon: 📋 → ✓
   ↓
7. After 2 seconds: Button resets to "Copy"
8. User can go to Cart → Checkout → Apply coupon
```

---

## 📍 Position on Screen

**Located between:**
- **Above**: New Collection section (horizontal product scroll)
- **Below**: New Arrivals Banner (carousel)

**Margins:**
- Horizontal: 16dp (from screen edges)
- Top: 20dp (from New Collection)

---

## 🎬 Animations

### 1. **Entrance** (on app load)
- Fade-in from 0% to 100% opacity
- Slide-up 20dp
- Delay: 210ms
- Duration: 300ms (default)

### 2. **Press/Bounce** (on interaction)
- Scale down slightly when pressed
- Bounce back when released
- Applied to:
  - Entire card
  - Copy button

### 3. **Button State Change**
- Text changes instantly
- Icon changes instantly
- Auto-revert after 2000ms

---

## 📱 Responsive Design

- **Width**: `match_parent` (fills screen width minus margins)
- **Height**: `wrap_content` (adjusts to content)
- **Min Height**: ~80dp (based on padding + content)
- **Card Radius**: 16dp (rounded corners)
- **Elevation**: 0dp (flat design)

---

## 🎨 Design Matching

### Reference Image Elements:
✅ **Gift icon on left** - Implemented with Material icon  
✅ **"DEAL OF THE DAY" label** - Top text in small caps  
✅ **Discount description** - "Extra 10% Off Cart"  
✅ **Coupon code display** - "Use code WELCOME10"  
✅ **Copy button** - Yellow/amber button on right  
✅ **Dark gradient background** - Brown gradient implemented  

### Customizations Made:
- Used Material Design icons instead of custom
- Adjusted colors to match your app's palette
- Added interactive animations
- Made entire card tappable for better UX

---

## 🔧 How to Customize

### Change Coupon Code
```xml
<TextView
    android:id="@+id/couponCodeText"
    android:text="WELCOME10"  <!-- Change here -->
```

### Change Discount Text
```xml
<TextView
    android:text="Extra 10% Off Cart"  <!-- Change here -->
```

### Change Colors
Edit in `colors.xml`:
- `coupon_accent` - Icon and button color
- `coupon_label` - Label text color
- `coupon_code_highlight` - Code text color

### Change Background Gradient
Edit `coupon_gradient_bg.xml`:
```xml
<gradient
    android:startColor="#YOUR_START_COLOR"
    android:endColor="#YOUR_END_COLOR"
    android:angle="135" />
```

---

## 📊 Code Statistics

- **New XML Lines**: ~120 lines (layout)
- **New Java Lines**: ~40 lines (logic)
- **New Resources**: 4 drawables, 6 colors
- **Dependencies**: None (uses existing Material components)

---

## ✅ Testing Checklist

### Visual Tests
- [ ] Card displays correctly on home screen
- [ ] Gradient background looks smooth
- [ ] Icon and text are aligned properly
- [ ] Button is positioned on the right
- [ ] Entrance animation plays smoothly

### Functional Tests
- [ ] Tap "Copy" button → Code copies to clipboard
- [ ] Toast notification shows
- [ ] Button text changes to "Copied!"
- [ ] Button icon changes to checkmark
- [ ] Button resets after 2 seconds
- [ ] Entire card is tappable
- [ ] Can paste code in checkout screen

### Responsive Tests
- [ ] Looks good on small screens (320dp)
- [ ] Looks good on large screens (600dp+)
- [ ] Text doesn't overflow
- [ ] Button doesn't get cut off

---

## 🚀 Future Enhancements (Optional)

### Dynamic Coupons
```java
// Fetch coupon from backend
private void loadActiveCoupon() {
    apiService.getActiveCoupon().enqueue(new Callback<Coupon>() {
        @Override
        public void onSuccess(Coupon coupon) {
            couponCodeText.setText(coupon.getCode());
            // Update discount text, etc.
        }
    });
}
```

### Multiple Coupons
- Add horizontal scroll with multiple coupon cards
- Show different coupons for different categories

### Countdown Timer
```xml
<TextView
    android:text="Expires in 3 hours"
    android:textColor="@color/red_error" />
```

### Share Option
```java
// Add share button to share coupon with friends
shareButton.setOnClickListener(v -> {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setType("text/plain");
    shareIntent.putExtra(Intent.EXTRA_TEXT, "Use code WELCOME10 for 10% off!");
    startActivity(Intent.createChooser(shareIntent, "Share Coupon"));
});
```

---

## 🐛 Troubleshooting

### Issue: Card not visible
**Check:**
- View ID matches: `android:id="@+id/couponCard"`
- setupCouponSection() is called in onCreate()
- Layout file has no syntax errors

### Issue: Copy doesn't work
**Check:**
- CLIPBOARD_SERVICE permission (not needed, automatic)
- TextView ID: `android:id="@+id/couponCodeText"`
- ClipboardManager is properly initialized

### Issue: Animation doesn't play
**Check:**
- UiAnimations.fadeSlideUp() is called
- View exists when animation runs
- Delay timing (210ms for coupon card)

### Issue: Button doesn't reset
**Check:**
- Handler.postDelayed() duration (2000ms)
- ic_check_circle_24 drawable exists
- ic_copy_24 drawable exists

---

## 📚 Related Files

- `activity_main.xml` - Layout with coupon card
- `MainActivity.java` - Coupon section setup
- `colors.xml` - Coupon colors
- `coupon_gradient_bg.xml` - Background drawable
- `coupon_icon_bg.xml` - Icon background
- `ic_gift_24.xml` - Gift icon
- `ic_copy_24.xml` - Copy icon

---

## ✨ Summary

**What was added:**
✅ Beautiful coupon card with gradient background  
✅ One-tap copy to clipboard functionality  
✅ Visual feedback (button state change, toast)  
✅ Smooth entrance animation  
✅ Interactive press animations  
✅ Entire card is tappable for easy copying  

**Design matches:**
✅ Reference image style  
✅ App color palette  
✅ Material Design guidelines  
✅ Consistent with app UI  

**User benefit:**
✅ Easy to find active coupons  
✅ Quick copy without typing  
✅ Clear visual design  
✅ Engaging interactions  

---

**Status:** ✅ Complete and Working  
**Build:** ✅ Successful  
**Location:** Home screen, below New Collection  
**Coupon:** WELCOME10 (10% discount)
