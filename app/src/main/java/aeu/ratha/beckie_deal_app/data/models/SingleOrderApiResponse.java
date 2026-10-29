package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

public class SingleOrderApiResponse {
    @SerializedName("status")
    public String status;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public ApiOrder data;
}
