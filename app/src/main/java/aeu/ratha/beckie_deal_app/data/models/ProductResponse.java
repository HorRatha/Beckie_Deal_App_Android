package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * API response wrapper for product list endpoints.
 * Matches the structure: { "status": "success", "message": "", "data": [...] }
 */
public class ProductResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<Product> data;
    
    @SerializedName("pagination")
    private Pagination pagination;
    
    public ProductResponse() {}
    
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
    
    public List<Product> getData() {
        return data;
    }
    
    public void setData(List<Product> data) {
        this.data = data;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
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
        return pagination != null ? pagination.getTotal() : (data != null ? data.size() : 0);
    }
}
