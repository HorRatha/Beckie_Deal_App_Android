package aeu.ratha.beckie_deal_app.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.utils.NetworkConnectivityHelper;

/**
 * Dialog shown when there's no internet connection.
 * This provides a consistent UX across the entire app.
 *
 * Features:
 * - Smooth fade-in/out animations
 * - Auto-retry with connectivity validation
 * - Loops smoothly if still offline
 * - Dismisses automatically when internet restored
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
    private MaterialButton retryButton;
    private View contentView;
    
    /**
     * Show the no internet dialog with callbacks.
     *
     * @param fragmentManager The fragment manager
     * @param onRetry Called when user clicks "Try Again" and network is restored
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
        
        contentView = view;
        retryButton = view.findViewById(R.id.retryButton);
        MaterialButton closeButton = view.findViewById(R.id.closeButton);
        
        retryButton.setOnClickListener(v -> handleRetryClick());
        
        closeButton.setOnClickListener(v -> {
            dismiss();
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
        });
    }
    
    /**
     * Handle retry button click with connectivity check.
     * If network is available, dismiss and retry.
     * If still offline, keep dialog open and loop with animation.
     */
    private void handleRetryClick() {
        // Disable retry button during check
        retryButton.setEnabled(false);
        
        // Check connectivity with a slight delay for UX smoothness
        contentView.postDelayed(() -> {
            if (NetworkConnectivityHelper.isNetworkAvailable(getContext())) {
                // Network restored - dismiss with smooth animation
                dismissWithAnimation();
                if (onRetryCallback != null) {
                    onRetryCallback.run();
                }
            } else {
                // Still no internet - loop with animation
                showRetryAnimation();
                retryButton.setEnabled(true);
            }
        }, 400);
    }
    
    /**
     * Show animation feedback for retry attempt.
     * Smooth fade-out and fade-in loop effect.
     */
    private void showRetryAnimation() {
        // Get fade animations
        android.view.animation.Animation fadeOut = AnimationUtils.loadAnimation(
                getContext(), 
                android.R.anim.fade_out
        );
        android.view.animation.Animation fadeIn = AnimationUtils.loadAnimation(
                getContext(), 
                android.R.anim.fade_in
        );
        
        // Set animation durations for smooth effect
        fadeOut.setDuration(300);
        fadeIn.setDuration(300);
        
        // On fade out complete, fade back in
        fadeOut.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {}
            
            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                // Start fade in
                if (isAdded()) {
                    contentView.startAnimation(fadeIn);
                }
            }
            
            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}
        });
        
        // Start the loop
        contentView.startAnimation(fadeOut);
    }
    
    /**
     * Dismiss dialog with smooth fade-out animation.
     */
    private void dismissWithAnimation() {
        android.view.animation.Animation fadeOut = AnimationUtils.loadAnimation(
                getContext(), 
                android.R.anim.fade_out
        );
        fadeOut.setDuration(300);
        
        fadeOut.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {}
            
            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                if (isAdded()) {
                    dismiss();
                }
            }
            
            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {}
        });
        
        contentView.startAnimation(fadeOut);
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
            
            // Apply blur effect to background
            window.setDimAmount(0.3f);
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        
        // Show dialog with smooth fade-in animation
        android.view.animation.Animation fadeIn = AnimationUtils.loadAnimation(
                getContext(), 
                android.R.anim.fade_in
        );
        fadeIn.setDuration(400);
        contentView.startAnimation(fadeIn);
    }
}
