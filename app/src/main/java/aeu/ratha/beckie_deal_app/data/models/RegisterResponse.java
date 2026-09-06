package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private RegisterData data;
    
    @SerializedName("errors")
    private Object errors;

    // Inner class for data object
    public static class RegisterData {
        @SerializedName("access_token")
        private String accessToken;
        
        @SerializedName("refresh_token")
        private String refreshToken;
        
        @SerializedName("token_type")
        private String tokenType;
        
        @SerializedName("expires_at")
        private String expiresAt;

        @SerializedName("user")
        private User user;

        // Getters
        public String getAccessToken() {
            return accessToken;
        }
        
        public String getRefreshToken() {
            return refreshToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public String getExpiresAt() {
            return expiresAt;
        }
        
        public int getExpiresIn() {
            // For compatibility - return a default value
            return 3600;
        }

        public User getUser() {
            return user;
        }

        // Setters
        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
        
        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public void setExpiresAt(String expiresAt) {
            this.expiresAt = expiresAt;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

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

    public RegisterData getData() {
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

    public void setData(RegisterData data) {
        this.data = data;
    }
    
    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
