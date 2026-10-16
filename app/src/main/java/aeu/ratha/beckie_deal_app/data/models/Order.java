package aeu.ratha.beckie_deal_app.data.models;

import java.util.List;

public class Order {
    public long id;
    public String order_number;
    public String status;
    public long total;
    public long subtotal;
    public long discount_total;
    public String created_at;
    public String expected_delivery_date;
    public String payment_status;
    public String payment_method;
    public String shipping_method;
    public Object shipping_address;
    public List<OrderItem> items;
}
