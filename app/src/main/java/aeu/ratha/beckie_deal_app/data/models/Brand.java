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
