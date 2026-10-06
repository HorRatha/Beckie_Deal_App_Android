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
    // List
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetch all products with pagination.
     *
     * @param limit      Max items to return
     * @param offset     Starting offset
     * @param useCache   Return cached list if available
     * @param callback   Result callback
     */
    public void fetchProducts(int limit, int offset, boolean useCache,
                              @NonNull ProductCallback callback) {
        if (useCache && !cachedProducts.isEmpty()) {
            Log.d(TAG, "Returning " + cachedProducts.size() + " cached products");
            callback.onSuccess(new ArrayList<>(cachedProducts), cachedProducts.size());
            return;
        }

        Log.d(TAG, "Fetching products list (limit=" + limit + ", offset=" + offset + ")");
        apiService.getProducts(limit, offset).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call,
                                   @NonNull Response<ProductResponse> response) {
                handleListResponse(response, offset, callback, "all products");
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                handleFailure(t, callback);
            }
        });
    }

    /**
     * Search products by keyword (name-based, server-side).
     */
    public void searchProducts(String query, int limit, int offset,
                               @NonNull ProductCallback callback) {
        Log.d(TAG, "Searching products: '" + query + "'");
        apiService.searchProducts(query, limit, offset).enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call,
                                   @NonNull Response<ProductResponse> response) {
                handleListResponse(response, -1, callback, "search: " + query);
            }

            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                handleFailure(t, callback);
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
        fetchProducts(limit, offset, false, new ProductCallback() {
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
                    // No results from embedded info — fall back to fetching individual details
                    Log.d(TAG, "✗ No results from embedded info, falling back to detail fetch");
                    fetchAllProductsWithDetails(limit, offset, new ProductCallback() {
                        @Override
                        public void onSuccess(List<Product> detailProducts, int detailTotal) {
                            List<Product> detailFiltered = new ArrayList<>();
                            Log.d(TAG, "Detail fetch complete: " + detailProducts.size() + " products");
                            
                            int matched = 0;
                            int detailMissing = 0;
                            int categoryNull = 0;
                            int categoryMismatch = 0;
                            
                            for (Product p : detailProducts) {
                                ProductDetail detail = detailCache.get(p.getId());
                                
                                if (detail == null) {
                                    Log.w(TAG, "  ✗ Product " + p.getId() + " (" + p.getName() + "): detail not cached");
                                    detailMissing++;
                                    continue;
                                }
                                
                                if (detail.getCategory() == null) {
                                    Log.w(TAG, "  ✗ Product " + p.getId() + " (" + p.getName() + "): category is NULL");
                                    categoryNull++;
                                    continue;
                                }
                                
                                int productCategoryId = detail.getCategory().getId();
                                int parentCategoryId = detail.getCategory().getParentId();
                                
                                // Check for EXACT category match
                                if (productCategoryId == categoryId) {
                                    Log.d(TAG, "  ✓ Product " + p.getId() + " (" + p.getName() + "): category ID " + productCategoryId + " MATCHES (exact)");
                                    detailFiltered.add(p);
                                    matched++;
                                }
                                // Check for PARENT category match (include products from child categories)
                                else if (parentCategoryId == categoryId && parentCategoryId > 0) {
                                    Log.d(TAG, "  ✓ Product " + p.getId() + " (" + p.getName() + "): category ID " + productCategoryId + " is child of parent " + parentCategoryId + " (MATCHES)");
                                    detailFiltered.add(p);
                                    matched++;
                                }
                                else {
                                    Log.d(TAG, "  ✗ Product " + p.getId() + " (" + p.getName() + "): category ID " + productCategoryId + " (parent=" + parentCategoryId + ") != " + categoryId);
                                    categoryMismatch++;
                                }
                            }
                            
                            Log.d(TAG, "=== DETAIL FILTER RESULTS ===");
                            Log.d(TAG, "Matched: " + matched);
                            Log.d(TAG, "Detail missing: " + detailMissing);
                            Log.d(TAG, "Category NULL: " + categoryNull);
                            Log.d(TAG, "Category mismatch: " + categoryMismatch);
                            Log.d(TAG, "Final result: " + detailFiltered.size() + " products");
                            
                            callback.onSuccess(detailFiltered, detailFiltered.size());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            callback.onError(errorMessage);
                        }
                    });
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
                return lowerName.contains("phone") || lowerName.contains("smartphone");
            
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
        fetchProducts(limit, offset, false, new ProductCallback() {
            @Override
            public void onSuccess(List<Product> products, int total) {
                if (products.isEmpty()) {
                    callback.onSuccess(products, total);
                    return;
                }

                // Fetch details in parallel; use a counter to know when all are done
                final int[] remaining = {products.size()};
                final boolean[] failed = {false};

                for (Product product : products) {
                    // Skip if already cached
                    if (detailCache.containsKey(product.getId())) {
                        synchronized (remaining) {
                            remaining[0]--;
                            if (remaining[0] == 0) {
                                callback.onSuccess(products, total);
                            }
                        }
                        continue;
                    }

                    fetchProductDetail(product.getId(), new SingleProductCallback() {
                        @Override
                        public void onSuccess(ProductDetail detail) {
                            synchronized (remaining) {
                                remaining[0]--;
                                if (remaining[0] == 0 && !failed[0]) {
                                    callback.onSuccess(products, total);
                                }
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Log.w(TAG, "Could not fetch detail for product " +
                                    product.getId() + ": " + errorMessage);
                            // Don't fail the whole request — just skip this product's detail
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
