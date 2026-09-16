package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Response model for coupon application.
 * Returned by POST /api/v1/coupons/apply
 */
public class CouponResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private CouponData data;
    
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public CouponData getData() {
        return data;
    }
    
    public boolean isSuccess() {
        return "success".equals(status);
    }
    
    public static class CouponData {
        @SerializedName("coupon")
        private String coupon;
        
        @SerializedName("discount_amount")
        private double discountAmount;
        
        @SerializedName("grand_total")
        private double grandTotal;
        
        @SerializedName("cart")
        private Cart cart;
        
        public String getCoupon() {
            return coupon;
        }
        
        public double getDiscountAmount() {
            return discountAmount;
        }
        
        public double getGrandTotal() {
            return grandTotal;
        }
        
        public Cart getCart() {
            return cart;
        }
    }
}
