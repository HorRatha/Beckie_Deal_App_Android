package aeu.ratha.beckie_deal_app.utils.helpers;

import aeu.ratha.beckie_deal_app.BuildConfig;

/**
 * Helper class for handling image URLs.
 * Converts relative image paths from API to absolute URLs.
 */
public class ImageUrlHelper {
    
    // Extract base domain from API_BASE_URL (remove /api/v1/ suffix)
    private static final String BASE_URL = BuildConfig.API_BASE_URL
            .replace("/api/v1/", "/")
            .replace("/api/v1", "/");
    
    /**
     * Convert a relative or absolute image URL to a full URL.
     * 
     * @param imageUrl Image URL from API (can be relative like "/uploads/..." or absolute)
     * @return Full absolute URL, or default placeholder if null/empty
     */
    public static String getFullImageUrl(String imageUrl) {
        // Handle null or empty
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return getFallbackImageUrl();
        }
        
        // Already absolute URL (starts with http:// or https://)
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }
        
        // Relative URL - prepend base URL
        // Remove leading slash if present to avoid double slashes
        String path = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
        return BASE_URL + path;
    }
    
    /**
     * Get fallback image URL for products without images.
     * 
     * @return Fallback image URL
     */
    public static String getFallbackImageUrl() {
        return "https://images.unsplash.com/photo-1498049794561-7780e7231661?w=400&h=400&fit=crop";
    }
    
    /**
     * Get full image URL with fallback.
     * If the provided URL is null/empty, returns fallback URL.
     * 
     * @param imageUrl Image URL from API
     * @return Full absolute URL or fallback
     */
    public static String getImageUrlOrFallback(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return getFallbackImageUrl();
        }
        return getFullImageUrl(imageUrl);
    }
}
