package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

public class ChangePasswordRequest {
    
    @SerializedName("current_password")
    private String currentPassword;
    
    @SerializedName("new_password")
    private String newPassword;
    
    @SerializedName("new_password_confirmation")
    private String newPasswordConfirmation;
    
    public ChangePasswordRequest() {}
    
    public ChangePasswordRequest(String currentPassword, String newPassword, String newPasswordConfirmation) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
    
    // Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }
    
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
    
    public String getNewPassword() {
        return newPassword;
    }
    
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }
    
    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
