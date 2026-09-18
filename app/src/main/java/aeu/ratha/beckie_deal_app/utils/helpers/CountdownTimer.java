package aeu.ratha.beckie_deal_app.utils.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Countdown timer utility to calculate remaining time until expiry.
 * Converts ISO 8601 dates to human-readable countdown format.
 */
public class CountdownTimer {
    
    /**
     * Parse ISO 8601 date string and return countdown text.
     * Examples:
     *   "2026-09-28T10:03:34+00:00" → "2d 14h 30m"
     *   "2026-10-13T01:34:03+00:00" → "23d 5h 12m"
     */
    public static String getCountdownText(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return "Unlimited";
        }
        
        try {
            // Parse ISO 8601 date with timezone support
            Date expiryDate = parseISO8601Date(isoDate);
            
            if (expiryDate == null) {
                return "Expired";
            }
            
            long now = System.currentTimeMillis();
            long expiry = expiryDate.getTime();
            long diffMs = expiry - now;
            
            // Already expired
            if (diffMs <= 0) {
                return "Expired";
            }
            
            // Calculate days, hours, minutes
            long days = TimeUnit.MILLISECONDS.toDays(diffMs);
            long hours = TimeUnit.MILLISECONDS.toHours(diffMs) - TimeUnit.DAYS.toHours(days);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diffMs));
            
            // Format output
            if (days > 0) {
                return String.format(Locale.getDefault(), "%dd %dh %dm", days, hours, minutes);
            } else if (hours > 0) {
                return String.format(Locale.getDefault(), "%dh %dm", hours, minutes);
            } else {
                return String.format(Locale.getDefault(), "%dm", minutes);
            }
        } catch (Exception e) {
            return "Soon";
        }
    }
    
    /**
     * Parse ISO 8601 date string with timezone support.
     * Handles formats: "2026-09-28T10:03:34+00:00", "2026-09-28T10:03:34Z", "2026-09-28T10:03:34"
     */
    private static Date parseISO8601Date(String isoDate) {
        try {
            // Remove timezone info to simplify parsing (we just care about the instant in time)
            String normalizedDate = isoDate;
            if (normalizedDate.contains("Z")) {
                normalizedDate = normalizedDate.replace("Z", "+00:00");
            }
            
            // Try parsing with timezone first (e.g., "2026-09-28T10:03:34+00:00")
            if (normalizedDate.contains("+") || normalizedDate.lastIndexOf("-") > 10) {
                // Extract just the date-time part, ignore timezone
                String dateTimePart = normalizedDate.substring(0, normalizedDate.lastIndexOf("+") >= 0 ? 
                    normalizedDate.lastIndexOf("+") : normalizedDate.lastIndexOf("-") > 10 ? 
                    normalizedDate.lastIndexOf("-") : normalizedDate.length());
                
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                return isoFormat.parse(dateTimePart);
            } else {
                // Parse without timezone (e.g., "2026-09-28T10:03:34")
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                return isoFormat.parse(normalizedDate);
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Check if promotion has expired.
     */
    public static boolean isExpired(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return false; // Unlimited promotions never expire
        }
        
        try {
            Date expiryDate = parseISO8601Date(isoDate);
            
            if (expiryDate == null) {
                return true;
            }
            
            long now = System.currentTimeMillis();
            long expiry = expiryDate.getTime();
            
            return now > expiry;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get remaining time in milliseconds.
     */
    public static long getRemainingMillis(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) {
            return Long.MAX_VALUE; // Unlimited
        }
        
        try {
            Date expiryDate = parseISO8601Date(isoDate);
            
            if (expiryDate == null) {
                return 0;
            }
            
            long now = System.currentTimeMillis();
            long expiry = expiryDate.getTime();
            long remaining = expiry - now;
            
            return Math.max(0, remaining);
        } catch (Exception e) {
            return 0;
        }
    }
}
