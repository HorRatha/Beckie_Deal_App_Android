package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Wishlist model representing user's wishlist.
 * Based on API response structure from GET /wishlist
 */
public class Wishlist {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("user_id")
    private int userId;
    
    @SerializedName("session_id")
    private String sessionId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("is_public")
    private boolean isPublic;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("metadata")
    private Object metadata;
    
    @SerializedName("items")
    private List<WishlistItem> items;
    
    // Constructors
    public Wishlist() {
    }
    
    public Wishlist(int id, int userId, String name, List<WishlistItem> items) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.items = items;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public Object getMetadata() {
        return metadata;
    }
    
    public List<WishlistItem> getItems() {
        return items;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
    
    public void setItems(List<WishlistItem> items) {
        this.items = items;
    }
    
    // Helper methods
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }
    
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
    
    public boolean hasProduct(String productId) {
        if (items == null) return false;
        for (WishlistItem item : items) {
            if (item.getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }
}
