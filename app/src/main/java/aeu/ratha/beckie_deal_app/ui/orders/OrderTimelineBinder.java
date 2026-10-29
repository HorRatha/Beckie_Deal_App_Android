package aeu.ratha.beckie_deal_app.ui.orders;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.List;

import aeu.ratha.beckie_deal_app.R;

public final class OrderTimelineBinder {

    private OrderTimelineBinder() {}

    public static void fill(Context ctx, LinearLayout container, List<TrackingStep> steps,
                            boolean cancelled, String provider, String trackingNumber) {
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(ctx);

        int current = -1;
        if (steps != null && !cancelled) {
            for (int i = 0; i < steps.size(); i++) {
                if (!steps.get(i).completed) { current = i; break; }
            }
        }
        boolean allDone = steps != null && !steps.isEmpty() && current == -1 && !cancelled;

        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                TrackingStep s = steps.get(i);
                View row = inflater.inflate(R.layout.item_tracking_step, container, false);
                TextView dot = row.findViewById(R.id.stepDot);
                View line = row.findViewById(R.id.stepLine);
                TextView label = row.findViewById(R.id.stepLabel);
                TextView time = row.findViewById(R.id.stepTime);

                label.setText(s.label);

                if (s.completed) {
                    dot.setBackgroundResource(allDone ? R.drawable.dot_done_green : R.drawable.dot_done);
                    dot.setText("✓");
                    time.setText(s.timeText == null ? "" : s.timeText);
                    time.setVisibility(s.timeText == null ? View.GONE : View.VISIBLE);
                } else if (i == current) {
                    dot.setBackgroundResource(R.drawable.dot_current);
                    dot.setText("");
                    time.setText(waitingText(s.key));
                    time.setVisibility(View.VISIBLE);
                } else {
                    dot.setBackgroundResource(R.drawable.dot_future);
                    dot.setText("");
                    label.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted));
                    time.setVisibility(View.GONE);
                }

                if (i == steps.size() - 1) {
                    line.setVisibility(View.GONE);
                } else {
                    line.setBackgroundColor(ContextCompat.getColor(ctx,
                            s.completed ? (allDone ? R.color.step_done_green : R.color.brand_orange)
                                        : R.color.border_color));
                }
                container.addView(row);
            }
        }

        if (cancelled) {
            TextView t = new TextView(ctx);
            t.setText("This order was cancelled");
            t.setTextColor(ContextCompat.getColor(ctx, R.color.chip_red_fg));
            t.setTextSize(13);
            t.setPadding(0, OrderCardBinder.dp(ctx, 8), 0, 0);
            container.addView(t);
        }

        // Carrier + tracking number
        boolean hasNumber = trackingNumber != null && !trackingNumber.isEmpty();
        TextView info = new TextView(ctx);
        info.setTextColor(ContextCompat.getColor(ctx, R.color.text_secondary));
        info.setTextSize(12);
        info.setLineSpacing(0, 1.3f);
        info.setPadding(0, OrderCardBinder.dp(ctx, 12), 0, 0);
        info.setText("Carrier: " + (provider == null ? "—" : provider)
                + "\nTracking no.: " + (hasNumber ? trackingNumber + "  (tap to copy)" : "Not available yet"));
        if (hasNumber) {
            info.setOnClickListener(v -> {
                ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) cm.setPrimaryClip(ClipData.newPlainText("Tracking number", trackingNumber));
                Toast.makeText(ctx, "Tracking number copied", Toast.LENGTH_SHORT).show();
            });
        }
        container.addView(info);
    }

    private static String waitingText(String key) {
        if ("payment_confirmed".equals(key)) return "Waiting for payment";
        if ("shipped".equals(key)) return "Waiting for shipment";
        if ("delivered".equals(key)) return "On its way";
        return "In progress";
    }
}
