# Promotion Banner V2 - Visual & Implementation Guide

## 🎨 Final Design Layout

```
╔════════════════════════════════════════════════════════════════╗
║                    PROMOTION BANNER CARD                       ║
║                    (16dp corner radius)                         ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║                                                                ║
║  30% OFF              ┌──────────────────────┐                ║
║  (56sp, bold)         │   EXPIRES IN         │                ║
║                       │   (8sp, RED text)    │                ║
║  Summer Sale          │                      │                ║
║  (22sp, bold)         │   23d 14h            │  ← RED card
║                       │   (14sp, RED text)   │     with RED
║  Min order $150       └──────────────────────┘     border
║  (14sp)               [40% white + RED #FF6B6B]
║                                                                ║
║  (All LEFT-aligned)              (Spacer)
║  (Text shadows)                                                ║
║                                                                ║
║                                  ┌──────────────┐             ║
║                                  │   [ICON]     │             ║
║                                  │  (28x28dp)   │             ║
║                                  │  Copy Icon   │ ← White card
║                                  └──────────────┘    glass
║                                  [40% white card]
║                                                                ║
║                                                                ║
║  [Background Image (85% opacity) + Glass Overlay]            ║
║  OR                                                           ║
║  [Gradient Fallback + Glass Overlay]                         ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
```

---

## 📐 Dimensions & Spacing

### Main Container
```
Width:          match_parent
Height:         match_parent (inside card)
Padding:        16dp on all sides
Card Radius:    16dp
Card Elevation: 4dp
```

### Left Column (Content)
```
Width:          0dp (layout_weight="1" - takes available space)
Height:         match_parent
Margin End:     16dp
Gravity:        center_vertical (vertically centered)
Padding:        0dp (inherits from parent)
```

### Right Column (Buttons)
```
Width:          wrap_content
Height:         match_parent
Orientation:    vertical
Gravity:        top (starts at top)
Spacing:        Spacer View with layout_weight="1" in middle
```

---

## 🎯 Left Column: Content Section

### Component 1: Discount Text
```
┌─────────────────────┐
│ 30% OFF             │
└─────────────────────┘

Properties:
  Size:           56sp
  Weight:         bold
  Color:          @color/white_fixed (#FFFFFFFF)
  Letter Spacing: 0.02
  Shadow:
    - Offset X:   2dp
    - Offset Y:   2dp
    - Blur:       4dp
    - Color:      Black with transparency
  Margin Bottom:  8dp
  Alignment:      wrap_content (naturally left-aligned)
```

### Component 2: Promotion Name
```
┌─────────────────────┐
│ Summer Sale         │
└─────────────────────┘

Properties:
  Size:           22sp
  Weight:         bold
  Color:          @color/white_fixed (#FFFFFFFF)
  Alpha:          0.95 (slightly transparent)
  Shadow:
    - Offset X:   1dp
    - Offset Y:   1dp
    - Blur:       3dp
    - Color:      Black with transparency
  Margin Bottom:  4dp
  Max Lines:      1
  Ellipsize:      end (... if too long)
  Alignment:      wrap_content (naturally left-aligned)
```

### Component 3: Description
```
┌─────────────────────┐
│ Min order $150      │
└─────────────────────┘

Properties:
  Size:           14sp
  Color:          @color/white_fixed (#FFFFFFFF)
  Alpha:          0.85 (more transparent)
  Shadow:
    - Offset X:   1dp
    - Offset Y:   1dp
    - Blur:       2dp
    - Color:      Black with transparency
  Margin Top:     4dp
  Max Lines:      2
  Ellipsize:      end
  Alignment:      wrap_content (naturally left-aligned)
```

---

## 🎨 Right Column: Button Section

### Spacer View (Vertical)
```
┌─────────────────────┐
│                     │
│                     │  ← Flexible space that grows
│     (Spacer)        │    to push copy icon down
│                     │
│                     │
└─────────────────────┘

Properties:
  Width:          0dp
  Height:         0dp (with layout_weight="1")
  Layout Weight:  1 (takes all available vertical space)
```

---

## 🎯 Top-Right: Expired Time Card

### Visual Layout
```
┌──────────────────────────┐
│  EXPIRES IN              │  ← Label (8sp, red, 90% opacity)
│                          │
│  23d 14h                 │  ← Timer (14sp, red, bold)
└──────────────────────────┘
↑ Card with RED border
```

