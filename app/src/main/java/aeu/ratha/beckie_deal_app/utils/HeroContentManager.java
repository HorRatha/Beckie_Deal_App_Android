package aeu.ratha.beckie_deal_app.utils;

import android.content.Context;
import androidx.core.util.Pair;

import aeu.ratha.beckie_deal_app.R;

/**
 * Manages hero section content (title and description) for categories and brands.
 * Maps category/brand names or IDs to their modern, engaging hero titles and descriptions.
 */
public class HeroContentManager {
    
    private final Context context;
    
    public HeroContentManager(Context context) {
        this.context = context;
    }
    
    /**
     * Get hero content (title and description) for a category.
     * Uses CategoryClassifier for reliable type classification.
     * 
     * @param categoryName The category name from API
     * @return Pair of (title, description) or null if no match found
     */
    public Pair<String, String> getHeroContentForCategory(String categoryName) {
        if (categoryName == null) {
            return getDefaultHeroContent();
        }
        
        // Create a temporary category to classify
        aeu.ratha.beckie_deal_app.data.models.Category tempCategory = 
            new aeu.ratha.beckie_deal_app.data.models.Category();
        tempCategory.setName(categoryName);
        
        // Use CategoryClassifier for reliable type detection
        String categoryType = CategoryClassifier.classifyCategory(tempCategory);
        
        return getHeroContentByType(categoryType);
    }
    
    /**
     * Get hero content by category type.
     * Centralizes hero content mapping for all category types.
     */
    private Pair<String, String> getHeroContentByType(String categoryType) {
        switch (categoryType) {
            case "accessories":
                return new Pair<>(
                    context.getString(R.string.hero_accessories_title),
                    context.getString(R.string.hero_accessories_desc)
                );
            case "laptop":
                return new Pair<>(
                    context.getString(R.string.hero_laptops_title),
                    context.getString(R.string.hero_laptops_desc)
                );
            case "mac":
                return new Pair<>(
                    context.getString(R.string.hero_apple_title),
                    context.getString(R.string.hero_apple_desc)
                );
            case "gaming":
                return new Pair<>(
                    context.getString(R.string.hero_gaming_laptops_title),
                    context.getString(R.string.hero_gaming_laptops_desc)
                );
            case "storage_network":
                return new Pair<>(
                    context.getString(R.string.hero_components_title),
                    context.getString(R.string.hero_components_desc)
                );
            default:
                return getDefaultHeroContent();
        }
    }
    
    /**
     * Get hero content (title and description) for a brand.
     * Matches by brand name.
     * 
     * @param brandName The brand name from API
     * @return Pair of (title, description) or null if no match found
     */
    public Pair<String, String> getHeroContentForBrand(String brandName) {
        if (brandName == null) {
            return getDefaultHeroContent();
        }
        
        String name = brandName.toLowerCase().trim();
        
        // Premium Brands
        if (name.contains("apple")) {
            return new Pair<>(
                context.getString(R.string.hero_apple_title),
                context.getString(R.string.hero_apple_desc)
            );
        }
        if (name.contains("samsung")) {
            return new Pair<>(
                context.getString(R.string.hero_samsung_title),
                context.getString(R.string.hero_samsung_desc)
            );
        }
        if (name.contains("microsoft")) {
            return new Pair<>(
                context.getString(R.string.hero_microsoft_title),
                context.getString(R.string.hero_microsoft_desc)
            );
        }
        
        // Gaming Specialists
        if (name.contains("asus") && name.contains("rog")) {
            return new Pair<>(
                context.getString(R.string.hero_asus_rog_title),
                context.getString(R.string.hero_asus_rog_desc)
            );
        }
        if (name.contains("razer")) {
            return new Pair<>(
                context.getString(R.string.hero_razer_title),
                context.getString(R.string.hero_razer_desc)
            );
        }
        if (name.contains("corsair")) {
            return new Pair<>(
                context.getString(R.string.hero_corsair_title),
                context.getString(R.string.hero_corsair_desc)
            );
        }
        
        // Professional & Business
        if (name.contains("dell")) {
            return new Pair<>(
                context.getString(R.string.hero_dell_title),
                context.getString(R.string.hero_dell_desc)
            );
        }
        if (name.contains("hp")) {
            return new Pair<>(
                context.getString(R.string.hero_hp_title),
                context.getString(R.string.hero_hp_desc)
            );
        }
        if (name.contains("lenovo")) {
            return new Pair<>(
                context.getString(R.string.hero_lenovo_title),
                context.getString(R.string.hero_lenovo_desc)
            );
        }
        
        // Audio Leaders
        if (name.contains("sony")) {
            return new Pair<>(
                context.getString(R.string.hero_sony_title),
                context.getString(R.string.hero_sony_desc)
            );
        }
        if (name.contains("bose")) {
            return new Pair<>(
                context.getString(R.string.hero_bose_title),
                context.getString(R.string.hero_bose_desc)
            );
        }
        if (name.contains("sennheiser")) {
            return new Pair<>(
                context.getString(R.string.hero_sennheiser_title),
                context.getString(R.string.hero_sennheiser_desc)
            );
        }
        
        // Emerging/Budget Brands
        if (name.contains("xiaomi")) {
            return new Pair<>(
                context.getString(R.string.hero_xiaomi_title),
                context.getString(R.string.hero_xiaomi_desc)
            );
        }
        if (name.contains("oneplus")) {
            return new Pair<>(
                context.getString(R.string.hero_oneplus_title),
                context.getString(R.string.hero_oneplus_desc)
            );
        }
        if (name.contains("realme")) {
            return new Pair<>(
                context.getString(R.string.hero_realme_title),
                context.getString(R.string.hero_realme_desc)
            );
        }
        
        // If no specific match, return default
        return getDefaultHeroContent();
    }
    
    /**
     * Get default hero content (fallback).
     */
    private Pair<String, String> getDefaultHeroContent() {
        return new Pair<>(
            context.getString(R.string.hero_accessories_title),
            context.getString(R.string.hero_accessories_desc)
        );
    }
}
