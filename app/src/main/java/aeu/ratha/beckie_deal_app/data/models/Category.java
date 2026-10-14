package aeu.ratha.beckie_deal_app.data.models;

import com.google.gson.annotations.SerializedName;

/**
 * Category model representing a product category.
 * Maps to the category data structure from the API.
 */
public class Category {
    
    @SerializedName("id")
    private int id;  // Integer from API (not UUID)
    
    @SerializedName("parent_id")
    private int parentId;  // Integer from API (0 means no parent)
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("slug")
    private String slug;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("image")
    private String image;
    
    @SerializedName("icon")
    private String icon;
    
    @SerializedName("is_active")
    private boolean isActive;
    
    @SerializedName("is_featured")
    private boolean isFeatured;
    
    @SerializedName("sort_order")
    private int sortOrder;
    
    @SerializedName("products_count")
    private int productsCount;
    
    @SerializedName("children")
    private java.util.List<Category> children;
    
    // Default constructor
    public Category() {
    }
    
    // Constructor with essential fields
    public Category(int id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }
    
    // Getters
    public int getId() {
        return id;
    }

    /** Returns true if this is a top-level (root) category. */
    public boolean isRootCategory() {
        return parentId == 0;
    }
    
    public int getParentId() {
        return parentId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getImage() {
        return image;
    }
    
    public String getIcon() {
        return icon;
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
    
    public java.util.List<Category> getChildren() {
        return children;
    }
    
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }
    
    public int getChildrenCount() {
        return children != null ? children.size() : 0;
    }
    
    // Setters
    public void setId(int id) {
        this.id = id;
    }
    
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
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
    
    public void setChildren(java.util.List<Category> children) {
        this.children = children;
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                ", image='" + image + '\'' +
                ", icon='" + icon + '\'' +
                ", isActive=" + isActive +
                ", isFeatured=" + isFeatured +
                ", sortOrder=" + sortOrder +
                ", productsCount=" + productsCount +
                ", childrenCount=" + getChildrenCount() +
                '}';
    }
}
