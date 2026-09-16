# ⚡ Quick Checkout Test (5 Minutes)

## Test Account
```
Email:    horratha4t5@gmail.com
Password: qwer1234
```

---

## 🚀 Quick Test - Copy & Paste

### 1️⃣ Login & Get Token (30 seconds)

```bash
curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"horratha4t5@gmail.com","password":"qwer1234"}'
```

**From response, copy the `access_token` value**

---

### 2️⃣ Get Products (30 seconds)

Replace `TOKEN` with your actual token:

```bash
TOKEN="paste-your-token-here"

curl -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/products?page=1&per_page=2" \
  -H "Authorization: Bearer $TOKEN"
```

**From response, copy one `id` value (product ID)**

---

### 3️⃣ Add to Cart (30 seconds)

Replace `TOKEN` and `PRODUCT_ID`:

```bash
TOKEN="your-token"
PRODUCT_ID="product-id-from-step-2"

curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/items" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"product_id\":\"$PRODUCT_ID\",\"quantity\":1,\"metadata\":{}}"
```

✅ Should return 200 with item details

---

### 4️⃣ Checkout (60 seconds)

Replace `TOKEN`:

```bash
TOKEN="your-token"

curl -X POST "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart/checkout" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "shipping_address":{
      "recipient_name":"Test User",
      "street":"123 Main St",
      "city":"Phnom Penh",
      "state":"Kandal",
      "postal_code":"12000",
      "country":"Cambodia",
      "phone":"+855 (98) 999-9999"
    },
    "payment_method":"card",
    "notes":"Test"
  }'
```

### ✅ Expected Success Response:
```json
{
  "success": true,
  "data": {
    "order_id": "ORD-2026-...",
    "status": "pending",
    "total_amount": 1599.00,
    "items_count": 1,
    "shipping_address": {...},
    "created_at": "2026-09-13T..."
  }
}
```

---

### 5️⃣ Verify Cart is Empty (30 seconds)

```bash
curl -X GET "https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/cart" \
  -H "Authorization: Bearer $TOKEN"
```

✅ Should show `items: []` (empty cart)

---

## 🎯 Checklist

After running all tests:

- [ ] Step 1: Got access_token
- [ ] Step 2: Got product ID
- [ ] Step 3: Item added to cart (200 status)
- [ ] Step 4: Checkout successful (200 status)
- [ ] Step 4: Order ID created
- [ ] Step 4: Total amount correct
- [ ] Step 5: Cart is now empty

---

## ❌ If Something Fails

| Error | Fix |
|-------|-----|
| `Unauthenticated` | Re-run Step 1, copy token correctly |
| `Cart is empty` | Re-run Step 3 to add an item |
| `Connection refused` | Check internet, verify API is running |
| `Invalid JSON` | Check quotes and commas in command |

---

## 📱 Android App Test

Instead of cURL, you can test directly in the app:

1. **Login:** Account → Login with test credentials
2. **Add Product:** Home → Tap product → Add to Cart
3. **Checkout:** Cart screen → Proceed to Checkout
4. **Enter Details:** Fill shipping info → Confirm Order
5. **Success:** Should see order confirmation

---

## 📊 What This Tests

✅ Authentication (JWT tokens work)
✅ Product fetching (API returns products)
✅ Cart operations (Add to cart works)
✅ **Checkout endpoint works** ← Main test
✅ Order creation (Order is created in DB)
✅ Cart clearing (Cart emptied after checkout)

---

**Total Time:** ~5 minutes ⏱️

