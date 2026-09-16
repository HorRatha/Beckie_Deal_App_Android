# ✅ Checkout Feature Testing Checklist

## Overview
This document provides a step-by-step checklist to verify that the checkout feature works correctly from both the Android app and backend API perspective.

**Test Account:**
- Email: `horratha4t5@gmail.com`
- Password: `qwer1234`

---

## 📋 Pre-Test Setup

### What You Need
- [ ] Test account login credentials
- [ ] Postman or cURL installed
- [ ] Android app installed on device/emulator
- [ ] Backend API running
- [ ] Internet connection

### Verify Backend is Running
```bash
curl -s https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/products \
  | head -20
```
✅ Should return a JSON response with product data

---

## 🔐 Part 1: Authentication Testing

### Test 1.1: Login via API
**Step:**
```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "horratha4t5@gmail.com",
    "password": "qwer1234"
  }'
```

**Expected Result:**
- [ ] Status: 200 OK
- [ ] Response contains `access_token`
- [ ] Response contains `token_type: Bearer`
- [ ] Response contains `expires_in` (should be ~5184000 seconds = 60 days)

**Example Response:**
```json
{
  "success": true,
  "data": {
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGc...",
    "token_type": "Bearer",
    "expires_in": 5184000
  }
}
```

**Save this token** - you'll use it for all subsequent requests.

---

### Test 1.2: Login via Android App
**Step:**
1. Open Beckie Deal app
2. Tap "Account" → "Login"
3. Enter email: `horratha4t5@gmail.com`
4. Enter password: `qwer1234`
5. Tap "Login"

**Expected Result:**
- [ ] Login successful
- [ ] Token stored securely
- [ ] Redirected to home screen
- [ ] Bottom nav shows logged-in state

---

## 🛒 Part 2: Cart Management Testing

### Test 2.1: Add Product to Cart via API
**Prerequisites:** Have a valid access token from Test 1.1

**Step 1: Get a Product ID**
```bash
TOKEN="your_access_token_here"

curl -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/products?page=1&per_page=5" \
  -H "Authorization: Bearer $TOKEN"
```

**Expected:** Get product list with IDs

**Step 2: Add Product to Cart**
```bash
PRODUCT_ID="product-id-from-above"

curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/items" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"product_id\": \"$PRODUCT_ID\",
    \"quantity\": 1,
    \"metadata\": {}
  }"
```

**Expected Result:**
- [ ] Status: 200/201
- [ ] Response contains cart item with:
  - [ ] `product_id`
  - [ ] `quantity: 1`
  - [ ] `id` (cart item ID)

---

### Test 2.2: Get Cart Contents
**Step:**
```bash
curl -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart" \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Result:**
- [ ] Status: 200 OK
- [ ] Response contains `data.items` array
- [ ] Items array has at least 1 item from Test 2.1
- [ ] Each item has:
  - [ ] `id` (cart item ID)
  - [ ] `product_id`
  - [ ] `quantity`
  - [ ] `price`
  - [ ] `product` (full product details)

**Example Response:**
```json
{
  "success": true,
  "data": {
    "id": "cart-uuid",
    "user_id": "user-uuid",
    "items": [
      {
        "id": 1,
        "cart_id": "cart-uuid",
        "product_id": "product-uuid",
        "quantity": 1,
        "price": "1599.00",
        "product": {
          "id": "product-uuid",
          "name": "NVIDIA GeForce RTX 4090",
          "price": "1599.00",
          "thumbnail": "..."
        }
      }
    ],
    "total": 1599.00,
    "item_count": 1
  }
}
```

---

### Test 2.3: Add Product to Cart via Android App
**Step:**
1. From home screen, find a product
2. Tap on product card
3. In product detail, tap "Add to Cart"
4. Confirm quantity and tap "Add"

**Expected Result:**
- [ ] Toast shows "Added to cart!"
- [ ] Cart badge updates (bottom nav shows cart count)
- [ ] Product appears in Cart screen

---

## 💳 Part 3: Checkout Testing

### Test 3.1: Checkout via API (Direct)
**Prerequisites:**
- [ ] Have items in cart (from Test 2.1 or 2.2)
- [ ] Have valid access token

**Step:**
```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shipping_address": {
      "recipient_name": "Test User",
      "street": "123 Main Street",
      "city": "Phnom Penh",
      "state": "Kandal",
      "postal_code": "12000",
      "country": "Cambodia",
      "phone": "+855 (98) 999-9999"
    },
    "payment_method": "card",
    "notes": "Test checkout order"
  }'
