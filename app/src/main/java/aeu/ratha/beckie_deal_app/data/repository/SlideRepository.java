package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.data.api.SlideApiService;
import aeu.ratha.beckie_deal_app.data.models.Slide;
import aeu.ratha.beckie_deal_app.data.models.SlideResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for slides/carousel promotions data operations.
 * Fetches promotional banner slides from /api/v1/slides endpoint.
 */
public class SlideRepository {
    
    private static final String TAG = "SlideRepository";
    private final SlideApiService slideApiService;
    private List<Slide> cachedSlides = new ArrayList<>();
    
    public SlideRepository(Context context) {
        this.slideApiService = RetrofitClient.getClient(context).create(SlideApiService.class);
    }
    
    /**
     * Fetch all active promotion slides from backend.
     * 
     * @param callback Callback with list of slides or error
     */
    public void getSlides(final SlideCallback callback) {
        Log.d(TAG, "=== SLIDE API CALL START ===");
        slideApiService.getSlides().enqueue(new Callback<SlideResponse>() {
            @Override
            public void onResponse(Call<SlideResponse> call, Response<SlideResponse> response) {
                Log.d(TAG, "📡 API Response received - HTTP Code: " + response.code() + ", Message: " + response.message());
                
                if (response.isSuccessful() && response.body() != null) {
                    SlideResponse body = response.body();
                    Log.d(TAG, "✓ Response successful - Status: " + body.getStatus() + ", Data count: " + body.getCount());
                    
                    if (body.isSuccess() && body.getData() != null) {
                        cachedSlides.clear();
                        cachedSlides.addAll(body.getData());
                        
                        Log.d(TAG, "✓✓✓ SUCCESS: Loaded " + body.getCount() + " slides");
                        for (int i = 0; i < body.getData().size(); i++) {
                            Slide s = body.getData().get(i);
                            boolean hasCarousel = s.hasCarousel();
                            Log.d(TAG, "  [" + i + "] " + s.getTitle() + 
                                    " (active=" + s.isActive() + 
                                    ", carousel=" + hasCarousel + 
                                    ", duration=" + s.getDurationMs() + "ms)");
                        }
                        callback.onSuccess(body.getData());
                    } else {
                        Log.e(TAG, "✗ API returned non-success status: " + body.getStatus());
                        Log.e(TAG, "  Message: " + body.getMessage());
                        Log.e(TAG, "  Data: " + (body.getData() != null ? body.getData().size() + " items" : "NULL"));
                        callback.onError("API returned non-success status: " + body.getStatus());
                    }
                } else {
                    String bodyContent = "NULL";
                    try {
                        bodyContent = response.errorBody() != null ? response.errorBody().string() : "NULL";
                    } catch (Exception e) {
                        bodyContent = "Error reading body";
                    }
                    Log.e(TAG, "✗ HTTP Error " + response.code() + ": " + response.message());
                    Log.e(TAG, "  Error body: " + bodyContent);
                    callback.onError("HTTP Error " + response.code() + ": " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<SlideResponse> call, Throwable t) {
                Log.e(TAG, "✗✗✗ NETWORK FAILURE: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);
                Log.e(TAG, "=== SLIDE API CALL FAILED ===");
                t.printStackTrace();
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get cached slides (from last successful fetch).
     */
    public List<Slide> getCachedSlides() {
        return new ArrayList<>(cachedSlides);
    }
    
    /**
     * Callback interface for slide operations.
     */
    public interface SlideCallback {
        void onSuccess(List<Slide> slides);
        void onError(String errorMessage);
    }
}
