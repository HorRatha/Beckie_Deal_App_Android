# Promotion Banner V3 - Visual Mockup

## 🎨 Final Banner Design

```
╔════════════════════════════════════════════════════════════════╗
║                   PROMOTION BANNER CARD                        ║
║                  (With Beautiful Image or Gradient             ║
║                   + Glass Morphism Overlay)                    ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  ┌────────────────────┐   EXPIRES IN                          ║
║  │ 30% OFF            │   (8sp, red label)                    ║
║  │ (56sp, bold)       │                                        ║
║  │                    │   23d 14h                              ║
║  │ Summer Sale        │   (14sp, red, bold, shadow)           ║
║  │ (22sp, bold)       │   [NO BACKGROUND CARD]                ║
║  │                    │                                        ║
║  │ Min order $150     │   (Spacer)                            ║
║  │ (14sp)             │                                        ║
║  │                    │                                        ║
║  │ (Left-aligned)     │   ┌──────────────────────────┐        ║
║  │ (Text shadows)     │   │  [ICON]  SUMMER30       │        ║
║  │                    │   │ (18x18)    (12sp mono)  │        ║
║  │                    │   │                          │        ║
║  │                    │   │   (Pill-shaped button)   │        ║
║  │                    │   │  (20dp rounded corners)  │        ║
║  │                    │   │   (40% white glass)      │        ║
║  │                    │   └──────────────────────────┘        ║
║  │                    │                                        ║
║  └────────────────────┘                                        ║
║                                                                ║
║  [Background: Image (85% opacity) + Glass Overlay]           ║
║  OR [Gradient Fallback + Glass Overlay]                       ║
║                                                                ║
╚════════════════════════════════════════════════════════════════╝
```

---

## 📐 Component Breakdown

### LEFT COLUMN (Content)

```
┌──────────────────────────────┐
│ 30% OFF                      │
│ (56sp, bold, white shadow)   │
│                              │
│ Summer Sale                  │
│ (22sp, bold, white shadow)   │
│                              │
│ Min order $150               │
│ (14sp, white shadow)         │
│                              │
│ (All LEFT-aligned)           │
│ (Vertically centered)        │
└──────────────────────────────┘

Properties:
- Width: 60% (layout_weight="1")
- Height: match_parent
- Gravity: center_vertical
- All text: left-aligned
```

### RIGHT COLUMN (Buttons)

```
┌─────────────────────────────┐
│ EXPIRES IN (red, 8sp)       │
│                             │
│ 23d 14h (red, 14sp, shadow) │
│ [NO BACKGROUND]             │
│                             │
│ (Spacer - grows to fill)    │
│                             │
│ ┌──────────────────────────┐│
│ │ [ICON] SUMMER30          ││
│ │ (18x18) (12sp monospace) ││
│ │                          ││
│ │ (Pill: 20dp rounded)     ││
│ │ (Glass white, 40%)       ││
│ └──────────────────────────┘│
│                             │
└─────────────────────────────┘

Properties:
- Width: wrap_content
- Height: match_parent
- Orientation: vertical
- Top section: expired timer (text only)
- Middle: spacer (flexible, layout_weight="1")
- Bottom: copy pill button
```

---

## 🎯 Expired Timer (Text Only)

```
EXPIRES IN
(8sp, red, 90% opacity, letter spacing)

23d 14h
(14sp, red, bold, text shadow)

Color:          #holo_red_light (RED)
Background:     NONE (removed!)
Layout:         Vertical
Alignment:      Right (gravity="end")
Shadow:         1dp offset, 2dp blur

Visual Effect:
┌─────────────┐
│ EXPIRES IN  │  ← Label, semi-transparent red
│ 23d 14h     │  ← Value, bold red with shadow
└─────────────┘
(No box around it - just text)
```

---

## 🎯 Copy Code Pill Button

```
┌──────────────────────────────┐
│ [COPY ICON]  SUMMER30        │
│  (18x18dp)    (12sp mono)    │
└──────────────────────────────┘
   (Pill: 20dp rounded corners)
   (Glass: 40% white + 60% border)

Layout: Horizontal (icon + text side-by-side)
Icon:   18x18dp white copy/duplicate icon
Margin: 6dp between icon and text
Text:   "SUMMER30" (12sp, bold, monospace, white)
Padding: 10dp vertical, 14dp horizontal
Border: 1dp white (60% opacity)

Visual Details:

When NOT clicked:
┌────────────────────────────────┐
│ [≡]  SUMMER30                 │  ← Pill container
│      (semi-transparent white)  │
└────────────────────────────────┘

When clicked:
- Ripple effect appears
- Code copied to clipboard
- Toast: "Code copied: SUMMER30"
```

---

## 🎨 Background Layers (Unchanged)

```
LAYER 4: Ripple Effect
├─ Material design ripple
└─ On touch feedback

LAYER 3: Content (Text & Buttons)
├─ Discount (56sp)
├─ Name (22sp)
├─ Description (14sp)
├─ Expired timer (text only, red)
└─ Copy pill (icon + text)

LAYER 2: Glass Morphism Overlay
├─ Gradient: 60%→40%→30% black
├─ Angle: 135°
└─ Creates frosted glass effect

LAYER 1: Background
├─ Product Image (85% opacity)
│  OR
├─ Gradient Fallback (45° linear)
│  #FF6B35 → #F7931E → #FDB827
└─ Ensures always looks professional
```

