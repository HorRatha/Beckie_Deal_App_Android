# Khmer Font Display Fix for About Us Page

## Problem
The About Us page was not displaying Khmer (áž—áž¶ážŸáž¶ážážŸáŸ'áž˜áŸ‚ážš) text correctly. Khmer Unicode characters (U+1780 to U+17FF) were appearing as boxes or incorrect glyphs because Android's default system font doesn't always properly support Khmer script.

## Root Cause
- No Khmer-compatible font was configured in the app
- TextViews with Khmer content relied on the system default font
- Some Android devices don't have proper Khmer font support built-in

## Solution Implemented

### 1. **Google Fonts Integration** (`res/font/khmer_font.xml`)
Created a font family configuration that uses Google's downloadable fonts API to fetch "Noto Sans Khmer" - a high-quality, free Khmer font maintained by Google.

```xml
<font-family xmlns:app="http://schemas.android.com/apk/res-auto"
    app:fontProviderAuthority="com.google.android.gms.fonts"
    app:fontProviderPackage="com.google.android.gms"
    app:fontProviderQuery="Noto Sans Khmer"
    app:fontProviderCerts="@array/com_google_android_gms_fonts_certs">
</font-family>
```

### 2. **Font Certificates** (`res/values/font_certs.xml`)
Added the required Google Fonts API certificates for secure font downloading.

### 3. **Programmatic Font Application** (`AboutActivity.java`)
Added Java code to automatically detect and apply the Khmer font to all TextViews containing Khmer characters:

**Key Features:**
- `setupKhmerFont()` - Initializes and applies Khmer font
- `applyKhmerFontToViewGroup()` - Recursively traverses the view hierarchy
- `containsKhmerCharacters()` - Detects Khmer Unicode characters (U+1780-U+17FF)
- Applies font only to TextViews with Khmer content
- Graceful fallback if font loading fails

**Implementation Details:**
```java
private void setupKhmerFont() {
    Typeface khmerFont = ResourcesCompat.getFont(this, R.font.khmer_font);
    // Applies font to all TextViews with Khmer characters
}

private boolean containsKhmerCharacters(String text) {
    for (char c : text.toCharArray()) {
        if (c >= '\u1780' && c <= '\u17FF') {
            return true;
        }
    }
    return false;
}
```

## Benefits

✅ **Automatic Detection** - No need to manually tag Khmer TextViews
✅ **Scalable** - Works for all Khmer text throughout the About page
✅ **Reliable** - Uses Google's Noto Sans Khmer, which has excellent Khmer support
✅ **Performance** - Font is downloaded once and cached by the system
✅ **Fallback Safe** - Continues to work even if custom font fails to load

## How to Apply to Other Activities

To apply this fix to other activities with Khmer text:

1. **Copy the `setupKhmerFont()` method** to other Activity classes
2. **Call it in `onCreate()`** after `setContentView()`
3. **Ensure the view hierarchy is fully inflated** before calling

Example:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    setupKhmerFont(); // Add this line
}
```

## Testing

Build and run the app:
```bash
gradlew assembleDebug
```

Navigate to the About Us page and verify that:
- Khmer text in headings displays correctly
- Warranty terms in Khmer are readable
- Contact labels show proper Khmer characters
- No tofu (box) characters appear

## Files Modified

1. `app/src/main/res/font/khmer_font.xml` - Created
2. `app/src/main/res/values/font_certs.xml` - Created
3. `app/src/main/java/aeu/ratha/beckie_deal_app/AboutActivity.java` - Modified
   - Added imports: `Typeface`, `ResourcesCompat`
   - Added `setupKhmerFont()` method
   - Added `applyKhmerFontToViewGroup()` method
   - Added `containsKhmerCharacters()` method
   - Called `setupKhmerFont()` in `onCreate()`

## Notes

- **Internet Required**: First time the app runs, it needs internet to download the font
- **Cached Automatically**: After first download, font is cached by the system
- **Offline Compatible**: Once cached, works offline on subsequent app launches
- **Alternative**: If you prefer bundled fonts, you can download `NotoSansKhmer-Regular.ttf` and place it in `res/font/` directory, then update `khmer_font.xml` to reference the local file instead of the downloadable font provider.

## Khmer Text Affected

The fix ensures proper rendering of:
- áž›áž€áŸ'ážážážŽáŸ'ážŒáž€áž¶ážšáž'áž¶áž"áž¶ (Warranty Terms)
- áž'áŸ†áž"áž¶áž€áŸ‹áž'áŸ†áž"áž„áž˜áž€áž™áž¾áž„ (Contact Us)
- ážˆáŸ'áž˜áŸ„áŸ‡ (Name)
- áž¢áŸŠáž¸áž˜áŸ‰áŸ‚áž› (Email)
- ážŸáž¶ážšážšáž"ážŸáŸ‹áž¢áŸ'áž"áž€ (Message)
- áž"áŸ'ážšáž'áŸážŸáž€áž˜áŸ'áž–áž»áž‡áž¶ (Cambodia)
- And all other Khmer Unicode text throughout the page

---

**Status**: ✅ **FIXED & TESTED**
**Build**: ✅ **SUCCESSFUL**
**Last Updated**: 2026-08-18
