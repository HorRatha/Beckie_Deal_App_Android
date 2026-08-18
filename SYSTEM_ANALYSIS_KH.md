# ការវិភាគប្រព័ន្ធ Beckie Deal E-Commerce

## 4.3 ការវិភាគប្រព័ន្ធ (System Analysis)

### 4.3.1 ការវិភាគលើប្រព័ន្ធថ្មី (New System Analysis)

បន្ទាប់ពីបានសិក្សាទៅលើប្រព័ន្ធបច្ចុប្បន្ន ក្រុមសិក្សាស្រាវជ្រាវសង្កេតឃើញថា ក្រុមហ៊ុន Beckie Deal ទទួលបានប្រព័ន្ធ E-Commerce ពេញលេញមួយ ដែលមានទាំង Web Application និង Mobile Application ដែលអាចឆ្លើយតបទៅនឹងតម្រូវការរបស់អាជីវកម្មដូចជា៖

---

### ស្ថាបត្យកម្មប្រព័ន្ធទាំងមូល (Overall System Architecture)

ប្រព័ន្ធមានអ្នកប្រើប្រាស់ ៣ ប្រភេទ៖

#### 1. **Client (អតិថិជន)**
- អ្នកទិញផលិតផលតាមរយៈ Mobile Application
- អាច Browse ផលិតផលដោយសេរី (មិនចាំបាច់ Login)
- ត្រូវ Register/Login ដើម្បីធ្វើការទិញផលិតផល

#### 2. **User (បុគ្គលិក/Staff)**  
- បុគ្គលិកក្រុមហ៊ុនដែលមានសិទ្ធិមួយចំនួន
- ត្រូវបានបង្កើតដោយ Admin
- មាន Role/Position ជាក់លាក់
- គ្រប់គ្រងតាមរយៈ Web Application

#### 3. **Admin (អ្នកគ្រប់គ្រងប្រព័ន្ធ)**
- អ្នកគ្រប់គ្រងប្រព័ន្ធពេញលេញ
- គ្រប់គ្រង Products, Categories, Users, Orders, Reports
- គ្រប់គ្រងតាមរយៈ Web Application

---

### វិសាលភាពការងារ Mobile Application

**សំខាន់:** Mobile Application របស់យើងផ្តល់សេវាកម្មសម្រាប់ **Client (អតិថិជន)** តែប៉ុណ្ណោះ។

ការគ្រប់គ្រង User និង Admin ត្រូវបានដំណើរការដោយ **Web Application** (ក្រុមការងារ Website)។

---

### Data Flow Diagram (DFD) - Mobile App Scope

តាម DFD ខាងលើ Mobile Application គ្រប់គ្រង Processes ទាំងនេះ៖

#### **Process 3: Login/Logout System**
- Client Login ចូលប្រើប្រាស់ប្រព័ន្ធ
- Client Logout ចាកចេញពីប្រព័ន្ធ
- ភ្ជាប់ទៅ **D4: User** (User Database Table)

#### **Process 2: Make Orders**  
- Client បញ្ជាទិញផលិតផល
- បង្កើត Order ថ្មី
- រក្សាទុកទៅក្នុង **D3: Orders** (Orders Database Table)

#### **Process 4: Create Account (Client Register)**
- Client ចុះឈ្មោះគណនីថ្មី
- បង្កើត User Account
- រក្សាទុកទៅក្នុង **D5: User** (User Database Table)

#### **Process 7: Document Management**
- Client មើល Product Documents
- Client Upload Personal Documents (ប្រសិនបើមាន)
- ភ្ជាប់ទៅ **D6: Document** (Document Database Table)

#### **Direct Data Access:**
- **View Product Details** - មើលព័ត៌មានលម្អិតផលិតផលពី **D1: Products**
- **View Products** - រកមើលផលិតផលតាម **D2: Category**
- **Search Product** - ស្វែងរកផលិតផលពី **D1: Products**

---

### បច្ចេកវិទ្យានិងស្ថាបត្យកម្ម Mobile Application

#### Platform & Development Stack
- **Platform:** Android Native Application
- **Language:** Java 11
- **Min SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 36 (Android 15)
- **Build System:** Gradle with Kotlin DSL

