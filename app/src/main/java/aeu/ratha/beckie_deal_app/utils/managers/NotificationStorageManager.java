package aeu.ratha.beckie_deal_app.utils.managers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Promotion;
import aeu.ratha.beckie_deal_app.data.repository.PromotionRepository;
import aeu.ratha.beckie_deal_app.security.TokenManager;

public class NotificationStorageManager {

    private static final String PREF_BASE_NAME = "beckie_deal_notifications_pref_";

    public static class NotificationData {
        public String id;
        public String title;
        public String body;
        public String timeAgo;
        public String type; // "promo"
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

    private static String getPrefName(Context context) {
        try {
            TokenManager tm = TokenManager.getInstance(context);
            String userId = tm.getUserId();
            if (userId == null || userId.trim().isEmpty()) {
                userId = tm.getUserEmail();
            }
            if (userId == null || userId.trim().isEmpty()) {
                userId = "guest";
            }
            userId = userId.replaceAll("[^a-zA-Z0-9_]", "_");
            return PREF_BASE_NAME + userId;
        } catch (Exception e) {
            return PREF_BASE_NAME + "guest";
        }
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(getPrefName(context), Context.MODE_PRIVATE);
    }

    public static List<NotificationData> getNotifications(Context context) {
        SharedPreferences prefs = getPrefs(context);
        String json = prefs.getString("stored_notifications_json", null);
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
        prefs.edit().putString("stored_notifications_json", json).apply();
    }

    public static void markAllAsRead(Context context) {
        List<NotificationData> list = getNotifications(context);
        for (NotificationData item : list) {
            item.unread = false;
        }
        saveNotifications(context, list);
        updateNotificationBadge(context instanceof Activity ? (Activity) context : null);
    }

    /**
     * Updates the top bar notification badge (R.id.notificationBadge) with unread count.
     */
    public static void updateNotificationBadge(Activity activity) {
        if (activity == null) return;
        TextView badge = activity.findViewById(R.id.notificationBadge);
        if (badge == null) return;

        List<NotificationData> list = getNotifications(activity);
        int unreadCount = 0;
        for (NotificationData nd : list) {
            if (nd.unread) unreadCount++;
        }

        if (unreadCount > 0) {
            badge.setText(String.valueOf(unreadCount));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    /**
     * Automatically fetches active promotions/discount codes from PromotionRepository
     * and syncs them into the notification feed as professional discount alerts.
     */
    public static void syncPromotionsToNotifications(Context context, Runnable onSynced) {
        if (context == null) {
            if (onSynced != null) onSynced.run();
            return;
        }
        PromotionRepository repo = new PromotionRepository(context);
        repo.getPromotions(new PromotionRepository.PromotionCallback() {
            @Override
            public void onSuccess(List<Promotion> promotions) {
                if (promotions == null || promotions.isEmpty()) {
                    if (onSynced != null) onSynced.run();
                    return;
                }
                List<NotificationData> currentList = getNotifications(context);
                boolean addedNew = false;

                for (Promotion p : promotions) {
                    String promoId = "promo_" + p.getId();
                    boolean exists = false;
                    for (NotificationData nd : currentList) {
                        if (nd.id != null && nd.id.equals(promoId)) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        NotificationData nd = new NotificationData(
                                "New Discount Code: " + p.getCode(),
                                p.getDescription() != null ? p.getDescription() : ("Use code " + p.getCode() + " for special discount!"),
                                "Just now",
                                "promo",
                                R.drawable.ic_local_offer,
                                R.color.primary,
                                true
                        );
                        nd.id = promoId;
                        currentList.add(0, nd);
                        addedNew = true;
                    }
                }

                if (addedNew) {
                    saveNotifications(context, currentList);
                }
                if (onSynced != null) onSynced.run();
            }

            @Override
            public void onError(String errorMessage) {
                if (onSynced != null) onSynced.run();
            }
        });
    }
}
