package aeu.ratha.beckie_deal_app.ui.activities.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.ApiAddressSnapshot;
import aeu.ratha.beckie_deal_app.data.models.ApiOrder;
import aeu.ratha.beckie_deal_app.data.models.ApiOrderItem;
import aeu.ratha.beckie_deal_app.data.models.ApiTrackingEvent;
import aeu.ratha.beckie_deal_app.ui.orders.OrderCardBinder;
import aeu.ratha.beckie_deal_app.ui.orders.OrderFormat;
import aeu.ratha.beckie_deal_app.ui.orders.OrderTimelineBinder;
import aeu.ratha.beckie_deal_app.ui.orders.TrackingStep;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.managers.ProductImageCacheManager;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

public class OrderDetailActivity extends BaseActivity {

    private ApiOrder apiOrder;
    private OrderManager.Order mockOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Check if passed via ApiOrder or OrderManager index
        if (getIntent().hasExtra("order_data")) {
            apiOrder = (ApiOrder) getIntent().getSerializableExtra("order_data");
        } else {
            int orderIndex = getIntent().getIntExtra("order_index", -1);
            List<OrderManager.Order> allOrders = OrderManager.getInstance().getAllOrders();
            if (orderIndex >= 0 && orderIndex < allOrders.size()) {
                mockOrder = allOrders.get(orderIndex);
            }
        }

        if (apiOrder == null && mockOrder == null) {
            finish();
            return;
        }

        setupTopBar();

        if (apiOrder != null) {
            bindApiOrderHeader(apiOrder);
            bindApiAddressSnapshot(apiOrder.addressSnapshot);
            buildTimeline();
            displayApiItems(apiOrder.items);
            displayApiSummary(apiOrder);

            // Asynchronously resolve real product thumbnails from /products/{id} endpoint
            ProductImageCacheManager.resolveOrderProductImages(
                    this,
                    java.util.Collections.singletonList(apiOrder),
                    (productId, imageUrl) -> runOnUiThread(() -> displayApiItems(apiOrder.items))
            );
        } else {
            bindMockOrderHeader();
            findViewById(R.id.shippingAddressHeader).setVisibility(View.GONE);
            findViewById(R.id.shippingAddressCard).setVisibility(View.GONE);
            buildTimeline();
            displayMockItems();
            displayMockSummary();
        }

        handleScrollToTracking();
        setupEntranceAnimation();

