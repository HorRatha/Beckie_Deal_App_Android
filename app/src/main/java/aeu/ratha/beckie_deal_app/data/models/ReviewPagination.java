package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Pagination metadata for review listing responses.
 * Returned in both product reviews and user's reviews endpoints.
 */
public class ReviewPagination {
    
    @SerializedName("total")
    private int total;
    
    @SerializedName("per_page")
    private int perPage;
    
    @SerializedName("current_page")
    private int currentPage;
    
    @SerializedName("last_page")
    private int lastPage;
    
    @SerializedName("count")
    private int count;  // Number of items in current page
    
    // Constructors
    public ReviewPagination() {}
    
    public ReviewPagination(int total, int perPage, int currentPage, int lastPage, int count) {
        this.total = total;
        this.perPage = perPage;
        this.currentPage = currentPage;
        this.lastPage = lastPage;
        this.count = count;
    }
    
    // Getters
    public int getTotal() {
        return total;
    }
    
    public int getPerPage() {
        return perPage;
    }
    
    public int getCurrentPage() {
        return currentPage;
    }
    
    public int getLastPage() {
        return lastPage;
    }
    
    public int getCount() {
        return count;
    }
    
    // Setters
    public void setTotal(int total) {
        this.total = total;
    }
    
    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }
    
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    
    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    /**
     * Check if there are more pages to fetch.
     */
    public boolean hasNextPage() {
        return currentPage < lastPage;
    }
    
    /**
     * Get the next page number.
     */
    public int getNextPage() {
        return hasNextPage() ? currentPage + 1 : currentPage;
    }
}
