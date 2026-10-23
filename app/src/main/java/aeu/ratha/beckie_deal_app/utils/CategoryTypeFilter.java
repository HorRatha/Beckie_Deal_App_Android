package aeu.ratha.beckie_deal_app.utils;

import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Filters products by category type.
 * Provides utility methods to organize and filter products based on their category classification.
 */
public class CategoryTypeFilter {
    
    /**
     * Filter products by category type.
     * Matches products whose category name matches the given type classification.
     */
    public static List<MainActivity.Product> filterByType(
            List<MainActivity.Product> products,
            String categoryType) {
        
        List<MainActivity.Product> filtered = new ArrayList<>();
        
        if (products == null || categoryType == null) {
            return filtered;
        }
        
        for (MainActivity.Product product : products) {
            if (product.categoryName != null) {
                String productType = classifyProductByName(product.categoryName);
                
                if (productType.equals(categoryType)) {
                    filtered.add(product);
                }
            }
        }
        
        return filtered;
    }
    
    /**
     * Classify a product category by name directly.
     */
    private static String classifyProductByName(String categoryName) {
        if (categoryName == null) {
            return "other";
        }
        
        // Create a temporary category and classify it
        aeu.ratha.beckie_deal_app.data.models.Category temp = 
            new aeu.ratha.beckie_deal_app.data.models.Category();
        temp.setName(categoryName);
        return CategoryClassifier.classifyCategory(temp);
    }
    
    /**
     * Filter products by category type AND subcategory.
     * Example: type="accessories", subcategory="Mouse"
     */
    public static List<MainActivity.Product> filterByTypeAndSubcategory(
            List<MainActivity.Product> products,
            String categoryType,
            String subcategory) {
        
        // First filter by type
        List<MainActivity.Product> byType = filterByType(products, categoryType);
        
        // Then filter by subcategory match
        List<MainActivity.Product> filtered = new ArrayList<>();
        for (MainActivity.Product product : byType) {
            if (CategoryClassifier.matchesSubcategory(product.name, subcategory)) {
                filtered.add(product);
            }
        }
        
        return filtered;
    }
    
    /**
     * Filter products by type and brand.
     * Example: type="gaming", brand="ASUS"
     */
    public static List<MainActivity.Product> filterByTypeAndBrand(
            List<MainActivity.Product> products,
            String categoryType,
            String brand) {
        
        // First filter by type
        List<MainActivity.Product> byType = filterByType(products, categoryType);
        
        // Then filter by brand
        List<MainActivity.Product> filtered = new ArrayList<>();
        for (MainActivity.Product product : byType) {
            if (product.brandName != null && 
                product.brandName.equalsIgnoreCase(brand)) {
                filtered.add(product);
            }
        }
        
        return filtered;
    }
    
    /**
     * Get recommended products for a category type.
     * Useful for home page or category showcase.
     */
    public static List<MainActivity.Product> getRecommendedByType(
            List<MainActivity.Product> products,
            String categoryType,
            int limit) {
        
        List<MainActivity.Product> byType = filterByType(products, categoryType);
        
        if (byType.size() <= limit) {
            return byType;
        }
        
        return new ArrayList<>(byType.subList(0, limit));
    }
    
    /**
     * Count products by category type.
     */
    public static int countByType(
            List<MainActivity.Product> products,
            String categoryType) {
        
        return filterByType(products, categoryType).size();
    }
    
    /**
     * Group products by category type.
     * Returns a map where key is type and value is list of products.
     */
    public static java.util.Map<String, List<MainActivity.Product>> groupByType(
            List<MainActivity.Product> products) {
        
        java.util.Map<String, List<MainActivity.Product>> grouped = 
            new java.util.HashMap<>();
        
        // Initialize all type buckets
        for (String type : CategoryClassifier.getAllTypes()) {
            grouped.put(type, new ArrayList<>());
        }
        grouped.put("other", new ArrayList<>());
        
        // Group products by their type
        for (MainActivity.Product product : products) {
            if (product.categoryName != null) {
                String type = classifyProductByName(product.categoryName);
                grouped.get(type).add(product);
            }
        }
        
        return grouped;
    }
    
    
    /**
     * Get category type from product's category name.
     */
    public static String getProductCategoryType(MainActivity.Product product) {
        if (product == null || product.categoryName == null) {
            return "other";
        }
        
        return classifyProductByName(product.categoryName);
    }
}
