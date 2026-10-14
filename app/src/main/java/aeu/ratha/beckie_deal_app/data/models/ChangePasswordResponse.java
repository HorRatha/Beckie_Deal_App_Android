package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Object data; // null in the response
    
    public ChangePasswordResponse() {}
    
    public ChangePasswordResponse(String status, String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
}

