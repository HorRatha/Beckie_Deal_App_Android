package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for creating or updating a review.
 * Used in POST /api/v1/reviews (create) and PATCH /api/v1/reviews/{id} (update)
 */
public class ReviewCreateRequest {
    
    @SerializedName("product_id")
    private String productId;
    
    @SerializedName("order_id")
    private long orderId;
    
    @SerializedName("rating")
    private int rating;  // 1-5 stars
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("comment")
    private String comment;
    
    // Constructors
    public ReviewCreateRequest() {}
    
    public ReviewCreateRequest(String productId, long orderId, int rating, 
                               String title, String comment) {
        this.productId = productId;
        this.orderId = orderId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
    }
    
    // Getters
    public String getProductId() {
        return productId;
    }
    
    public long getOrderId() {
        return orderId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getComment() {
        return comment;
    }
    
    // Setters
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }
    
    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating));  // Clamp between 1-5
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    /**
     * Validate that all required fields are present and valid.
     * @return true if the review request is valid
     */
    public boolean isValid() {
        return productId != null && !productId.isEmpty()
            && orderId > 0
            && rating >= 1 && rating <= 5
            && title != null && !title.trim().isEmpty()
            && comment != null && !comment.trim().isEmpty();
    }
}
