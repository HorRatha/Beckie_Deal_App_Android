package aeu.ratha.beckie_deal_app.ui.orders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class OrderFormat {
    private OrderFormat() {}

    private static Date parse(String iso) throws Exception {
        if (iso == null || iso.trim().isEmpty()) return null;
        String clean = iso.replaceAll("\\.\\d+Z$", "Z").replaceAll("Z$", "+00:00");
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).parse(clean);
        } catch (Exception e) {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(clean.substring(0, Math.min(clean.length(), 19)));
        }
    }

    /** "2026-10-28T10:37:02+00:00" -> "Oct 28, 17:37" (device time zone) */
    public static String dateTime(String iso) {
        if (iso == null || iso.isEmpty()) return null;
        try { 
            Date d = parse(iso);
            if (d == null) return iso;
            return new SimpleDateFormat("MMM d, HH:mm", Locale.US).format(d); 
        } catch (Exception e) { 
            return iso; 
        }
    }

    /** -> "Oct 28, 2026" */
    public static String date(String iso) {
        if (iso == null || iso.isEmpty()) return "Recent";
        try { 
            Date d = parse(iso);
            if (d == null) return iso.length() >= 10 ? iso.substring(0, 10) : iso;
            return new SimpleDateFormat("MMM d, yyyy", Locale.US).format(d); 
        } catch (Exception e) { 
            return iso.length() >= 10 ? iso.substring(0, 10) : iso; 
        }
    }

    public static String money(double value) {
        return String.format(Locale.US, "$%,.2f", value);
    }
}
