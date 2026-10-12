package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper for Profile API responses.
 * Matches backend response format: { status, message, data: User }
 * 
 * Response from GET /api/v1/profile:
 * {
 *   "status": "success",
 *   "message": "",
 *   "data": {
 *     "id": 15,
 *     "email": "user@example.com",
 *     "phone": null,
 *     "first_name": "John",
 *     "last_name": "Doe",
 *     "name": "John Doe",
 *     "avatar": null,
 *     "role": "user",
 *     "is_active": true,
 *     "is_banned": false,
 *     "created_at": "2026-09-06 05:29:30",
 *     "updated_at": "2026-09-06 05:29:30"
 *   }
 * }
 */
public class ProfileApiResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private User data;
    
    // Constructors
    public ProfileApiResponse() {}
    
    public ProfileApiResponse(String status, String message, User data) {
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
    
    public User getData() {
        return data;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(User data) {
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
     * Check if user data exists.
     */
    public boolean hasData() {
        return data != null;
    }
}