### Card Styling
```
Background Color:     #40FFFFFF (40% white opacity)
Border Color:         #FF6B6B (RED - #FF6B6B)
Border Width:         2dp
Corner Radius:        12dp
Padding:              12dp vertical, 14dp horizontal
Gravity:              end (right-aligned content)
```

### Component: Expires Label
```
Text:           "EXPIRES IN"
Size:           8sp
Color:          @android:color/holo_red_light
Weight:         bold
Alpha:          0.9 (semi-transparent)
Letter Spacing: 0.08 (wide spacing)
Alignment:      wrap_content
```

### Component: Countdown Timer
```
Text:           "23d 14h" (or "Expired")
Size:           14sp
Color:          @android:color/holo_red_light (RED)
Weight:         bold
Margin Top:     3dp
Alignment:      wrap_content
Updates:        Every 1 second
```

---

## 🎯 Bottom-Right: Copy Code Button

### Visual Layout
```
┌──────────────────┐
│     [ICON]       │
│   (28x28dp)      │
│   Copy Icon      │
│   (White SVG)    │
└──────────────────┘
↑ Glass morphism card
```

### Card Styling
```
Background Color:     #40FFFFFF (40% white opacity)
Border Color:         #60FFFFFF (60% white border)
Border Width:         1dp
Corner Radius:        12dp
Padding:              14dp (all sides)
Gravity:              center (icon centered in card)
Width:                wrap_content
Height:               wrap_content
```

### Component: Copy Icon
```
ID:                   promotionCopyCodeIcon
Type:                 ImageView
Size:                 28dp x 28dp
Source:               @drawable/ic_copy_icon
Scale Type:           centerInside
Tint:                 @color/white_fixed (WHITE)
Content Description:  "Copy code"
Click Listener:       Copy promotion code to clipboard
```

### Hidden Code Storage
```
ID:                   promotionCode
Type:                 TextView
Text:                 "SUMMER30" (or actual code)
Visibility:           gone (hidden from view)
Purpose:              Store code for clipboard copying
Accessed by:          copyCodeIcon click listener
```

---

## 🎨 Background Layer

### Base Layer
```
Type:           FrameLayout background
Background:     @drawable/promotion_gradient_fallback
Purpose:        Fallback when image missing
Gradient:       45° linear
Colors:
  - Start:      #FF6B35 (Coral red)
  - Center:     #F7931E (Orange)
  - End:        #FDB827 (Gold)
```

### Image Layer
```
Type:           ImageView
Width:          match_parent
Height:         match_parent
Alpha:          0.85 (semi-transparent)
Scale Type:     centerCrop
Loading:        Via Glide
Fallback:       None (shows gradient behind)
Purpose:        Promotional image if available
```

### Glass Morphism Overlay
```
Type:           View background drawable
Background:     @drawable/promotion_glass_morphism
Gradient:       135° linear
Opacity Levels:
  - Start:      #60000000 (60% black)
  - Center:     #40000000 (40% black)
  - End:        #30000000 (30% black)
Purpose:        Frosted glass effect over image
Effect:         Creates elegance and text readability
```

---

## 🔧 XML Structure

### Layout Hierarchy
```xml
FrameLayout (match_parent, background="gradient_fallback")
├─ ImageView (banner image, alpha=0.85)
├─ View (glass overlay, background="glass_morphism")
├─ LinearLayout (orientation="horizontal", padding="16dp")
│  ├─ LinearLayout (LEFT column, layout_weight="1")
│  │  ├─ TextView (discount, 56sp)
│  │  ├─ TextView (name, 22sp)
│  │  └─ TextView (description, 14sp)
│  │
│  └─ LinearLayout (RIGHT column, orientation="vertical")
│     ├─ LinearLayout (expired time card, background="red_card")
│     │  ├─ TextView (label, 8sp, red)
│     │  └─ TextView (countdown, 14sp, red)
│     │
│     ├─ View (spacer, layout_weight="1")
│     │
│     └─ LinearLayout (copy card, background="code_card")
│        ├─ ImageView (copy icon, 28x28dp, white)
│        └─ TextView (code, hidden)
│
└─ View (ripple effect)
```

---

## 📏 Typography Reference

| Element | Size | Weight | Color | Opacity | Shadow |
|---------|------|--------|-------|---------|--------|
| Discount | 56sp | bold | white | 100% | 2dp/4dp |
| Name | 22sp | bold | white | 95% | 1dp/3dp |
| Description | 14sp | regular | white | 85% | 1dp/2dp |
| Expires Label | 8sp | bold | red | 90% | none |
| Timer | 14sp | bold | red | 100% | none |

---

## 🎨 Color Reference (Hex Codes)

