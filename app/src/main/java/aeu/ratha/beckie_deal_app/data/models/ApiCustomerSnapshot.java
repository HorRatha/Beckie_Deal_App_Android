package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ApiCustomerSnapshot implements Serializable {
    @SerializedName("user_id")
    public long userId;

    @SerializedName("email")
    public String email;

    @SerializedName("phone")
    public String phone;
}
