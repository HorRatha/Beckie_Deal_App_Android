# Typography Usage Guide

## Overview
This project uses a custom typography system with **Amulya Bold** and **Synonym** font families.

## Typography Scale

| Element | Font | Size/Weight | Tracking | Style Name |
|---------|------|-------------|----------|------------|
| Screen title ("Laptops") | Amulya Bold | 28sp | -0.02em | `TextAppearance.App.ScreenTitle` |
| Product name (detail) | Amulya Bold | 24sp | -0.02em | `TextAppearance.App.ProductName` |
| Price (hero) | Amulya Bold | 22sp | -0.01em | `TextAppearance.App.PriceHero` |
| Section header ("Top Picks") | Amulya Bold | 18sp | -0.01em | `TextAppearance.App.SectionHeader` |
| Card product name | Synonym Medium | 15sp | 0 | `TextAppearance.App.CardProductName` |
| Card price | Synonym Semibold | 15sp | 0 | `TextAppearance.App.CardPrice` |
| Body / description | Synonym Regular | 15sp (1.6 line height) | 0 | `TextAppearance.App.Body` |
| Specs list | Synonym Regular | 14sp (1.5 line height) | 0 | `TextAppearance.App.Specs` |
| Labels, meta | Synonym Medium | 12sp | +0.04em | `TextAppearance.App.Label` (uppercase) |
| Button label | Synonym Semibold | 15sp | +0.01em | `TextAppearance.App.Button` |

## Usage in XML Layouts

### Screen Titles (e.g., Category Pages)
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Laptops"
    android:textAppearance="@style/TextAppearance.App.ScreenTitle" />
```

### Product Names (Detail Page)
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="MacBook Pro 14"
    android:textAppearance="@style/TextAppearance.App.ProductName" />
```

### Hero Prices
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="$1,999"
    android:textAppearance="@style/TextAppearance.App.PriceHero" />
```

### Section Headers
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Top Picks"
    android:textAppearance="@style/TextAppearance.App.SectionHeader" />
```

### Product Card Names
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Dell XPS 13"
    android:textAppearance="@style/TextAppearance.App.CardProductName" />
```

### Product Card Prices
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="$899"
    android:textAppearance="@style/TextAppearance.App.CardPrice" />
```

### Body Text / Descriptions
```xml
<TextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Experience powerful performance..."
    android:textAppearance="@style/TextAppearance.App.Body" />
```

### Specifications Lists
```xml
<TextView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="• Intel Core i7 processor\n• 16GB RAM\n• 512GB SSD"
    android:textAppearance="@style/TextAppearance.App.Specs" />
```

### Labels and Badges
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Free Shipping"
    android:textAppearance="@style/TextAppearance.App.Label" />
```

### Buttons
Buttons automatically use the correct typography when using the app theme:
```xml
<Button
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Add to Cart"
    style="@style/Widget.App.Button" />
```

## Usage in Code (Kotlin/Java)

### Setting text appearance programmatically (Kotlin)
```kotlin
// Screen title
textView.setTextAppearance(R.style.TextAppearance_App_ScreenTitle)

// Product name
productNameView.setTextAppearance(R.style.TextAppearance_App_ProductName)

// Card price
priceView.setTextAppearance(R.style.TextAppearance_App_CardPrice)
```

### Setting text appearance programmatically (Java)
```java
// Screen title
textView.setTextAppearance(R.style.TextAppearance_App_ScreenTitle);

// Product name
productNameView.setTextAppearance(R.style.TextAppearance_App_ProductName);

// Card price
priceView.setTextAppearance(R.style.TextAppearance_App_CardPrice);
```

## Helper Styles

For convenience, semantic aliases are provided:

- `TextAppearance.App.CategoryTitle` → same as ScreenTitle
- `TextAppearance.App.FeaturedSection` → same as SectionHeader
- `TextAppearance.App.ShippingBadge` → Label style with primary color
- `TextAppearance.App.ProductCardTitle` → same as CardProductName
- `TextAppearance.App.ProductCardPrice` → same as CardPrice
- `TextAppearance.App.ProductDescription` → same as Body
- `TextAppearance.App.ProductSpecs` → same as Specs

## Common Patterns

### Product Card Layout
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp">
        
        <ImageView
            android:layout_width="match_parent"
            android:layout_height="120dp"
            android:scaleType="centerCrop" />
        
        <!-- Product name: Synonym Medium 15sp -->
        <TextView
            android:id="@+id/productName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.CardProductName"
            android:maxLines="2"
            android:ellipsize="end"
            tools:text="MacBook Pro 14-inch" />
        
        <!-- Price: Synonym Semibold 15sp -->
        <TextView
            android:id="@+id/productPrice"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.CardPrice"
            android:layout_marginTop="4dp"
            tools:text="$1,999" />
        
        <!-- Free shipping badge: Synonym Medium 12sp uppercase -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Free Shipping"
            android:textAppearance="@style/TextAppearance.App.ShippingBadge"
            android:layout_marginTop="4dp" />
            
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### Product Detail Page
```xml
<ScrollView
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">
        
        <!-- Product name: Amulya Bold 24sp -->
        <TextView
            android:id="@+id/productTitle"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.ProductName"
            tools:text="MacBook Pro 14-inch" />
        
        <!-- Price: Amulya Bold 22sp -->
        <TextView
            android:id="@+id/productPrice"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.PriceHero"
            android:layout_marginTop="8dp"
            tools:text="$1,999" />
        
        <!-- Description section header: Amulya Bold 18sp -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Description"
            android:textAppearance="@style/TextAppearance.App.SectionHeader"
            android:layout_marginTop="24dp" />
        
        <!-- Description body: Synonym Regular 15sp, line height 1.6 -->
        <TextView
            android:id="@+id/productDescription"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Body"
            android:layout_marginTop="8dp"
            tools:text="Experience powerful performance with the new M2 Pro chip..." />
        
        <!-- Specs header: Amulya Bold 18sp -->
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Specifications"
            android:textAppearance="@style/TextAppearance.App.SectionHeader"
            android:layout_marginTop="24dp" />
        
        <!-- Specs list: Synonym Regular 14sp, line height 1.5 -->
        <TextView
            android:id="@+id/productSpecs"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textAppearance="@style/TextAppearance.App.Specs"
            android:layout_marginTop="8dp"
            tools:text="• M2 Pro chip\n• 16GB unified memory\n• 512GB SSD storage" />
        
        <!-- Add to cart button: Synonym Semibold 15sp -->
        <Button
            android:id="@+id/addToCartButton"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Add to Cart"
            style="@style/Widget.App.Button"
            android:layout_marginTop="24dp" />
            
    </LinearLayout>
</ScrollView>
```

## Notes

- All typography styles are defined in `res/values/typography.xml`
- Font files are located in `res/font/` directory
- The theme automatically applies these styles to Material3 components
- Line spacing is specified using `lineSpacingMultiplier` where 1.6 = 160% line height
- Letter spacing in Android uses em units (e.g., -0.02 = -2% of font size)
- Uppercase labels are automatically applied via `textAllCaps` attribute
