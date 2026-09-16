# Typography Quick Reference

## 📋 Copy-Paste Styles

### XML Layouts

```xml
<!-- Screen Title (28sp, Amulya Bold) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.ScreenTitle"
    android:text="Laptops" />

<!-- Product Name - Detail (24sp, Amulya Bold) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.ProductName"
    android:text="MacBook Pro 14" />

<!-- Hero Price (22sp, Amulya Bold) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.PriceHero"
    android:text="$1,999" />

<!-- Section Header (18sp, Amulya Bold) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.SectionHeader"
    android:text="Top Picks" />

<!-- Card Product Name (15sp, Synonym Medium) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.CardProductName"
    android:text="Dell XPS 13" />

<!-- Card Price (15sp, Synonym Semibold) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.CardPrice"
    android:text="$899" />

<!-- Body/Description (15sp, Synonym Regular, 1.6 line height) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.Body"
    android:text="Experience powerful performance..." />

<!-- Specs (14sp, Synonym Regular, 1.5 line height) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.Specs"
    android:text="• Intel Core i7\n• 16GB RAM" />

<!-- Label/Badge (12sp, Synonym Medium, UPPERCASE) -->
<TextView
    android:textAppearance="@style/TextAppearance.App.Label"
    android:text="Free Shipping" />

<!-- Button (15sp, Synonym Semibold) -->
<Button
    style="@style/Widget.App.Button"
    android:text="Add to Cart" />
```

### Kotlin Code

```kotlin
// Screen title
titleTextView.setTextAppearance(R.style.TextAppearance_App_ScreenTitle)

// Product name
productNameView.setTextAppearance(R.style.TextAppearance_App_ProductName)

// Hero price
priceView.setTextAppearance(R.style.TextAppearance_App_PriceHero)

// Section header
headerView.setTextAppearance(R.style.TextAppearance_App_SectionHeader)

// Card product name
cardTitleView.setTextAppearance(R.style.TextAppearance_App_CardProductName)

// Card price
cardPriceView.setTextAppearance(R.style.TextAppearance_App_CardPrice)

// Body text
descriptionView.setTextAppearance(R.style.TextAppearance_App_Body)

// Specs
specsView.setTextAppearance(R.style.TextAppearance_App_Specs)

// Label
labelView.setTextAppearance(R.style.TextAppearance_App_Label)
```

### Java Code

```java
// Screen title
titleTextView.setTextAppearance(R.style.TextAppearance_App_ScreenTitle);

// Product name
productNameView.setTextAppearance(R.style.TextAppearance_App_ProductName);

// Hero price
priceView.setTextAppearance(R.style.TextAppearance_App_PriceHero);

// Section header
headerView.setTextAppearance(R.style.TextAppearance_App_SectionHeader);

// Card product name
cardTitleView.setTextAppearance(R.style.TextAppearance_App_CardProductName);

// Card price
cardPriceView.setTextAppearance(R.style.TextAppearance_App_CardPrice);

// Body text
descriptionView.setTextAppearance(R.style.TextAppearance_App_Body);

// Specs
specsView.setTextAppearance(R.style.TextAppearance_App_Specs);

// Label
labelView.setTextAppearance(R.style.TextAppearance_App_Label);
```

## 🎯 When to Use What

| Use Case | Style to Use |
|----------|--------------|
| Category name at top of screen | `ScreenTitle` |
| "Featured Items" heading | `SectionHeader` |
| Product name in grid/list card | `CardProductName` |
| Price in grid/list card | `CardPrice` |
| Product name on detail page | `ProductName` |
| Large price on detail page | `PriceHero` |
| Product description paragraph | `Body` |
| Technical specifications | `Specs` |
| "Free Shipping", "Sale", "New" | `Label` |
| Any button text | `Button` (via style) |

## 📏 Size Reference

| Size | Used For |
|------|----------|
| 28sp | Screen titles |
| 24sp | Product names (detail) |
| 22sp | Hero prices |
| 18sp | Section headers |
| 15sp | Cards, body, buttons |
| 14sp | Specs lists |
| 12sp | Small labels |

## 🔤 Font Family Reference

| Font | Used For |
|------|----------|
| **Amulya Bold** | Titles, headers, prices (emphasis) |
| **Synonym Regular** | Body text, specs (readability) |
| **Synonym Medium** | Card titles, labels (balanced) |
| **Synonym Semibold** | Prices in cards, buttons (medium emphasis) |

## ⚡ Pro Tips

1. **Don't manually set textSize** - Use textAppearance styles instead
2. **Buttons automatically inherit** - Just use `@style/Widget.App.Button`
3. **Labels auto-uppercase** - No need to manually uppercase text
4. **Line heights are built-in** - Body and Specs have proper spacing
5. **Use semantic aliases** - e.g., `ProductCardTitle` instead of `CardProductName` for clarity

## 🔍 Find & Replace Patterns

### Replace old Material styles:
```
TextAppearance.Material3.HeadlineMedium → TextAppearance.App.ScreenTitle
TextAppearance.Material3.HeadlineSmall → TextAppearance.App.ProductName
TextAppearance.Material3.TitleLarge → TextAppearance.App.PriceHero
TextAppearance.Material3.TitleMedium → TextAppearance.App.SectionHeader
TextAppearance.Material3.BodyLarge → TextAppearance.App.CardProductName
TextAppearance.Material3.BodyMedium → TextAppearance.App.Body
```

### Replace manual sizing:
```xml
<!-- DON'T DO THIS -->
<TextView
    android:textSize="28sp"
    android:textStyle="bold" />

<!-- DO THIS INSTEAD -->
<TextView
    android:textAppearance="@style/TextAppearance.App.ScreenTitle" />
```

## 📦 Files Created

- `res/values/typography.xml` - All text styles
- `res/font/amulya_bold.xml` - Amulya font family
- `res/font/synonym.xml` - Synonym font family
- `res/values/themes.xml` - Updated with typography

## ✅ Setup Checklist

- [ ] Download Amulya Bold from fontshare.com
- [ ] Download Synonym (Regular, Medium, Semibold) from fontshare.com
- [ ] Add 4 .ttf files to `res/font/`
- [ ] Clean & rebuild project
- [ ] Test one screen to verify fonts load
- [ ] Migrate remaining screens

---

**Need help?** See `TYPOGRAPHY_USAGE_GUIDE.md` for detailed examples.
