# Typography Migration Checklist

## Setup Steps

### 1. Download and Install Fonts ✓ (Required)
- [ ] Visit [Amulya on Fontshare](https://www.fontshare.com/fonts/amulya)
- [ ] Download the Amulya font family
- [ ] Extract the Bold weight file
- [ ] Rename to `amulya_bold.ttf`
- [ ] Visit [Synonym on Fontshare](https://www.fontshare.com/fonts/synonym)
- [ ] Download the Synonym font family
- [ ] Extract Regular, Medium, and Semibold weight files
- [ ] Rename to:
  - `synonym_regular.ttf`
  - `synonym_medium.ttf`
  - `synonym_semibold.ttf`
- [ ] Place all 4 .ttf files in `app/src/main/res/font/`
- [ ] Delete the placeholder .txt files in the font directory

### 2. Verify Font Files
After adding fonts, verify these files exist:
- [ ] `app/src/main/res/font/amulya_bold.ttf`
- [ ] `app/src/main/res/font/synonym_regular.ttf`
- [ ] `app/src/main/res/font/synonym_medium.ttf`
- [ ] `app/src/main/res/font/synonym_semibold.ttf`
- [ ] `app/src/main/res/font/amulya_bold.xml` (already created)
- [ ] `app/src/main/res/font/synonym.xml` (already created)

### 3. Build and Test
- [ ] Clean the project: `./gradlew clean`
- [ ] Rebuild: `./gradlew build`
- [ ] Run app and verify fonts load correctly
- [ ] Check that no "font not found" errors appear in logs

## Layout Migration

### Update Product Listings / Grid Views
Find layouts with product cards and update:
- [ ] Product names: use `@style/TextAppearance.App.CardProductName`
- [ ] Product prices: use `@style/TextAppearance.App.CardPrice`
- [ ] "Free shipping" badges: use `@style/TextAppearance.App.ShippingBadge`

### Update Product Detail Pages
- [ ] Product title: use `@style/TextAppearance.App.ProductName`
- [ ] Hero price: use `@style/TextAppearance.App.PriceHero`
- [ ] Section headers: use `@style/TextAppearance.App.SectionHeader`
- [ ] Description text: use `@style/TextAppearance.App.Body`
- [ ] Specifications: use `@style/TextAppearance.App.Specs`

### Update Category/Screen Titles
- [ ] Top-level screen titles (e.g., "Laptops", "Smartphones"): use `@style/TextAppearance.App.ScreenTitle`
- [ ] Section headers (e.g., "Top Picks", "Featured"): use `@style/TextAppearance.App.SectionHeader`

### Update Labels and Meta Information
- [ ] Small labels and badges: use `@style/TextAppearance.App.Label`
- [ ] Verify uppercase is applied automatically

### Update Buttons
Buttons should automatically use the correct typography if using:
- [ ] `@style/Widget.App.Button`
- [ ] `@style/Widget.App.Button.Outlined`

## Code Migration (Kotlin/Java)

### Search and Replace Patterns

#### Kotlin
```kotlin
// Old pattern
textView.setTextAppearance(android.R.style.TextAppearance_Material_Headline)

// New pattern - Screen titles
textView.setTextAppearance(R.style.TextAppearance_App_ScreenTitle)

// New pattern - Product names
textView.setTextAppearance(R.style.TextAppearance_App_ProductName)

// New pattern - Prices
textView.setTextAppearance(R.style.TextAppearance_App_PriceHero)
```

#### RecyclerView Adapters
Check all ViewHolders that display:
- [ ] Product names in cards
- [ ] Product prices in cards
- [ ] Section headers
- [ ] Labels and badges

### Search Locations
Run search for old text appearance usage:
```bash
# Search for TextAppearance usage in Kotlin files
grep -r "setTextAppearance" app/src/main/java/

# Search for textAppearance in XML files
grep -r "android:textAppearance" app/src/main/res/layout/

# Search for textSize definitions (may need manual review)
grep -r "android:textSize" app/src/main/res/layout/
```

## Files to Review

### High Priority Layouts
- [ ] `fragment_home.xml` - Product listings, featured sections
- [ ] `fragment_categories.xml` - Category titles
- [ ] `activity_product_detail.xml` / `fragment_product_detail.xml` - Product details
- [ ] `item_product.xml` / `item_product_card.xml` - Product card layouts
- [ ] All button definitions

### Medium Priority
- [ ] Cart item layouts
- [ ] Wishlist item layouts
- [ ] Order history layouts
- [ ] Search results layouts

### Lower Priority
- [ ] Profile screens
- [ ] Settings screens
- [ ] About/Help screens

## Testing Checklist

### Visual Testing
- [ ] View product listing screens (grid and list views)
- [ ] Open product detail pages
- [ ] Check section headers throughout the app
- [ ] Verify button text appearance
- [ ] Check labels and badges
- [ ] Test on different screen sizes (phone, tablet)
- [ ] Test in light and dark modes

### Font Loading
- [ ] Verify fonts load on app startup
- [ ] Check logcat for any font-related errors
- [ ] Test on different Android versions (if possible)

### Accessibility
- [ ] Test with TalkBack enabled
- [ ] Verify font scaling works correctly
- [ ] Check contrast ratios are maintained

## Common Issues and Solutions

### Issue: Fonts not loading
**Solution:** 
- Verify .ttf files are in correct location
- Check file names match exactly (case-sensitive)
- Clean and rebuild project
- Invalidate caches (Android Studio > File > Invalidate Caches / Restart)

### Issue: Wrong font weight appearing
**Solution:**
- Check `android:textFontWeight` attribute
- Verify correct font file is being referenced
- For API < 28, fontWeight may not work; use separate font files

### Issue: Letter spacing looks wrong
**Solution:**
- Android uses em units for letterSpacing
- Negative values reduce spacing
- Positive values increase spacing
- -0.02 = tighter, +0.04 = looser

### Issue: Line height not applying
**Solution:**
- Use `android:lineSpacingMultiplier` not `android:lineSpacingExtra`
- 1.6 multiplier = 160% line height
- May need to adjust padding/margins after changing line height

## Rollback Plan

If issues occur, you can temporarily revert by:
1. Comment out typography imports in `themes.xml`
2. Keep old font (khmer_font.xml) as fallback
3. Gradually migrate screens one by one

## Completion
- [ ] All fonts downloaded and installed
- [ ] All product screens updated
- [ ] All category/listing screens updated
- [ ] All detail screens updated
- [ ] All buttons using new typography
- [ ] Visual testing complete
- [ ] No font errors in logs
- [ ] Accessibility testing passed
- [ ] Dark mode verified
- [ ] Different screen sizes tested

## Notes
_Add any project-specific notes or issues encountered during migration:_

---

Date Started: ___________
Date Completed: ___________
Migrated By: ___________
