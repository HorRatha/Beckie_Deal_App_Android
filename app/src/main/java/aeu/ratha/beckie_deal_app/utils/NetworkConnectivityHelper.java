package aeu.ratha.beckie_deal_app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

/**
 * Helper utility to check network connectivity status across the app.
 * This checks for both WiFi and mobile network availability.
 *
 * Usage:
 *   if (!NetworkConnectivityHelper.isNetworkAvailable(context)) {
 *       showNoInternetError();
 *   }
 */
public class NetworkConnectivityHelper {
    
    private static final String TAG = "NetworkConnectivity";
    
    /**
     * Check if the device has an active network connection.
     * Returns true only if network is available and connected.
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = 
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager == null) {
                Log.w(TAG, "ConnectivityManager is null");
                return false;
            }
            
            Network network = connectivityManager.getActiveNetwork();
            
            if (network == null) {
                Log.d(TAG, "No active network available");
                return false;
            }
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            
            if (capabilities == null) {
                Log.d(TAG, "Network capabilities are null");
                return false;
            }
            
            // Check if network has internet capability
            boolean hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            boolean isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            
            Log.d(TAG, "Network available: " + hasInternet + ", Validated: " + isValidated);
            
            return hasInternet && isValidated;
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking network availability: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Check if the device is connected to WiFi.
     */
    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = 
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager == null) return false;
            
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) return false;
            
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking WiFi connection: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Check if the device is connected to mobile network.
     */
    public static boolean isMobileConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = 
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            
            if (connectivityManager == null) return false;
            
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;
            
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) return false;
            
            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking mobile connection: " + e.getMessage(), e);
            return false;
        }
    }
}
