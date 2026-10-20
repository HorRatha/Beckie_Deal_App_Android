# Beckie-Deal Database Schema Summary

## Quick Reference

### Entity Count: 12 Entities
- **✅ Implemented (4)**: User, Product, Category, Brand
- **⏳ Pending (8)**: Cart, Cart Item, Wishlist, Wishlist Item, Order, Order Item, Product Variant, Coupon

---

## Entity List with Status

| Entity | Status | Primary Key | API Status | Notes |
|--------|--------|-------------|------------|-------|
| **USER** | ✅ | UUID | Working | Auth endpoints functional |
| **PRODUCT** | ✅ | UUID | Working | 18 products available |
| **CATEGORY** | ✅ | UUID | Working | Hierarchical structure |
| **BRAND** | ✅ | UUID | Working | Master data available |
| **CART** | ⏳ | Integer | 404 | Using local CartManager |
| **CART_ITEM** | ⏳ | Integer | 404 | Part of Cart |
| **WISHLIST** | ⏳ | Integer | 404 | Model designed |
| **WISHLIST_ITEM** | ⏳ | Integer | 404 | Part of Wishlist |
| **ORDER** | ⏳ | String | Partial | GET works, POST needs cart_id |
| **ORDER_ITEM** | ⏳ | Integer | Pending | Part of Order |
| **PRODUCT_VARIANT** | ⏳ | UUID | Pending | Referenced but not implemented |
| **COUPON** | ⏳ | UUID | Untested | Model exists |

---

## Complete Attribute List

### USER ✅
```
id                  (PK, UUID)
email               (String, Unique)
first_name          (String)
last_name           (String)
phone               (String)
name                (String)
```

### PRODUCT ✅
```
id                  (PK, UUID)
category_id         (FK → CATEGORY)
brand_id            (FK → BRAND)
name                (String)
slug                (String, Unique)
sku                 (String)
barcode             (String)
short_description   (Text)
description         (Text)
price               (Decimal/String)
sale_price          (Decimal/String, Nullable)
cost_price          (Decimal/String, Nullable)
stock_quantity      (Integer)
min_stock_alert     (Integer)
track_inventory     (Boolean)
in_stock            (Boolean)
weight              (String)
length              (String)
width               (String)
height              (String)
thumbnail           (String/URL)
meta_title          (String)
meta_description    (Text)
is_active           (Boolean)
is_featured         (Boolean)
is_digital          (Boolean)
views_count         (Integer)
sales_count         (Integer)
sort_order          (Integer)
```

### CATEGORY ✅
```
id                  (PK, UUID)
parent_id           (FK → CATEGORY, Nullable)
name                (String)
slug                (String, Unique)
description         (Text)
image               (String/URL)
icon                (String/URL)
is_active           (Boolean)
is_featured         (Boolean)
sort_order          (Integer)
products_count      (Integer)
```

### BRAND ✅
```
id                  (PK, UUID)
name                (String)
slug                (String, Unique)
logo                (String/URL)
website             (String/URL)
description         (Text)
meta_title          (String)
meta_description    (Text)
is_active           (Boolean)
is_featured         (Boolean)
sort_order          (Integer)
products_count      (Integer)
```

### CART ⏳
```
id                  (PK, Integer)
user_id             (FK → USER, Nullable)
session_id          (String, Nullable)
status              (String: active|completed|abandoned)
coupon_code         (String, Nullable)
discount_total      (Decimal/String)
subtotal            (Decimal/String)
grand_total         (Decimal/String)
metadata            (JSON/Object)
```

### CART_ITEM ⏳
```
id                  (PK, Integer)
cart_id             (FK → CART)
product_id          (FK → PRODUCT)
product_variant_id  (FK → PRODUCT_VARIANT, Nullable)
quantity            (Integer)
price               (Decimal/String)
metadata            (JSON/Object)
```

### WISHLIST ⏳
```
id                  (PK, Integer)
user_id             (FK → USER, Nullable)
session_id          (String, Nullable)
name                (String)
description         (Text)
is_public           (Boolean)
is_active           (Boolean)
metadata            (JSON/Object)
```

### WISHLIST_ITEM ⏳
```
id                  (PK, Integer)
wishlist_id         (FK → WISHLIST)
product_id          (FK → PRODUCT)
product_variant_id  (FK → PRODUCT_VARIANT, Nullable)
quantity            (Integer)
metadata            (JSON/Object)
```

### ORDER ⏳
```
id                  (PK, String - e.g., "BD-1001")
user_id             (FK → USER)
order_no            (String, Unique)
date                (String/Date)
total_amount        (Decimal)
status              (Enum: PROCESSING|SHIPPED|DELIVERED|CANCELLED)
created_at          (Timestamp)
```

### ORDER_ITEM ⏳
```
id                  (PK, Integer)
order_id            (FK → ORDER)
product_id          (FK → PRODUCT)
product_name        (String - snapshot)
product_price       (String - snapshot)
product_image       (String - snapshot)
quantity            (Integer)
```

### PRODUCT_VARIANT ⏳
```
id                  (PK, UUID)
product_id          (FK → PRODUCT)
sku                 (String)
name                (String)
price               (Decimal/String)
stock_quantity      (Integer)
attributes          (JSON - e.g., {"size":"L","color":"Red"})
```

