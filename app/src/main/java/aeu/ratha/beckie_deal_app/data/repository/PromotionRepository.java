package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.data.api.PromotionApiService;
import aeu.ratha.beckie_deal_app.data.models.Promotion;
import aeu.ratha.beckie_deal_app.data.models.PromotionResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for promotions/coupons data operations.
 * Fetches active promotions from the backend to display on home screen.
 */
public class PromotionRepository {
    
    private static final String TAG = "PromotionRepository";
    private final PromotionApiService promotionApiService;
    private List<Promotion> cachedPromotions = new ArrayList<>();
    
    public PromotionRepository(Context context) {
        this.promotionApiService = RetrofitClient.getClient(context).create(PromotionApiService.class);
    }
    
    /**
     * Fetch all active promotions from backend.
     * 
     * @param callback Callback with list of promotions or error
     */
    public void getPromotions(final PromotionCallback callback) {
        // Use the no-parameter version to avoid query string issues
        Log.d(TAG, "=== PROMOTION API CALL START ===");
        promotionApiService.getPromotions().enqueue(new Callback<PromotionResponse>() {
            @Override
            public void onResponse(Call<PromotionResponse> call, Response<PromotionResponse> response) {
                Log.d(TAG, "📡 API Response received - HTTP Code: " + response.code() + ", Message: " + response.message());
                
                if (response.isSuccessful() && response.body() != null) {
                    PromotionResponse body = response.body();
                    Log.d(TAG, "✓ Response successful - Status: " + body.getStatus() + ", Data count: " + body.getCount());
                    
                    if (body.isSuccess() && body.getData() != null) {
                        cachedPromotions.clear();
                        cachedPromotions.addAll(body.getData());
                        
                        Log.d(TAG, "✓✓✓ SUCCESS: Loaded " + body.getCount() + " promotions");
                        for (int i = 0; i < body.getData().size(); i++) {
                            Promotion p = body.getData().get(i);
                            Log.d(TAG, "  [" + i + "] " + p.getCode() + " - " + p.getName() + " (" + p.getDiscountDisplay() + ") expires: " + p.getExpiresAt());
                        }
                        callback.onSuccess(body.getData());
                    } else {
                        Log.e(TAG, "✗ API returned non-success status: " + body.getStatus());
                        Log.e(TAG, "  Message: " + body.getMessage());
                        Log.e(TAG, "  Data: " + (body.getData() != null ? body.getData().size() + " items" : "NULL"));
                        // Fallback to mock data
                        useMockPromotions(callback);
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
                    // Fallback to mock data
                    useMockPromotions(callback);
                }
            }
            
            @Override
            public void onFailure(Call<PromotionResponse> call, Throwable t) {
                Log.e(TAG, "✗✗✗ NETWORK FAILURE: " + t.getClass().getSimpleName() + " - " + t.getMessage(), t);
                Log.e(TAG, "=== PROMOTION API CALL FAILED ===");
                t.printStackTrace();
                // Fallback to mock data
                useMockPromotions(callback);
            }
        });
    }
    
    /**
     * Load mock promotion data as fallback when API fails or returns no data.
     * This allows the banner to display while backend is being implemented.
     */
    private void useMockPromotions(PromotionCallback callback) {
        Log.d(TAG, "📦 Using MOCK PROMOTION DATA (backend endpoint not yet implemented)");
        
        java.util.List<Promotion> mockPromotions = new java.util.ArrayList<>();
        
        // Mock Promotion 1: Summer Sale
        Promotion promo1 = new Promotion();
        promo1.setId(1);
        promo1.setName("Summer Sale");
        promo1.setCode("SUMMER30");
        promo1.setType("percentage");
        promo1.setValue(30);
        promo1.setDescription("Get 30% off on summer collection");
        promo1.setBannerImage("https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&h=200&fit=crop");
        promo1.setMinOrderAmount(150);
        promo1.setExpiresAt("2026-12-31T23:59:59+00:00");
        mockPromotions.add(promo1);
        
        // Mock Promotion 2: New Arrival Discount
        Promotion promo2 = new Promotion();
        promo2.setId(2);
        promo2.setName("New Arrivals");
        promo2.setCode("NEWARR20");
        promo2.setType("percentage");
        promo2.setValue(20);
        promo2.setDescription("20% off on new products");
        promo2.setBannerImage("https://images.unsplash.com/photo-1505228395891-9a51e7e86e81?w=800&h=200&fit=crop");
        promo2.setMinOrderAmount(100);
        promo2.setExpiresAt("2026-11-30T23:59:59+00:00");
        mockPromotions.add(promo2);
        
        // Mock Promotion 3: Flash Sale
        Promotion promo3 = new Promotion();
        promo3.setId(3);
        promo3.setName("Flash Sale");
        promo3.setCode("FLASH15");
        promo3.setType("fixed");
        promo3.setValue(15);
        promo3.setDescription("$15 off on orders over $100");
        promo3.setBannerImage("https://images.unsplash.com/photo-1556740738-b6a63e27c4df?w=800&h=200&fit=crop");
        promo3.setMinOrderAmount(100);
        promo3.setExpiresAt("2026-10-31T23:59:59+00:00");
        mockPromotions.add(promo3);
        
        Log.d(TAG, "✓ Created " + mockPromotions.size() + " mock promotions");
        callback.onSuccess(mockPromotions);
    }
    
    /**
     * Get cached promotions (from last successful fetch).
     */
    public List<Promotion> getCachedPromotions() {
        return new ArrayList<>(cachedPromotions);
    }
    
    /**
     * Callback interface for promotion operations.
     */
    public interface PromotionCallback {
        void onSuccess(List<Promotion> promotions);
        void onError(String errorMessage);
    }
}
