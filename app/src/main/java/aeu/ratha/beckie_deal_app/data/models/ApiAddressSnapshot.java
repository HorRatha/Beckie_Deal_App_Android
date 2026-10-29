package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ApiAddressSnapshot implements Serializable {
    @SerializedName("full_name")
    public String fullName;

    @SerializedName("recipient_name")
    public String recipientName;

    @SerializedName("phone")
    public String phone;

    @SerializedName("email")
    public String email;

    @SerializedName("address_line_1")
    public String addressLine1;

    @SerializedName("address_line_2")
    public String addressLine2;

    @SerializedName("city")
    public String city;

    @SerializedName("state")
    public String state;

    @SerializedName("postal_code")
    public String postalCode;

    @SerializedName("country")
    public String country;

    @SerializedName("delivery_note")
    public String deliveryNote;

    public String getRecipientName() {
        if (recipientName != null && !recipientName.trim().isEmpty()) {
            return recipientName;
        }
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        return "Valued Customer";
    }

    public String getFullFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null && !addressLine1.trim().isEmpty()) {
            sb.append(addressLine1);
        }
        if (addressLine2 != null && !addressLine2.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(addressLine2);
        }
        if (city != null && !city.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (state != null && !state.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (postalCode != null && !postalCode.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(postalCode);
        }
        if (country != null && !country.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(country);
        }
        return sb.length() > 0 ? sb.toString() : "Address not specified";
    }
}
