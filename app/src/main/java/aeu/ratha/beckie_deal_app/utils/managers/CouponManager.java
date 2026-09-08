package aeu.ratha.beckie_deal_app.utils.managers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Manages coupon codes, validation, and discount calculation.
 * Singleton pattern for app-wide coupon state.
 */
public class CouponManager {
    private static CouponManager instance;
    private Coupon appliedCoupon;
    
    // Predefined coupons (in production, these would come from a backend)
    private final Map<String, Coupon> availableCoupons = new HashMap<>();

    public static class Coupon {
        public final String code;
        public final DiscountType type;
        public final double value;
        public final double minPurchase;
        public final String description;

        public Coupon(String code, DiscountType type, double value, double minPurchase, String description) {
            this.code = code;
            this.type = type;
            this.value = value;
            this.minPurchase = minPurchase;
            this.description = description;
        }

        /**
         * Calculate discount amount based on subtotal
         */
        public double calculateDiscount(double subtotal) {
            if (subtotal < minPurchase) {
                return 0.0;
            }
            
            switch (type) {
                case PERCENTAGE:
                    return subtotal * (value / 100.0);
                case FIXED_AMOUNT:
                    return Math.min(value, subtotal); // Can't discount more than subtotal
                default:
                    return 0.0;
            }
        }

        /**
         * Get formatted discount display string
         */
        public String getDiscountLabel() {
            switch (type) {
                case PERCENTAGE:
                    return String.format(Locale.US, "%.0f%% OFF", value);
                case FIXED_AMOUNT:
                    return String.format(Locale.US, "$%.2f OFF", value);
                default:
                    return "";
            }
        }
    }

    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    private CouponManager() {
        initializeCoupons();
    }

    public static synchronized CouponManager getInstance() {
        if (instance == null) {
            instance = new CouponManager();
        }
        return instance;
    }

    /**
     * Initialize sample coupons (in production, fetch from API)
     */
    private void initializeCoupons() {
        availableCoupons.put("WELCOME10", new Coupon(
            "WELCOME10",
            DiscountType.PERCENTAGE,
            10.0,
            0.0,
            "10% off for new users"
        ));
        
        availableCoupons.put("SAVE20", new Coupon(
            "SAVE20",
            DiscountType.FIXED_AMOUNT,
            20.0,
            50.0,
            "$20 off orders over $50"
        ));
        
        availableCoupons.put("DEAL50", new Coupon(
            "DEAL50",
            DiscountType.PERCENTAGE,
            50.0,
            100.0,
            "50% off orders over $100"
        ));
        
        availableCoupons.put("FREESHIP", new Coupon(
            "FREESHIP",
            DiscountType.FIXED_AMOUNT,
            15.0,
            30.0,
            "Free shipping on orders over $30"
        ));
    }

    /**
     * Validate and apply coupon code
     * @return CouponResult with success status and message
     */
    public CouponResult applyCoupon(String code, double subtotal) {
        if (code == null || code.trim().isEmpty()) {
            return new CouponResult(false, "Please enter a coupon code", null);
        }

        String upperCode = code.trim().toUpperCase();
        Coupon coupon = availableCoupons.get(upperCode);

        if (coupon == null) {
            return new CouponResult(false, "Invalid coupon code", null);
        }

        if (subtotal < coupon.minPurchase) {
            String message = String.format(Locale.US, 
                "Minimum purchase of $%.2f required", coupon.minPurchase);
            return new CouponResult(false, message, null);
        }

        appliedCoupon = coupon;
        double discount = coupon.calculateDiscount(subtotal);
        String message = String.format(Locale.US, 
            "Coupon applied! You saved $%.2f", discount);
        return new CouponResult(true, message, coupon);
    }

    /**
     * Remove currently applied coupon
     */
    public void removeCoupon() {
        appliedCoupon = null;
    }

    /**
     * Get currently applied coupon
     */
    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }

    /**
     * Check if a coupon is currently applied
     */
    public boolean hasCoupon() {
        return appliedCoupon != null;
    }

    /**
     * Calculate discount for current applied coupon
     */
    public double calculateDiscount(double subtotal) {
        if (appliedCoupon == null) {
            return 0.0;
        }
        return appliedCoupon.calculateDiscount(subtotal);
    }

    /**
     * Result object for coupon application attempts
     */
    public static class CouponResult {
        public final boolean success;
        public final String message;
        public final Coupon coupon;

        public CouponResult(boolean success, String message, Coupon coupon) {
            this.success = success;
            this.message = message;
            this.coupon = coupon;
        }
    }
}
