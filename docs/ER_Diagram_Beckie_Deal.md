# Beckie-Deal E-Commerce System - Entity Relationship Diagram (v2.0)

## Final ER Diagram - Ready for Thesis Document

**LAST UPDATED**: September 27, 2026  
**VERSION**: 2.0 - Complete Schema with All Requirements  
**STATUS**: ✅ Ready to Draw

---

## 📊 COMPLETE ER DIAGRAM (Simplified for Easy Drawing)

```
═══════════════════════════════════════════════════════════════════════════════════
                     BECKIE-DEAL E-COMMERCE - COMPLETE ER DIAGRAM v2.0
═══════════════════════════════════════════════════════════════════════════════════

┌─────────────────────────────────────────────────────────────────────────────┐
│                            CORE ENTITIES (Users & Auth)                     │
└─────────────────────────────────────────────────────────────────────────────┘

                    ┌─────────────────────────┐
                    │       USER ✅           │
                    ├─────────────────────────┤
                    │ PK  id (UUID)           │
                    │     email               │
                    │     password_hash       │
                    │     first_name          │
                    │     last_name           │
                    │     phone               │
                    │     created_at          │
                    │     updated_at          │
                    └──────────┬──────────────┘
                               │
                               │ 1:N
                               │
                    ┌──────────▼──────────┐
                    │ CUSTOMER_PROFILE    │
                    ├─────────────────────┤
                    │ PK  id (UUID)       │
                    │ FK  user_id         │
                    │     address         │
                    │     city            │
                    │     state           │
                    │     postal_code     │
                    │     country         │
                    │     telegram        │
                    │     website         │
                    │     contact_person  │
                    │     created_at      │
                    └─────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                      CATALOG ENTITIES (Products & Categories)               │
└─────────────────────────────────────────────────────────────────────────────┘

                           ┌──────────────────┐
                           │   CATEGORY ✅    │
                           ├──────────────────┤
                           │ PK  id (UUID)    │
                           │ FK  parent_id    │◄─────┐
                           │     name         │      │
                           │     slug         │      │
                           │     description  │      │
                           │     image        │      │ Self-Reference
                           │     icon         │      │ (Parent Category)
                           │     is_active    │      │
                           │     is_featured  │      │
                           │     sort_order   │      │
                           └────────┬─────────┘      │
                                    │                │
                                    └────────────────┘
                           1
                           │
                           │ 1:N
                           │
                    ┌──────▼────────────────────┐
                    │    PRODUCT ✅             │
                    ├───────────────────────────┤
                    │ PK  id (UUID)             │
                    │ FK  category_id           │
                    │ FK  brand_id              │
                    │     name                  │
                    │     slug                  │
                    │     sku                   │
                    │     barcode               │
                    │     short_description     │
                    │     description           │
                    │     price                 │
                    │     sale_price            │
                    │     cost_price            │
                    │     stock_quantity        │
                    │     min_stock_alert       │
                    │     track_inventory       │
                    │     in_stock              │
                    │     weight, length, width,│
                    │     height                │
                    │     meta_title            │
                    │     meta_description      │
                    │     is_active             │
                    │     is_featured           │
                    │     is_digital            │
                    │     views_count           │
                    │     sales_count           │
                    │     sort_order            │
                    │     created_at            │
                    └───────────┬────────────────┘
                                │
                    ┌───────────┴────────────┐
                    │ 1:N         1:N        │
                    │                        │
        ┌───────────▼───────────┐  ┌────────▼───────────┐
        │   PRODUCT_IMAGE       │  │    BRAND ✅        │
        ├───────────────────────┤  ├────────────────────┤
        │ PK  id (UUID)         │  │ PK  id (UUID)      │
        │ FK  product_id        │  │     name           │
        │     image_url         │  │     slug           │
        │     alt_text          │  │     logo           │
        │     sort_order        │  │     website        │
        │     is_thumbnail      │  │     description    │
        │     created_at        │  │     meta_title     │
        └───────────────────────┘  │     meta_desc      │
                                    │     is_active     │
        ┌───────────────────────┐  │     is_featured   │
        │ PRODUCT_VARIANT ⏳    │  │     sort_order    │
        ├───────────────────────┤  │     products_count│
        │ PK  id (UUID)         │  └────────────────────┘
        │ FK  product_id        │
        │     name              │
        │     sku               │
        │     price             │
        │     stock_quantity    │
        │     attributes (JSON) │
        │     created_at        │
        └───────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    TRANSACTION ENTITIES (Orders, Cart, etc.)                │
└─────────────────────────────────────────────────────────────────────────────┘

                           USER (PK: id)
                                 │
                    ┌────────────┼────────────┬────────────┐
                    │            │            │            │
                  1:N          1:N          1:N          1:N
                    │            │            │            │
        ┌───────────▼────────┐  ┌─▼─────────┐  ┌─────────▼───────┐  ┌───────────▼──────┐
        │    CART ⏳         │  │ORDER ⏳    │  │WISHLIST ⏳     │  │ PRODUCT_REVIEW   │
        ├───────────────────┤  ├──────────┤  ├────────────────┤  ├──────────────────┤
        │PK id (UUID)       │  │PK id (str)│  │PK id (UUID)    │  │PK id (UUID)      │
        │FK user_id (null)  │  │FK user_id │  │FK user_id(null)│  │FK user_id        │
        │   session_id      │  │ order_no  │  │  session_id    │  │FK product_id     │
        │   status          │  │   date    │  │    name        │  │   rating (1-5)   │
        │coupon_code (null) │  │ total_amt │  │ description    │  │   title          │
        │discount_total     │  │  status   │  │  is_public     │  │   review_text     │
        │   subtotal        │  │ paid_amt  │  │ is_active      │  │   helpful_count   │
        │   grand_total     │  │shipping_id│  │  metadata      │  │   created_at      │
        │   metadata        │  │payment_id │  │  created_at    │  └──────────────────┘
        │   created_at      │  │created_at │  │  updated_at    │
        └────────┬──────────┘  └──┬────────┘  └────────┬────────┘
                 │                │                    │
                 │ 1:N            │ 1:N                │ 1:N
                 │                │                    │
        ┌────────▼────────┐  ┌────▼─────────┐  ┌──────▼──────────┐
        │  CART_ITEM ⏳   │  │ ORDER_ITEM ⏳ │  │WISHLIST_ITEM ⏳ │
        ├─────────────────┤  ├──────────────┤  ├─────────────────┤
        │PK id (UUID)     │  │PK id (UUID)  │  │PK id (UUID)     │
        │FK cart_id       │  │FK order_id   │  │FK wishlist_id   │
        │FK product_id    │  │FK product_id │  │FK product_id    │
        │FK variant_id    │  │product_name  │  │FK variant_id    │
        │   quantity      │  │product_price │  │   quantity      │
        │   price         │  │product_image │  │   metadata      │
        │   discount      │  │   quantity   │  │   created_at    │
        │   metadata      │  │  order_item_ │  └─────────────────┘
        │   created_at    │  │   discount   │
        └─────────────────┘  │  created_at  │
                             └──────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                   SHIPPING & PAYMENT ENTITIES                               │
└─────────────────────────────────────────────────────────────────────────────┘

                         ┌───────────────────────┐
                         │SHIPPING_METHOD ⏳     │
                         ├───────────────────────┤
                         │PK id (UUID)           │
                         │    name               │
                         │    description        │
                         │    cost               │
                         │    estimated_days     │
                         │    is_active          │
                         │    created_at         │
                         └───────────┬───────────┘
                                     │
                                     │ 1:N
                                     │
        ┌────────────────────────┬───▼────────┐
        │                        │            │
     1:N│                        │            │
        │                        │            │
   ┌────▼──────────────┐  ┌─────▼──────────┐  ┌──────────────────┐
   │ORDER_SHIPMENT ⏳  │  │ PAYMENT ⏳     │  │    DISCOUNT      │
   ├────────────────┤  ├────────────────┤  ├──────────────────┤
   │PK id (UUID)    │  │PK id (UUID)    │  │PK id (UUID)      │
   │FK order_id     │  │FK order_id     │  │FK order_id       │
   │FK shipping_id  │  │payment_method  │  │   discount_type  │
   │tracking_number │  │    amount      │  │   amount         │
   │    status      │  │    status      │  │   reason         │
   │ shipped_date   │  │transaction_id  │  │   created_by     │
   │delivered_date  │  │   created_at   │  │   created_at     │
   │    notes       │  └────────────────┘  └──────────────────┘
   └────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                   SUPPLIER & PROMOTION ENTITIES                             │
└─────────────────────────────────────────────────────────────────────────────┘

        ┌──────────────────────────┐         ┌──────────────────────────┐
        │     SUPPLIER ⏳          │         │    COUPON/PROMOTION ⏳   │
        ├──────────────────────────┤         ├──────────────────────────┤
        │ PK  id (UUID)            │         │ PK  id (UUID)            │
        │     name                 │         │     code                 │
        │     address              │         │     type                 │
        │     city                 │         │  (percentage/fixed/buy)  │
        │     state                │         │     value                │
        │     postal_code          │         │  min_purchase_amount     │
        │     country              │         │  max_discount_amount     │
        │     phone                │         │  valid_from              │
        │     telegram             │         │  valid_until             │
        │     email                │         │  usage_limit             │
        │     website              │         │  used_count              │
        │     contact_person       │         │  applicable_categories   │
        │     is_active            │         │  (JSON)                  │
        │     created_at           │         │  applicable_brands       │
        └────────┬─────────────────┘         │  (JSON)                  │
                 │                           │  is_active               │
                 │ 1:N                       │  created_at              │
                 │                           └──────────────────────────┘
        ┌────────▼─────────────────┐
        │PRODUCT_SUPPLIER ⏳       │
        ├──────────────────────────┤
        │ PK  id (UUID)            │
        │ FK  product_id           │
        │ FK  supplier_id          │
        │     supplier_code        │
        │     lead_time_days       │
        │     min_order_qty        │
        │     unit_cost            │
        │     is_active            │
        │     created_at           │
        └──────────────────────────┘

═══════════════════════════════════════════════════════════════════════════════════
```

