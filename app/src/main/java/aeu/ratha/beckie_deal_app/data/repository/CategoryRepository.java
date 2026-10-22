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
    private final Context context;
    
    private CategoryRepository(Context context) {
        this.context = context.getApplicationContext();
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
     * Note: The /api/v1/categories endpoint does not support pagination.
     * It returns all categories at once with parent categories containing
     * a "children" array for subcategories.
     * 
     * @param forceRefresh Whether to force a fresh API call (not implemented yet)
     * @param callback Callback to handle success/error
     */
    public void fetchCategories(boolean forceRefresh, CategoryCallback callback) {
        Log.d(TAG, "Fetching categories from /api/v1/categories");
        
        apiService.getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryResponse categoryResponse = response.body();
                    
                    if ("success".equals(categoryResponse.getStatus()) 
                            && categoryResponse.getData() != null) {
                        java.util.List<Category> categories = categoryResponse.getData();
                        int total = categories.size();
                        
                        Log.d(TAG, "Successfully fetched " + total + " categories");
                        logCategoryHierarchy(categories);
                        
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
     * Backward compatible method that accepts limit/offset params
     * but calls the endpoint without them (API doesn't support pagination).
     * 
     * @deprecated Use fetchCategories(boolean, CategoryCallback) instead
     */
    @Deprecated
    public void fetchCategories(int limit, int offset, boolean forceRefresh, CategoryCallback callback) {
        Log.w(TAG, "fetchCategories with limit/offset called but endpoint doesn't support pagination. " +
                "Ignoring limit=" + limit + ", offset=" + offset);
        fetchCategories(forceRefresh, callback);
    }
    
    /**
     * Fetch only active categories.
     * 
     * PERFORMANCE NOTE: Product counts are fetched using N+1 approach for the wallet/categories view.
     * This is only done when explicitly requested via fetchActiveCategories(), not on main page load.
     * The main page uses fetchProducts() with cache which is much faster.
     * 
     * This provides accurate product counts for the category wallet interface.
     */
    public void fetchActiveCategories(CategoryCallback callback) {
        fetchCategories(false, new CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories, int total) {
                // Filter for active categories only
                List<Category> activeCategories = new java.util.ArrayList<>();
                int activeCount = 0;
                
                if (categories != null) {
                    for (Category category : categories) {
                        if (category.isActive()) {
                            // Add parent category if active
                            activeCategories.add(category);
                            activeCount++;
                            
                            // Also filter children to be active only
                            if (category.hasChildren()) {
                                List<Category> activeChildren = new java.util.ArrayList<>();
                                for (Category child : category.getChildren()) {
                                    if (child.isActive()) {
                                        activeChildren.add(child);
                                    }
                                }
                                category.setChildren(activeChildren);
                            }
                        }
                    }
                }
                
                Log.d(TAG, "Filtered to " + activeCount + " active categories");
                
                // ✓ Fetch product counts synchronously for wallet display
                // This is only called from CategoriesActivity wallet view, not main page
                fetchProductCountsForCategories(activeCategories, callback);
            }
            
            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }
    
    /**
     * Fetch product counts for all categories by querying products per category.
     * This is a workaround because the API doesn't populate products_count.
     * 
     * NOTE: This is only called from the CategoriesActivity wallet view, which is not
     * on the critical main page load path. The main page uses cached products instead
     * for better performance. This keeps the wallet accurate without affecting app startup speed.
     */
    private void fetchProductCountsForCategories(List<Category> categories, CategoryCallback callback) {
        if (categories == null || categories.isEmpty()) {
            Log.d(TAG, "No categories to fetch product counts for");
            callback.onSuccess(categories, categories.size());
            return;
        }
        
        // Get ProductRepository instance to fetch products per category
        ProductRepository productRepo = ProductRepository.getInstance(context);
        
        // Counter for tracking completed fetches
        final int[] completedFetches = {0};
        final int totalCategories = categories.size();
        
        Log.d(TAG, "Fetching product counts for " + totalCategories + " categories");
        
        for (Category category : categories) {
            productRepo.fetchProductsByCategory(category.getId(), 20, 0, 
                new ProductRepository.ProductCallback() {
                    @Override
                    public void onSuccess(List<aeu.ratha.beckie_deal_app.data.models.Product> products, int count) {
                        // Set the product count for this category
                        category.setProductsCount(count);
                        Log.d(TAG, "Category '" + category.getName() + "' has " + count + " products");
                        
                        // Increment counter
                        completedFetches[0]++;
                        
                        // Check if all fetches are complete
                        if (completedFetches[0] == totalCategories) {
                            Log.d(TAG, "✓ All product counts fetched");
                            callback.onSuccess(categories, categories.size());
                        }
                    }
                    
                    @Override
                    public void onError(String errorMessage) {
                        Log.e(TAG, "Error fetching products for category '" + category.getName() + "': " + errorMessage);
                        // Set count to 0 on error
                        category.setProductsCount(0);
                        
                        // Increment counter
                        completedFetches[0]++;
                        
                        // Check if all fetches are complete
                        if (completedFetches[0] == totalCategories) {
                            Log.d(TAG, "✓ All product counts fetched (some with errors)");
                            callback.onSuccess(categories, categories.size());
                        }
                    }
                });
        }
    }
    
    /**
     * Log category hierarchy for debugging.
     */
    private void logCategoryHierarchy(List<Category> categories) {
        if (categories == null) {
            Log.d(TAG, "Categories list is null");
            return;
        }
        
        for (Category category : categories) {
            Log.d(TAG, "├─ [" + category.getId() + "] " + category.getName() + 
                    " (parent_id: " + category.getParentId() + ")");
            
            if (category.hasChildren()) {
                for (Category child : category.getChildren()) {
                    Log.d(TAG, "│  └─ [" + child.getId() + "] " + child.getName());
                }
            }
        }
    }
    
    /**
     * Callback interface for category fetch operations.
     */
    public interface CategoryCallback {
        void onSuccess(List<Category> categories, int total);
        void onError(String errorMessage);
    }
}
