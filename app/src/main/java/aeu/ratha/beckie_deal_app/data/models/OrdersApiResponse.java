package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrdersApiResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public List<ApiOrder> data;

    @SerializedName("meta")
    public ApiMeta meta;
}
