package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Customer information nested within a Review.
 * Contains minimal customer data shown in review display.
 */
public class ReviewCustomer {
    
    @SerializedName("name")
    private String name;
    
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
    
    public String getAvatar() {
        return avatar;
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