#### UI Framework & Libraries
- **Design System:** Material Design 3 (`com.google.android.material:material:1.13.0`)
- **Image Loading:** Glide 5.0 (`com.github.bumptech.glide:glide:5.0.0-rc01`)
- **Layouts:** ConstraintLayout, MaterialCardView, ViewPager2

#### Architecture Pattern
- **Navigation:** Intent-based with Bottom Navigation + Side Drawer
- **State Management:** Singleton Managers (CartManager, FavoriteManager, OrderManager)
- **Data Storage:** SharedPreferences for User Preferences
- **API Communication:** RESTful API (នឹង integrate ជាមួយ Backend)

---

## មុខងារសម្រាប់ Client តាមរយៈ Mobile Application

### 🔓 មុខងារមិនចាំបាច់ Login (Guest Mode)

#### ១. ប្រព័ន្ធផ្សព្វផ្សាយផលិតផលតាមអនឡាញ
**ទិន្នន័យប្រភព:** ពី **D1: Products** និង **D2: Category**

- ✅ មើលផលិតផលទាំងអស់ (View Products from D1)
- ✅ Home Screen ជាមួយ Hero Banner Carousel
- ✅ Featured Products Section
- ✅ "New Collection" Section - ផលិតផលថ្មីៗតម្លៃខ្ពស់ ($800+)
- ✅ "Top Selling" Section - ផលិតផល Rating ខ្ពស់ (4.7+)
- ✅ Promotional Banners

#### ២. ការរកមើលតាមប្រភេទ និង Brand (Browse by Category & Brand)

**ទិន្នន័យប្រភព:** ពី **D2: Category**

- ✅ Browse by Categories:
  - Laptops
  - Smartphones
  - Gaming
  - Accessories
  - Wearables
- ✅ Browse by Brands: Apple, ASUS, Samsung, Dell, HP, Lenovo, Microsoft, MSI, Razer
- ✅ Category Hero Banner
- ✅ Product Count Display

#### ៣. មើលព័ត៌មានលម្អិតផលិតផល (View Product Details)

**ទិន្នន័យប្រភព:** ពី **D1: Products**

- ✅ រូបភាពផលិតផលគុណភាពខ្ពស់
- ✅ ឈ្មោះផលិតផល និងតម្លៃ
- ✅ Star Rating និង Review Count
- ✅ SKU Number
- ✅ Product Specifications (Specs)
- ✅ Warranty Information
- ✅ Stock Status (In Stock/Out of Stock)
- ✅ Free Items Label

#### ៤. ស្វែងរកផលិតផល (Search Product)

**Process:** Search Product ពី **D1: Products**

- ✅ Full-text Search ឆ្លងកាត់ Product Name, Brand, Category, Specs, SKU
- ✅ Live Search Results បង្ហាញភ្លាមៗពេលវាយអក្សរ
- ✅ Keyboard Action Support (Search Key)
- ✅ Empty State Message

#### ៥. Filter & Sort (ច្រោះតម និងរៀបតាម)

- ✅ Sort by: Default, Price Low→High, Price High→Low, Top Rated
- ✅ Filter by Brand (Brand Chips)
- ✅ Filter by Release Year (2010-2026)
- ✅ Filter by Max Price ($0-$3,000 ជាមួយ $50 increments)
- ✅ បង្ហាញចំនួនលទ្ធផលដែលត្រូវនឹង Filter

#### ៦. View Mode Toggle

- ✅ Switch រវាង Grid View (2 columns) និង List View (1 column)
- ✅ Smooth Animation
- ✅ រក្សាទុក View Preference

#### ៧. ព័ត៌មានបន្ថែម

- ✅ View Banners/Promotions
- ✅ View About Us
- ✅ Contact Form
- ✅ Store Location Map (Google Maps Static API)
- ✅ FAQs
- ✅ Terms & Privacy Policy

---

### 🔐 មុខងារត្រូវការ Login (Authenticated Features)

#### ១. ការចុះឈ្មោះ និងគ្រប់គ្រងគណនី

