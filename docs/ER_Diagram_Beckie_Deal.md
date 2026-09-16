# Beckie-Deal E-Commerce System - Entity Relationship Diagram

## Database Schema Design

This ER diagram represents the Beckie-Deal e-commerce application's data model based on the API structure and client-side models.

**IMPLEMENTATION STATUS:**
- ✅ **WORKING**: User, Product, Category, Brand (APIs implemented and working)
- ⏳ **PLANNED**: Cart, Wishlist, Order (Models designed, APIs pending implementation)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          BECKIE-DEAL E-COMMERCE SYSTEM                          │
│                               ER DIAGRAM                                         │
└─────────────────────────────────────────────────────────────────────────────────┘


                                    ┌──────────────┐
                                    │    USER ✅   │
                                    ├──────────────┤
                                    │ PK id (UUID) │
                                    │    email     │
                                    │  first_name  │
                                    │  last_name   │
                                    │    phone     │
                                    │    name      │
                                    └──────┬───────┘
                                           │
                                           │ 1
                                           │
                    ┌──────────────────────┼──────────────────────┐
                    │                      │                      │
                    │ 1                    │ 1                    │ 1
                    │                      │                      │
           ┌────────▼────────┐    ┌───────▼────────┐    ┌───────▼────────┐
           │  WISHLIST ⏳    │    │   CART ⏳      │    │   ORDER ⏳     │
           ├─────────────────┤    ├────────────────┤    ├────────────────┤
           │ PK id (int)     │    │ PK id (int)    │    │ PK id (string) │
           │ FK user_id      │    │ FK user_id     │    │ FK user_id     │
           │   session_id    │    │  session_id    │    │   order_no     │
           │    name         │    │    status      │    │     date       │
           │  description    │    │  coupon_code   │    │  total_amount  │
           │   is_public     │    │discount_total  │    │    status      │
           │   is_active     │    │   subtotal     │    │   created_at   │
           │   metadata      │    │  grand_total   │    └────────────────┘
           └────────┬────────┘    │   metadata     │
                    │              └────────┬───────┘
                    │ 1                     │ 1
                    │                       │
                    │ N                     │ N
                    │                       │
           ┌────────▼────────┐    ┌────────▼────────┐    ┌────────────────┐
           │ WISHLIST_ITEM⏳ │    │ CART_ITEM ⏳   │    │  ORDER_ITEM ⏳ │
           ├─────────────────┤    ├─────────────────┤    ├────────────────┤
           │ PK id (int)     │    │ PK id (int)     │    │ PK id (int)    │
           │ FK wishlist_id  │    │ FK cart_id      │    │ FK order_id    │
           │ FK product_id   │    │ FK product_id   │    │ FK product_id  │
           │ FK variant_id   │    │ FK variant_id   │    │ product_name   │
           │   quantity      │    │   quantity      │    │ product_price  │
           │   metadata      │    │     price       │    │ product_image  │
           └────────┬────────┘    │   metadata      │    │   quantity     │
                    │              └────────┬────────┘    └────────┬───────┘
                    │ N                     │ N                    │ N
                    │                       │                      │
                    │                       │                      │
                    └───────────────────────┼──────────────────────┘
                                            │
                                            │ 1
                                            │
                                ┌───────────▼───────────┐
                                │    PRODUCT ✅         │
                                ├───────────────────────┤
                                │ PK id (UUID)          │
                                │ FK category_id        │
                                │ FK brand_id           │
                                │    name               │
                                │    slug               │
                                │    sku                │
                                │    barcode            │
                                │ short_description     │
                                │  description          │
                                │    price              │
                                │  sale_price           │
                                │  cost_price           │
                                │ stock_quantity        │
                                │ min_stock_alert       │
                                │ track_inventory       │
                                │   in_stock            │
                                │   weight              │
                                │   length              │
                                │   width               │
                                │   height              │
                                │  thumbnail            │
                                │  meta_title           │
                                │ meta_description      │
                                │  is_active            │
                                │  is_featured          │
                                │  is_digital           │
                                │  views_count          │
                                │  sales_count          │
                                │  sort_order           │
                                └──────┬────────┬───────┘
                                       │        │
                                       │ N      │ N
                                       │        │
                                ┌──────▼────┐   │
                                │ BRAND ✅  │   │
                                ├───────────┤   │
                                │PK id(UUID)│   │
                                │   name    │   │
                                │   slug    │   │
                                │   logo    │   │
                                │  website  │   │
                                │description│   │
                                │meta_title │   │
                                │ meta_desc │   │
                                │is_active  │   │
                                │is_featured│   │
                                │sort_order │   │
                                │products_  │   │
                                │  count    │   │
                                └───────────┘   │
                                                │
                                        ┌───────▼────────┐
                                        │  CATEGORY ✅   │
                                        ├────────────────┤
                                        │ PK id (UUID)   │
                                        │ FK parent_id   │◄──┐
                                        │    name        │   │
                                        │    slug        │   │
                                        │ description    │   │
                                        │    image       │   │
                                        │    icon        │   │
                                        │  is_active     │   │
                                        │  is_featured   │   │
                                        │  sort_order    │   │
                                        │products_count  │   │
                                        └────────────────┘   │
                                                │             │
                                                └─────────────┘
                                            Self-Reference
                                          (Parent Category)


                ┌──────────────────────────┐          ┌──────────────────────────┐
                │  PRODUCT_VARIANT ⏳      │          │     COUPON ⏳           │
                │    (Referenced)          │          ├──────────────────────────┤
                ├──────────────────────────┤          │ PK id (UUID)             │
                │ PK id (UUID)             │          │    code                  │
                │ FK product_id            │          │    type                  │
                │    sku                   │          │    value                 │
                │    name                  │          │  min_purchase            │
                │    price                 │          │  max_discount            │
                │  stock_quantity          │          │  valid_from              │
                │    attributes            │          │  valid_until             │
                └──────────────────────────┘          │   is_active              │
                                                      │  usage_limit             │
                                                      │  used_count              │
                                                      └──────────────────────────┘
