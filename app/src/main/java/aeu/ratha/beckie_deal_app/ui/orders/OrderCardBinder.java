package aeu.ratha.beckie_deal_app.ui.orders;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import aeu.ratha.beckie_deal_app.R;

public final class OrderCardBinder {

    private OrderCardBinder() {}

    public interface Listener {
        void onViewDetails(OrderCardData data);
        void onTrack(OrderCardData data);
        /** Optional: return true and implement onBuyAgain to show "Buy again" on finished orders. */
        default boolean supportsBuyAgain() { return false; }
        default void onBuyAgain(OrderCardData data) {}
    }

    public static View inflateAndBind(Context ctx, ViewGroup parent, OrderCardData d, Listener l) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_order_card, parent, false);
        bind(v, d, l);
        return v;
    }

    public static void bind(View v, OrderCardData d, Listener l) {
        TextView shortNo = v.findViewById(R.id.orderNumberShort);
        TextView fullNo = v.findViewById(R.id.orderNumberFull);
        TextView chip = v.findViewById(R.id.orderStatusChip);
        TextView paid = v.findViewById(R.id.orderPaidChip);
        TextView dateItems = v.findViewById(R.id.orderDateItems);
        TextView total = v.findViewById(R.id.orderTotal);
        TextView btnView = v.findViewById(R.id.btnViewDetails);
        TextView btnTrack = v.findViewById(R.id.btnTrack);

        shortNo.setText("Order #" + shortRef(d.orderNumber));
        fullNo.setText(d.orderNumber);
        applyStatusChip(chip, d.status, d.statusLabel);

        // Payment tag: Paid (after payment_confirmed) or Pay on delivery (COD)
        Context ctx = v.getContext();
        if (d.paid) {
            paid.setText("✓ Paid");
            paid.setTextColor(ContextCompat.getColor(ctx, R.color.chip_green_fg));
            paid.setBackgroundResource(R.drawable.bg_chip_paid);
            paid.setVisibility(View.VISIBLE);
        } else if (d.payOnDelivery) {
            paid.setText("Pay on delivery");
            paid.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
            paid.setBackgroundResource(R.drawable.form_field_background);
            paid.setVisibility(View.VISIBLE);
        } else {
            paid.setVisibility(View.GONE);
        }

        dateItems.setText(d.dateText + " · " + d.itemCount + (d.itemCount == 1 ? " item" : " items"));
        total.setText(d.totalText);

        bindThumbs(v, d);
        bindTracker(v, d);

        // Buttons
        String key = key(d.status);
        boolean active = key.equals("pending") || key.equals("processing") || key.equals("shipped");
        btnView.setOnClickListener(x -> { if (l != null) l.onViewDetails(d); });
        v.setOnClickListener(x -> { if (l != null) l.onViewDetails(d); });

        if (active) {
            btnTrack.setText("Track order ›");
            btnTrack.setVisibility(View.VISIBLE);
            btnTrack.setOnClickListener(x -> { if (l != null) l.onTrack(d); });
        } else if (l != null && l.supportsBuyAgain()) {
            btnTrack.setText("Buy again");
            btnTrack.setVisibility(View.VISIBLE);
            btnTrack.setOnClickListener(x -> l.onBuyAgain(d));
        } else {
            btnTrack.setVisibility(View.GONE);
        }
    }

    // ── Status chip: the single place that maps status -> color ──────────────
    public static void applyStatusChip(TextView chip, String status, String label) {
        Context ctx = chip.getContext();
        int bg, fg;
        switch (key(status)) {
            case "processing":
            case "shipped":    bg = R.color.chip_blue_bg;  fg = R.color.chip_blue_fg;  break;
            case "completed":
            case "delivered":  bg = R.color.chip_green_bg; fg = R.color.chip_green_fg; break;
            case "cancelled":
            case "canceled":   bg = R.color.chip_red_bg;   fg = R.color.chip_red_fg;   break;
            case "pending":
            default:           bg = R.color.chip_amber_bg; fg = R.color.chip_amber_fg; break;
        }

        android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        shape.setCornerRadius(dp(ctx, 8));
        shape.setColor(ContextCompat.getColor(ctx, bg));

        chip.setBackground(shape);
        chip.setTextColor(ContextCompat.getColor(ctx, fg));
        chip.setText(label != null && !label.isEmpty() ? label : capitalize(status));
    }

    // ── Thumbnails ───────────────────────────────────────────────────────────
    private static void bindThumbs(View v, OrderCardData d) {
        LinearLayout box = v.findViewById(R.id.orderThumbs);
        TextView more = v.findViewById(R.id.orderThumbsMore);
        Context ctx = v.getContext();
        box.removeAllViews();

        List<String> urls = d.thumbnailUrls;
        int shown = urls == null ? 0 : Math.min(4, urls.size());
        for (int i = 0; i < shown; i++) {
            ImageView img = new ImageView(ctx);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(ctx, 40), dp(ctx, 40));
            lp.setMarginEnd(dp(ctx, 6));
            img.setLayoutParams(lp);
            img.setPadding(dp(ctx, 3), dp(ctx, 3), dp(ctx, 3), dp(ctx, 3));
            img.setBackgroundColor(ContextCompat.getColor(ctx, R.color.card_bg_secondary));
            img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Glide.with(ctx).load(urls.get(i)).placeholder(R.drawable.placeholder_image).into(img);
            box.addView(img);
        }
        box.setVisibility(shown == 0 ? View.GONE : View.VISIBLE);

        int remaining = d.itemCount - shown;
        if (shown > 0 && remaining > 0) {
            more.setText("+" + remaining);
            more.setVisibility(View.VISIBLE);
        } else {
            more.setVisibility(View.GONE);
        }
    }

    // ── Progress dots, built from tracking.events (3 or 4 steps both work) ───
    private static void bindTracker(View v, OrderCardData d) {
        Context ctx = v.getContext();
        LinearLayout row = v.findViewById(R.id.trackerRow);
        TextView caption = v.findViewById(R.id.trackerCaption);
        row.removeAllViews();

        String key = key(d.status);
        if (key.equals("cancelled") || key.equals("canceled")) {
            row.setVisibility(View.GONE);
            caption.setVisibility(View.VISIBLE);
            caption.setText("This order was cancelled");
            return;
        }
        List<TrackingStep> steps = d.steps;
        if (steps == null || steps.isEmpty()) {
            row.setVisibility(View.GONE);
            caption.setVisibility(View.GONE);
            return;
        }
        row.setVisibility(View.VISIBLE);
        caption.setVisibility(View.VISIBLE);

        int current = -1;
        for (int i = 0; i < steps.size(); i++) {
            if (!steps.get(i).completed) { current = i; break; }
        }
        boolean allDone = current == -1;

        for (int i = 0; i < steps.size(); i++) {
            if (i > 0) row.addView(line(ctx, steps.get(i - 1).completed, allDone));
            int state = steps.get(i).completed ? 0 : (i == current ? 1 : 2); // 0 done, 1 current, 2 future
            row.addView(dot(ctx, state, allDone));
        }

        // Caption = the last finished step (bold) + its time
        TrackingStep last = null;
        for (int i = steps.size() - 1; i >= 0; i--) {
            if (steps.get(i).completed) { last = steps.get(i); break; }
        }
        SpannableStringBuilder sb = new SpannableStringBuilder();
        if (last == null) {
            sb.append("Waiting for confirmation");
        } else {
            int s = sb.length();
            sb.append(last.label);
            sb.setSpan(new StyleSpan(Typeface.BOLD), s, sb.length(), 0);
            if (last.timeText != null && !last.timeText.isEmpty()) sb.append(" · ").append(last.timeText);
        }
        caption.setText(sb);
    }

    private static View dot(Context ctx, int state, boolean allDone) {
        TextView t = new TextView(ctx);
        t.setLayoutParams(new LinearLayout.LayoutParams(dp(ctx, 16), dp(ctx, 16)));
        t.setGravity(Gravity.CENTER);
        t.setTextSize(10);
        t.setTypeface(Typeface.DEFAULT_BOLD);
        t.setTextColor(ContextCompat.getColor(ctx, R.color.white_fixed));
        if (state == 0) {
            t.setBackgroundResource(allDone ? R.drawable.dot_done_green : R.drawable.dot_done);
            t.setText("✓");
        } else if (state == 1) {
            t.setBackgroundResource(R.drawable.dot_current);
        } else {
            t.setBackgroundResource(R.drawable.dot_future);
        }
        return t;
    }

    private static View line(Context ctx, boolean previousDone, boolean allDone) {
        View line = new View(ctx);
        line.setLayoutParams(new LinearLayout.LayoutParams(0, dp(ctx, 2), 1f));
        int color = !previousDone ? R.color.border_color
                : (allDone ? R.color.step_done_green : R.color.brand_orange);
        line.setBackgroundColor(ContextCompat.getColor(ctx, color));
        return line;
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    static String key(String status) {
        return status == null ? "" : status.toLowerCase(Locale.ROOT);
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    /** "ORD-20261028103702-WYPKF" -> "WYPKF" */
    private static String shortRef(String orderNumber) {
        if (orderNumber == null) return "";
        int i = orderNumber.lastIndexOf('-');
        return i >= 0 ? orderNumber.substring(i + 1) : orderNumber;
    }

    public static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }
}
