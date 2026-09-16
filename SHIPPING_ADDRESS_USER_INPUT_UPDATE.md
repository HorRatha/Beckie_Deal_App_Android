# Shipping Address User Input Implementation

## Overview
Replaced static hardcoded shipping address with dynamic user input fields. Users now **MUST** enter their shipping information before completing checkout.

---

## Changes Made

### 1. **Layout Changes** (`activity_payout.xml`)

#### Before:
```xml
<!-- Static display with hardcoded address -->
<TextView android:text="Home Address" />
<TextView android:text="123 Tech Avenue, Silicon Valley, CA" />
<ImageView android:src="@android:drawable/ic_menu_edit" />
```

#### After:
```xml
<!-- Dynamic input fields -->
<EditText android:id="@+id/shippingNameInput" 
    android:hint="Enter recipient name" />
<EditText android:id="@+id/shippingPhoneInput" 
    android:hint="+855 12 345 678" />
<EditText android:id="@+id/shippingAddressInput" 
    android:hint="Enter street address" />
<EditText android:id="@+id/shippingCityInput" 
    android:hint="Phnom Penh" />
<EditText android:id="@+id/shippingPostalCodeInput" 
    android:hint="12000" />
<EditText android:id="@+id/shippingStateInput" 
    android:hint="Kandal" />  <!-- Optional -->
<EditText android:id="@+id/shippingCountryInput" 
    android:hint="Cambodia" />
```

### 2. **Java Code Changes** (`PayoutActivity.java`)

#### A. Field Initialization - `setupShippingFields()`

**Before:**
```java
// Fields were commented out - NOT initialized
// shippingNameInput = findViewById(R.id.shippingNameInput);
```

**After:**
```java
// Fields are now properly initialized
shippingNameInput = findViewById(R.id.shippingNameInput);
shippingPhoneInput = findViewById(R.id.shippingPhoneInput);
shippingAddressInput = findViewById(R.id.shippingAddressInput);
shippingCityInput = findViewById(R.id.shippingCityInput);
shippingPostalCodeInput = findViewById(R.id.shippingPostalCodeInput);

// Pre-fill country with default
EditText countryInput = findViewById(R.id.shippingCountryInput);
if (countryInput != null) {
    countryInput.setText("Cambodia");
}
```

#### B. Validation - `validateShippingInfo()`

**Before:**
```java
// Allowed checkout without validation
if (shippingNameInput == null) {
    return true;  // Skip validation
}
```

**After:**
```java
// Strict validation with specific error messages
if (name.isEmpty()) {
    shippingNameInput.setError("Recipient name is required");
    shippingNameInput.requestFocus();
    return false;
}
// ... validates all required fields
```

**Validation Rules:**
- ✅ **Required**: Name, Phone, Address, City, Postal Code
- ⚠️ **Optional**: State/Province
- ℹ️ **Default**: Country (pre-filled with "Cambodia")

#### C. Checkout Data - `processCheckout()`

**Before:**
```java
// Used static fallback data
shippingAddress.put("name", "Customer");
shippingAddress.put("phone", "+855");
shippingAddress.put("address", "Phnom Penh");
```

**After:**
```java
// Uses actual user input
shippingAddress.put("recipient_name", shippingNameInput.getText().toString().trim());
shippingAddress.put("phone", shippingPhoneInput.getText().toString().trim());
shippingAddress.put("street", shippingAddressInput.getText().toString().trim());
shippingAddress.put("city", shippingCityInput.getText().toString().trim());
shippingAddress.put("postal_code", shippingPostalCodeInput.getText().toString().trim());
shippingAddress.put("state", stateInput != null ? stateInput.getText().toString() : "");
shippingAddress.put("country", countryInput != null ? countryInput.getText().toString() : "Cambodia");
```

---

## API Compliance

### Shipping Address Fields Sent to API

According to `CHECKOUT_TESTING_CHECKLIST.md` section 3.1, the API expects:

```json
{
  "shipping_address": {
    "recipient_name": "Test User",      // ✅ Now from user input
    "street": "123 Main Street",        // ✅ Now from user input
    "city": "Phnom Penh",              // ✅ Now from user input
    "state": "Kandal",                 // ✅ Now from user input (optional)
    "postal_code": "12000",            // ✅ Now from user input
    "country": "Cambodia",             // ✅ Now from user input (default)
    "phone": "+855 (98) 999-9999"      // ✅ Now from user input
  }
}
```

**All fields now come from user input, not hardcoded values!**

---

## User Experience Flow

### 1. **User Opens Checkout**
- Sees empty shipping fields (except country pre-filled)
- All fields are clearly labeled with (*) for required fields

### 2. **User Fills Information**
- Enters: Name, Phone, Address, City, Postal Code
- Optionally enters: State/Province
- Country defaults to "Cambodia" but can be changed