```

## Implementation Status

### ✅ **FULLY IMPLEMENTED** (API Working)
- **USER**: Authentication endpoints working
- **PRODUCT**: Full CRUD and listing working (18 products available)
- **CATEGORY**: Master data endpoint working
- **BRAND**: Master data endpoint working

### ⏳ **DESIGNED BUT PENDING** (Models exist, APIs return 404)
- **CART** & **CART_ITEM**: Models designed, currently using local CartManager
- **WISHLIST** & **WISHLIST_ITEM**: Models designed, API not implemented
- **ORDER** & **ORDER_ITEM**: Using local OrderManager for now
- **PRODUCT_VARIANT**: Referenced but not fully implemented
- **COUPON**: Model exists, API endpoint not tested

## Entity Descriptions

### Core Entities (✅ Working)

#### 1. **USER** ✅
- Central entity representing registered customers
- Identified by UUID
- Contains authentication and profile information
- **API Status**: `/auth/login`, `/auth/register` working
- Relationships: 1-to-many with Cart, 1-to-many with Wishlist, 1-to-many with Orders

#### 2. **PRODUCT** ✅
- Main product entity with extensive attributes
- Contains pricing, inventory, and SEO metadata
- Supports physical and digital products
- Tracks views and sales metrics
- **API Status**: `/products` working (18 products available)
- Relationships: Many-to-1 with Category, Many-to-1 with Brand

#### 3. **CATEGORY** ✅
- Hierarchical category system (self-referencing)
- Supports parent-child relationships for subcategories
- Contains presentation metadata (image, icon)
- Tracks product count per category
- **API Status**: `/master/categories` working
- Self-referencing relationship via `parent_id`

#### 4. **BRAND** ✅
- Product brand/manufacturer information
- Contains branding assets (logo, website)
- Supports featured brands
- Tracks product count per brand
- **API Status**: `/master/brands` working

### Transaction Entities (⏳ Pending API Implementation)

#### 5. **CART** ⏳
- Shopping cart for user or guest (session)
- Tracks totals and discount application
- Status-based (active, completed, abandoned)
- Supports coupon code application
- Can be associated with authenticated user or guest session
- **API Status**: All `/cart/*` endpoints return 404
- **Current Implementation**: Using local `CartManager` singleton

#### 6. **CART_ITEM** ⏳
- Individual product entries in cart
- Stores quantity and price at time of addition
- References product and optional variant
- Contains metadata for customization
- **API Status**: Pending (part of Cart API)
- Belongs to Cart (cart_id FK)

#### 7. **WISHLIST** ⏳
- Saved items for later purchase
- Can be public or private
- Named wishlists support
- Session-based for guests, user-based for authenticated
- **API Status**: All `/wishlist/*` endpoints return 404
- Model designed but API not implemented

#### 8. **WISHLIST_ITEM** ⏳
- Individual product entries in wishlist
- References product and optional variant
- Tracks quantity desired
- **API Status**: Pending (part of Wishlist API)
- Belongs to Wishlist (wishlist_id FK)

#### 9. **ORDER** ⏳
- Completed purchases after checkout
- Contains order number, date, status
- Tracks total amount
- **API Status**: GET `/orders` works (returns empty array), POST needs cart_id
- **Current Implementation**: Using local `OrderManager` singleton
- Status enum: PROCESSING, SHIPPED, DELIVERED, CANCELLED

#### 10. **ORDER_ITEM** ⏳
- Individual items in a placed order
- Snapshot of product data at time of purchase
- Stores product name, price, image, quantity
- **Current Implementation**: Part of OrderManager.Order class
- Belongs to Order (order_id FK)

### Supporting Entities (⏳ Pending)

#### 11. **PRODUCT_VARIANT** ⏳
- Product variations (size, color, etc.)
- Own pricing and stock
- Attribute-based differentiation
- **Status**: Referenced in CartItem and WishlistItem but not fully implemented

#### 12. **COUPON** ⏳
- Discount codes and promotions
- Time-based validity
- Usage tracking and limits
- Minimum purchase requirements
- **API Status**: `/coupons/apply` endpoint exists but not fully tested
- Applied to Cart via coupon_code field

## Relationship Summary

### One-to-Many Relationships (1:N)
1. **User → Carts** (1:N) - User can have multiple shopping carts
2. **User → Wishlists** (1:N) - User can have multiple wishlists
3. **User → Orders** (1:N) - User can place multiple orders
4. **Cart → Cart Items** (1:N) - Cart contains multiple items
5. **Wishlist → Wishlist Items** (1:N) - Wishlist contains multiple items
6. **Order → Order Items** (1:N) - Order contains multiple items
7. **Category → Products** (1:N) - Category contains multiple products
8. **Brand → Products** (1:N) - Brand has multiple products
9. **Product → Cart Items** (1:N) - Product can be in multiple carts
10. **Product → Wishlist Items** (1:N) - Product can be in multiple wishlists
11. **Product → Order Items** (1:N) - Product can be in multiple orders
12. **Product → Product Variants** (1:N) - Product can have multiple variants

### Self-Referencing Relationships
- **Category → Parent Category** (N:1) - Hierarchical category structure

### Many-to-One Relationships (N:1)
1. **Product → Category** (N:1) - Each product belongs to one category
2. **Product → Brand** (N:1) - Each product belongs to one brand
3. **Cart Item → Cart** (N:1) - Each cart item belongs to one cart
4. **Cart Item → Product** (N:1) - Each cart item references one product
5. **Wishlist Item → Wishlist** (N:1) - Each wishlist item belongs to one wishlist
6. **Wishlist Item → Product** (N:1) - Each wishlist item references one product
7. **Order Item → Order** (N:1) - Each order item belongs to one order
8. **Order Item → Product** (N:1) - Each order item references one product (snapshot)

### Optional Relationships (Nullable Foreign Keys)
- **Cart.user_id** (nullable) - Supports guest carts via session_id
- **Wishlist.user_id** (nullable) - Supports guest wishlists via session_id
- **CartItem.product_variant_id** (nullable) - Product variants are optional
- **WishlistItem.product_variant_id** (nullable) - Product variants are optional

## Key Features

1. **Guest and User Support**: Both Cart and Wishlist support session-based (guest) and user-based (authenticated) modes via nullable `user_id` and `session_id` fields

2. **Product Organization**: Hierarchical categories (self-referencing parent_id) + brand classification for flexible product discovery

3. **Inventory Management**: Track stock levels (`stock_quantity`), set minimum alerts (`min_stock_alert`), and manage availability (`in_stock`)

4. **Pricing Flexibility**: 
   - Regular price (price)
   - Sale price (sale_price) for discounts
   - Cost price (cost_price) for profit tracking

5. **Metadata Storage**: Generic metadata fields (JSON/Object) allow custom data storage for extensibility without schema changes

6. **Product Variants**: Support for product variations (size, color, etc.) through optional variant system referenced in cart and wishlist items

7. **Discount System**: Coupon-based discounts applied at cart level via `coupon_code`, calculates `discount_total` and `grand_total`

8. **SEO Support**: Meta title and description fields for products, categories, and brands for search engine optimization

9. **Order Management**: 
   - Local order tracking with status (PROCESSING, SHIPPED, DELIVERED, CANCELLED)
   - Order items store product snapshot at time of purchase
   - Currently using in-memory OrderManager, designed for API integration

10. **Current Workaround**:
    - Cart: Using local `CartManager` singleton for in-memory cart management
    - Order: Using local `OrderManager` singleton for order history
    - Designed to migrate to API when backend is ready

## Technical Notes

### Data Types & IDs
- **UUID** used for primary entities (User, Product, Category, Brand) for distributed system compatibility and uniqueness
- **Integer IDs** used for transactional entities (Cart, Wishlist, Items, Order Items) for performance and auto-increment
- **String IDs** for Order (e.g., "BD-1001") for human-readable order numbers

### Price Handling
- **String price fields** accommodate decimal precision without floating-point issues
- Convert to double for calculations using helper methods
- Supports multiple price points (regular, sale, cost)

### Feature Flags
- **Boolean flags** for soft deletion and feature toggles:
  - `is_active`: Enable/disable without deleting
  - `is_featured`: Highlight special items
  - `is_public`: Control wishlist visibility
  - `track_inventory`: Enable/disable stock tracking

### Extensibility
- **Metadata fields** (JSON/Object) provide schema flexibility for:
  - Custom product attributes
  - Additional cart/wishlist data
  - Order notes and special instructions
  - Future feature additions without migration

### Nullable Foreign Keys
- **user_id (nullable)**: Support guest transactions via session_id
- **product_variant_id (nullable)**: Variants are optional, products work standalone
- **parent_id (nullable)**: Top-level categories have no parent

## API Integration

This schema aligns with the following API endpoints:

### ✅ Working Endpoints
- **`POST /api/v1/auth/login`** - User authentication (returns JWT token)
- **`POST /api/v1/auth/register`** - User registration
- **`GET /api/v1/products`** - Product catalog with pagination (18 products available)
- **`GET /api/v1/products/{id}`** - Single product details with category & brand
- **`GET /api/v1/master/categories`** - Category hierarchy
- **`GET /api/v1/master/brands`** - Brand listings
- **`GET /api/v1/orders`** - User order history (currently returns empty array)
- **`GET /api/v1/orders/{id}`** - Single order details

### ⏳ Pending Implementation (Return 404)
- **`GET /api/v1/cart`** - Get user's cart with items
- **`POST /api/v1/cart/items`** - Add item to cart
- **`PATCH /api/v1/cart/items/{id}`** - Update cart item quantity
- **`DELETE /api/v1/cart/items/{id}`** - Remove item from cart
- **`POST /api/v1/cart/checkout`** - Convert cart to order
- **`GET /api/v1/wishlist`** - Get user's wishlist with items
- **`POST /api/v1/wishlist/items`** - Add item to wishlist
- **`DELETE /api/v1/wishlist/items/{id}`** - Remove item from wishlist
- **`GET /api/v1/wishlist/check?product_id={id}`** - Check if product in wishlist
- **`POST /api/v1/coupons/apply`** - Validate and apply coupon code (exists but untested)

### Current Workarounds
- **Cart Management**: Using local `CartManager` singleton (in-memory)
- **Wishlist**: UI designed, awaiting API implementation
- **Order Placement**: Using local `OrderManager` to store order history
- **Order API**: Can view orders (GET), but POST requires cart_id from non-existent cart

---

**Generated**: Based on Beckie-Deal Android application data models  
**API Base URL**: `https://laravel-app-114227311375.asia-southeast1.run.app/api/v1`  
**Last API Test**: September 10, 2026  
**Version**: 1.1  
**Last Updated**: Corrected with actual implementation status
