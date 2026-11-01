package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Shipping method model.
 * Maps to shipping method data from GET /api/v1/master/shipping-methods.
 */
public class ShippingMethod {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("description")
    private String description;

    @SerializedName("base_price")
    private String basePrice;

    @SerializedName("base_cost")
    private String baseCost;

    @SerializedName("price_per_kg")
    private String pricePerKg;

    @SerializedName("type")
    private String type;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("estimated_days")
    private int estimatedDays;

    @SerializedName("estimated_delivery")
    private String estimatedDelivery;

    @SerializedName("sort_order")
    private int sortOrder;

    public ShippingMethod() {}

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    
    public String getBasePrice() { return basePrice != null ? basePrice : baseCost; }
    public String getBaseCost() { return getBasePrice(); }
    
    public String getPricePerKg() { return pricePerKg; }
    public String getType() { return type; }
    public boolean isActive() { return isActive; }
    public int getEstimatedDays() { return estimatedDays; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public int getSortOrder() { return sortOrder; }

    public double getBaseCostAsDouble() {
        String costStr = getBasePrice();
        if (costStr == null || costStr.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(costStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public String getDisplayLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(name != null ? name : "Shipping");
        double cost = getBaseCostAsDouble();
        if (cost == 0.0) {
            sb.append(" — FREE");
        } else {
            sb.append(String.format(java.util.Locale.US, " — $%.2f", cost));
        }
        if (estimatedDelivery != null && !estimatedDelivery.isEmpty()) {
            sb.append(" (").append(estimatedDelivery).append(")");
        } else if (estimatedDays > 0) {
            sb.append(" (").append(estimatedDays).append(" day").append(estimatedDays != 1 ? "s" : "").append(")");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ShippingMethod{id=" + id + ", name='" + name + "', base_price='" + getBasePrice() + "', type='" + type + "'}";
    }
}
