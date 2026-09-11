<div align="center">

# 🛒 Beckie Deal

**A modern Android e-commerce app for tech products — built for Cambodia's market with full API integration.**

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java-ED8B00?logo=openjdk&logoColor=white)](https://www.java.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-blue)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-brightgreen)](https://developer.android.com/about/versions/15)
[![Version](https://img.shields.io/badge/Version-1.0-orange)](https://github.com)
[![Backend](https://img.shields.io/badge/Backend-Laravel%20REST%20API-FF2D20?logo=laravel&logoColor=white)](https://laravel.com)
[![Auth](https://img.shields.io/badge/Auth-JWT-000000?logo=json-web-tokens&logoColor=white)](https://jwt.io)
[![API Status](https://img.shields.io/badge/API-Live-success)](https://documenter.getpostman.com/view/51923872/2sBYAvvVnR)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)

[Features](#-features) • [Architecture](#-architecture--tech-stack) • [Getting Started](#-getting-started) • [API Docs](https://documenter.getpostman.com/view/51923872/2sBYAvvVnR)

</div>

---

## 📱 Overview

Beckie Deal is a modern tech shopping app built with native Android (Java) and powered by a Laravel backend API. It delivers a complete e-commerce experience — from browsing products and categories to managing cart and placing orders — with a clean Material Design 3 interface and seamless API integration.

The app features a comprehensive catalogue of laptops, smartphones, gaming gear, accessories, audio products, and wearables from top brands like Apple, ASUS, Samsung, Dell, HP, Lenovo, Microsoft, MSI, Razer, and more — all served dynamically from the backend API.

---

## ✨ Features

### 🏠 Home
- Hero promotional banner with call-to-action
- Horizontal category quick-access row powered by Category API
- **New Collection** horizontal scroll — flagship products ($800+), sorted by price from Product API
- **Top Selling** horizontal scroll — highest-rated products (4.7+) from Product API
- Featured product grid with real-time data from backend
- Favourite toggling with local state management

### 🔍 Search
- Full-text search across the entire catalogue — matches product name, brand, category, specs, and SKU
- Live results as you type
- Keyboard action support (Search key triggers search and hides the keyboard)
- Empty state with contextual guidance messages

### 📂 Categories
- Browse all categories dynamically loaded from Category API: **Laptops · Smartphones · Accessories · Audio · Gaming · Wearables**
- Real-time product data from Product API with category filtering
- Per-category hero banner with dynamic product count
- Brand filter chips (filter by brand within a category using Brand API)
- Filter & Sort bottom sheet:
  - Sort by Default / Price Low→High / Price High→Low / Top Rated
  - Release Year chips (2010–2026)
  - Max price slider ($0–$3,000, snaps to $50 increments)
- Results label updates dynamically with active filters
- Efficient API pagination support

### ❤️ Favourites
- Add or remove any product from favourites with one tap
- Persistent in-session state via `FavoriteManager` singleton
- Dedicated Favourites screen

### 🛒 Cart
- Add products to cart from the Product Detail screen
- Increment / decrement quantity per item, or remove entirely
- Live subtotal per line item and grand total
- Cart badge count shown on all top bars
- Local cart state management via `CartManager` singleton
- Proceeds to Payout / Checkout screen
- **Note:** Backend Cart API endpoints are being prepared — current implementation uses local storage with planned API integration

### 🧾 Product Detail
- Real-time product data from Product API with variants support
- Full product card: image, name, price, star rating, SKU, specs, warranty, stock status
- **Enhanced Features:**
  - Product variants display (if available)
  - Free items label when applicable
  - Dynamic stock status indicator
  - Related products suggestions
- Add to Cart button with quantity selection
- Favourite toggle
- Product mapper utility for data transformation

### 👤 Profile
- Login and Register navigation buttons (fully integrated with Laravel backend API)
- **Secure JWT Authentication** with encrypted token storage
- My Orders row (coming soon)
- Notifications toggle switch
- Language preference row
- Dark Mode toggle (system-wide via `AppCompatDelegate`)

### 🔐 Authentication & Security
- **Full Backend Integration:** Connected to Laravel API on Google Cloud Run
- **Comprehensive Auth Flow:**
  - User registration with email validation
  - JWT-based login with access and refresh tokens
  - Automatic token refresh mechanism
  - Secure logout with token invalidation
  - User profile fetching (GET `/auth/user`)
- **JWT Token Management:** 
  - Automatic token injection in API requests via `AuthInterceptor`
  - Token expiry: 60 days
  - Refresh token support
- **Security Features:**
  - AES-256-GCM encryption for sensitive data via `SecurePreferences`
  - HTTPS-only communication with network security config
  - Certificate pinning ready
  - Input validation on all forms
- **Session Management:**
  - Auto-login with persisted sessions
  - Graceful token expiry handling
  - Secure token storage in EncryptedSharedPreferences

### 📍 About
- Contact form with name / email / message validation, sends via email intent to `beckiedeal@gmail.com`
- Store location map card (Google Maps Static API) that opens Google Maps on tap
- Phnom Penh, Cambodia HQ coordinates

### 🗂️ Navigation
- Floating glass-style bottom navigation bar (Home · Categories · Favourites · About · Account)
- Side navigation drawer (`AppDrawerHelper`) accessible from any screen via the menu icon

---

## 🏗️ Architecture & Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 11 |
| **Backend API** | Laravel REST API (cloud-hosted) |
| **Authentication** | JWT with access & refresh tokens |
| **Security** | AndroidX Security-Crypto (EncryptedSharedPreferences) + AES-256-GCM |
| **Networking** | Retrofit 2.9.0 + OkHttp 4.12.0 with logging interceptor |
| **HTTP Interceptors** | AuthInterceptor for automatic JWT injection |
| **JSON Parsing** | Gson 2.10.1 |
| **Image Loading** | Glide 5.0 with caching |
| **UI Toolkit** | Material Design 3 (`com.google.android.material:material:1.13.0`) |
| **Layout** | ConstraintLayout, LinearLayout, GridLayout, MaterialCardView |
| **Navigation** | Manual Intent-based with bottom nav + side drawer |
| **State Management** | Singleton managers (CartManager, FavoriteManager, TokenManager) |
| **Repository Pattern** | ProductRepository, CategoryRepository, BrandRepository |
| **Build System** | Gradle with Kotlin DSL (build.gradle.kts) |
| **Min SDK** | 24 (Android 7.0 Nougat) |
| **Target SDK** | 36 |

### API Integration Status

| Feature | Backend Status | Android Status |
|---------|---------------|----------------|
| Authentication (Login/Register) | ✅ Working | ✅ Implemented |
| Products & Variants | ✅ Working | ✅ Implemented |
| Categories | ✅ Working | ✅ Implemented |
| Brands | ✅ Working | ✅ Implemented |
| Product Search & Filtering | ✅ Working | ✅ Implemented |
| Shipping Methods | ✅ Working | ✅ Implemented |
| Cart | ⚠️ Pending Backend | ✅ Local Implementation Ready |
| Wishlist | ⚠️ Pending Backend | ✅ Local Implementation Ready |
| Orders | ⚠️ Testing Needed | 🔄 In Development |

---

## 📁 Project Structure

```
app/src/main/
├── java/aeu/ratha/beckie_deal_app/
│   ├── data/
│   │   ├── api/                      # Retrofit API service interfaces
│   │   │   ├── AuthApiService.java       # Auth endpoints (login, register, user, logout, refresh)
│   │   │   ├── ProductApiService.java    # Product endpoints (list, detail, variants)
│   │   │   ├── CategoryApiService.java   # Category endpoints
│   │   │   └── BrandApiService.java      # Brand endpoints
│   │   ├── models/                   # API data models & responses
│   │   │   ├── User.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── LoginResponse.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── RegisterResponse.java
│   │   │   ├── UserProfileResponse.java
│   │   │   ├── Product.java
│   │   │   ├── ProductResponse.java
│   │   │   ├── Category.java
│   │   │   ├── CategoryResponse.java
│   │   │   ├── Brand.java
│   │   │   ├── BrandResponse.java
│   │   │   └── Pagination.java
│   │   └── repository/               # Data layer abstraction
│   │       ├── ProductRepository.java
│   │       ├── CategoryRepository.java
│   │       └── BrandRepository.java
│   ├── network/
│   │   ├── RetrofitClient.java       # API client configuration with OkHttp
│   │   └── AuthInterceptor.java      # JWT auto-injection interceptor
│   ├── security/
│   │   ├── TokenManager.java         # JWT token CRUD operations
│   │   └── SecurePreferences.java    # AES-256-GCM encrypted storage
│   ├── utils/
│   │   └── helpers/
│   │       └── ProductMapper.java    # API response to local model mapper
│   ├── ui/
│   │   ├── activities/
│   │   │   ├── main/
│   │   │   │   └── MainActivity.java         # Home screen with API data
│   │   │   ├── auth/
│   │   │   │   ├── LoginActivity.java        # Login with JWT
│   │   │   │   └── RegisterActivity.java     # Registration
│   │   │   ├── product/
│   │   │   │   ├── CategoriesActivity.java   # All categories from API
│   │   │   │   ├── CategoryDetailActivity.java # Products by category
│   │   │   │   ├── ProductDetailActivity.java  # Product detail with variants
│   │   │   │   ├── ProductListActivity.java
│   │   │   │   ├── SearchActivity.java
│   │   │   │   └── AllBrandsActivity.java
│   │   │   ├── cart/
│   │   │   │   ├── CartActivity.java
│   │   │   │   ├── PayoutActivity.java
│   │   │   │   ├── MyOrdersActivity.java
│   │   │   │   └── OrderDetailActivity.java
│   │   │   └── profile/
│   │   │       ├── ProfileActivity.java
│   │   │       ├── FavoritesActivity.java
│   │   │       └── AboutActivity.java
│   │   ├── adapters/                 # RecyclerView adapters
│   │   ├── viewholders/              # ViewHolder classes
│   │   └── fragments/                # Bottom sheets & dialogs
│   ├── managers/
│   │   ├── CartManager.java          # Cart state singleton
│   │   └── FavoriteManager.java      # Favourites state singleton
│   └── helpers/
│       ├── AppDrawerHelper.java      # Navigation drawer
│       ├── ProductCardBinder.java    # Shared product card binding
│       └── ScreenUtils.java          # Screen utilities
├── res/
│   ├── layout/                       # All activity & item layouts (XML)
│   ├── drawable/                     # Icons, backgrounds, shapes
│   ├── xml/
│   │   └── network_security_config.xml  # HTTPS & certificate pinning config
│   ├── values/
│   │   ├── strings.xml               # All string resources
│   │   ├── colors.xml                # Color palette
│   │   └── themes.xml                # Material 3 themes
│   └── color/                        # Color state lists
└── AndroidManifest.xml               # App configuration & permissions
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Meerkat** (2024.3) or later
- Android SDK 36
- JDK 11+
- An Android device or emulator running Android 7.0+ (API 24+)
- Internet connection for API access

### Build & Run

```bash
# 1. Clone the repository
git clone https://github.com/your-username/Beckie-Deal.git
cd Beckie-Deal

# 2. Open in Android Studio
#    File → Open → select the project folder

# 3. Let Gradle sync automatically
#    The project will download all dependencies

# 4. API Configuration (already set)
#    Base URL is configured in app/build.gradle.kts
#    No additional setup needed

# 5. Run on device or emulator
#    Run → Run 'app'
```

### First Time Setup

1. **Register a new account** using the Register screen
   
2. **Login** to access personalized features

3. **Browse products** from the home screen or categories

4. **Add to cart** and proceed to checkout

### API Connection

The app connects to a Laravel backend API deployed on Google Cloud Run:
- **Documentation:** [Postman API Docs](https://documenter.getpostman.com/view/51923872/2sBYAvvVnR)
- **Authentication:** JWT tokens with automatic refresh
- **Security:** HTTPS-only, certificate validation, encrypted local storage

### Troubleshooting

**Build Issues:**
- Clean and rebuild: `Build → Clean Project` then `Build → Rebuild Project`
- Invalidate caches: `File → Invalidate Caches → Invalidate and Restart`

**API Connection Issues:**
- Ensure device has internet connection
- Verify API base URL is correctly configured in `build.gradle.kts`
- Check network security config allows HTTPS
- Confirm backend API is accessible

**Authentication Issues:**
- Token expired: Simply login again
- Clear app data if persistent issues occur

**Performance:**
- Enable ProGuard for release builds (already configured)
- Glide caching reduces image loading times

### Optional: Google Maps Static API

The About page shows a store location map. To load the real map snapshot, add your [Maps Static API key](https://developers.google.com/maps/documentation/maps-static/get-api-key) in `AboutActivity.java`:

```java
// AboutActivity.java
String staticMapUrl = "https://maps.googleapis.com/maps/api/staticmap"
        + "...&key=YOUR_API_KEY";  // ← replace this
```

The map card still works without a key — tapping it opens Google Maps or the device's default maps app.

---

## 📦 Key Dependencies

```kotlin
// build.gradle.kts (app level)

android {
    namespace = "aeu.ratha.beckie_deal_app"
    compileSdk = 36

    defaultConfig {
        applicationId = "aeu.ratha.beckie_deal_app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // API Configuration (configured via build config)
        buildConfigField("String", "API_BASE_URL", "\"YOUR_API_BASE_URL\"")
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // AndroidX Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.cardview:cardview:1.0.0")

    // Material Design 3
    implementation("com.google.android.material:material:1.13.0-alpha03")

    // Networking & API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Security & Encryption
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Image Loading & Caching
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    annotationProcessor("com.github.bumptech.glide:compiler:5.0.0-rc01")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
```

### Network Configuration

**Network Security Config** (`res/xml/network_security_config.xml`):
```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
    <!-- Production domain configuration -->
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">your-api-domain.com</domain>
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </domain-config>
</network-security-config>
```

**Security Features:**
- ✅ Enforces HTTPS-only communication
- ✅ Blocks cleartext (HTTP) traffic
- ✅ Ready for certificate pinning
- ✅ Trusts system certificates
- ✅ AES-256-GCM encryption for local sensitive data

---

## 🔗 API Documentation & Testing

### API Documentation
Full API documentation is available on Postman:
- **Documentation URL:** https://documenter.getpostman.com/view/51923872/2sBYAvvVnR
- **API Type:** RESTful API with JWT authentication

### Testing Status
Comprehensive API testing has been completed. For detailed test reports, see:
- `API_ENDPOINTS_STATUS.md` - Full endpoint status report
- `CART_API_TEST_RESULTS.md` - Detailed cart API testing

### Working Endpoints ✅
- **Authentication:** Login, Register, User Profile, Logout, Token Refresh
- **Products:** List with pagination, Product Details, Product Variants
- **Categories:** List categories, Category details with products
- **Brands:** List brands, Brand details with products
- **Master Data:** Categories, Brands, Shipping Methods

### Pending Backend Implementation ⚠️
- Cart endpoints (prepared on Android side)
- Wishlist endpoints (prepared on Android side)
- Orders endpoints (under testing)

---

## 🎨 Design Highlights

- **Material Design 3** throughout — rounded cards, ripple feedback, proper elevation
- **Floating glass bottom nav** — custom pill background with translucent primary indicator
- **Consistent product card** — shared `ProductCardBinder` ensures every screen renders cards identically
- **Dark mode ready** — toggled from the Profile page, applied system-wide instantly
- **Activity-based navigation** — clean, straightforward navigation without fragments
- **Repository pattern** — clean separation between data sources and UI
- **Efficient image loading** — Glide with caching and placeholder support
- **Responsive layouts** — adapts to different screen sizes and orientations

---

## 📸 Screens at a Glance

| Screen | Description | Data Source |
|---|---|---|
| Home | Hero + categories + featured + horizontal scroll sections | Product API, Category API |
| Categories | Grid of category cards with icon and label | Category API |
| Category Detail | Brand chips + filter sheet + 2-column product grid | Product API, Brand API |
| Product Detail | Full product info with variants, add to cart, favourite | Product API |
| Search | Live full-text search across entire catalogue | Product API with search params |
| Favourites | Saved products list | Local state + Product API |
| Cart | Items, quantity controls, total, checkout button | Local CartManager |
| Payout | Checkout flow | Local + planned API integration |
| Login | JWT authentication with email/password | Auth API |
| Register | New user registration | Auth API |
| Profile | Account info, dark mode, settings | Auth API (user profile) |
| About | Contact form + interactive map | Local |

---

## 🆕 Recent Updates & Improvements

### Backend API Integration (September 2026)
- ✅ **Full Authentication System:** JWT-based login/register with token management
- ✅ **Product Catalog Integration:** Real-time product data from Laravel API
- ✅ **Category & Brand APIs:** Dynamic category and brand filtering
- ✅ **Product Variants Support:** Multi-variant product handling
- ✅ **Pagination Support:** Efficient data loading with page-based navigation
- ✅ **Network Security:** HTTPS-only with certificate validation
- ✅ **Encrypted Storage:** AES-256-GCM for sensitive data

### Architecture Enhancements
- ✅ **Repository Pattern:** Clean data layer abstraction (ProductRepository, CategoryRepository, BrandRepository)
- ✅ **AuthInterceptor:** Automatic JWT token injection in API requests
- ✅ **ProductMapper:** API response transformation utility
- ✅ **Secure Token Management:** TokenManager with encrypted preferences
- ✅ **Network Logging:** OkHttp logging interceptor for debugging

### Planned Features
- 🔄 **Orders Management:** My Orders screen and order tracking
- 🔄 **Backend Cart Sync:** Integration when cart API endpoints are deployed
- 🔄 **Backend Wishlist Sync:** Integration when wishlist API endpoints are deployed
- 🔄 **Push Notifications:** Order updates and promotional alerts
- 🔄 **Payment Gateway:** Integration with payment providers

---

## 🤝 Contributing

Pull requests are welcome. For larger changes, please open an issue first to discuss what you'd like to change.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'Add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

<div align="center">

Built with ❤️ by the Beckie Deal team · Phnom Penh, Cambodia 🇰🇭

</div>