### COUPON ⏳
```
id                  (PK, UUID)
code                (String, Unique)
type                (String: percentage|fixed)
value               (Decimal)
min_purchase        (Decimal)
max_discount        (Decimal, Nullable)
valid_from          (Date/Timestamp)
valid_until         (Date/Timestamp)
is_active           (Boolean)
usage_limit         (Integer, Nullable)
used_count          (Integer)
```

---

## Relationship Matrix

| From Entity | Relationship | To Entity | Type | FK Column |
|-------------|--------------|-----------|------|-----------|
| USER | has many | CART | 1:N | cart.user_id |
| USER | has many | WISHLIST | 1:N | wishlist.user_id |
| USER | has many | ORDER | 1:N | order.user_id |
| CART | has many | CART_ITEM | 1:N | cart_item.cart_id |
| WISHLIST | has many | WISHLIST_ITEM | 1:N | wishlist_item.wishlist_id |
| ORDER | has many | ORDER_ITEM | 1:N | order_item.order_id |
| CATEGORY | has many | PRODUCT | 1:N | product.category_id |
| BRAND | has many | PRODUCT | 1:N | product.brand_id |
| PRODUCT | has many | CART_ITEM | 1:N | cart_item.product_id |
| PRODUCT | has many | WISHLIST_ITEM | 1:N | wishlist_item.product_id |
| PRODUCT | has many | ORDER_ITEM | 1:N | order_item.product_id |
| PRODUCT | has many | PRODUCT_VARIANT | 1:N | product_variant.product_id |
| CATEGORY | references | CATEGORY (parent) | N:1 | category.parent_id |
| CART_ITEM | references | PRODUCT_VARIANT | N:1 | cart_item.product_variant_id |
| WISHLIST_ITEM | references | PRODUCT_VARIANT | N:1 | wishlist_item.product_variant_id |

---

## Key Constraints & Rules

### Nullable Foreign Keys (Guest Support)
- `cart.user_id` → Allows guest carts via `session_id`
- `wishlist.user_id` → Allows guest wishlists via `session_id`

### Nullable Foreign Keys (Optional Features)
- `category.parent_id` → Top-level categories have no parent
- `cart_item.product_variant_id` → Products work without variants
- `wishlist_item.product_variant_id` → Products work without variants
- `product.sale_price` → Not all products on sale
- `product.cost_price` → Optional for profit tracking

### Unique Constraints
- `user.email` → One account per email
- `product.slug` → URL-friendly unique identifier
- `product.sku` → Inventory tracking
- `category.slug` → URL routing
- `brand.slug` → URL routing
- `coupon.code` → Coupon validation
- `order.order_no` → Order tracking

### Enums
- `cart.status`: "active", "completed", "abandoned"
- `coupon.type`: "percentage", "fixed"
- `order.status`: "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"

---

## Data Type Mapping

### Android Java → Database
- `String id (UUID)` → `UUID` or `VARCHAR(36)`
- `int id` → `INTEGER` or `BIGINT` (auto-increment)
- `String price` → `DECIMAL(10,2)` or `VARCHAR` (to avoid float precision issues)
- `boolean` → `BOOLEAN` or `TINYINT(1)`
- `Object metadata` → `JSON` or `TEXT`
- `List<T>` → Separate table with FK (1:N relationship)

---

## Current Implementation Strategy

### Working (API-backed)
```
User → API Authentication ✅
Product → API Catalog ✅
Category → API Master Data ✅
Brand → API Master Data ✅
```

### Temporary (In-memory)
```
Cart → CartManager Singleton (in-memory)
Order → OrderManager Singleton (in-memory)
Wishlist → Not implemented yet (UI designed)
```

### Migration Plan
When backend APIs are implemented:
1. Keep model classes unchanged
2. Switch from Manager singletons to Repository + API calls
3. Add sync logic for offline support (optional)
4. Test end-to-end flow

---

## API Endpoints Reference

### ✅ Working
```
POST   /api/v1/auth/login              → User authentication
POST   /api/v1/auth/register           → User registration
GET    /api/v1/products                → Product list
GET    /api/v1/products/{id}           → Product details
GET    /api/v1/master/categories       → Categories
GET    /api/v1/master/brands           → Brands
GET    /api/v1/orders                  → Order history
GET    /api/v1/orders/{id}             → Order details
```

### ⏳ Pending (404)
```
GET    /api/v1/cart                    → Get cart
POST   /api/v1/cart/items              → Add to cart
PATCH  /api/v1/cart/items/{id}         → Update cart item
DELETE /api/v1/cart/items/{id}         → Remove from cart
POST   /api/v1/cart/checkout           → Create order from cart

GET    /api/v1/wishlist                → Get wishlist
POST   /api/v1/wishlist/items          → Add to wishlist
DELETE /api/v1/wishlist/items/{id}     → Remove from wishlist
GET    /api/v1/wishlist/check          → Check if product in wishlist

POST   /api/v1/coupons/apply           → Apply coupon (untested)
```

---

**Generated**: From Beckie-Deal Android application models  
**Version**: 1.1  
**Last Updated**: Corrected with actual API implementation status  
**Base URL**: Configured in `local.properties` (not hardcoded in repository)
