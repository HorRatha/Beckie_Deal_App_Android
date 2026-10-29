package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ApiMeta implements Serializable {
    @SerializedName("current_page")
    public int currentPage;

    @SerializedName("from")
    public int from;

    @SerializedName("last_page")
    public int lastPage;

    @SerializedName("per_page")
    public int perPage;

    @SerializedName("to")
    public int to;

    @SerializedName("total")
    public int total;
}