**Process 4: Create Account (Client Register)**  
**ទិន្នន័យទៅកាន់:** **D5: User**

##### 1.1 Register (ចុះឈ្មោះ)
- ✅ បំពេញព័ត៌មានផ្ទាល់ខ្លួន (Name, Email, Password)
- ✅ ការពិនិត្យមើល Terms and Conditions
- ✅ បង្កើតគណនីថ្មីក្នុង D5: User Table
- 🔄 Email Verification (នឹង integrate ជាមួយ Backend API)

**Process 3: Login/Logout System**  
**ទិន្នន័យភ្ជាប់:** **D4: User**

##### 1.2 Login (ចូលប្រើប្រាស់)
- ✅ Login UI ជាមួយ Email និង Password
- 🔄 API Authentication (នឹង integrate)
- 🔄 JWT Token Storage
- 🔄 Session Management

##### 1.3 Forget Password (ភ្លេចពាក្យសម្ងាត់)
- ✅ Forgot Password UI
- 🔄 Reset Password Link via Email (នឹង integrate)

##### 1.4 Logout (ចាកចេញ)
- ✅ Logout មុខងារ
- 🔄 Clear Session និង Token (នឹង integrate)

##### 1.5 Profile Management
- ✅ មើល និងកែប្រែ Profile
- ✅ Manage Delivery Addresses (ទិន្នន័យពី addresses table)
- ✅ Change Password
- ✅ Avatar Upload

#### ២. ការគ្រប់គ្រង Shopping Cart

**ទិន្នន័យ:** **carts** និង **cart_items** tables

##### 2.1 Add to Cart (បញ្ចូលទៅកន្ត្រក)
- ✅ Add Product ពី Product Detail Screen
- ✅ បង្ហាញសារ "Added to Cart"
- ✅ Update Cart Badge Count

##### 2.2 View Cart (មើលកន្ត្រក)
- ✅ បង្ហាញផលិតផលទាំងអស់ក្នុង Cart
- ✅ រូបភាព, ឈ្មោះ, តម្លៃ, Quantity
- ✅ Subtotal សម្រាប់នីមួយៗ Line Item
- ✅ Grand Total

##### 2.3 Update Cart Items (កែប្រែទំនិញ)
- ✅ Increment Quantity (+)
- ✅ Decrement Quantity (-)
- ✅ Remove Item (X)
- ✅ Auto-update Totals

##### 2.4 Proceed to Checkout
- ✅ Checkout/Payout Screen
- ✅ Order Summary
- ✅ Shipping Cost Calculation

#### ៣. ការបញ្ជាទិញផលិតផល (Orders Management)

**Process 2: Make Orders**  
**ទិន្នន័យទៅកាន់:** **D3: Orders** និង **order_items**

##### 3.1 Purchase Order (Place Order)
- ✅ Review Order Summary
- ✅ Select Delivery Address (ពី addresses table)
- 🔄 Select Payment Method (នឹង integrate Payment Gateway)
- ✅ Place Order Button
- ✅ Order Confirmation Screen
- ✅ បញ្ជាទិញត្រូវបានរក្សាទុកក្នុង D3: Orders

##### 3.2 View My Orders (មើលការបញ្ជាទិញ)
- ✅ My Orders Screen
- ✅ Active Orders Tab
- ✅ Completed Orders Tab
- ✅ Order Status Display (Pending, Processing, Shipped, Delivered, Cancelled)
- ✅ ទិន្នន័យពី D3: Orders Table

##### 3.3 View Order Details (មើលព័ត៌មានលម្អិត)
- ✅ Order Number (Order ID)
- ✅ Order Date
- ✅ Products List (ពី order_items table)
- ✅ Delivery Status
- ✅ Total Amount
- ✅ Shipping Information

#### ៤. Wishlist/Favorites Management

**ទិន្នន័យ:** **wishlists** និង **wishlist_items** tables

##### 4.1 Add to Favourites
- ✅ Toggle Favourite Icon (Heart) នៅលើ Product Card
- ✅ រក្សាទុកក្នុង wishlists table
- ✅ Visual Feedback

