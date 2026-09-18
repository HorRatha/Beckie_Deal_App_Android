package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Full product model for the single-product detail endpoint.
 * GET /products/{uuid}
 *
 * Extends the basic product fields with:
 *  - category (full Category object)
 *  - brand (full Brand object)
 *  - variants (List<ProductVariant>)
 *  - images (List<ProductImage>)
 *  - reviews (included but not yet used in UI)
 *  - average_rating returned as a number (not string) on this endpoint
 */
public class ProductDetail {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("slug")
    private String slug;

    @SerializedName("sku")
    private String sku;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("short_description")
    private String shortDescription;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private String price;

    @SerializedName("sale_price")
    private String salePrice;

    @SerializedName("stock_quantity")
    private int stockQuantity;

    @SerializedName("min_stock_alert")
    private int minStockAlert;

    @SerializedName("track_inventory")
    private boolean trackInventory;

    @SerializedName("in_stock")
    private boolean inStock;

    @SerializedName("weight")
    private String weight;

    @SerializedName("length")
    private String length;

    @SerializedName("width")
    private String width;

    @SerializedName("height")
    private String height;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("meta_title")
    private String metaTitle;

    @SerializedName("meta_description")
    private String metaDescription;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("is_featured")
    private boolean isFeatured;

    @SerializedName("is_digital")
    private boolean isDigital;

    @SerializedName("views_count")
    private int viewsCount;

    @SerializedName("sales_count")
    private int salesCount;

    @SerializedName("sort_order")
    private int sortOrder;

    /** On the list endpoint this is a String "0"; on the detail endpoint it comes as a number. */
    @SerializedName("average_rating")
    private Object averageRating;

    // ── Relations (only returned by detail endpoint) ──────────────────────

    @SerializedName("category")
    private Category category;

    @SerializedName("brand")
    private Brand brand;

    @SerializedName("variants")
    private List<ProductVariant> variants;

    @SerializedName("images")
    private List<ProductImage> images;

    public ProductDetail() {}

    // ── Getters ────────────────────────────────────────────────────────────

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getSku() { return sku; }
    public String getBarcode() { return barcode; }
    public String getShortDescription() { return shortDescription; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getSalePrice() { return salePrice; }
    public int getStockQuantity() { return stockQuantity; }
    public int getMinStockAlert() { return minStockAlert; }
    public boolean isTrackInventory() { return trackInventory; }
    public boolean isInStock() { return inStock; }
    public String getWeight() { return weight; }
    public String getLength() { return length; }
    public String getWidth() { return width; }
    public String getHeight() { return height; }
    public String getThumbnail() { return thumbnail; }
    public String getMetaTitle() { return metaTitle; }
    public String getMetaDescription() { return metaDescription; }
    public boolean isActive() { return isActive; }
    public boolean isFeatured() { return isFeatured; }
    public boolean isDigital() { return isDigital; }
    public int getViewsCount() { return viewsCount; }
    public int getSalesCount() { return salesCount; }
    public int getSortOrder() { return sortOrder; }

    public float getAverageRatingAsFloat() {
        if (averageRating == null) return 0f;
        try { return Float.parseFloat(averageRating.toString()); }
        catch (NumberFormatException e) { return 0f; }
    }

    public Category getCategory() { return category; }
    public Brand getBrand() { return brand; }
    public List<ProductVariant> getVariants() { return variants; }
    public List<ProductImage> getImages() { return images; }

    // ── Helpers ────────────────────────────────────────────────────────────

    public double getPriceAsDouble() {
        try { return Double.parseDouble(price); } catch (Exception e) { return 0.0; }
    }

    public double getSalePriceAsDouble() {
        if (salePrice == null || salePrice.isEmpty()) return 0.0;
        try { return Double.parseDouble(salePrice); } catch (Exception e) { return 0.0; }
    }

    public double getEffectivePrice() {
        double sp = getSalePriceAsDouble();
        return sp > 0 ? sp : getPriceAsDouble();
    }

    public String getFormattedPrice() {
        return "$" + String.format("%.2f", getEffectivePrice());
    }

    public boolean hasDiscount() {
        return salePrice != null && !salePrice.isEmpty()
                && getSalePriceAsDouble() > 0
                && getSalePriceAsDouble() < getPriceAsDouble();
    }

    public int getDiscountPercentage() {
        if (!hasDiscount()) return 0;
        double regular = getPriceAsDouble();
        if (regular <= 0) return 0;
        return (int) Math.round(((regular - getSalePriceAsDouble()) / regular) * 100);
    }

    /**
     * Returns the best available description text.
     * Priority: description → short_description → meta_description → fallback.
     */
    public String getBestDescription() {
        if (description != null && !description.trim().isEmpty()) return description.trim();
        if (shortDescription != null && !shortDescription.trim().isEmpty()) return shortDescription.trim();
        if (metaDescription != null && !metaDescription.trim().isEmpty()) return metaDescription.trim();
        return "No description available.";
    }

    /**
     * Returns a list of gallery image URLs from the images array.
     * Falls back to the thumbnail if images is empty.
     */
    public java.util.List<String> getGalleryUrls() {
        java.util.List<String> urls = new java.util.ArrayList<>();
        if (images != null && !images.isEmpty()) {
            // Sort by sort_order; primary image first
            java.util.List<ProductImage> sorted = new java.util.ArrayList<>(images);
            sorted.sort((a, b) -> {
                if (a.isPrimary() && !b.isPrimary()) return -1;
                if (!a.isPrimary() && b.isPrimary()) return 1;
                return Integer.compare(a.getSortOrder(), b.getSortOrder());
            });
            for (ProductImage img : sorted) {
                String url = img.getUrl();
                if (url != null && !url.isEmpty()) urls.add(url);
            }
        }
        if (urls.isEmpty() && thumbnail != null && !thumbnail.isEmpty()) {
            urls.add(thumbnail);
        }
        return urls;
    }

    /** Returns the default variant, or null if no variants. */
    public ProductVariant getDefaultVariant() {
        if (variants == null || variants.isEmpty()) return null;
        for (ProductVariant v : variants) {
            if (v.isDefault()) return v;
        }
        return variants.get(0);
    }
}
