<div align="center">

# 🛒 Beckie Deal

**A modern Android e-commerce app for tech products — built for Cambodia's market.**

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Language](https://img.shields.io/badge/Language-Java-ED8B00?logo=openjdk&logoColor=white)](https://www.java.com)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-blue)](https://developer.android.com/about/versions/nougat)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-36-brightgreen)](https://developer.android.com/about/versions/15)
[![Version](https://img.shields.io/badge/Version-1.0-orange)](https://github.com)
[![License](https://img.shields.io/badge/License-MIT-lightgrey)](LICENSE)

</div>

---

## 📱 Overview

Beckie Deal is a fully-featured tech shopping app built with native Android (Java). It covers the complete customer journey — from browsing a rich product catalogue to adding items to cart and checking out — with a clean Material Design 3 interface and a smooth, responsive UX.

The app is designed around a real-world catalogue of laptops, smartphones, gaming gear, accessories, audio products, and wearables from top brands like Apple, ASUS, Samsung, Dell, HP, Lenovo, Microsoft, MSI, Razer, and more.

---

## ✨ Features

### 🏠 Home
- Hero promotional banner with a call-to-action
- Horizontal category quick-access row
- **New Collection** horizontal scroll — flagship products ($800+), sorted by price
- **Top Selling** horizontal scroll — highest-rated products (4.7+)
- Featured product grid with favourite toggling

### 🔍 Search
- Full-text search across the entire catalogue — matches product name, brand, category, specs, and SKU
- Live results as you type
- Keyboard action support (Search key triggers search and hides the keyboard)
- Empty state with contextual guidance messages

### 📂 Categories
- Browse all categories: **Laptops · Smartphones · Accessories · Audio · Gaming · Wearables**
- Per-category hero banner with product count
- Brand filter chips (filter by brand within a category)
- Filter & Sort bottom sheet:
  - Sort by Default / Price Low→High / Price High→Low / Top Rated
  - Release Year chips (2010–2026)
  - Max price slider ($0–$3,000, snaps to $50 increments)
- Results label updates dynamically with active filters

### ❤️ Favourites
- Add or remove any product from favourites with one tap
- Persistent in-session state via `FavoriteManager` singleton
- Dedicated Favourites screen

### 🛒 Cart
- Add products to cart from the Product Detail screen
- Increment / decrement quantity per item, or remove entirely
- Live subtotal per line item and grand total
- Cart badge count shown on all top bars
- Proceeds to Payout / Checkout screen

### 🧾 Product Detail
- Full product card: image, name, price, star rating, SKU, specs, warranty, stock status, free items label
- Add to Cart button
- Favourite toggle

### 👤 Profile
- Login and Register navigation buttons (fully integrated with Laravel backend API)
- **Secure JWT Authentication** with encrypted token storage
- My Orders row (coming soon)
- Notifications toggle switch
- Language preference row
- Dark Mode toggle (system-wide via `AppCompatDelegate`)

### 🔐 Authentication & Security
- **Backend Integration:** Connects to Laravel API on Google Cloud Run
- **JWT Token Management:** Automatic token injection in API requests
- **Encrypted Storage:** AES-256-GCM encryption for sensitive data
- **HTTPS-only:** Secure network communication
- **Auto-login:** Session persists across app restarts
- **Input Validation:** Email and password validation before API calls

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
| Language | Java 11 |
| Backend API | Laravel on Google Cloud Run |
| Authentication | JWT with encrypted local storage |
| Networking | Retrofit 2.9.0 + OkHttp 4.12.0 |
| Security | AndroidX Security-Crypto (EncryptedSharedPreferences) |
| JSON Parsing | Gson 2.10.1 |
| UI toolkit | Material Design 3 (`com.google.android.material:material:1.13.0`) |
| Image loading | Glide 5.0 |
| Layout | ConstraintLayout, LinearLayout, GridLayout, MaterialCardView |
| Navigation | Manual `Intent`-based, with bottom nav + side drawer |
| State management | Singleton managers (`CartManager`, `FavoriteManager`, `TokenManager`) |
| Build system | Gradle with Kotlin DSL (`build.gradle.kts`) |
| Min SDK | 24 (Android 7.0 Nougat) |
| Target SDK | 36 |

---

## 📁 Project Structure

```
app/src/main/
├── java/aeu/ratha/beckie_deal_app/
│   ├── data/
│   │   ├── api/
│   │   │   └── AuthApiService.java  # Authentication API endpoints
│   │   └── models/                   # API data models
│   │       ├── User.java
│   │       ├── LoginRequest.java
│   │       ├── LoginResponse.java
│   │       ├── RegisterRequest.java
│   │       └── RegisterResponse.java
│   ├── network/
│   │   ├── RetrofitClient.java       # API client configuration
│   │   └── AuthInterceptor.java      # JWT auto-injection
│   ├── security/
│   │   ├── TokenManager.java         # JWT token management
│   │   └── SecurePreferences.java    # Encrypted storage
│   ├── MainActivity.java             # Home screen, mock data seed
│   ├── CategoriesActivity.java       # All-categories grid
│   ├── CategoryDetailActivity.java   # Per-category product list + CATALOGUE data
│   ├── ProductDetailActivity.java    # Full product detail + add to cart
│   ├── ProductListActivity.java      # Flat list mode (New Collection / Top Selling)
│   ├── SearchActivity.java           # Full-text search
│   ├── FavoritesActivity.java        # Saved favourites list
│   ├── CartActivity.java             # Cart screen with quantity controls
│   ├── PayoutActivity.java           # Checkout / payout
│   ├── LoginActivity.java            # Login screen with API integration
│   ├── RegisterActivity.java         # Registration screen with API integration
│   ├── ProfileActivity.java          # User profile & settings
│   ├── AboutActivity.java            # Contact form + map
│   ├── AllBrandsActivity.java        # All brands directory
│   ├── FilterBottomSheet.java        # Sort & filter bottom sheet
│   ├── ProductCardBinder.java        # Shared product card view binding
│   ├── CartManager.java              # Singleton cart state
│   ├── FavoriteManager.java          # Singleton favourites state
│   ├── AppDrawerHelper.java          # Side navigation drawer helper
│   └── ScreenUtils.java              # Screen size utilities
├── res/
│   ├── layout/                       # All activity & item layouts (XML)
│   ├── drawable/                     # Icons, backgrounds, shapes
│   ├── xml/
│   │   └── network_security_config.xml  # HTTPS security configuration
│   └── color/                        # Color state lists (nav tint etc.)
└── AndroidManifest.xml
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Meerkat** (2024.3) or later
- Android SDK 36
- JDK 11+
- An Android device or emulator running Android 7.0+ (API 24+)

### Build & Run

```bash
# 1. Clone the repository
git clone https://github.com/your-username/Beckie-Deal.git
cd Beckie-Deal

# 2. Open in Android Studio
#    File → Open → select the project folder

# 3. Let Gradle sync automatically

# 4. Run on device or emulator
#    Run → Run 'app'
```

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
// build.gradle.kts

// Networking & API
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
implementation("com.google.code.gson:gson:2.10.1")

// Security
implementation("androidx.security:security-crypto:1.1.0-alpha06")

// UI & Images
implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
implementation("com.google.android.material:material:1.13.0-alpha03")
implementation("androidx.cardview:cardview:1.0.0")
```

### API Configuration

Backend API URL is configured in `build.gradle.kts`:
```kotlin
buildConfigField("String", "API_BASE_URL", 
    "\"https://laravel-app-114227311375.asia-southeast1.run.app/api/v1/\"")
```

---

## 🎨 Design Highlights

- **Material Design 3** throughout — rounded cards, ripple feedback, proper elevation
- **Floating glass bottom nav** — custom pill background with translucent primary indicator
- **Consistent product card** — shared `ProductCardBinder` ensures every screen renders cards identically
- **Dark mode ready** — toggled from the Profile page, applied system-wide instantly
- **No fragments** — clean, straightforward Activity-based navigation

---

## 📸 Screens at a Glance

| Screen | Description |
|---|---|
| Home | Hero + categories + featured + horizontal scroll sections |
| Categories | Grid of category cards with icon and label |
| Category Detail | Brand chips + filter sheet + 2-column product grid |
| Product Detail | Full product info, add to cart, favourite |
| Search | Live full-text search across entire catalogue |
| Favourites | Saved products list |
| Cart | Items, quantity controls, total, checkout button |
| Payout | Checkout flow |
| Profile | Account actions, dark mode, language, legal |
| About | Contact form + interactive map |

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
