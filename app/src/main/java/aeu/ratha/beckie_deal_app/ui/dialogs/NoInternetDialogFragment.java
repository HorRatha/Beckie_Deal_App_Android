package aeu.ratha.beckie_deal_app.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import aeu.ratha.beckie_deal_app.R;

/**
 * Dialog shown when there's no internet connection.
 * This provides a consistent UX across the entire app.
 *
 * Usage:
 *   NoInternetDialogFragment.show(getSupportFragmentManager(),
 *       () -> { // On retry
 *           // Retry the operation
 *       },
 *       () -> { // On close
 *           // Close the dialog or navigate back
 *       }
 *   );
 */
public class NoInternetDialogFragment extends DialogFragment {
    
    public static final String TAG = "NoInternetDialog";
    
    private Runnable onRetryCallback;
    private Runnable onCloseCallback;
    
    /**
     * Show the no internet dialog with callbacks.
     *
     * @param fragmentManager The fragment manager
     * @param onRetry Called when user clicks "Try Again"
     * @param onClose Called when user clicks "Go Back"
     */
    public static void show(
            @NonNull androidx.fragment.app.FragmentManager fragmentManager,
            @NonNull Runnable onRetry,
            @NonNull Runnable onClose) {
        
        NoInternetDialogFragment dialog = new NoInternetDialogFragment();
        dialog.setCallbacks(onRetry, onClose);
        dialog.show(fragmentManager, TAG);
    }
    
    /**
     * Show with a default close callback (just dismiss the dialog).
     */
    public static void show(
            @NonNull androidx.fragment.app.FragmentManager fragmentManager,
            @NonNull Runnable onRetry) {
        
        show(fragmentManager, onRetry, () -> {});
    }
    
    /**
     * Set the callbacks for this dialog.
     */
    private void setCallbacks(Runnable onRetry, Runnable onClose) {
        this.onRetryCallback = onRetry;
        this.onCloseCallback = onClose;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppAlertDialog);
    }
    
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.dialog_no_internet, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        MaterialButton retryButton = view.findViewById(R.id.retryButton);
        MaterialButton closeButton = view.findViewById(R.id.closeButton);
        
        retryButton.setOnClickListener(v -> {
            dismiss();
            if (onRetryCallback != null) {
                onRetryCallback.run();
            }
        });
        
        closeButton.setOnClickListener(v -> {
            dismiss();
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
        });
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        // Configure dialog dimensions and styling
        if (getDialog() != null && getDialog().getWindow() != null) {
            android.view.Window window = getDialog().getWindow();
            
            // Set dialog size
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            
            // Set background to transparent for rounded corners
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
