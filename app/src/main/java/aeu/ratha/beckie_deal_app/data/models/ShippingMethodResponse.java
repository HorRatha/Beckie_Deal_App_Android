package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Response wrapper for GET /api/v1/master/shipping-methods.
 * Supports both direct list format and paginated wrapper object format.
 */
@JsonAdapter(ShippingMethodResponse.Deserializer.class)
public class ShippingMethodResponse {

    private String status;
    private String message;
    private List<ShippingMethod> methods;
    private ShippingMethodData dataWrapper;

    public String getStatus() { return status; }
    public String getMessage() { return message; }

    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status);
    }

    public List<ShippingMethod> getMethods() {
        if (methods != null) return methods;
        if (dataWrapper != null && dataWrapper.getMethods() != null) {
            return dataWrapper.getMethods();
        }
        return null;
    }

    public ShippingMethodData getData() {
        return dataWrapper;
    }

    public static class ShippingMethodData {
        @SerializedName("data")
        private List<ShippingMethod> methods;

        @SerializedName("pagination")
        private Pagination pagination;

        public List<ShippingMethod> getMethods() { return methods; }
        public Pagination getPagination() { return pagination; }
    }

    public static class Deserializer implements JsonDeserializer<ShippingMethodResponse> {
        @Override
        public ShippingMethodResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            ShippingMethodResponse response = new ShippingMethodResponse();
            if (!json.isJsonObject()) return response;
            JsonObject obj = json.getAsJsonObject();

            if (obj.has("status") && !obj.get("status").isJsonNull()) {
                response.status = obj.get("status").getAsString();
            }
            if (obj.has("message") && !obj.get("message").isJsonNull()) {
                response.message = obj.get("message").getAsString();
            }

            if (obj.has("data") && !obj.get("data").isJsonNull()) {
                JsonElement dataElem = obj.get("data");
                if (dataElem.isJsonArray()) {
                    response.methods = context.deserialize(dataElem, new TypeToken<List<ShippingMethod>>(){}.getType());
                } else if (dataElem.isJsonObject()) {
                    response.dataWrapper = context.deserialize(dataElem, ShippingMethodData.class);
                }
            }
            return response;
        }
    }
}
