# BECKIE-DEAL APP - API ENDPOINTS REFERENCE
## Complete API Documentation by Status

---

## 🟢 WORKING ENDPOINTS (Fully Implemented)

### 1. Authentication API
**File:** `AuthApiService.java`

```
POST /api/v1/auth/login
├─ Request: { "email": "user@example.com", "password": "pass" }
├─ Response: { "status": "success", "data": { "token": "JWT_TOKEN", "user": {...}, "expires_in": 3600 } }
├─ Status: ✅ WORKING
└─ Used in: LoginActivity

POST /api/v1/auth/register
├─ Request: { "fullName": "John", "email": "john@example.com", "password": "pass", "phone": "123456789" }
├─ Response: { "status": "success", "data": { "token": "JWT_TOKEN", "user": {...} } }
├─ Status: ✅ WORKING
└─ Used in: LoginActivity

POST /api/v1/auth/logout
├─ Request: (no body)
├─ Response: { "status": "success" }
├─ Status: ✅ WORKING
└─ Used in: ProfileActivity

GET /api/v1/auth/user
├─ Request: (Authentication required)
├─ Response: { "status": "success", "data": { "id": 1, "fullName": "John", "email": "john@example.com", "phone": "123456789" } }
├─ Status: ✅ WORKING
└─ Used in: ProfileActivity

FUTURE ENDPOINTS (Commented in code):
├─ POST /api/v1/auth/refresh-token (for token refresh)
└─ POST /api/v1/auth/forgot-password (not implemented)
```

---

### 2. Product API
**File:** `ProductApiService.java`

```
GET /api/v1/products?limit={limit}&offset={offset}
├─ Query Params: limit=20, offset=0
├─ Response: { "status": "success", "data": [...], "pagination": { "total": 150, "limit": 20, "offset": 0 } }
├─ Features: Pagination
├─ Status: ✅ WORKING
├─ Used in: MainActivity, ProductListActivity
└─ Caching: YES (ProductRepository caches locally)

GET /api/v1/products?search={query}&limit={limit}&offset={offset}
├─ Query Params: search="laptop", limit=20, offset=0
├─ Response: { "status": "success", "data": [...], "pagination": {...} }
├─ Features: Full-text search
├─ Status: ✅ WORKING
├─ Used in: SearchActivity
└─ Caching: Limited (fresh search each time)

GET /api/v1/products/{id}
├─ Path Param: id=123
├─ Response: { 
│   "status": "success",
│   "data": {
│     "id": 123,
│     "name": "Samsung Galaxy S21",
│     "price": 999.99,
│     "category": {...},
│     "brand": {...},
│     "variants": [...],
│     "images": [
│       { "id": 1, "url": "https://...", "alt_text": "Front view" },
│       { "id": 2, "url": "https://...", "alt_text": "Back view" }
│     ],
│     "specs": {
│       "sku": "SAM-GAL-21",
│       "barcode": "8806090000000",
│       "dimensions": "6.2 x 2.8 x 0.31 inches",
│       "weight": 171
│     }
│   }
│ }
├─ Features: Full product detail with variants and images
├─ Status: ✅ WORKING
├─ Used in: ProductDetailActivity
└─ Caching: Per-product cache

Response Data Structure (Product):
{
  "id": 123,
  "name": "Product Name",
  "slug": "product-name",
  "description": "Product description...",
  "price": 299.99,
  "category": { "id": 5, "name": "Electronics", "slug": "electronics" },
  "brand": { "id": 10, "name": "Samsung", "slug": "samsung", "logo": "https://..." },
  "stock": 45,
  "rating": 4.5,
  "sales": 1200,
  "views": 5000,
  "images": [...],
  "variants": [
    { "id": 1, "name": "8GB RAM + 256GB Storage", "sku": "SAM-8-256", "stock": 30, "price": 299.99 },
    { "id": 2, "name": "12GB RAM + 512GB Storage", "sku": "SAM-12-512", "stock": 15, "price": 399.99 }
  ]
}
```

---

### 3. Category API
**File:** `CategoryApiService.java`

