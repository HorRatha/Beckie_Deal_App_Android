package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("email")
    private String email;
    
    @SerializedName("first_name")
    private String firstName;
    
    @SerializedName("last_name")
    private String lastName;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("password_confirmation")
    private String passwordConfirmation;
    
    @SerializedName("phone")
    private String phone;

    public RegisterRequest(String email, String firstName, String lastName, String name, String password, String passwordConfirmation) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }

    public RegisterRequest(String email, String firstName, String lastName, String name, String password, String passwordConfirmation, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.name = name;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.phone = phone;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