---

## 📋 ENTITY DEFINITIONS & ATTRIBUTES

### GROUP 1: CORE USER ENTITIES ✅

#### 1. **USER** ✅ (Authentication & Core Profile)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| email | String | UNIQUE, NOT NULL | Login email |
| password_hash | String | NOT NULL | Encrypted password |
| first_name | String | NOT NULL | User's first name |
| last_name | String | NOT NULL | User's last name |
| phone | String | Nullable | Contact number |
| created_at | DateTime | NOT NULL | Registration date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- 1:N → CUSTOMER_PROFILE (extended profile data)
- 1:N → CART (shopping carts)
- 1:N → ORDER (placed orders)
- 1:N → WISHLIST (saved items)
- 1:N → PRODUCT_REVIEW (product reviews)

**API Status**: ✅ `/auth/login`, `/auth/register` working

---

#### 2. **CUSTOMER_PROFILE** (Extended User Profile)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| user_id | UUID | FK → USER, NOT NULL | Reference to user |
| address | String | Nullable | Street address |
| city | String | Nullable | City name |
| state | String | Nullable | State/Province |
| postal_code | String | Nullable | ZIP/Postal code |
| country | String | Nullable | Country name |
| telegram | String | Nullable | Telegram handle |
| website | String | Nullable | Personal/business website |
| contact_person | String | Nullable | Contact name |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← USER (owner of profile)

---

### GROUP 2: CATALOG ENTITIES ✅

#### 3. **CATEGORY** ✅ (Product Categories with Hierarchy)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| parent_id | UUID | FK → CATEGORY, Nullable | Parent category (self-reference) |
| name | String | NOT NULL | Category name |
| slug | String | UNIQUE, NOT NULL | URL-friendly name |
| description | String | Nullable | Category description |
| image | String | Nullable | Category image URL |
| icon | String | Nullable | Category icon |
| is_active | Boolean | DEFAULT true | Enable/disable category |
| is_featured | Boolean | DEFAULT false | Featured category flag |
| sort_order | Integer | DEFAULT 0 | Display order |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- 1:N → PRODUCT (contains products)
- N:1 ← CATEGORY (parent category - self-reference)

**API Status**: ✅ `/master/categories` working

---

#### 4. **BRAND** ✅ (Product Brands/Manufacturers)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| name | String | NOT NULL | Brand name |
| slug | String | UNIQUE, NOT NULL | URL-friendly name |
| logo | String | Nullable | Brand logo URL |
| website | String | Nullable | Brand website |
| description | String | Nullable | Brand description |
| meta_title | String | Nullable | SEO meta title |
| meta_description | String | Nullable | SEO meta description |
| is_active | Boolean | DEFAULT true | Enable/disable brand |
| is_featured | Boolean | DEFAULT false | Featured brand flag |
| sort_order | Integer | DEFAULT 0 | Display order |
| products_count | Integer | DEFAULT 0 | Number of products |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- 1:N → PRODUCT (offers products)

**API Status**: ✅ `/master/brands` working

---

