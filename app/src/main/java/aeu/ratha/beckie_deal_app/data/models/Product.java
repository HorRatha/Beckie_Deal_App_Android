package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Product model matching the API response structure.
 * Endpoint: GET /api/v1/products
 */
public class Product {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("category_id")
    private int categoryId;
    
    @SerializedName("brand_id")
    private int brandId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("slug")
    private String slug;
    
    @SerializedName("sku")
    private String sku;
    
    @SerializedName("barcode")
    private String barcode;
    
    @SerializedName("short_description")
    private String shortDescription;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("price")
    private String price; // String in API, convert to double when needed
    
    @SerializedName("sale_price")
    private String salePrice;
    
    @SerializedName("cost_price")
    private String costPrice;
    
    @SerializedName("stock_quantity")
    private int stockQuantity;
    
    @SerializedName("min_stock_alert")
    private int minStockAlert;
    
    @SerializedName("track_inventory")
    private boolean trackInventory;
    
    @SerializedName("in_stock")
    private boolean inStock;
    
    @SerializedName("weight")
    private String weight;
    
    @SerializedName("length")
    private String length;
    
    @SerializedName("width")
    private String width;
    
    @SerializedName("height")
    private String height;
    
    @SerializedName("thumbnail")
    private String thumbnail;
    
    @SerializedName("meta_title")
    private String metaTitle;
    
    @SerializedName("meta_description")
    private String metaDescription;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("is_featured")
    private boolean isFeatured;
    
    @SerializedName("is_digital")
    private boolean isDigital;
    
    @SerializedName("views_count")
    private int viewsCount;
    
    @SerializedName("sales_count")
    private int salesCount;
    
    @SerializedName("sort_order")
    private int sortOrder;
    
    @SerializedName("category")
    private Category category;
    
    @SerializedName("brand")
    private Brand brand;
    
    // Constructors
    public Product() {}
    
    public Product(int id, String name, String price, String thumbnail, 
                   boolean isFeatured, boolean inStock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.thumbnail = thumbnail;
        this.isFeatured = isFeatured;
        this.inStock = inStock;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public int getBrandId() {
        return brandId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public String getSku() {
        return sku;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public String getShortDescription() {
        return shortDescription;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getPrice() {
        return price;
    }
    
    /**
     * Get price as double for calculations.
     */
    public double getPriceAsDouble() {
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    public String getSalePrice() {
        return salePrice;
    }
    
    /**
     * Get sale price as double for calculations.
     */
    public double getSalePriceAsDouble() {
        if (salePrice == null || salePrice.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(salePrice);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Get the effective price (sale price if available, otherwise regular price).
     */
    public double getEffectivePrice() {
        double sPrice = getSalePriceAsDouble();
        return sPrice > 0 ? sPrice : getPriceAsDouble();
    }
    
    public String getCostPrice() {
        return costPrice;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public int getMinStockAlert() {
        return minStockAlert;
    }
    
    public boolean isTrackInventory() {
        return trackInventory;
    }
    
    public boolean isInStock() {
        return inStock;
    }
    
    public String getWeight() {
        return weight;
    }
    
    public String getLength() {
        return length;
    }
    
    public String getWidth() {
        return width;
    }
    
    public String getHeight() {
        return height;
    }
    
    public String getThumbnail() {
        return thumbnail;
    }
    
    public String getMetaTitle() {
        return metaTitle;
    }
    
    public String getMetaDescription() {
        return metaDescription;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean isFeatured() {
        return isFeatured;
    }
    
    public boolean isDigital() {
        return isDigital;
    }
    
    public int getViewsCount() {
        return viewsCount;
    }
    
    public int getSalesCount() {
        return salesCount;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public Brand getBrand() {
        return brand;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }
    
    public void setCostPrice(String costPrice) {
        this.costPrice = costPrice;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public void setMinStockAlert(int minStockAlert) {
        this.minStockAlert = minStockAlert;
    }
    
    public void setTrackInventory(boolean trackInventory) {
        this.trackInventory = trackInventory;
    }
    
    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
    
    public void setWeight(String weight) {
        this.weight = weight;
    }
    
    public void setLength(String length) {
        this.length = length;
    }
    
    public void setWidth(String width) {
        this.width = width;
    }
    
    public void setHeight(String height) {
        this.height = height;
    }
    
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    
    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }
    
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }
    
    public void setDigital(boolean digital) {
        isDigital = digital;
    }
    
    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }
    
    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public void setBrand(Brand brand) {
        this.brand = brand;
    }
    
    /**
     * Check if product has discount (sale price is set and lower than regular price).
     */
    public boolean hasDiscount() {
        return salePrice != null && !salePrice.isEmpty() 
            && getSalePriceAsDouble() > 0 
            && getSalePriceAsDouble() < getPriceAsDouble();
    }
    
    /**
     * Calculate discount percentage.
     */
    public int getDiscountPercentage() {
        if (!hasDiscount()) return 0;
        double regular = getPriceAsDouble();
        double sale = getSalePriceAsDouble();
        if (regular <= 0) return 0;
        return (int) Math.round(((regular - sale) / regular) * 100);
    }
    
    /**
     * Get formatted price string.
     */
    public String getFormattedPrice() {
        return "$" + String.format("%.2f", getEffectivePrice());
    }
    
    /**
     * Check if product is low in stock.
     */
    public boolean isLowStock() {
        return trackInventory && inStock && stockQuantity > 0 
            && stockQuantity <= minStockAlert;
    }
}
