package aeu.ratha.beckie_deal_app.utils.helpers;

import android.content.Context;
import androidx.core.content.ContextCompat;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Category;

/**
 * Category color scheme manager for consistent UI styling.
 * 
 * Maintains visual harmony and category recognition through:
 * - Consistent color theming per category type
 * - Matching gradient backgrounds
 * - Accent colors for highlights
 * 
 * This ensures that even new categories automatically get
 * appropriate colors that fit the app's design language.
 */
public class CategoryColorScheme {
    
    private static final String TAG = "CategoryColorScheme";
    
    /**
     * Get the primary color for a category.
     * Each category type has a distinctive color for quick recognition.
     * 
     * @param category The category
     * @return Android color resource ID
     */
    public static int getPrimaryColor(Category category) {
        if (category == null) {
            return R.color.text_secondary;
        }
        
        String slug = CategoryIconMapper.getCategorySlug(category);
        if (slug.isEmpty()) {
            return R.color.text_secondary;
        }
        
        // Categorize by slug and return appropriate color
        if (slug.contains("laptop") || slug.contains("computer") || 
            slug.contains("workstation") || slug.contains("desktop")) {
            return R.color.category_color_laptops;  // Blue: #1E88E5
        }
        
        if (slug.contains("smartphone") || slug.contains("phone") || 
            slug.contains("tablet") || slug.contains("mobile")) {
            return R.color.category_color_mobile;   // Pink: #E91E63
        }
        
        if (slug.contains("gaming") || slug.contains("console") || 
            slug.contains("game-pc")) {
            return R.color.category_color_gaming;   // Purple: #9C27B0
        }
        
        if (slug.contains("wearable") || slug.contains("watch") || 
            slug.contains("fitness") || slug.contains("band")) {
            return R.color.category_color_wearables; // Orange: #FF6F00
        }
        
        if (slug.contains("accessory") || slug.contains("cable") || 
            slug.contains("charger") || slug.contains("mouse") || 
            slug.contains("keyboard")) {
            return R.color.category_color_accessories; // Yellow: #FBC02D
        }
        
        if (slug.contains("storage") || slug.contains("drive") || 
            slug.contains("ssd") || slug.contains("hdd") || 
            slug.contains("memory")) {
            return R.color.category_color_storage;  // Green: #43A047
        }
        
        if (slug.contains("monitor") || slug.contains("display") || 
            slug.contains("tv") || slug.contains("projector")) {
            return R.color.category_color_displays;  // Cyan: #00ACC1
        }
        
        if (slug.contains("network") || slug.contains("router") || 
            slug.contains("modem") || slug.contains("wifi")) {
            return R.color.category_color_networking; // Teal: #00897B
        }
        
        if (slug.contains("audio") || slug.contains("headphone") || 
            slug.contains("speaker") || slug.contains("earbuds")) {
            return R.color.category_color_audio;    // Deep Orange: #D84315
        }
        
        // Default color for unknown categories
        return R.color.category_color_default;      // Gray-Blue: #607D8B
    }
    
    /**
     * Get primary color as Android int (not resource ID).
     * Convenience method for contexts where you need the actual color value.
     * 
     * @param context Android context
     * @param category The category
     * @return Color int value
     */
    public static int getPrimaryColorInt(Context context, Category category) {
        if (context == null) return 0;
        return ContextCompat.getColor(context, getPrimaryColor(category));
    }
    
    /**
     * Get the gradient background drawable for a category.
     * These add visual depth and category-specific styling.
     * 
     * Returns existing gradient resources or falls back to wave background.
     * 
     * @param category The category
     * @return Drawable resource ID for gradient
     */
    public static int getBackgroundGradient(Category category) {
        if (category == null) {
            return R.drawable.bg_top_wave;
        }
        
        String slug = CategoryIconMapper.getCategorySlug(category);
        if (slug.isEmpty()) {
            return R.drawable.bg_top_wave;
        }
        
        // Return wave background for all categories (consistent styling)
        // Can be enhanced later with category-specific gradients
        if (slug.contains("laptop") || slug.contains("computer")) {
            return R.drawable.bg_top_wave;
        }
        
        if (slug.contains("gaming") || slug.contains("console")) {
            return R.drawable.bg_top_wave;
        }
        
        if (slug.contains("wearable") || slug.contains("watch")) {
            return R.drawable.bg_top_wave;
        }
        
        if (slug.contains("smartphone") || slug.contains("mobile")) {
            return R.drawable.bg_top_wave;
        }
        
        if (slug.contains("accessory")) {
            return R.drawable.bg_top_wave;
        }
        
        // Default gradient
        return R.drawable.bg_top_wave;
    }
    
    /**
     * Get accent color for highlights, badges, and interactive elements.
     * Accent colors are more saturated versions of primary colors.
     * 
     * @param category The category
     * @return Accent color resource ID
     */
    public static int getAccentColor(Category category) {
        if (category == null) {
            return R.color.primary;
        }
        
        String slug = CategoryIconMapper.getCategorySlug(category);
        if (slug.isEmpty()) {
            return R.color.primary;
        }
        
        // Return slight variations of primary colors for accents
        if (slug.contains("laptop")) return R.color.category_accent_color_laptops;
        if (slug.contains("gaming")) return R.color.category_accent_color_gaming;
        if (slug.contains("wearable")) return R.color.category_accent_color_wearables;
        
        return R.color.primary;
    }
    
    /**
     * Get text color suitable for a category's background color.
     * Ensures text is readable (high contrast).
     * 
     * @param category The category
     * @return Text color resource ID (usually white or dark gray)
     */
    public static int getTextColor(Category category) {
        // Most category backgrounds are dark enough for white text
        return R.color.white_fixed;
    }
    
    /**
     * Get secondary text color for category descriptions.
     * Slightly less prominent than primary text.
     * 
     * @param category The category
     * @return Secondary text color resource ID
     */
    public static int getSecondaryTextColor(Category category) {
        return R.color.text_secondary_light;
    }
    
    /**
     * Get the category color palette as a single resource ID.
     * Useful for widgets or other components that need one color reference.
     * 
     * @param category The category
     * @return Color palette resource ID
     */
    public static int getColorPalette(Category category) {
        // This could map to color state lists or attr resources
        // For now, returns the primary color
        return getPrimaryColor(category);
    }
    
    /**
     * Get a contrasting color for use on category backgrounds.
     * Useful for text, icons, or overlay elements.
     * 
     * @param category The category
     * @return Contrasting color resource ID
     */
    public static int getContrastingColor(Category category) {
        String slug = CategoryIconMapper.getCategorySlug(category);
        
        // Light backgrounds get dark text, dark backgrounds get light text
        if (slug.contains("yellow") || slug.contains("light")) {
            return R.color.dark_brown;
        }
        
        return R.color.white_fixed;
    }
    
    /**
     * Check if category has a "light" or "dark" color scheme.
     * Useful for determining overlay opacity, icon colors, etc.
     * 
     * @param category The category
     * @return true if light theme, false if dark theme
     */
    public static boolean isLightTheme(Category category) {
        String slug = CategoryIconMapper.getCategorySlug(category);
        
        // Determine if the category's color is "light" or "dark"
        return slug.contains("yellow") || slug.contains("light") || 
               slug.contains("accessory") || slug.contains("storage");
    }
    
    /**
     * Get animation color for category interactions.
     * Creates fluid transitions between states.
     * 
     * @param category The category
     * @return Animation color resource ID
     */
    public static int getAnimationColor(Category category) {
        // Usually a lighter shade of the primary color
        return getPrimaryColor(category);
    }
}