#### 5. **PRODUCT** ✅ (Main Product Catalog)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| category_id | UUID | FK → CATEGORY, NOT NULL | Product category |
| brand_id | UUID | FK → BRAND, NOT NULL | Product brand |
| name | String | NOT NULL | Product name |
| slug | String | UNIQUE, NOT NULL | URL-friendly name |
| sku | String | UNIQUE, Nullable | Stock keeping unit |
| barcode | String | Nullable | Product barcode |
| short_description | String | Nullable | Short description |
| description | String | Nullable | Full description |
| price | Decimal | NOT NULL | Regular price |
| sale_price | Decimal | Nullable | Sale/discount price |
| cost_price | Decimal | Nullable | Cost to business |
| stock_quantity | Integer | DEFAULT 0 | Available stock |
| min_stock_alert | Integer | DEFAULT 10 | Low stock threshold |
| track_inventory | Boolean | DEFAULT true | Inventory tracking flag |
| in_stock | Boolean | Computed | Based on stock_quantity |
| weight | Decimal | Nullable | Product weight |
| length | Decimal | Nullable | Product length |
| width | Decimal | Nullable | Product width |
| height | Decimal | Nullable | Product height |
| meta_title | String | Nullable | SEO title |
| meta_description | String | Nullable | SEO description |
| is_active | Boolean | DEFAULT true | Enable/disable product |
| is_featured | Boolean | DEFAULT false | Featured product flag |
| is_digital | Boolean | DEFAULT false | Digital product flag |
| views_count | Integer | DEFAULT 0 | View count |
| sales_count | Integer | DEFAULT 0 | Sales count |
| sort_order | Integer | DEFAULT 0 | Display order |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← CATEGORY (belongs to)
- N:1 ← BRAND (belongs to)
- 1:N → PRODUCT_IMAGE (product images)
- 1:N → PRODUCT_VARIANT (variants)
- 1:N → PRODUCT_SUPPLIER (suppliers)
- 1:N → PRODUCT_REVIEW (reviews)
- 1:N → CART_ITEM (cart references)
- 1:N → WISHLIST_ITEM (wishlist references)
- 1:N → ORDER_ITEM (order references)

**API Status**: ✅ `/products` working (18 products available)

---

#### 6. **PRODUCT_IMAGE** (Product Images)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| product_id | UUID | FK → PRODUCT, NOT NULL | Reference to product |
| image_url | String | NOT NULL | Image URL |
| alt_text | String | Nullable | Alt text for accessibility |
| sort_order | Integer | DEFAULT 0 | Display order |
| is_thumbnail | Boolean | DEFAULT false | Is main thumbnail |
| created_at | DateTime | NOT NULL | Creation date |

**Relationships:**
- N:1 ← PRODUCT (belongs to)

---

#### 7. **PRODUCT_VARIANT** ⏳ (Product Variations)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| product_id | UUID | FK → PRODUCT, NOT NULL | Reference to product |
| name | String | NOT NULL | Variant name (e.g., "Red-Large") |
| sku | String | UNIQUE, NOT NULL | Variant SKU |
| price | Decimal | Nullable | Variant-specific price |
| stock_quantity | Integer | DEFAULT 0 | Variant stock |
| attributes | JSON | Nullable | Variant attributes (size, color, etc.) |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← PRODUCT (belongs to)
- 1:N → CART_ITEM (referenced in carts)
- 1:N → WISHLIST_ITEM (referenced in wishlists)
- 1:N → ORDER_ITEM (referenced in orders)

---

### GROUP 3: TRANSACTION ENTITIES ⏳

#### 8. **CART** ⏳ (Shopping Cart)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| user_id | UUID | FK → USER, Nullable | User (null for guests) |
| session_id | String | Nullable | Guest session ID |
| status | String | Enum: active/completed/abandoned | Cart status |
| coupon_code | String | Nullable | Applied coupon code |
| discount_total | Decimal | DEFAULT 0 | Total discounts applied |
| subtotal | Decimal | DEFAULT 0 | Sum of item prices |
| grand_total | Decimal | DEFAULT 0 | Final total (subtotal - discount) |
| metadata | JSON | Nullable | Additional data |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← USER (belongs to - nullable for guests)
- 1:N → CART_ITEM (contains items)

**API Status**: ⏳ `/cart/*` endpoints return 404 (using local CartManager)

---

#### 9. **CART_ITEM** ⏳ (Items in Cart)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| cart_id | UUID | FK → CART, NOT NULL | Reference to cart |
| product_id | UUID | FK → PRODUCT, NOT NULL | Reference to product |
| variant_id | UUID | FK → PRODUCT_VARIANT, Nullable | Variant (optional) |
| quantity | Integer | NOT NULL, > 0 | Item quantity |
| price | Decimal | NOT NULL | Price at time of addition |
| discount | Decimal | DEFAULT 0 | Item-level discount |
| metadata | JSON | Nullable | Customization data |
| created_at | DateTime | NOT NULL | Addition date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← CART (belongs to)
- N:1 ← PRODUCT (references)
- N:1 ← PRODUCT_VARIANT (references - optional)

---

#### 10. **WISHLIST** ⏳ (Saved Items List)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| user_id | UUID | FK → USER, Nullable | User (null for guests) |
| session_id | String | Nullable | Guest session ID |
| name | String | NOT NULL | Wishlist name (e.g., "Christmas") |
| description | String | Nullable | Wishlist description |
| is_public | Boolean | DEFAULT false | Public/private flag |
| is_active | Boolean | DEFAULT true | Active/inactive flag |
| metadata | JSON | Nullable | Additional data |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← USER (belongs to - nullable for guests)
- 1:N → WISHLIST_ITEM (contains items)

**API Status**: ⏳ `/wishlist/*` endpoints return 404

---

#### 11. **WISHLIST_ITEM** ⏳ (Items in Wishlist)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| wishlist_id | UUID | FK → WISHLIST, NOT NULL | Reference to wishlist |
| product_id | UUID | FK → PRODUCT, NOT NULL | Reference to product |
| variant_id | UUID | FK → PRODUCT_VARIANT, Nullable | Variant (optional) |
| quantity | Integer | DEFAULT 1 | Desired quantity |
| metadata | JSON | Nullable | Additional data |
| created_at | DateTime | NOT NULL | Addition date |

**Relationships:**
- N:1 ← WISHLIST (belongs to)
- N:1 ← PRODUCT (references)
- N:1 ← PRODUCT_VARIANT (references - optional)

---

#### 12. **ORDER** ⏳ (Placed Orders)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | String | PK, NOT NULL | Order number (e.g., "BD-1001") |
| user_id | UUID | FK → USER, NOT NULL | Reference to user |
| order_no | String | UNIQUE, NOT NULL | Human-readable order number |
| date | DateTime | NOT NULL | Order date |
| total_amount | Decimal | NOT NULL | Total order amount |
| status | String | Enum: PROCESSING/SHIPPED/DELIVERED/CANCELLED | Order status |
| paid_amount | Decimal | DEFAULT 0 | Amount paid |
| shipping_id | UUID | FK → SHIPPING_METHOD, Nullable | Shipping method |
| payment_id | UUID | FK → PAYMENT, Nullable | Payment record |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← USER (belongs to)
- 1:N → ORDER_ITEM (contains items)
- N:1 ← SHIPPING_METHOD (has)
- 1:N → ORDER_SHIPMENT (shipment tracking)
- 1:N → PAYMENT (payments)
- 1:N → DISCOUNT (discounts applied)

**API Status**: ⏳ GET `/orders` works (empty), POST needs cart_id (using local OrderManager)

---