```
GET /api/v1/master/categories?limit={limit}&offset={offset}
├─ Query Params: limit=50, offset=0
├─ Response: { "status": "success", "data": [...], "pagination": {...} }
├─ Features: Paginated category list with hierarchy
├─ Status: ✅ WORKING
├─ Used in: MainActivity, CategoriesActivity, ProductListActivity
├─ Caching: Aggressive (singleton pattern)
└─ Cache Strategy: Clear on app version update

Response Data Structure (Category):
{
  "id": 5,
  "name": "Electronics",
  "slug": "electronics",
  "parent_id": null,
  "image": "https://...",
  "description": "Electronic devices and gadgets"
}
```

---

### 4. Brand API
**File:** `BrandApiService.java`

```
GET /api/v1/master/brands?limit={limit}&offset={offset}
├─ Query Params: limit=20, offset=0
├─ Response: { "status": "success", "data": [...], "pagination": {...} }
├─ Features: All brands with pagination
├─ Status: ✅ WORKING
├─ Used in: MainActivity, AllBrandsActivity
├─ Caching: YES (Singleton pattern)
└─ Default: limit=20, offset=0

GET /api/v1/master/brands?featured=true&limit={limit}&offset={offset}
├─ Query Params: featured=true, limit=10, offset=0
├─ Response: { "status": "success", "data": [...], "pagination": {...} }
├─ Features: Featured brands only
├─ Status: ✅ WORKING
├─ Used in: MainActivity brand carousel
└─ Note: Set featured=true to filter

Response Data Structure (Brand):
{
  "id": 10,
  "name": "Samsung",
  "slug": "samsung",
  "logo": "https://cdn.example.com/brands/samsung.png",
  "description": "South Korean electronics company",
  "website": "https://www.samsung.com"
}
```

---

### 5. Promotion API
**File:** `PromotionApiService.java`

```
GET /api/v1/promotions?limit={limit}&offset={offset}
├─ Query Params: limit=10, offset=0
├─ Response: { "status": "success", "data": [...], "pagination": {...} }
├─ Features: Active promotions with countdown timers
├─ Status: ✅ WORKING
├─ Used in: MainActivity (home carousel)
├─ Fallback: Mock promotions if API fails (graceful degradation)
└─ Update Frequency: Every 30 seconds

GET /api/v1/promotions
├─ Default: Uses pagination (limit=20, offset=0)
├─ Status: ✅ WORKING
└─ Used in: CouponsActivity

Response Data Structure (Promotion):
{
  "id": 1,
  "code": "WELCOME10",
  "description": "Get 10% off on first order",
  "discount_type": "percentage",
  "discount_value": 10,
  "minOrderAmount": 50.00,
  "expiresAt": "2026-12-31T23:59:59Z",
  "usageRemaining": 500,
  "image": "https://...",
  "applicableCategories": [5, 8, 12]
}
```

---

## 🟡 MAINTENANCE ENDPOINTS (Waiting for Backend Implementation)

### 6. Cart API ⚠️ BACKEND NOT IMPLEMENTED (Returns 404)
**File:** `CartApiService.java`  
**Repository:** `CartRepository.java`

