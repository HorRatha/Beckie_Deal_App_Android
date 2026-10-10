package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for email OTP verification.
 * Received from /api/v1/auth/verify-email-otp endpoint.
 */
public class VerifyEmailOtpResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private VerifyOtpData data;

    public VerifyEmailOtpResponse() {}

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public VerifyOtpData getData() {
        return data;
    }

    public void setData(VerifyOtpData data) {
        this.data = data;
    }

    /**
     * Inner class for verification data response.
     */
    public static class VerifyOtpData {
        
        @SerializedName("access_token")
        private String accessToken;
        
        @SerializedName("token_type")
        private String tokenType;
        
        @SerializedName("expires_in")
        private int expiresIn;
        
        @SerializedName("user")
        private User user;

        public VerifyOtpData() {}

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public void setExpiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
}