#### 13. **ORDER_ITEM** ⏳ (Items in Order)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| order_id | String | FK → ORDER, NOT NULL | Reference to order |
| product_id | UUID | FK → PRODUCT, NOT NULL | Reference to product |
| product_name | String | NOT NULL | Product name snapshot |
| product_price | Decimal | NOT NULL | Price at time of order |
| product_image | String | Nullable | Image snapshot |
| quantity | Integer | NOT NULL | Order quantity |
| order_item_discount | Decimal | DEFAULT 0 | Item-level discount |
| created_at | DateTime | NOT NULL | Creation date |

**Relationships:**
- N:1 ← ORDER (belongs to)
- N:1 ← PRODUCT (references)

---

### GROUP 4: SHIPPING & PAYMENT ENTITIES ⏳

#### 14. **SHIPPING_METHOD** ⏳ (Shipping Options)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| name | String | NOT NULL | Shipping method name |
| description | String | Nullable | Description |
| cost | Decimal | DEFAULT 0 | Shipping cost |
| estimated_days | Integer | NOT NULL | Estimated delivery days |
| is_active | Boolean | DEFAULT true | Enable/disable |
| created_at | DateTime | NOT NULL | Creation date |

**Relationships:**
- 1:N → ORDER_SHIPMENT (used for)
- 1:N → ORDER (selected in)

---

#### 15. **ORDER_SHIPMENT** ⏳ (Shipment Tracking)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| order_id | String | FK → ORDER, NOT NULL | Reference to order |
| shipping_id | UUID | FK → SHIPPING_METHOD, NOT NULL | Shipping method |
| tracking_number | String | Nullable | Carrier tracking number |
| status | String | Enum: pending/shipped/in_transit/delivered | Shipment status |
| shipped_date | DateTime | Nullable | Date shipped |
| delivered_date | DateTime | Nullable | Date delivered |
| notes | String | Nullable | Shipment notes |
| created_at | DateTime | NOT NULL | Creation date |

**Relationships:**
- N:1 ← ORDER (belongs to)
- N:1 ← SHIPPING_METHOD (uses)

---

#### 16. **PAYMENT** ⏳ (Payment Records)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| order_id | String | FK → ORDER, NOT NULL | Reference to order |
| payment_method | String | Enum: card/bank/wallet/cash | Payment method |
| amount | Decimal | NOT NULL | Payment amount |
| status | String | Enum: pending/completed/failed/cancelled | Payment status |
| transaction_id | String | Nullable | External transaction ID |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← ORDER (belongs to)

---

#### 17. **DISCOUNT** (Order-Level Discounts)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| order_id | String | FK → ORDER, NOT NULL | Reference to order |
| discount_type | String | Not NULL | Type of discount |
| amount | Decimal | NOT NULL | Discount amount |
| reason | String | Nullable | Discount reason |
| created_by | String | Nullable | Admin who created |
| created_at | DateTime | NOT NULL | Creation date |

**Relationships:**
- N:1 ← ORDER (belongs to)

---

### GROUP 5: SUPPLIER & PROMOTION ENTITIES ⏳

#### 18. **SUPPLIER** ⏳ (Product Suppliers/Vendors)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| name | String | NOT NULL | Supplier name |
| address | String | Nullable | Street address |
| city | String | Nullable | City |
| state | String | Nullable | State/Province |
| postal_code | String | Nullable | ZIP/Postal code |
| country | String | Nullable | Country |
| phone | String | Nullable | Contact phone |
| telegram | String | Nullable | Telegram handle |
| email | String | Nullable | Email address |
| website | String | Nullable | Website URL |
| contact_person | String | Nullable | Contact person name |
| is_active | Boolean | DEFAULT true | Enable/disable |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- 1:N → PRODUCT_SUPPLIER (supplies products)

---

#### 19. **PRODUCT_SUPPLIER** ⏳ (Product-Supplier Mapping)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| product_id | UUID | FK → PRODUCT, NOT NULL | Reference to product |
| supplier_id | UUID | FK → SUPPLIER, NOT NULL | Reference to supplier |
| supplier_code | String | Nullable | Supplier's product code |
| lead_time_days | Integer | NOT NULL | Lead time in days |
| min_order_qty | Integer | DEFAULT 1 | Minimum order quantity |
| unit_cost | Decimal | NOT NULL | Cost per unit |
| is_active | Boolean | DEFAULT true | Active supplier |
| created_at | DateTime | NOT NULL | Creation date |

**Relationships:**
- N:1 ← PRODUCT (belongs to)
- N:1 ← SUPPLIER (belongs to)

---

#### 20. **COUPON/PROMOTION** ⏳ (Discount Codes)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| code | String | UNIQUE, NOT NULL | Coupon code |
| type | String | Enum: percentage/fixed_amount/buy_x_get_y | Discount type |
| value | Decimal | NOT NULL | Discount value |
| min_purchase_amount | Decimal | DEFAULT 0 | Minimum purchase |
| max_discount_amount | Decimal | Nullable | Maximum discount cap |
| valid_from | DateTime | NOT NULL | Start date |
| valid_until | DateTime | NOT NULL | End date |
| usage_limit | Integer | Nullable | Max uses allowed |
| used_count | Integer | DEFAULT 0 | Times used |
| applicable_categories | JSON | Nullable | Applicable categories |
| applicable_brands | JSON | Nullable | Applicable brands |
| is_active | Boolean | DEFAULT true | Active/inactive |
| created_at | DateTime | NOT NULL | Creation date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- Applied via CART.coupon_code field
- 1:N → NOTIFICATION (when promotion starts)

---

#### 22. **NOTIFICATION** 📢 (User Notifications)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| user_id | UUID | FK → USER, NOT NULL | Recipient user |
| type | String | Enum: promo/order/system/support | Notification type |
| title | String | NOT NULL | Notification title |
| message | String | NOT NULL | Notification message |
| related_entity_type | String | Nullable | Entity type (promotion/order/ticket) |
| related_entity_id | String | Nullable | Entity ID (promotion_id/order_id/ticket_id) |
| is_read | Boolean | DEFAULT false | Read/unread flag |
| action_url | String | Nullable | Deep link (e.g., /orders/123) |
| created_at | DateTime | NOT NULL | Creation date |
| read_at | DateTime | Nullable | When user read it |

**Relationships:**
- N:1 ← USER (sent to)
- From: COUPON/PROMOTION (promo started)
- From: ORDER (status changed)
- From: SUPPORT_TICKET (new response)

**Example Flows:**
- Promotion starts → NOTIFICATION created with type="promo", related_entity_id=promotion_id
- Order status changes → NOTIFICATION created with type="order", related_entity_id=order_id
- Support ticket response → NOTIFICATION created with type="support", related_entity_id=ticket_id

---

