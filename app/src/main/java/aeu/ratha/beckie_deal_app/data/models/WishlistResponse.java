package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * WishlistResponse wrapper for API responses.
 * Follows standard API response format: { status, message, data }
 */
public class WishlistResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Wishlist data;
    
    // Constructors
    public WishlistResponse() {
    }
    
    public WishlistResponse(String status, String message, Wishlist data) {
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
    
    public Wishlist getData() {
        return data;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(Wishlist data) {
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