### Text Colors
```
Content Text:         #FFFFFFFF (white)
Red Timer Text:       android:color/holo_red_light
Red Border:           #FF6B6B
Shadow Color:         Black with transparency
```

### Card Colors
```
Expired Card Background:   #40FFFFFF (40% white)
Expired Card Border:       #FF6B6B (RED, 2dp)
Copy Card Background:      #40FFFFFF (40% white)
Copy Card Border:          #60FFFFFF (60% white, 1dp)
Copy Card Corner:          12dp radius
```

### Background Colors
```
Gradient Start:           #FF6B35 (coral)
Gradient Center:          #F7931E (orange)
Gradient End:             #FDB827 (gold)
Glass Overlay Start:      #60000000 (60% black)
Glass Overlay Center:     #40000000 (40% black)
Glass Overlay End:        #30000000 (30% black)
```

---

## 🖱️ Interaction Design

### Copy Icon Click
```
User Action:    Tap copy icon
Response:       Icon background brightens
Action:         Copy promotion code to clipboard
Feedback:       Toast: "Code copied: SUMMER30"
Haptic:         Optional (future enhancement)
Animation:      Optional icon change (future enhancement)
```

### Countdown Timer Update
```
Trigger:        Every 1000ms (1 second)
Update:         Recalculate remaining time
Display:        Update countdown text
Format:         "Xd Yh Zm" or "Expired"
Stop:           When time reaches 0 or promotion ends
```

### Banner Card Ripple
```
Touch:          User touches banner anywhere
Effect:         Ripple effect (material design)
Color:          Default material ripple
Duration:       ~300ms
No Action:      Ripple is just visual feedback
```

---

## 📱 Responsive Behavior

### Small Screens (240dp width)
```
Left Column:    Takes ~65% of width (after 16dp padding)
Right Column:   Takes ~20% of width
Spacing:        16dp margin between columns
Card Width:     Wraps around content (compact)
Text:           May truncate with ellipsize
Icon:           Remains 28x28dp
```

### Medium Screens (480dp width)
```
Left Column:    Takes ~70% of width
Right Column:   Takes ~20% of width
Spacing:        16dp margin between columns
Card Width:     Slightly wider but still wrap_content
Text:           Displays normally
Icon:           Remains 28x28dp
```

### Large Screens (1080dp width)
```
Left Column:    Takes ~75% of width
Right Column:   Takes ~15% of width
Spacing:        16dp margin between columns
Card Width:     Comfortable sizing
Text:           Fully visible, well-spaced
Icon:           Remains 28x28dp (proportional)
```

### Landscape Mode
```
Width:          Much wider
Height:         Shorter
Layout:         Same (horizontal still works well)
Content:        All visible at once
Buttons:        Properly positioned on right
Readability:    Excellent
```

---

## 🧪 Verification Checklist

### Visual Verification
- [ ] Content is on LEFT side
- [ ] All text is LEFT-aligned
- [ ] Discount is 56sp and bold
- [ ] Name is 22sp and bold
- [ ] Description is 14sp
- [ ] Buttons are on RIGHT side
- [ ] Expired time at TOP-right
- [ ] Copy icon at BOTTOM-right

### Color Verification
- [ ] Discount text is white
- [ ] Name text is white
- [ ] Description text is white
- [ ] Timer label is RED
- [ ] Timer text is RED
- [ ] Card border is RED (expired)
- [ ] Card border is white (copy)
- [ ] Icon is white

### Styling Verification
- [ ] Text shadows are visible
- [ ] Card corners are 12dp rounded
- [ ] Card has semi-transparent background
- [ ] Background image is 85% opacity
- [ ] Glass overlay creates frosted effect

### Interaction Verification
- [ ] Copy icon is clickable
- [ ] Clicking copies code
- [ ] Toast shows "Code copied: [code]"
- [ ] Countdown updates every second
- [ ] Timer shows "Expired" when done

---

## 🎯 Design Principles

### Clarity
✓ Content on left (natural reading)
✓ Buttons on right (secondary)
✓ Large discount (primary focus)
✓ Red timer (urgent action)

### Minimalism
✓ Icon instead of text
✓ No unnecessary elements
✓ Clean layout structure
✓ Focused visual hierarchy

### Usability
✓ Easy to scan left to right
✓ Clear action buttons
✓ Obvious copy action (icon)
✓ Clear urgency (red timer)

### Professional
✓ Glass morphism aesthetic
✓ Professional typography
✓ Balanced spacing
✓ Luxury retail feel

