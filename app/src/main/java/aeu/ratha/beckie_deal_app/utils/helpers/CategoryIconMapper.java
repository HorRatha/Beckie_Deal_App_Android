package aeu.ratha.beckie_deal_app.utils.helpers;

import android.util.Log;
import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Category;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Centralized icon mapping system for categories.
 * 
 * This utility provides dynamic icon resolution based on:
 * 1. API-provided icon field (if available)
 * 2. Slug-based lookup (most reliable)
 * 3. Smart name matching (fallback)
 * 4. Default icon (when all else fails)
 * 
 * Benefits:
 * - No hardcoded conditions scattered across activities
 * - Easy to add new categories without code changes
 * - Consistent icon/color handling across the app
 * - Future-proof for server-provided icons
 * - Single source of truth for icon assignments
 */
public class CategoryIconMapper {
    
    private static final String TAG = "CategoryIconMapper";
    
    /**
     * Main category icon mapping.
     * Maps category slugs to Android drawable resource IDs.
     * 
     * Add new category entries here when new categories are added to the system.
     * This is the primary configuration for icon assignments.
     */
    private static final Map<String, Integer> CATEGORY_ICON_MAP = 
        new LinkedHashMap<String, Integer>() {{
            // Laptops & Computers - Blue theme
            put("laptops", R.drawable.devices_24);
            put("computers", R.drawable.devices_24);
            put("desktop", R.drawable.devices_24);
            put("workstation", R.drawable.devices_24);
            put("ultrabook", R.drawable.devices_24);
            
            // Mobile & Phones - Pink/Red theme
            put("smartphones", R.drawable.mobile_24);
            put("phones", R.drawable.mobile_24);
            put("mobile", R.drawable.mobile_24);
            put("tablets", R.drawable.mobile_24);
            put("ipad", R.drawable.mobile_24);
            
            // Gaming - Purple theme
            put("gaming", R.drawable.sports_esports_24);
            put("game", R.drawable.sports_esports_24);
            put("console", R.drawable.sports_esports_24);
            put("gaming-laptops", R.drawable.sports_esports_24);
            put("gaming-pc", R.drawable.sports_esports_24);
            
            // Wearables - Orange theme
            put("wearables", R.drawable.devices_wearables_24);
            put("smartwatch", R.drawable.devices_wearables_24);
            put("watch", R.drawable.devices_wearables_24);
            put("fitness", R.drawable.devices_wearables_24);
            put("bands", R.drawable.devices_wearables_24);
            
            // Displays & Monitors - Cyan theme
            put("monitors", R.drawable.devices_24);
            put("displays", R.drawable.devices_24);
            put("tv", R.drawable.devices_24);
            put("projector", R.drawable.devices_24);
            
            // Accessories - Yellow theme
            put("accessories", R.drawable.widgets_24);
            put("cables", R.drawable.widgets_24);
            put("chargers", R.drawable.widgets_24);
            put("mouse", R.drawable.widgets_24);
            put("keyboard", R.drawable.widgets_24);
            put("bag", R.drawable.widgets_24);
            put("screen-protector", R.drawable.widgets_24);
            
            // Storage - Green theme
            put("storage", R.drawable.widgets_24);
            put("drives", R.drawable.widgets_24);
            put("ssd", R.drawable.widgets_24);
            put("hdd", R.drawable.widgets_24);
            put("memory", R.drawable.widgets_24);
            put("usb", R.drawable.widgets_24);
            
            // Networking - Teal theme
            put("networking", R.drawable.widgets_24);
            put("router", R.drawable.widgets_24);
            put("modem", R.drawable.widgets_24);
            put("wifi", R.drawable.widgets_24);
            put("adapter", R.drawable.widgets_24);
            
            // Audio - Deep Orange theme
            put("audio", R.drawable.devices_24);
            put("headphones", R.drawable.devices_24);
            put("speakers", R.drawable.devices_24);
            put("earbuds", R.drawable.devices_24);
            put("microphone", R.drawable.devices_24);
            
            // Default fallback
            put("default", R.drawable.ic_category_default);
    }};
    
    /**
     * Accent/decorative icons for category cards.
     * These add visual interest and category-specific styling.
     */
    private static final Map<String, Integer> CATEGORY_ACCENT_ICON_MAP = 
        new LinkedHashMap<String, Integer>() {{
            put("laptops", R.drawable.category_accent_laptop);
            put("smartphones", R.drawable.category_accent_phone);
            put("gaming", R.drawable.category_accent_gaming);
            put("wearables", R.drawable.category_accent_wearables);
            put("accessories", R.drawable.category_accent_accessories);
            put("default", R.drawable.decorative_sparkle_outline);
    }};
    
    /**
     * Get the main icon for a category using multi-layer resolution.
     * 
     * Resolution order:
     * 1. API-provided icon field (future-proof)
     * 2. Slug-based lookup (most reliable, configured)
     * 3. Smart name matching (fallback for variations)
     * 4. Default icon (when everything fails)
     * 
     * @param category The category from API
     * @return Resource ID of the category icon
     */
    public static int getIconForCategory(Category category) {
        if (category == null) {
            Log.w(TAG, "Category is null, returning default icon");
            return R.drawable.ic_category_default;
        }
        
        // Layer 1: Check if API provides icon identifier
        if (category.getIcon() != null && !category.getIcon().isEmpty()) {
            Log.d(TAG, "Using API-provided icon for category: " + category.getName());
            int apiIcon = resolveRemoteIcon(category.getIcon());
            if (apiIcon != 0) {
                return apiIcon;
            }
        }
        
        // Layer 2: Use slug-based lookup (most reliable)
        String slug = category.getSlug();
        if (slug != null && !slug.isEmpty()) {
            String slugKey = slug.toLowerCase();
            Integer iconRes = CATEGORY_ICON_MAP.get(slugKey);
            if (iconRes != null) {
                Log.d(TAG, "Found icon for slug: " + slug);
                return iconRes;
            }
        }
        
        // Layer 3: Use smart name matching (fallback)
        String name = category.getName();
        if (name != null && !name.isEmpty()) {
            Log.d(TAG, "Attempting smart match for category: " + name);
            int smartMatch = getIconBySmartMatch(name);
            if (smartMatch != 0) {
                return smartMatch;
            }
        }
        
        // Layer 4: Return default icon
        Log.w(TAG, "No icon found for category: " + category.getName() + 
              ", using default");
        return R.drawable.ic_category_default;
    }
    
