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
            // Laptops & Computers 3D images
            put("macbook", R.drawable.macbook_pro);
            put("macbook-pro", R.drawable.macbook_pro);
            put("mac", R.drawable.macbook_pro);
            put("laptops", R.drawable.laptop);
            put("laptop", R.drawable.laptop);
            put("computers", R.drawable.laptop);
            put("desktop", R.drawable.laptop);
            put("workstation", R.drawable.laptop);
            put("ultrabook", R.drawable.laptop);
            put("notebook", R.drawable.laptop);
            
            // Mobile & Phones 3D images
            put("smartphones", R.drawable.smartphone);
            put("smartphone", R.drawable.smartphone);
            put("phones", R.drawable.smartphone);
            put("phone", R.drawable.smartphone);
            put("mobile", R.drawable.smartphone);
            put("tablets", R.drawable.tablet);
            put("tablet", R.drawable.tablet);
            put("ipad", R.drawable.tablet);
            
            // Gaming 3D image
            put("gaming", R.drawable.gaming_laptop);
            put("game", R.drawable.gaming_laptop);
            put("console", R.drawable.gaming_laptop);
            put("gaming-laptops", R.drawable.gaming_laptop);
            put("gaming-pc", R.drawable.gaming_laptop);
            
            // Displays & Monitors 3D image
            put("monitors", R.drawable.monitor);
            put("monitor", R.drawable.monitor);
            put("displays", R.drawable.monitor);
            put("display", R.drawable.monitor);
            put("tv", R.drawable.monitor);
            put("projector", R.drawable.monitor);
            
            // Accessories 3D image
            put("accessories", R.drawable.accessories);
            put("accessory", R.drawable.accessories);
            put("wearables", R.drawable.accessories);
            put("smartwatch", R.drawable.accessories);
            put("watch", R.drawable.accessories);
            put("cables", R.drawable.accessories);
            put("chargers", R.drawable.accessories);
            put("mouse", R.drawable.accessories);
            put("keyboard", R.drawable.accessories);
            put("bag", R.drawable.accessories);
            put("screen-protector", R.drawable.accessories);
            put("audio", R.drawable.accessories);
            put("headphones", R.drawable.accessories);
            put("speakers", R.drawable.accessories);
            put("earbuds", R.drawable.accessories);
            
            // Storage 3D image
            put("storage", R.drawable.storage);
            put("drives", R.drawable.storage);
            put("drive", R.drawable.storage);
            put("ssd", R.drawable.storage);
            put("hdd", R.drawable.storage);
            put("memory", R.drawable.storage);
            put("usb", R.drawable.storage);
            
            // Networking 3D image
            put("networking", R.drawable.networking);
            put("network", R.drawable.networking);
            put("router", R.drawable.networking);
            put("modem", R.drawable.networking);
            put("wifi", R.drawable.networking);
            put("adapter", R.drawable.networking);
            
            // Default fallback
            put("default", R.drawable.laptop);
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
            return R.drawable.laptop;
        }
        
        String name = categoryName.toLowerCase().trim();
        
        // Check MacBook / Apple
        if (name.contains("macbook") || name.contains("macebook") || (name.contains("mac") && name.contains("book"))) {
            return R.drawable.macbook_pro;
        }
        
        // Check Accessories / Fashion
        if (name.contains("accessor") || name.contains("fashion")) {
            return R.drawable.accessories;
        }
        
        // Check Gaming
        if (name.contains("gaming") || name.contains("game") || name.contains("alienware") || name.contains("rog")) {
            return R.drawable.gaming_laptop;
        }
        
        // Check Laptops (including brand/series names)
        if (name.contains("laptop") || name.contains("computer") || name.contains("desktop") ||
            name.contains("asus") || name.contains("dell") || name.contains("hp") ||
            name.contains("lenovo") || name.contains("acer") || name.contains("msi") ||
            name.contains("notebook") || name.contains("ultrabook") || name.contains("workstation") ||
            name.contains("vivobook") || name.contains("zenbook") || name.contains("thinkpad")) {
            return R.drawable.laptop;
        }
        
        // Check Smartphones
        if (name.contains("phone") || name.contains("mobile") || name.contains("smartphone") || name.contains("samsung")) {
            return R.drawable.smartphone;
        }
        
        // Check Tablets
        if (name.contains("tablet") || name.contains("ipad")) {
            return R.drawable.tablet;
        }
        
        // Check Storage
        if (name.contains("storage") || name.contains("drive") || name.contains("ssd") || name.contains("hdd")) {
            return R.drawable.storage;
        }
        
        // Check Networking
        if (name.contains("network") || name.contains("wifi") || name.contains("router")) {
            return R.drawable.networking;
        }
        
        // Check Monitor
        if (name.contains("monitor") || name.contains("display") || name.contains("screen") || name.contains("tv")) {
            return R.drawable.monitor;
        }
        
        // Check all entries in map against category name
        for (Map.Entry<String, Integer> entry : CATEGORY_ICON_MAP.entrySet()) {
            if (name.contains(entry.getKey())) {
                Log.d(TAG, "Smart matched '" + categoryName + "' to key '" + entry.getKey() + "'");
                return entry.getValue();
            }
        }
        
        return R.drawable.laptop;
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