#### 23. **SUPPORT_TICKET** 🎫 (Customer Support)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| user_id | UUID | FK → USER, NOT NULL | Ticket creator |
| order_id | String | FK → ORDER, Nullable | Related order (optional) |
| subject | String | NOT NULL | Ticket subject |
| description | String | NOT NULL | Issue description |
| category | String | Enum: product/delivery/payment/return/other | Ticket category |
| priority | String | Enum: low/medium/high/urgent | Priority level |
| status | String | Enum: open/in_progress/resolved/closed | Ticket status |
| assigned_to | String | Nullable | Admin user assigned |
| created_at | DateTime | NOT NULL | Creation date |
| resolved_at | DateTime | Nullable | Resolution date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← USER (created by)
- N:1 ← ORDER (optional, related to)
- 1:N → SUPPORT_TICKET_REPLY (responses)
- 1:N → NOTIFICATION (replies trigger notification)

---

#### 24. **SUPPORT_TICKET_REPLY** 💬 (Ticket Responses)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| ticket_id | UUID | FK → SUPPORT_TICKET, NOT NULL | Parent ticket |
| user_id | UUID | FK → USER, NOT NULL | Reply author (admin or customer) |
| message | String | NOT NULL | Reply text |
| attachment_url | String | Nullable | Screenshot/document URL |
| is_admin_reply | Boolean | DEFAULT false | Admin or customer reply |
| created_at | DateTime | NOT NULL | Creation date |

**Relationships:**
- N:1 ← SUPPORT_TICKET (belongs to)
- N:1 ← USER (written by)
- Triggers: NOTIFICATION creation

---

#### 21. **PRODUCT_REVIEW** (Customer Reviews)
| Attribute | Type | Constraints | Notes |
|-----------|------|-------------|-------|
| id | UUID | PK, NOT NULL | Unique identifier |
| user_id | UUID | FK → USER, NOT NULL | Reviewer |
| product_id | UUID | FK → PRODUCT, NOT NULL | Reviewed product |
| rating | Integer | Range: 1-5 | Star rating |
| title | String | Nullable | Review title |
| review_text | String | Nullable | Review content |
| helpful_count | Integer | DEFAULT 0 | Helpful votes |
| created_at | DateTime | NOT NULL | Review date |
| updated_at | DateTime | NOT NULL | Last update |

**Relationships:**
- N:1 ← USER (written by)
- N:1 ← PRODUCT (reviews)

---

## 🔗 COMPLETE RELATIONSHIP MAPPING

### One-to-Many (1:N) Relationships
```
USER (1) ──────────► (N) CUSTOMER_PROFILE
USER (1) ──────────► (N) CART
USER (1) ──────────► (N) ORDER
USER (1) ──────────► (N) WISHLIST
USER (1) ──────────► (N) PRODUCT_REVIEW

CATEGORY (1) ──────────► (N) PRODUCT
BRAND (1) ──────────► (N) PRODUCT

PRODUCT (1) ──────────► (N) PRODUCT_IMAGE
PRODUCT (1) ──────────► (N) PRODUCT_VARIANT
PRODUCT (1) ──────────► (N) PRODUCT_SUPPLIER
PRODUCT (1) ──────────► (N) PRODUCT_REVIEW
PRODUCT (1) ──────────► (N) CART_ITEM
PRODUCT (1) ──────────► (N) WISHLIST_ITEM
PRODUCT (1) ──────────► (N) ORDER_ITEM

CART (1) ──────────► (N) CART_ITEM
WISHLIST (1) ──────────► (N) WISHLIST_ITEM
ORDER (1) ──────────► (N) ORDER_ITEM
ORDER (1) ──────────► (N) ORDER_SHIPMENT
ORDER (1) ──────────► (N) PAYMENT
ORDER (1) ──────────► (N) DISCOUNT

SHIPPING_METHOD (1) ──────────► (N) ORDER_SHIPMENT

SUPPLIER (1) ──────────► (N) PRODUCT_SUPPLIER

CATEGORY (1) ──◄──── (N) CATEGORY (Self-Reference)
```

### Many-to-One (N:1) Relationships (Inverse)
```
CUSTOMER_PROFILE (N) ◄────────────── (1) USER
CART (N) ◄────────────── (1) USER
ORDER (N) ◄────────────── (1) USER
WISHLIST (N) ◄────────────── (1) USER
PRODUCT_REVIEW (N) ◄────────────── (1) USER

PRODUCT (N) ◄────────────── (1) CATEGORY
PRODUCT (N) ◄────────────── (1) BRAND

PRODUCT_IMAGE (N) ◄────────────── (1) PRODUCT
PRODUCT_VARIANT (N) ◄────────────── (1) PRODUCT
PRODUCT_SUPPLIER (N) ◄────────────── (1) PRODUCT
PRODUCT_REVIEW (N) ◄────────────── (1) PRODUCT
CART_ITEM (N) ◄────────────── (1) PRODUCT
WISHLIST_ITEM (N) ◄────────────── (1) PRODUCT
ORDER_ITEM (N) ◄────────────── (1) PRODUCT

CART_ITEM (N) ◄────────────── (1) CART
WISHLIST_ITEM (N) ◄────────────── (1) WISHLIST
ORDER_ITEM (N) ◄────────────── (1) ORDER
ORDER_SHIPMENT (N) ◄────────────── (1) ORDER
PAYMENT (N) ◄────────────── (1) ORDER
DISCOUNT (N) ◄────────────── (1) ORDER

ORDER_SHIPMENT (N) ◄────────────── (1) SHIPPING_METHOD

PRODUCT_SUPPLIER (N) ◄────────────── (1) SUPPLIER

CATEGORY (N) ◄────────────── (1) CATEGORY (Self-Reference)
```

### Optional/Nullable Foreign Keys
```
USER.id (in CUSTOMER_PROFILE) → Required
CART.user_id → Nullable (supports guest carts via session_id)
WISHLIST.user_id → Nullable (supports guest wishlists via session_id)
CART_ITEM.variant_id → Nullable (variants are optional)
WISHLIST_ITEM.variant_id → Nullable (variants are optional)
ORDER_ITEM.variant_id → Not applicable (product snapshot)
CATEGORY.parent_id → Nullable (top-level categories)
PRODUCT_VARIANT.price → Nullable (inherits from product)
COUPON.applicable_categories → Nullable/JSON
COUPON.applicable_brands → Nullable/JSON
ORDER.shipping_id → Nullable
ORDER.payment_id → Nullable
ORDER_SHIPMENT.tracking_number → Nullable
PAYMENT.transaction_id → Nullable
```

---

## 📌 DRAWING GUIDE FOR YOUR THESIS

### **Simplified Drawing Order** (Recommended for manual drawing):

**Step 1: Draw Core Users** (Top)
```
┌──────────┐      ┌──────────────────┐
│   USER   │──────│ CUSTOMER_PROFILE │
└──────────┘      └──────────────────┘
```

