package aeu.ratha.beckie_deal_app.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper utility for handling page-based pagination from the API.
 *
 * The Beckie Deal API uses page-based pagination:
 *  - Query params: ?page={page}&per_page={perPage}
 *  - Response contains an array of items (no pagination metadata in response body)
 *  - To fetch all pages: keep incrementing page until response is empty or has fewer items than perPage
 *
 * USAGE EXAMPLE:
 *
 * PaginationHelper helper = new PaginationHelper(20); // 20 items per page
 *
 * // Fetch first page
 * apiService.getProducts("featured", 1, 20).enqueue(new Callback<ProductResponse>() {
 *     @Override
 *     public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
 *         List<Product> allProducts = new ArrayList<>();
 *         helper.addPageData(response.body().getData(), allProducts);
 *
 *         if (helper.hasMorePages()) {
 *             // Fetch next page
 *             int nextPage = helper.getNextPage();
 *             apiService.getProducts("featured", nextPage, 20).enqueue(...);
 *         } else {
 *             // All pages fetched
 *             callback.onSuccess(allProducts);
 *         }
 *     }
 * });
 */
public class PaginationHelper<T> {

    private static final String TAG = "PaginationHelper";

    private final int perPage;
    private int currentPage;
    private int totalItemsReceived;
    private boolean hasMorePages;

    /**
     * Create a pagination helper.
     *
     * @param perPage  Items per page (should match API request)
     */
    public PaginationHelper(int perPage) {
        this.perPage = perPage;
        this.currentPage = 0;  // Next page will be 1
        this.totalItemsReceived = 0;
        this.hasMorePages = true;
    }

    /**
     * Add data from a page response and determine if more pages exist.
     *
     * @param pageData  List of items from current page
     * @param accumulatedList  The list to accumulate all items into (optional, can pass null)
     * @return  true if there are more pages to fetch, false if this was the last page
     */
    public boolean addPageData(List<T> pageData, List<T> accumulatedList) {
        if (pageData == null) {
            pageData = new ArrayList<>();
        }

        int itemsInPage = pageData.size();
        Log.d(TAG, "Page " + (currentPage + 1) + ": received " + itemsInPage + " items");

        // Accumulate if list provided
        if (accumulatedList != null) {
            accumulatedList.addAll(pageData);
        }

        totalItemsReceived += itemsInPage;

        // Determine if more pages exist
        if (itemsInPage < perPage) {
            // Received fewer items than per_page means this is the last page
            this.hasMorePages = false;
            Log.d(TAG, "✓ Last page reached (received " + itemsInPage + " < " + perPage + ")");
        } else if (itemsInPage == 0) {
            // Empty page means no more data
            this.hasMorePages = false;
            Log.d(TAG, "✓ No more data (empty page)");
        } else {
            // Full page received, likely more data
            this.hasMorePages = true;
            currentPage++;
            Log.d(TAG, "→ More pages may exist, next page: " + (currentPage + 1));
        }

        return this.hasMorePages;
    }

    /**
     * Get the next page number to request.
     *
     * @return  Next page number (1-indexed)
     */
    public int getNextPage() {
        return currentPage + 1;
    }

    /**
     * Check if more pages are available.
     *
     * @return  true if more pages should be fetched
     */
    public boolean hasMorePages() {
        return hasMorePages;
    }

    /**
     * Get total items received so far across all pages.
     *
     * @return  Total count
     */
    public int getTotalItemsReceived() {
        return totalItemsReceived;
    }

    /**
     * Get current page number.
     *
     * @return  Current page (0 if no pages fetched yet)
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Reset the helper to fetch from the beginning.
     */
    public void reset() {
        this.currentPage = 0;
        this.totalItemsReceived = 0;
        this.hasMorePages = true;
        Log.d(TAG, "Helper reset");
    }
}
