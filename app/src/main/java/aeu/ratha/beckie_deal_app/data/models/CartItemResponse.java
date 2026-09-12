package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper for CartItem API responses.
 * Used for add item and update item responses.
 */
public class CartItemResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private CartItem data;
    
    // Constructors
    public CartItemResponse() {}
    
    public CartItemResponse(String status, String message, CartItem data) {
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
    
    public CartItem getData() {
        return data;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(CartItem data) {
        this.data = data;
    }
    
    // Helper methods
    
    /**
     * Check if response was successful.
     */
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
    
    /**
     * Check if cart item data exists.
     */
    public boolean hasData() {
        return data != null;
    }
}