**Step 2: Draw Catalog** (Left-Right)
```
┌──────────┐   ┌──────────┐
│CATEGORY  │   │  BRAND   │
└────┬─────┘   └────┬─────┘
     │              │
     └────┬─────────┘
          │
      ┌───▼────────┐
      │  PRODUCT   │
      └───┬────────┘
          │
    ┌─────┼──────┐
    │     │      │
┌───▼──┐ ┌─▼──┐ ┌──▼────────┐
│IMAGE │ │VAR.│ │SUPPLIER   │
└──────┘ └────┘ └───────────┘
```

**Step 3: Draw Transactions** (Bottom)
```
USER → CART → CART_ITEM → PRODUCT
   → ORDER → ORDER_ITEM → PRODUCT
   → WISHLIST → WISHLIST_ITEM → PRODUCT
   → REVIEW → PRODUCT
```

**Step 4: Draw Supporting** (Right side)
```
ORDER → SHIPPING → ORDER_SHIPMENT
     → PAYMENT
     → DISCOUNT
```

---

## ✅ IMPLEMENTATION CHECKLIST

### Currently Working (Ready for Thesis) ✅
- [x] USER entity with authentication
- [x] PRODUCT catalog (18 items)
- [x] CATEGORY with hierarchy
- [x] BRAND master data
- [x] CUSTOMER_PROFILE concept
- [x] PRODUCT_IMAGE references

### Ready to Implement ⏳
- [ ] CART & CART_ITEM (API pending)
- [ ] ORDER & ORDER_ITEM (local manager active)
- [ ] WISHLIST & WISHLIST_ITEM (model designed)
- [ ] PRODUCT_REVIEW (basic structure ready)
- [ ] PRODUCT_VARIANT (referenced)

### Future Enhancements 📅
- [ ] PAYMENT tracking
- [ ] ORDER_SHIPMENT & SHIPPING_METHOD
- [ ] SUPPLIER & PRODUCT_SUPPLIER
- [ ] COUPON advanced features
- [ ] DISCOUNT management

---

## 📐 ENTITY COUNT SUMMARY

| Category | Count | Status |
|----------|-------|--------|
| **User Entities** | 2 | ✅ Core implemented |
| **Catalog Entities** | 6 | ✅ Core + variants |
| **Transaction Entities** | 7 | ⏳ Designed, partial impl |
| **Shipping/Payment Entities** | 4 | ⏳ Designed |
| **Supplier Entities** | 2 | ⏳ Designed |
| **Review Entities** | 1 | ⏳ Designed |
| **Promotion Entities** | 1 | ⏳ Designed |
| **TOTAL ENTITIES** | **23** | **Ready for thesis** |

---

## 🎯 KEY FEATURES BY ENTITY GROUP

### ✅ FULLY IMPLEMENTED
1. **Authentication**: User registration, login, token management
2. **Product Catalog**: Browse, search, filter by category/brand
3. **Product Details**: Full specs, pricing, inventory, images
4. **Categories**: Hierarchical organization with parent-child
5. **Brands**: Manufacturer information and product association

### ⏳ DESIGNED BUT PENDING API
1. **Shopping Cart**: Item management, quantity updates, discounts
2. **Wishlist**: Save products for later
3. **Orders**: Purchase history and status tracking
4. **Variants**: Size, color, and other product options
5. **Coupons**: Discount code management

### 📅 PLANNED FEATURES
1. **Shipping**: Multiple methods, tracking
2. **Payments**: Multiple payment methods, status tracking
3. **Reviews**: Customer ratings and feedback
4. **Suppliers**: Vendor management and procurement
5. **Advanced Discounts**: Rule-based, promotional campaigns

---

## 💾 SQL SCHEMA HINTS

### Data Types
- **UUID/GUID**: User, Product, Category, Brand, most entities
- **String**: Order numbers (e.g., "BD-1001")
- **Decimal**: All monetary values (price, discount, amount)
- **Integer**: Counts, quantities, sort order
- **Boolean**: Flags (is_active, is_featured, track_inventory)
- **DateTime**: Timestamps (created_at, updated_at)
- **JSON**: Flexible data (metadata, attributes, applicable_categories)
- **Enum**: Status fields (payment_method, order_status, shipment_status)

### Indexing Strategy
```
Primary Keys: All PK fields
Foreign Keys: All FK fields (category_id, brand_id, user_id, etc.)
Unique: email (USER), slug (PRODUCT, CATEGORY, BRAND)
Search: name, code, sku fields
Timeline: created_at, updated_at for sorting/filtering
```

### Constraints
- NOT NULL: All primary information (name, price, etc.)
- UNIQUE: Identifiers (email, slug, code)
- CHECK: Ranges (rating 1-5, quantity > 0)
- DEFAULT: Flags (is_active=true), counters (count=0)
- FOREIGN KEY: Referential integrity with CASCADE rules

---

## 🚀 NEXT STEPS FOR YOUR THESIS

1. **Draw the ER Diagram**: Use the visual structure provided above
2. **Document Each Entity**: Include all attributes from the table
3. **Show Relationships**: Draw lines with 1:N, N:M labels
4. **Add Cardinality**: Show (1)──N notation clearly
5. **Include Implementation Status**: Mark ✅ and ⏳ on diagram
6. **Create Data Dictionary**: Reference the attributes table
7. **Add Visual Legend**: Show Primary Key, Foreign Key, Optional notation
8. **Cross-reference with Code**: Point to actual Java models in Android app

---

**Document Version**: 2.0  
**Last Updated**: September 27, 2026  
**Ready for**: Thesis Documentation  
**Total Entities**: 23 entities with complete attribute definitions  
**Drawing Complexity**: Intermediate (can be split into 3-4 logical sections)

---

## 🎨 QUICK REFERENCE - EASY DRAWING CHECKLIST

### **For Your Thesis Drawing - Print This:**

#### **Section 1: Users (Top-Left)**
- [ ] USER (blue box, large)
- [ ] CUSTOMER_PROFILE (connected below)

#### **Section 2: Catalog (Top-Right to Center)**
- [ ] CATEGORY (self-referencing, left side)
- [ ] BRAND (right side)
- [ ] PRODUCT (center, largest entity)
- [ ] PRODUCT_IMAGE (small, below product)
- [ ] PRODUCT_VARIANT (small, below product)

#### **Section 3: Transactions (Bottom)**
- [ ] CART (connected to USER)
- [ ] CART_ITEM (connected to CART)
- [ ] ORDER (connected to USER)
- [ ] ORDER_ITEM (connected to ORDER)
- [ ] WISHLIST (connected to USER)
- [ ] WISHLIST_ITEM (connected to WISHLIST)

#### **Section 4: Supporting (Right-Bottom)**
- [ ] PRODUCT_REVIEW (reviews section)
- [ ] SHIPPING_METHOD (shipping section)
- [ ] ORDER_SHIPMENT (under SHIPPING_METHOD)
- [ ] PAYMENT (payment section)
- [ ] DISCOUNT (under PAYMENT)
- [ ] SUPPLIER (vendor section)
- [ ] PRODUCT_SUPPLIER (under SUPPLIER)
- [ ] COUPON/PROMOTION (promotions section)

