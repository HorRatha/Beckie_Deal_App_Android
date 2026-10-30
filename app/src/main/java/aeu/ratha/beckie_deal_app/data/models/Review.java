package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Review model representing a single review for a product.
 * Used in GET /api/v1/reviews endpoints and product reviews endpoints.
 */
public class Review {
    
    @SerializedName("id")
    private long id;
    
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
    
    @SerializedName("images")
    private String[] images;  // Array of image URLs
    
    @SerializedName("is_verified_purchase")
    private boolean isVerifiedPurchase;
    
    @SerializedName("is_featured")
    private boolean isFeatured;
    
    @SerializedName("helpful_count")
    private int helpfulCount;
    
    @SerializedName("customer")
    private ReviewCustomer customer;

    @SerializedName("name")
    private String name;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("username")
    private String username;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;
    
    // Constructors
    public Review() {}
    
    public Review(int rating, String title, String comment, String productId, long orderId) {
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.productId = productId;
        this.orderId = orderId;
    }
    
    // Getters
    public long getId() {
        return id;
    }
    
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
    
    public String[] getImages() {
        return images;
    }
    
    public boolean isVerifiedPurchase() {
        return isVerifiedPurchase;
    }
    
    public boolean isFeatured() {
        return isFeatured;
    }
    
    public int getHelpfulCount() {
        return helpfulCount;
    }
    
    public ReviewCustomer getCustomer() {
        return customer;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        if (customer != null && customer.getFullName() != null && !customer.getFullName().trim().isEmpty()) {
            return customer.getFullName().trim();
        }
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }
        if (firstName != null && lastName != null) {
            return (firstName + " " + lastName).trim();
        }
        if (firstName != null && !firstName.trim().isEmpty()) return firstName.trim();
        if (lastName != null && !lastName.trim().isEmpty()) return lastName.trim();
        if (userName != null && !userName.trim().isEmpty()) return userName.trim();
        if (username != null && !username.trim().isEmpty()) return username.trim();
        return null;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setId(long id) {
        this.id = id;
    }
    
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
    
    public void setImages(String[] images) {
        this.images = images;
    }
    
    public void setVerifiedPurchase(boolean verifiedPurchase) {
        isVerifiedPurchase = verifiedPurchase;
    }
    
    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }
    
    public void setHelpfulCount(int helpfulCount) {
        this.helpfulCount = helpfulCount;
    }
    
    public void setCustomer(ReviewCustomer customer) {
        this.customer = customer;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Check if this review is authored by the current user (for edit/delete operations).
     * This would need to be set during deserialization based on auth context.
     */
    public boolean isOwnReview() {
        // This would be determined by comparing customer ID with current user ID
        // Implementation depends on auth context availability
        return false;
    }
}
