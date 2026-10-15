# Scroll Indicator - Setup & Assets

## Quick Setup Guide

### ✅ Files Already Created

1. **Layout**: `app/src/main/res/layout/scroll_indicator.xml`
2. **Utility**: `app/src/main/java/aeu/ratha/beckie_deal_app/utils/ScrollIndicatorHelper.java`
3. **Documentation**: 
   - `SCROLL_INDICATOR_GUIDE.md` (detailed guide)
   - `SCROLL_INDICATOR_EXAMPLE.md` (code examples)
   - `SCROLL_INDICATOR_SETUP.md` (this file)

### ⚠️ Drawable Assets Required

The scroll indicator uses `ic_arrow_down` drawable. This should already exist in your project, but if not, you'll need it.

**Expected Location**: `app/src/main/res/drawable/ic_arrow_down.xml`

If this drawable is missing or named differently, update the reference in `scroll_indicator.xml`:

```xml
<!-- Current -->
android:src="@drawable/ic_arrow_down"

<!-- Alternative names to check -->
android:src="@drawable/arrow_down"
android:src="@drawable/ic_chevron_down"
android:src="@drawable/expand_more"
```

### 🎨 Creating the Arrow Drawable (if needed)

If `ic_arrow_down` doesn't exist, create it at:
`app/src/main/res/drawable/ic_arrow_down.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    
    <path
        android:fillColor="@android:color/white"
        android:pathData="M7,10l5,5 5,-5z" />
</vector>
```

---

## Installation Steps

### Step 1: Verify Drawable Exists
```bash
# Check if ic_arrow_down.xml exists
ls -la app/src/main/res/drawable/ | grep arrow
```

### Step 2: Update Your Activity Layout

Add the scroll indicator to any activity with scrollable content:

```xml
<!-- For NestedScrollView (like ProductDetailActivity) -->
<FrameLayout
    android:id="@+id/scrollIndicatorContainer"
    android:layout_width="match_parent"
    android:layout_height="56dp"
    android:layout_marginTop="24dp"
    android:layout_marginBottom="8dp">

    <com.google.android.material.card.MaterialCardView
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:layout_gravity="center_horizontal|bottom"
        app:cardCornerRadius="24dp"
        app:cardElevation="4dp"
        app:cardBackgroundColor="@color/white_fixed">

        <ImageView
            android:id="@+id/scrollArrowDown"
            android:layout_width="24dp"
            android:layout_height="24dp"
            android:layout_gravity="center"
            android:src="@drawable/ic_arrow_down"
            android:contentDescription="Scroll down"
            app:tint="@color/brand_orange" />
    </com.google.android.material.card.MaterialCardView>
</FrameLayout>
```

### Step 3: Update Your Activity Code

```java
import aeu.ratha.beckie_deal_app.utils.ScrollIndicatorHelper;

public class YourActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_layout);
        
        // For NestedScrollView
        NestedScrollView scrollView = findViewById(R.id.scrollView);
        View indicator = findViewById(R.id.scrollIndicatorContainer);
        ScrollIndicatorHelper.attachToNestedScrollView(scrollView, indicator);
        
        // OR for RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        View indicator = findViewById(R.id.scrollIndicator);
        ScrollIndicatorHelper.attachToRecyclerView(recyclerView, indicator);
    }
}
```

### Step 4: Build & Test

```bash
# Build the project
./gradlew build

# Or use Android Studio's Build menu
```

---

## Color Customization

The scroll indicator uses `@color/brand_orange` by default. To customize:

### Option 1: Change Color Directly
Edit `scroll_indicator.xml`:
```xml
<!-- Current -->
app:tint="@color/brand_orange"

<!-- Change to other colors -->
app:tint="@color/primary"
app:tint="@color/accent"
app:tint="#FF6B00"
```

### Option 2: Update Color in Code
```java
ImageView arrowView = indicator.findViewById(R.id.scrollArrowDown);
arrowView.setColorFilter(Color.BLUE);
```

---

## Animation Customization

### Adjust Bounce Speed
In `ScrollIndicatorHelper.java`:

```java
// Faster animation (400ms instead of 600ms)
private static final long ANIMATION_DURATION = 400L;

// Slower animation (800ms)
private static final long ANIMATION_DURATION = 800L;
```

### Adjust Bounce Distance
```java
// Larger bounce
private static final float TRANSLATE_DISTANCE = 12f;

// Smaller bounce (more subtle)
private static final float TRANSLATE_DISTANCE = 4f;
```

### Adjust Scroll Threshold
```java
// Hide indicator after scrolling 150dp instead of 100dp
if (scrollDistance > 150 || lastVisiblePosition >= totalItemCount - 1) {
    fadeOutIndicator(indicator);
}
```

---

## Advanced Customization

### Custom Animation Interpolator
```java
// In ScrollIndicatorHelper.java, change the interpolator:

// Current (smooth acceleration/deceleration)
animator.setInterpolator(new AccelerateDecelerateInterpolator());

// Alternatives:
animator.setInterpolator(new BounceInterpolator());
animator.setInterpolator(new OvershootInterpolator());
animator.setInterpolator(new LinearInterpolator());
animator.setInterpolator(new DecelerateInterpolator());
```

