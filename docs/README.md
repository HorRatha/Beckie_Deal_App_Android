# Beckie-Deal Database Documentation

Complete database schema documentation for the Beckie-Deal E-Commerce Android application.

## 📚 Documentation Files

### 1. [ER_Diagram_Beckie_Deal.md](./ER_Diagram_Beckie_Deal.md)
Complete Entity-Relationship diagram with ASCII art visualization, detailed entity descriptions, relationships, and technical notes.

**Contains:**
- Visual ER diagram (ASCII art)
- Implementation status for each entity
- Detailed entity descriptions with all attributes
- Relationship mappings
- Key features and technical notes
- API endpoint mappings

### 2. [ER_Diagram_Visual.html](./ER_Diagram_Visual.html)
Interactive HTML visualization of the ER diagram with color coding and visual relationships.

**Features:**
- Color-coded entities (Green = Working ✅, Orange = Pending ⏳)
- Visual relationship lines
- Interactive legend
- Status indicators
- Complete API endpoint mapping

**How to use:** Open in any web browser

### 3. [DATABASE_SCHEMA_SUMMARY.md](./DATABASE_SCHEMA_SUMMARY.md)
Quick reference guide with complete attribute lists for all entities.

**Contains:**
- Entity status table
- Complete attribute lists for all 12 entities
- Relationship matrix
- Constraints and rules
- Data type mapping
- API endpoint reference

### 4. [RELATIONSHIP_DIAGRAM.md](./RELATIONSHIP_DIAGRAM.md)
Detailed relationship documentation with cardinality and join examples.

**Contains:**
- Visual relationship map
- Cardinality reference (1:N, N:1, self-reference)
- Detailed relationship descriptions
- Foreign key constraints summary
- Join query examples
- Index recommendations

---

## 🎯 Quick Start

### View the ER Diagram
1. **Text Version**: Open [ER_Diagram_Beckie_Deal.md](./ER_Diagram_Beckie_Deal.md)
2. **Visual Version**: Open [ER_Diagram_Visual.html](./ER_Diagram_Visual.html) in browser

### Find Entity Details
1. Open [DATABASE_SCHEMA_SUMMARY.md](./DATABASE_SCHEMA_SUMMARY.md)
2. Search for entity name
3. View complete attribute list

### Understand Relationships
1. Open [RELATIONSHIP_DIAGRAM.md](./RELATIONSHIP_DIAGRAM.md)
2. See visual relationship map
3. Read detailed relationship descriptions

---

## 📊 Database Overview

### Entity Count: **12 Entities**

#### ✅ Implemented (4)
- **USER** - Authentication and user profiles
- **PRODUCT** - Product catalog
- **CATEGORY** - Product categories (hierarchical)
- **BRAND** - Product brands

#### ⏳ Pending API Implementation (8)
- **CART** - Shopping cart
- **CART_ITEM** - Cart line items
- **WISHLIST** - User wishlists
- **WISHLIST_ITEM** - Wishlist line items
- **ORDER** - Completed orders
- **ORDER_ITEM** - Order line items
- **PRODUCT_VARIANT** - Product variations
- **COUPON** - Discount coupons

---

## 🔗 Key Relationships

```
USER (1) ──→ CART (N)
USER (1) ──→ WISHLIST (N)
USER (1) ──→ ORDER (N)

CART (1) ──→ CART_ITEM (N) ──→ PRODUCT (1)
WISHLIST (1) ──→ WISHLIST_ITEM (N) ──→ PRODUCT (1)
ORDER (1) ──→ ORDER_ITEM (N) ──→ PRODUCT (1)

CATEGORY (1) ──→ PRODUCT (N)
BRAND (1) ──→ PRODUCT (N)
PRODUCT (1) ──→ PRODUCT_VARIANT (N)

CATEGORY (N) ──→ CATEGORY (1) [self-reference via parent_id]
```

---

## 🛠️ Implementation Status

### Working APIs ✅
```
POST   /api/v1/auth/login              ✅ Authentication
POST   /api/v1/auth/register           ✅ User registration
GET    /api/v1/products                ✅ Product list (18 products)
GET    /api/v1/products/{id}           ✅ Product details
GET    /api/v1/master/categories       ✅ Categories
GET    /api/v1/master/brands           ✅ Brands
GET    /api/v1/orders                  ✅ Order history
```

### Pending APIs ⏳
```
GET    /api/v1/cart                    ⏳ Cart operations (404)
POST   /api/v1/cart/items              ⏳ Add to cart (404)
GET    /api/v1/wishlist                ⏳ Wishlist operations (404)
POST   /api/v1/wishlist/items          ⏳ Add to wishlist (404)
POST   /api/v1/coupons/apply           ⏳ Apply coupon (untested)
```

### Current Workarounds
- **Cart**: Using local `CartManager` singleton (in-memory)
- **Orders**: Using local `OrderManager` singleton (in-memory)
- **Wishlist**: UI designed, awaiting API implementation

