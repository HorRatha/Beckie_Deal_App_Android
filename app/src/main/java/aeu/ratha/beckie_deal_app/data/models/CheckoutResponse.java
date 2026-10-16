package aeu.ratha.beckie_deal_app.data.models;

public class CheckoutResponse {
    public String status;
    public String message;
    public CheckoutData data;
    
    public class CheckoutData {
        public long order_id;
        public String order_number;
        public String status;
        public long total;
        public String created_at;
    }
}
