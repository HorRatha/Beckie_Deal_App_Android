package aeu.ratha.beckie_deal_app.utils.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import aeu.ratha.beckie_deal_app.ui.dialogs.NoInternetDialogFragment;
import aeu.ratha.beckie_deal_app.utils.NetworkConnectivityHelper;

/**
 * Base fragment for fragments that need to monitor network connectivity.
 * Automatically shows no-internet dialog when connection is lost.
 *
 * Subclasses can override:
 * - onNetworkLost() - called when network is disconnected
 * - onNetworkRestored() - called when network is reconnected
 */
public abstract class BaseFragment extends Fragment {
    
    private static final String TAG = "BaseFragment";
    
    private BroadcastReceiver networkChangeReceiver;
    private boolean isNetworkDialogShown = false;
    private boolean wasNetworkAvailable = true;
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Setup network monitoring when fragment view is created
        setupNetworkConnectivityMonitor();
    }
    
    /**
     * Setup network connectivity broadcast receiver to monitor connection changes.
     */
    private void setupNetworkConnectivityMonitor() {
        try {
            if (getContext() == null) return;
            
            networkChangeReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    checkNetworkConnectivity();
                }
            };
            
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            getContext().registerReceiver(networkChangeReceiver, filter, Context.RECEIVER_EXPORTED);
            
            // Initial check
            wasNetworkAvailable = NetworkConnectivityHelper.isNetworkAvailable(getContext());
            
        } catch (Exception e) {
            Log.e(TAG, "Error setting up network monitor: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if network connectivity has changed and handle accordingly.
     */
    private void checkNetworkConnectivity() {
        try {
            Context context = getContext();
            if (context == null) return;
            
            boolean isNetworkNowAvailable = NetworkConnectivityHelper.isNetworkAvailable(context);
            
            if (!isNetworkNowAvailable && wasNetworkAvailable) {
                // Network just lost
                Log.d(TAG, "Network connection lost in fragment");
                onNetworkLost();
                
            } else if (isNetworkNowAvailable && !wasNetworkAvailable) {
                // Network just restored
                Log.d(TAG, "Network connection restored in fragment");
                isNetworkDialogShown = false;
                onNetworkRestored();
            }
            
            wasNetworkAvailable = isNetworkNowAvailable;
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking network connectivity: " + e.getMessage(), e);
        }
    }
    
    /**
     * Called when network connection is lost.
     * Shows the no-internet dialog by default.
     * Subclasses can override to customize behavior.
     */
    protected void onNetworkLost() {
        if (!isNetworkDialogShown && getFragmentManager() != null) {
            isNetworkDialogShown = true;
            NoInternetDialogFragment.show(
                    getFragmentManager(),
                    this::checkNetworkConnectivity,  // Retry
                    () -> {
                        // On close, just dismiss
                    }
            );
        }
    }
    
    /**
     * Called when network connection is restored.
     * Subclasses can override to refresh data or retry failed operations.
     */
    protected void onNetworkRestored() {
        Log.d(TAG, "Network restored - subclasses can override this to refresh data");
    }
    
    /**
     * Utility method to check if network is available before making API calls.
     */
    protected boolean ensureNetworkAvailable() {
        Context context = getContext();
        if (context == null) {
            Log.w(TAG, "Context is null");
            return false;
        }
        
        if (!NetworkConnectivityHelper.isNetworkAvailable(context)) {
            onNetworkLost();
            return false;
        }
        
        return true;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Unregister network receiver to prevent memory leaks
        try {
            Context context = getContext();
            if (context != null && networkChangeReceiver != null) {
                context.unregisterReceiver(networkChangeReceiver);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering network receiver: " + e.getMessage(), e);
        }
    }
}
