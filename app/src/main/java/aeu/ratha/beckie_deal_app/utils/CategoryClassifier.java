package aeu.ratha.beckie_deal_app.utils;

import aeu.ratha.beckie_deal_app.data.models.Category;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classifies categories into curated types based on their name and properties.
 * 
 * Category Types:
 * - "accessories" → Keyboard, Mouse, Headphone, Router, Cables, Stands
 * - "laptop" → All general laptops (ASUS VivoBook, Dell XPS, HP Pavilion)
 * - "mac" → Apple products and MacBooks
 * - "gaming" → Gaming laptops (ROG, MSI RTX, Razer Blade)
 * - "storage_network" → SSDs, HDDs, RAM, Network Cards, Routers
 */
public class CategoryClassifier {
    
    private static final String TYPE_ACCESSORIES = "accessories";
    private static final String TYPE_LAPTOP = "laptop";
    private static final String TYPE_MAC = "mac";
    private static final String TYPE_GAMING = "gaming";
    private static final String TYPE_STORAGE_NETWORK = "storage_network";
    private static final String TYPE_OTHER = "other";
    
    /**
     * Classify a category based on its name and properties.
     * Returns the category type string.
     */
    public static String classifyCategory(Category category) {
        if (category == null || category.getName() == null) {
            return TYPE_OTHER;
        }
        
        return classifyByName(category.getName());
    }
    
    /**
     * Classify by category name (case-insensitive).
     * Priority order:
     * 1. Gaming (most specific)
     * 2. Mac (Apple products)
     * 3. Storage & Network
     * 4. Accessories
     * 5. Laptop (general)
     */
    private static String classifyByName(String name) {
        String lower = name.toLowerCase().trim();
        
        // Gaming laptops - most specific match
        if ((lower.contains("gaming") && lower.contains("laptop")) ||
            lower.contains("rog") ||  // ASUS ROG
            lower.contains("msi") && lower.contains("gaming")) {
            return TYPE_GAMING;
        }
        
        // Apple/Mac products
        if (lower.contains("mac") || 
            lower.contains("macbook") ||
            lower.contains("apple") ||
            lower.contains("ipad") ||
            lower.contains("iphone")) {
            return TYPE_MAC;
        }
        
        // Storage & Networking
        if (lower.contains("storage") ||
            lower.contains("network") ||
            lower.contains("ssd") ||
            lower.contains("hdd") ||
            lower.contains("ram") ||
            lower.contains("memory") ||
            lower.contains("router") ||
            lower.contains("modem") ||
            lower.contains("switch")) {
            return TYPE_STORAGE_NETWORK;
        }
        
        // Accessories - broad category
        if (lower.contains("accessory") ||
            lower.contains("accessories") ||
            lower.contains("keyboard") ||
            lower.contains("mouse") ||
            lower.contains("headphone") ||
            lower.contains("speaker") ||
            lower.contains("charger") ||
            lower.contains("cable") ||
            lower.contains("stand") ||
            lower.contains("dock") ||
            lower.contains("hub") ||
            lower.contains("adapter") ||
            lower.contains("usb") ||
            lower.contains("hdmi")) {
            return TYPE_ACCESSORIES;
        }
        
        // General laptops
        if (lower.contains("laptop") ||
            lower.contains("notebook") ||
            lower.contains("computer") ||
            lower.contains("asus") ||
            lower.contains("dell") ||
            lower.contains("hp") ||
            lower.contains("lenovo") ||
            lower.contains("acer") ||
            lower.contains("asus vivobook") ||
            lower.contains("dell xps") ||
            lower.contains("hp pavilion")) {
            return TYPE_LAPTOP;
        }
        
        return TYPE_OTHER;
    }
    
    /**
     * Get a user-friendly display name for a category type.
     */
    public static String getDisplayName(String categoryType) {
        switch (categoryType) {
            case TYPE_ACCESSORIES:
                return "Accessories";
            case TYPE_LAPTOP:
                return "Laptops";
            case TYPE_MAC:
                return "Mac & Apple";
            case TYPE_GAMING:
                return "Gaming Laptops";
            case TYPE_STORAGE_NETWORK:
                return "Storage & Network";
            default:
                return "Products";
        }
    }
    
    /**
     * Get all available category types.
     */
    public static List<String> getAllTypes() {
        List<String> types = new ArrayList<>();
        types.add(TYPE_ACCESSORIES);
        types.add(TYPE_LAPTOP);
        types.add(TYPE_MAC);
        types.add(TYPE_GAMING);
        types.add(TYPE_STORAGE_NETWORK);
        return types;
    }
    
    /**
     * Get type-specific subcategories.
     * Used for filtering within a category type.
     */
    public static List<String> getSubcategoriesForType(String categoryType) {
        List<String> subcategories = new ArrayList<>();
        
        switch (categoryType) {
            case TYPE_ACCESSORIES:
                subcategories.add("Keyboard");
                subcategories.add("Mouse");
                subcategories.add("Headphones");
                subcategories.add("Speakers");
                subcategories.add("Router");
                subcategories.add("Cables & Adapters");
                subcategories.add("Stands & Docks");
                subcategories.add("Chargers");
                break;
            case TYPE_LAPTOP:
                subcategories.add("Budget");
                subcategories.add("Mid-Range");
                subcategories.add("Premium");
                subcategories.add("Ultrabook");
                break;
            case TYPE_MAC:
                subcategories.add("MacBook Pro");
                subcategories.add("MacBook Air");
                subcategories.add("iMac");
                subcategories.add("Mac Accessories");
                break;
            case TYPE_GAMING:
                subcategories.add("Entry Level");
                subcategories.add("Mid Range");
                subcategories.add("High End");
                break;
            case TYPE_STORAGE_NETWORK:
                subcategories.add("SSDs");
                subcategories.add("HDDs");
                subcategories.add("RAM");
                subcategories.add("Networking");
                subcategories.add("Storage Devices");
                break;
        }
        
        return subcategories;
    }
    
    /**
     * Check if a product name matches a specific subcategory.
     * Used for product filtering within a category type.
     */
    public static boolean matchesSubcategory(String productName, String subcategory) {
        if (productName == null || subcategory == null) {
            return false;
        }
        
        String lower = productName.toLowerCase();
        String subLower = subcategory.toLowerCase();
        
        return lower.contains(subLower);
    }
    
    /**
     * Get an icon name/resource ID for a category type.
     * Map this to your drawable resources.
     */
    public static String getIconName(String categoryType) {
        switch (categoryType) {
            case TYPE_ACCESSORIES:
                return "ic_accessories";
            case TYPE_LAPTOP:
                return "ic_laptop";
            case TYPE_MAC:
                return "ic_mac";
            case TYPE_GAMING:
                return "ic_gaming";
            case TYPE_STORAGE_NETWORK:
                return "ic_storage";
            default:
                return "ic_products";
        }
    }
    
    /**
     * Classify multiple categories and return a map of type -> list of categories.
     */
    public static Map<String, List<Category>> groupCategoriesByType(List<Category> categories) {
        Map<String, List<Category>> grouped = new HashMap<>();
        
        // Initialize all type buckets
        for (String type : getAllTypes()) {
            grouped.put(type, new ArrayList<>());
        }
        grouped.put(TYPE_OTHER, new ArrayList<>());
        
        // Classify and group
        for (Category category : categories) {
            String type = classifyCategory(category);
            grouped.get(type).add(category);
        }
        
        return grouped;
    }
}
