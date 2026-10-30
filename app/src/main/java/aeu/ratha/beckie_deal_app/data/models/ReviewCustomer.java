package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Customer information nested within a Review.
 * Contains minimal customer data shown in review display.
 */
public class ReviewCustomer {
    
    @SerializedName("name")
    private String name;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;
    
    @SerializedName("avatar")
    private String avatar;  // URL to customer's avatar
    
    // Constructors
    public ReviewCustomer() {}
    
    public ReviewCustomer(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }
    
    // Getters
    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }
        if (firstName != null && lastName != null) {
            return (firstName + " " + lastName).trim();
        }
        if (firstName != null) {
            return firstName.trim();
        }
        if (lastName != null) {
            return lastName.trim();
        }
        return null;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
