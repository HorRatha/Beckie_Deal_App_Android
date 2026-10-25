package aeu.ratha.beckie_deal_app.ui.activities.profile;

import aeu.ratha.beckie_deal_app.R;


import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;

import android.os.Bundle;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;

import android.view.LayoutInflater;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;

import android.view.View;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;

import android.widget.ImageView;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;

import android.widget.LinearLayout;

import aeu.ratha.beckie_deal_app.utils.helpers.BaseActivity;
import aeu.ratha.beckie_deal_app.utils.animations.UiAnimations;

import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends BaseActivity {

    private LinearLayout notificationsList;
    private View emptyState;
    private List<NotificationItem> allNotifications;

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

        initMockNotifications();
        displayNotifications("all");

        aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.setup(this, aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.NavItem.ACCOUNT);
        aeu.ratha.beckie_deal_app.utils.helpers.BottomNavHelper.updateCartBadge(this);

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
        chip.setTextColor(ContextCompat.getColor(this, R.color.white_fixed));
        activeChip = chip;

        displayNotifications(filter);
    }

    private void markAllRead() {
        for (NotificationItem item : allNotifications) {
            item.unread = false;
        }
        // Refresh the current view
        String filter = getCurrentFilter();
        displayNotifications(filter);
    }

    private String getCurrentFilter() {
        if (activeChip == chipOrders) return "order";
        if (activeChip == chipPromos) return "promo";
        if (activeChip == chipAlerts) return "alert";
        return "all";
    }

    // Stagger delay between each notification card's entrance.
    private static final long CARD_STAGGER_MS = 45L;

    private void displayNotifications(String filter) {
        notificationsList.removeAllViews();

        List<NotificationItem> filtered = new ArrayList<>();
        for (NotificationItem item : allNotifications) {
            if (filter.equals("all") || item.type.equals(filter)) {
                filtered.add(item);
            }
        }

        if (filtered.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            notificationsList.setVisibility(View.GONE);
            UiAnimations.popIn(emptyState, 0);
        } else {
            emptyState.setVisibility(View.GONE);
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
        View iconBg = card.findViewById(R.id.iconBackground);

        title.setText(item.title);
        body.setText(item.body);
        time.setText(item.timeAgo);

        // Set icon based on type
        icon.setImageResource(item.iconRes);
        icon.setColorFilter(ContextCompat.getColor(this, item.iconTint));

        // Unread indicator
        if (item.unread) {
            unreadDot.setVisibility(View.VISIBLE);
        } else {
            unreadDot.setVisibility(View.GONE);
        }

        // Tap to mark as read — fade the unread dot out instead of an instant cut
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

        // Staggered slide-up + fade entrance for each card, top to bottom
        UiAnimations.fadeSlideUp(card, position * CARD_STAGGER_MS);

        notificationsList.addView(card);
    }

    // ── Mock Data ─────────────────────────────────────────────────────────────

    private void initMockNotifications() {
        allNotifications = new ArrayList<>();

        allNotifications.add(new NotificationItem(
                "Your order has shipped!",
                "Order #1042 is on its way to your address.",
                "2 hours ago",
                "order",
                R.drawable.ic_local_shipping,
                R.color.primary,
                true
        ));

        allNotifications.add(new NotificationItem(
                "Flash Sale: 20% Off!",
                "Selected electronics are discounted today only.",
                "5 hours ago",
                "promo",
                R.drawable.ic_local_offer,
                R.color.primary,
                true
        ));

        allNotifications.add(new NotificationItem(
                "Order Delivered",
                "Order #1038 has been delivered successfully.",
                "Yesterday",
                "order",
                R.drawable.ic_check_circle,
                R.color.green_stock,
                false
        ));

        allNotifications.add(new NotificationItem(
                "Price Drop Alert",
                "iPhone 15 Pro Max dropped $50 in price.",
                "2 days ago",
                "alert",
                R.drawable.ic_trending_down,
                R.color.primary,
                false
        ));

        allNotifications.add(new NotificationItem(
                "Weekend Deal: Free Shipping",
                "All orders over $100 ship free this weekend.",
                "3 days ago",
                "promo",
                R.drawable.ic_local_offer,
                R.color.primary,
                false
        ));

        allNotifications.add(new NotificationItem(
                "Order Confirmed",
                "Order #1042 has been confirmed and is being prepared.",
                "4 days ago",
                "order",
                R.drawable.ic_package,
                R.color.primary,
                false
        ));

        allNotifications.add(new NotificationItem(
                "Back in Stock",
                "MacBook Pro M3 is now available again.",
                "5 days ago",
                "alert",
                R.drawable.ic_notifications,
                R.color.primary,
                false
        ));
    }

    // ── Data class ────────────────────────────────────────────────────────────

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
