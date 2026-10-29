package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ApiOrder implements Serializable {
    @SerializedName("id")
    public long id;

    @SerializedName("order_number")
    public String orderNumber;

    @SerializedName("user_id")
    public long userId;

    @SerializedName("address_id")
    public Object addressId;

    @SerializedName("subtotal")
    public String subtotal;

    @SerializedName("discount_total")
    public String discountTotal;

    @SerializedName("coupon_code")
    public String couponCode;

    @SerializedName("tax_total")
    public String taxTotal;

    @SerializedName("shipping_total")
    public String shippingTotal;

    @SerializedName("grand_total")
    public String grandTotal;

    @SerializedName("currency")
    public String currency;

    @SerializedName("payment_method")
    public String paymentMethod;

    @SerializedName("payment_status")
    public String paymentStatus;

    @SerializedName("transaction_id")
    public String transactionId;

    @SerializedName("status")
    public String status;

    @SerializedName("tracking")
    public ApiTracking tracking;

    @SerializedName("customer_snapshot")
    public ApiCustomerSnapshot customerSnapshot;

    @SerializedName("address_snapshot")
    public ApiAddressSnapshot addressSnapshot;

    @SerializedName("paid_at")
    public String paidAt;

    @SerializedName("shipped_at")
    public String shippedAt;

    @SerializedName("delivered_at")
    public String deliveredAt;

    @SerializedName("cancelled_at")
    public String cancelledAt;

    @SerializedName("refunded_at")
    public String refundedAt;

    @SerializedName("items")
    public List<ApiOrderItem> items;

    public String getDisplayOrderNumber() {
        if (orderNumber != null && !orderNumber.trim().isEmpty()) {
            return orderNumber;
        }
        return "ORD-" + id;
    }

    public double getGrandTotalDouble() {
        if (grandTotal == null) return 0.0;
        try {
            return Double.parseDouble(grandTotal.replace("$", "").replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getSubtotalDouble() {
        if (subtotal == null) return 0.0;
        try {
            return Double.parseDouble(subtotal.replace("$", "").replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getShippingTotalDouble() {
        if (shippingTotal == null) return 0.0;
        try {
            return Double.parseDouble(shippingTotal.replace("$", "").replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public double getDiscountTotalDouble() {
        if (discountTotal == null) return 0.0;
        try {
            return Double.parseDouble(discountTotal.replace("$", "").replace(",", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public int getTotalItemCount() {
        if (items == null || items.isEmpty()) return 0;
        int count = 0;
        for (ApiOrderItem item : items) {
            count += item.quantity > 0 ? item.quantity : 1;
        }
        return count;
    }

    public boolean isActive() {
        String mainStatus = status != null ? status.toLowerCase() : "";
        String trackStatus = tracking != null && tracking.status != null ? tracking.status.toLowerCase() : "";

        if (mainStatus.equals("completed") || mainStatus.equals("delivered") || mainStatus.equals("cancelled") || mainStatus.equals("refunded")) {
            return false;
        }
        if (trackStatus.equals("delivered") || trackStatus.equals("cancelled")) {
            return false;
        }
        return true;
    }

    public String getStatusLabel() {
        if (tracking != null && tracking.statusLabel != null && !tracking.statusLabel.trim().isEmpty()) {
            return tracking.statusLabel;
        }
        if (status == null) return "Pending";
        switch (status.toLowerCase()) {
            case "completed":
            case "delivered":
                return "Completed";
            case "shipped":
                return "Shipped";
            case "processing":
            case "paid":
                return "Processing";
            case "cancelled":
                return "Cancelled";
            default:
                return "Pending";
        }
    }

    public String getFormattedDate() {
        String dateStr = paidAt;
        if (dateStr == null && tracking != null && tracking.events != null && !tracking.events.isEmpty()) {
            dateStr = tracking.events.get(0).timestamp;
        }
        if (dateStr == null) return "Recent";

        try {
            String clean = dateStr.replaceAll("\\.\\d+Z$", "Z").replaceAll("Z$", "+00:00");
            java.text.SimpleDateFormat inFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US);
            java.util.Date d = inFormat.parse(clean);
            if (d != null) {
                return new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US).format(d);
            }
        } catch (Exception ignored) {}

        if (dateStr.length() >= 10) {
            return dateStr.substring(0, 10);
        }
        return dateStr;
    }
}