    /**
     * Get the accent/decorative icon for a category.
     * These are used for visual enhancement on category cards.
     * 
     * @param category The category from API
     * @return Resource ID of the accent icon
     */
    public static int getAccentIconForCategory(Category category) {
        if (category == null) {
            return R.drawable.decorative_sparkle_outline;
        }
        
        // Try slug-based lookup first
        String slug = category.getSlug();
        if (slug != null) {
            String slugKey = slug.toLowerCase();
            Integer accentRes = CATEGORY_ACCENT_ICON_MAP.get(slugKey);
            if (accentRes != null) {
                return accentRes;
            }
        }
        
        // Try smart matching on name
        String name = category.getName();
        if (name != null) {
            String lowerName = name.toLowerCase();
            for (Map.Entry<String, Integer> entry : CATEGORY_ACCENT_ICON_MAP.entrySet()) {
                if (lowerName.contains(entry.getKey())) {
                    return entry.getValue();
                }
            }
        }
        
        return R.drawable.decorative_sparkle_outline;
    }
    
    /**
     * Smart pattern matching for category names.
     * Handles variations, typos, and partial matches.
     * This is the fallback when slug-based lookup fails.
     * 
     * @param categoryName The name to match
     * @return Resource ID if match found, 0 otherwise
     */
    private static int getIconBySmartMatch(String categoryName) {
        if (categoryName == null || categoryName.isEmpty()) {
            return 0;
        }
        
        String name = categoryName.toLowerCase();
        
        // Check all entries in map against category name
        for (Map.Entry<String, Integer> entry : CATEGORY_ICON_MAP.entrySet()) {
            if (name.contains(entry.getKey())) {
                Log.d(TAG, "Smart matched '" + categoryName + "' to key '" + 
                      entry.getKey() + "'");
                return entry.getValue();
            }
        }
        
        return 0;
    }
    
    /**
     * Resolve remote/API-provided icon identifiers.
     * 
     * This handles icons provided directly by the API.
     * Future enhancement: When API sends icon URLs, we can:
     * 1. Map to local resources by identifier
     * 2. Download and cache with Glide
     * 3. Use Material Icons library
     * 
     * @param remoteIcon The icon identifier from API
     * @return Resource ID, or 0 if unresolved
     */
    private static int resolveRemoteIcon(String remoteIcon) {
        // TODO: Implement when API provides icon URLs/identifiers
        // For now, return 0 to fall through to next resolution layer
        return 0;
    }
    
    /**
     * Register a new category icon mapping at runtime.
     * Useful for dynamic categories or plugin system.
     * 
     * @param slug The category slug (should be lowercase)
     * @param iconResId The drawable resource ID
     */
    public static void registerCategoryIcon(String slug, int iconResId) {
        if (slug == null || slug.isEmpty()) {
            Log.w(TAG, "Cannot register icon with null/empty slug");
            return;
        }
        
        String slugKey = slug.toLowerCase();
        CATEGORY_ICON_MAP.put(slugKey, iconResId);
        Log.d(TAG, "Registered icon for slug: " + slugKey);
    }
    
    /**
     * Register accent icon mapping at runtime.
     * 
     * @param slug The category slug (should be lowercase)
     * @param accentResId The accent drawable resource ID
     */
    public static void registerAccentIcon(String slug, int accentResId) {
        if (slug == null || slug.isEmpty()) {
            Log.w(TAG, "Cannot register accent icon with null/empty slug");
            return;
        }
        
        String slugKey = slug.toLowerCase();
        CATEGORY_ACCENT_ICON_MAP.put(slugKey, accentResId);
        Log.d(TAG, "Registered accent icon for slug: " + slugKey);
    }
    
    /**
     * Get all registered category slugs (for debugging/admin).
     * 
     * @return Set of all mapped category slugs
     */
    public static Set<String> getMappedCategories() {
        return CATEGORY_ICON_MAP.keySet();
    }
    
    /**
     * Get category slug for a given category (helper).
     * 
     * @param category The category
     * @return The slug in lowercase, or empty string if null
     */
    public static String getCategorySlug(Category category) {
        if (category == null || category.getSlug() == null) {
            return "";
        }
        return category.getSlug().toLowerCase();
    }
    
    /**
     * Check if a category slug is registered in the system.
     * 
     * @param slug The slug to check
     * @return true if registered, false otherwise
     */
    public static boolean isRegistered(String slug) {
        return slug != null && CATEGORY_ICON_MAP.containsKey(slug.toLowerCase());
    }
    
    /**
     * Debug: Print all registered mappings to logs.
     * Useful for development and debugging.
     */
    public static void debugPrintMappings() {
        Log.d(TAG, "=== Category Icon Mappings ===");
        for (Map.Entry<String, Integer> entry : CATEGORY_ICON_MAP.entrySet()) {
            Log.d(TAG, "  " + entry.getKey() + " -> " + entry.getValue());
        }
        Log.d(TAG, "Total mappings: " + CATEGORY_ICON_MAP.size());
    }
}
