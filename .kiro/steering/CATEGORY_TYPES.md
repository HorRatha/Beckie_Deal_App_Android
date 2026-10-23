---
inclusion: manual
name: "Category Types & Classification"
description: "Guide for category type classification and filtering"
---

# Category Type Classification System

## Overview

The app now has a **category classification system** that categorizes products into curated types for better organization and filtering.

## Category Types

| Type | Display Name | Product Examples |
|------|---|---|
| `accessories` | Accessories | Keyboard, Mouse, Headphones, Router, USB Hub, Cables, Stands, Chargers |
| `laptop` | Laptops | General laptops (ASUS VivoBook, Dell XPS, HP Pavilion) |
| `mac` | Mac & Apple | MacBook Pro, MacBook Air, iMac |
| `gaming` | Gaming Laptops | Gaming laptops (ROG, MSI RTX, Razer Blade) |
| `storage_network` | Storage & Network | SSDs, HDDs, RAM, Network Cards, Routers |

## How It Works

### Classification Flow

```
Category Name (from API)
    ↓
CategoryClassifier.classifyCategory()
    ↓
Category Type String
    ↓
Frontend Filtering / Display Logic
```

### Classification Priority

The classifier uses a priority order to ensure accurate classification:

1. **Gaming** - Matches "gaming laptop", "rog", "msi gaming"
2. **Mac** - Matches "mac", "macbook", "apple", "ipad"
3. **Storage & Network** - Matches storage, network, ssd, hdd, ram, router
4. **Accessories** - Matches keyboard, mouse, headphone, speaker, charger, cable
5. **Laptop** - General laptops and computer products

## Usage Examples

### Get Category Type

```java
import aeu.ratha.beckie_deal_app.utils.CategoryClassifier;
import aeu.ratha.beckie_deal_app.data.models.Category;

Category category = getCategory();
String type = CategoryClassifier.classifyCategory(category);
// type = "laptop", "gaming", "accessories", etc.
```

### Filter Products by Type

```java
import aeu.ratha.beckie_deal_app.utils.CategoryTypeFilter;
import aeu.ratha.beckie_deal_app.ui.activities.main.MainActivity;

List<MainActivity.Product> allProducts = getProducts();
List<MainActivity.Product> accessories = 
    CategoryTypeFilter.filterByType(allProducts, "accessories");
```

### Filter by Type + Subcategory

```java
List<MainActivity.Product> keyboards = 
    CategoryTypeFilter.filterByTypeAndSubcategory(
        allProducts, 
        "accessories", 
        "Keyboard"
    );
```

### Filter by Type + Brand

```java
List<MainActivity.Product> gamingASUS = 
    CategoryTypeFilter.filterByTypeAndBrand(
        allProducts,
        "gaming",
        "ASUS"
    );
```

### Get Recommended Products

```java
List<MainActivity.Product> topAccessories = 
    CategoryTypeFilter.getRecommendedByType(
        allProducts,
        "accessories",
        5  // limit
    );
```

### Group Products by Type

```java
Map<String, List<MainActivity.Product>> grouped = 
    CategoryTypeFilter.groupByType(allProducts);

List<MainActivity.Product> laptops = grouped.get("laptop");
List<MainActivity.Product> gaming = grouped.get("gaming");
List<MainActivity.Product> accessories = grouped.get("accessories");
```

### Count Products by Type

```java
int accessoryCount = CategoryTypeFilter.countByType(
    allProducts, 
    "accessories"
);
```

## Subcategories

Each type has specific subcategories for filtering:

### Accessories Subcategories
- Keyboard
- Mouse
- Headphones
- Speakers
- Router
- Cables & Adapters
- Stands & Docks
- Chargers

### Laptop Subcategories
- Budget
- Mid-Range
- Premium
- Ultrabook

### Mac Subcategories
- MacBook Pro
- MacBook Air
- iMac
- Mac Accessories

### Gaming Subcategories
- Entry Level
- Mid Range
- High End

