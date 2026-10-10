package aeu.ratha.beckie_deal_app.data.api;

import aeu.ratha.beckie_deal_app.data.models.SlideResponse;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit API service interface for slides/carousel promotions endpoints.
 * Base URL: /api/v1
 *
 * STATUS: ✅ READY
 * 
 * The backend provides homepage carousel/banner promotions.
 * This endpoint returns rich animation data for promotional slides.
 */
public interface SlideApiService {
    
    /**
     * Get all active promotion slides for homepage carousel.
     * GET /api/v1/slides
     * 
     * Returns a list of slides with animation metadata, images, and call-to-action buttons.
     * Each slide can contain:
     * - Single image or carousel of images (meta.frames)
     * - Animation settings (transition, duration, gradient)
     * - CTA button with URL
     * - Optional date range (starts_at, ends_at)
     * 
     * @return Call object with SlideResponse containing list of slides
     */
    @GET("slides")
    Call<SlideResponse> getSlides();
}
