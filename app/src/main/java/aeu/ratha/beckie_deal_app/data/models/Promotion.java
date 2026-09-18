package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Promotion/Coupon model from backend.
 * Endpoint: GET /api/v1/promotions
 */
public class Promotion {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;  // e.g. "Summer Sale"
    
    @SerializedName("code")
    private String code;  // e.g. "SUMMER30"
    
    @SerializedName("type")
    private String type;  // "percentage" or "fixed"
    
    @SerializedName("value")
    private double value;    // e.g. 30 (for 30%) or 15 (for $15 off) or 9.99 (for $9.99 off)
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("banner_image")
    private String bannerImage;
    
    @SerializedName("min_order_amount")
    private double minOrderAmount;  // e.g. 150 (min order $150)
    
    @SerializedName("max_discount_amount")
    private Double maxDiscountAmount;
    
    @SerializedName("applicable_products")
    private String applicableProducts;
    
    @SerializedName("applicable_categories")
    private String applicableCategories;
    
    @SerializedName("starts_at")
    private String startsAt;  // ISO 8601 date
    
    @SerializedName("expires_at")
    private String expiresAt;  // ISO 8601 date (nullable for unlimited)
    
    @SerializedName("usage_remaining")
    private Integer usageRemaining;  // e.g. 25 uses left
    
    public Promotion() {}
    
    // ── Getters ────────────────────────────────────────────────────────────
    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getType() { return type; }
    public double getValue() { return value; }
    public String getDescription() { return description; }
    public String getBannerImage() { return bannerImage; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public Double getMaxDiscountAmount() { return maxDiscountAmount; }
    public String getApplicableProducts() { return applicableProducts; }
    public String getApplicableCategories() { return applicableCategories; }
    public String getStartsAt() { return startsAt; }
    public String getExpiresAt() { return expiresAt; }
    public Integer getUsageRemaining() { return usageRemaining; }
    
    // ── Setters (for mock data) ────────────────────────────────────────────
    
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCode(String code) { this.code = code; }
    public void setType(String type) { this.type = type; }
    public void setValue(double value) { this.value = value; }
    public void setDescription(String description) { this.description = description; }
    public void setBannerImage(String bannerImage) { this.bannerImage = bannerImage; }
    public void setMinOrderAmount(double minOrderAmount) { this.minOrderAmount = minOrderAmount; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }
    
    // ── Helpers ────────────────────────────────────────────────────────────
    
    /**
     * Get discount display text (e.g. "30% Off" or "$15 Off")
     */
    public String getDiscountDisplay() {
        if ("percentage".equals(type)) {
            // Format percentage (remove .0 if it's a whole number)
            if (value == (int)value) {
                return (int)value + "% Off";
            } else {
                return value + "% Off";
            }
        } else if ("fixed".equals(type)) {
            // Format fixed amount
            if (value == (int)value) {
                return "$" + (int)value + " Off";
            } else {
                return String.format("$%.2f Off", value);
            }
        }
        return "Special Offer";
    }
    
    /**
     * Check if promotion is active (between start and expiry dates)
     */
    public boolean isActive() {
        // Simple check - could be enhanced with proper date parsing
        return expiresAt == null || expiresAt.isEmpty() || 
               expiresAt.compareTo(System.currentTimeMillis() + "") > 0;
    }
    
    /**
     * Get usage status text (e.g. "25 uses left" or "Unlimited")
     */
    public String getUsageText() {
        if (usageRemaining == null || usageRemaining < 0) {
            return "Unlimited uses";
        }
        if (usageRemaining == 0) {
            return "Expired";
        }
        return usageRemaining + " uses left";
    }
    
    /**
     * Check if promotion has expired
     */
    public boolean hasExpired() {
        return usageRemaining != null && usageRemaining <= 0;
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", minOrderAmount=" + minOrderAmount +
                ", usageRemaining=" + usageRemaining +
                '}';
    }
}
