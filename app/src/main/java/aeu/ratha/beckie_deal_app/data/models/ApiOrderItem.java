package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import aeu.ratha.beckie_deal_app.utils.helpers.ImageUrlHelper;

public class ApiOrderItem implements Serializable {
    @SerializedName("id")
    public long id;

    @SerializedName("product_id")
    public String productId;

    @SerializedName("product_variant_id")
    public Object productVariantId;

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("unit_price")
    public String unitPrice;

    @SerializedName("sale_price")
    public String salePrice;

    @SerializedName("cost_price")
    public String costPrice;

    @SerializedName("subtotal")
    public String subtotal;

    @SerializedName("discount")
    public String discount;

    @SerializedName("tax")
    public String tax;

    @SerializedName("total")
    public String total;

    @SerializedName("product_name")
    public String productName;

    @SerializedName("product_sku")
    public String productSku;

    @SerializedName("variant_name")
    public String variantName;

    @SerializedName("product_thumbnail")
    public String productThumbnail;

    @SerializedName("product_image")
    public String productImage;

    @SerializedName("image_url")
    public String imageUrl;

    public String getDisplayImageUrl() {
        if (productThumbnail != null && !productThumbnail.trim().isEmpty()) {
            return ImageUrlHelper.getImageUrlOrFallback(productThumbnail);
        }
        if (productImage != null && !productImage.trim().isEmpty()) {
            return ImageUrlHelper.getImageUrlOrFallback(productImage);
        }
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            return ImageUrlHelper.getImageUrlOrFallback(imageUrl);
        }
        return ImageUrlHelper.getFallbackImageUrl();
    }

    public double getTotalPriceDouble() {
        if (total != null) {
            try {
                return Double.parseDouble(total.replace("$", "").replace(",", "").trim());
            } catch (Exception ignored) {}
        }
        if (unitPrice != null) {
            try {
                double u = Double.parseDouble(unitPrice.replace("$", "").replace(",", "").trim());
                return u * (quantity > 0 ? quantity : 1);
            } catch (Exception ignored) {}
        }
        return 0.0;
    }
}
