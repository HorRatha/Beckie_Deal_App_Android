package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Pagination model for paginated API responses.
 */
public class Pagination {
    
    @SerializedName("total")
    private int total;
    
    @SerializedName("limit")
    private int limit;
    
    @SerializedName("offset")
    private int offset;
    
    @SerializedName("count")
    private int count;
    
    // Default constructor
    public Pagination() {
    }
    
    public Pagination(int total, int limit, int offset, int count) {
        this.total = total;
        this.limit = limit;
        this.offset = offset;
        this.count = count;
    }
    
    // Getters
    public int getTotal() {
        return total;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public int getOffset() {
        return offset;
    }
    
    public int getCount() {
        return count;
    }
    
    // Setters
    public void setTotal(int total) {
        this.total = total;
    }
    
    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    @Override
    public String toString() {
        return "Pagination{" +
                "total=" + total +
                ", limit=" + limit +
                ", offset=" + offset +
                ", count=" + count +
                '}';
    }
}