```
GET /api/v1/cart
├─ Authentication: Required
├─ Response: { "status": "success", "data": { "id": 1, "userId": 5, "items": [...] } }
├─ Status: ⚠️ BACKEND 404 (UI Ready)
├─ Used in: CartActivity
├─ Error Handling: Gracefully handled - app doesn't crash
└─ Frontend: Ready, waiting for backend

POST /api/v1/cart/items
├─ Authentication: Required
├─ Request Body:
│  {
│    "product_id": 123,
│    "variant_id": 1,  (optional)
│    "quantity": 2
│  }
├─ Response: { "status": "success", "data": { "id": 1, "cartId": 1, "productId": 123, "quantity": 2, "price": 299.99 } }
├─ Status: ⚠️ BACKEND 404
├─ Used in: ProductDetailActivity (Add to Cart button)
└─ Error Handling: Logs 404 but doesn't crash

PATCH /api/v1/cart/items/{id}
├─ Authentication: Required
├─ Path Param: id=cart_item_id (e.g., 456)
├─ Request Body: { "quantity": 5 }
├─ Response: { "status": "success", "data": { "id": 456, "quantity": 5 } }
├─ Status: ⚠️ BACKEND 404
├─ Used in: CartActivity (Update quantity)
└─ Error Handling: Graceful

DELETE /api/v1/cart/items/{id}
├─ Authentication: Required
├─ Path Param: id=cart_item_id
├─ Response: { "status": "success" }
├─ Status: ⚠️ BACKEND 404
├─ Used in: CartActivity (Remove from cart button)
└─ Error Handling: Graceful

PUT /api/v1/cart
├─ Authentication: Required
├─ Request Body: { "notes": "Gift wrap please" }
├─ Response: { "status": "success", "data": { "id": 1 } }
├─ Status: ⚠️ BACKEND 404
└─ Used in: PayoutActivity (checkout flow)

POST /api/v1/cart/checkout
├─ Authentication: Required
├─ Request Body:
│  {
│    "shipping_address_id": 1,
│    "shipping_method_id": 2,
│    "payment_method": "aba_payway",
│    "coupon_code": "WELCOME10"  (optional)
│  }
├─ Response: { "status": "success", "data": { "order_id": 999, "total": 599.99 } }
├─ Status: ⚠️ BACKEND 404
├─ Used in: PayoutActivity (Convert cart to order)
└─ Error Handling: Graceful

BACKEND TODO:
├─ Implement all endpoints above
├─ Add database schema for Cart, CartItem
├─ Add quantity validation
├─ Add stock checking
├─ Add price calculation (including taxes, shipping)
└─ Estimated Time: 3-5 days
```

---

### 7. Wishlist API ⚠️ BACKEND NOT IMPLEMENTED (Returns 404)
**File:** `WishlistApiService.java`  
**Repository:** `WishlistRepository.java`

```
GET /api/v1/wishlist
├─ Authentication: Required
├─ Response: { "status": "success", "data": { "id": 1, "userId": 5, "items": [...], "isPublic": false } }
├─ Status: ⚠️ BACKEND 404 (UI Ready)
├─ Used in: WishlistActivity
└─ Caching: Local state management in place

PUT /api/v1/wishlist
├─ Authentication: Required
├─ Request Body: { "isPublic": true }
├─ Response: { "status": "success", "data": { "id": 1 } }
├─ Status: ⚠️ BACKEND 404
└─ Used in: WishlistActivity settings

DELETE /api/v1/wishlist
├─ Authentication: Required
├─ Response: { "status": "success" }
├─ Status: ⚠️ BACKEND 404
└─ Used in: WishlistActivity (Clear wishlist)

GET /api/v1/wishlist/check?product_id={productId}
├─ Authentication: Required
├─ Query Param: product_id=123
├─ Response: { "status": "success", "data": { "inWishlist": true } }
├─ Status: ⚠️ BACKEND 404
├─ Used in: ProductDetailActivity (Check if heart icon should be filled)
└─ Frontend Workaround: Uses cached product object

POST /api/v1/wishlist/items
├─ Authentication: Required
├─ Request Body: { "product_id": 123, "quantity": 1 }
├─ Response: { "status": "success", "data": { "id": 1, "wishlistId": 1, "productId": 123, "quantity": 1 } }
├─ Status: ⚠️ BACKEND 404
├─ Used in: ProductDetailActivity (Add to Wishlist button)
└─ Error Handling: Graceful - shows toast

DELETE /api/v1/wishlist/items/{wishlistItemId}
├─ Authentication: Required
├─ Path Param: wishlistItemId=456
├─ Response: { "status": "success" }
├─ Status: ⚠️ BACKEND 404
├─ Used in: WishlistActivity (Remove from wishlist)
└─ Error Handling: Graceful

BACKEND TODO:
├─ Implement all endpoints above
├─ Add database schema for Wishlist, WishlistItem
├─ Add public wishlist sharing (for future)
└─ Estimated Time: 2-3 days
```

---

