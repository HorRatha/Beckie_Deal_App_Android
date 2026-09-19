# Promotion Banner V2 - Final Visual Mockup

## 📱 Complete Banner Layout

```
╔════════════════════════════════════════════════════════════════╗
║                    PROMOTION BANNER CARD                       ║
║                  (With Beautiful Product Image                 ║
║                   + Glass Morphism Overlay)                    ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  ┌────────────────────────┐ ┌──────────────────────┐          ║
║  │ 30% OFF                │ │ EXPIRES IN           │          ║
║  │ (56sp, bold, shadow)   │ │ (8sp, red, label)    │          ║
║  │                        │ │                      │          ║
║  │ Summer Sale            │ │ 23d 14h              │          ║
║  │ (22sp, bold, shadow)   │ │ (14sp, RED, bold)    │          ║
║  │                        │ │                      │          ║
║  │ Min order $150         │ │ [RED BORDER #FF6B6B] │          ║
║  │ (14sp, shadow)         │ │ [40% white bg]       │          ║
║  │                        │ │ [12dp corners]       │          ║
║  │ (All LEFT-aligned)     │ │                      │          ║
║  │ (Vertically centered)  │ │                      │          ║
║  └────────────────────────┘ │                      │          ║
║                             │                      │          ║
║                             └──────────────────────┘          ║
║                                    ▲                           ║
║                           (Top-Right Corner)                   ║
║                                                                ║
║                                                                ║
║                                                                ║
║                      [50% of banner height]                   ║
║                                                                ║
║                                                                ║
║                             ┌──────────────┐                  ║
║                             │    [ICON]    │                  ║
║                             │  (28x28dp)   │                  ║
║                             │ White Copy   │                  ║
║                             │ Icon Vector  │                  ║
║                             │              │                  ║
║                             │[40% white bg]│                  ║
║                             │[60% border]  │                  ║
║                             │[12dp corners]│                  ║
║                             └──────────────┘                  ║
║                                    ▲                           ║
║                           (Bottom-Right Corner)               ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
```

---

## 🎨 Detailed Component Breakdown

### 📍 Left Column (60% width)

```
┌─────────────────────────────────────────┐
│                                         │
│  30% OFF                                │  ← Primary focus
│  (56sp bold white text with shadow)     │
│                                         │
│  Summer Sale                            │
│  (22sp bold white text with shadow)     │
│                                         │
│  Min order $150                         │
│  (14sp white text with shadow)          │
│                                         │
│  (All naturally left-aligned)           │
│  (Vertically centered in card)          │
│                                         │
└─────────────────────────────────────────┘

Key Properties:
- Width: 0dp (layout_weight="1", takes available space)
- Height: match_parent
- Alignment: LEFT (natural text alignment)
- Vertical Position: center_vertical
- Margin Right: 16dp (spacing from buttons)
```

### 📍 Right Column (Top): Expired Time

```
┌──────────────────────────────┐
│ EXPIRES IN                   │  ← Label
│ (8sp, red, semi-transparent) │
│                              │
│ 23d 14h                      │  ← Timer
│ (14sp, bold RED)             │
│                              │
└──────────────────────────────┘
  ↑ Background: 40% white
  ↑ Border: RED #FF6B6B (2dp)
  ↑ Corners: 12dp rounded
  ↑ Padding: 12dp vertical, 14dp horizontal

Properties:
- Width: wrap_content
- Height: wrap_content
- Background: promo_expiry_glass_card_red
- Gravity: end (right-aligned content inside card)
- Position: Top of right column
```

### 📍 Right Column (Spacer): Flexible Space

```
┌──────────────────────────────┐
│                              │
│          (Spacer)            │  ← Grows to fill space
│                              │  ← Pushes copy icon down
│          (Flexible)          │
│                              │
│          (Layout Weight: 1)  │
│                              │
└──────────────────────────────┘

Properties:
- Width: 0dp
- Height: 0dp (with layout_weight="1")
- Weight: 1 (takes all available vertical space)
- Purpose: Push copy button to bottom
```

### 📍 Right Column (Bottom): Copy Icon

```
┌──────────────────────────────┐
│           [ICON]             │
│         (28x28dp)            │
│                              │
│   ┌───────────────────┐      │
│   │  Copy Icon (SVG)  │      │
│   │   (2 rectangles)  │      │
│   │  (White colored)  │      │
│   └───────────────────┘      │
│                              │
└──────────────────────────────┘
  ↑ Background: 40% white
  ↑ Border: 60% white (1dp)
  ↑ Corners: 12dp rounded
  ↑ Padding: 14dp all sides

Properties:
- Width: wrap_content
- Height: wrap_content
- Background: promo_code_glass_card
- Gravity: center (icon centered)
- Position: Bottom of right column
- Icon Size: 28x28dp
- Icon Color: White
```

---

## 🎨 Background Layers

```
┌─────────────────────────────────────────────────────┐
│  LAYER 4: Ripple Effect                             │
│  ├─ Material design ripple                          │
│  └─ On touch feedback                               │
├─────────────────────────────────────────────────────┤
│  LAYER 3: Content (Text & Icons)                    │
│  ├─ Discount (56sp)                                 │
│  ├─ Name (22sp)                                     │
│  ├─ Description (14sp)                              │
│  ├─ Expired timer card (red)                        │
│  └─ Copy icon card (white)                          │
├─────────────────────────────────────────────────────┤
│  LAYER 2: Glass Morphism Overlay                    │
│  ├─ Gradient: 60%→40%→30% black                    │
│  ├─ Angle: 135°                                     │
│  └─ Creates frosted glass effect                    │
├─────────────────────────────────────────────────────┤
│  LAYER 1: Background                                │
│  ├─ Product Image (85% opacity)                     │
│  │  OR                                              │
│  ├─ Gradient Fallback (45° linear)                 │
│  │  #FF6B35 → #F7931E → #FDB827                    │
│  └─ Ensures always looks professional               │
└─────────────────────────────────────────────────────┘
```

