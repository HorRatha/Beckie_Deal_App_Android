package aeu.ratha.beckie_deal_app.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import aeu.ratha.beckie_deal_app.data.api.ProductApiService;
import aeu.ratha.beckie_deal_app.data.models.Product;
import aeu.ratha.beckie_deal_app.data.models.ProductDetail;
import aeu.ratha.beckie_deal_app.data.models.ProductResponse;
import aeu.ratha.beckie_deal_app.data.models.SingleProductResponse;
import aeu.ratha.beckie_deal_app.network.RetrofitClient;
import aeu.ratha.beckie_deal_app.utils.PaginationHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for managing product data from the API.
 *
 * Architecture notes:
 *  - The product LIST endpoint (GET /products) returns flat items with NO category/brand info.
 *  - The product DETAIL endpoint (GET /products/{uuid}) returns the full object with
 *    category, brand, variants, and images.
 *  - Category/brand filtering is done client-side: fetch all products, then load each
 *    product's detail (cached) to get its category and brand.
 *  - There are only ~18 products total, so individual detail fetches are acceptable.
 */
public class ProductRepository {

    private static final String TAG = "ProductRepository";
    private static ProductRepository instance;

    private final ProductApiService apiService;

    /** Full list cache — reset on each fresh fetch (offset == 0). */
    private final List<Product> cachedProducts = new ArrayList<>();

    /** Detail cache keyed by product UUID. */
    private final Map<String, ProductDetail> detailCache = new HashMap<>();
    
    /** Cache timestamp tracking */
    private static final long CACHE_VALIDITY_MS = 5 * 60 * 1000;  // 5 minutes
    private long lastCacheTime = 0;
    private long lastDetailsCacheTime = 0;

    private ProductRepository(Context context) {
        this.apiService = RetrofitClient.getClient(context).create(ProductApiService.class);
    }