### Disable Animation on Specific Devices
```java
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
    // Skip indicator on older Android versions
    indicator.setVisibility(View.GONE);
} else {
    ScrollIndicatorHelper.attachToRecyclerView(recyclerView, indicator);
}
```

---

## Testing Checklist

### Visual Tests
- [ ] Indicator appears when activity loads
- [ ] Arrow bounces smoothly
- [ ] Color matches design
- [ ] Size appropriate for screen
- [ ] Text is visible (good contrast)

### Functional Tests
- [ ] Indicator hides after scrolling down
- [ ] Indicator reappears if scrolled back up
- [ ] Indicator hides at end of list
- [ ] Works with empty lists
- [ ] Works with single-item lists
- [ ] Works with large lists (100+ items)

### Performance Tests
- [ ] No lag on scroll
- [ ] Animation smooth on low-end devices
- [ ] No memory leaks
- [ ] No excessive CPU usage

### Device Tests
- [ ] Works on phones (small screens)
- [ ] Works on tablets (large screens)
- [ ] Works on landscape/portrait
- [ ] Works with notch/punch-hole

---

## Troubleshooting

### Issue: Drawable not found

**Error**: `android.content.res.Resources$NotFoundException: Resource ID`

**Solution**:
1. Verify drawable exists: `app/src/main/res/drawable/ic_arrow_down.xml`
2. Check drawable name matches exactly
3. Try alternative names:
   ```xml
   android:src="@drawable/expand_more"
   android:src="@drawable/chevron_down"
   ```

### Issue: Indicator doesn't animate

**Problem**: Arrow doesn't bounce

**Solution**:
```java
// Check animation is attached to correct view
View arrowView = indicator.findViewById(R.id.scrollArrowDown);
if (arrowView != null) {
    // Animation should work
} else {
    // View ID mismatch - fix in layout XML
}
```

### Issue: Indicator overlaps content

**Problem**: Scroll indicator covers product cards

**Solution**: Adjust `paddingBottom` in parent layout:
```xml
<FrameLayout
    ...
    android:paddingBottom="16dp">
    ...
</FrameLayout>
```

### Issue: Animation stutters on older devices

**Problem**: Bouncing animation is not smooth

**Solution**: Reduce animation duration:
```java
private static final long ANIMATION_DURATION = 400L; // Was 600L
private static final float TRANSLATE_DISTANCE = 4f;   // Was 8f (smaller distance)
```

---

## Integration with State Preservation

The scroll indicator works perfectly with `ScrollStatePreferences`:

```java
@Override
protected void onResume() {
    super.onResume();
    
    // Restore scroll position
    GridLayoutManager lm = (GridLayoutManager) recyclerView.getLayoutManager();
    if (lm != null) {
        ScrollStatePreferences.restoreScrollPosition(this, pageKey, lm, recyclerView);
    }
    
    // Update indicator (will show if there's more content)
    ScrollIndicatorHelper.updateIndicatorVisibility(recyclerView, indicator);
}

@Override
protected void onPause() {
    super.onPause();
    
    // Save scroll position
    GridLayoutManager lm = (GridLayoutManager) recyclerView.getLayoutManager();
    if (lm != null) {
        ScrollStatePreferences.saveScrollPosition(this, pageKey, lm);
    }
}
```

---

## Files Summary

| File | Purpose | Location |
|------|---------|----------|
| `scroll_indicator.xml` | Layout for indicator | `app/src/main/res/layout/` |
| `ScrollIndicatorHelper.java` | Indicator logic | `app/src/main/java/.../utils/` |
| `activity_product_detail.xml` | Updated with indicator | `app/src/main/res/layout/` |
| `ic_arrow_down.xml` | Arrow drawable | `app/src/main/res/drawable/` |

---

## Next Steps

1. ✅ Copy files from this implementation
2. ✅ Add indicator to your activity layouts
3. ✅ Import and call ScrollIndicatorHelper in activities
4. ✅ Test on multiple devices
5. ✅ Adjust colors/animation as needed
6. ✅ Deploy to production

---

## Support Resources

- **Full Guide**: See `SCROLL_INDICATOR_GUIDE.md`
- **Code Examples**: See `SCROLL_INDICATOR_EXAMPLE.md`
- **Helper Class**: `ScrollIndicatorHelper.java`
- **Layout**: `scroll_indicator.xml`

For any issues, refer to the Troubleshooting section above or check the example implementations.

---

## Success Indicators ✅

You've successfully implemented the scroll indicator when:
1. Arrow appears at bottom of scrollable content ✅
2. Arrow bounces continuously ✅
3. Arrow hides when user scrolls down ✅
4. Arrow reappears when scrolled back up ✅
5. No crashes or warnings in logcat ✅
6. Smooth animation on all devices ✅

**You're ready for production!** 🚀
