# Beckie-Deal App - Complete Feature Documentation

## Table of Contents
1. [App Overview](#app-overview)
2. [First Launch & Home Screen](#first-launch--home-screen)
3. [Navigation Flow](#navigation-flow)
4. [Feature Details](#feature-details)
5. [Validation & Business Rules](#validation--business-rules)
6. [Filter Systems](#filter-systems)
7. [User Journey Flows](#user-journey-flows)

---

## App Overview

**Beckie-Deal** is a modern Android e-commerce application for browsing and purchasing technology products including:
- Laptops
- Smartphones
- Gaming devices
- Accessories
- Audio equipment
- Wearables

**Key Technologies:**
- Built with Java for Android
- Material Design 3 components
- Glide for image loading
- In-memory data management (Singleton pattern)
- Responsive layouts supporting phones and tablets

---

## First Launch & Home Screen

### What Users See on First Open

When a user opens the app for the first time, they land on **MainActivity** (Home Screen) which displays:

#### 1. **Top App Bar**
- **Menu Icon** (left): Opens navigation drawer
- **App Title**: "Beckie Deal"
- **Notification Bell** (right): Shows notifications (with badge if any)

#### 2. **Search Bar**
- Tappable search bar that opens dedicated SearchActivity
- Placeholder text: "Search for products"

#### 3. **Hero Banner Carousel** (Auto-rotating)
- 3 promotional slides with gradients:
  - Slide 1: "🔥 HOT DEAL - Summer Sale - Up to 50% off"
  - Slide 2: "⚡ LIMITED - New Arrivals - Latest tech gadgets"
  - Slide 3: "🎁 EXCLUSIVE - Flash Deal - Free shipping $99+"
- Left/Right navigation arrows
- Dot indicators showing current slide
- Auto-scrolls every 4.5 seconds (pauses when user touches)

#### 4. **Categories Section**
- **View All Categories** link (opens CategoriesActivity)
- 2-column grid showing 5 categories:
  - **Laptops** (device icon)
  - **Phones** (mobile icon)
  - **Accessories** (widgets icon)
  - **Gaming** (gaming controller icon) - highlighted as "HOT"
  - **Wearables** (watch icon)
- Each category card shows:
  - Category icon
  - Category name
  - Decorative accent icon (unique per category)

#### 5. **New Arrivals Banner** (Second Carousel)
- 3 promotional slides (different from hero):
  - "✦ JUST LANDED - New Arrivals"
  - "🚀 TRENDING - Top Picks"
  - "💎 PREMIUM - Exclusive Deals"
- Same auto-scroll behavior as hero banner

#### 6. **New Collection Section** (Horizontal Scroll)
- **View All** link (opens ProductListActivity)
- Horizontally scrollable product cards
- Shows 6 premium products (price ≥ $800)
- Auto-scrolls slowly every 2.5 seconds
- Each card shows:
  - Product image
  - Product name
  - Price
  - Star rating
  - Heart icon (favorite toggle)

#### 7. **Top Selling Section** (Horizontal Scroll)
- **View All** link
- Shows 6 top-rated products (rating ≥ 4.7)
- Same card design and behavior as New Collection

#### 8. **Featured Products Section**
- **Grid/List Toggle Button** (changes view mode)
- 2-column responsive grid (1 column on list mode)
- Shows ALL products from catalog
- Standard product cards with:
  - Product image
  - Favorite button
  - Product name
  - Price
  - Rating

#### 9. **Bottom Navigation Bar** (Always visible)
- **Home** (active)
- **Categories**
- **Favorites**
- **Cart** (with badge showing item count)
- **Account**

#### 10. **Navigation Drawer** (Slide from left)
- User avatar section
- Menu items:
  - Home
  - Categories
  - My Orders
  - Favorites
  - Cart
  - Profile
  - About Us
  - Notifications
- Dark Mode toggle
- App version

---

## Navigation Flow

### Main Navigation Structure

```
MainActivity (Home)
├── SearchActivity (Search)
├── CategoriesActivity (Browse Categories)
│   ├── CategoryDetailActivity (Category Products)
│   │   └── ProductDetailActivity
│   └── AllBrandsActivity (All Brands)
├── FavoritesActivity (Saved Products)
│   └── ProductDetailActivity
├── CartActivity (Shopping Cart)
│   └── PayoutActivity (Checkout)
│       └── Success/Back to Home
├── ProfileActivity (Account Settings)
│   ├── LoginActivity
│   ├── RegisterActivity
│   ├── MyOrdersActivity
│   │   └── OrderDetailActivity
│   ├── NotificationsActivity
│   └── AboutActivity
├── ProductListActivity (Full Product Lists)
│   └── ProductDetailActivity
└── NotificationsActivity
```

---

## Feature Details

### 1. Home Screen (MainActivity)

**Purpose**: Main hub for browsing products and accessing key features

**Features:**
- ✅ Auto-rotating hero banners (2 carousels)
- ✅ Category quick access (5 categories)
- ✅ Horizontal scrolling product sections (New Collection, Top Selling)
- ✅ Featured products grid with toggle view (grid/list)
- ✅ Search bar access
- ✅ Cart badge showing item count
- ✅ Navigation drawer
- ✅ Smooth entrance animations

**Data Displayed:**
- Products pulled from central CATALOGUE (in CategoryDetailActivity)
- Categories: Laptops, Phones, Accessories, Gaming, Wearables
- New Collection: Products with price ≥ $800
- Top Selling: Products with rating ≥ 4.7

---

### 2. Search (SearchActivity)

**Purpose**: Full-text search across all products

**Features:**
- ✅ Real-time search (updates as you type)
- ✅ Searches across:
  - Product name
  - Brand name
  - Category name
  - Product SKU
  - Product specs
- ✅ Clear button to reset search
- ✅ Grid/List view toggle
- ✅ Shows result count
- ✅ Empty state when no results
- ✅ Auto-opens keyboard on entry

**Search Example:**
- Typing "mac" shows all MacBook products
- Typing "apple" shows all Apple products across categories
- Typing "gaming" shows gaming laptops and accessories

**Validation:**
- No minimum character requirement
- Case-insensitive search
- Empty search shows helpful prompt

---

### 3. Categories (CategoriesActivity)

**Purpose**: Browse products by category and brand

**Features:**
- ✅ **Laptop Brands Section**:
  - Shows 6 brand cards (preview)
  - "View All Brands" link (opens AllBrandsActivity)
  - Each brand shows:
    - Brand logo/icon
    - Brand name
    - Product count ("X items")
    - Colorful background tint (rotating 6 colors)
  - Selection state (highlighted when tapped)
  - Opens CategoryDetailActivity filtered to brand

- ✅ **All Categories Grid**:
  - 2-column responsive grid
  - 6 categories:
    - Laptops (124 items) - **HOT badge**
    - Smartphones (210 items)
    - Accessories (456 items)
    - Audio (89 items)
    - Gaming (167 items)
    - Wearables (54 items)
  - Each card shows:
    - Hero image (via Unsplash)
    - Category icon
    - Category name
    - Item count
    - "HOT" badge for trending categories

**Brands Available:**
- Apple, ASUS, ROG, Razer, MSI, Dell, HP, Lenovo, Acer, Microsoft, Samsung

**Navigation:**
- Tap brand → CategoryDetailActivity (filtered to that brand)
- Tap category → CategoryDetailActivity (all products in category)
- View All Brands → AllBrandsActivity (full brand list)

---

### 4. Category Detail (CategoryDetailActivity)

**Purpose**: Browse products within a specific category or brand

**Features:**
- ✅ **Hero Banner**: Large image for the category
- ✅ **Brand Filter Chips**: Horizontal scroll showing:
  - "All" (shows all brands)
  - Individual brand chips (Apple, ASUS, Dell, etc.)
  - Selected chip highlighted with primary color
- ✅ **Filter & Sort Button**: Opens FilterBottomSheet
- ✅ **View Mode Toggle**: Switch between grid/list view
- ✅ **Results Label**: Shows applied filters
  - Example: "15 products · Apple · Price: Low → High · 2024 · ≤$2000"
- ✅ **Product Grid**: Responsive 2-column grid (1 column on list view)
- ✅ **Favorite Toggle**: Heart icon on each card

**Filters Available** (via FilterBottomSheet):
1. **Sort Options:**
   - Default
   - Price: Low → High
   - Price: High → Low
   - Top Rated

2. **Year Filter:**
   - Chips for: All, 2026, 2025, 2024, ..., 2010
   - Simulated via price bands (since products lack real year field)

3. **Max Price Slider:**
   - Range: $0 - $3,000
   - Adjustable in $50 increments

**Active Filter Indicators:**
- Orange dot on filter button
- Horizontal scroll showing active filter chips
- Can be removed by resetting

**Data Source:**
- Central CATALOGUE (static data)
- Categories: Laptops, Smartphones, Accessories, Audio, Gaming, Wearables
- Each category contains multiple brands
- Each brand contains multiple products

---

### 5. Product Detail (ProductDetailActivity)

**Purpose**: View complete product information and add to cart

**Features:**
- ✅ **Image Gallery**:
  - Main large image (tappable for fullscreen)
  - 4 thumbnail images below
  - Left/Right navigation arrows
  - Dot indicators (pill for active, circle for inactive)
  - Fullscreen button (opens ImageFullscreenActivity)

- ✅ **Product Information**:
  - Product name
  - Price
  - Star rating
  - Review count (e.g., "120 reviews")
  - Favorite button (heart icon)

- ✅ **Quick Spec Tags** (Pill chips):
  - 3-4 key specifications
  - Example: "Apple M2 Chip", "13.6\" Retina", "8GB Memory"

- ✅ **Description**:
  - Full product description paragraph

- ✅ **Spec Cards** (2x2 Grid):
  - 4 quick-view spec cards with icons:
    - CHIP / CPU
    - MEMORY / RAM
    - STORAGE
    - DISPLAY
  - Each shows icon, label, and value

- ✅ **Detailed Specifications** (Expandable rows):
  - Key-value pairs
  - Example: "CPU: 8-Core CPU", "GPU: 8-Core GPU"
  - Divided by horizontal lines

- ✅ **Quantity Selector**:
  - Decrement/Increment buttons
  - Shows zero-padded quantity (01, 02, ...)
  - Minimum: 1

- ✅ **Action Buttons**:
  - **Add to Cart**: Adds selected quantity to cart
  - **Favorite Button**: Toggle favorite status
  - Shows toast confirmation

- ✅ **Share Button**: "Link copied!" toast

**Product-Specific Data:**
- Different products show tailored specs
- MacBook products: M2/M3 chip, RAM, SSD, Display
- iPhone products: A17 Pro, Camera, Battery, Display
- Gaming laptops: CPU, GPU, RAM, Display refresh rate
- Wearables: Sensors, Battery life, Water resistance

**Animations:**
- Entrance: Gallery, thumbnails, info block slide in
- Button presses: Bounce effect
- Gallery transitions: Smooth image swaps

---

### 6. Favorites (FavoritesActivity)

**Purpose**: View and manage saved products

**Features:**
- ✅ **Product Grid**: Responsive 2-column grid
- ✅ **Heart Icon**: Pre-filled (all items are favorites)
- ✅ **Remove from Favorites**: Tap heart icon
- ✅ **Empty State**:
  - Shows when no favorites
  - "Explore" button → returns to Home
  - Friendly message: "Your favorite items will appear here"
- ✅ **Navigation**: Tap product → ProductDetailActivity

**Data Management:**
- Managed by FavoriteManager (Singleton)
- In-memory storage (resets on app restart)
- Products identified by name (unique key)

**Responsive Layout:**
- Phone: 2 columns
- Tablet (7"): 3 columns
- Large tablet (10"): 4 columns
- Cards equal width with invisible spacers to fill last row

---

### 7. Cart (CartActivity)

**Purpose**: Review cart items and proceed to checkout

**Features:**
- ✅ **Cart Items List**:
  - Product image
  - Product name
  - Unit price
  - Quantity stepper (decrement/increment)
  - Subtotal (price × quantity)
  - Remove button (trash icon)

- ✅ **Quantity Controls**:
  - **Increment (+)**: Adds 1 unit
  - **Decrement (-)**: Removes 1 unit
    - If quantity = 1, decrement removes item entirely
  - **Remove Button**: Deletes item regardless of quantity
  - Updates in real-time (no full re-render)

- ✅ **Order Summary**:
  - Subtotal (sum of all item subtotals)
  - Displays with cart badge everywhere

- ✅ **Checkout Button**:
  - Fixed at bottom
  - Shows total price
  - Opens PayoutActivity

- ✅ **Empty State**:
  - Shows when cart is empty
  - "Start Shopping" button → returns to Home
  - Friendly message with shopping bag icon

**Cart Badge:**
- Shows on bottom navigation Cart icon
- Displays total number of distinct products
- Updates in real-time across all screens

**Data Management:**
- Managed by CartManager (Singleton)
- CartItem wrapper: contains Product + quantity
- Total units: sum of all quantities
- Total price: sum of (price × quantity)

**Price Calculation:**
- Parses price string (removes "$" and ",")
- Calculates: subtotal = unit price × quantity
- Displays: "$XXX.XX" format

---

### 8. Checkout / Payout (PayoutActivity)

**Purpose**: Complete purchase (simulated payment)

**Features:**
- ✅ **Order Summary Card**:
  - Subtotal
  - Shipping: $15.00 (fixed)
  - **Total**: Subtotal + Shipping

- ✅ **Shipping Address Section** (Mock data):
  - Name: "John Doe"
  - Address line 1
  - City, State, ZIP
  - Edit button (not functional - placeholder)

- ✅ **Payment Method Section** (Mock data):
  - Card number (masked): •••• 4242
  - Expiry, CVV fields
  - Edit button (not functional)

- ✅ **Confirm Payment Button**:
  - Starts payment processing
  - Shows loading overlay ("Processing payment...")
  - Simulates 3-second delay

- ✅ **Success Screen** (After processing):
  - Success checkmark animation
  - Order ID (e.g., "BD-1001")
  - Amount paid
  - Success message
  - Two buttons:
    - **Back to Home**: Returns to MainActivity
    - **View Orders**: Opens MyOrdersActivity

**Validation & Business Logic:**
- ⚠️ **NO real validation** (mock checkout)
- Accepts empty fields (placeholder feature)
- No credit card validation
- Always succeeds after 3 seconds

**Order Creation:**
- Creates Order object via OrderManager
- Saves cart snapshot (product name, price, image, quantity)
- Assigns order ID: "BD-XXXX" (incremental)
- Sets date: Current date (e.g., "Dec 18, 2026")
- Sets status: PROCESSING
- Clears cart after successful "payment"

---

### 9. My Orders (MyOrdersActivity)

**Purpose**: View order history (active and completed)

**Features:**
- ✅ **Tab Layout**:
  - **Active Orders**: Processing + Shipped
  - **Completed Orders**: Delivered + Cancelled

- ✅ **Order Cards**:
  - Order ID (e.g., "Order #BD-1001")
  - Order date
  - Status badge with color:
    - **Processing**: Orange (#F97316)
    - **Shipped**: Blue (#1565C0)
    - **Delivered**: Green (#22C55E)
    - **Cancelled**: Red (#EF4444)
  - Item count (e.g., "3 items")
  - Thumbnail strip (up to 4 product images)
  - "+X" indicator if more than 4 items
  - Total amount

- ✅ **View Details Button**:
  - Opens OrderDetailActivity
  - Shows full order breakdown

- ✅ **Empty State** (per tab):
  - Active tab: "No active orders"
  - Completed tab: "No completed orders"
  - "Start Shopping" button → MainActivity

**Order Status Flow:**
1. **Processing** (initial)
2. **Shipped** (in transit)
3. **Delivered** or **Cancelled** (final)

**Data Source:**
- OrderManager.getAllOrders()
- Newest orders first
- Orders persist in-memory (reset on app restart)

---

### 10. Order Detail (OrderDetailActivity)

**Purpose**: View full details of a specific order

**Features:**
- ✅ **Order Header**:
  - Order ID
  - Order date
  - Status badge

- ✅ **Order Items List**:
  - Product image
  - Product name
  - Price per unit
  - Quantity
  - Subtotal

- ✅ **Order Summary**:
  - Subtotal
  - Shipping: $15.00
  - **Total**

- ✅ **Shipping Address**:
  - Full shipping details (mock data)

- ✅ **Payment Method**:
  - Masked card details (mock data)

- ✅ **Order Timeline** (Future feature):
  - Status progression
  - Timestamps for each status

**Navigation:**
- Back button returns to MyOrdersActivity

---

### 11. Profile / Account (ProfileActivity)

**Purpose**: Manage account settings and preferences

**Features:**
- ✅ **User Avatar Section**:
  - Circular avatar (placeholder image)
  - Edit photo button (shows toast)
  - User name: "Guest User"
  - User email: "guest@example.com"

- ✅ **Authentication Buttons** (Not logged in):
  - **Login Button** → LoginActivity
  - **Register Button** → RegisterActivity
  - Side-by-side layout

- ✅ **Dark Mode Toggle**:
  - Light / Dark switch (pill design)
  - Applies theme immediately
  - Persists choice via ThemePreferences
  - Uses AppCompatDelegate.setDefaultNightMode()

- ✅ **Notification Toggle**:
  - On / Off switch
  - Shows toast confirmation
  - (Not connected to real notifications)

- ✅ **Account Section**:
  - **My Orders** → MyOrdersActivity
  - **Notifications** → NotificationsActivity (toast)

- ✅ **Preferences Section**:
  - **Language** (shows toast)
  - **Dark Mode** (toggle above)
  - **Notifications** (toggle above)

- ✅ **Legal Section**:
  - **Privacy Policy** (shows toast)
  - **Terms of Service** (shows toast)
  - **About Us** → AboutActivity

- ✅ **App Version Footer**:
  - "Version 1.0.0"

**Animations:**
- Avatar pops in with scale effect
- Settings cards stagger up from bottom
- Header drops down from top

---

### 12. Login (LoginActivity)

**Purpose**: User authentication (simulated)

**Features:**
- ✅ **Wavy Header Design**:
  - Gradient background
  - Transparent status bar
  - Decorative wave illustration

- ✅ **Form Fields**:
  - Email input
  - Password input (with show/hide toggle)
  - Fields have Material Design outlined style

- ✅ **Login Button**:
  - Primary button
  - Shows "Login successful!" toast
  - Navigates to MainActivity

- ✅ **Forgot Password Link**:
  - Shows toast (not functional)

- ✅ **Register Link**:
  - "Don't have an account? Register"
  - Opens RegisterActivity

**Validation:**
- ⚠️ **NO real validation**
- Empty fields accepted
- No email format check
- No password requirements
- Always succeeds (mock login)

**Animations:**
- Header wave fades down
- Form fields stagger up from bottom

---

### 13. Register (RegisterActivity)

**Purpose**: New user registration (simulated)

**Features:**
- ✅ **Wavy Header Design**: Same as Login

- ✅ **Form Fields**:
  - Full Name
  - Email
  - Password
  - Confirm Password
  - All with Material Design outlined style

- ✅ **Terms & Conditions Checkbox**:
  - "I agree to Terms & Conditions"
  - (Not enforced - can register without checking)

- ✅ **Register Button**:
  - Primary button
  - Shows "Registration successful!" toast
  - Navigates to MainActivity

- ✅ **Login Link**:
  - "Already have an account? Login"
  - Opens LoginActivity (clears Register from stack)

**Validation:**
- ⚠️ **NO real validation**
- Empty fields accepted
- No password matching check
- No terms enforcement
- Always succeeds (mock registration)

**Animations:**
- Header wave fades down
- Form fields stagger up from bottom

---

### 14. Product List (ProductListActivity)

**Purpose**: View full filtered product lists (from Home sections)

**Entry Points:**
- "View All" link on **New Collection** section
- "View All" link on **Top Selling** section

**Features:**
- ✅ **Screen Title**: "New Collection" or "Top Selling"
- ✅ **Filter Button**: Opens FilterBottomSheet
  - Orange dot when filters active
- ✅ **View Mode Toggle**: Grid/List switch
- ✅ **Active Filter Chips**: Horizontal scroll showing applied filters
- ✅ **Results Label**: "Showing X products"
- ✅ **Product Grid**: Responsive 2-column grid

**Data Logic:**
- **New Collection Mode**: Products with price ≥ $800
- **Top Selling Mode**: Products with rating ≥ 4.7

**Filters** (Same as CategoryDetailActivity):
1. Sort (Default, Price Low-High, Price High-Low, Top Rated)
2. Year (All, 2026-2010)
3. Max Price ($0-$3,000 slider)

---

### 15. Notifications (NotificationsActivity)

**Purpose**: Display app notifications

**Current Status:** 🚧 **Placeholder Screen**
- Shows "No notifications" empty state
- Back button returns to previous screen
- No real notification system implemented

**Future Features:**
- Order status updates
- Promotional notifications
- Price drop alerts
- New arrivals

---

### 16. About (AboutActivity)

**Purpose**: Display app information and team details

**Features:**
- ✅ **App Logo/Icon**
- ✅ **App Name & Tagline**
- ✅ **Version Number**
- ✅ **App Description**
- ✅ **Statistics Cards** (mock data):
  - Products Available
  - Happy Customers
  - Years of Service
  - Categories
- ✅ **Team Section** (optional)
- ✅ **Contact Information**
- ✅ **Social Media Links**

---

### 17. All Brands (AllBrandsActivity)

**Purpose**: View complete list of all brands

**Features:**
- ✅ **Brand Grid**: All 11 brands displayed
- ✅ **Brand Cards**:
  - Brand logo
  - Brand name
  - Same design as Categories page

**Brands:**
- ASUS, ROG, Apple, Dell, HP, MSI, Samsung, Lenovo, Acer, Razer, Microsoft

**Navigation:**
- Tap brand → CategoryDetailActivity filtered to that brand
- Each brand opens its primary category:
  - Gaming brands (ROG, MSI, Razer) → Gaming category
  - Samsung → Smartphones category
  - Others → Laptops category

---

### 18. Image Fullscreen (ImageFullscreenActivity)

**Purpose**: View product images in fullscreen

**Features:**
- ✅ **Immersive Mode**: Hides status/navigation bars
- ✅ **Image Gallery**: Swipeable ViewPager2
- ✅ **Zoom Support**: Pinch-to-zoom (if implemented)
- ✅ **Image Counter**: "1 / 4" indicator
- ✅ **Close Button**: Returns to product detail
- ✅ **Left/Right Navigation Arrows**

**Entry:**
- Tap main image on ProductDetailActivity
- Tap fullscreen icon on product detail

**Data:**
- Receives ArrayList of image URLs
- Receives start index (current image)

---

## Validation & Business Rules

### Shopping Flow Validation

#### 1. Adding to Cart
- ✅ **No login required** to add items to cart
- ✅ Minimum quantity: 1
- ✅ Maximum quantity: Unlimited (no stock check)
- ✅ Same product added multiple times → quantity increases
- ✅ Cart persists across navigation (until app closes)

#### 2. Favorites
- ✅ **No login required** to add favorites
- ✅ Unlimited favorites
- ✅ Duplicate prevention (same product only added once)
- ✅ Toggle on/off (heart icon)
- ✅ Favorites persist across navigation (until app closes)

#### 3. Checkout Process
**Current Implementation: NO real validation**

- ⚠️ **No authentication required** - Anyone can checkout
- ⚠️ **No address validation** - Mock data used
- ⚠️ **No payment validation** - Always succeeds
- ⚠️ **No stock checking** - Unlimited inventory
- ⚠️ **No minimum order amount**

**What SHOULD be validated (recommended for production):**
- ❌ User must be logged in
- ❌ Shipping address required (name, address, city, state, zip)
- ❌ Valid payment method
- ❌ Stock availability check
- ❌ Price verification
- ❌ Order minimum ($10+)

#### 4. User Authentication
**Current Implementation: Mock login/register**

- ⚠️ **No email validation**
- ⚠️ **No password requirements** (length, strength)
- ⚠️ **No password confirmation matching**
- ⚠️ **No terms acceptance enforcement**
- ⚠️ **Always succeeds** (no database)
- ⚠️ **No session management** (no persistent login)

**What SHOULD be validated:**
- ❌ Email format: `user@domain.com`
- ❌ Password: Minimum 8 characters, 1 uppercase, 1 number
- ❌ Passwords must match (register)
- ❌ Terms checkbox must be checked
- ❌ Email uniqueness check
- ❌ Proper authentication backend

### Data Validation Rules

#### Product Data
- ✅ Product name: Required (never null)
- ✅ Price: Required, format "$X,XXX.XX"
- ✅ Rating: Required, format "X.X" (0.0-5.0)
- ✅ Image URL: Required (uses placeholder if missing)
- ✅ Product identification: By name (unique key)

#### Cart Data
- ✅ Quantity: Integer ≥ 1
- ✅ Subtotal calculation: price × quantity
- ✅ Price parsing: Removes "$" and "," before calculation

#### Order Data
- ✅ Order ID: Auto-generated, format "BD-XXXX"
- ✅ Date: Auto-set to current date
- ✅ Status: Enum (PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- ✅ Total: Includes $15 shipping fee

---

## Filter Systems

### 1. Category Detail Filters

**Accessible via**: Filter button on CategoryDetailActivity

#### Filter Options:

**A. Sort**
- **Default**: Catalog order (no sorting)
- **Price: Low → High**: Ascending by price
- **Price: High → Low**: Descending by price
- **Top Rated**: Descending by rating

**B. Year**
- **Chips**: All, 2026, 2025, ..., 2010
- **Logic**: Simulated via price bands:
  - Each year maps to ~$100-400 price range
  - 2010 = $0-400
  - 2024 = $1400-1800
  - 2026 = $1800+
- **Note**: Real app should use actual release year field

**C. Max Price**
- **Slider**: $0 - $3,000
- **Increment**: $50
- **Default**: $3,000 (no filter)
- **Display**: Shows "≤$X" in results label

#### Filter UI:

**Active Filter Indicators:**
- ✅ Orange dot on filter button
- ✅ Horizontal scroll of filter chips
- ✅ Results label shows all active filters
- ✅ Example: "15 products · Apple · Price: Low → High · 2024 · ≤$2000"

**Filter Reset:**
- ✅ Reset button in bottom sheet
- ✅ Clears all filters back to defaults

### 2. Product List Filters

**Accessible via**: Filter button on ProductListActivity

- Same filter options as Category Detail
- Same UI and behavior
- Applies to New Collection or Top Selling products

### 3. Brand Filter

**Accessible via**: Brand chips on CategoryDetailActivity

- **Location**: Below hero banner
- **Display**: Horizontal scrollable chips
- **Options**: "All" + individual brands
- **Logic**:
  - "All": Shows products from all brands
  - Specific brand: Shows only that brand's products
- **Selection**: Single selection (mutually exclusive)
- **Visual**: Selected chip has primary color background

### 4. Search Filter

**Accessible via**: SearchActivity

- **No explicit filters** (no filter button)
- **Real-time filtering** as user types
- **Multi-field search**:
  - Product name
  - Brand name
  - Category name
  - SKU
  - Specs
- **Case-insensitive**
- **Substring matching**

---

## User Journey Flows

### Journey 1: Browse and Add to Cart

**Steps:**
1. User opens app → Lands on Home (MainActivity)
2. Scrolls through categories and products
3. Taps on a product card → ProductDetailActivity opens
4. Views product images, specs, and description
5. Adjusts quantity (e.g., 2 units)
6. Taps "Add to Cart" button
7. Toast shows: "2× added to cart!"
8. Cart badge updates: Shows "1" (1 product type, 2 units)
9. User continues shopping or navigates to Cart

**Validation Points:**
- ✅ No login required
- ✅ Quantity minimum: 1
- ✅ Cart updates immediately
- ✅ Badge reflects distinct product count

---

### Journey 2: Checkout and Place Order

**Steps:**
1. User navigates to Cart (via bottom nav)
2. Reviews cart items, adjusts quantities if needed
3. Taps "Checkout" button → PayoutActivity opens
4. Reviews order summary (subtotal + $15 shipping)
5. Sees mock shipping address and payment method
6. Taps "Confirm Payment" button
7. Loading overlay appears: "Processing payment..."
8. After 3 seconds: Success screen appears
9. Shows order ID (e.g., "BD-1001") and amount paid
10. User chooses:
    - **Back to Home**: Returns to MainActivity
    - **View Orders**: Opens MyOrdersActivity

**Behind the Scenes:**
- Order created in OrderManager
- Cart snapshot saved (products, quantities, prices)
- Order status set to PROCESSING
- Cart cleared after successful payment
- Order appears in "Active Orders" tab

**Validation Points:**
- ⚠️ NO authentication check
- ⚠️ NO address validation
- ⚠️ NO payment validation
- ✅ Order always succeeds

---

### Journey 3: Browse by Category and Filter

**Steps:**
1. User taps "Categories" on bottom navigation
2. CategoriesActivity opens
3. User scrolls through categories
4. Taps "Gaming" category card
5. CategoryDetailActivity opens with Gaming products
6. User sees hero banner and brand filters
7. Taps "ROG" brand chip → Filters to ROG products
8. Taps Filter button → FilterBottomSheet opens
9. Selects:
   - Sort: "Price: Low → High"
   - Year: "2024"
   - Max Price: $2,500
10. Taps "Apply" button
11. Product grid updates with filtered results
12. Results label shows: "12 products · ROG · Price: Low → High · 2024 · ≤$2500"
13. User finds desired product and taps card
14. ProductDetailActivity opens

**Validation Points:**
- ✅ Filters combine (AND logic)
- ✅ Brand + Sort + Year + Price all apply
- ✅ Empty result shows "No products found"
- ✅ Reset button clears all filters

---

### Journey 4: Search for Product

**Steps:**
1. User taps search bar on Home screen
2. SearchActivity opens, keyboard auto-appears
3. User types "macbook"
4. Results update in real-time
5. Shows all MacBook products (MacBook Air, MacBook Pro)
6. Results label: "5 results for \"macbook\""
7. User continues typing: "macbook pro"
8. Results narrow to MacBook Pro models
9. User toggles to List view
10. Taps a product card
11. ProductDetailActivity opens

**Search Behavior:**
- ✅ Case-insensitive
- ✅ Searches name, brand, category, SKU, specs
- ✅ Updates as you type (no search button)
- ✅ Clear button (X) resets search
- ✅ Empty search shows helpful prompt

---

### Journey 5: Manage Favorites

**Steps:**
1. User browses products on Home
2. Taps heart icon on a product card (outline → filled)
3. Toast shows: "Added to Favorites"
4. Heart icon turns red (filled)
5. User navigates to Favorites tab (bottom nav)
6. FavoritesActivity opens
7. Shows all favorited products in grid
8. User taps heart icon on a product (filled → outline)
9. Toast shows: "Removed from Favorites"
10. Product removed from grid
11. If grid becomes empty: Empty state appears

**Validation Points:**
- ✅ No login required
- ✅ Unlimited favorites
- ✅ Duplicate prevention (can't favorite twice)
- ✅ State syncs across all screens
- ✅ Favorites persist during session

---

### Journey 6: View Order History

**Steps:**
1. User navigates to Profile (bottom nav)
2. ProfileActivity opens
3. Taps "My Orders" row
4. MyOrdersActivity opens
5. User sees two tabs: "Active" and "Completed"
6. Active tab shows Processing and Shipped orders
7. Each order card shows:
   - Order ID, date, status badge
   - Product thumbnails (up to 4)
   - Item count and total amount
8. User taps "View Details" on an order
9. OrderDetailActivity opens
10. Shows full order breakdown:
    - All products with quantities
    - Subtotal, shipping, total
    - Shipping address and payment method
11. User returns to orders list
12. Switches to "Completed" tab
13. Sees Delivered and Cancelled orders

**Order Status Progression:**
- PROCESSING (orange) → Initial state
- SHIPPED (blue) → In transit
- DELIVERED (green) → Successfully delivered
- CANCELLED (red) → Order cancelled

---

### Journey 7: Login and Register

**Steps:**
1. User navigates to Profile tab
2. Sees "Login" and "Register" buttons
3. Taps "Login" button
4. LoginActivity opens (wavy header design)
5. User enters email and password (no validation)
6. Taps "Login" button
7. Toast shows: "Login successful!"
8. Returns to MainActivity

**Alternative: Register Flow:**
1. User taps "Register" link on Login screen
2. RegisterActivity opens
3. User enters:
   - Full name
   - Email
   - Password
   - Confirm password
4. (Optional) Checks "I agree to Terms"
5. Taps "Register" button
6. Toast shows: "Registration successful!"
7. Returns to MainActivity

**Current Limitations:**
- ⚠️ No actual authentication
- ⚠️ No persistent session
- ⚠️ No user profile creation
- ⚠️ No validation
- ⚠️ Always succeeds

---

### Journey 8: Switch Theme (Dark Mode)

**Steps:**
1. User navigates to Profile tab
2. Sees "Dark Mode" toggle section
3. Currently shows "Light" selected (white background)
4. Taps "Dark" option
5. UI immediately updates:
   - Dark pill background activates
   - Icon color changes to white
   - App theme switches to dark mode
6. All screens now show dark backgrounds
7. Theme preference saved (persists across app restarts)

**Technical:**
- Uses ThemePreferences.setDarkMode()
- Calls AppCompatDelegate.setDefaultNightMode()
- MODE_NIGHT_YES or MODE_NIGHT_NO
- Applies to all activities immediately

---

## Summary of Key Features

### ✅ Implemented Features

**Shopping Features:**
- ✅ Browse products (Home, Categories, Search)
- ✅ View product details (images, specs, description)
- ✅ Add to cart (with quantity selection)
- ✅ Manage cart (update quantities, remove items)
- ✅ Checkout (simulated payment)
- ✅ Order history (active and completed)
- ✅ Favorites (add/remove, view list)

**Browsing Features:**
- ✅ Category browsing (6 categories)
- ✅ Brand filtering (11 brands)
- ✅ Product search (multi-field, real-time)
- ✅ Advanced filtering (sort, year, price)
- ✅ Grid/List view toggle
- ✅ Responsive layouts (phone, tablet)

**User Features:**
- ✅ Login/Register screens (mock)
- ✅ Profile management (settings)
- ✅ Dark mode toggle
- ✅ Notification toggle
- ✅ About page

**UI/UX Features:**
- ✅ Bottom navigation (5 tabs)
- ✅ Navigation drawer (side menu)
- ✅ Auto-rotating carousels (2 on home)
- ✅ Horizontal scrolling sections (New Collection, Top Selling)
- ✅ Smooth entrance animations
- ✅ Button press bounce effects
- ✅ Toast notifications
- ✅ Empty states with friendly messages
- ✅ Cart and notification badges

### ⚠️ Limitations (Mock/Placeholder Features)

**Authentication:**
- ⚠️ No real user accounts (mock login)
- ⚠️ No session management
- ⚠️ No password security
- ⚠️ No validation

**Payment:**
- ⚠️ No real payment processing
- ⚠️ No payment gateway integration
- ⚠️ No address validation
- ⚠️ Always succeeds after 3 seconds

**Data Persistence:**
- ⚠️ In-memory storage only (resets on app close)
- ⚠️ No database (no SQLite, Room, Firebase)
- ⚠️ Cart, favorites, orders lost on restart
- ⚠️ No sync across devices

**Product Management:**
- ⚠️ Static product catalog (hardcoded)
- ⚠️ No inventory management
- ⚠️ No stock checking
- ⚠️ No dynamic pricing
- ⚠️ No product variants (size, color)

**Notifications:**
- ⚠️ Placeholder screen only
- ⚠️ No real notification system
- ⚠️ No push notifications
- ⚠️ No order status alerts

### 🎯 Production Recommendations

To make this app production-ready, implement:

1. **Backend Integration:**
   - User authentication (Firebase Auth, JWT)
   - Database (Firebase Firestore, PostgreSQL)
   - Payment gateway (Stripe, PayPal)
   - Order management system

2. **Data Validation:**
   - Email validation
   - Password requirements (8+ chars, uppercase, number)
   - Address validation (zip code, state)
   - Payment validation (card number, CVV)
   - Stock checking

3. **Security:**
   - Encrypted storage (passwords, tokens)
   - HTTPS API calls
   - Input sanitization
   - Rate limiting

4. **Enhanced Features:**
   - User reviews and ratings
   - Product variants (size, color)
   - Wishlist sharing
   - Order tracking
   - Push notifications
   - Live chat support

5. **Testing:**
   - Unit tests (business logic)
   - UI tests (Espresso)
   - Integration tests
   - Payment testing (sandbox)

---

## Conclusion

The **Beckie-Deal** app is a fully-featured e-commerce shopping experience with:
- ✅ Intuitive navigation (bottom nav + drawer)
- ✅ Rich product browsing (categories, brands, search, filters)
- ✅ Complete shopping flow (cart, checkout, orders)
- ✅ Modern UI/UX (Material Design 3, animations)
- ✅ Responsive design (phones and tablets)
- ✅ Dark mode support

**Current Status:** Functional demo/prototype with mock data and authentication

**Next Steps for Production:**
1. Integrate real backend API
2. Implement authentication system
3. Add payment gateway
4. Set up database (users, products, orders)
5. Add data validation
6. Implement push notifications
7. Add comprehensive testing

---

*Document Version: 1.0*  
*Last Updated: December 18, 2026*  
*App Version: 1.0.0*
