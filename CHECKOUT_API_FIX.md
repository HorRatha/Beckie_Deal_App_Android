# Checkout API - Shipping Method Issue

## 🔴 CURRENT BLOCKER

**Error:**
```json
{
  "status": "error",
  "message": "The given data was invalid.",
  "errors": {
    "shipping_method_id": ["The selected shipping method id is invalid."]
  }
}
```

**Root Cause:** Backend `/api/v1/shipping-methods` returns empty array `[]`. No shipping methods exist in the database.

---

## 🧪 What I Tested

### Test 1: Check Available Shipping Methods
```bash
GET /api/v1/shipping-methods
Authorization: Bearer <token>
```

**Response:**
```json
{
  "status": "success",
  "message": "",
  "data": []  ← Empty! No shipping methods exist
}
```

### Test 2: Checkout with shipping_method_id="1"
```json
{
  "cart_id": "1",
  "shipping_method_id": "1",  ← Backend rejects this
  "recipient_name": "k",
  ...
}
```

**Response:** 422 - "The selected shipping method id is invalid."

---

## ✅ ATTEMPTED FIX

Temporarily removed `shipping_method_id` from checkout request to test if field is optional:

```java
// checkoutData.put("shipping_method_id", "1");  // Commented out
```

**Status:** ✅ Built successfully - Ready to test

---

## 🎯 Next Steps

### Option A: Test Without Field (CURRENT)
Try checkout without `shipping_method_id` to see if backend accepts it.

**Expected Outcomes:**
- ✅ Success → Field is optional, proceed without it
- ❌ Still fails → Field is required, need backend fix

### Option B: Backend Needs to Fix
Contact backend developer to:

1. **Create shipping method(s)** in database:
   ```sql
   INSERT INTO shipping_methods (id, name, description, cost, active) 
   VALUES (1, 'Standard Shipping', 'Delivery within 3-5 days', 5.00, true);
   ```

2. **OR make field optional** in checkout validation (Laravel):
   ```php
   'shipping_method_id' => 'nullable|exists:shipping_methods,id'
   ```

3. **OR provide valid shipping_method_id** to use in app

### Option C: Add Shipping Method Selection UI
If backend adds shipping methods, implement UI:
- Fetch from `/api/v1/shipping-methods`
- Display options to user (Standard, Express, etc.)
- Send selected ID in checkout

---

## 📋 Current Checkout Payload

```json
{
  "cart_id": "1",
  // "shipping_method_id": "1",  ← REMOVED FOR TESTING
  "recipient_name": "k",
  "address_line_1": "k",
  "city": "pp",
  "state": "pp",
  "country": "Cambodia",
  "phone": "855",
  "postal_code": "12000",
  "payment_method": "aba_payway",
  "notes": ""
}
```

---

## 🔍 Backend Investigation

**Shipping Methods Endpoint:**
- URL: `https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/shipping-methods`
- Method: GET
- Auth: Required (Bearer token)
- Response: `{"status":"success","data":[]}`
- **Issue:** Empty array - no methods seeded

**Possible Backend Issues:**
1. ❌ Migration ran but no seeder for shipping_methods table
2. ❌ Seeder exists but wasn't run
3. ❌ Manual data entry needed
4. ❌ Feature not implemented yet

---

## ✅ Summary

**Problem:** Backend requires `shipping_method_id` but has no shipping methods  
**Current Fix:** Removed field to test if optional  
**Build Status:** ✅ Successful  
**Next:** Test checkout without the field  

**If test fails:** This is a backend blocker - they need to either:
- Add shipping methods to database, OR
- Make the field optional in validation

---

**Updated:** 2026-09-13  
**Status:** 🟡 Testing without shipping_method_id  
**Blocked By:** Backend has no shipping methods

## 🔴 Problem
Checkout was failing with 422 error. The backend API structure changed and now requires different fields.

## ❌ Error Message
```json
{
  "status": "error",
  "message": "The given data was invalid.",
  "errors": {
    "cart_id": ["The cart id field is required."],
    "shipping_method_id": ["The shipping method id field is required."],
    "recipient_name": ["The recipient name field is required when address id is not present."],
    "address_line_1": ["The address line 1 field is required when address id is not present."],
    "city": ["The city field is required when address id is not present."],
    "country": ["The country field is required when address id is not present."]
  }
}
```

---

## 🔍 Root Cause

### Old API Structure (What we were sending):
```json
{
  "shipping_address": {
    "recipient_name": "John Doe",
    "street": "123 Main St",      ← Wrong field name
    "city": "Phnom Penh",
    "postal_code": "12000",
    "country": "Cambodia",
    "phone": "+855..."
  },
  "payment_method": "aba_payway",
  "notes": ""
}
```

### New API Structure (What API expects):
```json
{
  "cart_id": "1",                  ← NEW: Required
  "shipping_method_id": "1",       ← NEW: Required
  "recipient_name": "John Doe",    ← Flat, not nested
  "address_line_1": "123 Main St", ← Changed from "street"
  "city": "Phnom Penh",           ← Flat, not nested
  "country": "Cambodia",          ← Flat, not nested
  "phone": "+855...",             ← Flat, not nested
  "postal_code": "12000",         ← Flat, not nested
  "state": "Kandal",              ← Optional
  "payment_method": "aba_payway",
  "notes": ""
}
```

**Key Changes:**
1. ❌ **Removed nesting** - All fields are now flat, not inside `shipping_address`
2. ✅ **Added `cart_id`** - Required field
3. ✅ **Added `shipping_method_id`** - Required field  
4. ✅ **Renamed `street`** → `address_line_1`
5. ✅ **Changed structure** - From nested to flat

---

## ✅ Solution

### 1. Updated `PayoutActivity.java`

