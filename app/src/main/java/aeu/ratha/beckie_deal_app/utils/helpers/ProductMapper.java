package aeu.ratha.beckie_deal_app.utils.helpers;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Maps between API Product model and UI Product model (MainActivity.Product).
 * This maintains backwards compatibility with existing UI code while integrating API data.
 */
public class ProductMapper {
    
    /**
     * Convert API Product to UI Product (MainActivity.Product).
     * 
     * @param apiProduct Product from API
     * @return MainActivity.Product for UI rendering
     */
    public static MainActivity.Product toUiProduct(aeu.ratha.beckie_deal_app.data.models.Product apiProduct) {
        if (apiProduct == null) return null;
        
        // Extract product ID (CRITICAL for ProductDetailActivity API integration)
        String id = apiProduct.getId();  // Now returns String (UUID)
        android.util.Log.d("ProductMapper", "Converting API product - ID: " + id + ", Name: " + apiProduct.getName());
        
        // Extract basic fields
        String name = apiProduct.getName() != null ? apiProduct.getName() : "Unknown Product";
        String price = "$" + apiProduct.getPrice();
        
        // Generate rating from sales and views (mock calculation since API doesn't have rating)
        String rating = generateRating(apiProduct);
        
        boolean isFavorite = false; // Will be synced with FavoriteManager later
        String imageUrl = ImageUrlHelper.getImageUrlOrFallback(apiProduct.getThumbnail());
        
        // Extract extended fields
        String sku = apiProduct.getSku() != null ? apiProduct.getSku() : "";
        boolean inStock = apiProduct.isInStock();
        int warrantyYears = 1; // Default warranty, could be extracted from product description
        String specs = generateSpecs(apiProduct);
        String freeItems = generateFreeItems(apiProduct);
        
        // Extract category and brand names
        String categoryName = getCategoryName(apiProduct);
        String brandName = getBrandName(apiProduct);
        
        return new MainActivity.Product(
            id, name, price, rating, isFavorite, imageUrl,
            sku, inStock, warrantyYears, specs, freeItems,
            categoryName, brandName
        );
    }
    
    /**
     * Convert list of API Products to list of UI Products.
     * 
     * @param apiProducts List of products from API
     * @return List of MainActivity.Product for UI
     */
    public static List<MainActivity.Product> toUiProducts(List<aeu.ratha.beckie_deal_app.data.models.Product> apiProducts) {
        List<MainActivity.Product> uiProducts = new ArrayList<>();
        if (apiProducts == null) return uiProducts;
        
        for (aeu.ratha.beckie_deal_app.data.models.Product apiProduct : apiProducts) {
            MainActivity.Product uiProduct = toUiProduct(apiProduct);
            if (uiProduct != null) {
                uiProducts.add(uiProduct);
            }
        }
        
        return uiProducts;
    }
    
    /**
     * Generate a rating score based on product metrics.
     * Uses sales count, views, and featured status to calculate a rating between 4.0 and 5.0.
     * 
     * @param apiProduct Product from API
     * @return Rating string (e.g., "4.8")
     */
    private static String generateRating(aeu.ratha.beckie_deal_app.data.models.Product apiProduct) {
        // Base rating
        double rating = 4.0;
        
        // Boost for featured products
        if (apiProduct.isFeatured()) {
            rating += 0.5;
        }
        
        // Boost based on sales (0-0.3 boost)
        int sales = apiProduct.getSalesCount();
        if (sales > 0) {
            rating += Math.min(0.3, sales * 0.03);
        }
        
        // Boost based on views (0-0.2 boost)
        int views = apiProduct.getViewsCount();
        if (views > 0) {
            rating += Math.min(0.2, views * 0.002);
        }
        
        // Cap at 5.0
        rating = Math.min(5.0, rating);
        
        // Round to 1 decimal place
        return String.format("%.1f", rating);
    }
    
    /**
     * Generate specs string from product details.
     * 
     * @param apiProduct Product from API
     * @return Formatted specs string with newlines
     */
    private static String generateSpecs(aeu.ratha.beckie_deal_app.data.models.Product apiProduct) {
        StringBuilder specs = new StringBuilder();
        
        // Add short description if available
        if (apiProduct.getShortDescription() != null && !apiProduct.getShortDescription().isEmpty()) {
            String desc = apiProduct.getShortDescription();
            // Truncate if too long
            if (desc.length() > 100) {
                desc = desc.substring(0, 97) + "...";
            }
            specs.append("-").append(desc);
        }
        
        // Add stock info
        if (apiProduct.isInStock()) {
            if (specs.length() > 0) specs.append("\n");
            specs.append("-").append(apiProduct.getStockQuantity()).append(" units available");
        }
        
        // Add dimensions if available
        if (apiProduct.getWeight() != null && !apiProduct.getWeight().isEmpty()) {
            if (specs.length() > 0) specs.append("\n");
            specs.append("-Weight: ").append(apiProduct.getWeight());
        }
        
        return specs.toString();
    }
    
    /**
     * Generate free items text from product details.
     * Backend does not provide free shipping info, so always return empty string.
     * Free shipping will be determined by backend in future updates.
     * 
     * @param apiProduct Product from API
     * @return Free items text (currently always empty)
     */
    private static String generateFreeItems(aeu.ratha.beckie_deal_app.data.models.Product apiProduct) {
        // Backend does not provide free shipping information
        // This will be removed or updated when backend adds free_shipping_info field
        return "";
    }
    
    /**
     * Get category name from API product.
     * 
     * @param apiProduct Product from API
     * @return Category name or "Uncategorized"
     */
    public static String getCategoryName(aeu.ratha.beckie_deal_app.data.models.Product apiProduct) {
        if (apiProduct != null && apiProduct.getCategory() != null) {
            return apiProduct.getCategory().getName();
        }
        return "Uncategorized";
    }
    
    /**
     * Get brand name from API product.
     * 
     * @param apiProduct Product from API
     * @return Brand name or "Generic"
     */
    public static String getBrandName(aeu.ratha.beckie_deal_app.data.models.Product apiProduct) {
        if (apiProduct != null && apiProduct.getBrand() != null) {
            return apiProduct.getBrand().getName();
        }
        return "Generic";
    }
}
