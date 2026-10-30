package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response from GET /api/v1/reviews (paginated list of user's reviews)
 * Contains the list of reviews created/managed by the current user.
 */
public class ReviewsResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private ReviewsData data;
    
    // Constructors
    public ReviewsResponse() {}
    
    // Getters
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public ReviewsData getData() {
        return data;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(ReviewsData data) {
        this.data = data;
    }
    
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
    
    /**
     * Nested data class containing reviews list
     */
    public static class ReviewsData {
        @SerializedName("items")
        private List<Review> items;
        
        @SerializedName("pagination")
        private ReviewPagination pagination;
        
        // Constructors
        public ReviewsData() {}
        
        // Getters
        public List<Review> getItems() {
            return items;
        }
        
        public ReviewPagination getPagination() {
            return pagination;
        }
        
        // Setters
        public void setItems(List<Review> items) {
            this.items = items;
        }
        
        public void setPagination(ReviewPagination pagination) {
            this.pagination = pagination;
        }
    }
}
