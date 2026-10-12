package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for avatar upload API endpoint.
 * Contains the avatar URL and user profile data.
 */
public class AvatarUploadResponse {
    @SerializedName("avatar_url")
    private String avatarUrl;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("user")
    private User user;

    // Getters
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
