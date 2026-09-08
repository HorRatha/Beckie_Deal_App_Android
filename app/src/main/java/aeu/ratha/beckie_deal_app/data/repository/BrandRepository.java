package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import aeu.ratha.beckie_deal_app.data.api.BrandApiService;
import aeu.ratha.beckie_deal_app.data.models.Brand;
import aeu.ratha.beckie_deal_app.data.models.BrandResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class for managing brand data.
 * Handles API calls and data caching for brands.
 */
public class BrandRepository {
    
    private static final String TAG = "BrandRepository";
    private static BrandRepository instance;
    
    private BrandApiService apiService;
    private List<Brand> cachedBrands;
    private long lastFetchTime = 0;
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5 minutes
    
    /**
     * Callback interface for brand fetch operations.
     */
    public interface BrandCallback {
        void onSuccess(List<Brand> brands, int total);
        void onError(String errorMessage);
    }
    
    /**
     * Private constructor for singleton pattern.
     */
    private BrandRepository(Context context) {
        apiService = RetrofitClient.getClient(context).create(BrandApiService.class);
        cachedBrands = new ArrayList<>();
    }
    
    /**
     * Get singleton instance of BrandRepository.
     */
    public static synchronized BrandRepository getInstance(Context context) {
        if (instance == null) {
            instance = new BrandRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Fetch all brands with pagination.
     * Uses cache if available and not expired.
     */
    public void fetchBrands(int limit, int offset, boolean forceRefresh, 
                           @NonNull BrandCallback callback) {
        // Check cache if not forcing refresh and offset is 0
        if (!forceRefresh && offset == 0 && isCacheValid()) {
            Log.d(TAG, "Returning cached brands: " + cachedBrands.size());
            callback.onSuccess(new ArrayList<>(cachedBrands), cachedBrands.size());
            return;
        }
        
        // Fetch from API
        Call<BrandResponse> call = apiService.getBrands(limit, offset);
        
        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BrandResponse brandResponse = response.body();
                    
                    if ("success".equals(brandResponse.getStatus()) && 
                        brandResponse.getData() != null && 
                        brandResponse.getData().getBrands() != null) {
                        
                        List<Brand> brands = brandResponse.getData().getBrands();
                        int total = brandResponse.getData().getPagination().getTotal();
                        
                        // Update cache for first page
                        if (offset == 0) {
                            cachedBrands = new ArrayList<>(brands);
                            lastFetchTime = System.currentTimeMillis();
                        }
                        
                        Log.d(TAG, "Fetched " + brands.size() + " brands from API");
                        callback.onSuccess(brands, total);
                    } else {
                        String error = "Invalid response format";
                        Log.e(TAG, error);
                        callback.onError(error);
                    }
                } else {
                    String error = "API Error: " + response.code() + " - " + response.message();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                
                // Return cached data if available
                if (!cachedBrands.isEmpty()) {
                    Log.d(TAG, "Returning cached brands after error");
                    callback.onSuccess(new ArrayList<>(cachedBrands), cachedBrands.size());
                } else {
                    callback.onError(error);
                }
            }
        });
    }
    
    /**
     * Fetch all brands with default pagination.
     */
    public void fetchBrands(@NonNull BrandCallback callback) {
        fetchBrands(20, 0, false, callback);
    }
    
    /**
     * Fetch featured brands only.
     */
    public void fetchFeaturedBrands(int limit, @NonNull BrandCallback callback) {
        Call<BrandResponse> call = apiService.getFeaturedBrands(true, limit, 0);
        
        call.enqueue(new Callback<BrandResponse>() {
            @Override
            public void onResponse(Call<BrandResponse> call, Response<BrandResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    BrandResponse brandResponse = response.body();
                    
                    if ("success".equals(brandResponse.getStatus()) && 
                        brandResponse.getData() != null && 
                        brandResponse.getData().getBrands() != null) {
                        
                        List<Brand> brands = brandResponse.getData().getBrands();
                        int total = brandResponse.getData().getPagination().getTotal();
                        
                        Log.d(TAG, "Fetched " + brands.size() + " featured brands");
                        callback.onSuccess(brands, total);
                    } else {
                        callback.onError("Invalid response format");
                    }
                } else {
                    callback.onError("API Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BrandResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get active brands only from a list.
     */
    public List<Brand> filterActiveBrands(List<Brand> brands) {
        List<Brand> activeBrands = new ArrayList<>();
        for (Brand brand : brands) {
            if (brand.isActive()) {
                activeBrands.add(brand);
            }
        }
        return activeBrands;
    }
    
    /**
     * Search brands by name.
     */
    public void searchBrands(String query, @NonNull BrandCallback callback) {
        // First fetch all brands
        fetchBrands(100, 0, false, new BrandCallback() {
            @Override
            public void onSuccess(List<Brand> brands, int total) {
                List<Brand> filteredBrands = new ArrayList<>();
                String lowerQuery = query.toLowerCase().trim();
                
                for (Brand brand : brands) {
                    if (brand.getName().toLowerCase().contains(lowerQuery) ||
                        (brand.getSlug() != null && brand.getSlug().toLowerCase().contains(lowerQuery))) {
                        filteredBrands.add(brand);
                    }
                }
                
                callback.onSuccess(filteredBrands, filteredBrands.size());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    /**
     * Check if cached data is still valid.
     */
    private boolean isCacheValid() {
        if (cachedBrands.isEmpty()) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastFetchTime) < CACHE_DURATION;
    }
    
    /**
     * Clear the cache.
     */
    public void clearCache() {
        cachedBrands.clear();
        lastFetchTime = 0;
        Log.d(TAG, "Cache cleared");
    }
    
    /**
     * Get cached brands.
     */
    public List<Brand> getCachedBrands() {
        return new ArrayList<>(cachedBrands);
    }
    
    /**
     * Check if cache is available.
     */
    public boolean hasCachedData() {
        return !cachedBrands.isEmpty();
    }
}
