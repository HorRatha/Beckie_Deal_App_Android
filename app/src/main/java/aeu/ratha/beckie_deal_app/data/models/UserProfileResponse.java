package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for user profile API endpoint.
 * Matches the Laravel backend response format.
 */
public class UserProfileResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private User data;
    
    @SerializedName("errors")
    private Object errors;

    // Getters
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
    
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public User getData() {
        return data;
    }
    
    public Object getErrors() {
        return errors;
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
    
    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
