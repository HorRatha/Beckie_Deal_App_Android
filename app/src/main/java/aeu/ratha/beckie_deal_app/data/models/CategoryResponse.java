package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response wrapper for category API calls.
 * Handles both paginated and direct list responses from the API.
 * 
 * API Response Format:
 * {
 *   "status": "success",
 *   "message": "",
 *   "data": [ {Category}, {Category}, ... ]  ← Direct array of categories
 * }
 */
public class CategoryResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<Category> data;  // Changed from CategoryData to List<Category>
    
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
    
    public List<Category> getData() {
        return data;
    }
    
    public void setData(List<Category> data) {
        this.data = data;
    }
    
    /**
     * Safely get categories from the response.
     * Handles both direct array and nested structure.
     */
    public List<Category> getCategories() {
        return data;
    }
    
    /**
     * Nested data class - kept for potential backward compatibility
     * if API structure changes in the future.
     */
    public static class CategoryData {
        @SerializedName("data")
        private List<Category> categories;
        
        @SerializedName("pagination")
        private Pagination pagination;
        
        public List<Category> getCategories() {
            return categories;
        }
        
        public void setCategories(List<Category> categories) {
            this.categories = categories;
        }
        
        public Pagination getPagination() {
            return pagination;
        }
        
        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
    }
}