### 8. Coupon/Discount API ⚠️ BACKEND NOT IMPLEMENTED (Returns 404)
**File:** `CouponApiService.java`  
**Manager:** `CouponManager.java`

```
POST /api/v1/coupons/apply
├─ Authentication: Required
├─ Request Body:
│  {
│    "code": "WELCOME10",
│    "cart_id": 1
│  }
├─ Response: { 
│   "status": "success",
│   "data": {
│     "coupon": "WELCOME10",
│     "discount_type": "percentage",
│     "discount_value": 10,
│     "discount_amount": 159.90,
│     "cart_total_before": 1599.00,
│     "grand_total": 1439.10
│   }
│ }
├─ Status: ⚠️ BACKEND 404 (UI Ready)
├─ Used in: PayoutActivity (Step 2 - Payment Method)
├─ Frontend: Input field exists, ready for backend
└─ Error Scenarios:
   ├─ Invalid coupon code → Return error message
   ├─ Coupon expired → Return error message
   ├─ Minimum order not met → Return error message
   └─ Coupon already used → Return error message

BACKEND TODO:
├─ Implement coupon validation
├─ Check coupon expiry date
├─ Check minimum order amount
├─ Check usage limit
├─ Calculate discount (based on type: percentage, fixed amount, free shipping)
├─ Apply to cart total
└─ Estimated Time: 3-5 days
```

---

### 9. Orders API ⚠️ BACKEND NOT IMPLEMENTED
**File:** `OrderManager.java` (mock data)  
**Activities:** `MyOrdersActivity.java`, `OrderDetailActivity.java`

```
GET /api/v1/orders
├─ Authentication: Required
├─ Query Params: limit=20, offset=0
├─ Response: { 
│   "status": "success",
│   "data": [
│     {
│       "id": 999,
│       "order_number": "ORD-20261005-001",
│       "status": "shipped",  (pending, confirmed, shipped, delivered, cancelled)
│       "total": 1439.10,
│       "items": 2,
│       "created_at": "2026-10-01T10:30:00Z",
│       "shipping_address": {...},
│       "estimated_delivery": "2026-10-08"
│     }
│   ],
│   "pagination": {...}
│ }
├─ Status: ⚠️ BACKEND NOT IMPLEMENTED
├─ Used in: MyOrdersActivity (Order list)
├─ Caching: No (fetches fresh each time)
└─ Frontend Workaround: Mock orders for testing

GET /api/v1/orders/{id}
├─ Authentication: Required
├─ Path Param: id=999
├─ Response: {
│   "status": "success",
│   "data": {
│     "id": 999,
│     "order_number": "ORD-20261005-001",
│     "status": "shipped",
│     "items": [
│       { "product_id": 123, "name": "Samsung Galaxy S21", "price": 299.99, "quantity": 1 }
│     ],
│     "subtotal": 299.99,
│     "tax": 29.99,
│     "shipping": 50.00,
│     "discount": -159.90,
│     "total": 219.09,
│     "payment_method": "aba_payway",
│     "shipping_address": { "street": "...", "city": "...", "country": "Cambodia" },
│     "tracking_number": "DEXP123456",
│     "estimated_delivery": "2026-10-08"
│   }
│ }
├─ Status: ⚠️ BACKEND NOT IMPLEMENTED
├─ Used in: OrderDetailActivity (Order detail)
└─ Frontend Calculation: Uses heuristics
   ├─ Free shipping on orders $50+ (hardcoded rule)
   └─ Tax = 10% of subtotal (hardcoded calculation)

BACKEND TODO:
├─ Create Order model with statuses
├─ GET /orders (list user's orders, paginated)
├─ GET /orders/{id} (order detail)
├─ POST /orders (create order from cart checkout)
├─ PATCH /orders/{id} (update order status, for admin)
├─ GET /orders/{id}/tracking (for tracking page, future feature)
└─ Estimated Time: 1 week
```

---

### 10. Shipping Methods API
**File:** `MasterApiService.java`