##### 4.2 View Favourites
- ✅ Favourites Screen
- ✅ បង្ហាញផលិតផលទាំងអស់ដែលចូលចិត្ត (ពី wishlist_items)
- ✅ Click to Product Detail

##### 4.3 Remove from Favourites
- ✅ Toggle Heart Icon
- ✅ Auto-update List

#### ៥. Reviews & Ratings

**ទិន្នន័យ:** **reviews** table

##### 5.1 Submit Reviews
- ✅ Rate Products (1-5 stars)
- ✅ Write Review Comment
- 🔄 Submit to Backend API (នឹង integrate)
- ✅ រក្សាទុកក្នុង reviews table

##### 5.2 View Reviews
- ✅ បង្ហាញ Reviews ទាំងអស់សម្រាប់ផលិតផល
- ✅ Filter by Rating
- ✅ Sort by Date/Helpful

#### ៦. Document Management

**Process 7: Document**  
**ទិន្នន័យ:** **D6: Document**

##### 6.1 View Product Documents
- ✅ មើល Product Manuals, Specifications
- ✅ Download Documents
- ✅ ទិន្នន័យពី D6: Document Table

##### 6.2 Upload Personal Documents (ប្រសិនបើមាន)
- 🔄 Upload ID Documents (សម្រាប់ Verification)
- 🔄 រក្សាទុកក្នុង D6: Document

#### ៧. Notifications

##### 7.1 Push Notifications
- ✅ Receive Order Updates
- ✅ Promotional Notifications
- ✅ New Product Alerts

##### 7.2 Notification Center
- ✅ View All Notifications
- ✅ Mark as Read
- ✅ Notification Badge Count

#### ៨. Settings & Preferences

##### 8.1 Dark Mode
- ✅ Toggle រវាង Light Mode និង Dark Mode
- ✅ System-wide Application
- ✅ រក្សាទុក Preference

##### 8.2 Language Settings
- ✅ ជ្រើសរើសភាសា (Khmer, English)
- ✅ Apply Language Preference

##### 8.3 Notification Settings
- ✅ Toggle On/Off Notifications
- ✅ Notification Preferences

---

## ទិន្នន័យ Database Tables ដែល Mobile App ប្រើប្រាស់

តាម DFD និង Database Schema Mobile Application ភ្ជាប់ទៅ Tables ទាំងនេះ៖

| Database Table | Process/Usage | Description |
|---|---|---|
| **D1: products** | View Products, Search | ទិន្នន័យផលិតផល |
| **D2: categories** | Browse by Category | ប្រភេទផលិតផល |
| **brands** | Filter by Brand | Brand ផលិតផល |
| **D3: orders** | Process 2 (Make Orders) | ការបញ្ជាទិញ |
| **order_items** | Order Details | ផលិតផលក្នុង Order |
| **D4: User** | Process 3 (Login/Logout) | Authentication |
| **D5: User** | Process 4 (Register) | គណនីអ្នកប្រើប្រាស់ |
| **D6: Document** | Process 7 (Documents) | ឯកសារផលិតផល |
| **carts** | Shopping Cart | កន្ត្រកទិញទំនិញ |
| **cart_items** | Cart Items List | ផលិតផលក្នុងកន្ត្រក |
| **wishlists** | Favorites | បញ្ជីចូលចិត្ត |
| **wishlist_items** | Wishlist Items | ផលិតផលចូលចិត្ត |
| **addresses** | Delivery Address | អាសយដ្ឋានដឹកជញ្ជូន |
| **payments** | Payment Processing | ការទូទាត់ប្រាក់ |
| **reviews** | Product Reviews | ការវាយតម្លៃផលិតផល |
| **product_images** | Product Gallery | រូបភាពផលិតផល |
| **coupons** | Discount Codes | លេខកូដបញ្ចុះតម្លៃ |
| **shipments** | Order Tracking | តាមដានការដឹកជញ្ជូន |

---

## Backend API Integration (អនាគត)

បន្ទាប់ពី Web Team បញ្ចប់ Backend API, Mobile App នឹង integrate endpoints ទាំងនេះ៖

