# Promotion Banner - Visual Implementation Guide

## Final UI Layout

```
╔═══════════════════════════════════════════════════════════╗
║                   PROMOTION BANNER CARD                   ║
╠═══════════════════════════════════════════════════════════╣
║                                                           ║
║  ┌─ GLASS CARD ─┐           ┌─ GLASS CARD ─┐            ║
║  │ COPY CODE    │           │ EXPIRES IN   │            ║
║  │              │           │              │            ║
║  │ SUMMER30     │           │ 23d 14h      │            ║
║  └──────────────┘           └──────────────┘            ║
║                                                           ║
║            ▲                                              ║
║       [Background: Either Beautiful Image with           ║
║        Glass Overlay OR Professional Gradient]           ║
║            ▼                                              ║
║                                                           ║
║                 30% OFF                                  ║
║              Summer Sale                                 ║
║           Min order $150                                 ║
║                                                           ║
║                                                           ║
╚═══════════════════════════════════════════════════════════╝
```

---

## Detailed Component Breakdown

### 1. BACKGROUND LAYER

#### With Banner Image:
```xml
<FrameLayout background="@drawable/promotion_gradient_fallback">
    <ImageView scaleType="centerCrop" alpha="0.85" />
    <View background="@drawable/promotion_glass_morphism" />
</FrameLayout>
```

**Visual Effect:**
```
┌─────────────────────────────────────────┐
│  [Beautiful promotional image]          │  ← alpha="0.85" (85% visible)
│  + Frosted glass overlay                │  ← Creates elegant glass effect
│  Result: Slightly muted image with      │
│  semi-transparent frosted appearance    │
└─────────────────────────────────────────┘
```

#### Without Banner Image:
```
┌─────────────────────────────────────────┐
│  Gradient: #FF6B35 → #F7931E → #FDB827 │  ← Linear gradient 45°
│  + Frosted glass overlay                │  ← Same glass effect
│  Result: Professional warm gradient     │
│  that looks promotional                 │
└─────────────────────────────────────────┘
```

**Drawable: `promotion_glass_morphism.xml`**
```xml
<shape>
    <gradient
        type="linear"
        angle="135"
        startColor="#60000000"     <!-- 60% opacity black -->
        centerColor="#40000000"    <!-- 40% opacity black -->
        endColor="#30000000"       <!-- 30% opacity black -->
    />
</shape>
```

---

### 2. TOP-LEFT: COPY CODE SECTION

```xml
<LinearLayout
    layout_gravity="top|start"
    background="@drawable/promo_code_glass_card"
    padding="12dp"
    paddingHorizontal="14dp">
    
    <TextView text="COPY CODE"
              textSize="8sp"
              alpha="0.7"
              letterSpacing="0.08" />
    
    <TextView text="SUMMER30"
              textSize="13sp"
              textStyle="bold"
              typeface="monospace"
              marginTop="3dp" />
</LinearLayout>
```

**Visual Result:**
```
┌─────────────────────────┐
│ COPY CODE               │  ← 8sp, semi-transparent
│                         │
│ SUMMER30                │  ← 13sp, bold, monospace
└─────────────────────────┘
↑ Semi-transparent white background
↑ Thin white border (glass effect)
↑ 12dp rounded corners
```

**Drawable: `promo_code_glass_card.xml`**
```xml
<shape android:shape="rectangle">
    <solid android:color="#40FFFFFF" />          <!-- 40% white -->
    <corners android:radius="12dp" />
    <stroke android:width="1dp" color="#60FFFFFF" />  <!-- 60% white -->
</shape>
```

---

### 3. TOP-RIGHT: EXPIRY TIME SECTION

```xml
<LinearLayout
    layout_gravity="top|end"
    background="@drawable/promo_expiry_glass_card"
    padding="12dp"
    paddingHorizontal="14dp"
    gravity="end">
    
    <TextView text="EXPIRES IN"
              textSize="8sp"
              alpha="0.7"
              letterSpacing="0.08" />
    
    <TextView text="23d 14h"
              textSize="13sp"
              textStyle="bold"
              marginTop="3dp" />
</LinearLayout>
```

**Visual Result:**
```
                         ┌──────────────────────┐
                         │        EXPIRES IN    │  ← Right-aligned
                         │                      │     8sp, semi-transparent
                         │       23d 14h        │  ← 13sp, bold
                         └──────────────────────┘
                         ↑ Semi-transparent white background
                         ↑ Thin white border (glass effect)
                         ↑ 12dp rounded corners
```

**Drawable: `promo_expiry_glass_card.xml`**
```xml
<shape android:shape="rectangle">
    <solid android:color="#40FFFFFF" />          <!-- 40% white -->
    <corners android:radius="12dp" />
    <stroke android:width="1dp" color="#60FFFFFF" />  <!-- 60% white -->
</shape>
```

---

### 4. CENTER: DISCOUNT & OFFER DETAILS

