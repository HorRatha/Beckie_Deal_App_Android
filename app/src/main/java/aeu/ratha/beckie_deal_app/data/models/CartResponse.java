package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper for Cart API responses.
 * Matches backend response format: { status, message, data: Cart }
 */
public class CartResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Cart data;
    
    // Constructors
    public CartResponse() {}
    
    public CartResponse(String status, String message, Cart data) {
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
    
    public Cart getData() {
        return data;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(Cart data) {
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
     * Check if cart data exists.
     */
    public boolean hasData() {
        return data != null;
    }
    
    /**
     * Check if cart is empty.
     */
    public boolean isCartEmpty() {
        return data == null || data.isEmpty();
    }
}
