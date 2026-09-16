# Beckie-Deal Entity Relationships - Complete Reference

## Visual Relationship Map

```
                                   ┌─────────────┐
                                   │    USER     │
                                   │   (UUID)    │
                                   └─────┬───────┘
                                         │
                      ┌──────────────────┼──────────────────┐
                      │                  │                  │
                    1 │                1 │                1 │
                      ▼                  ▼                  ▼
              ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
              │   WISHLIST   │  │     CART     │  │    ORDER     │
              │    (int)     │  │    (int)     │  │   (string)   │
              └──────┬───────┘  └──────┬───────┘  └──────┬───────┘
                   N │                N │                N │
                     ▼                  ▼                  ▼
          ┌──────────────────┐┌──────────────────┐┌──────────────────┐
          │ WISHLIST_ITEM    ││   CART_ITEM      ││   ORDER_ITEM     │
          │     (int)        ││     (int)        ││     (int)        │
          └────────┬─────────┘└────────┬─────────┘└────────┬─────────┘
                 N │                  N │                  N │
                   │                    │                    │
                   └────────────────────┼────────────────────┘
                                        │
                                      1 │
                                        ▼
                              ┌──────────────────┐
                              │     PRODUCT      │
                              │      (UUID)      │
                              └────┬──────┬──────┘
                                 N │    N │
                                   │      │
                         ┌─────────┘      └─────────┐
                       1 │                          1 │
                         ▼                            ▼
                  ┌─────────────┐            ┌──────────────┐
                  │  CATEGORY   │            │    BRAND     │
                  │   (UUID)    │            │    (UUID)    │
                  └─────────────┘            └──────────────┘
                         │
                       N │ (self-reference)
                         │
                  ┌──────┴─────────┐
                  │   parent_id    │
                  └────────────────┘


           ┌──────────────────────┐              ┌──────────────────┐
           │  PRODUCT_VARIANT     │              │     COUPON       │
           │       (UUID)         │              │     (UUID)       │
           └──────────────────────┘              └──────────────────┘
                     │                                    │
                   N │                                    │
                     ▼                                    │
           ┌──────────────────┐                          │
           │    PRODUCT       │◄─────────────────────────┘
           │     (1:N)        │        (applied to cart)
           └──────────────────┘
```

## Cardinality Reference

### One-to-Many (1:N)

#### USER Relationships
```
USER (1) ──→ CART (N)
USER (1) ──→ WISHLIST (N)
USER (1) ──→ ORDER (N)
```
**Note**: USER can be null (guest support via session_id)

#### Container → Item Relationships
```
CART (1) ──→ CART_ITEM (N)
WISHLIST (1) ──→ WISHLIST_ITEM (N)
ORDER (1) ──→ ORDER_ITEM (N)
```

#### Product Organization
```
CATEGORY (1) ──→ PRODUCT (N)
BRAND (1) ──→ PRODUCT (N)
PRODUCT (1) ──→ PRODUCT_VARIANT (N)
```

#### Product References
```
PRODUCT (1) ──→ CART_ITEM (N)
PRODUCT (1) ──→ WISHLIST_ITEM (N)
PRODUCT (1) ──→ ORDER_ITEM (N)
```

### Many-to-One (N:1)

#### Items → Container
```
CART_ITEM (N) ──→ CART (1)
WISHLIST_ITEM (N) ──→ WISHLIST (1)
ORDER_ITEM (N) ──→ ORDER (1)
```

#### Product Relationships
```
PRODUCT (N) ──→ CATEGORY (1)
PRODUCT (N) ──→ BRAND (1)
CART_ITEM (N) ──→ PRODUCT (1)
WISHLIST_ITEM (N) ──→ PRODUCT (1)
ORDER_ITEM (N) ──→ PRODUCT (1)
PRODUCT_VARIANT (N) ──→ PRODUCT (1)
```

### Self-Referencing

#### Category Hierarchy
```
CATEGORY (N) ──→ CATEGORY.parent_id (1)
```
Example:
- Electronics (parent_id: null)
  - Laptops (parent_id: Electronics.id)
  - Phones (parent_id: Electronics.id)
    - Samsung Phones (parent_id: Phones.id)

---

## Detailed Relationship Descriptions

