# Beckie-Deal Database - Quick Reference Card

One-page reference for the most important information.

---

## 📊 System Overview

**Total Entities**: 12  
**Working**: 4 (User, Product, Category, Brand)  
**Pending**: 8 (Cart, Wishlist, Order, and related entities)

---

## 🗂️ Entity Quick List

| # | Entity | PK Type | Status | Notes |
|---|--------|---------|--------|-------|
| 1 | USER | UUID | ✅ | Auth working |
| 2 | PRODUCT | UUID | ✅ | 18 products available |
| 3 | CATEGORY | UUID | ✅ | Hierarchical |
| 4 | BRAND | UUID | ✅ | Master data |
| 5 | CART | Integer | ⏳ | API 404 |
| 6 | CART_ITEM | Integer | ⏳ | API 404 |
| 7 | WISHLIST | Integer | ⏳ | API 404 |
| 8 | WISHLIST_ITEM | Integer | ⏳ | API 404 |
| 9 | ORDER | String | ⏳ | GET works |
| 10 | ORDER_ITEM | Integer | ⏳ | Pending |
| 11 | PRODUCT_VARIANT | UUID | ⏳ | Referenced |
| 12 | COUPON | UUID | ⏳ | Untested |

---

## 🔗 Core Relationships

```
USER (1:N) → CART → CART_ITEM (N:1) → PRODUCT
USER (1:N) → WISHLIST → WISHLIST_ITEM (N:1) → PRODUCT
USER (1:N) → ORDER → ORDER_ITEM (N:1) → PRODUCT

CATEGORY (1:N) → PRODUCT (N:1) → BRAND
PRODUCT (1:N) → PRODUCT_VARIANT

CATEGORY (self-reference) → CATEGORY.parent_id
```

---

## 📱 API Status

### ✅ Working
```
POST   /api/v1/auth/login
POST   /api/v1/auth/register
GET    /api/v1/products
GET    /api/v1/products/{id}
GET    /api/v1/master/categories
GET    /api/v1/master/brands
GET    /api/v1/orders
```

### ⏳ Pending (404)
```
GET    /api/v1/cart
POST   /api/v1/cart/items
GET    /api/v1/wishlist
POST   /api/v1/wishlist/items
POST   /api/v1/coupons/apply
```

---

## 🎯 Key Attributes

### USER
- id (UUID, PK)
- email (unique)
- first_name, last_name
- phone, name

### PRODUCT
- id (UUID, PK)
- category_id (FK), brand_id (FK)
- name, slug, sku, barcode
- price, sale_price, cost_price
- stock_quantity, in_stock
- thumbnail, description
- is_active, is_featured

### CART
- id (int, PK)
- user_id (FK, nullable)
- session_id (nullable)
- status (active|completed|abandoned)
- coupon_code
- subtotal, discount_total, grand_total

### ORDER
- id (string, PK) - e.g., "BD-1001"
- user_id (FK)
- date, total_amount
- status (PROCESSING|SHIPPED|DELIVERED|CANCELLED)

---

## 🔑 Foreign Keys

| Child | FK Column | Parent | Nullable |
|-------|-----------|--------|----------|
| CART | user_id | USER.id | YES |
| WISHLIST | user_id | USER.id | YES |
| ORDER | user_id | USER.id | NO |
| CART_ITEM | cart_id | CART.id | NO |
| CART_ITEM | product_id | PRODUCT.id | NO |
| WISHLIST_ITEM | wishlist_id | WISHLIST.id | NO |
| WISHLIST_ITEM | product_id | PRODUCT.id | NO |
| ORDER_ITEM | order_id | ORDER.id | NO |
| PRODUCT | category_id | CATEGORY.id | NO |
| PRODUCT | brand_id | BRAND.id | NO |
| CATEGORY | parent_id | CATEGORY.id | YES |

---

## 💾 Data Types

- **UUID**: User, Product, Category, Brand, Product Variant, Coupon
- **Integer**: Cart, Cart Item, Wishlist, Wishlist Item, Order Item
- **String**: Order ID (human-readable like "BD-1001")
- **Decimal/String**: All price fields (avoid float precision issues)
- **Boolean**: is_active, is_featured, is_public, in_stock, etc.
- **JSON**: metadata fields for extensibility

---

## 🚨 Important Notes

1. **Guest Support**: Cart and Wishlist use nullable `user_id` + `session_id`
2. **Price Freeze**: Order items store product snapshot at purchase time
3. **Hierarchical Categories**: Self-referencing via `parent_id`
4. **Variants Optional**: Products work with or without variants
5. **Current Workaround**: CartManager and OrderManager for in-memory storage

---

## 📂 Documentation Files

1. **README.md** - Start here, overview of all docs
2. **ER_Diagram_Visual.html** - Open in browser for visual diagram
3. **ER_Diagram_Beckie_Deal.md** - Complete text ER diagram
4. **DATABASE_SCHEMA_SUMMARY.md** - All attributes listed
5. **RELATIONSHIP_DIAGRAM.md** - Detailed relationships
6. **IMPLEMENTATION_CHECKLIST.md** - Track progress
7. **QUICK_REFERENCE.md** - This file

---

## 🎨 Visual Legend

- **✅ Green** = API implemented and working
- **⏳ Orange** = Model designed, API pending (404)
- **PK** = Primary Key
- **FK** = Foreign Key
- **1:N** = One-to-Many relationship
- **N:1** = Many-to-One relationship

---

## 📞 Quick Contacts

**API Base URL**:  
`https://laravel-app-114227311375.asia-southeast1.run.app/api/v1`

**Last API Test**: September 10, 2026

**Backend Status**: See `API_FINAL_TEST_RESULTS.md` in project root

---

## 🔧 Essential SQL Queries

### Get Cart with Items
```sql
SELECT c.*, ci.*, p.name, p.price
FROM cart c
JOIN cart_item ci ON ci.cart_id = c.id
JOIN product p ON p.id = ci.product_id
WHERE c.user_id = ? AND c.status = 'active'
```

### Get Products with Category & Brand
```sql
SELECT p.*, c.name as category, b.name as brand
FROM product p
JOIN category c ON c.id = p.category_id
JOIN brand b ON b.id = p.brand_id
WHERE p.is_active = true
```

### Get User Orders
```sql
SELECT o.*, oi.*, p.name
FROM order o
JOIN order_item oi ON oi.order_id = o.id
LEFT JOIN product p ON p.id = oi.product_id
WHERE o.user_id = ?
ORDER BY o.created_at DESC
```

---

## ✅ Quick Status Check

**Can Build Now**:
- ✅ Product browsing
- ✅ Authentication
- ✅ Category filtering
- ✅ Brand filtering
- ✅ Product search
- ✅ Order history (view only)

**Need Backend First**:
- ⏳ Shopping cart
- ⏳ Add to cart
- ⏳ Wishlist
- ⏳ Checkout
- ⏳ Order creation
- ⏳ Coupon application

**Current Workarounds**:
- 🔧 CartManager (in-memory cart)
- 🔧 OrderManager (in-memory orders)

---

**Version**: 1.1  
**Generated**: From Beckie-Deal Android app models  
**Last Updated**: Corrected with actual implementation status