        BottomNavHelper.setup(this, BottomNavHelper.NavItem.ACCOUNT);
        BottomNavHelper.updateCartBadge(this);
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
            UiAnimations.pressBounce(backButton);
        }

        TextView title = findViewById(R.id.toolbarTitle);
        if (title != null) {
            title.setText("Order Details");
        }
    }

    private void setupEntranceAnimation() {
        UiAnimations.fadeSlideDown(findViewById(R.id.appBarLayout), 0);
        NestedScrollView scroll = findViewById(R.id.orderDetailScroll);
        if (scroll != null && scroll.getChildCount() > 0) {
            View listRoot = scroll.getChildAt(0);
            if (listRoot instanceof ViewGroup) {
                UiAnimations.staggerChildrenIn((ViewGroup) listRoot, 80L);
            }
        }
    }

    private void handleScrollToTracking() {
        if (getIntent().getBooleanExtra("scroll_to_tracking", false)) {
            NestedScrollView sv = findViewById(R.id.orderDetailScroll);
            View card = findViewById(R.id.trackingCard);
            if (sv != null && card != null) {
                sv.post(() -> sv.smoothScrollTo(0, Math.max(0, card.getTop() - 24)));
            }
        }
    }

    private void bindApiOrderHeader(ApiOrder order) {
        TextView orderIdTv = findViewById(R.id.detailOrderId);
        TextView orderNumberFullTv = findViewById(R.id.detailOrderNumberFull);
        TextView statusBadge = findViewById(R.id.detailStatusBadge);
        TextView dateTv = findViewById(R.id.detailOrderDate);
        TextView paidChip = findViewById(R.id.detailPaidChip);

        String fullNum = order.getDisplayOrderNumber();
        if (orderIdTv != null) orderIdTv.setText("Order #" + shortRef(fullNum));
        if (orderNumberFullTv != null) orderNumberFullTv.setText(fullNum);
        if (dateTv != null) dateTv.setText("Placed on " + order.getFormattedDate());

        if (statusBadge != null) {
            OrderCardBinder.applyStatusChip(statusBadge, order.status, order.getStatusLabel());
        }

        if (paidChip != null) {
            boolean isPaid = order.paidAt != null || "paid".equalsIgnoreCase(order.paymentStatus) || "completed".equalsIgnoreCase(order.status);
            boolean isCod = "cod".equalsIgnoreCase(order.paymentMethod);
            if (isPaid) {
                paidChip.setText("✓ Paid");
                paidChip.setTextColor(ContextCompat.getColor(this, R.color.chip_green_fg));
                paidChip.setBackgroundResource(R.drawable.bg_chip_paid);
                paidChip.setVisibility(View.VISIBLE);
            } else if (isCod) {
                paidChip.setText("Pay on delivery");
                paidChip.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
                paidChip.setBackgroundResource(R.drawable.form_field_background);
                paidChip.setVisibility(View.VISIBLE);
            } else {
                paidChip.setVisibility(View.GONE);
            }
        }
    }

    private String shortRef(String orderNumber) {
        if (orderNumber == null) return "";
        int i = orderNumber.lastIndexOf('-');
        return i >= 0 ? orderNumber.substring(i + 1) : orderNumber;
    }

    private void bindApiAddressSnapshot(ApiAddressSnapshot address) {
        View header = findViewById(R.id.shippingAddressHeader);
        View card = findViewById(R.id.shippingAddressCard);

        if (address == null) {
            if (header != null) header.setVisibility(View.GONE);
            if (card != null) card.setVisibility(View.GONE);
            return;
        }

        if (header != null) header.setVisibility(View.VISIBLE);
        if (card != null) card.setVisibility(View.VISIBLE);

        TextView nameTv = findViewById(R.id.detailRecipientName);
        TextView phoneTv = findViewById(R.id.detailRecipientPhone);
        TextView addressTv = findViewById(R.id.detailRecipientAddress);

        if (nameTv != null) nameTv.setText(address.getRecipientName());
        if (phoneTv != null) {
            if (address.phone != null && !address.phone.trim().isEmpty()) {
                phoneTv.setText(address.phone);
                phoneTv.setVisibility(View.VISIBLE);
            } else {
                phoneTv.setVisibility(View.GONE);
            }
        }
        if (addressTv != null) addressTv.setText(address.getFullFormattedAddress());
    }

    private void buildTimeline() {
        LinearLayout timeline = findViewById(R.id.trackingTimeline);
        if (timeline == null) return;

        List<TrackingStep> steps = new ArrayList<>();
        boolean isCancelled = false;
        String provider = null;
        String trackingNumber = null;

        if (apiOrder != null) {
            isCancelled = "cancelled".equalsIgnoreCase(apiOrder.status) || "canceled".equalsIgnoreCase(apiOrder.status);
            if (apiOrder.tracking != null) {
                provider = apiOrder.tracking.shippingProvider;
                trackingNumber = apiOrder.tracking.trackingNumber;
                if (apiOrder.tracking.events != null) {
                    for (ApiTrackingEvent e : apiOrder.tracking.events) {
                        steps.add(new TrackingStep(
                                e.status,
                                e.label,
                                e.completed,
                                OrderFormat.dateTime(e.timestamp)
                        ));
                    }
                }
            }
        } else if (mockOrder != null) {
            isCancelled = mockOrder.status == OrderManager.OrderStatus.CANCELLED;
            provider = "Express Delivery";
            boolean isDelivered = mockOrder.status == OrderManager.OrderStatus.DELIVERED;
            boolean isShipped = mockOrder.status == OrderManager.OrderStatus.SHIPPED || isDelivered;

            steps.add(new TrackingStep("order_created", "Order Confirmed", true, mockOrder.date));
            steps.add(new TrackingStep("payment_confirmed", "Payment Confirmed", true, mockOrder.date));
            steps.add(new TrackingStep("shipped", "Order Shipped", isShipped, null));
            steps.add(new TrackingStep("delivered", "Order Delivered", isDelivered, null));
        }

        OrderTimelineBinder.fill(this, timeline, steps, isCancelled, provider, trackingNumber);
    }

    private void displayApiItems(List<ApiOrderItem> items) {
        LinearLayout itemsList = findViewById(R.id.detailItemsList);
        if (itemsList == null) return;
        itemsList.removeAllViews();

        if (items == null || items.isEmpty()) return;

        for (ApiOrderItem item : items) {
            View v = LayoutInflater.from(this).inflate(R.layout.item_order_detail_product, itemsList, false);

            ImageView image = v.findViewById(R.id.orderItemImage);
            TextView name = v.findViewById(R.id.orderItemName);
            TextView quantity = v.findViewById(R.id.orderItemQuantity);
            TextView price = v.findViewById(R.id.orderItemPrice);

            name.setText(item.productName != null ? item.productName : "Product Item");

            String qtyStr = "Qty: " + (item.quantity > 0 ? item.quantity : 1);
            if (item.variantName != null && !item.variantName.trim().isEmpty()) {
                qtyStr += " · " + item.variantName;
            }
            quantity.setText(qtyStr);

            price.setText(String.format(Locale.US, "$%.2f", item.getTotalPriceDouble()));

            Glide.with(this)
                    .load(item.getDisplayImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .into(image);

            itemsList.addView(v);
        }
    }

    private void displayApiSummary(ApiOrder order) {
        TextView subtotalTv = findViewById(R.id.detailSubtotal);
        TextView shippingTv = findViewById(R.id.detailShipping);
        TextView totalTv = findViewById(R.id.detailTotal);

        if (subtotalTv != null) {
            subtotalTv.setText(String.format(Locale.US, "$%.2f", order.getSubtotalDouble()));
        }

        if (shippingTv != null) {
            double shipTotal = order.getShippingTotalDouble();
            if (shipTotal == 0.0) {
                shippingTv.setText("FREE");
                shippingTv.setTextColor(ContextCompat.getColor(this, R.color.green_stock));
            } else {
                shippingTv.setText(String.format(Locale.US, "$%.2f", shipTotal));
                shippingTv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            }
        }

        if (totalTv != null) {
            totalTv.setText(String.format(Locale.US, "$%.2f", order.getGrandTotalDouble()));
        }
    }

    private void bindMockOrderHeader() {
        TextView orderIdTv = findViewById(R.id.detailOrderId);
        TextView orderNumberFullTv = findViewById(R.id.detailOrderNumberFull);
        TextView statusBadge = findViewById(R.id.detailStatusBadge);
        TextView dateTv = findViewById(R.id.detailOrderDate);

        if (orderIdTv != null) orderIdTv.setText("Order #" + mockOrder.orderId);
        if (orderNumberFullTv != null) orderNumberFullTv.setText("ORD-" + mockOrder.orderId);
        if (dateTv != null) dateTv.setText("Placed on " + mockOrder.date);

        if (statusBadge != null) {
            OrderCardBinder.applyStatusChip(statusBadge, mockOrder.status.name(), mockOrder.status.label);
        }
    }

    private void displayMockItems() {
        LinearLayout itemsList = findViewById(R.id.detailItemsList);
        if (itemsList == null) return;
        itemsList.removeAllViews();

        for (OrderManager.OrderItem item : mockOrder.items) {
            View v = LayoutInflater.from(this).inflate(R.layout.item_order_detail_product, itemsList, false);

            ImageView image = v.findViewById(R.id.orderItemImage);
            TextView name = v.findViewById(R.id.orderItemName);
            TextView quantity = v.findViewById(R.id.orderItemQuantity);
            TextView price = v.findViewById(R.id.orderItemPrice);

            name.setText(item.productName);
            quantity.setText("Qty: " + item.quantity);

            try {
                String priceStr = item.productPrice.replace("$", "").replace(",", "");
                double total = Double.parseDouble(priceStr) * item.quantity;
                price.setText(String.format(Locale.US, "$%.2f", total));
            } catch (Exception e) {
                price.setText(item.productPrice != null ? item.productPrice : "");
            }

            Glide.with(this)
                    .load(item.productImageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(image);

            itemsList.addView(v);
        }
    }

    private void displayMockSummary() {
        TextView subtotalTv = findViewById(R.id.detailSubtotal);
        TextView shippingTv = findViewById(R.id.detailShipping);
        TextView totalTv = findViewById(R.id.detailTotal);

        double total = mockOrder.totalAmount;
        final double FREE_SHIPPING_THRESHOLD = 50.00;
        final double STANDARD_SHIPPING = 15.00;

        double potentialSubtotal = total - STANDARD_SHIPPING;
        double shipping;
        double subtotal;

        if (potentialSubtotal >= FREE_SHIPPING_THRESHOLD) {
            shipping = 0.0;
            subtotal = total;
        } else {
            shipping = STANDARD_SHIPPING;
            subtotal = potentialSubtotal;
        }

        if (subtotalTv != null) subtotalTv.setText(String.format(Locale.US, "$%.2f", subtotal));

        if (shippingTv != null) {
            if (shipping == 0.0) {
                shippingTv.setText("FREE");
                shippingTv.setTextColor(ContextCompat.getColor(this, R.color.green_stock));
            } else {
                shippingTv.setText(String.format(Locale.US, "$%.2f", shipping));
                shippingTv.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
            }
        }

        if (totalTv != null) totalTv.setText(String.format(Locale.US, "$%.2f", mockOrder.totalAmount));
    }
}
