package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Cart item model matching the API response structure.
 */
public class CartItem {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("cart_id")
    private int cartId;
    
    @SerializedName("product_id")
    private String productId; // UUID
    
    @SerializedName("product_variant_id")
    private String productVariantId;
    
    @SerializedName("quantity")
    private int quantity;
    
    @SerializedName("price")
    private String price; // Price at time of adding
    
    @SerializedName("metadata")
    private Object metadata;
    
    @SerializedName("product")
    private Product product; // Full product object from API
    
    @SerializedName("variant")
    private Object variant;
    
    // Constructors
    public CartItem() {}
    
    public CartItem(int id, String productId, int quantity, Product product) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.product = product;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public int getCartId() {
        return cartId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public String getProductVariantId() {
        return productVariantId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public String getPrice() {
        return price;
    }
    
    public Object getMetadata() {
        return metadata;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public Object getVariant() {
        return variant;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setCartId(int cartId) {
        this.cartId = cartId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public void setProductVariantId(String productVariantId) {
        this.productVariantId = productVariantId;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void setPrice(String price) {
        this.price = price;
    }
    
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public void setVariant(Object variant) {
        this.variant = variant;
    }
    
    // Helper methods
    
    /**
     * Get product name (from embedded product object).
     */
    public String getProductName() {
        return product != null ? product.getName() : "Unknown";
    }
    
    /**
     * Get product price as double.
     */
    public double getPriceAsDouble() {
        if (price == null || price.isEmpty()) {
            // Fall back to product effective price (sale price if available, otherwise regular price)
            return product != null ? product.getEffectivePrice() : 0.0;
        }
        try {
            return Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Calculate subtotal (price × quantity).
     */
    public double getSubtotal() {
        return getPriceAsDouble() * quantity;
    }
    
    /**
     * Get formatted subtotal.
     */
    public String getFormattedSubtotal() {
        return "$" + String.format("%.2f", getSubtotal());
    }
    
    /**
     * Check if product is in stock.
     */
    public boolean isInStock() {
        return product != null && product.isInStock();
    }
    
    /**
     * Get product thumbnail.
     */
    public String getProductThumbnail() {
        return product != null ? product.getThumbnail() : null;
    }
}
