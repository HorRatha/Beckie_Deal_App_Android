package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import aeu.ratha.beckie_deal_app.data.api.CategoryApiService;
import aeu.ratha.beckie_deal_app.data.models.Category;
import aeu.ratha.beckie_deal_app.data.models.CategoryResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository class for handling category data operations.
 * Manages API calls and data transformation for categories.
 */
public class CategoryRepository {
    
    private static final String TAG = "CategoryRepository";
    private static CategoryRepository instance;
    private final CategoryApiService apiService;
    
    private CategoryRepository(Context context) {
        this.apiService = RetrofitClient.getClient(context)
                .create(CategoryApiService.class);
    }
    
    /**
     * Get singleton instance of CategoryRepository.
     */
    public static synchronized CategoryRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CategoryRepository(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Fetch categories from the API.
     * 
     * @param limit Maximum number of categories to fetch
     * @param offset Number of categories to skip
     * @param forceRefresh Whether to force a fresh API call (not implemented yet)
     * @param callback Callback to handle success/error
     */
    public void fetchCategories(int limit, int offset, boolean forceRefresh, 
                                CategoryCallback callback) {
        Log.d(TAG, "Fetching categories: limit=" + limit + ", offset=" + offset);
        
        apiService.getCategories(limit, offset).enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryResponse categoryResponse = response.body();
                    
                    if ("success".equals(categoryResponse.getStatus()) 
                            && categoryResponse.getData() != null) {
                        List<Category> categories = categoryResponse.getData().getCategories();
                        int total = categoryResponse.getData().getPagination() != null 
                                ? categoryResponse.getData().getPagination().getTotal() 
                                : (categories != null ? categories.size() : 0);
                        
                        Log.d(TAG, "Successfully fetched " + 
                            (categories != null ? categories.size() : 0) + 
                            " categories out of " + total + " total");
                        
                        if (callback != null) {
                            callback.onSuccess(categories, total);
                        }
                    } else {
                        String errorMsg = categoryResponse.getMessage() != null 
                                ? categoryResponse.getMessage() 
                                : "Invalid response status";
                        Log.e(TAG, "API returned error: " + errorMsg);
                        if (callback != null) {
                            callback.onError(errorMsg);
                        }
                    }
                } else {
                    String errorMsg = "HTTP " + response.code() + ": " + response.message();
                    Log.e(TAG, "API call failed: " + errorMsg);
                    if (callback != null) {
                        callback.onError(errorMsg);
                    }
                }
            }
            
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                String errorMsg = "Network error: " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                if (callback != null) {
                    callback.onError(errorMsg);
                }
            }
        });
    }
    
    /**
     * Fetch only active categories.
     */
    public void fetchActiveCategories(int limit, int offset, CategoryCallback callback) {
        fetchCategories(limit, offset, false, new CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories, int total) {
                // Filter for active categories only
                List<Category> activeCategories = new java.util.ArrayList<>();
                int activeCount = 0;
                
                if (categories != null) {
                    for (Category category : categories) {
                        if (category.isActive()) {
                            activeCategories.add(category);
                            activeCount++;
                        }
                    }
                }
                
                Log.d(TAG, "Filtered to " + activeCount + " active categories");
                callback.onSuccess(activeCategories, activeCount);
            }
            
            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    /**
     * Callback interface for category fetch operations.
     */
    public interface CategoryCallback {
        void onSuccess(List<Category> categories, int total);
        void onError(String errorMessage);
    }
}
