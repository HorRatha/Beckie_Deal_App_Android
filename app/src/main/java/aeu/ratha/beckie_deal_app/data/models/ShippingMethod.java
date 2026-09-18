package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Shipping method model.
 * Maps to the shipping method data returned by GET /api/v1/master/shipping-methods.
 *
 * Example record:
 * {
 *   "id": 1,
 *   "name": "Standard Delivery",
 *   "code": "standard",
 *   "description": "Delivered in 3–5 business days",
 *   "base_cost": "5.00",
 *   "is_active": true,
 *   "estimated_days": 5,
 *   "sort_order": 1
 * }
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

    @SerializedName("base_cost")
    private String baseCost;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("estimated_days")
    private int estimatedDays;

    @SerializedName("sort_order")
    private int sortOrder;

    // ── Default constructor ───────────────────────────────────────────────────
    public ShippingMethod() {}

    // ── Getters ───────────────────────────────────────────────────────────────
    public int getId() { return id; }

    public String getName() { return name; }

    public String getCode() { return code; }

    public String getDescription() { return description; }

    public String getBaseCost() { return baseCost; }

    public boolean isActive() { return isActive; }

    public int getEstimatedDays() { return estimatedDays; }

    public int getSortOrder() { return sortOrder; }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Returns base_cost as a double. Returns 0.0 if the field is null or unparseable.
     */
    public double getBaseCostAsDouble() {
        if (baseCost == null || baseCost.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(baseCost);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Human-readable label shown in the UI, e.g.
     * "Standard Delivery — $5.00 (3-5 days)"
     */
    public String getDisplayLabel() {
        StringBuilder sb = new StringBuilder();
        sb.append(name != null ? name : "Shipping");
        double cost = getBaseCostAsDouble();
        if (cost == 0.0) {
            sb.append(" — FREE");
        } else {
            sb.append(String.format(java.util.Locale.US, " — $%.2f", cost));
        }
        if (estimatedDays > 0) {
            sb.append(" (").append(estimatedDays).append(" day").append(estimatedDays != 1 ? "s" : "").append(")");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "ShippingMethod{id=" + id + ", name='" + name + "', cost=" + baseCost + "}";
    }
}
