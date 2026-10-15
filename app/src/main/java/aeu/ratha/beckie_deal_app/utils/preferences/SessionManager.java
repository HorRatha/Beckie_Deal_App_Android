package aeu.ratha.beckie_deal_app.utils.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * SessionManager handles 5-minute inactivity timeout and session state management.
 * 
 * When the user closes the app and returns after 5+ minutes:
 * - All saved scroll positions are cleared
 * - Cached repositories are flushed
 * - Fresh data will be fetched from the API
 * 
 * This ensures users get updated content and don't see stale data from previous sessions.
 */
public final class SessionManager {
    
    private static final String TAG = "SessionManager";
    private static final String SESSION_PREFS = "session_prefs";
    private static final String KEY_LAST_ACTIVE_TIME = "last_active_time";
    private static final long FIVE_MINUTES_MS = 5 * 60 * 1000; // 300,000 ms
    
    private SessionManager() {}
    
    /**
     * Record the current time as the last active timestamp.
     * Call this in onPause() to capture when user leaves the app.
     *
     * @param context Application or Activity context
     */
    public static void recordActivity(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                    .putLong(KEY_LAST_ACTIVE_TIME, System.currentTimeMillis())
                    .apply();
            Log.d(TAG, "Activity recorded at: " + System.currentTimeMillis());
        } catch (Exception e) {
            Log.e(TAG, "Error recording activity: " + e.getMessage());
        }
    }
    
    /**
     * Check if the session has timed out (> 5 minutes of inactivity).
     * Call this in onResume() to detect timeout.
     *
     * @param context Application or Activity context
     * @return true if session has expired, false if session is still active
     */
    public static boolean hasSessionExpired(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE);
            long lastActive = prefs.getLong(KEY_LAST_ACTIVE_TIME, 0);
            long currentTime = System.currentTimeMillis();
            
            // If lastActive == 0, app was never properly tracked (cold start) - no expiration
            if (lastActive == 0) {
                Log.d(TAG, "First session start detected");
                recordActivity(context);
                return false;
            }
            
            long inactiveTime = currentTime - lastActive;
            boolean expired = inactiveTime > FIVE_MINUTES_MS;
            
            if (expired) {
                Log.d(TAG, "Session expired after " + (inactiveTime / 1000) + " seconds of inactivity");
            }
            
            return expired;
        } catch (Exception e) {
            Log.e(TAG, "Error checking session timeout: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Clear the session timestamp (useful for logout or debugging).
     * After this, the next check will see lastActive == 0.
     *
     * @param context Application or Activity context
     */
    public static void clearSessionTimestamp(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                    .remove(KEY_LAST_ACTIVE_TIME)
                    .apply();
            Log.d(TAG, "Session timestamp cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing session timestamp: " + e.getMessage());
        }
    }
    
    /**
     * Get the last recorded activity time (for debugging/logging purposes).
     *
     * @param context Application or Activity context
     * @return Timestamp in milliseconds, or 0 if not found
     */
    public static long getLastActivityTime(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE);
            return prefs.getLong(KEY_LAST_ACTIVE_TIME, 0);
        } catch (Exception e) {
            Log.e(TAG, "Error getting last activity time: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get the inactivity duration in seconds (for debugging purposes).
     *
     * @param context Application or Activity context
     * @return Seconds of inactivity, or -1 if unable to determine
     */
    public static long getInactivityDuration(Context context) {
        try {
            long lastActive = getLastActivityTime(context);
            if (lastActive == 0) {
                return -1;
            }
            return (System.currentTimeMillis() - lastActive) / 1000;
        } catch (Exception e) {
            Log.e(TAG, "Error calculating inactivity duration: " + e.getMessage());
            return -1;
        }
    }
}
