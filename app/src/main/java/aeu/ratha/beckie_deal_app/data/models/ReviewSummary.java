package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Summary statistics for product reviews.
 * Contains aggregated rating data across all reviews for a product.
 */
public class ReviewSummary {
    
    @SerializedName("average_rating")
    private double averageRating;
    
    @SerializedName("total_reviews")
    private int totalReviews;
    
    @SerializedName("rating_breakdown")
    private List<ReviewRating> ratingBreakdown;
    
    // Constructors
    public ReviewSummary() {}
    
    public ReviewSummary(double averageRating, int totalReviews, List<ReviewRating> ratingBreakdown) {
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
        this.ratingBreakdown = ratingBreakdown;
    }
    
    // Getters
    public double getAverageRating() {
        return averageRating;
    }
    
    public int getTotalReviews() {
        return totalReviews;
    }
    
    public List<ReviewRating> getRatingBreakdown() {
        return ratingBreakdown;
    }
    
    // Setters
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    
    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public void setRatingBreakdown(List<ReviewRating> ratingBreakdown) {
        this.ratingBreakdown = ratingBreakdown;
    }
}
