package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Slide/Banner promotion model from /api/v1/slides endpoint.
 * This represents carousel/banner promotions displayed on the homepage.
 */
public class Slide {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("title")
    private String title;
    
    @SerializedName("subtitle")
    private String subtitle;
    
    @SerializedName("button_text")
    private String buttonText;
    
    @SerializedName("button_url")
    private String buttonUrl;
    
    @SerializedName("image_desktop")
    private String imageDesktop;
    
    @SerializedName("image_mobile")
    private String imageMobile;
    
    @SerializedName("images")
    private List<String> images;  // Array of image URLs for carousel
    
    @SerializedName("duration_ms")
    private int durationMs;  // Slide display duration in milliseconds
    
    @SerializedName("transition")
    private String transition;  // e.g. "fade", "cut"
    
    @SerializedName("gradient")
    private String gradient;  // Background gradient CSS
    
    @SerializedName("meta")
    private SlideMeta meta;  // Animation metadata
    
    @SerializedName("position")
    private String position;  // e.g. "homepage"
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("starts_at")
    private String startsAt;  // ISO 8601 date (nullable)
    
    @SerializedName("ends_at")
    private String endsAt;  // ISO 8601 date (nullable)
    
    public Slide() {}
    
    // ── Getters ────────────────────────────────────────────────────────────
    
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getButtonText() { return buttonText; }
    public String getButtonUrl() { return buttonUrl; }
    public String getImageDesktop() { return imageDesktop; }
    public String getImageMobile() { return imageMobile; }
    public List<String> getImages() { return images; }
    public int getDurationMs() { return durationMs; }
    public String getTransition() { return transition; }
    public String getGradient() { return gradient; }
    public SlideMeta getMeta() { return meta; }
    public String getPosition() { return position; }
    public boolean isActive() { return isActive; }
    public String getStartsAt() { return startsAt; }
    public String getEndsAt() { return endsAt; }
    
    // ── Setters ────────────────────────────────────────────────────────────
    
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public void setButtonText(String buttonText) { this.buttonText = buttonText; }
    public void setButtonUrl(String buttonUrl) { this.buttonUrl = buttonUrl; }
    public void setImageDesktop(String imageDesktop) { this.imageDesktop = imageDesktop; }
    public void setImageMobile(String imageMobile) { this.imageMobile = imageMobile; }
    public void setImages(List<String> images) { this.images = images; }
    public void setDurationMs(int durationMs) { this.durationMs = durationMs; }
    public void setTransition(String transition) { this.transition = transition; }
    public void setGradient(String gradient) { this.gradient = gradient; }
    public void setPosition(String position) { this.position = position; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
    public void setStartsAt(String startsAt) { this.startsAt = startsAt; }
    public void setEndsAt(String endsAt) { this.endsAt = endsAt; }
    
    // ── Helpers ────────────────────────────────────────────────────────────
    
    /**
     * Get the primary image to display (prefer desktop for now)
     */
    public String getPrimaryImage() {
        if (imageDesktop != null && !imageDesktop.isEmpty()) {
            return imageDesktop;
        }
        if (imageMobile != null && !imageMobile.isEmpty()) {
            return imageMobile;
        }
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }
    
    /**
     * Get carousel images (from meta frames if available, otherwise from images array)
     */
    public List<String> getCarouselImages() {
        if (meta != null && meta.getFrames() != null && !meta.getFrames().isEmpty()) {
            return meta.getFrames();
        }
        return images;
    }
    
    /**
     * Check if this slide has multiple images for carousel
     */
    public boolean hasCarousel() {
        List<String> carouselImages = getCarouselImages();
        return carouselImages != null && carouselImages.size() > 1;
    }
    
    @Override
    public String toString() {
        return "Slide{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", transition='" + transition + '\'' +
                ", isActive=" + isActive +
                ", durationMs=" + durationMs +
                '}';
    }
    
    // ── Inner class for animation metadata ──────────────────────────────────
    
    /**
     * Animation metadata for carousel/frame animations
     */
    public static class SlideMeta {
        @SerializedName("frames")
        private List<String> frames;  // Array of frame images
        
        @SerializedName("durationMs")
        private int durationMs;
        
        @SerializedName("transition")
        private String transition;
        
        @SerializedName("gradient")
        private String gradient;
        
        public SlideMeta() {}
        
        public List<String> getFrames() { return frames; }
        public int getDurationMs() { return durationMs; }
        public String getTransition() { return transition; }
        public String getGradient() { return gradient; }
        
        public void setFrames(List<String> frames) { this.frames = frames; }
        public void setDurationMs(int durationMs) { this.durationMs = durationMs; }
        public void setTransition(String transition) { this.transition = transition; }
        public void setGradient(String gradient) { this.gradient = gradient; }
    }
}