---

## 📝 ENTITY SYMBOL GUIDE

When drawing, use these symbols:
```
Entity Type          Symbol      Color (Optional)
─────────────────────────────────────────
Implemented ✅       ▭▭▭▭▭       Blue/Green
Pending ⏳           ▭▭▭▭▭       Orange/Yellow
ID Field             ⊕ PK        Bold border
Foreign Key          ⊗ FK        Italic text
Nullable Field       (  )        Dashed line
One-to-Many          ───→        Solid arrow
Many-to-One          ←───        Solid arrow (reverse)
Self-Reference       ⟲           Curved arrow
```

---

## 🚀 THESIS PRESENTATION TIPS

1. **Draw in Sections**: Don't draw all 23 entities at once
2. **Use Color Coding**: Green for ✅ working, Orange for ⏳ pending
3. **Include Legend**: Show PK, FK, 1:N notation
4. **Add Title**: "Beckie-Deal E-Commerce Database Schema v2.0"
5. **Include Date**: "September 2026"
6. **Highlight Status**: Show which entities are implemented

---

## ✅ FINAL VERIFICATION

Your ER Diagram v2.0 now includes:
- ✅ 23 Total Entities (vs 12 in v1.0)
- ✅ Complete Attribute Definitions
- ✅ All Relationship Types (1:N, N:1, Self-Reference)
- ✅ Foreign Key Mapping
- ✅ Implementation Status
- ✅ Data Type Specifications
- ✅ Drawing Guide & Checklist
- ✅ Quick Reference Symbols
- ✅ SQL Hints for Implementation
- ✅ Thesis-Ready Format

**YOU'RE READY TO DRAW!** 🎨


---

## 🎨 DRAWIO CONNECTION GUIDE - PROMOTION, NOTIFICATION, SUPPORT_TICKET

### **Visual Diagram (Copy this structure into DrawIO)**

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    PROMOTION & NOTIFICATION SECTION                         │
└─────────────────────────────────────────────────────────────────────────────┘

                         ┌────────────────────────┐
                         │   COUPON/PROMOTION ⏳  │
                         ├────────────────────────┤
                         │ PK  id (UUID)          │
                         │     code               │
                         │     type               │
                         │     value              │
                         │ valid_from / until     │
                         │     is_active          │
                         └───────────┬────────────┘
                                     │
                                     │ 1:N
                                     │ (notification sent when promo starts)
                                     │
         ┌───────────────────────────▼─────────────────────────┐
         │                NOTIFICATION 📢                       │
         ├─────────────────────────────────────────────────────┤
         │ PK  id (UUID)                                       │
         │ FK  user_id ────┐                                   │
         │     type        │ (promo/order/system/support)     │
         │     title       │                                   │
         │     message     │                                   │
         │ related_entity_ │ ◄── Points to: promo_id, order_id │
         │ type/id         │     or ticket_id                   │
         │     is_read     │                                   │
         │ action_url      │ (e.g., /orders/123)              │
         │   created_at    │                                   │
         └────────┬────────┘                                    │
                  │                                             │
                  │ N:1 ◄────────────────────────────────────────┘
                  │
                  │ Goes to:
                  │
         ┌────────▼──────────────┐
         │      USER             │
         │   (receives notif)    │
         └───────────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│                       SUPPORT TICKET SECTION                                │
└─────────────────────────────────────────────────────────────────────────────┘

         ┌──────────────────────────┐
         │        USER              │
         │   (creates support       │
         │    ticket)               │
         └────────┬────────┬────────┘
                  │        │
                  │ 1:N    │ 1:N
                  │        │
         ┌────────▼──────┐ └──────┬────────────────────────┐
         │  SUPPORT_     │        │                        │
         │  TICKET 🎫    │        │ (optional, if         │
         ├───────────────┤        │  ticket about         │
         │ PK  id        │        │  specific order)      │
         │ FK  user_id   │◄───────┘                        │
         │ FK  order_id  │                                 │
         │ (Nullable)    │        ┌─────────────────────┐  │
         │     subject   │        │      ORDER          │  │
         │     category  │        │  (optional ref)     │  │
         │     priority  │        └─────────────────────┘  │
         │     status    │                                  │
         └────────┬──────┘                                  │
                  │                                         │
                  │ 1:N                                     │
                  │ (ticket has multiple replies)          │
                  │                                         │
         ┌────────▼──────────────────────┐                  │
         │ SUPPORT_TICKET_REPLY 💬       │                  │
         ├───────────────────────────────┤                  │
         │ PK  id (UUID)                 │                  │
         │ FK  ticket_id                 │                  │
         │ FK  user_id                   │                  │
         │ (admin or customer writing)   │                  │
         │     message                   │                  │
         │ is_admin_reply (Boolean)      │                  │
         │   created_at                  │                  │
         └────────┬──────────────────────┘                  │
                  │                                         │
                  │ 1:N (creates notification)              │
                  │ Triggers: New message → notification    │
                  │ to original ticket creator              │
                  │                                         │
         ┌────────▼──────────────────────────────────┐      │
         │       NOTIFICATION (type: support)       │      │
         │   "New reply to your support ticket"     │      │
         │   related_entity_id = ticket_id          │      │
         └───────────────────────────────────────────┘      │
                                                            │
                  ◄─ Goes back to USER ─────────────────────┘
