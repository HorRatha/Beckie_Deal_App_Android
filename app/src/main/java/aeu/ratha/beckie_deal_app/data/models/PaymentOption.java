package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Payment options response from the backend
 * Contains available payment methods for checkout
 */
public class PaymentOption {
    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public PaymentData data;

    public static class PaymentData {
        @SerializedName("platform")
        public String platform;

        @SerializedName("checkout")
        public CheckoutConfig checkout;

        @SerializedName("currency")
        public List<String> currency;

        @SerializedName("methods")
        public List<PaymentMethod> methods;
    }

    public static class CheckoutConfig {
        @SerializedName("view_type")
        public String viewType;

        @SerializedName("requires_return_deeplink")
        public boolean requiresReturnDeeplink;
    }

    public static class PaymentMethod {
        @SerializedName("key")
        public String key;

        @SerializedName("enabled")
        public boolean enabled;

        @SerializedName("options")
        public List<String> options;

        // Helper method to get display name
        public String getDisplayName() {
            switch (key) {
                case "aba_payway":
                    return "ABA PayWay";
                case "cod":
                    return "Cash on Delivery";
                case "khqr":
                    return "KHQR";
                default:
                    return key;
            }
        }

        // Helper method to get description
        public String getDescription() {
            switch (key) {
                case "aba_payway":
                    return "Pay with ABA bank cards or KHQR";
                case "cod":
                    return "Pay when you receive your order";
                case "khqr":
                    return "QR code payment";
                default:
                    return "Payment method";
            }
        }
    }
}
