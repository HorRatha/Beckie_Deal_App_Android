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
     * Matches by category name since API doesn't guarantee consistent IDs.
     * 
     * @param categoryName The category name from API
     * @return Pair of (title, description) or null if no match found
     */
    public Pair<String, String> getHeroContentForCategory(String categoryName) {
        if (categoryName == null) {
            return getDefaultHeroContent();
        }
        
        String name = categoryName.toLowerCase().trim();
        
        // Laptops & Computers
        if (name.contains("laptop")) {
            return new Pair<>(
                context.getString(R.string.hero_laptops_title),
                context.getString(R.string.hero_laptops_desc)
            );
        }
        if (name.contains("gaming laptop") || name.contains("gaming-laptop")) {
            return new Pair<>(
                context.getString(R.string.hero_gaming_laptops_title),
                context.getString(R.string.hero_gaming_laptops_desc)
            );
        }
        if (name.contains("ultrabook")) {
            return new Pair<>(
                context.getString(R.string.hero_ultrabooks_title),
                context.getString(R.string.hero_ultrabooks_desc)
            );
        }
        if (name.contains("desktop") || name.contains("computer")) {
            return new Pair<>(
                context.getString(R.string.hero_desktop_title),
                context.getString(R.string.hero_desktop_desc)
            );
        }
        if (name.contains("component")) {
            return new Pair<>(
                context.getString(R.string.hero_components_title),
                context.getString(R.string.hero_components_desc)
            );
        }
        
        // Smartphones & Mobile
        if (name.contains("phone") || name.contains("smartphone")) {
            return new Pair<>(
                context.getString(R.string.hero_smartphones_title),
                context.getString(R.string.hero_smartphones_desc)
            );
        }
        if (name.contains("tablet") && !name.contains("ipad")) {
            return new Pair<>(
                context.getString(R.string.hero_tablets_title),
                context.getString(R.string.hero_tablets_desc)
            );
        }
        if (name.contains("ipad")) {
            return new Pair<>(
                context.getString(R.string.hero_ipad_title),
                context.getString(R.string.hero_ipad_desc)
            );
        }
        
        // Gaming
        if (name.contains("gaming") && !name.contains("gaming laptop") && !name.contains("gaming monitor")) {
            return new Pair<>(
                context.getString(R.string.hero_gaming_title),
                context.getString(R.string.hero_gaming_desc)
            );
        }
        if (name.contains("console")) {
            return new Pair<>(
                context.getString(R.string.hero_gaming_consoles_title),
                context.getString(R.string.hero_gaming_consoles_desc)
            );
        }
        if (name.contains("peripheral")) {
            return new Pair<>(
                context.getString(R.string.hero_gaming_peripherals_title),
                context.getString(R.string.hero_gaming_peripherals_desc)
            );
        }
        
        // Audio
        if (name.contains("audio")) {
            return new Pair<>(
                context.getString(R.string.hero_audio_title),
                context.getString(R.string.hero_audio_desc)
            );
        }
        if (name.contains("headphone")) {
            return new Pair<>(
                context.getString(R.string.hero_headphones_title),
                context.getString(R.string.hero_headphones_desc)
            );
        }
        if (name.contains("earbud")) {
            return new Pair<>(
                context.getString(R.string.hero_earbuds_title),
                context.getString(R.string.hero_earbuds_desc)
            );
        }
        if (name.contains("speaker")) {
            return new Pair<>(
                context.getString(R.string.hero_speakers_title),
                context.getString(R.string.hero_speakers_desc)
            );
        }
        
        // Monitors & Displays
        if (name.contains("monitor")) {
            if (name.contains("gaming")) {
                return new Pair<>(
                    context.getString(R.string.hero_gaming_monitors_title),
                    context.getString(R.string.hero_gaming_monitors_desc)
                );
            }
            return new Pair<>(
                context.getString(R.string.hero_monitors_title),
                context.getString(R.string.hero_monitors_desc)
            );
        }
        if (name.contains("4k") || name.contains("display")) {
            return new Pair<>(
                context.getString(R.string.hero_4k_displays_title),
                context.getString(R.string.hero_4k_displays_desc)
            );
        }
        
        // Wearables
        if (name.contains("wearable")) {
            return new Pair<>(
                context.getString(R.string.hero_wearables_title),
                context.getString(R.string.hero_wearables_desc)
            );
        }
        if (name.contains("smartwatch")) {
            return new Pair<>(
                context.getString(R.string.hero_smartwatches_title),
                context.getString(R.string.hero_smartwatches_desc)
            );
        }
        if (name.contains("fitness") || name.contains("tracker")) {
            return new Pair<>(
                context.getString(R.string.hero_fitness_trackers_title),
                context.getString(R.string.hero_fitness_trackers_desc)
            );
        }
        
        // Accessories
        if (name.contains("accessor")) {
            return new Pair<>(
                context.getString(R.string.hero_accessories_title),
                context.getString(R.string.hero_accessories_desc)
            );
        }
        if (name.contains("case") || name.contains("protection")) {
            return new Pair<>(
                context.getString(R.string.hero_phone_cases_title),
                context.getString(R.string.hero_phone_cases_desc)
            );
        }
        if (name.contains("charger")) {
            return new Pair<>(
                context.getString(R.string.hero_chargers_title),
                context.getString(R.string.hero_chargers_desc)
            );
        }
        if (name.contains("bag")) {
            return new Pair<>(
                context.getString(R.string.hero_bags_title),
                context.getString(R.string.hero_bags_desc)
            );
        }
        
        // If no specific match, return default
        return getDefaultHeroContent();
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
