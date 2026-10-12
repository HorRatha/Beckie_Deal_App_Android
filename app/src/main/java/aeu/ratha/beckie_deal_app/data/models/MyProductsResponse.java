package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * API response wrapper for my-products (order/purchase list) endpoints.
 * Matches the structure: { "status": "success", "message": "", "data": { "items": [...], "pagination": {...} } }
 */
public class MyProductsResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private MyProductsData data;
    
    public MyProductsResponse() {}
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public MyProductsData getData() {
        return data;
    }
    
    public void setData(MyProductsData data) {
        this.data = data;
    }
    
    /**
     * Check if the API response was successful.
     */
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
    
    /**
     * Get total count from pagination if available.
     */
    public int getTotalCount() {
        return data != null && data.getPagination() != null ? 
            data.getPagination().getTotal() : 0;
    }
    
    /**
     * Nested data object containing items and pagination.
     */
    public static class MyProductsData {
        
        @SerializedName("items")
        private List<Product> items;
        
        @SerializedName("pagination")
        private Pagination pagination;
        
        public MyProductsData() {}
        
        public List<Product> getItems() {
            return items;
        }
        
        public void setItems(List<Product> items) {
            this.items = items;
        }
        
        public Pagination getPagination() {
            return pagination;
        }
        
        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }
}