```

---

### **HOW TO DRAW IN DRAWIO:**

#### **Step 1: Connect PROMOTION → NOTIFICATION**
```
1. Draw line from PROMOTION.id → NOTIFICATION
2. Label on line: "1:N"
3. Add note: "When promotion starts, create notification"
4. NOTIFICATION.related_entity_type = "promotion"
5. NOTIFICATION.related_entity_id = promotion.id
```

#### **Step 2: Connect NOTIFICATION → USER**
```
1. Draw line from NOTIFICATION.user_id → USER.id
2. Label on line: "N:1"
3. This shows: "User receives notification"
4. One user can have MANY notifications
```

#### **Step 3: Connect ORDER → NOTIFICATION**
```
1. Draw line from ORDER.id → NOTIFICATION
2. Label on line: "1:N (optional)"
3. NOTIFICATION.related_entity_type = "order"
4. NOTIFICATION.related_entity_id = order.id
5. Example: "Your order BD-1001 has been shipped"
```

#### **Step 4: Connect USER → SUPPORT_TICKET**
```
1. Draw line from USER.id → SUPPORT_TICKET.user_id
2. Label on line: "1:N"
3. This shows: "User creates support tickets"
4. One user can create MANY support tickets
```

#### **Step 5: Connect ORDER → SUPPORT_TICKET (Optional)**
```
1. Draw line from ORDER.id → SUPPORT_TICKET.order_id
2. Label on line: "1:N (optional)"
3. Make line DASHED (because order_id is Nullable)
4. Example: "This ticket is about order BD-1001"
5. Not all tickets are about orders (some are general questions)
```

#### **Step 6: Connect SUPPORT_TICKET → SUPPORT_TICKET_REPLY**
```
1. Draw line from SUPPORT_TICKET.id → SUPPORT_TICKET_REPLY.ticket_id
2. Label on line: "1:N"
3. One ticket has MANY replies
4. Each reply belongs to ONE ticket
```

#### **Step 7: Connect USER → SUPPORT_TICKET_REPLY**
```
1. Draw line from USER.id → SUPPORT_TICKET_REPLY.user_id
2. Label on line: "N:1"
3. Admin and customer both write replies
4. Multiple people can write replies to same ticket
```

#### **Step 8: Connect SUPPORT_TICKET_REPLY → NOTIFICATION**
```
1. Draw line from SUPPORT_TICKET_REPLY → NOTIFICATION
2. Label on line: "1:N (trigger)"
3. NOTIFICATION.type = "support"
4. NOTIFICATION.related_entity_type = "ticket"
5. NOTIFICATION.related_entity_id = ticket_id
6. Example: "Admin replied to your support ticket"
```

---

### **QUICK REFERENCE TABLE: Where Each Entity Connects**

| Entity | Connects FROM | Connects TO | Relationship | Notes |
|--------|---------------|-------------|--------------|-------|
| **PROMOTION** | - | NOTIFICATION | 1:N | When promo starts → notify user |
| **NOTIFICATION** | PROMOTION, ORDER, SUPPORT_TICKET_REPLY | USER | 1:N to source, N:1 to USER | Central hub for all notifications |
| **SUPPORT_TICKET** | USER, ORDER | SUPPORT_TICKET_REPLY | N:1 from USER, 1:N to replies | User creates, has many replies |
| **SUPPORT_TICKET_REPLY** | SUPPORT_TICKET, USER | NOTIFICATION | N:1 from both, 1:N to notif | Reply triggers notification |
| **ORDER** | - | NOTIFICATION, SUPPORT_TICKET | 1:N to both (optional) | Order status → notification; can link to ticket |

---

### **EXAMPLE REAL-WORLD FLOWS:**

#### **Flow 1: User Gets Promotion Notification**
```
1. Admin creates PROMOTION
   - code: "SAVE20"
   - valid_from: today
   - is_active: true

2. System creates NOTIFICATION
   - user_id: all_users
   - type: "promo"
   - title: "Special Offer: 20% Off!"
   - message: "Save 20% on all products this week"
   - related_entity_type: "promotion"
   - related_entity_id: promotion.id
   - action_url: "/promotions/promo123"

3. User sees notification in app
   - Tap notification → goes to promotion details
   - Can apply coupon code SAVE20 at checkout
```

#### **Flow 2: User Gets Order Status Notification**
```
1. Order placed: BD-1001
   - status: PROCESSING
   
2. Admin updates status → SHIPPED
   - System creates NOTIFICATION
   - user_id: order.user_id
   - type: "order"
   - title: "Your Order is Shipped!"
   - message: "Order BD-1001 shipped. Tracking: FDX123456"
   - related_entity_type: "order"
   - related_entity_id: "BD-1001"
   - action_url: "/orders/BD-1001"

3. User sees notification → taps → sees order details
```

#### **Flow 3: User Gets Support Ticket Response Notification**
```
1. User creates SUPPORT_TICKET
   - subject: "Product damaged"
   - description: "The item arrived broken"
   - status: "open"

2. Admin writes SUPPORT_TICKET_REPLY
   - message: "We're sorry to hear that. We'll arrange a replacement."
   - is_admin_reply: true
   - System creates NOTIFICATION
   - user_id: ticket.user_id
   - type: "support"
   - title: "Response to your support ticket"
   - message: "[Admin]: We're sorry to hear that..."
   - related_entity_type: "ticket"
   - related_entity_id: ticket.id
   - action_url: "/support/tickets/ticket123"

3. User sees notification → taps → sees ticket with new reply
   - Can reply back to continue conversation
```

---

### **DRAWIO POSITIONING GUIDE**

**Recommended layout for your diagram:**

```
┌──────────────────────────────────────────────────────────────┐
│  TOP: Main Catalog (CATEGORY, BRAND, PRODUCT)              │
├──────────────────────────────────────────────────────────────┤
│  MIDDLE LEFT: Transactions (CART, ORDER, WISHLIST)          │
├──────────────────────────────────────────────────────────────┤
│  MIDDLE RIGHT: PROMOTION → NOTIFICATION → USER (hub)        │
├──────────────────────────────────────────────────────────────┤
│  BOTTOM: SUPPORT_TICKET → SUPPORT_TICKET_REPLY → NOTIF      │
└──────────────────────────────────────────────────────────────┘
```

Or in DrawIO:
```
              CATEGORY
                 │ 1:N
                 ▼
              PRODUCT ◄─── BRAND
               │ 1:N
               ├─► PRODUCT_IMAGE
               └─► PRODUCT_VARIANT

USER ◄─ CUSTOMER_PROFILE
 │
 ├─► (1:N) CART ─► CART_ITEM ─► PRODUCT
 │
 ├─► (1:N) ORDER ─► ORDER_ITEM ─► PRODUCT
 │
 ├─► (1:N) WISHLIST ─► WISHLIST_ITEM ─► PRODUCT
 │
 ├─ (1:N) ──────────────────┐
 │                          ▼
 │              ┌──────────────────────┐
 │              │  PROMOTION ──(1:N)─► NOTIFICATION
 │              │                      │
 │              │                      │ N:1 (user receives)
 │              │                      ▼
 │              │                    USER ◄─ (Central Hub)
 │              └──────────────────────┘
 │
 └─ (1:N) ──────────────────┐
                            ▼
                 ┌──────────────────────┐
                 │ SUPPORT_TICKET       │
                 │  └─► (1:N) REPLY     │
                 │       └─(1:N)►NOTIF  │
                 └──────────────────────┘
```

---

### **SUMMARY FOR YOUR THESIS**

**The 3 New Entities (Why they're separate):**

1. **PROMOTION/COUPON** ✏️
   - Lives independently in system
   - Can be active without anyone buying
   - Creates notifications when active

2. **NOTIFICATION** 📢
   - **Central Hub** - connects to everything
   - User receives notifications from:
     - PROMOTION starts
     - ORDER status changes
     - SUPPORT_TICKET has reply
   - Enables real-time user communication

3. **SUPPORT_TICKET** 🎫
   - User-initiated support requests
   - Can be about a specific order (optional FK)
   - Has conversation thread (SUPPORT_TICKET_REPLY)
   - Replies trigger notifications

**In DrawIO, they're separate because:**
- They're **optional features** (not core to browsing/buying)
- They **communicate** (notify users) rather than store transaction data
- They have **one-way dependencies** (promotion → notification, not vice versa)

---

**Now go back to DrawIO and add these connections!** 🎨

Would you like me to also create a simplified DrawIO file (.drawio XML) that you can import directly?
