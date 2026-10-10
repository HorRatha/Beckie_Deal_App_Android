package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * API response wrapper for /api/v1/slides endpoint
 */
public class SlideResponse {
    
    @SerializedName("status")
    private String status;  // "success" or "error"
    
    @SerializedName("message")
    private String message;  // Error message or empty
    
    @SerializedName("data")
    private List<Slide> data;
    
    public SlideResponse() {}
    
    // ── Getters ────────────────────────────────────────────────────────────
    
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Slide> getData() { return data; }
    
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }
    
    public int getCount() {
        return data != null ? data.size() : 0;
    }
    
    // ── Setters ────────────────────────────────────────────────────────────
    
    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setData(List<Slide> data) { this.data = data; }
}
