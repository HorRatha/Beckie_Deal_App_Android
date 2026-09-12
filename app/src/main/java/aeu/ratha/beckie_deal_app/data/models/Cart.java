package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Cart model matching the API response structure.
 * Endpoint: GET /api/v1/cart
 */
public class Cart {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("user_id")
    private Integer userId;
    
    @SerializedName("session_id")
    private String sessionId;
    
    @SerializedName("status")
    private String status; // active, completed, abandoned
    
    @SerializedName("metadata")
    private Object metadata;
    
    @SerializedName("items")
    private List<CartItem> items;
    
    // Constructors
    public Cart() {}
    
    public Cart(int id, List<CartItem> items) {
        this.id = id;
        this.items = items;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Object getMetadata() {
        return metadata;
    }
    
    public List<CartItem> getItems() {
        return items;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    
    // Helper methods
    
    /**
     * Get total number of items in cart.
     */
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }
    
    /**
     * Get total number of units (sum of all quantities).
     */
    public int getTotalUnits() {
        if (items == null) return 0;
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
    
    /**
     * Calculate total price of all items.
     */
    public double getTotalPrice() {
        if (items == null) return 0.0;
        double total = 0.0;
        for (CartItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }
    
    /**
     * Check if cart is empty.
     */
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
    
    /**
     * Check if product is in cart.
     */
    public boolean hasProduct(String productId) {
        if (items == null || productId == null) return false;
        for (CartItem item : items) {
            if (productId.equals(item.getProductId())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get cart item for a product.
     */
    public CartItem getItemForProduct(String productId) {
        if (items == null || productId == null) return null;
        for (CartItem item : items) {
            if (productId.equals(item.getProductId())) {
                return item;
            }
        }
        return null;
    }
}
