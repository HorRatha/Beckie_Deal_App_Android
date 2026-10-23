package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Brand model representing a product brand.
 * Maps to the brand data structure from the API.
 */
public class Brand {
    
    @SerializedName("id")
    private int id;  // Integer from API (not UUID)
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("slug")
    private String slug;
    
    @SerializedName("logo")
    private String logo;
    
    @SerializedName("website")
    private String website;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("meta_title")
    private String metaTitle;
    
    @SerializedName("meta_description")
    private String metaDescription;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("is_featured")
    private boolean isFeatured;
    
    @SerializedName("sort_order")
    private int sortOrder;
    
    @SerializedName("products_count")
    private int productsCount;
    
    // Default constructor
    public Brand() {
    }
    
    // Constructor with essential fields
    public Brand(int id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public String getLogo() {
        return logo;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getMetaTitle() {
        return metaTitle;
    }
    
    public String getMetaDescription() {
        return metaDescription;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public boolean isFeatured() {
        return isFeatured;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public int getProductsCount() {
        return productsCount;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }
    
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public void setProductsCount(int productsCount) {
        this.productsCount = productsCount;
    }
    
    /**
     * Get the drawable resource ID for this brand logo.
     * Maps brand slugs to their webp images in res/drawable.
     * Falls back to placeholder if image not available.
     */
    public int getLogoResourceId() {
        if (slug == null) return 0;
        
        // Direct mapping - much more reliable than reflection
        switch (slug.toLowerCase()) {
            case "acer":
                return aeu.ratha.beckie_deal_app.R.drawable.acer;
            case "amd":
                return aeu.ratha.beckie_deal_app.R.drawable.amd;
            case "anker":
                return aeu.ratha.beckie_deal_app.R.drawable.anker;
            case "apple":
                return aeu.ratha.beckie_deal_app.R.drawable.apple;
            case "asrock":
                return aeu.ratha.beckie_deal_app.R.drawable.asrock;
            case "asus":
                return aeu.ratha.beckie_deal_app.R.drawable.asus;
            case "bose":
                return aeu.ratha.beckie_deal_app.R.drawable.bose;
            case "cooler-master":
                return aeu.ratha.beckie_deal_app.R.drawable.cooler_master;
            case "corsair":
                return aeu.ratha.beckie_deal_app.R.drawable.corsair;
            case "dell":
                return aeu.ratha.beckie_deal_app.R.drawable.dell;
            case "elgato":
                return aeu.ratha.beckie_deal_app.R.drawable.elgato;
            case "gigabyte":
                return aeu.ratha.beckie_deal_app.R.drawable.gigabyte;
            case "hp":
                return aeu.ratha.beckie_deal_app.R.drawable.hp;
            case "intel":
                return aeu.ratha.beckie_deal_app.R.drawable.intel;
            case "jbl":
                return aeu.ratha.beckie_deal_app.R.drawable.jbl;
            case "kingston":
                return aeu.ratha.beckie_deal_app.R.drawable.kingston;
            case "lenovo":
                return aeu.ratha.beckie_deal_app.R.drawable.lenovo;
            case "lian-li":
                return aeu.ratha.beckie_deal_app.R.drawable.lian_li;
            case "logitech":
                return aeu.ratha.beckie_deal_app.R.drawable.logitech;
            case "msi":
                return aeu.ratha.beckie_deal_app.R.drawable.msi_official_red_glossy;
            case "nvidia":
                return aeu.ratha.beckie_deal_app.R.drawable.nvidia;
            case "nzxt":
                return aeu.ratha.beckie_deal_app.R.drawable.nzxt;
            case "razer":
                return aeu.ratha.beckie_deal_app.R.drawable.razer;
            case "samsung":
                return aeu.ratha.beckie_deal_app.R.drawable.samsung;
            case "sandisk":
                return aeu.ratha.beckie_deal_app.R.drawable.sandisk;
            case "seagate":
                return aeu.ratha.beckie_deal_app.R.drawable.seagate;
            case "sonos":
                return aeu.ratha.beckie_deal_app.R.drawable.sonos;
            case "sony":
                return aeu.ratha.beckie_deal_app.R.drawable.sony;
            default:
                return 0;  // Return 0 for unknown brands (instead of placeholder)
        }
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", logo='" + logo + '\'' +
                ", website='" + website + '\'' +
                ", isActive=" + isActive +
                ", isFeatured=" + isFeatured +
                ", productsCount=" + productsCount +
                '}';
    }
}
