package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Represents a product variant (e.g. 16GB/1TB/Eclipse Gray).
 * Returned by GET /products/{id} under the "variants" array.
 */
public class ProductVariant {

    @SerializedName("id")
    private int id;

    @SerializedName("product_id")
    private String productId;   // UUID of the parent product

    @SerializedName("name")
    private String name;        // e.g. "16GB / 1TB / Eclipse Gray"

    @SerializedName("slug")
    private String slug;

    @SerializedName("sku")
    private String sku;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("price")
    private String price;

    @SerializedName("sale_price")
    private String salePrice;

    @SerializedName("stock_quantity")
    private int stockQuantity;

    @SerializedName("in_stock")
    private boolean inStock;

    @SerializedName("track_inventory")
    private boolean trackInventory;

    @SerializedName("weight")
    private String weight;

    @SerializedName("length")
    private String length;

    @SerializedName("width")
    private String width;

    @SerializedName("height")
    private String height;

    @SerializedName("image")
    private String image;

    /** Key-value attribute map, e.g. {"ram":"16GB","storage":"1TB","color":"Eclipse Gray"} */
    @SerializedName("attributes")
    private Map<String, String> attributes;

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("sort_order")
    private int sortOrder;

    public ProductVariant() {}

    // ── Getters ────────────────────────────────────────────────────────────

    public int getId() { return id; }
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getSku() { return sku; }
    public String getBarcode() { return barcode; }
    public String getPrice() { return price; }
    public String getSalePrice() { return salePrice; }
    public int getStockQuantity() { return stockQuantity; }
    public boolean isInStock() { return inStock; }
    public boolean isTrackInventory() { return trackInventory; }
    public String getWeight() { return weight; }
    public String getLength() { return length; }
    public String getWidth() { return width; }
    public String getHeight() { return height; }
    public String getImage() { return image; }
    public Map<String, String> getAttributes() { return attributes; }
    public boolean isDefault() { return isDefault; }
    public boolean isActive() { return isActive; }
    public int getSortOrder() { return sortOrder; }

    /** Helper: get a formatted attribute string like "16GB · 1TB · Eclipse Gray". */
    public String getAttributeSummary() {
        if (attributes == null || attributes.isEmpty()) return name != null ? name : "";
        StringBuilder sb = new StringBuilder();
        for (String value : attributes.values()) {
            if (sb.length() > 0) sb.append(" · ");
            sb.append(value);
        }
        return sb.toString();
    }

    public double getPriceAsDouble() {
        try { return Double.parseDouble(price); } catch (Exception e) { return 0.0; }
    }

    public double getSalePriceAsDouble() {
        if (salePrice == null || salePrice.isEmpty()) return 0.0;
        try { return Double.parseDouble(salePrice); } catch (Exception e) { return 0.0; }
    }
}