    public static synchronized ProductRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProductRepository(context.getApplicationContext());
        }
        return instance;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cache Validity Checking
    // ─────────────────────────────────────────────────────────────────────────
    
    /**
     * Check if cached products are still fresh (less than 5 minutes old).
     * @return true if cache is fresh and should be used
     */
    private boolean isCacheFresh() {
        if (cachedProducts.isEmpty()) return false;
        long timeSinceCache = System.currentTimeMillis() - lastCacheTime;
        boolean fresh = timeSinceCache < CACHE_VALIDITY_MS;
        Log.d(TAG, "Cache age: " + (timeSinceCache / 1000) + "s, Fresh: " + fresh);
        return fresh;
    }
    
    /**
     * Check if detail cache is still fresh.
     */
    private boolean isDetailCacheFresh() {
        if (detailCache.isEmpty()) return false;
        long timeSinceCache = System.currentTimeMillis() - lastDetailsCacheTime;
        return timeSinceCache < CACHE_VALIDITY_MS;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // List
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetch all products with page-based pagination.
     * Uses smart caching: returns cached data if fresh (less than 5 minutes old).
     * On subsequent calls within 5 minutes, returns cached data instantly.
     *
     * PAGINATION STRATEGY:
     * - offset == 0: Fetch ALL pages automatically using a loop
     *   * Converts limit/offset to page size for API calls
     *   * Uses PaginationHelper to track pagination state
     *   * Updates cache only on first page (offset==0)
     * - offset > 0: Fetch specific page (for manual pagination)
     *   * Converts offset to page number: page = (offset / limit) + 1
     *   * Returns single page only
     *
     * @param limit      Max items per page
     * @param offset     Starting offset (0 = fetch all pages, >0 = fetch specific page)
     * @param useCache   Return cached list if available and fresh
     * @param callback   Result callback
     */
    public void fetchProducts(int limit, int offset, boolean useCache,
                              @NonNull ProductCallback callback) {
        // Strategy: 
        // - If offset == 0 and useCache && cache is fresh: return cached immediately
        // - If offset == 0 and cache is stale: fetch from API (all pages)
        // - If offset > 0: fetch specific page from API
        
        if (offset == 0 && useCache && isCacheFresh()) {
            Log.d(TAG, "✓ Returning " + cachedProducts.size() + " cached products (fresh cache)");
            callback.onSuccess(new ArrayList<>(cachedProducts), cachedProducts.size());
            return;
        }
        
        if (offset == 0 && useCache && !cachedProducts.isEmpty()) {
            Log.d(TAG, "⚠ Returning " + cachedProducts.size() + " cached products (may be stale)");
            // Return stale cache while fetching fresh data in background
            callback.onSuccess(new ArrayList<>(cachedProducts), cachedProducts.size());
            // Still fetch fresh data (don't return early)
        }

        // Fetch all pages when offset==0, otherwise fetch specific page
        if (offset == 0) {
            Log.d(TAG, "Fetching ALL products from API (per_page=" + limit + ")");
            fetchAllPagesInternal(limit, callback);
        } else {
            // Convert offset to page number (1-indexed)
            int page = (offset / limit) + 1;
            Log.d(TAG, "Fetching products from API (page=" + page + ", per_page=" + limit + ")");
            apiService.getProducts("featured", page, limit).enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call,
                                       @NonNull Response<ProductResponse> response) {
                    handleListResponse(response, offset, callback, "products (page " + page + ")");
                }

                @Override
                public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                    handleFailure(t, callback);
                }
            });
        }
    }

    /**
     * Search products by keyword (name-based, server-side).
     * 
     * PAGINATION STRATEGY:
     * - offset == 0: Fetch ALL pages automatically using a loop
     *   * Uses PaginationHelper to track pagination state
     *   * Does NOT cache search results (search results should reflect real-time data)
     * - offset > 0: Fetch specific page
     *   * Converts offset to page number: page = (offset / limit) + 1
     *   * Returns single page only
     *
     * Note: Search results are NOT cached to reflect real-time data.
     *
     * @param query    Search keyword
     * @param limit    Items per page
     * @param offset   Starting offset (0 = fetch all, >0 = specific page)
     * @param callback Result callback
     */
    public void searchProducts(String query, int limit, int offset,
                               @NonNull ProductCallback callback) {
        Log.d(TAG, "Searching products: '" + query + "'");
        
        // Fetch all pages when offset==0, otherwise fetch specific page
        if (offset == 0) {
            Log.d(TAG, "Fetching ALL search results from API (per_page=" + limit + ")");
            searchAllPagesInternal(query, limit, callback);
        } else {
            // Convert offset to page number (1-indexed)
            int page = (offset / limit) + 1;
            Log.d(TAG, "Fetching search page (page=" + page + ", per_page=" + limit + ")");
            apiService.searchProducts(query, page, limit).enqueue(new Callback<ProductResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProductResponse> call,
                                       @NonNull Response<ProductResponse> response) {
                    handleListResponse(response, -1, callback, "search: " + query + " (page " + page + ")");
                }

                @Override
                public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                    handleFailure(t, callback);
                }
            });
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal pagination helpers (page-based API pagination)
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Start fetching all pages for products.
     * Initializes PaginationHelper and recursively fetches all pages.
     *
     * @param perPage  Items per page
     * @param callback Result callback with all accumulated products
     */
    private void fetchAllPagesInternal(int perPage, @NonNull ProductCallback callback) {
        PaginationHelper<Product> helper = new PaginationHelper<>(perPage);
        List<Product> allProducts = new ArrayList<>();
        fetchNextPage(helper, allProducts, perPage, callback);
    }

    /**
     * Recursively fetch the next page of products using PaginationHelper.
     * Continues fetching until PaginationHelper determines there are no more pages.
     *
     * @param helper       PaginationHelper tracking pagination state
     * @param accumulatedProducts List accumulating all products from all pages
     * @param perPage      Items per page
     * @param callback     Result callback
     */
    private void fetchNextPage(PaginationHelper<Product> helper, List<Product> accumulatedProducts,
                               int perPage, @NonNull ProductCallback callback) {
        int nextPage = helper.getNextPage();
        Log.d(TAG, "Fetching page " + nextPage + " (per_page=" + perPage + ")");
        
        apiService.getProducts("featured", nextPage, perPage).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call,
                                   @NonNull Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse pr = response.body();
                    if (pr.isSuccess() && pr.getData() != null) {
                        List<Product> pageData = pr.getData();
                        
                        // Add page data and check if more pages exist
                        boolean hasMorePages = helper.addPageData(pageData, accumulatedProducts);
                        
                        if (hasMorePages) {
                            // Recursively fetch next page
                            Log.d(TAG, "More pages available, fetching next...");
                            fetchNextPage(helper, accumulatedProducts, perPage, callback);
                        } else {
                            // All pages fetched
                            int total = accumulatedProducts.size();
                            Log.d(TAG, "✓ All pages fetched: " + total + " total products");
                            
                            // Update cache with all products
                            cachedProducts.clear();
                            cachedProducts.addAll(accumulatedProducts);
                            lastCacheTime = System.currentTimeMillis();
                            Log.d(TAG, "✓ Cache updated with " + total + " products at " + new java.util.Date());
                            
                            callback.onSuccess(accumulatedProducts, total);
                        }
                    } else {
                        callback.onError("API non-success: " + pr.getStatus());
                    }
                } else {
                    String error = "HTTP " + response.code() + ": " + response.message();
                    Log.e(TAG, error);
                    
                    // If we have accumulated some results, return them instead of failing
                    if (!accumulatedProducts.isEmpty()) {
                        Log.d(TAG, "⚠ Returning " + accumulatedProducts.size() + " accumulated products before error");
                        callback.onSuccess(accumulatedProducts, accumulatedProducts.size());
                    } else {
                        callback.onError(error);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                
                // If we have accumulated some results, return them instead of failing
                if (!accumulatedProducts.isEmpty()) {
                    Log.d(TAG, "⚠ Returning " + accumulatedProducts.size() + " accumulated products before error");
                    callback.onSuccess(accumulatedProducts, accumulatedProducts.size());
                } else {
                    callback.onError(error);
                }
            }
        });
    }

    /**
     * Start fetching all pages for search results.
     * Initializes PaginationHelper and recursively fetches all pages.
     * Note: Search results are NOT cached to reflect real-time data.
     *
     * @param query    Search keyword
     * @param perPage  Items per page
     * @param callback Result callback with all accumulated search results
     */
    private void searchAllPagesInternal(String query, int perPage, @NonNull ProductCallback callback) {
        PaginationHelper<Product> helper = new PaginationHelper<>(perPage);
        List<Product> allResults = new ArrayList<>();
        searchNextPage(helper, query, allResults, perPage, callback);
    }

    /**
     * Recursively fetch the next page of search results using PaginationHelper.
     * Continues fetching until PaginationHelper determines there are no more pages.
     * Note: Search results are NOT cached.
     *
     * @param helper       PaginationHelper tracking pagination state
     * @param query        Search keyword
     * @param accumulatedResults List accumulating all search results from all pages
     * @param perPage      Items per page
     * @param callback     Result callback
     */
    private void searchNextPage(PaginationHelper<Product> helper, String query, List<Product> accumulatedResults,
                                int perPage, @NonNull ProductCallback callback) {
        int nextPage = helper.getNextPage();
        Log.d(TAG, "Fetching search page " + nextPage + " for '" + query + "' (per_page=" + perPage + ")");
        
        apiService.searchProducts(query, nextPage, perPage).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call,
                                   @NonNull Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse pr = response.body();
                    if (pr.isSuccess() && pr.getData() != null) {
                        List<Product> pageData = pr.getData();
                        
                        // Add page data and check if more pages exist
                        boolean hasMorePages = helper.addPageData(pageData, accumulatedResults);
                        
                        if (hasMorePages) {
                            // Recursively fetch next page
                            Log.d(TAG, "More search pages available, fetching next...");
                            searchNextPage(helper, query, accumulatedResults, perPage, callback);
                        } else {
                            // All pages fetched
                            int total = accumulatedResults.size();
                            Log.d(TAG, "✓ All search pages fetched: " + total + " total results for '" + query + "'");
                            
                            callback.onSuccess(accumulatedResults, total);
                        }
                    } else {
                        callback.onError("API non-success: " + pr.getStatus());
                    }
                } else {
                    String error = "HTTP " + response.code() + ": " + response.message();
                    Log.e(TAG, error);
                    
                    // If we have accumulated some results, return them instead of failing
                    if (!accumulatedResults.isEmpty()) {
                        Log.d(TAG, "⚠ Returning " + accumulatedResults.size() + " accumulated search results before error");
                        callback.onSuccess(accumulatedResults, accumulatedResults.size());
                    } else {
                        callback.onError(error);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                
                // If we have accumulated some results, return them instead of failing
                if (!accumulatedResults.isEmpty()) {
                    Log.d(TAG, "⚠ Returning " + accumulatedResults.size() + " accumulated search results before error");
                    callback.onSuccess(accumulatedResults, accumulatedResults.size());
                } else {
                    callback.onError(error);
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Client-side category / brand filtering
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetch all products then filter client-side by category integer ID.
     *
     * Backend Constraint:
     * - GET /products returns a flat list; category_id field is NOT included per product
     * - The ?category_id= query filter expects UUID format, but master/categories returns integer IDs
     * - Solution: fetch all products and individual product details to get category information
     *
     * Category Hierarchy:
     * - Categories have parent_id relationships (0 = root, otherwise parent ID)
     * - This method filters by EXACT category ID match (not including subcategories)
     * - Pass the exact category ID you want (root ID or subcategory ID)
     * - Products are returned only if their category.getId() exactly matches categoryId
     *
     * @param categoryId  Integer ID from master/categories (both root and subcategory IDs supported)
     */
    public void fetchProductsByCategory(int categoryId, int limit, int offset,
                                        @NonNull ProductCallback callback) {
        Log.d(TAG, "=== FETCHING PRODUCTS FOR CATEGORY ID: " + categoryId + " ===");
        
        // First, try to fetch all products and use their embedded category info
        fetchProducts(limit, offset, true, new ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                Log.d(TAG, "Initial fetch: " + products.size() + " products");
                
                // Filter using embedded category info if available
                List<Product> filtered = filterByEmbeddedCategory(products, categoryId);
                
                if (!filtered.isEmpty()) {
                    // We got results from embedded category info — use them
                    Log.d(TAG, "✓ Using embedded category info: " + filtered.size() + " products matched");
                    callback.onSuccess(filtered, filtered.size());
                } else {
                    // Check if detailCache has cached details for any products
                    List<Product> detailFiltered = new ArrayList<>();
                    boolean hasCachedDetails = false;
                    for (Product p : products) {
                        ProductDetail detail = detailCache.get(p.getId());
                        if (detail != null) {
                            hasCachedDetails = true;
                            if (detail.getCategory() != null) {
                                if (detail.getCategory().getId() == categoryId || detail.getCategory().getParentId() == categoryId) {
                                    detailFiltered.add(p);
                                }
                            }
                        }
                    }
                    if (hasCachedDetails) {
                        Log.d(TAG, "✓ Using cached details: " + detailFiltered.size() + " products matched");
                        callback.onSuccess(detailFiltered, detailFiltered.size());
                    } else {
                        // Instant callback with filtered list (even if empty) to prevent UI stall
                        Log.d(TAG, "✓ Instant callback with filtered list (" + filtered.size() + " items)");
                        callback.onSuccess(filtered, filtered.size());
                        
                        // Background detail pre-fetch without blocking UI thread
                        fetchAllProductsWithDetails(limit, offset, new ProductCallback() {
                            @Override
                            public void onSuccess(List<Product> detailProducts, int detailTotal) {
                                Log.d(TAG, "Background detail pre-fetch complete");
                            }
                            @Override
                            public void onError(String errorMessage) {}
                        });
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                // If initial fetch fails, report error
                Log.e(TAG, "Failed to fetch products: " + errorMessage);
                callback.onError(errorMessage);
            }
        });
    }
    
    /**
     * Try to filter products using their embedded category information.
     * This is the fast path if products already include category data.
     * 
     * WORKAROUND: If products don't have category objects, use keyword matching
     * on product names to infer category. This is a temporary solution until
     * the backend API includes category info in the product list response.
     * 
     * IMPORTANT: If a parent category is requested (e.g., "Accessories" with id=5),
     * this will also include products from child categories (e.g., "Mouse" id=20, "Cables" id=49)
     * that have parent_id == 5.
     * 
     * This ensures parent categories show all products, not just products directly assigned.
     */
    private List<Product> filterByEmbeddedCategory(List<Product> products, int categoryId) {
        List<Product> filtered = new ArrayList<>();
        int noEmbedded = 0;
        int exactMatches = 0;
        int parentMatches = 0;
        int keywordMatches = 0;
        
        for (Product p : products) {
            if (p.getCategory() == null) {
                noEmbedded++;
                // WORKAROUND: If no embedded category, try keyword matching on product name
                if (matchesCategoryByKeyword(p.getName(), categoryId)) {
                    Log.d(TAG, "  ✓ Keyword: Product " + p.getId() + " (" + p.getName() + ") matches category id=" + categoryId);
                    filtered.add(p);
                    keywordMatches++;
                }
                continue;
            }
            
            int productCategoryId = p.getCategory().getId();
            int parentCategoryId = p.getCategory().getParentId();
            
            // Check for EXACT category match
            if (productCategoryId == categoryId) {
                Log.d(TAG, "  ✓ Embedded: Product " + p.getId() + " (" + p.getName() + ") exact category match (id=" + productCategoryId + ")");
                filtered.add(p);
                exactMatches++;
            }
            // Check for PARENT category match (include products from child categories)
            // This handles cases like "Accessories" (id=5) showing products from "Mouse" (id=20, parent=5)
            else if (parentCategoryId == categoryId && parentCategoryId > 0) {
                Log.d(TAG, "  ✓ Embedded: Product " + p.getId() + " (" + p.getName() + ") parent category match (product category=" + productCategoryId + ", parent=" + parentCategoryId + ")");
                filtered.add(p);
                parentMatches++;
            }
        }
        
        Log.d(TAG, "Embedded category check: " + filtered.size() + " total matched (" + exactMatches + " exact, " + parentMatches + " parent, " + keywordMatches + " keyword), " + noEmbedded + " missing category");
        return filtered;
    }
    
    /**
     * WORKAROUND: Match product name against category keywords.
     * Since backend doesn't include category in product list, we infer it from product name.
     * This is temporary until backend API is updated.
     */
    private boolean matchesCategoryByKeyword(String productName, int categoryId) {
        if (productName == null) return false;
        
        String lowerName = productName.toLowerCase();
        
        switch (categoryId) {
            // Printers & Scanners category (ID = 10)
            case 10:
                return lowerName.contains("printer") || lowerName.contains("scanner") || 
                       lowerName.contains("print") || lowerName.contains("canon") ||
                       lowerName.contains("brother") || lowerName.contains("epson") ||
                       lowerName.contains("officejet") || lowerName.contains("laserjet") ||
                       lowerName.contains("inkjet");

            // Laptops / Computers (ID = 1, 3, 15, 16, 70)
            case 1:
            case 3:
            case 15:
            case 16:
            case 70:
                return lowerName.contains("laptop") || lowerName.contains("macbook") || 
                       lowerName.contains("thinkpad") || lowerName.contains("vivobook") ||
                       lowerName.contains("zenbook") || lowerName.contains("rog") ||
                       lowerName.contains("legion") || lowerName.contains("alienware") ||
                       lowerName.contains("surface") || lowerName.contains("dell") ||
                       lowerName.contains("hp") || lowerName.contains("asus") ||
                       lowerName.contains("lenovo") || lowerName.contains("msi") ||
                       lowerName.contains("probook") || lowerName.contains("latitude");

            // Gaming (ID = 23, 77, 78)
            case 23:
            case 77:
            case 78:
                return lowerName.contains("gaming") || lowerName.contains("rog") || 
                       lowerName.contains("rtx") || lowerName.contains("razer") ||
                       lowerName.contains("alienware") || lowerName.contains("legion") ||
                       lowerName.contains("strix") || lowerName.contains("msi");

            // Wearables (ID = 26, 79)
            case 26:
            case 79:
                return lowerName.contains("watch") || lowerName.contains("band") || 
                       lowerName.contains("wearable") || lowerName.contains("fitbit") ||
                       lowerName.contains("galaxy watch") || lowerName.contains("apple watch");

            // Audio (ID = 27, 80)
            case 27:
            case 80:
                return lowerName.contains("headphone") || lowerName.contains("earbud") || 
                       lowerName.contains("speaker") || lowerName.contains("airpods") ||
                       lowerName.contains("audio") || lowerName.contains("microphone");

            // Storage category (ID=2)
            case 2:
                return lowerName.contains("ssd") || lowerName.contains("storage") || 
                       lowerName.contains("nvme") || lowerName.contains("hard drive") ||
                       lowerName.contains("samsung 990") || lowerName.contains("drive");
            
            // Monitor category (ID=4)
            case 4:
                return lowerName.contains("monitor") || lowerName.contains("display");
            
            // Accessories category (ID=5)
            case 5:
                return lowerName.contains("mouse") || lowerName.contains("keyboard") ||
                       lowerName.contains("cable") || lowerName.contains("adapter") ||
                       lowerName.contains("backpack") || lowerName.contains("bag") ||
                       lowerName.contains("power bank") || lowerName.contains("charger") ||
                       lowerName.contains("headphone") || lowerName.contains("router");
            
            // Networking category (ID=6)
            case 6:
                return lowerName.contains("router") || lowerName.contains("network");
            
            // Electronics category (ID=22)
            case 22:
                return lowerName.contains("gpu") || lowerName.contains("graphics") ||
                       lowerName.contains("card") || lowerName.contains("rtx");
            
            // Smartphones category (ID=24)
            case 24:
                // Match "smartphone", "cell phone", "mobile" but NOT "headphone"
                return (lowerName.contains("smartphone") || 
                        lowerName.contains("mobile phone") ||
                        lowerName.contains("cellular")) &&
                       !lowerName.contains("headphone");
            
            // Tablets category (ID=25)
            case 25:
                return lowerName.contains("tablet") || lowerName.contains("ipad");
            
            // Apple MacBook Air (ID=71)
            case 71:
                return lowerName.contains("macbook air") || lowerName.contains("macbook");
            
            // Apple MacBook Pro (ID=72)
            case 72:
                return lowerName.contains("macbook pro");
            
            // Microsoft Mouse (ID=73)
            case 73:
                return lowerName.contains("microsoft mouse");
            
            // ASUS VIVOBOOK (ID=74)
            case 74:
                return lowerName.contains("asus") || lowerName.contains("vivobook");
            
            // Surface Pro (ID=75)
            case 75:
                return lowerName.contains("surface pro");
            
            // Laptop (generic for various laptop IDs)
            case 76: // Dell Alienware Gaming
                return lowerName.contains("alienware") || lowerName.contains("dell");
            
            // Gaming Monitor (ID=17)
            case 17:
                return lowerName.contains("gaming monitor");
            
            // Office Monitor (ID=18)
            case 18:
                return lowerName.contains("office monitor") || (lowerName.contains("monitor") && !lowerName.contains("gaming"));
            
            // NVMe SSD (ID=13)
            case 13:
                return lowerName.contains("nvme") || lowerName.contains("990 pro");
            
            // Hard Drives (ID=14)
            case 14:
                return lowerName.contains("hard drive");
            
            // Internal SSDs (ID=40)
            case 40:
                return lowerName.contains("internal ssd") || lowerName.contains("ssd");
            
            // External Storage (ID=41)
            case 41:
                return lowerName.contains("external");
            
            // Mouse subcategory (ID=20)
            case 20:
                return lowerName.contains("mouse") && !lowerName.contains("microsoft");
            
            // Cables & Adapters (ID=49)
            case 49:
                return lowerName.contains("cable") || lowerName.contains("adapter");
            
            // Laptop Bags (ID=50)
            case 50:
                return lowerName.contains("case") || lowerName.contains("bag") || lowerName.contains("sleeve");
            
            // Power Banks (ID=51)
            case 51:
                return lowerName.contains("power bank") || lowerName.contains("charger");
            
            // Router (ID=21)
            case 21:
                return lowerName.contains("router");
            
            default:
                return false;
        }
    }

    /**
     * Fetch all products then filter client-side by brand integer ID.
     *
     * @param brandId  Integer ID from master/brands
     */
    public void fetchProductsByBrand(int brandId, int limit, int offset,
                                     @NonNull ProductCallback callback) {
        Log.d(TAG, "Fetching products for brand id=" + brandId + " (client-side filter)");
        fetchAllProductsWithDetails(limit, offset, new ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                List<Product> filtered = new ArrayList<>();
                for (Product p : products) {
                    ProductDetail detail = detailCache.get(p.getId());
                    if (detail != null && detail.getBrand() != null
                            && detail.getBrand().getId() == brandId) {
                        filtered.add(p);
                    }
                }
                Log.d(TAG, "Brand filter id=" + brandId + " → " + filtered.size() + " products");
                callback.onSuccess(filtered, filtered.size());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch all products then filter client-side by both category AND brand.
     */
    public void fetchProductsByCategoryAndBrand(int categoryId, int brandId,
                                                int limit, int offset,
                                                @NonNull ProductCallback callback) {
        Log.d(TAG, "Fetching products for category=" + categoryId + " & brand=" + brandId);
        fetchAllProductsWithDetails(limit, offset, new ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                List<Product> filtered = new ArrayList<>();
                for (Product p : products) {
                    ProductDetail detail = detailCache.get(p.getId());
                    if (detail != null
                            && detail.getCategory() != null
                            && detail.getCategory().getId() == categoryId
                            && detail.getBrand() != null
                            && detail.getBrand().getId() == brandId) {
                        filtered.add(p);
                    }
                }
                Log.d(TAG, "Category+Brand filter → " + filtered.size() + " products");
                callback.onSuccess(filtered, filtered.size());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Fetch products related to Apple brand, MacBooks, Mac accessories, Mac Neo, etc.
     */
    public void fetchAppleProducts(int limit, int offset, @NonNull ProductCallback callback) {
        Log.d(TAG, "Fetching Apple & MacBook related products");
        fetchAllProductsWithDetails(limit, offset, new ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                List<Product> filtered = new ArrayList<>();
                for (Product p : products) {
                    ProductDetail detail = detailCache.get(p.getId());
                    boolean isAppleBrand = detail != null && detail.getBrand() != null && 
                                           (detail.getBrand().getId() == 25 || 
                                            (detail.getBrand().getName() != null && detail.getBrand().getName().toLowerCase().contains("apple")));
                    
                    String name = p.getName() != null ? p.getName().toLowerCase() : "";
                    boolean isAppleKeyword = name.contains("mac") || name.contains("apple") || 
                                             name.contains("macbook") || name.contains("ipad") || 
                                             name.contains("iphone") || name.contains("airpods") ||
                                             name.contains("neo") || name.contains("magsafe") ||
                                             name.contains("magic keyboard") || name.contains("magic mouse") ||
                                             name.contains("studio display");

                    if (isAppleBrand || isAppleKeyword) {
                        filtered.add(p);
                    }
                }
                Log.d(TAG, "Apple products filter → " + filtered.size() + " products matched");
                callback.onSuccess(filtered, filtered.size());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Single product detail
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetch a single product's full detail by UUID.
     * Uses an in-memory cache to avoid redundant calls.
     *
     * @param productId  UUID string (e.g. "01a09874-da51-7036-96ce-42b1fca277ce")
     * @param callback   Result callback
     */
    public void fetchProductDetail(String productId,
                                   @NonNull SingleProductCallback callback) {
        // Return from cache if available
        if (detailCache.containsKey(productId)) {
            Log.d(TAG, "Returning cached detail for " + productId);
            callback.onSuccess(detailCache.get(productId));
            return;
        }

        Log.d(TAG, "Fetching detail for product " + productId);
        apiService.getProduct(productId).enqueue(new Callback<SingleProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<SingleProductResponse> call,
                                   @NonNull Response<SingleProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SingleProductResponse spr = response.body();
                    if (spr.isSuccess() && spr.getData() != null) {
                        ProductDetail detail = spr.getData();
                        detailCache.put(productId, detail);
                        Log.d(TAG, "✓ Fetched detail: " + detail.getName()
                                + " | category=" + (detail.getCategory() != null ? detail.getCategory().getName() : "null")
                                + " | brand=" + (detail.getBrand() != null ? detail.getBrand().getName() : "null")
                                + " | variants=" + (detail.getVariants() != null ? detail.getVariants().size() : 0)
                                + " | images=" + (detail.getImages() != null ? detail.getImages().size() : 0));
                        callback.onSuccess(detail);
                    } else {
                        callback.onError("API returned non-success: " + spr.getStatus());
                    }
                } else {
                    callback.onError("HTTP " + response.code() + ": " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SingleProductResponse> call,
                                  @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetches all products from the list endpoint, then fetches each product's detail
     * (in parallel, cached) so that category/brand info is available for filtering.
     */
    private void fetchAllProductsWithDetails(int limit, int offset,
                                              @NonNull ProductCallback callback) {
        fetchProducts(limit, offset, true, new ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                if (products.isEmpty()) {
                    callback.onSuccess(products, total);
                    return;
                }

                // Collect all product IDs that need detail fetching
                List<String> productIdsToFetch = new ArrayList<>();
                for (Product product : products) {
                    if (!detailCache.containsKey(product.getId())) {
                        productIdsToFetch.add(product.getId());
                    }
                }

                // If all are cached, return immediately
                if (productIdsToFetch.isEmpty()) {
                    callback.onSuccess(products, total);
                    return;
                }

                // Fetch all details in parallel without blocking on a counter
                final int[] remaining = {productIdsToFetch.size()};
                final boolean[] failed = {false};

                for (String productId : productIdsToFetch) {
                    fetchProductDetail(productId, new SingleProductCallback() {
                        @Override
                        public void onSuccess(ProductDetail detail) {
                            synchronized (remaining) {
                                remaining[0]--;
                                if (remaining[0] == 0 && !failed[0]) {
                                    Log.d(TAG, "All product details fetched, returning " + products.size() + " products");
                                    callback.onSuccess(products, total);
                                }
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.w(TAG, "Could not fetch detail for product " + productId + ": " + errorMessage);
                            synchronized (remaining) {
                                remaining[0]--;
                                if (remaining[0] == 0 && !failed[0]) {
                                    callback.onSuccess(products, total);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    private void handleListResponse(Response<ProductResponse> response, int offset,
                                    ProductCallback callback, String ctx) {
        if (response.isSuccessful() && response.body() != null) {
            ProductResponse pr = response.body();
            if (pr.isSuccess() && pr.getData() != null) {
                List<Product> products = pr.getData();
                int total = pr.getTotalCount();
                Log.d(TAG, "✓ Fetched " + products.size() + " products [" + ctx + "] total=" + total);
                if (offset == 0) {
                    cachedProducts.clear();
                    cachedProducts.addAll(products);
                }
                callback.onSuccess(products, total);
            } else {
                callback.onError("API non-success: " + pr.getStatus());
            }
        } else {
            callback.onError("HTTP " + response.code() + ": " + response.message());
        }
    }

    private void handleFailure(Throwable t, ProductCallback callback) {
        String msg = "Network error: " + t.getMessage();
        Log.e(TAG, msg, t);
        callback.onError(msg);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cache management
    // ─────────────────────────────────────────────────────────────────────────

    public void clearCache() {
        cachedProducts.clear();
        detailCache.clear();
        Log.d(TAG, "Cache cleared");
    }

    public List<Product> getCachedProducts() {
        return new ArrayList<>(cachedProducts);
    }

    @Nullable
    public ProductDetail getCachedDetail(String productId) {
        return detailCache.get(productId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Callback interfaces
    // ─────────────────────────────────────────────────────────────────────────

    public interface ProductCallback {
        void onSuccess(List<Product> products, int total);
        void onError(String errorMessage);
    }

    public interface SingleProductCallback {
        void onSuccess(ProductDetail detail);
        void onError(String errorMessage);
    }
}