### 1. USER → CART (1:N)
**Description**: A user can have multiple shopping carts (active, abandoned, completed)  
**Foreign Key**: `cart.user_id` → `user.id`  
**Nullable**: Yes (supports guest carts via `session_id`)  
**Business Rule**: One active cart per user at a time (enforced at application level)

### 2. USER → WISHLIST (1:N)
**Description**: A user can have multiple wishlists (e.g., "Christmas Gifts", "Personal")  
**Foreign Key**: `wishlist.user_id` → `user.id`  
**Nullable**: Yes (supports guest wishlists via `session_id`)  
**Business Rule**: Users can create named, public/private wishlists

### 3. USER → ORDER (1:N)
**Description**: A user can place multiple orders over time  
**Foreign Key**: `order.user_id` → `user.id`  
**Nullable**: No (only authenticated users can place orders)  
**Business Rule**: Orders are immutable after creation (status changes only)

### 4. CART → CART_ITEM (1:N)
**Description**: A cart contains multiple products (cart items)  
**Foreign Key**: `cart_item.cart_id` → `cart.id`  
**Cascade**: Delete cart items when cart is deleted  
**Business Rule**: Each product can appear only once per cart (update quantity instead)

### 5. WISHLIST → WISHLIST_ITEM (1:N)
**Description**: A wishlist contains multiple products (wishlist items)  
**Foreign Key**: `wishlist_item.wishlist_id` → `wishlist.id`  
**Cascade**: Delete wishlist items when wishlist is deleted  
**Business Rule**: Same product can be in multiple wishlists

### 6. ORDER → ORDER_ITEM (1:N)
**Description**: An order contains multiple products purchased (snapshot at checkout)  
**Foreign Key**: `order_item.order_id` → `order.id`  
**Cascade**: Never delete order items (order history preservation)  
**Business Rule**: Order items store product details as they were at purchase time

### 7. CATEGORY → PRODUCT (1:N)
**Description**: A category contains multiple products  
**Foreign Key**: `product.category_id` → `category.id`  
**Nullable**: No (every product must have a category)  
**Business Rule**: Products must be moved to another category before deleting a category

### 8. BRAND → PRODUCT (1:N)
**Description**: A brand has multiple products  
**Foreign Key**: `product.brand_id` → `brand.id`  
**Nullable**: No (every product must have a brand)  
**Business Rule**: Products must be reassigned before deleting a brand

### 9. PRODUCT → CART_ITEM (1:N)
**Description**: A product can be in multiple users' carts  
**Foreign Key**: `cart_item.product_id` → `product.id`  
**Business Rule**: Out-of-stock products can still be in cart (show warning)

### 10. PRODUCT → WISHLIST_ITEM (1:N)
**Description**: A product can be in multiple users' wishlists  
**Foreign Key**: `wishlist_item.product_id` → `product.id`  
**Business Rule**: Wishlisted products remain even if discontinued

### 11. PRODUCT → ORDER_ITEM (1:N)
**Description**: A product can be in multiple orders  
**Foreign Key**: `order_item.product_id` → `product.id`  
**Business Rule**: Order item stores snapshot, not live product reference

### 12. PRODUCT → PRODUCT_VARIANT (1:N)
**Description**: A product can have multiple variants (size, color, etc.)  
**Foreign Key**: `product_variant.product_id` → `product.id`  
**Business Rule**: Each variant has its own SKU, price, and stock

### 13. CATEGORY → CATEGORY (Self-Reference)
**Description**: Categories can have parent categories (hierarchy)  
**Foreign Key**: `category.parent_id` → `category.id`  
**Nullable**: Yes (root categories have no parent)  
**Business Rule**: No circular references (A → B → C → A)

---

## Foreign Key Constraints Summary

| Child Table | Foreign Key Column | References | On Delete | Nullable |
|-------------|-------------------|------------|-----------|----------|
| CART | user_id | USER.id | CASCADE | YES |
| WISHLIST | user_id | USER.id | CASCADE | YES |
| ORDER | user_id | USER.id | RESTRICT | NO |
| CART_ITEM | cart_id | CART.id | CASCADE | NO |
| CART_ITEM | product_id | PRODUCT.id | RESTRICT | NO |
| CART_ITEM | product_variant_id | PRODUCT_VARIANT.id | SET NULL | YES |
| WISHLIST_ITEM | wishlist_id | WISHLIST.id | CASCADE | NO |
| WISHLIST_ITEM | product_id | PRODUCT.id | RESTRICT | NO |
| WISHLIST_ITEM | product_variant_id | PRODUCT_VARIANT.id | SET NULL | YES |
| ORDER_ITEM | order_id | ORDER.id | RESTRICT | NO |
| ORDER_ITEM | product_id | PRODUCT.id | RESTRICT | NO |
| PRODUCT | category_id | CATEGORY.id | RESTRICT | NO |
| PRODUCT | brand_id | BRAND.id | RESTRICT | NO |
| PRODUCT_VARIANT | product_id | PRODUCT.id | CASCADE | NO |
| CATEGORY | parent_id | CATEGORY.id | RESTRICT | YES |

