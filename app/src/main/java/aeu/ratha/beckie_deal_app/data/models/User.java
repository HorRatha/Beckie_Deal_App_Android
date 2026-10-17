package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("email")
    private String email;
    
    @SerializedName("first_name")
    private String firstName;
    
    @SerializedName("last_name")
    private String lastName;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("avatar")
    private String avatar;
    
    @SerializedName("avatar_url")
    private String avatarUrl;

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

    public String getPhone() {
        return phone;
    }
    
    public String getName() {
        return name;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public String getAvatarUrl() {
        return avatarUrl != null ? avatarUrl : avatar;
    }

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        if (name != null) {
            return name;
        }
        return email != null ? email : "User";
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

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
