# Typography Visual Hierarchy

## 📐 Typography System at a Glance

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│  LAPTOPS                          ← Screen Title       │
│  (Amulya Bold, 28sp, -0.02em)                         │
│                                                         │
│  Top Picks                        ← Section Header     │
│  (Amulya Bold, 18sp, -0.01em)                         │
│                                                         │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐      │
│  │            │  │            │  │            │       │
│  │   [IMG]    │  │   [IMG]    │  │   [IMG]    │      │
│  │            │  │            │  │            │       │
│  │ MacBook    │  │ Dell XPS   │  │ ThinkPad   │      │
│  │ Pro 14     │  │ 13         │  │ X1         │      │
│  │ (Synonym   │  │ (Synonym   │  │ (Synonym   │      │
│  │ Medium,    │  │ Medium,    │  │ Medium,    │      │
│  │ 15sp)      │  │ 15sp)      │  │ 15sp)      │      │
│  │            │  │            │  │            │       │
│  │ $1,999     │  │ $899       │  │ $1,499     │      │
│  │ (Synonym   │  │ (Synonym   │  │ (Synonym   │      │
│  │ Semibold,  │  │ Semibold,  │  │ Semibold,  │      │
│  │ 15sp)      │  │ 15sp)      │  │ 15sp)      │      │
│  │            │  │            │  │            │       │
│  │ FREE       │  │ FREE       │  │ FREE       │      │
│  │ SHIPPING   │  │ SHIPPING   │  │ SHIPPING   │      │
│  │ (Synonym   │  │ (Synonym   │  │ (Synonym   │      │
│  │ Medium,    │  │ Medium,    │  │ Medium,    │      │
│  │ 12sp,      │  │ 12sp,      │  │ 12sp,      │      │
│  │ uppercase) │  │ uppercase) │  │ uppercase) │      │
│  └────────────┘  └────────────┘  └────────────┘      │
│                                                         │
└─────────────────────────────────────────────────────────┘


┌─────────────────────────────────────────────────────────┐
│                     PRODUCT DETAIL                      │
│                                                         │
│         [         PRODUCT IMAGE         ]               │
│                                                         │
│  MacBook Pro 14-inch           ← Product Name          │
│  (Amulya Bold, 24sp, -0.02em)                         │
│                                                         │
│  $1,999                        ← Hero Price            │
│  (Amulya Bold, 22sp, -0.01em)                         │
│                                                         │
│  FREE SHIPPING                 ← Label                 │
│  (Synonym Medium, 12sp, +0.04em, uppercase)           │
│                                                         │
│  Description                   ← Section Header        │
│  (Amulya Bold, 18sp, -0.01em)                         │
│                                                         │
│  Experience powerful           ← Body Text             │
│  performance with the new      (Synonym Regular,       │
│  M2 Pro chip. Perfect for      15sp, line height 1.6)  │
│  creative professionals                                │
│  and developers alike.                                 │
│                                                         │
│  Specifications                ← Section Header        │
│  (Amulya Bold, 18sp, -0.01em)                         │
│                                                         │
│  • M2 Pro chip                 ← Specs                 │
│  • 16GB unified memory         (Synonym Regular,       │
│  • 512GB SSD storage           14sp, line height 1.5)  │
│  • 14.2-inch Liquid Retina                            │
│                                                         │
│  ┌─────────────────────────────────────────┐          │
│  │     ADD TO CART                         │          │
│  │     (Synonym Semibold, 15sp, +0.01em)   │          │
│  └─────────────────────────────────────────┘          │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## 🎨 Font Usage Patterns

### Amulya Bold (Headlines & Emphasis)
```
┌─────────────────────────────────────┐
│                                     │
│   LAPTOPS (28sp)                    │  ← Largest, category/screen titles
│                                     │
│   MacBook Pro 14-inch (24sp)        │  ← Product names on detail pages
│                                     │
│   $1,999 (22sp)                     │  ← Hero prices (main price)
│                                     │
│   Top Picks (18sp)                  │  ← Section headers
│                                     │
└─────────────────────────────────────┘
```

### Synonym (Content & UI)
```
┌─────────────────────────────────────┐
│                                     │
│   Dell XPS 13 (Medium, 15sp)        │  ← Card product names
│                                     │
│   $899 (Semibold, 15sp)             │  ← Card prices & button text
│                                     │
│   Experience powerful performance   │  ← Body text / descriptions
│   with... (Regular, 15sp, 1.6 LH)   │     (with generous line height)
│                                     │
│   • Intel Core i7 (Regular, 14sp)   │  ← Specs lists
│   • 16GB RAM (1.5 LH)               │     (comfortable but compact)
│                                     │
│   FREE SHIPPING (Medium, 12sp)      │  ← Small labels (uppercase)
│                                     │
└─────────────────────────────────────┘
```

