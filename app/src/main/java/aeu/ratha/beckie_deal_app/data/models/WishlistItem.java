package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * WishlistItem model representing an item in user's wishlist.
 * Based on API response structure from GET /wishlist
 */
public class WishlistItem {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("wishlist_id")
    private int wishlistId;
    
    @SerializedName("product_id")
    private String productId;
    
    @SerializedName("product_variant_id")
    private String productVariantId;
    
    @SerializedName("quantity")
    private int quantity;
    
    @SerializedName("metadata")
    private Object metadata;
    
    @SerializedName("product")
    private Product product;
    
    @SerializedName("variant")
    private Object variant;  // Can be null or ProductVariant if you have that model
    
    // Constructors
    public WishlistItem() {
    }
    
    public WishlistItem(int id, String productId, Product product, int quantity) {
        this.id = id;
        this.productId = productId;
        this.product = product;
        this.quantity = quantity;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public int getWishlistId() {
        return wishlistId;
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
    
    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
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
    public String getProductName() {
        return product != null ? product.getName() : "";
    }
    
    public String getProductPrice() {
        return product != null ? product.getFormattedPrice() : "0.00";
    }
    
    public String getProductThumbnail() {
        return product != null ? product.getThumbnail() : null;
    }
    
    public boolean isInStock() {
        return product != null && product.isInStock();
    }
}
