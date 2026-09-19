package aeu.ratha.beckie_deal_app.ui.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Promotion;
import aeu.ratha.beckie_deal_app.utils.helpers.CountdownTimer;

/**
 * RecyclerView adapter for displaying promotions as full-width banner cards.
 * Each banner shows: banner image, discount, name, code, and countdown timer.
 */
public class PromotionBannerAdapter extends RecyclerView.Adapter<PromotionBannerAdapter.PromotionBannerViewHolder> {
    
    private static final String TAG = "PromotionBannerAdapter";
    private final Context context;
    private final List<Promotion> promotions;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;
    
    public PromotionBannerAdapter(Context context, List<Promotion> promotions) {
        this.context = context;
        this.promotions = promotions;
    }
    
    @NonNull
    @Override
    public PromotionBannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_promotion_banner, parent, false);
        return new PromotionBannerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PromotionBannerViewHolder holder, int position) {
        Promotion promotion = promotions.get(position);
        holder.bind(promotion);
    }
    
    @Override
    public int getItemCount() {
        return promotions.size();
    }
    
    @Override
    public void onViewRecycled(@NonNull PromotionBannerViewHolder holder) {
        super.onViewRecycled(holder);
        holder.stopCountdownTimer();
    }
    
    /**
     * ViewHolder for a single promotion banner.
     */
    public class PromotionBannerViewHolder extends RecyclerView.ViewHolder {
        
        private final ImageView bannerImage;
        private final TextView discountText;
        private final TextView nameText;
        private final TextView descriptionText;
        private final TextView codeText;
        private final TextView countdownText;
        private final ImageView copyCodeIcon;
        private final LinearLayout copyCodePill;
        private Runnable countdownRunnable;
        private Handler countdownHandler = new Handler(Looper.getMainLooper());
        
        public PromotionBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            
            bannerImage = itemView.findViewById(R.id.promotionBannerImage);
            discountText = itemView.findViewById(R.id.promotionDiscount);
            nameText = itemView.findViewById(R.id.promotionName);
            descriptionText = itemView.findViewById(R.id.promotionDescription);
            codeText = itemView.findViewById(R.id.promotionCode);
            countdownText = itemView.findViewById(R.id.promotionCountdown);
            copyCodeIcon = itemView.findViewById(R.id.promotionCopyCodeIcon);
            
            // Find the copy code pill container (parent of icon)
            copyCodePill = (LinearLayout) copyCodeIcon.getParent();
        }
        
        /**
         * Bind promotion data to the banner view.
         */
        public void bind(Promotion promotion) {
            // Set text content
            discountText.setText(promotion.getDiscountDisplay());  // "30% OFF" or "$15 OFF"
            nameText.setText(promotion.getName());                 // "Summer Sale"
            
            // Set description if available
            if (promotion.getDescription() != null && !promotion.getDescription().isEmpty()) {
                descriptionText.setText(promotion.getDescription());
            } else {
                descriptionText.setText("Min. order $" + (int)promotion.getMinOrderAmount());
            }
            
            codeText.setText(promotion.getCode());                 // "SUMMER30"
            
            // Load banner image with fallback to gradient background
            if (promotion.getBannerImage() != null && !promotion.getBannerImage().isEmpty()) {
                Glide.with(context)
                        .load(promotion.getBannerImage())
                        .centerCrop()
                        .into(bannerImage);
                Log.d(TAG, "✓ Loaded banner image: " + promotion.getBannerImage());
            } else {
                // No image available - gradient fallback will show
                bannerImage.setImageDrawable(null);
                Log.d(TAG, "⚠ No banner image - using gradient fallback for: " + promotion.getCode());
            }
            
            // Set initial countdown
            updateCountdown(promotion);
            
            // Start countdown timer (updates every second)
            startCountdownTimer(promotion);
            
            // Click handler for copy code pill - copy code to clipboard
            copyCodePill.setOnClickListener(v -> {
                ClipboardManager clipboard = 
                    (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Promo Code", promotion.getCode());
                clipboard.setPrimaryClip(clip);
                
                Toast.makeText(context, 
                    "Code copied: " + promotion.getCode(), 
                    Toast.LENGTH_SHORT).show();
                
                Log.d(TAG, "✓ Copied code: " + promotion.getCode());
            });
        }
        
        /**
         * Update countdown timer display.
         */
        private void updateCountdown(Promotion promotion) {
            String countdownText = CountdownTimer.getCountdownText(promotion.getExpiresAt());
            this.countdownText.setText(countdownText);
        }
        
        /**
         * Start countdown timer that updates every second.
         */
        private void startCountdownTimer(Promotion promotion) {
            stopCountdownTimer(); // Stop any existing timer
            
            countdownRunnable = new Runnable() {
                @Override
                public void run() {
                    // Update countdown display
                    updateCountdown(promotion);
                    
                    // Check if expired
                    if (CountdownTimer.isExpired(promotion.getExpiresAt())) {
                        Log.d(TAG, "Promotion expired: " + promotion.getCode());
                        countdownHandler.removeCallbacks(this);
                        return;
                    }
                    
                    // Schedule next update (every second)
                    countdownHandler.postDelayed(this, 1000);
                }
            };
            
            // Start the countdown timer
            countdownHandler.post(countdownRunnable);
            Log.d(TAG, "✓ Countdown timer started for: " + promotion.getCode());
        }
        
        /**
         * Stop countdown timer.
         */
        private void stopCountdownTimer() {
            if (countdownRunnable != null) {
                countdownHandler.removeCallbacks(countdownRunnable);
                Log.d(TAG, "✓ Countdown timer stopped");
            }
        }
    }
}