## 📊 Size Comparison

```
Screen Title (28sp)          ████████████████████████████
Product Name (24sp)          ████████████████████████
Hero Price (22sp)            ██████████████████████
Section Header (18sp)        ██████████████████
Card Name/Price (15sp)       ███████████████
Button Text (15sp)           ███████████████
Body Text (15sp)             ███████████████
Specs (14sp)                 ██████████████
Label (12sp)                 ████████████
```

## 🔤 Weight Comparison

```
Amulya Bold (700)            ████████  Heavy, for emphasis
Synonym Semibold (600)       ███████   Medium emphasis
Synonym Medium (500)         ██████    Balanced
Synonym Regular (400)        █████     Standard body text
```

## 📐 Tracking (Letter Spacing)

```
Tight (-0.02em)    M a c B o o k  P r o      ← Large headlines
Tight (-0.01em)    $ 1 , 9 9 9                ← Prices & section headers
Normal (0)         Dell XPS 13                ← Most UI text
Loose (+0.01em)    A d d  t o  C a r t       ← Buttons
Loose (+0.04em)    F R E E  S H I P P I N G   ← Small labels
```

## 🎯 Use Case Matrix

| Screen/Component | Titles | Prices | Names | Body | Labels |
|-----------------|--------|--------|-------|------|--------|
| **Category List** | Screen Title (28sp) | - | - | - | - |
| **Product Grid** | Section Header (18sp) | Card Price (15sp) | Card Name (15sp) | - | Label (12sp) |
| **Product Detail** | Product Name (24sp) | Hero Price (22sp) | - | Body (15sp) | Label (12sp) |
| **Specs Section** | Section Header (18sp) | - | - | Specs (14sp) | - |
| **Buttons** | - | - | - | Button (15sp) | - |

## 💡 Typography Hierarchy Rules

### 1. **One H1 Per Screen**
- Use Screen Title (28sp) only once at the top
- Example: "Laptops", "Smartphones", "Orders"

### 2. **Section Headers for Groups**
- Use Section Header (18sp) for content sections
- Example: "Top Picks", "Featured Items", "Description"

### 3. **Consistent Card Typography**
- Card Name: Synonym Medium 15sp
- Card Price: Synonym Semibold 15sp
- This creates visual consistency across grids

### 4. **Hero Elements Stand Out**
- Product Name (24sp) and Hero Price (22sp) are for detail pages
- They create clear focal points

### 5. **Body Text Is Comfortable**
- 15sp with 1.6 line height for descriptions
- 14sp with 1.5 line height for specs
- Optimal reading experience

### 6. **Labels Are Distinct**
- 12sp, uppercase, with loose tracking
- Clearly differentiates metadata from content
- Example: "FREE SHIPPING", "NEW", "SALE"

## 🎨 Real-World Examples

### Product Card (Grid View)
```
┌─────────────────┐
│                 │
│   [Image 180px] │
│                 │
│─────────────────│
│ Dell XPS 13     │ ← CardProductName (Synonym Medium 15sp)
│ $899            │ ← CardPrice (Synonym Semibold 15sp)
│ FREE SHIPPING   │ ← Label (Synonym Medium 12sp, uppercase)
└─────────────────┘
```

### Section with Products
```
Top Picks                         ← SectionHeader (Amulya Bold 18sp)

[Card] [Card] [Card] [Card]       ← Each using Card typography
```

### Product Detail Header
```
[Large Product Image]

MacBook Pro 14-inch               ← ProductName (Amulya Bold 24sp)
$1,999                            ← PriceHero (Amulya Bold 22sp)
FREE SHIPPING                     ← Label (Synonym Medium 12sp)

[Add to Cart Button]              ← Button (Synonym Semibold 15sp)
```

### Description Section
```
Description                       ← SectionHeader (Amulya Bold 18sp)

Experience powerful performance   ← Body (Synonym Regular 15sp, 1.6 LH)
with the new M2 Pro chip. Perfect
for creative professionals and
developers.
```

### Specifications Section
```
Specifications                    ← SectionHeader (Amulya Bold 18sp)

• M2 Pro chip                     ← Specs (Synonym Regular 14sp, 1.5 LH)
• 16GB unified memory
• 512GB SSD storage
• 14.2-inch Liquid Retina XDR
```

## ✨ Design Principles Applied

1. **Clear Hierarchy** - Size differences create obvious importance levels
2. **Font Pairing** - Amulya for impact, Synonym for content
3. **Consistent Scale** - Predictable sizing pattern
4. **Readable Body** - Generous line heights prevent fatigue
5. **Distinct Labels** - Small caps stand out from content
6. **Balanced Cards** - Medium weight for names, Semibold for prices
7. **Breathing Room** - Proper tracking at each size

---

**This visual guide shows how the typography system creates a cohesive, professional look across your entire app.**
