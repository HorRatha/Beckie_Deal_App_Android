package aeu.ratha.beckie_deal_app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Singleton that persists placed orders in-memory.
 * Each time checkout completes, the cart snapshot is saved here as an Order.
 */
public class OrderManager {

    // ── Order model ───────────────────────────────────────────────────────────

    public enum OrderStatus {
        PROCESSING("Processing", "#F97316"),
        SHIPPED("Shipped", "#1565C0"),
        DELIVERED("Delivered", "#22C55E"),
        CANCELLED("Cancelled", "#EF4444");

        public final String label;
        public final String color;

        OrderStatus(String label, String color) {
            this.label = label;
            this.color = color;
        }
    }

    public static class OrderItem {
        public final String productName;
        public final String productPrice;
        public final String productImageUrl;
        public final int quantity;

        public OrderItem(String name, String price, String imageUrl, int quantity) {
            this.productName = name;
            this.productPrice = price;
            this.productImageUrl = imageUrl;
            this.quantity = quantity;
        }
    }

    public static class Order {
        public final String orderId;
        public final String date;
        public final List<OrderItem> items;
        public final double totalAmount;
        public OrderStatus status;

        public Order(String orderId, String date, List<OrderItem> items,
                     double totalAmount, OrderStatus status) {
            this.orderId = orderId;
            this.date = date;
            this.items = items;
            this.totalAmount = totalAmount;
            this.status = status;
        }

        public int getTotalItemCount() {
            int count = 0;
            for (OrderItem item : items) count += item.quantity;
            return count;
        }
    }

    // ── Singleton ─────────────────────────────────────────────────────────────

    private static OrderManager instance;
    private final List<Order> orders = new ArrayList<>();
    private int orderCounter = 1000;

    private OrderManager() {}

    public static OrderManager getInstance() {
        if (instance == null) instance = new OrderManager();
        return instance;
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Creates an order from the current cart contents and adds it to history.
     * Call this when payment succeeds (before clearing the cart).
     */
    public Order placeOrder(List<CartManager.CartItem> cartItems, double totalAmount) {
        orderCounter++;
        String orderId = "BD-" + orderCounter;
        String date = new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(new Date());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartManager.CartItem cartItem : cartItems) {
            orderItems.add(new OrderItem(
                    cartItem.product.name,
                    cartItem.product.price,
                    cartItem.product.imageUrl,
                    cartItem.quantity
            ));
        }

        Order order = new Order(orderId, date, orderItems, totalAmount, OrderStatus.PROCESSING);
        orders.add(0, order); // newest first
        return order;
    }

    /** Get all orders (newest first). */
    public List<Order> getAllOrders() {
        return orders;
    }

    /** Get orders filtered by status. */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        List<Order> filtered = new ArrayList<>();
        for (Order order : orders) {
            if (order.status == status) filtered.add(order);
        }
        return filtered;
    }

    /** Get active orders (processing + shipped). */
    public List<Order> getActiveOrders() {
        List<Order> active = new ArrayList<>();
        for (Order order : orders) {
            if (order.status == OrderStatus.PROCESSING || order.status == OrderStatus.SHIPPED) {
                active.add(order);
            }
        }
        return active;
    }

    /** Get completed orders (delivered + cancelled). */
    public List<Order> getCompletedOrders() {
        List<Order> completed = new ArrayList<>();
        for (Order order : orders) {
            if (order.status == OrderStatus.DELIVERED || order.status == OrderStatus.CANCELLED) {
                completed.add(order);
            }
        }
        return completed;
    }

    public int getOrderCount() {
        return orders.size();
    }
}
