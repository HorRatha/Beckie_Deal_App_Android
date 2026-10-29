package aeu.ratha.beckie_deal_app.ui.activities.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;
import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper;
import aeu.ratha.beckie_deal_app.utils.managers.NotificationStorageManager;

public class NotificationsActivity extends BaseActivity {

    private LinearLayout notificationsList;
    private View emptyState;
    private List<NotificationItem> allNotifications = new ArrayList<>();

    // Filter chips
    private TextView chipAll, chipOrders, chipPromos, chipAlerts;
    private TextView activeChip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        setupTopBar();
        setupChips();

        notificationsList = findViewById(R.id.notificationsList);
        emptyState = findViewById(R.id.emptyState);

        loadNotifications();

        BottomNavHelper.setup(this, BottomNavHelper.NavItem.ACCOUNT);
        BottomNavHelper.updateCartBadge(this);

        UiAnimations.fadeSlideDown(findViewById(R.id.appBarLayout), 0);
        UiAnimations.fadeSlideUp(findViewById(R.id.chipScroll), 80);
    }

    private void setupTopBar() {
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) backButton.setOnClickListener(v -> finish());

        TextView markAllRead = findViewById(R.id.markAllReadButton);
        if (markAllRead != null) markAllRead.setOnClickListener(v -> markAllRead());

        UiAnimations.pressBounce(backButton, markAllRead);
    }

    private void setupChips() {
        chipAll = findViewById(R.id.chipAll);
        chipOrders = findViewById(R.id.chipOrders);
        chipPromos = findViewById(R.id.chipPromos);
        chipAlerts = findViewById(R.id.chipAlerts);

        activeChip = chipAll;
        if (chipAll != null) {
            chipAll.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_selected));
            chipAll.setTextColor(ContextCompat.getColor(this, R.color.chip_selected_text));
        }

        chipAll.setOnClickListener(v -> selectChip(chipAll, "all"));
        chipOrders.setOnClickListener(v -> selectChip(chipOrders, "order"));
        chipPromos.setOnClickListener(v -> selectChip(chipPromos, "promo"));
        chipAlerts.setOnClickListener(v -> selectChip(chipAlerts, "alert"));

        UiAnimations.pressBounce(chipAll, chipOrders, chipPromos, chipAlerts);
    }

    private void selectChip(TextView chip, String filter) {
        if (activeChip != null) {
            activeChip.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_unselected));
            activeChip.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }
        chip.setBackground(ContextCompat.getDrawable(this, R.drawable.chip_selected));
        chip.setTextColor(ContextCompat.getColor(this, R.color.chip_selected_text));
        activeChip = chip;

        displayNotifications(filter);
    }

    private void markAllRead() {
        for (NotificationItem item : allNotifications) {
            item.unread = false;
        }
        NotificationStorageManager.markAllAsRead(this);
        String filter = getCurrentFilter();
        displayNotifications(filter);
    }

    private String getCurrentFilter() {
        if (activeChip == chipOrders) return "order";
        if (activeChip == chipPromos) return "promo";
        if (activeChip == chipAlerts) return "alert";
        return "all";
    }

    private static final long CARD_STAGGER_MS = 45L;

    private void loadNotifications() {
        // First display what is currently stored
        loadStoredNotifications();
        displayNotifications(getCurrentFilter());

        // Then sync new promotions from backend repository in background
        NotificationStorageManager.syncPromotionsToNotifications(this, () -> {
            loadStoredNotifications();
            runOnUiThread(() -> displayNotifications(getCurrentFilter()));
        });
    }

    private void loadStoredNotifications() {
        allNotifications = new ArrayList<>();
        List<NotificationStorageManager.NotificationData> stored = NotificationStorageManager.getNotifications(this);

        if (stored != null) {
            for (NotificationStorageManager.NotificationData data : stored) {
                allNotifications.add(new NotificationItem(
                        data.title,
                        data.body,
                        data.timeAgo != null ? data.timeAgo : "Recent",
                        data.type != null ? data.type : "order",
                        data.iconRes != 0 ? data.iconRes : R.drawable.ic_notifications,
                        data.iconTint != 0 ? data.iconTint : R.color.primary,
                        data.unread
                ));
            }
        }
    }

    private void displayNotifications(String filter) {
        if (notificationsList == null) return;
        notificationsList.removeAllViews();

        List<NotificationItem> filtered = new ArrayList<>();
        for (NotificationItem item : allNotifications) {
            if (filter.equals("all") || item.type.equals(filter)) {
                filtered.add(item);
            }
        }

        if (filtered.isEmpty()) {
            if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
            notificationsList.setVisibility(View.GONE);
            if (emptyState != null) UiAnimations.popIn(emptyState, 0);
        } else {
            if (emptyState != null) emptyState.setVisibility(View.GONE);
            notificationsList.setVisibility(View.VISIBLE);

            for (int i = 0; i < filtered.size(); i++) {
                inflateNotificationCard(filtered.get(i), i);
            }
        }
    }

    private void inflateNotificationCard(NotificationItem item, int position) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_notification, notificationsList, false);

        TextView title = card.findViewById(R.id.notificationTitle);
        TextView body = card.findViewById(R.id.notificationBody);
        TextView time = card.findViewById(R.id.notificationTime);
        ImageView icon = card.findViewById(R.id.notificationTypeIcon);
        View unreadDot = card.findViewById(R.id.unreadDot);

        title.setText(item.title);
        body.setText(item.body);
        time.setText(item.timeAgo);

        icon.setImageResource(item.iconRes);
        icon.setColorFilter(ContextCompat.getColor(this, item.iconTint));

        if (item.unread) {
            unreadDot.setVisibility(View.VISIBLE);
        } else {
            unreadDot.setVisibility(View.GONE);
        }

        card.setOnClickListener(v -> {
            item.unread = false;
            if (unreadDot.getVisibility() == View.VISIBLE) {
                unreadDot.animate()
                        .alpha(0f)
                        .scaleX(0f)
                        .scaleY(0f)
                        .setDuration(180)
                        .withEndAction(() -> unreadDot.setVisibility(View.GONE))
                        .start();
            }
        });
        UiAnimations.pressBounce(card);

        UiAnimations.fadeSlideUp(card, position * CARD_STAGGER_MS);

        notificationsList.addView(card);
    }

    static class NotificationItem {
        String title;
        String body;
        String timeAgo;
        String type; // "order", "promo", "alert"
        int iconRes;
        int iconTint;
        boolean unread;

        NotificationItem(String title, String body, String timeAgo, String type,
                         int iconRes, int iconTint, boolean unread) {
            this.title = title;
            this.body = body;
            this.timeAgo = timeAgo;
            this.type = type;
            this.iconRes = iconRes;
            this.iconTint = iconTint;
            this.unread = unread;
        }
    }
}