```

**Expected Result:**
- [ ] Status: 200 OK
- [ ] Response `success: true`
- [ ] Response contains `order_id`
- [ ] Response contains `status: "pending"` or similar
- [ ] Response contains:
  - [ ] `total_amount` (correct calculation)
  - [ ] `items_count`
  - [ ] `shipping_address` (as provided)
  - [ ] `payment_method: "card"`
  - [ ] `created_at` (timestamp)

**Example Success Response:**
```json
{
  "success": true,
  "data": {
    "order_id": "ORD-2026-001234",
    "status": "pending",
    "total_amount": 1599.00,
    "items_count": 1,
    "shipping_address": {
      "recipient_name": "Test User",
      "street": "123 Main Street",
      "city": "Phnom Penh",
      "state": "Kandal",
      "postal_code": "12000",
      "country": "Cambodia",
      "phone": "+855 (98) 999-9999"
    },
    "payment_method": "card",
    "notes": "Test checkout order",
    "created_at": "2026-09-13T12:34:56Z"
  },
  "message": "Order created successfully"
}
```

### Test 3.2: Verify Cart is Empty After Checkout
**Step:**
```bash
curl -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart" \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Result:**
- [ ] Status: 200 OK
- [ ] `data.items` is empty array `[]`
- [ ] `data.item_count: 0`
- [ ] `data.total: 0` or `0.00`

---

### Test 3.3: Checkout via Android App
**Step:**
1. From Cart screen, tap "Proceed to Checkout"
2. Enter shipping information:
   - Name: Test User
   - Phone: +855 (98) 999-9999
   - Address: 123 Main Street
   - City: Phnom Penh
   - Postal Code: 12000
3. Select payment method (e.g., "Card")
4. Tap "Confirm Order"

**Expected Result:**
- [ ] Shipping fields validate correctly
- [ ] No error messages for valid input
- [ ] "Confirm Order" button is enabled
- [ ] Loading indicator appears
- [ ] One of the following:
  - [ ] Success dialog shows with order details
  - [ ] Redirected to payment gateway (if payment method required)
  - [ ] Order confirmation shown

---

## ❌ Part 4: Error Handling Testing

### Test 4.1: Checkout with Empty Cart
**Prerequisites:** Ensure cart is empty

**Step:**
```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shipping_address": {
      "recipient_name": "Test",
      "street": "123 Main",
      "city": "Phnom Penh",
      "state": "Kandal",
      "postal_code": "12000",
      "country": "Cambodia",
      "phone": "+855 (98) 999-9999"
    },
    "payment_method": "card",
    "notes": ""
  }'
```

**Expected Result:**
- [ ] Status: 400 Bad Request
- [ ] `success: false`
- [ ] Error message includes "empty" or "cart"
- [ ] No order created

**Example Error Response:**
```json
{
  "success": false,
  "message": "Cart is empty"
}
```

---

### Test 4.2: Checkout Without Authentication
**Step:**
```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout" \
  -H "Content-Type: application/json" \
  -d '{
    "shipping_address": { ... },
    "payment_method": "card"
  }'
```

**Expected Result:**
- [ ] Status: 401 Unauthorized
- [ ] `success: false`
- [ ] Error message: "Unauthenticated" or similar
- [ ] No order created

---

### Test 4.3: Checkout with Missing Required Fields
**Step:**
```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shipping_address": {
      "recipient_name": "Test"
      # Missing other required fields
    },
    "payment_method": "card"
  }'
```

**Expected Result:**
- [ ] Status: 422 Unprocessable Entity
- [ ] `success: false`
- [ ] `errors` object contains field validation messages
- [ ] Error messages for missing fields:
  - [ ] `shipping_address.street`
  - [ ] `shipping_address.city`
  - [ ] `shipping_address.postal_code`
  - [ ] etc.

