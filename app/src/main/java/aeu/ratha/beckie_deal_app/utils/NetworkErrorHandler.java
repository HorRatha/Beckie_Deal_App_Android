package aeu.ratha.beckie_deal_app.utils;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import aeu.ratha.beckie_deal_app.ui.dialogs.NoInternetDialogFragment;
import retrofit2.HttpException;

/**
 * Centralized error handler for network-related issues.
 * Handles API errors and network connectivity issues consistently across the app.
 *
 * Usage:
 *   if (!NetworkErrorHandler.checkNetworkBeforeRequest(context)) {
 *       return; // No network available
 *   }
 *
 * Or for Retrofit errors:
 *   if (NetworkErrorHandler.isNetworkError(throwable)) {
 *       NetworkErrorHandler.handleNetworkError(context, fragmentManager);
 *   }
 */
public class NetworkErrorHandler {
    
    private static final String TAG = "NetworkErrorHandler";
    
    /**
     * Check if network is available before making an API request.
     * If not available, shows the no-internet dialog.
     *
     * @return true if network is available, false otherwise
     */
    public static boolean checkNetworkBeforeRequest(Context context) {
        if (!NetworkConnectivityHelper.isNetworkAvailable(context)) {
            Log.w(TAG, "Network not available for API request");
            return false;
        }
        return true;
    }
    
    /**
     * Check if network is available before making an API request.
     * If not available, shows the no-internet dialog with retry callback.
     *
     * @return true if network is available, false otherwise
     */
    public static boolean checkNetworkBeforeRequest(
            Context context,
            FragmentManager fragmentManager,
            Runnable onRetry) {
        
        if (!NetworkConnectivityHelper.isNetworkAvailable(context)) {
            Log.w(TAG, "Network not available for API request");
            
            if (fragmentManager != null) {
                NoInternetDialogFragment.show(fragmentManager, onRetry);
            }
            
            return false;
        }
        return true;
    }
    
    /**
     * Check if a Throwable is a network-related error.
     */
    public static boolean isNetworkError(Throwable throwable) {
        if (throwable == null) return false;
        
        String message = throwable.getMessage() != null ? 
                throwable.getMessage().toLowerCase() : "";
        
        return message.contains("network") ||
               message.contains("no route") ||
               message.contains("connection refused") ||
               message.contains("timeout") ||
               message.contains("unreachable") ||
               message.contains("disconnect");
    }
    
    /**
     * Check if an error is a socket/connectivity exception.
     */
    public static boolean isConnectivityException(Throwable throwable) {
        if (throwable == null) return false;
        
        return throwable instanceof java.net.SocketException ||
               throwable instanceof java.net.SocketTimeoutException ||
               throwable instanceof java.net.UnknownHostException ||
               throwable instanceof java.io.EOFException ||
               throwable.getCause() instanceof java.net.SocketException;
    }
    
    /**
     * Get the HTTP status code from an HttpException.
     */
    public static int getHttpStatusCode(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return ((HttpException) throwable).code();
        }
        return -1;
    }
    
    /**
     * Check if the error is a 5xx server error.
     */
    public static boolean isServerError(Throwable throwable) {
        int code = getHttpStatusCode(throwable);
        return code >= 500;
    }
    
    /**
     * Check if the error is a 4xx client error.
     */
    public static boolean isClientError(Throwable throwable) {
        int code = getHttpStatusCode(throwable);
        return code >= 400 && code < 500;
    }
    
    /**
     * Handle a network error by showing the appropriate dialog.
     */
    public static void handleNetworkError(
            Context context,
            FragmentManager fragmentManager) {
        
        handleNetworkError(context, fragmentManager, () -> {});
    }
    
    /**
     * Handle a network error by showing the no-internet dialog with callback.
     */
    public static void handleNetworkError(
            Context context,
            FragmentManager fragmentManager,
            Runnable onRetry) {
        
        if (fragmentManager != null && !NetworkConnectivityHelper.isNetworkAvailable(context)) {
            Log.d(TAG, "Showing no-internet dialog");
            NoInternetDialogFragment.show(fragmentManager, onRetry);
        } else {
            Log.d(TAG, "Network error but no network dialog needed");
        }
    }
    
    /**
     * Log network error details for debugging.
     */
    public static void logNetworkError(String tag, Throwable error) {
        Log.e(tag, "Network error: " + error.getMessage(), error);
        
        if (isNetworkError(error)) {
            Log.d(tag, "Error type: Network connectivity issue");
        } else if (isConnectivityException(error)) {
            Log.d(tag, "Error type: Socket/connectivity exception");
        } else if (isServerError(error)) {
            Log.d(tag, "Error type: Server error (5xx)");
        } else if (isClientError(error)) {
            Log.d(tag, "Error type: Client error (4xx)");
        }
    }
}
