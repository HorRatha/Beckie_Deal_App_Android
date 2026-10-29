package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ApiTracking implements Serializable {
    @SerializedName("status")
    public String status;

    @SerializedName("status_label")
    public String statusLabel;

    @SerializedName("tracking_number")
    public String trackingNumber;

    @SerializedName("shipping_provider")
    public String shippingProvider;

    @SerializedName("events")
    public List<ApiTrackingEvent> events;

    @SerializedName("estimated_delivery")
    public String estimatedDelivery;
}
