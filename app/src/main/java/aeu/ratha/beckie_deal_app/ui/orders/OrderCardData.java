package aeu.ratha.beckie_deal_app.ui.orders;

import java.util.List;
import aeu.ratha.beckie_deal_app.data.models.ApiOrder;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

/** Everything one order card needs. Fill it from your own Order model. */
public class OrderCardData {
    public String orderId;                 // whatever OrderDetailActivity needs to load the order
    public String orderNumber;             // "ORD-20261028103702-WYPKF"
    public String status;                  // pending / processing / completed / cancelled
    public String statusLabel;             // "Pending Shipment" (from tracking.status_label), may be null
    public String dateText;                // "Oct 28, 2026"
    public int itemCount;
    public List<String> thumbnailUrls;     // product thumbnails
    public String totalText;               // "$6,441.48"
    public boolean paid;                   // true when the payment_confirmed event is completed
    public boolean payOnDelivery;          // true for COD orders that are not paid yet
    public List<TrackingStep> steps;       // all tracking.events, in order (3 or 4)
    public String provider;                // "Express Delivery"
    public String trackingNumber;          // null until shipped

    // Direct object references for seamless activity navigation
    public ApiOrder apiOrder;
    public OrderManager.Order mockOrder;
}
