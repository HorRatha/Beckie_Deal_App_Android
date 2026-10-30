package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response from GET /api/v1/products/{product_id}/reviews
 * Contains review summary and paginated list of reviews for a product.
 */
public class ProductReviewsResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private ProductReviewsData data;
    
    // Constructors
    public ProductReviewsResponse() {}
    
    // Getters
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public ProductReviewsData getData() {
        return data;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(ProductReviewsData data) {
        this.data = data;
    }
    
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
    
    /**
     * Nested data class containing review information
     */
    public static class ProductReviewsData {
        @SerializedName("product_id")
        private String productId;
        
        @SerializedName("summary")
        private ReviewSummary summary;
        
        @SerializedName("items")
        private List<Review> items;
        
        @SerializedName("pagination")
        private ReviewPagination pagination;
        
        // Constructors
        public ProductReviewsData() {}
        
        // Getters
        public String getProductId() {
            return productId;
        }
        
        public ReviewSummary getSummary() {
            return summary;
        }
        
        public List<Review> getItems() {
            return items;
        }
        
        public ReviewPagination getPagination() {
            return pagination;
        }
        
        // Setters
        public void setProductId(String productId) {
            this.productId = productId;
        }
        
        public void setSummary(ReviewSummary summary) {
            this.summary = summary;
        }
        
        public void setItems(List<Review> items) {
            this.items = items;
        }
        
        public void setPagination(ReviewPagination pagination) {
            this.pagination = pagination;
        }
    }
}