**Example Error Response:**
```json
{
  "success": false,
  "errors": {
    "shipping_address.street": ["The street field is required."],
    "shipping_address.city": ["The city field is required."]
  }
}
```

---

### Test 4.4: Checkout with Invalid Payment Method
**Step:**
```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shipping_address": { ... },
    "payment_method": "invalid_method"
  }'
```

**Expected Result:**
- [ ] Status: 422 Unprocessable Entity
- [ ] `success: false`
- [ ] Error message for invalid payment method
- [ ] Lists valid payment methods in response

---

## 📊 Part 5: Data Verification

### Test 5.1: Verify Order in Backend Database
After successful checkout, verify the order exists:

**Step (via Postman):**
```bash
curl -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/orders" \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Result:**
- [ ] Order appears in user's order list
- [ ] Order ID matches the checkout response
- [ ] Order status is correct
- [ ] Order total is correct
- [ ] Shipping address is saved correctly
- [ ] Order items are associated correctly

---

### Test 5.2: Verify Cart is Empty After Checkout
**Step:**
```bash
curl -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart" \
  -H "Authorization: Bearer $TOKEN"
```

**Expected Result:**
- [ ] `data.items` is empty: `[]`
- [ ] `data.item_count: 0`

---

## 🔄 Part 6: Integration Testing

### Test 6.1: Full Android Flow
**Complete flow in Android app:**

1. [ ] Login with test account
2. [ ] Browse home screen
3. [ ] Add 2-3 products to cart
4. [ ] Verify cart badge updates
5. [ ] Open Cart screen
6. [ ] Verify all products shown
7. [ ] Verify totals calculated correctly
8. [ ] Proceed to checkout
9. [ ] Enter shipping information
10. [ ] Confirm order
11. [ ] See success confirmation
12. [ ] Return to home - cart should be empty
13. [ ] Check "My Orders" (when implemented)

**Expected Result:**
- [ ] All steps complete without errors
- [ ] Data persists correctly
- [ ] UI updates reflect backend state

---

## 📝 Test Results Summary

After running all tests, fill in this summary:

| Test | Status | Notes |
|------|--------|-------|
| 1.1 - Login API | ✅ / ❌ | |
| 1.2 - Login App | ✅ / ❌ | |
| 2.1 - Add to Cart API | ✅ / ❌ | |
| 2.2 - Get Cart API | ✅ / ❌ | |
| 2.3 - Add to Cart App | ✅ / ❌ | |
| 3.1 - Checkout API | ✅ / ❌ | |
| 3.2 - Cart Empty After | ✅ / ❌ | |
| 3.3 - Checkout App | ✅ / ❌ | |
| 4.1 - Empty Cart Error | ✅ / ❌ | |
| 4.2 - No Auth Error | ✅ / ❌ | |
| 4.3 - Missing Fields Error | ✅ / ❌ | |
| 4.4 - Invalid Method Error | ✅ / ❌ | |
| 5.1 - Verify Order DB | ✅ / ❌ | |
| 5.2 - Verify Cart Empty | ✅ / ❌ | |
| 6.1 - Full App Flow | ✅ / ❌ | |

---

## 🔧 Troubleshooting

### Common Issues

**"Unauthenticated" response**
- ✅ Check token is valid: `curl ... -H "Authorization: Bearer $TOKEN"`
- ✅ Re-login to get fresh token
- ✅ Verify token not expired

**"Cart is empty" on checkout**
- ✅ Add items first using POST /cart/items
- ✅ Verify items exist with GET /cart

**"Connection refused" error**
- ✅ Verify internet connection
- ✅ Ping API: `curl https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/products`
- ✅ Check if backend service is running

**Android app crashes on checkout**
- ✅ Check app logs: `adb logcat | grep PayoutActivity`
- ✅ Verify API response format matches expectations
- ✅ Ensure network connection is stable

---

## 📚 Additional Resources

- **API Documentation:** https://documenter.getpostman.com/view/51923872/2sBYAvvVnR
- **Checkout Test Guide:** `CHECKOUT_API_TEST_GUIDE.md`
- **Backend Repo:** (add link if available)

---

**Test Date:** ________________
**Tester:** ________________
**Overall Status:** ✅ PASSED / ⚠️ NEEDS FIXES / ❌ FAILED