---

## 📏 Exact Dimensions

### Overall Card
```
Width:              match_parent
Height:             match_parent (inside parent ViewPager)
Corner Radius:      16dp
Elevation:          4dp
Background:         Gradient fallback or image

Internal Padding:   16dp (all sides)
```

### Left Column (Content)
```
Width:              0dp (layout_weight="1")
Height:             match_parent
Layout Weight:      1
Gravity:            center_vertical
Margin End:         16dp

Text Alignment:
  - Discount: wrap_content (LEFT)
  - Name:     wrap_content (LEFT)
  - Desc:     wrap_content (LEFT)
```

### Right Column (Container)
```
Width:              wrap_content
Height:             match_parent
Orientation:        vertical
Gravity:            top

Expired Card:
  - Width:          wrap_content
  - Height:         wrap_content
  - Padding:        12dp V, 14dp H
  - Corner:         12dp

Spacer:
  - Width:          0dp
  - Height:         0dp
  - Weight:         1

Copy Card:
  - Width:          wrap_content
  - Height:         wrap_content
  - Padding:        14dp all
  - Corner:         12dp

Icon:
  - Size:           28x28dp
  - Color:          White
```

---

## 🎨 Color Reference

### Red Expired Card
```
Background:         #40FFFFFF (40% white opacity)
Border:             #FF6B6B (RED, 2dp)
Text (Label):       #android:color/holo_red_light (RED)
Text (Timer):       #android:color/holo_red_light (RED)
```

### White Copy Card
```
Background:         #40FFFFFF (40% white opacity)
Border:             #60FFFFFF (60% white, 1dp)
Icon:               @color/white_fixed (#FFFFFFFF)
```

### Content Text
```
Discount:           @color/white_fixed (#FFFFFFFF)
Name:               @color/white_fixed (#FFFFFFFF)
Description:        @color/white_fixed (#FFFFFFFF)
Shadows:            Black with transparency
```

### Background
```
Image:              85% opacity (semi-transparent)
Overlay:            #60000000 → #40000000 → #30000000
Gradient:           #FF6B35 → #F7931E → #FDB827
```

---

## 🎯 Interaction States

### Default State
```
┌────────────────────────────────────────┐
│ [Content]           [Expired] [Icon]  │
│ 30% OFF             RED CARD   WHITE   │
│ Summer Sale         23d 14h    ICON    │
│ Min order $150                         │
└────────────────────────────────────────┘
```

### When Icon Touched
```
┌────────────────────────────────────────┐
│ [Content]           [Expired] [Icon]  │
│ 30% OFF             RED CARD   WHITE   │
│ Summer Sale         23d 14h    ICON    │
│ Min order $150         ▓▓▓▓▓▓  ▓▓▓▓▓▓ │ ← Ripple effect
│                       ▓Card▓  ▓Card▓  │
└────────────────────────────────────────┘
```

### After Copied
```
Toast appears: "Code copied: SUMMER30"
Duration: 2 seconds
Position: Bottom center
```

---

## 📱 Responsive Breakpoints

### Small Screen (240dp width)
```
[Content]           [Buttons]
30% OFF             [Expired]
Summer Sale
Min order...        [Copy]
```

### Medium Screen (480dp width)
```
[Content]           [Buttons]
30% OFF             [Expired]
Summer Sale
Min order...        [Copy]
```

### Large Screen (1080dp width)
```
[Content]           [Buttons]
30% OFF             [Expired]
Summer Sale
Min order...        [Copy]
```

### Landscape
```
[Content]           [Expired]
30% OFF
Summer Sale         [Copy]
Min order...
```

---

## ✨ Visual Effects

### Text Shadows
```
Discount:
  - Offset: 2dp X, 2dp Y
  - Blur:   4dp
  - Color:  Black semi-transparent

Name:
  - Offset: 1dp X, 1dp Y
  - Blur:   3dp
  - Color:  Black semi-transparent

Description:
  - Offset: 1dp X, 1dp Y
  - Blur:   2dp
  - Color:  Black semi-transparent
```

### Glass Morphism
```
Overlay Gradient:
  - Type:   Linear 135°
  - Start:  60% black
  - Mid:    40% black
  - End:    30% black
  - Effect: Frosted glass appearance
```

### Card Styling
```
All Cards:
  - Background: 40% white semi-transparent
  - Border:     Stroke (1dp or 2dp)
  - Corners:    12dp rounded
  - Shadow:     Not applied (glass effect only)
```

---

## 🎭 Design Philosophy

### Clarity
✓ Content clearly visible on left
✓ Actions clearly on right
✓ Red timer shows urgency
✓ Icon conveys copy action

### Hierarchy
✓ Large discount (56sp) is primary
✓ Name & description secondary
✓ Red timer creates urgency
✓ Icon is minimal but clear

### Aesthetics
✓ Glass morphism is modern
✓ Professional typography
✓ Balanced spacing
✓ Premium appearance

### Usability
✓ Left-to-right reading
✓ Clear action buttons
✓ Icon is intuitive
✓ Information organized