```
GET /api/v1/master/shipping-methods?limit={limit}&offset={offset}
├─ Query Params: limit=50, offset=0
├─ Response: { 
│   "status": "success",
│   "data": [
│     { "id": 1, "name": "Standard Shipping", "base_cost": 3.00, "estimate_days": 3 },
│     { "id": 2, "name": "Express Shipping", "base_cost": 8.00, "estimate_days": 1 },
│     { "id": 3, "name": "Overnight", "base_cost": 15.00, "estimate_days": 0.5 }
│   ],
│   "pagination": {...}
│ }
├─ Status: ✅ WORKING (assumed, based on usage)
├─ Used in: PayoutActivity (Step 2 - Shipping method picker)
└─ Note: Shows estimated delivery days and cost
```

---

## 🔵 FUTURE ENDPOINTS (Not Yet Implemented)

### 11. Notifications API (Future)
```
GET /api/v1/notifications
├─ Authentication: Required
├─ Query Params: limit=20, offset=0
├─ Response: { "status": "success", "data": [...], "pagination": {...} }
├─ Categories: Orders, Promotions, Alerts
└─ Status: 🔵 FUTURE (UI exists, API integration needed)

POST /api/v1/notifications/{id}/read
├─ Mark notification as read
└─ Status: 🔵 FUTURE

REQUIRES: Firebase Cloud Messaging (FCM) setup
```

---

### 12. Reviews API (Future)
```
GET /api/v1/products/{id}/reviews
├─ Authentication: Optional
├─ Query Params: limit=10, offset=0
├─ Response: { "data": [{ "rating": 5, "text": "Great product", "reviewer": "John", "created_at": "..." }] }
└─ Status: 🔵 FUTURE

POST /api/v1/products/{id}/reviews
├─ Authentication: Required
├─ Request: { "rating": 5, "text": "Great product", "photos": [...] }
└─ Status: 🔵 FUTURE
```

---

### 13. Payment Webhook (For Payment Gateway)
```
POST /webhook/aba-payway
├─ Webhook: Receives payment confirmation from ABA PayWay
├─ Payload: { "order_id": 999, "status": "success|failed", "transaction_id": "..." }
└─ Status: 🔵 FUTURE (Backend needs to handle webhook)
```

---

## 🛠️ ERROR HANDLING STRATEGY

### Standard Error Response Format:
```json
{
  "status": "error",
  "message": "Invalid request",
  "errors": [
    { "field": "email", "message": "Email already exists" }
  ]
}
```

### HTTP Status Codes:
- `200` - Success
- `400` - Bad request (validation error)
- `401` - Unauthorized (missing/invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not found (endpoint/resource)
- `409` - Conflict (duplicate, already exists)
- `500` - Server error

### App Behavior on Errors:
- **404 Response:** App logs error but continues (graceful degradation)
- **401 Response:** App shows login screen
- **500 Response:** Shows "Try again later" toast, retries on button click
- **Network Error:** Shows offline message, data loads from cache if available

---

## 📍 API BASE URL

```
Development: http://api.dev.beckiedeal.local
Staging: https://api.staging.beckiedeal.com
Production: https://api.beckiedeal.com
```

---

## 🔐 Authentication Header

All endpoints (except login/register) require:
```
Authorization: Bearer {JWT_TOKEN}
```

**Example Request:**
```
GET /api/v1/cart
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📊 ENDPOINT STATUS SUMMARY

| Category | Implemented | Waiting for Backend | Future | Total |
|----------|-------------|-------------------|--------|-------|
| Authentication | 4 | 2 | — | 6 |
| Products | 3 | — | — | 3 |
| Categories | 1 | — | — | 1 |
| Brands | 2 | — | — | 2 |
| Promotions | 2 | — | — | 2 |
| Cart | — | 6 | — | 6 |
| Wishlist | — | 6 | — | 6 |
| Coupons | — | 1 | — | 1 |
| Orders | — | 2 | — | 2 |
| Shipping | 1 | — | — | 1 |
| Notifications | — | — | 2 | 2 |
| Reviews | — | — | 2 | 2 |
| **TOTAL** | **18** | **17** | **4** | **39** |

---

**Document Version:** 1.0  
**Last Updated:** October 2026  
**Prepared for:** Development Team