**Changed from:**
```java
Map<String, Object> shippingAddress = new HashMap<>();
shippingAddress.put("recipient_name", ...);
shippingAddress.put("street", ...);
// ...

cartRepository.checkout(
    shippingAddress,  // Nested object
    "aba_payway",     // Separate parameter
    "",               // Separate parameter
    callback
);
```

**Changed to:**
```java
Map<String, Object> checkoutData = new HashMap<>();

// Required fields
checkoutData.put("cart_id", String.valueOf(currentCart.getId()));
checkoutData.put("shipping_method_id", "1");  // Default
checkoutData.put("recipient_name", shippingNameInput.getText()...);
checkoutData.put("address_line_1", shippingAddressInput.getText()...);
checkoutData.put("city", shippingCityInput.getText()...);
checkoutData.put("country", country);

// Optional fields
checkoutData.put("phone", shippingPhoneInput.getText()...);
checkoutData.put("postal_code", shippingPostalCodeInput.getText()...);
checkoutData.put("state", ...);
checkoutData.put("payment_method", "aba_payway");
checkoutData.put("notes", "");

cartRepository.checkout(
    checkoutData,  // Flat object with all fields
    callback
);
```

### 2. Updated `CartRepository.java`

**Old Signature:**
```java
public void checkout(Map<String, Object> shippingAddress, 
                    String paymentMethod, 
                    String notes, 
                    CheckoutCallback callback)
```

**New Signature:**
```java
public void checkout(Map<String, Object> checkoutData, 
                    CheckoutCallback callback)
```

Now accepts a single flat map with all checkout data instead of separate parameters.

---

## 📋 New Required Fields

### Absolutely Required:
1. ✅ `cart_id` - Cart ID (integer or string)
2. ✅ `shipping_method_id` - Shipping method ID (currently hardcoded to "1")
3. ✅ `recipient_name` - Recipient's full name
4. ✅ `address_line_1` - Street address (was "street")
5. ✅ `city` - City name
6. ✅ `country` - Country name

### Optional:
- `phone` - Phone number
- `postal_code` - ZIP/postal code
- `state` - State/province
- `address_line_2` - Additional address info
- `payment_method` - Payment method (default: "aba_payway")
- `notes` - Order notes

---

## 🚨 Important Notes

### 1. Shipping Method ID
Currently hardcoded to `"1"`:
```java
checkoutData.put("shipping_method_id", "1");
```

**TODO:** Should be fetched from API or selected by user. For now, using default value.

### 2. Field Name Changes
| Old Field Name | New Field Name |
|----------------|----------------|
| `street` | `address_line_1` |
| (nested in shipping_address) | (flat in root) |

### 3. Structure Change
**Before:** Nested structure with `shipping_address` object  
**After:** Flat structure with all fields at root level

---

## 🧪 Testing

### Test Payload Example:
```json
{
  "cart_id": "1",
  "shipping_method_id": "1",
  "recipient_name": "Test User",
  "address_line_1": "123 Main Street",
  "city": "Phnom Penh",
  "country": "Cambodia",
  "phone": "+855 12 345 678",
  "postal_code": "12000",
  "state": "Kandal",
  "payment_method": "aba_payway",
  "notes": ""
}
```

### Expected Response:
```json
{
  "status": "success",
  "data": {
    "order_id": "ORD-2026-123456",
    "status": "pending",
    "total_amount": 1599.00,
    "items_count": 3,
    ...
  }
}
```

---

## 📝 Debugging Tips

### Check Request in Logs:
```
Filter: tag:CartRepository
Look for: "Checkout request: {cart_id=1, ...}"
```

### Check Response/Error:
```
Filter: tag:CartRepository or tag:PayoutActivity
Look for: 
  - "✓ Checkout successful"
  - "✗ Checkout failed: 422"
  - "Error response: {...}"
```

### Common Issues:

**Issue 1: Missing cart_id**
```
Error: "The cart id field is required."
Fix: Ensure currentCart is loaded and has valid ID
```

**Issue 2: Missing shipping_method_id**
```
Error: "The shipping method id field is required."
Fix: Currently hardcoded to "1" - check if backend accepts this
```

**Issue 3: Wrong field names**
```
Error: "The address line 1 field is required..."
Fix: Use "address_line_1" not "street"
```

---

## 🔄 Migration Summary

### What Changed:
- ✅ API structure: nested → flat
- ✅ Field names: `street` → `address_line_1`
- ✅ New required fields: `cart_id`, `shipping_method_id`
- ✅ Method signature: multiple params → single map
- ✅ Request building: build all fields flat

### What Stayed Same:
- ✅ Callback pattern
- ✅ Response handling
- ✅ UI validation
- ✅ Success dialog display

---

## ✅ Completion Checklist

**Code Changes:**
- [x] Updated PayoutActivity.processCheckout()
- [x] Updated CartRepository.checkout() signature
- [x] Changed from nested to flat structure
- [x] Renamed "street" to "address_line_1"
- [x] Added cart_id field
- [x] Added shipping_method_id field
- [x] Added error logging

**Testing:**
- [ ] Test with real backend
- [ ] Verify all fields are sent correctly
- [ ] Check response is handled properly
- [ ] Test error cases
- [ ] Verify order creation

---

## 🎯 Summary

**Problem:** Checkout failed with 422 - API structure changed  
**Root Cause:** Backend expects flat structure with new required fields  
**Solution:** Updated code to match new API structure  
**Status:** ✅ Fixed and ready for testing  

**Key Fix:**
- Send all fields flat (not nested)
- Use `address_line_1` instead of `street`
- Include `cart_id` and `shipping_method_id`

---

**Updated:** 2026-09-13  
**Status:** ✅ Complete  
**Build:** ✅ Successful  
**Ready for:** Backend Testing