```xml
<LinearLayout
    layout_width="match_parent"
    layout_height="match_parent"
    orientation="vertical"
    gravity="center">
    
    <!-- DISCOUNT TEXT -->
    <TextView
        id="@+id/promotionDiscount"
        text="30% OFF"
        textSize="56sp"
        textStyle="bold"
        textColor="@color/white_fixed"
        letterSpacing="0.02"
        marginBottom="8dp"
        shadowColor="@android:color/black"
        shadowDx="2"
        shadowDy="2"
        shadowRadius="4" />
    
    <!-- PROMOTION NAME -->
    <TextView
        id="@+id/promotionName"
        text="Summer Sale"
        textSize="22sp"
        textStyle="bold"
        textColor="@color/white_fixed"
        alpha="0.95"
        marginBottom="4dp"
        shadowColor="@android:color/black"
        shadowDx="1"
        shadowDy="1"
        shadowRadius="3" />
    
    <!-- DESCRIPTION -->
    <TextView
        id="@+id/promotionDescription"
        text="Min order $150"
        textSize="14sp"
        textColor="@color/white_fixed"
        alpha="0.85"
        marginTop="4dp"
        shadowColor="@android:color/black"
        shadowDx="1"
        shadowDy="1"
        shadowRadius="2" />
</LinearLayout>
```

**Visual Result:**
```
                 30% OFF
              Summer Sale
             Min order $150

Text hierarchy with shadows ensuring readability on any background
```

**Typography Details:**

| Element | Size | Weight | Color | Shadow | Opacity |
|---------|------|--------|-------|--------|---------|
| Discount | 56sp | bold | white | 2dp/4dp blur | 100% |
| Name | 22sp | bold | white | 1dp/3dp blur | 95% |
| Description | 14sp | regular | white | 1dp/2dp blur | 85% |

---

## Color Palette

### Glass Morphism Colors
```
Code Card Background:    #40FFFFFF (40% white)
Code Card Border:        #60FFFFFF (60% white)
Expiry Card Background:  #40FFFFFF (40% white)
Expiry Card Border:      #60FFFFFF (60% white)

Glass Overlay Gradient:
  Top:    #60000000 (60% black)
  Middle: #40000000 (40% black)
  Bottom: #30000000 (30% black)
```

### Gradient Fallback Colors
```
Start (Top-Left):    #FF6B35 (Coral Red)
Center:              #F7931E (Orange)
End (Bottom-Right):  #FDB827 (Gold)
Angle: 45°
```

### Text Colors
```
All Text:     @color/white_fixed (#FFFFFFFF)
Shadows:      @android:color/black with transparency
```

---

## Animation & Interaction

### Click Interaction
```
User touches banner:
1. Ripple effect triggers (built-in Material ripple)
2. Code copied to clipboard
3. Toast shows: "Code copied: SUMMER30"
4. Haptic feedback (optional future enhancement)
```

### Countdown Timer
```
Runs every 1 second:
- Updates top-right timer display
- Format: "Xd Yh Zm" or "Expired"
- Handler automatically manages lifecycle
- Stops when ViewHolder is recycled
```

### Image Loading
```
If image URL exists:
  ├─ Glide loads image
  ├─ centerCrop transformation
  ├─ Image displays with 85% opacity
  └─ Glass overlay on top

If image URL is null/empty:
  ├─ ImageView shows gradient fallback
  ├─ Gradient displays with 100% opacity
  ├─ Glass overlay creates frosted effect
  └─ Result looks professional either way
```

---

## Responsive Behavior

### Different Screen Sizes
```
Small Screen (240dp width):
- 56sp discount still readable
- Glass cards stack vertically if needed
- Text truncates with ellipsize="end"

Large Screen (1080dp width):
- 56sp discount dominates center
- Glass cards have more breathing room
- Full text visible

Landscape vs Portrait:
- FrameLayout layout_gravity handles positioning
- Cards stay in corners regardless of orientation
- Center text scales with viewport
```

---

## File Structure

```
app/src/main/res/
├── layout/
│   └── item_promotion_banner.xml      ← Main layout (redesigned)
│
└── drawable/
    ├── promotion_glass_morphism.xml   ← Frosted glass overlay
    ├── promotion_gradient_fallback.xml ← Fallback gradient
    ├── promo_code_glass_card.xml      ← Top-left glass card
    ├── promo_expiry_glass_card.xml    ← Top-right glass card
    ├── promotion_banner_overlay.xml   ← OLD (kept for backward compat)
    ├── badge_pill.xml                 ← OLD (kept for backward compat)
    ├── code_badge_bg.xml              ← OLD (kept for backward compat)
    └── countdown_badge_bg.xml         ← OLD (kept for backward compat)

app/src/main/java/aeu/ratha/beckie_deal_app/ui/adapters/
└── PromotionBannerAdapter.java        ← Updated bind() method
```

---

## Browser/Comparison with Old Design

### OLD: Pill Badge Approach
```
❌ Red pill background (#android:color/holo_red_dark)
❌ 24sp text in pill
❌ Looks basic and dated
❌ Can blend with image
❌ No glass effect
```

### NEW: Professional Typography
```
✅ 56sp pure white text
✅ Professional shadows for readability
✅ Modern glass morphism aesthetic
✅ Works beautifully with or without images
✅ Glass cards add premium feel
```

---

## Testing Checklist

- [ ] Verify discount displays 56sp large text
- [ ] Verify code is visible in top-left glass card
- [ ] Verify countdown updates every second in top-right
- [ ] Verify clicking banner copies code
- [ ] Verify gradient appears when image fails to load
- [ ] Verify text shadows provide readability
- [ ] Test on different screen sizes (small, medium, large)
- [ ] Test in portrait and landscape
- [ ] Verify countdown shows "Expired" when time runs out
- [ ] Verify card corners are 12dp rounded
- [ ] Verify glass effect opacity looks correct
- [ ] Test with multiple promotions (carousel scrolling)

