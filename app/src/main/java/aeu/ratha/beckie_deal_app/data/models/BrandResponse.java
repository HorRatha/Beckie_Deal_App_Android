package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response model for brand API endpoints.
 * Wraps the list of brands and pagination information.
 */
public class BrandResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private BrandData data;
    
    // Default constructor
    public BrandResponse() {
    }
    
    // Getters
    public String getStatus() {
        return status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public BrandData getData() {
        return data;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setData(BrandData data) {
        this.data = data;
    }
    
    /**
     * Inner class representing the data wrapper containing brands list and pagination.
     */
    public static class BrandData {
        
        @SerializedName("data")
        private List<Brand> brands;
        
        @SerializedName("pagination")
        private Pagination pagination;
        
        // Default constructor
        public BrandData() {
        }
        
        // Getters
        public List<Brand> getBrands() {
            return brands;
        }
        
        public Pagination getPagination() {
            return pagination;
        }
        
        // Setters
        public void setBrands(List<Brand> brands) {
            this.brands = brands;
        }
        
        public void setPagination(Pagination pagination) {
            this.pagination = pagination;
        }
        
        @Override
        public String toString() {
            return "BrandData{" +
                    "brands=" + (brands != null ? brands.size() : 0) + " items" +
                    ", pagination=" + pagination +
                    '}';
        }
    }
    
    @Override
    public String toString() {
        return "BrandResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