### Authentication APIs
```
POST /api/register         - Register new user (Process 4)
POST /api/login            - Login (Process 3)
POST /api/logout           - Logout (Process 3)
POST /api/forgot-password  - Reset password
GET  /api/profile          - Get user profile
PUT  /api/profile          - Update profile
```

### Products APIs
```
GET  /api/products                - Get all products (D1)
GET  /api/products/{id}           - Get product details (D1)
GET  /api/products/search?q=...   - Search products (D1)
GET  /api/categories              - Get categories (D2)
GET  /api/brands                  - Get brands
GET  /api/products/filter         - Filter products
```

### Cart APIs
```
GET    /api/cart                 - Get user cart (carts table)
POST   /api/cart/add             - Add item (cart_items)
PUT    /api/cart/update/{id}     - Update quantity
DELETE /api/cart/remove/{id}     - Remove item
POST   /api/cart/clear           - Clear cart
```

### Orders APIs
```
POST /api/orders                 - Place order (Process 2 → D3)
GET  /api/orders                 - Get user orders (D3)
GET  /api/orders/{id}            - Get order details (order_items)
PUT  /api/orders/{id}/cancel     - Cancel order
GET  /api/orders/{id}/track      - Track shipment
```

### Wishlist APIs
```
GET    /api/wishlist             - Get wishlist (wishlists table)
POST   /api/wishlist/add         - Add to wishlist (wishlist_items)
DELETE /api/wishlist/remove/{id} - Remove from wishlist
```

### Reviews APIs
```
POST /api/reviews                - Submit review (reviews table)
GET  /api/products/{id}/reviews  - Get product reviews
PUT  /api/reviews/{id}           - Update review
```

### Documents APIs
```
GET  /api/documents/{productId}  - Get product documents (Process 7, D6)
POST /api/documents/upload       - Upload document
```

### Payment APIs
```
POST /api/payments/process       - Process payment (payments table)
GET  /api/payments/{id}          - Get payment details
POST /api/payments/refund        - Request refund
```

---

## សេចក្តីសន្និដ្ឋាន

ប្រព័ន្ធ Beckie Deal E-Commerce រួមបញ្ចូលនូវ Mobile Application និង Web Application ដែលធ្វើការជាមួយគ្នា។

### ការចែកចាយការងារ៖

| Component | Responsibility | Team |
|---|---|---|
| **Mobile App** | Client interface, Shopping experience | Mobile Team |
| **Web App** | Admin panel, User management, Reports | Website Team |
| **Backend API** | Business logic, Data processing | Website Team |
| **Database (PostgreSQL)** | Data storage | Website Team |

### ស្ថានភាពបច្ចុប្បន្ន Mobile Application៖

✅ **រួចរាល់:**
- UI/UX Implementation 100%
- All Client Features (with mock data)
- User Flow និង Navigation
- Material Design 3 គុណភាពខ្ពស់

⏳ **កំពុងរង់ចាំ:**
- Backend API endpoints ពី Website Team
- Database setup និង data migration
- Payment Gateway configuration

🔄 **ជំហានបន្ទាប់:**
1. Web Team បញ្ចប់ Backend API + Database
2. Mobile Team integrate API calls (replace mock data)
3. Authentication និង Session Management
4. Real-time data synchronization
5. Testing និង Debugging
6. Production Deployment

### សារៈសំខាន់នៃប្រព័ន្ធថ្មី៖

✓ ផ្តល់នូវបទពិសោធន៍ទិញទំនិញដ៏ល្អតាមរយៈ Mobile Platform  
✓ គ្រប់គ្រងផលិតផល និង Inventory បានច្បាស់លាស់  
✓ ការទូទាត់ប្រាក់មានសុវត្ថិភាព  
✓ តាមដានការបញ្ជាទិញបានរហ័ស  
✓ ទទួលបានអតិថិជនកាន់តែច្រើនតាមរយៈ Mobile App  
✓ របាយការណ៍និងការគ្រប់គ្រងច្បាស់លាស់តាមរយៈ Web Admin Panel  

---

**Created for:** Beckie Deal E-Commerce Project  
**Document Version:** 2.0  
**Last Updated:** 2026  
**Mobile Team Focus:** Client Application Development
