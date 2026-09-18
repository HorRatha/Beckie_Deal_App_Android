package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Response wrapper for promotions list endpoint.
 * Endpoint: GET /api/v1/promotions
 */
public class PromotionResponse {
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<Promotion> data;
    
    public PromotionResponse() {}
    
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Promotion> getData() { return data; }
    
    public boolean isSuccess() {
        return "success".equals(status);
    }
    
    public int getCount() {
        return data != null ? data.size() : 0;
    }
}
