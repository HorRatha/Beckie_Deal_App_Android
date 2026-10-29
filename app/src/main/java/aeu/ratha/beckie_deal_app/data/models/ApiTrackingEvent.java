package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ApiTrackingEvent implements Serializable {
    @SerializedName("status")
    public String status;

    @SerializedName("label")
    public String label;

    @SerializedName("timestamp")
    public String timestamp;

    @SerializedName("completed")
    public boolean completed;

    public String getFormattedTimestamp() {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            return "Pending";
        }
        try {
            String clean = timestamp.replaceAll("\\.\\d+Z$", "Z").replaceAll("Z$", "+00:00");
            java.text.SimpleDateFormat inFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US);
            java.util.Date d = inFormat.parse(clean);
            if (d != null) {
                return new java.text.SimpleDateFormat("MMM dd, yyyy · hh:mm a", java.util.Locale.US).format(d);
            }
        } catch (Exception ignored) {}

        if (timestamp.length() >= 16) {
            return timestamp.substring(0, 16).replace("T", " ");
        }
        return timestamp;
    }
}
