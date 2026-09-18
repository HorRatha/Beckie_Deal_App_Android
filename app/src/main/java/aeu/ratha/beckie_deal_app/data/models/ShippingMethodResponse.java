package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response wrapper for GET /api/v1/master/shipping-methods.
 *
 * Shape returned by the backend:
 * {
 *   "status": "success",
 *   "message": "Master shipping methods retrieved successfully.",
 *   "data": {
 *     "data": [ { ...ShippingMethod... } ],
 *     "pagination": { "total": N, "limit": 20, "offset": 0, "count": N }
 *   }
 * }
 */
public class ShippingMethodResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ShippingMethodData data;

    // ── Getters ───────────────────────────────────────────────────────────────
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public ShippingMethodData getData() { return data; }

    public boolean isSuccess() {
        return "success".equals(status);
    }

    // ── Nested data wrapper ───────────────────────────────────────────────────
    public static class ShippingMethodData {

        @SerializedName("data")
        private List<ShippingMethod> methods;

        @SerializedName("pagination")
        private Pagination pagination;

        public List<ShippingMethod> getMethods() { return methods; }
        public Pagination getPagination() { return pagination; }
    }
}
