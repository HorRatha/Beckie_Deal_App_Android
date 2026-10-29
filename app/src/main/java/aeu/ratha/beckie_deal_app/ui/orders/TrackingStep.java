package aeu.ratha.beckie_deal_app.ui.orders;

/** One event from tracking.events. */
public class TrackingStep {
    public final String key;        // order_created, payment_confirmed, shipped, delivered
    public final String label;      // "Order Confirmed"
    public final boolean completed;
    public final String timeText;   // "Oct 28, 10:37" or null

    public TrackingStep(String key, String label, boolean completed, String timeText) {
        this.key = key;
        this.label = label;
        this.completed = completed;
        this.timeText = timeText;
    }
}