### Cascade Rules Explained

**CASCADE**: When parent is deleted, child is also deleted
- Deleting CART → deletes all CART_ITEMS
- Deleting WISHLIST → deletes all WISHLIST_ITEMS

**RESTRICT**: Prevent deletion if child records exist
- Cannot delete PRODUCT if it has CART_ITEMS
- Cannot delete CATEGORY if it has PRODUCTS
- Cannot delete ORDER (ever - for audit trail)

**SET NULL**: When parent is deleted, set FK to null
- Deleting PRODUCT_VARIANT → sets cart_item.product_variant_id to null
- Product remains in cart, but without variant selection

---

## Join Query Examples

### Get Cart with Products
```sql
SELECT c.*, ci.*, p.name, p.price, p.thumbnail
FROM cart c
JOIN cart_item ci ON ci.cart_id = c.id
JOIN product p ON p.id = ci.product_id
WHERE c.user_id = ? AND c.status = 'active'
```

### Get Products with Category and Brand
```sql
SELECT p.*, c.name as category_name, b.name as brand_name
FROM product p
JOIN category c ON c.id = p.category_id
JOIN brand b ON b.id = p.brand_id
WHERE p.is_active = true
```

### Get Category Hierarchy
```sql
-- Get all subcategories of a parent
SELECT child.*
FROM category child
WHERE child.parent_id = ?

-- Get full path to root
WITH RECURSIVE category_path AS (
  SELECT id, name, parent_id, 1 as level
  FROM category
  WHERE id = ?
  UNION ALL
  SELECT c.id, c.name, c.parent_id, cp.level + 1
  FROM category c
  JOIN category_path cp ON c.id = cp.parent_id
)
SELECT * FROM category_path
```

### Get Order with Items and Product Details
```sql
SELECT o.*, oi.*, p.name, p.thumbnail
FROM order o
JOIN order_item oi ON oi.order_id = o.id
LEFT JOIN product p ON p.id = oi.product_id
WHERE o.user_id = ?
ORDER BY o.created_at DESC
```

---

## Index Recommendations

### Primary Indexes (Already Exist)
- All primary keys are indexed by default

### Foreign Key Indexes (Highly Recommended)
```sql
CREATE INDEX idx_cart_user_id ON cart(user_id);
CREATE INDEX idx_cart_session_id ON cart(session_id);
CREATE INDEX idx_cart_item_cart_id ON cart_item(cart_id);
CREATE INDEX idx_cart_item_product_id ON cart_item(product_id);
CREATE INDEX idx_wishlist_user_id ON wishlist(user_id);
CREATE INDEX idx_wishlist_item_wishlist_id ON wishlist_item(wishlist_id);
CREATE INDEX idx_wishlist_item_product_id ON wishlist_item(product_id);
CREATE INDEX idx_order_user_id ON order(user_id);
CREATE INDEX idx_order_item_order_id ON order_item(order_id);
CREATE INDEX idx_product_category_id ON product(category_id);
CREATE INDEX idx_product_brand_id ON product(brand_id);
CREATE INDEX idx_category_parent_id ON category(parent_id);
```

### Query Optimization Indexes
```sql
CREATE INDEX idx_product_slug ON product(slug);
CREATE INDEX idx_product_is_active ON product(is_active);
CREATE INDEX idx_product_is_featured ON product(is_featured);
CREATE INDEX idx_cart_status ON cart(status);
CREATE INDEX idx_order_status ON order(status);
CREATE INDEX idx_order_created_at ON order(created_at);
```

---

**Version**: 1.1  
**Last Updated**: Corrected with actual implementation status  
**Generated**: From Beckie-Deal Android application models
