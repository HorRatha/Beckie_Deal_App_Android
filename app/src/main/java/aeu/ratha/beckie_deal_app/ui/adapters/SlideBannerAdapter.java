package aeu.ratha.beckie_deal_app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import aeu.ratha.beckie_deal_app.R;
import aeu.ratha.beckie_deal_app.data.models.Slide;

/**
 * RecyclerView adapter for displaying promotion slides as full-width banner cards.
 * Handles carousel images, transitions, and gradient backgrounds.
 * 
 * Features:
 * - Responsive image loading (desktop/mobile)
 * - Gradient background support
 * - Animation transitions (fade/cut)
 * - CTA button with deep linking
 * - Optional carousel with multiple frames
 */
public class SlideBannerAdapter extends RecyclerView.Adapter<SlideBannerAdapter.SlideBannerViewHolder> {
    
    private static final String TAG = "SlideBannerAdapter";
    private final Context context;
    private final List<Slide> slides;
    
    public SlideBannerAdapter(Context context, List<Slide> slides) {
        this.context = context;
        this.slides = slides;
    }
    
    @NonNull
    @Override
    public SlideBannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_slide_banner, parent, false);
        return new SlideBannerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SlideBannerViewHolder holder, int position) {
        Slide slide = slides.get(position);
        holder.bind(slide);
    }
    
    @Override
    public int getItemCount() {
        return slides.size();
    }
    
    /**
     * ViewHolder for a single slide banner.
     */
    public class SlideBannerViewHolder extends RecyclerView.ViewHolder {
        
        private final ImageView bannerImage;
        private final TextView titleText;
        private final TextView subtitleText;
        private final Button ctaButton;
        private final View gradientOverlay;
        private int currentFrameIndex = 0;
        private Runnable carouselRunnable;
        private final android.os.Handler carouselHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        
        public SlideBannerViewHolder(@NonNull View itemView) {
            super(itemView);
            
            bannerImage = itemView.findViewById(R.id.slideBannerImage);
            titleText = itemView.findViewById(R.id.slideTitle);
            subtitleText = itemView.findViewById(R.id.slideSubtitle);
            ctaButton = itemView.findViewById(R.id.slideCtaButton);
            gradientOverlay = itemView.findViewById(R.id.slideGradientOverlay);
        }
        
        /**
         * Bind slide data to the banner view.
         */
        public void bind(Slide slide) {
            if (slide == null) {
                Log.e(TAG, "❌ Slide is null!");
                return;
            }
            
            try {
                Log.d(TAG, "Binding slide: " + slide.getTitle() + " (ID: " + slide.getId() + ")");
                
                // Set text content with null checks
                if (titleText != null && slide.getTitle() != null) {
                    titleText.setText(slide.getTitle());
                }
                if (subtitleText != null && slide.getSubtitle() != null) {
                    subtitleText.setText(slide.getSubtitle());
                }
                
                // Set CTA button
                if (ctaButton != null && slide.getButtonText() != null) {
                    ctaButton.setText(slide.getButtonText());
                    ctaButton.setOnClickListener(v -> handleCtaClick(slide));
                }
                
                // Load primary image
                String primaryImage = slide.getPrimaryImage();
                if (bannerImage != null) {
                    if (primaryImage != null && !primaryImage.isEmpty()) {
                        try {
                            Glide.with(context)
                                    .load(primaryImage)
                                    .centerCrop()
                                    .into(bannerImage);
                            Log.d(TAG, "✓ Loaded slide image: " + primaryImage);
                        } catch (Exception e) {
                            Log.e(TAG, "⚠ Glide load error: " + e.getMessage());
                            bannerImage.setImageDrawable(null);
                        }
                    } else {
                        bannerImage.setImageDrawable(null);
                        Log.d(TAG, "⚠ No image for slide: " + slide.getTitle());
                    }
                }
                
                // Apply gradient overlay if available
                applyGradient(slide.getGradient());
                
                // Start carousel if slide has multiple images
                if (slide.hasCarousel()) {
                    startCarousel(slide);
                    Log.d(TAG, "✓ Carousel started - " + slide.getCarouselImages().size() + " frames");
                } else {
                    stopCarousel();
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error binding slide: " + e.getMessage(), e);
            }
        }
        
        /**
         * Apply gradient background overlay.
         */
        private void applyGradient(String gradientCss) {
            if (gradientCss == null || gradientCss.isEmpty()) {
                gradientOverlay.setBackground(null);
                return;
            }
            
            try {
                // Parse CSS gradient like "linear-gradient(135deg, #1b2a4a 0%, #6d28d9 100%)"
                // Extract colors and angle
                if (gradientCss.contains("linear-gradient")) {
                    // Extract angle and colors
                    String[] parts = gradientCss.split(",");
                    
                    if (parts.length >= 3) {
                        // Parse angle (e.g., "135deg")
                        int angle = parseAngle(parts[0]);
                        
                        // Parse first color (e.g., "#1b2a4a 0%")
                        int color1 = parseColor(parts[1]);
                        
                        // Parse second color (e.g., "#6d28d9 100%")
                        int color2 = parseColor(parts[2]);
                        
                        // Create gradient drawable
                        GradientDrawable gradient = new GradientDrawable();
                        gradient.setColors(new int[]{color1, color2});
                        
                        // Set orientation based on angle
                        gradient.setOrientation(getGradientOrientation(angle));
                        
                        gradientOverlay.setBackground(gradient);
                        Log.d(TAG, "✓ Applied gradient - angle: " + angle + ", colors: " + 
                              String.format("%06X", color1) + " → " + String.format("%06X", color2));
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "✗ Failed to parse gradient: " + e.getMessage());
            }
        }
        
        /**
         * Parse angle from gradient CSS (e.g., "135deg" → 135)
         */
        private int parseAngle(String anglePart) {
            try {
                String cleaned = anglePart.replaceAll("[^0-9]", "");
                return Integer.parseInt(cleaned);
            } catch (Exception e) {
                return 135; // Default 135 degrees
            }
        }
        
        /**
         * Parse hex color from CSS (e.g., "#1b2a4a" or "#1b2a4a 0%")
         */
        private int parseColor(String colorPart) {
            try {
                // Extract hex color (format: #RRGGBB)
                String hex = colorPart.trim();
                int percentIndex = hex.indexOf(' ');
                if (percentIndex > 0) {
                    hex = hex.substring(0, percentIndex).trim();
                }
                
                // Remove # if present
                if (hex.startsWith("#")) {
                    hex = hex.substring(1);
                }
                
                // Pad to 6 characters if needed
                if (hex.length() == 3) {
                    hex = "" + hex.charAt(0) + hex.charAt(0) + 
                          hex.charAt(1) + hex.charAt(1) + 
                          hex.charAt(2) + hex.charAt(2);
                }
                
                return (int) Long.parseLong(hex, 16) | 0xFF000000; // Add alpha channel
            } catch (Exception e) {
                Log.e(TAG, "✗ Failed to parse color: " + colorPart);
                return 0xFF000000; // Default black
            }
        }
        
        /**
         * Get GradientDrawable orientation from angle.
         */
        private GradientDrawable.Orientation getGradientOrientation(int angle) {
            // Normalize angle to 0-360
            angle = ((angle % 360) + 360) % 360;
            
            if (angle >= 45 && angle < 135) {
                return GradientDrawable.Orientation.BR_TL; // 90 degrees
            } else if (angle >= 135 && angle < 225) {
                return GradientDrawable.Orientation.BL_TR; // 180 degrees
            } else if (angle >= 225 && angle < 315) {
                return GradientDrawable.Orientation.TL_BR; // 270 degrees
            } else {
                return GradientDrawable.Orientation.TR_BL; // 0/360 degrees
            }
        }
        
        /**
         * Start carousel animation cycling through images.
         */
        private void startCarousel(Slide slide) {
            stopCarousel(); // Stop any existing carousel
            
            List<String> carouselImages = slide.getCarouselImages();
            if (carouselImages == null || carouselImages.isEmpty()) {
                return;
            }
            
            final int durationMs = slide.getDurationMs() > 0 ? slide.getDurationMs() : 3000;
            
            currentFrameIndex = 0;
            
            carouselRunnable = new Runnable() {
                @Override
                public void run() {
                    // Load current frame
                    if (currentFrameIndex < carouselImages.size()) {
                        String frameUrl = carouselImages.get(currentFrameIndex);
                        
                        // Apply transition effect (fade/cut)
                        if ("fade".equalsIgnoreCase(slide.getTransition())) {
                            bannerImage.setAlpha(0f);
                            bannerImage.animate().alpha(1f).setDuration(500).start();
                        }
                        
                        Glide.with(context)
                                .load(frameUrl)
                                .centerCrop()
                                .into(bannerImage);
                        
                        Log.d(TAG, "Carousel frame " + (currentFrameIndex + 1) + "/" + carouselImages.size());
                    }
                    
                    // Move to next frame (loop)
                    currentFrameIndex = (currentFrameIndex + 1) % carouselImages.size();
                    
                    // Schedule next frame
                    carouselHandler.postDelayed(this, durationMs);
                }
            };
            
            // Start carousel immediately
            carouselHandler.post(carouselRunnable);
        }
        
        /**
         * Stop carousel animation.
         */
        private void stopCarousel() {
            if (carouselRunnable != null) {
                carouselHandler.removeCallbacks(carouselRunnable);
                carouselRunnable = null;
            }
        }
        
        /**
         * Handle CTA button click - navigate to button URL.
         */
        private void handleCtaClick(Slide slide) {
            String buttonUrl = slide.getButtonUrl();
            if (buttonUrl == null || buttonUrl.isEmpty()) {
                Log.w(TAG, "No button URL for slide: " + slide.getTitle());
                return;
            }
            
            Log.d(TAG, "Navigating to: " + buttonUrl);
            
            // Handle internal routes vs external URLs
            if (buttonUrl.startsWith("/")) {
                // Internal route - could be handled by navigation
                // For now, we'll treat it as a web URL and prepend the base URL
                // In production, you might use Android's Navigation component
                Log.d(TAG, "Internal route detected: " + buttonUrl);
                // Example: navigate to /categories/graphics-cards
                // This could be expanded to use custom URL schemes or Navigation graphs
            } else {
                // External URL - open in browser
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(buttonUrl));
                context.startActivity(intent);
            }
        }
        
        /**
         * Cleanup when view is recycled.
         */
        public void cleanup() {
            stopCarousel();
        }
    }
    
    @Override
    public void onViewRecycled(@NonNull SlideBannerViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanup();
    }
}
