package aeu.ratha.beckie_deal_app.ui.activities.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.ApiOrder;
import aeu.ratha.beckie_deal_app.data.models.ApiOrderItem;
import aeu.ratha.beckie_deal_app.data.models.ApiTracking;
import aeu.ratha.beckie_deal_app.data.models.ApiTrackingEvent;
import aeu.ratha.beckie_deal_app.data.repository.OrderRepository;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;
import aeu.ratha.beckie_deal_app.ui.orders.OrderCardBinder;
import aeu.ratha.beckie_deal_app.ui.orders.OrderCardData;
import aeu.ratha.beckie_deal_app.ui.orders.OrderFormat;
import aeu.ratha.beckie_deal_app.ui.orders.TrackingStep;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.AppDrawerHelper;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.managers.OrderManager;

public class MyOrdersActivity extends BaseActivity {

    private LinearLayout ordersList;
    private View emptyState;
    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout skeletonLoadingContainer;
    private AppDrawerHelper drawerHelper;

    private OrderRepository orderRepository;
    private List<ApiOrder> apiOrders = new ArrayList<>();

    private final OrderCardBinder.Listener cardListener = new OrderCardBinder.Listener() {
        @Override
        public void onViewDetails(OrderCardData d) {
            Intent intent = new Intent(MyOrdersActivity.this, OrderDetailActivity.class);
            if (d.apiOrder != null) {
                intent.putExtra("order_data", d.apiOrder);
            } else if (d.mockOrder != null) {
                List<OrderManager.Order> allOrders = OrderManager.getInstance().getAllOrders();
                int index = allOrders.indexOf(d.mockOrder);
                intent.putExtra("order_index", index);
            } else {
                intent.putExtra("order_id", d.orderId);
            }
            startActivity(intent);
        }

        @Override
        public void onTrack(OrderCardData d) {
            Intent intent = new Intent(MyOrdersActivity.this, OrderDetailActivity.class);
            if (d.apiOrder != null) {
                intent.putExtra("order_data", d.apiOrder);
            } else if (d.mockOrder != null) {
                List<OrderManager.Order> allOrders = OrderManager.getInstance().getAllOrders();
                int index = allOrders.indexOf(d.mockOrder);
                intent.putExtra("order_index", index);
            } else {
                intent.putExtra("order_id", d.orderId);
            }
            intent.putExtra("scroll_to_tracking", true);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        orderRepository = new OrderRepository(this);

        drawerHelper = AppDrawerHelper.attach(this, R.id.drawerLayout, R.id.menuIcon);
        drawerHelper.refreshDrawerWithLatestUserData(this);

        setupTopBar();
        setupTabs();

        ordersList = findViewById(R.id.ordersList);
        emptyState = findViewById(R.id.emptyState);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        skeletonLoadingContainer = findViewById(R.id.skeletonLoadingContainer);

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setColorSchemeResources(R.color.brand_orange);
            swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
        }

        // Start Shopping button in empty state
        View shopBtn = findViewById(R.id.startShoppingButton);
        if (shopBtn != null) {
            shopBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
            UiAnimations.pressBounce(shopBtn);
        }

        UiAnimations.fadeSlideDown(findViewById(R.id.appBarLayout), 0);

        BottomNavHelper.setup(this, BottomNavHelper.NavItem.ACCOUNT);
        BottomNavHelper.updateCartBadge(this);

        // Fetch orders from API (all pages)
        loadOrders();
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
            UiAnimations.pressBounce(backButton);
        }
    }

    private void setupTabs() {
        tabLayout = findViewById(R.id.orderTabLayout);
        if (tabLayout == null) return;

        String[] labels = {"All", "Pending", "Processing", "Completed", "Cancelled"};
        String[] keys   = {"all", "pending", "processing", "completed", "cancelled"};

        for (int i = 0; i < labels.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(labels[i]).setTag(keys[i]));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String filterKey = tab.getTag() != null ? (String) tab.getTag() : "all";
                renderCurrentTabOrders(filterKey);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadOrders() {
        if (apiOrders.isEmpty()) {
            showSkeletonLoading();
        }

        orderRepository.fetchAllOrders(new OrderRepository.OrdersCallback() {
            @Override
            public void onSuccess(List<ApiOrder> orders) {
                hideSkeletonLoading();
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

                apiOrders = orders != null ? orders : new ArrayList<>();
                String filterKey = getCurrentFilterKey();
                renderCurrentTabOrders(filterKey);

                // Asynchronously resolve real product thumbnails from /products/{id} endpoint
                aeu.ratha.beckie_deal_app.utils.managers.ProductImageCacheManager.resolveOrderProductImages(
                        MyOrdersActivity.this,
                        apiOrders,
                        (productId, imageUrl) -> runOnUiThread(() -> renderCurrentTabOrders(getCurrentFilterKey()))
                );
            }

            @Override
            public void onError(String errorMessage) {
                hideSkeletonLoading();
                if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

                // If API fails or is offline, fallback to local/mock orders if available
                if (apiOrders.isEmpty()) {
                    List<OrderManager.Order> mockOrders = OrderManager.getInstance().getAllOrders();
                    if (!mockOrders.isEmpty()) {
                        displayMockOrders(mockOrders);
                        return;
                    }
                }

                if (!apiOrders.isEmpty()) {
                    Toast.makeText(MyOrdersActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    renderCurrentTabOrders(getCurrentFilterKey());
                }
            }
        });
    }

    private String getCurrentFilterKey() {
        if (tabLayout == null) return "all";
        int pos = tabLayout.getSelectedTabPosition();
        TabLayout.Tab tab = tabLayout.getTabAt(pos >= 0 ? pos : 0);
        return tab != null && tab.getTag() != null ? (String) tab.getTag() : "all";
    }

    private void renderCurrentTabOrders(String filterKey) {
        List<ApiOrder> filtered = new ArrayList<>();
        for (ApiOrder order : apiOrders) {
            String st = order.status != null ? order.status.toLowerCase(Locale.ROOT) : "";
            if ("all".equals(filterKey)) {
                filtered.add(order);
            } else if (filterKey.equals(st) || ("cancelled".equals(filterKey) && "canceled".equals(st))) {
                filtered.add(order);
            }
        }

        if (filtered.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            ordersList.setVisibility(View.GONE);
            UiAnimations.fadeSlideUp(emptyState, 60);

            TextView emptyTitle = findViewById(R.id.emptyTitle);
            TextView emptySubtitle = findViewById(R.id.emptySubtitle);
            if (!"all".equals(filterKey)) {
                if (emptyTitle != null) emptyTitle.setText("No " + filterKey + " orders");
                if (emptySubtitle != null) emptySubtitle.setText("Your " + filterKey + " orders will appear here.");
            } else {
                if (emptyTitle != null) emptyTitle.setText("No orders yet");
                if (emptySubtitle != null) emptySubtitle.setText("When you place an order, it will appear here.");
            }
        } else {
            emptyState.setVisibility(View.GONE);
            ordersList.setVisibility(View.VISIBLE);
            ordersList.removeAllViews();

            for (ApiOrder order : filtered) {
                OrderCardData data = toCardData(order);
                View cardView = OrderCardBinder.inflateAndBind(this, ordersList, data, cardListener);
                ordersList.addView(cardView);
                UiAnimations.fadeSlideUp(cardView, ordersList.getChildCount() * 40);
            }
        }
    }

    private OrderCardData toCardData(ApiOrder o) {
        OrderCardData d = new OrderCardData();
        d.apiOrder = o;
        d.orderId = String.valueOf(o.id);
        d.orderNumber = o.getDisplayOrderNumber();
        d.status = o.status;
        d.statusLabel = o.getStatusLabel();
        d.dateText = OrderFormat.date(o.paidAt != null ? o.paidAt : (o.tracking != null && o.tracking.events != null && !o.tracking.events.isEmpty() ? o.tracking.events.get(0).timestamp : null));
        d.itemCount = o.getTotalItemCount();
        d.totalText = OrderFormat.money(o.getGrandTotalDouble());
        d.thumbnailUrls = new ArrayList<>();

        if (o.items != null) {
            for (ApiOrderItem it : o.items) {
                d.thumbnailUrls.add(it.getDisplayImageUrl());
            }
        }

        ApiTracking t = o.tracking;
        d.steps = new ArrayList<>();
        if (t != null) {
            d.provider = t.shippingProvider;
            d.trackingNumber = t.trackingNumber;
            if (t.events != null) {
                for (ApiTrackingEvent e : t.events) {
                    d.steps.add(new TrackingStep(
                            e.status,
                            e.label,
                            e.completed,
                            OrderFormat.dateTime(e.timestamp)
                    ));
                    if ("payment_confirmed".equals(e.status) && e.completed) {
                        d.paid = true;
                    }
                }
            }
        }

        if (o.paidAt != null || "paid".equalsIgnoreCase(o.paymentStatus) || "completed".equalsIgnoreCase(o.status)) {
            d.paid = true;
        }

        d.payOnDelivery = !d.paid && "cod".equalsIgnoreCase(o.paymentMethod);
        return d;
    }

    private OrderCardData toCardData(OrderManager.Order o) {
        OrderCardData d = new OrderCardData();
        d.mockOrder = o;
        d.orderId = o.orderId;
        d.orderNumber = "ORD-" + o.orderId;
        d.status = o.status.name().toLowerCase();
        d.statusLabel = o.status.label;
        d.dateText = o.date;
        d.itemCount = o.getTotalItemCount();
        d.totalText = OrderFormat.money(o.totalAmount);
        d.thumbnailUrls = new ArrayList<>();

        if (o.items != null) {
            for (OrderManager.OrderItem it : o.items) {
                d.thumbnailUrls.add(it.productImageUrl);
            }
        }

        d.steps = new ArrayList<>();
        boolean isDelivered = o.status == OrderManager.OrderStatus.DELIVERED;
        boolean isShipped = o.status == OrderManager.OrderStatus.SHIPPED || isDelivered;

        d.steps.add(new TrackingStep("order_created", "Order Confirmed", true, o.date));
        d.steps.add(new TrackingStep("payment_confirmed", "Payment Confirmed", true, o.date));
        d.steps.add(new TrackingStep("shipped", "Order Shipped", isShipped, null));
        d.steps.add(new TrackingStep("delivered", "Order Delivered", isDelivered, null));

        d.paid = true;
        d.payOnDelivery = false;
        return d;
    }

    private void displayMockOrders(List<OrderManager.Order> orders) {
        if (orders.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            ordersList.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            ordersList.setVisibility(View.VISIBLE);
            ordersList.removeAllViews();

            for (OrderManager.Order order : orders) {
                OrderCardData data = toCardData(order);
                View cardView = OrderCardBinder.inflateAndBind(this, ordersList, data, cardListener);
                ordersList.addView(cardView);
            }
        }
    }

    private void showSkeletonLoading() {
        if (skeletonLoadingContainer == null) return;
        
        skeletonLoadingContainer.removeAllViews();
        skeletonLoadingContainer.setVisibility(View.VISIBLE);
        ordersList.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        // Add 3-4 skeleton order cards
        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < 4; i++) {
            View skeletonCard = inflater.inflate(R.layout.skeleton_order_card, skeletonLoadingContainer, false);
            skeletonLoadingContainer.addView(skeletonCard);
        }
    }

    private void hideSkeletonLoading() {
        if (skeletonLoadingContainer != null) {
            skeletonLoadingContainer.setVisibility(View.GONE);
            skeletonLoadingContainer.removeAllViews();
        }
    }
}