### 3. **User Taps "Confirm Payment"**
- **Validation runs automatically**
- If any required field is empty:
  - Field shows red error message
  - Focus moves to first invalid field
  - Toast message: "Please fill in all shipping information"
  - Checkout does NOT proceed

### 4. **All Fields Valid**
- Data sent to API with real user input
- Payment processing begins

---

## Validation Error Messages

| Field | Error Message |
|-------|--------------|
| Name | "Recipient name is required" |
| Phone | "Phone number is required" |
| Address | "Street address is required" |
| City | "City is required" |
| Postal Code | "Postal code is required" |

**Error Display:**
- Shows inline on the field (red text)
- Field gets focus automatically
- User can see exactly what needs to be fixed

---

## Input Field Types

| Field | Input Type | Purpose |
|-------|-----------|----------|
| Name | `textPersonName` | Capitalizes names properly |
| Phone | `phone` | Shows numeric keyboard with symbols |
| Address | `textPostalAddress` | Optimized for addresses |
| City | `text` | Standard text input |
| Postal Code | `number` | Numeric keyboard only |
| State | `text` | Standard text input |
| Country | `text` | Standard text input |

---

## Field Layout

### Visual Structure:
```
┌─ Shipping Address Card ──────────────────────┐
│                                               │
│  Recipient Name *                            │
│  [_____________________________________]     │
│                                               │
│  Phone Number *                              │
│  [_____________________________________]     │
│                                               │
│  Street Address *                            │
│  [_____________________________________]     │
│                                               │
│  City *              │  Postal Code *        │
│  [________________] │ [________________]     │
│                                               │
│  State/Province (Optional)                   │
│  [_____________________________________]     │
│                                               │
│  Country                                     │
│  [_____________________________________]     │
│                                               │
└───────────────────────────────────────────────┘
```

---

## Testing Checklist

### ✅ Test Scenarios

**Scenario 1: Empty Fields**
1. Open checkout with items in cart
2. Tap "Confirm Payment" without filling fields
3. **Expected**: First field shows error, checkout blocked

**Scenario 2: Partial Fields**
1. Fill only Name and Phone
2. Tap "Confirm Payment"
3. **Expected**: Address field shows error, checkout blocked

**Scenario 3: All Required Fields**
1. Fill: Name, Phone, Address, City, Postal Code
2. Tap "Confirm Payment"
3. **Expected**: Validation passes, checkout proceeds

**Scenario 4: With Optional Fields**
1. Fill all required + State
2. Tap "Confirm Payment"
3. **Expected**: State included in API request

**Scenario 5: Check API Request**
1. Complete checkout with real data
2. Check Logcat for: "Shipping address prepared: {..."
3. **Expected**: Shows actual user input, not static data

---

## Debugging

### Check Logs for Shipping Data

**Filter:** `tag:PayoutActivity`

**Look for:**
```
D/PayoutActivity: Shipping address prepared: {
    recipient_name=John Doe, 
    phone=+855 12 345 678, 
    street=123 Main St, 
    city=Phnom Penh, 
    postal_code=12000, 
    state=Kandal, 
    country=Cambodia
}
```

**Verify:** All values match what user typed, not hardcoded defaults!

---

## Before vs After Comparison

### Before Update:
❌ Static address display  
❌ No user input  
❌ Hardcoded values sent to API  
❌ No validation  
❌ Users couldn't specify their address  

### After Update:
✅ Dynamic input fields  
✅ User enters their own address  
✅ Real data sent to API  
✅ Strict validation with helpful errors  
✅ Users can specify exactly where to ship  

---

## Future Enhancements (Optional)

1. **Save Address to Profile**
   - Store in user's account
   - Pre-fill on next checkout

2. **Multiple Addresses**
   - Let user save multiple addresses
   - Choose from saved addresses dropdown

3. **Address Autocomplete**
   - Integrate Google Maps API
   - Auto-complete as user types

4. **Validation Enhancement**
   - Phone number format validation
   - Postal code format by country
   - Real-time field validation

5. **Country Picker**
   - Replace EditText with Spinner
   - Show list of countries

---

## Related Files

- `activity_payout.xml` - Layout with input fields (updated)
- `PayoutActivity.java` - Logic and validation (updated)
- `CHECKOUT_TESTING_CHECKLIST.md` - API spec reference
- `CHECKOUT_DYNAMIC_RESPONSE_UPDATE.md` - Related checkout updates

---

## Build Status

✅ **Compilation**: Success  
✅ **Layout**: Fields properly defined  
✅ **Validation**: Working with error messages  
✅ **API Integration**: Sends real user data  

---

**Updated:** 2026-09-13  
**Issue Fixed:** Shipping address now collected from user, not hardcoded  
**Status:** ✅ Complete and tested
