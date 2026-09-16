package aeu.ratha.beckie_deal_app.ui.activities.cart;

import aeu.ratha.beckie_deal_app.R;


import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.graphics.Color;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.graphics.drawable.GradientDrawable;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.os.Bundle;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.view.Gravity;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.view.LayoutInflater;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.view.View;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.view.ViewGroup;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.widget.ImageView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.widget.LinearLayout;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.ScreenUtils;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class OrderDetailActivity extends BaseActivity {

    private OrderManager.Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Retrieve order
        int orderIndex = getIntent().getIntExtra("order_index", -1);
        List<OrderManager.Order> allOrders = OrderManager.getInstance().getAllOrders();
        if (orderIndex < 0 || orderIndex >= allOrders.size()) {
            finish();
            return;
        }
        order = allOrders.get(orderIndex);

        setupTopBar();
        bindOrderHeader();
        buildTimeline();
        displayItems();
        displaySummary();
        setupEntranceAnimation();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) backButton.setOnClickListener(v -> finish());
        UiAnimations.pressBounce(backButton);

        TextView title = findViewById(R.id.toolbarTitle);
        if (title != null) title.setText("Order #" + order.orderId);
    }

    /** Staggers the order header, tracking timeline and payment summary cards into place. */
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

    private void bindOrderHeader() {
        TextView orderIdTv = findViewById(R.id.detailOrderId);
        TextView statusBadge = findViewById(R.id.detailStatusBadge);
        TextView dateTv = findViewById(R.id.detailOrderDate);

        if (orderIdTv != null) orderIdTv.setText("Order #" + order.orderId);
        if (dateTv != null) dateTv.setText("Placed on " + order.date);

        if (statusBadge != null) {
            statusBadge.setText(order.status.label);
            int statusColor = Color.parseColor(order.status.color);
            statusBadge.setTextColor(statusColor);
            GradientDrawable badgeBg = new GradientDrawable();
            badgeBg.setShape(GradientDrawable.RECTANGLE);
            badgeBg.setCornerRadius(dpToPx(20));
            badgeBg.setColor(setAlpha(statusColor, 0.1f));
            statusBadge.setBackground(badgeBg);
        }
    }

    private void buildTimeline() {
        LinearLayout timeline = findViewById(R.id.trackingTimeline);
        if (timeline == null) return;
        timeline.removeAllViews();

        // Define tracking steps
        String[][] steps = {
                {"Order Placed", "Your order has been confirmed", "#22C55E"},
                {"Processing", "Preparing your items for shipment", null},
                {"Shipped", "Your package is on the way", null},
                {"Delivered", "Package delivered successfully", null}
        };

        int activeStep = getActiveStep();

        for (int i = 0; i < steps.length; i++) {
            boolean isCompleted = i <= activeStep;
            boolean isCurrent = i == activeStep;
            addTimelineStep(timeline, steps[i][0], steps[i][1], isCompleted, isCurrent, i < steps.length - 1);
        }
    }

    private int getActiveStep() {
        switch (order.status) {
            case PROCESSING: return 1;
            case SHIPPED: return 2;
            case DELIVERED: return 3;
            case CANCELLED: return 0;
            default: return 0;
        }
    }

    private void addTimelineStep(LinearLayout parent, String title, String subtitle,
                                  boolean isCompleted, boolean isCurrent, boolean hasLine) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setGravity(Gravity.TOP);

        // Left: dot + line
        LinearLayout dotColumn = new LinearLayout(this);
        dotColumn.setOrientation(LinearLayout.VERTICAL);
        dotColumn.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams dotColLp = new LinearLayout.LayoutParams(dpToPx(32), LinearLayout.LayoutParams.WRAP_CONTENT);
        dotColumn.setLayoutParams(dotColLp);

        // Dot
        View dot = new View(this);
        int dotSize = isCurrent ? dpToPx(14) : dpToPx(10);
        LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dotSize, dotSize);
        dotLp.topMargin = dpToPx(4);
        dot.setLayoutParams(dotLp);
        GradientDrawable dotBg = new GradientDrawable();
        dotBg.setShape(GradientDrawable.OVAL);
        if (isCompleted) {
            dotBg.setColor(Color.parseColor("#22C55E"));
        } else {
            dotBg.setColor(getColor(R.color.border_color));
        }
        dot.setBackground(dotBg);
        dotColumn.addView(dot);

        // Connector line
        if (hasLine) {
            View line = new View(this);
            LinearLayout.LayoutParams lineLp = new LinearLayout.LayoutParams(dpToPx(2), dpToPx(32));
            lineLp.topMargin = dpToPx(4);
            line.setLayoutParams(lineLp);
            line.setBackgroundColor(isCompleted ? Color.parseColor("#22C55E") : getColor(R.color.border_color));
            dotColumn.addView(line);
        }

        row.addView(dotColumn);

        // Right: text
        LinearLayout textColumn = new LinearLayout(this);
        textColumn.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        textLp.setMarginStart(dpToPx(12));
        textColumn.setLayoutParams(textLp);

        TextView titleTv = new TextView(this);
        titleTv.setText(title);
        ScreenUtils.applyTextSize(titleTv, R.dimen.text_size_body);
        titleTv.setTextColor(isCompleted ? getColor(R.color.text_primary) : getColor(R.color.text_muted));
        if (isCurrent) titleTv.setTypeface(null, android.graphics.Typeface.BOLD);
        textColumn.addView(titleTv);

        TextView subtitleTv = new TextView(this);
        subtitleTv.setText(subtitle);
        ScreenUtils.applyTextSize(subtitleTv, R.dimen.text_size_caption);
        subtitleTv.setTextColor(getColor(R.color.text_muted));
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = dpToPx(2);
        subLp.bottomMargin = hasLine ? dpToPx(8) : 0;
        subtitleTv.setLayoutParams(subLp);
        textColumn.addView(subtitleTv);

        row.addView(textColumn);
        parent.addView(row);
    }

    private void displayItems() {
        LinearLayout itemsList = findViewById(R.id.detailItemsList);
        if (itemsList == null) return;
        itemsList.removeAllViews();

        for (OrderManager.OrderItem item : order.items) {
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

    private void displaySummary() {
        TextView subtotalTv = findViewById(R.id.detailSubtotal);
        TextView shippingTv = findViewById(R.id.detailShipping);
        TextView totalTv = findViewById(R.id.detailTotal);

        // Calculate shipping based on total - since Order doesn't store shipping separately,
        // we use the same logic as PayoutActivity
        double total = order.totalAmount;
        
        // Free shipping threshold (matches product detail spec and PayoutActivity)
        final double FREE_SHIPPING_THRESHOLD = 50.00;
        final double STANDARD_SHIPPING = 15.00;
        
        // Reverse calculate: try to determine if shipping was included
        // First assume shipping was applied, calculate subtotal
        double potentialSubtotal = total - STANDARD_SHIPPING;
        double shipping;
        double subtotal;
        
        if (potentialSubtotal >= FREE_SHIPPING_THRESHOLD) {
            // If subtotal >= $50, shipping should have been free
            shipping = 0.0;
            subtotal = total; // No shipping was added
        } else {
            // Shipping was applied
            shipping = STANDARD_SHIPPING;
            subtotal = potentialSubtotal;
        }

        if (subtotalTv != null) {
            subtotalTv.setText(String.format(Locale.US, "$%.2f", subtotal));
        }
        
        if (shippingTv != null) {
            if (shipping == 0.0) {
                shippingTv.setText("FREE");
                shippingTv.setTextColor(getResources().getColor(R.color.green_stock, null));
            } else {
                shippingTv.setText(String.format(Locale.US, "$%.2f", shipping));
            }
        }
        
        if (totalTv != null) {
            totalTv.setText(String.format(Locale.US, "$%.2f", order.totalAmount));
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private int setAlpha(int color, float alpha) {
        int a = (int) (alpha * 255);
        return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
    }
}
