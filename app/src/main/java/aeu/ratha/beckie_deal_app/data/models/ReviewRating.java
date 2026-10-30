package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Rating breakdown information for product reviews.
 * Part of the review summary showing count and percentage of each star rating.
 */
public class ReviewRating {
    
    @SerializedName("rating")
    private int rating;  // 1-5 stars
    
    @SerializedName("count")
    private int count;  // Number of reviews with this rating
    
    @SerializedName("percentage")
    private int percentage;  // Percentage of total reviews (0-100)
    
    // Constructors
    public ReviewRating() {}
    
    public ReviewRating(int rating, int count, int percentage) {
        this.rating = rating;
        this.count = count;
        this.percentage = percentage;
    }
    
    // Getters
    public int getRating() {
        return rating;
    }
    
    public int getCount() {
        return count;
    }
    
    public int getPercentage() {
        return percentage;
    }
    
    // Setters
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