---

## 💡 Key Features

### 1. Guest Support
Both Cart and Wishlist support unauthenticated users via `session_id` (nullable `user_id`)

### 2. Hierarchical Categories
Self-referencing `parent_id` enables multi-level category trees

### 3. Flexible Pricing
- Regular price
- Sale price (discounts)
- Cost price (profit tracking)

### 4. Inventory Management
- Stock quantity tracking
- Minimum stock alerts
- In-stock status flags

### 5. Product Variants
Optional product variations (size, color, etc.) with separate SKU, price, and stock

### 6. Discount System
Coupon-based discounts applied at cart level with automatic total calculation

### 7. Order Snapshots
Order items capture product details at purchase time (price freeze)

### 8. SEO Optimization
Meta title and description fields for products, categories, and brands

### 9. Extensibility
JSON metadata fields for custom data without schema changes

---

## 🔑 Primary Keys & Data Types

### UUID Primary Keys
Used for core business entities (distributed system friendly):
- USER
- PRODUCT
- CATEGORY
- BRAND
- PRODUCT_VARIANT
- COUPON

### Integer Primary Keys
Used for transactional/relationship entities (performance):
- CART
- CART_ITEM
- WISHLIST
- WISHLIST_ITEM
- ORDER_ITEM

### String Primary Keys
Used for human-readable identifiers:
- ORDER (e.g., "BD-1001")

---

## 📈 Recommended Reading Order

1. **Start Here**: [DATABASE_SCHEMA_SUMMARY.md](./DATABASE_SCHEMA_SUMMARY.md)
   - Quick overview of all entities
   - Complete attribute lists
   - Status table

2. **Visual Understanding**: [ER_Diagram_Visual.html](./ER_Diagram_Visual.html)
   - Open in browser
   - See entity relationships visually
   - Understand data flow

3. **Detailed Documentation**: [ER_Diagram_Beckie_Deal.md](./ER_Diagram_Beckie_Deal.md)
   - In-depth entity descriptions
   - Business rules
   - Technical implementation notes

4. **Relationship Deep Dive**: [RELATIONSHIP_DIAGRAM.md](./RELATIONSHIP_DIAGRAM.md)
   - Understand all relationships
   - Foreign key constraints
   - Query examples
   - Index recommendations

---

## 🎨 Diagram Legend

### Entity Status
- **✅ Green/Solid Border**: API implemented and working
- **⏳ Orange/Dashed Border**: Model designed, API pending (404)

### Key Types
- **PK** - Primary Key (unique identifier)
- **FK** - Foreign Key (relationship to another table)

### Cardinality
- **1:N** - One to Many (e.g., one cart has many items)
- **N:1** - Many to One (e.g., many products belong to one category)
- **N:N** - Many to Many (implemented via junction table)

---

## 🔍 Need Help?

### Find an Entity
Use your editor's search function (Ctrl+F / Cmd+F) to search for entity names across all documentation files.

### Understand a Relationship
1. Check [RELATIONSHIP_DIAGRAM.md](./RELATIONSHIP_DIAGRAM.md)
2. Look for the specific relationship section
3. See detailed description and business rules

### See All Attributes
1. Open [DATABASE_SCHEMA_SUMMARY.md](./DATABASE_SCHEMA_SUMMARY.md)
2. Find entity section
3. View complete attribute list with data types

### Check API Status
1. Look for ✅ (working) or ⏳ (pending) indicators
2. See API endpoint mapping sections
3. Check current workarounds section

---

## 📝 Version Information

- **Version**: 1.1
- **Last Updated**: Corrected with actual implementation status
- **API Test Date**: September 10, 2026
- **Base URL**: Configured in `local.properties` (not exposed in public repository)
- **Generated From**: Beckie-Deal Android application data models

---

## 🚀 Future Work

### Backend Team Tasks
1. ✅ Implement User authentication API (DONE)
2. ✅ Implement Product catalog API (DONE)
3. ✅ Implement Category and Brand APIs (DONE)
4. ⏳ Implement Cart API endpoints
5. ⏳ Implement Wishlist API endpoints
6. ⏳ Complete Order creation flow
7. ⏳ Implement Coupon validation API

### Android Team Tasks
1. ✅ Product browsing features (IN PROGRESS)
2. ✅ Category and brand filtering (IN PROGRESS)
3. ✅ Local cart management (CartManager)
4. ✅ Local order tracking (OrderManager)
5. ⏳ Migrate to Cart API when ready
6. ⏳ Implement Wishlist UI
7. ⏳ Complete checkout flow

---

**Generated by**: Kiro AI Assistant  
**For**: Beckie-Deal E-Commerce Android Application  
**Project**: Database Schema Documentation  
**Contact**: See API_FINAL_TEST_RESULTS.md for backend status
