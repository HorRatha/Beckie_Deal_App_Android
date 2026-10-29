package aeu.ratha.beckie_deal_app.utils.managers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.ApiOrder;

public class NotificationStorageManager {

    private static final String PREF_NAME = "beckie_deal_notifications_pref";
    private static final String KEY_NOTIFICATIONS = "stored_notifications_json";
    private static final String KEY_ORDER_STATUS_MAP = "order_status_history_map";
    private static final String CHANNEL_ID = "order_updates_channel";

    public static class NotificationData {
        public String id;
        public String title;
        public String body;
        public String timeAgo;
        public String type; // "order", "promo", "alert"
        public int iconRes;
        public int iconTint;
        public boolean unread;
        public long timestamp;

        public NotificationData(String title, String body, String timeAgo, String type,
                                int iconRes, int iconTint, boolean unread) {
            this.id = String.valueOf(System.currentTimeMillis() + (long)(Math.random() * 1000));
            this.title = title;
            this.body = body;
            this.timeAgo = timeAgo;
            this.type = type;
            this.iconRes = iconRes;
            this.iconTint = iconTint;
            this.unread = unread;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static List<NotificationData> getNotifications(Context context) {
        SharedPreferences prefs = getPrefs(context);
        String json = prefs.getString(KEY_NOTIFICATIONS, null);
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Type listType = new TypeToken<ArrayList<NotificationData>>() {}.getType();
            List<NotificationData> list = new Gson().fromJson(json, listType);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void saveNotifications(Context context, List<NotificationData> notifications) {
        SharedPreferences prefs = getPrefs(context);
        String json = new Gson().toJson(notifications);
        prefs.edit().putString(KEY_NOTIFICATIONS, json).apply();
    }

    public static void addNotification(Context context, NotificationData notification) {
        List<NotificationData> list = getNotifications(context);
        list.add(0, notification); // newest first
        saveNotifications(context, list);
    }

    public static void markAllAsRead(Context context) {
        List<NotificationData> list = getNotifications(context);
        for (NotificationData item : list) {
            item.unread = false;
        }
        saveNotifications(context, list);
    }

    /**
     * Inspects list of fetched orders, detects status or tracking changes,
     * and generates dynamic notifications + posts system notification!
     */
    public static void checkAndNotifyStatusChanges(Context context, List<ApiOrder> orders) {
        if (orders == null || orders.isEmpty() || context == null) return;

        SharedPreferences prefs = getPrefs(context);
        String mapJson = prefs.getString(KEY_ORDER_STATUS_MAP, null);
        Gson gson = new Gson();
        Type mapType = new TypeToken<HashMap<String, String>>() {}.getType();
        Map<String, String> statusMap = mapJson != null ? gson.fromJson(mapJson, mapType) : new HashMap<>();
        if (statusMap == null) statusMap = new HashMap<>();

        boolean updatedMap = false;

        for (ApiOrder order : orders) {
            String orderKey = String.valueOf(order.id);
            String currentStatus = order.getStatusLabel();

            if (statusMap.containsKey(orderKey)) {
                String previousStatus = statusMap.get(orderKey);
                if (previousStatus != null && !previousStatus.equalsIgnoreCase(currentStatus)) {
                    // Status changed! Generate notification
                    String title = "Order #" + order.getDisplayOrderNumber() + " Updated";
                    String body = "Your order status is now: " + currentStatus + ". Tap to track your shipment.";

                    NotificationData notification = new NotificationData(
                            title,
                            body,
                            "Just now",
                            "order",
                            R.drawable.ic_local_shipping,
                            R.color.primary,
                            true
                    );
                    addNotification(context, notification);
                    postSystemNotification(context, title, body, (int) (order.id % 10000));

                    statusMap.put(orderKey, currentStatus);
                    updatedMap = true;
                }
            } else {
                // First time seeing this order
                statusMap.put(orderKey, currentStatus);
                updatedMap = true;
            }
        }

        if (updatedMap) {
            prefs.edit().putString(KEY_ORDER_STATUS_MAP, gson.toJson(statusMap)).apply();
        }
    }

    private static void postSystemNotification(Context context, String title, String body, int notificationId) {
        try {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        "Order Tracking Updates",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("Notifications when your order status is updated");
                nm.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notifications)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            nm.notify(notificationId, builder.build());
        } catch (Exception ignored) {}
    }
}
