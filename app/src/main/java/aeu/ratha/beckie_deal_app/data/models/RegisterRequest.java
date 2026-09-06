package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("email")
    private String email;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("password_confirmation")
    private String passwordConfirmation;
    
    @SerializedName("phone")
    private String phone;

    public RegisterRequest(String email, String name, String password, String passwordConfirmation) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public String getPhone() {
        return phone;
    }

    // Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
