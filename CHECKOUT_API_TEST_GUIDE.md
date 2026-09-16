# 🧪 Checkout API Testing Guide

## API Details

**Base URL:** `https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/`

**Endpoint:** `POST /cart/checkout`

**Authentication:** JWT Bearer Token (required)

---

## Step 1: Login & Get Token

### Using cURL

```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "horratha4t5@gmail.com",
    "password": "qwer1234"
  }'
```

**Response:**
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

**Save the `access_token` for the next step.**

---

## Step 2: Add Items to Cart

Before checkout, you need items in the cart. Add a product first:

```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/items" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "product_id": "product-uuid-here",
    "quantity": 1,
    "metadata": {}
  }'
```

**To get valid product IDs, first fetch products:**

```bash
curl -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/products?page=1&per_page=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## Step 3: Test Checkout Endpoint

### Using cURL

```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shipping_address": {
      "recipient_name": "Your Name",
      "street": "123 Main St",
      "city": "Phnom Penh",
      "state": "Kandal",
      "postal_code": "12000",
      "country": "Cambodia",
      "phone": "+855 (98) 123-4567"
    },
    "payment_method": "card",
    "notes": "Please deliver during business hours"
  }'
```

### Expected Success Response (200 OK)

```json
{
  "success": true,
  "data": {
    "order_id": "ORD-2026-001234",
    "status": "pending",
    "total_amount": 1599.00,
    "items_count": 1,
    "shipping_address": {
      "recipient_name": "Your Name",
      "street": "123 Main St",
      "city": "Phnom Penh",
      "state": "Kandal",
      "postal_code": "12000",
      "country": "Cambodia",
      "phone": "+855 (98) 123-4567"
    },
    "payment_method": "card",
    "notes": "Please deliver during business hours",
    "created_at": "2026-09-13T12:34:56Z"
  },
  "message": "Order created successfully"
}
```

### Error Responses

**400 - Empty Cart:**
```json
{
  "success": false,
  "message": "Cart is empty"
}
```

**401 - Unauthorized:**
```json
{
  "success": false,
  "message": "Unauthenticated"
}
```

**422 - Validation Error:**
```json
{
  "success": false,
  "errors": {
    "shipping_address.recipient_name": ["The recipient name field is required."],
    "shipping_address.street": ["The street field is required."],
    "payment_method": ["The payment method must be one of: card, bank_transfer, cash_on_delivery"]
  }
}
```

---

## Using Postman (Recommended for Easier Testing)

### 1. Create a New Request

- **Method:** POST
- **URL:** `https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout`

### 2. Add Authorization

- **Type:** Bearer Token
- **Token:** `(paste your access_token from login response)`

### 3. Add Headers

| Key | Value |
|---|---|
| Content-Type | application/json |

### 4. Add Body (raw JSON)

```json
{
  "shipping_address": {
    "recipient_name": "Your Full Name",
    "street": "123 Main Street",
    "city": "Phnom Penh",
    "state": "Kandal",
    "postal_code": "12000",
    "country": "Cambodia",
    "phone": "+855 (98) 123-4567"
  },
  "payment_method": "card",
  "notes": "Please deliver during business hours"
}
```

### 5. Send Request

Click **Send** and check the response.

---

## Complete Test Workflow

### Full Workflow with Example Commands

```bash
# 1. Login
TOKEN_RESPONSE=$(curl -s -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "horratha4t5@gmail.com",
    "password": "qwer1234"
  }')

# Extract token (requires jq)
ACCESS_TOKEN=$(echo $TOKEN_RESPONSE | jq -r '.data.access_token')
echo "Access Token: $ACCESS_TOKEN"

# 2. Get cart contents
curl -s -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart" \
  -H "Authorization: Bearer $ACCESS_TOKEN" | jq .

# 3. Perform checkout
curl -s -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shipping_address": {
      "recipient_name": "John Doe",
      "street": "123 Main St",
      "city": "Phnom Penh",
      "state": "Kandal",
      "postal_code": "12000",
      "country": "Cambodia",
      "phone": "+855 (98) 123-4567"
    },
    "payment_method": "card",
    "notes": "Test order"
  }' | jq .
```

---

## Troubleshooting

### "Unauthenticated" Error (401)
- ✅ Ensure token is valid and not expired
- ✅ Use correct `Authorization: Bearer TOKEN` header format
- ✅ Re-login to get a fresh token

### "Cart is empty" Error (400)
- ✅ Add items to cart first using `/cart/items` endpoint
- ✅ Verify items were added by calling `GET /cart`

### "Missing required fields" Error (422)
- ✅ Check all required fields are included in `shipping_address`
- ✅ Verify `payment_method` is one of: `card`, `bank_transfer`, `cash_on_delivery`
- ✅ Format phone number correctly

### Connection Refused / Timeout
- ✅ Verify internet connection
- ✅ Check if backend API is running
- ✅ Try a simpler endpoint first like `GET /products` to verify connectivity

---

## What to Check

### Successful Checkout Should:
✅ Return 200 OK status code
✅ Include order ID in response
✅ Show "pending" status for new order
✅ Include total amount calculated correctly
✅ Save shipping address provided
✅ Include timestamp of creation
✅ Clear the cart after successful checkout (on backend)

### Failed Checkout Should:
❌ Not create an order if cart is empty
❌ Not create an order if authentication fails
❌ Not create an order if required fields are missing
❌ Return appropriate error messages
❌ Include validation error details

---

## Test Scenarios

| Scenario | Expected Result | Status |
|----------|------------------|--------|
| Checkout with valid data | ✅ 200 OK, Order created | To test |
| Checkout without token | ❌ 401 Unauthorized | To test |
| Checkout with empty cart | ❌ 400 Bad Request, "Cart is empty" | To test |
| Checkout with invalid payment method | ❌ 422 Validation error | To test |
| Checkout with missing shipping address | ❌ 422 Validation error | To test |
| Checkout creates correct order | ✅ Order data saved | To test |
| Checkout clears cart | ✅ Cart emptied after success | To test |

---

## Next Steps

1. **Test Login** first to ensure authentication works
2. **Add Products to Cart** to have items for checkout
3. **Test Checkout** with valid shipping data
4. **Test Error Cases** (empty cart, missing fields, etc.)
5. **Verify Order Creation** in backend database
6. **Check Android App** integration with these endpoints

---

## API Documentation Reference

For complete API documentation, see:
https://documenter.getpostman.com/view/51923872/2sBYAvvVnR

---

**Last Updated:** September 13, 2026
