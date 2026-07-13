package aeu.ratha.beckie_deal_app;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Locale;

public class MyOrdersActivity extends BaseActivity {

    private LinearLayout ordersList;
    private View emptyState;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        setupTopBar();
        setupTabs();
        ordersList = findViewById(R.id.ordersList);
        emptyState = findViewById(R.id.emptyState);

        // Show active orders by default
        displayOrders(OrderManager.getInstance().getActiveOrders(), "active");

        // Start Shopping button in empty state
        View shopBtn = findViewById(R.id.startShoppingButton);
        if (shopBtn != null) {
            shopBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) backButton.setOnClickListener(v -> finish());
    }

    private void setupTabs() {
        tabLayout = findViewById(R.id.orderTabLayout);
        if (tabLayout == null) return;

        tabLayout.addTab(tabLayout.newTab().setText("Active"));
        tabLayout.addTab(tabLayout.newTab().setText("Completed"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    displayOrders(OrderManager.getInstance().getActiveOrders(), "active");
                } else {
                    displayOrders(OrderManager.getInstance().getCompletedOrders(), "completed");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void displayOrders(List<OrderManager.Order> orders, String type) {
        if (orders.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            ordersList.setVisibility(View.GONE);

            // Customize empty state message based on tab
            TextView emptyTitle = findViewById(R.id.emptyTitle);
            TextView emptySubtitle = findViewById(R.id.emptySubtitle);
            if (type.equals("active")) {
                if (emptyTitle != null) emptyTitle.setText("No active orders");
                if (emptySubtitle != null)
                    emptySubtitle.setText("Your active orders will appear here.");
            } else {
                if (emptyTitle != null) emptyTitle.setText("No completed orders");
                if (emptySubtitle != null)
                    emptySubtitle.setText("Completed and cancelled orders will show here.");
            }
        } else {
            emptyState.setVisibility(View.GONE);
            ordersList.setVisibility(View.VISIBLE);
            ordersList.removeAllViews();

            for (OrderManager.Order order : orders) {
                inflateOrderCard(order);
            }
        }
    }

    private void inflateOrderCard(OrderManager.Order order) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_order, ordersList, false);

        TextView orderId = card.findViewById(R.id.orderIdText);
        TextView statusBadge = card.findViewById(R.id.orderStatusBadge);
        TextView dateText = card.findViewById(R.id.orderDateText);
        TextView itemCountText = card.findViewById(R.id.orderItemCountText);
        LinearLayout thumbnailStrip = card.findViewById(R.id.orderThumbnailStrip);
        TextView totalText = card.findViewById(R.id.orderTotalText);
        View detailsBtn = card.findViewById(R.id.viewDetailsButton);

        // Bind data
        orderId.setText("Order #" + order.orderId);
        dateText.setText(order.date);

        int totalItems = order.getTotalItemCount();
        itemCountText.setText(totalItems + (totalItems == 1 ? " item" : " items"));

        totalText.setText(String.format(Locale.US, "$%.2f", order.totalAmount));

        // Status badge
        statusBadge.setText(order.status.label);
        int statusColor = Color.parseColor(order.status.color);
        statusBadge.setTextColor(statusColor);
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setShape(GradientDrawable.RECTANGLE);
        badgeBg.setCornerRadius(dpToPx(20));
        badgeBg.setColor(setAlpha(statusColor, 0.1f));
        statusBadge.setBackground(badgeBg);

        // Thumbnails — show up to 3 product images
        thumbnailStrip.removeAllViews();
        int maxThumbs = Math.min(order.items.size(), 3);
        for (int i = 0; i < maxThumbs; i++) {
            ImageView thumb = createThumbnail(order.items.get(i).productImageUrl);
            thumbnailStrip.addView(thumb);
        }
        if (order.items.size() > 3) {
            TextView moreText = new TextView(this);
            moreText.setText("+" + (order.items.size() - 3));
            moreText.setTextColor(getColor(R.color.text_muted));
            moreText.setTextSize(12);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart(dpToPx(8));
            moreText.setLayoutParams(lp);
            moreText.setGravity(android.view.Gravity.CENTER_VERTICAL);
            thumbnailStrip.addView(moreText);
        }

        // View details tap
        detailsBtn.setOnClickListener(v -> openOrderDetail(order));
        card.setOnClickListener(v -> openOrderDetail(order));

        ordersList.addView(card);
    }

    private ImageView createThumbnail(String imageUrl) {
        MaterialCardView wrapper = new MaterialCardView(this);
        LinearLayout.LayoutParams wrapperLp = new LinearLayout.LayoutParams(
                dpToPx(48), dpToPx(48));
        wrapperLp.setMarginEnd(dpToPx(8));
        wrapper.setLayoutParams(wrapperLp);
        wrapper.setRadius(dpToPx(10));
        wrapper.setCardElevation(0);
        wrapper.setCardBackgroundColor(getColor(R.color.card_bg_secondary));

        ImageView img = new ImageView(this);
        img.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        img.setPadding(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(img);

        wrapper.addView(img);

        // We can't add MaterialCardView directly to a LinearLayout expecting ImageView,
        // so we use a FrameLayout wrapper approach — but simpler: just return the image
        // Actually, let's just create a simple ImageView with rounded background
        ImageView thumb = new ImageView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpToPx(48), dpToPx(48));
        lp.setMarginEnd(dpToPx(8));
        thumb.setLayoutParams(lp);
        thumb.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        thumb.setPadding(dpToPx(6), dpToPx(6), dpToPx(6), dpToPx(6));

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(dpToPx(10));
        bg.setColor(getColor(R.color.card_bg_secondary));
        thumb.setBackground(bg);
        thumb.setClipToOutline(true);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(thumb);

        return thumb;
    }

    private void openOrderDetail(OrderManager.Order order) {
        // Pass the order index so OrderDetailActivity can retrieve it
        List<OrderManager.Order> allOrders = OrderManager.getInstance().getAllOrders();
        int index = allOrders.indexOf(order);
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("order_index", index);
        startActivity(intent);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private int setAlpha(int color, float alpha) {
        int a = (int) (alpha * 255);
        return Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
    }
}
