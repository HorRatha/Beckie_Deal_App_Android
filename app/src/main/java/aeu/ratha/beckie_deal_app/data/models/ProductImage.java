package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a product image entry.
 * Returned by GET /products/{id} under the "images" array.
 */
public class ProductImage {

    @SerializedName("id")
    private int id;

    @SerializedName("product_id")
    private String productId;   // UUID of parent product

    @SerializedName("image")
    private String image;       // image path/URL stored in DB

    @SerializedName("url")
    private String url;         // full resolved URL (use this for loading)

    @SerializedName("alt_text")
    private String altText;

    @SerializedName("title")
    private String title;

    /** "thumbnail", "gallery", etc. */
    @SerializedName("type")
    private String type;

    @SerializedName("is_primary")
    private boolean isPrimary;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("sort_order")
    private int sortOrder;

    public ProductImage() {}

    // ── Getters ────────────────────────────────────────────────────────────

    public int getId() { return id; }
    public String getProductId() { return productId; }
    public String getImage() { return image; }
    /** Returns the best URL to use for loading the image. Falls back to image field. */
    public String getUrl() { return url != null && !url.isEmpty() ? url : image; }
    public String getAltText() { return altText; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public boolean isPrimary() { return isPrimary; }
    public boolean isActive() { return isActive; }
    public int getSortOrder() { return sortOrder; }
}