### Storage & Network Subcategories
- SSDs
- HDDs
- RAM
- Networking
- Storage Devices

## Get Subcategories

```java
List<String> subcats = CategoryClassifier.getSubcategoriesForType("accessories");
// Returns: ["Keyboard", "Mouse", "Headphones", "Speakers", ...]
```

## Display Names & Icons

### Get Display Name

```java
String displayName = CategoryClassifier.getDisplayName("gaming");
// Returns: "Gaming Laptops"
```

### Get Icon Name

```java
String iconName = CategoryClassifier.getIconName("accessories");
// Returns: "ic_accessories"
// Map this to your drawable resources
```

## Hero Content Integration

The `HeroContentManager` now uses `CategoryClassifier` for reliable hero content mapping:

```java
HeroContentManager heroManager = new HeroContentManager(context);
Pair<String, String> heroContent = 
    heroManager.getHeroContentForCategory("Accessories");
// Returns: (title, description)
```

Benefits:
- ✓ Consistent classification
- ✓ No fragile string matching
- ✓ Easy to maintain and extend

## Implementation in Activities

### CategoryDetailActivity Example

```java
// Get category type
String categoryType = CategoryClassifier.classifyCategory(currentCategory);

// Filter products by type
List<Product> typeProducts = CategoryTypeFilter.filterByType(
    allProducts, 
    categoryType
);

// Get subcategories for this type
List<String> subcategories = 
    CategoryClassifier.getSubcategoriesForType(categoryType);

// Setup UI with type-specific subcategory filters
setupSubcategoryFilters(subcategories);
```

## Adding New Types

To add a new category type:

1. Add constant to `CategoryClassifier`:
   ```java
   private static final String TYPE_NEW = "new_type";
   ```

2. Update `classifyByName()` to detect it:
   ```java
   if (lower.contains("new_keyword")) {
       return TYPE_NEW;
   }
   ```

3. Add to `getAllTypes()`:
   ```java
   types.add(TYPE_NEW);
   ```

4. Add subcategories in `getSubcategoriesForType()`:
   ```java
   case TYPE_NEW:
       subcategories.add("Subcat1");
       subcategories.add("Subcat2");
       break;
   ```

5. Update `getDisplayName()` and `getIconName()` if needed

## Best Practices

### ✓ DO:
- Use `CategoryTypeFilter` for product filtering
- Use `CategoryClassifier.getSubcategoriesForType()` for UI setup
- Use `CategoryClassifier.getDisplayName()` for labels
- Store type in UI state (don't recalculate each time)
- Group products by type for section headers

### ✗ DON'T:
- Use string matching directly on category names
- Mix classification logic in multiple places
- Hardcode type strings (use constants)
- Forget to handle null products/categories

## Testing

### Test Classification

```java
Category cat = new Category();
cat.setName("Gaming Laptop");
String type = CategoryClassifier.classifyCategory(cat);
assertEquals("gaming", type);  // Should pass

cat.setName("Wireless Mouse");
type = CategoryClassifier.classifyCategory(cat);
assertEquals("accessories", type);  // Should pass
```

### Test Filtering

```java
List<MainActivity.Product> accessories = 
    CategoryTypeFilter.filterByType(products, "accessories");
assertTrue(accessories.stream()
    .allMatch(p -> CategoryClassifier.classifyCategory(
        new MockCategory(p.categoryName)).equals("accessories")));
```

## Related Files

- `CategoryClassifier.java` - Core classification logic
- `CategoryTypeFilter.java` - Product filtering utilities
- `HeroContentManager.java` - Updated hero content mapping
- `Category.java` - Category model
- `CategoryDetailActivity.java` - Uses type classification for product display

## Future Enhancements

### Phase 2: Backend Integration
- Add `category_type` field to API response
- Backend provides pre-classified types
- Remove frontend classification when available

### Phase 3: Advanced Filtering
- Type-specific price ranges
- Type-specific sorting
- Type-specific recommendations

### Phase 4: Analytics
- Track user views by category type
- Recommend based on browsing history
- Personalized category suggestions
