package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response wrapper for category API calls.
 * Contains status, message, data list and pagination info.
 */
public class CategoryResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private CategoryData data;
    
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
    
    public CategoryData getData() {
        return data;
    }
    
    public void setData(CategoryData data) {
        this.data = data;
    }
    
    /**
     * Nested data class containing the list and pagination
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
