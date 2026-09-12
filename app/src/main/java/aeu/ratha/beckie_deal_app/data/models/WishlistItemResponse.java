package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * WishlistItemResponse wrapper for add/update wishlist item API responses.
 * Used for POST /wishlist/items and DELETE responses
 */
public class WishlistItemResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private WishlistItem data;
    
    // Constructors
    public WishlistItemResponse() {
    }
    
    public WishlistItemResponse(String status, String message, WishlistItem data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
    
    // Getters
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public WishlistItem getData() {
        return data;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(WishlistItem data) {
        this.data = data;
    }
    
    // Helper methods
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
    
    public boolean hasData() {
        return data != null;
    }
}
