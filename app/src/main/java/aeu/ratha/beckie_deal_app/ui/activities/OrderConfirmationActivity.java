package aeu.ratha.beckie_deal_app.ui.activities;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

/**
 * Order Confirmation Activity
 * Displays order confirmation with options to track or continue shopping
 */
public class OrderConfirmationActivity extends AppCompatActivity {
    
    private static final String TAG = "OrderConfirmationActivity";
    
    private TextView textOrderNumber;
    private TextView textConfirmationMessage;
    private TextView textOrderStatus;
    private ProgressBar progressBar;
    private Button btnTrackOrder;
    private Button btnContinueShopping;
    
    private long orderId;
    private SoundPool soundPool;
    private int soundId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        
        // Extract order ID
        orderId = getIntent().getLongExtra("order_id", 0);
        
        // Initialize views
        initializeViews();
        displayOrderConfirmation();
        setupClickListeners();
        
        // Play success sound
        playSuccessSound();
    }
    
    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        textOrderNumber = findViewById(R.id.text_order_number);
        textConfirmationMessage = findViewById(R.id.text_confirmation_message);
        textOrderStatus = findViewById(R.id.text_order_status);
        progressBar = findViewById(R.id.progress_loading);
        btnTrackOrder = findViewById(R.id.btn_track_order);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);
    }
    
    /**
     * Display order confirmation
     */
    private void displayOrderConfirmation() {
        showProgress(false);
        textOrderNumber.setText("Order #" + orderId);
        textConfirmationMessage.setText("Thank You!");
        
        double totalAmount = getIntent().getDoubleExtra("total_amount", 0.0);
        String statusText = getIntent().getStringExtra("status");
        if (statusText == null || statusText.isEmpty()) statusText = "PROCESSING";
        
        StringBuilder status = new StringBuilder();
        status.append("Status: ").append(statusText).append("\n");
        if (totalAmount > 0) {
            status.append("Amount: ").append(String.format(java.util.Locale.US, "$%.2f", totalAmount)).append("\n");
        }
        status.append("Date: Today\n");
        status.append("Expected Delivery: Soon");
        
        textOrderStatus.setText(status.toString());
    }
    
    /**
     * Set up click listeners
     */
    private void setupClickListeners() {
        btnTrackOrder.setOnClickListener(v -> trackOrder());
        btnContinueShopping.setOnClickListener(v -> continueShopping());
    }
    
    /**
     * Track order
     */
    private void trackOrder() {
        // TODO: Navigate to order tracking page
    }
    
    /**
     * Continue shopping
     */
    private void continueShopping() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    /**
     * Show/hide loading
     */
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }
    
    /**
     * Play success sound using SoundPool
     */
    private void playSuccessSound() {
        try {
            // Create AudioAttributes for the sound
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            
            // Create SoundPool
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
            
            // Load the success sound
            soundId = soundPool.load(this, R.raw.google_pay_success, 1);
            
            // Set listener for when sound is loaded
            soundPool.setOnLoadCompleteListener((sp, sampleId, status) -> {
                if (status == 0) {
                    // Sound loaded successfully, play it
                    soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up SoundPool
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
    
    @Override
    public void onBackPressed() {
        // Just navigate to home instead of closing
        continueShopping();
    }
}