---

## 📏 Exact Dimensions

### Overall Card
```
Width:          match_parent
Height:         match_parent
Corner Radius:  16dp
Elevation:      4dp
Padding:        16dp (all sides)
```

### Expired Timer
```
Width:          wrap_content
Height:         wrap_content
Orientation:    vertical
Gravity:        end (right-aligned)
Margin Bottom:  Auto (spacer below)

Label:
  - Size: 8sp
  - Weight: bold
  - Color: #holo_red_light (RED)
  - Opacity: 90%
  - Letter Spacing: 0.08

Value:
  - Size: 14sp
  - Weight: bold
  - Color: #holo_red_light (RED)
  - Opacity: 100%
  - Margin Top: 3dp
  - Shadow: 1dp/2dp
```

### Copy Pill
```
Width:          wrap_content
Height:         wrap_content
Orientation:    horizontal
Gravity:        center_vertical
Padding:        10dp vertical, 14dp horizontal
Background:     promo_code_pill_bg
Corner Radius:  20dp (pill shape)
Border:         1dp white (60% opacity)

Icon:
  - Size: 18x18dp
  - Color: White
  - Margin Right: 6dp

Text:
  - Font: Monospace
  - Size: 12sp
  - Weight: bold
  - Color: White
  - Content: "SUMMER30" or actual code
```

---

## 🎨 Color Reference

### Text Colors
```
Discount:           White (#FFFFFFFF)
Name:               White (#FFFFFFFF)
Description:        White (#FFFFFFFF)
Expired Label:      RED (#holo_red_light)
Expired Value:      RED (#holo_red_light)
Copy Icon:          White (#FFFFFFFF)
Copy Text:          White (#FFFFFFFF)
Shadows:            Black semi-transparent
```

### Background Colors
```
Copy Pill BG:       #40FFFFFF (40% white)
Copy Pill Border:   #60FFFFFF (60% white)
Glass Overlay:      #60→40→30% black (gradient)
Gradient Start:     #FF6B35 (coral)
Gradient Center:    #F7931E (orange)
Gradient End:       #FDB827 (gold)
```

---

## 🎭 Interaction States

### Default State
```
┌──────────────────┐  ┌────────────────────┐
│ 30% OFF          │  │ EXPIRES IN         │
│ Summer Sale      │  │ 23d 14h (red)      │
│ Min order $150   │  │ [NO BACKGROUND]    │
│                  │  │                    │
│ (Left content)   │  │ ┌────────────────┐ │
│                  │  │ │ [I] SUMMER30   │ │
│                  │  │ │  (pill button) │ │
│                  │  │ └────────────────┘ │
└──────────────────┘  └────────────────────┘
```

### Hover/Focus State
```
(Copy pill shows slight highlight or ripple)
```

### After Click
```
Toast appears: "Code copied: SUMMER30"
Duration: 2 seconds
Position: Bottom of screen
```

---

## 📱 Responsive Behavior

### Small Screen (240dp)
```
LEFT (content): ~60%
RIGHT (buttons): ~30%
Copy pill: Compact sizing
Text: May truncate
Icon: Remains 18x18dp
```

### Medium Screen (480dp)
```
LEFT (content): ~65%
RIGHT (buttons): ~30%
Copy pill: Normal sizing
All text: Fully visible
```

### Large Screen (1080dp)
```
LEFT (content): ~70%
RIGHT (buttons): ~25%
Copy pill: Generous spacing
All elements: Well-proportioned
```

### Landscape
```
All elements adapt to wider viewport
Content still on left, buttons on right
Proportions maintained
```

---

## ✨ Design Highlights

### Professional Appearance
✓ Pill shape is modern and trendy
✓ Icon + text = clear call-to-action
✓ Glass morphism = luxury feel
✓ Text shadows = readability

### User Experience
✓ Copy code is immediately recognizable (pill shape)
✓ Red timer draws urgent attention
✓ Click anywhere on pill to copy
✓ Toast confirmation feedback

### Visual Hierarchy
✓ Large discount dominates (56sp)
✓ Red timer shows urgency
✓ Pill button is secondary action
✓ Clear left-to-right flow

### Technical Quality
✓ Responsive layout
✓ All screen sizes
✓ Professional typography
✓ Proper spacing

---

## 🎯 Key Differences from V2

| Aspect | V2 | V3 |
|--------|----|----|
| Copy Style | Icon only (icon + icon card) | Pill with icon + text |
| Copy Shape | Square card | Rounded pill (20dp) |
| Copy Icon Size | 28x28 | 18x18 (smaller) |
| Copy Text | Hidden (gone) | Visible "SUMMER30" |
| Copy Layout | Vertical (icon over hidden code) | Horizontal (icon + text side) |
| Expired Card | Has red border + bg | No card, text only |
| Expired Background | Red bordered card | None (just text) |
| Expired Style | Card with design | Minimal text |

