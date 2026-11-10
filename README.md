# Nearby Rest Locations 🏨 🔍

**Nearby Rest Locations** is a modern, high-performance native Android application engineered by **Waves IT**. It enables travelers and locals to discover, track, and reserve real-time hotels, oceanfront cabanas, restaurants, and artisanal cafes in their nearest town or anywhere in the world using continuous GPS tracking, Google Maps Platform integrations, and live spatial data.

---

## 🌟 Key Features

### 1. 🔍 Explore & Discover
- **Category Filter Chips:** Instant single-tap filtering across:
  - 🏨 **Hotels:** Luxury resorts, boutique hotels, colonial villas, and guest suites.
  - 🛖 **Cabanas:** Eco-beach shacks, oceanfront chalets, and tropical treehouse lodges.
  - 🍽️ **Restaurants:** Fine dining, authentic local seafood grills, and spice gardens.
  - ☕ **Cafes:** Specialty coffee roasteries, co-working bakeries, and high tea lounges.
- **Real-Time GPS Location Tracking:** High-accuracy location telemetry using Google Play Services `FusedLocationProviderClient` with automatic fallback to system GPS/Network providers.
- **Global Manual Town Geocoding:** Search any town or city worldwide (e.g., *Galle, Colombo, Kandy, Mirissa, Ella, London, Paris, Tokyo, Dubai*) with real-time coordinate geocoding.
- **Geodesic Distance:** Accurate live distance calculations (in km) calculated via the Haversine formula from current coordinates to every establishment.

### 2. 📡 360° Radar Scanner View
- **Rotating Radar Scope:** Live circular radar display featuring an animated sweep line and luminous trail.
- **Concentric Distance Rings:** Calibrated range rings (`1.0 km`, `2.5 km`, `5.0 km`, and customizable ranges up to `25 km`) with distance markers and cardinal directions (N, S, E, W).
- **Interactive Pin Targeting:** Category-color-coded pins plotted at their exact bearing and distance relative to your position. Tapping any pin locks a targeting reticle and reveals a preview card.
- **Nearest Detected Feed:** Horizontally scrollable live feed ordered strictly by distance with direct booking and turn-by-turn navigation actions.

### 3. 🗺️ 100% Real Places & Google Maps Integration
- **Google Places API (New & Legacy):** Integrated via Retrofit & Moshi to retrieve genuine business names, physical addresses, opening hours, pricing tiers, Google Maps ratings, and verified reviews.
- **Live Overpass Spatial Engine:** Zero-key fallback querying live OpenStreetMap spatial nodes and ways (`around:7000,lat,lon`) for registered hotels, chalets, restaurants, and cafes with real street addresses and phone numbers.
- **Live Google Maps Actions:**
  - One-tap *"Google Maps Data"* button opening live Google Maps listings around your current GPS coordinates.
  - Direct *"Map"* button on each place card launching Google Maps turn-by-turn directions.

### 4. 📞 Full Contact & Business Profile
- **One-Tap Actions:**
  - 📞 **Call:** Launches native phone dialer with business number.
  - ✉️ **Email:** Launches email client with pre-filled subject.
  - 📍 **Directions:** Opens exact place coordinates in Google Maps or default mapping app.
  - 🔗 **Share:** Native Android share sheet with place link, name, and address.
- **Operating Hours:** Real-time *"Open Now"* indicator and daily schedule.
- **Amenities Showcase:** Visual badges for High-Speed Wi-Fi, Beachfront, Infinity Pool, Breakfast, Parking, Air Conditioning, and Pet Friendly.
- **Guest Reviews:** Detailed star rating breakdown and verified guest testimonials.

### 5. 💳 In-App Booking Engine (Booking.com Partner Style)
- **Room, Cabana & Table Selection:** Browse specific options (e.g., *Deluxe Ocean Suite, Waterfront Eco-Chalet, Courtyard Dining Table*) with live pricing and capacity limits.
- **Date & Time Picker:** Reservation date selector and arrival/dinner time slot selector.
- **Guest Counter:** Multi-guest adjustment with capacity limits.
- **Transparent Pricing:** Itemized breakdown including 10% taxes and service fees.
- **Payment Method Selection:** Supports Google Pay, Credit/Debit Cards, or Pay at Property on arrival.
- **Digital Booking Pass:** Generates a confirmed booking pass with unique reference code (`BKG-XXXXXX`) and digital QR code.

### 6. 🔐 User Authentication & Local Room SQLite Database
- **Room Database Architecture:** Encrypted local SQLite persistence for:
  - `UserEntity`: User profile, credentials, and Google account details.
  - `BookingEntity`: Active and completed reservations.
  - `SearchHistoryEntity`: Recent search queries with fast re-search.
- **Sign In & Sign Up UI:** Email/password authentication with field validation.
- **Google One-Tap Sign-In:** Instant authentication using Google account credentials.
- **My Bookings Screen:** Manage past and upcoming reservations with cancellation support.

### 7. 🔔 Push Notifications
- **Notification Channels:** Dedicated Android notification channels for *Upcoming Bookings & Appointments* (High Importance) and *Promotional Offers* (Default Importance).
- **Live Triggers:** Automatic push notification dispatched to system status bar upon booking confirmation with reference details.
- **Test Notifications:** In-app triggers in Settings to preview appointment reminders and promotional discount vouchers live on device.

### 8. 👨‍💻 Developer Showcase: Waves IT
- **Dedicated Developer Hub:** Highlighted within the Application Settings celebrating **Waves IT** as the lead digital software and travel technology engineering team.
- Official developer verification badge, mission statement, contact email (`contact@wavesit.lk`), and official website (`wavesit.lk`).

### 9. 📱 Responsive & Adaptive Design
- Built entirely with **Jetpack Compose** following **Material 3** guidelines.
- Seamless layout adaptation between smartphones (Bottom Navigation Bar, single-column feed) and tablets/foldables (Navigation Rail, dual-column adaptive grid).

---

## 🛠️ Technology Stack & Architecture

- **Language:** Kotlin (100%)
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Architecture with Kotlin Coroutines & `StateFlow`
- **Local Persistence:** Room Database (SQLite) with KSP (Kotlin Symbol Processing)
- **Networking:** Retrofit 2, OkHttp 3, Moshi (JSON serialization with code generation)
- **Image Loading:** Coil Compose (Asynchronous image loading and disk caching)
- **Location Services:** Google Play Services Location (`FusedLocationProviderClient`) + Android `LocationManager` fallback
- **Reverse Geocoding:** Android `Geocoder` (synchronous & modern asynchronous APIs)
- **Notifications:** Android `NotificationManagerCompat` & `NotificationChannel` (API 26–36)
- **Secrets Management:** Google Maps Platform Secrets Gradle Plugin (`.env` / `.env.example`)

---

## 📁 Project Structure

```
NearbyRestLocations/
├── app/
│   ├── build.gradle.kts                # App-level dependencies & signing configs
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml     # Permissions (GPS, Network, Notifications)
│   │   │   ├── java/com/example/
│   │   │   │   ├── MainActivity.kt     # App entry point with edge-to-edge
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/            # Retrofit interfaces & Moshi models
│   │   │   │   │   │   ├── GooglePlacesModels.kt
│   │   │   │   │   │   └── PlacesApiService.kt
│   │   │   │   │   ├── local/          # Room Database, DAO, & Entities
│   │   │   │   │   │   ├── AppDao.kt
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── BookingEntity.kt
│   │   │   │   │   │   ├── SearchHistoryEntity.kt
│   │   │   │   │   │   └── UserEntity.kt
│   │   │   │   │   ├── model/          # Domain models (RestLocation, Categories)
│   │   │   │   │   │   └── RestLocation.kt
│   │   │   │   │   └── repository/     # Data repositories
│   │   │   │   │       ├── AuthRepository.kt
│   │   │   │   │       ├── BookingRepository.kt
│   │   │   │   │       ├── LocationsRepository.kt
│   │   │   │   │       ├── RealPlacesRepository.kt
│   │   │   │   │       └── SearchHistoryRepository.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainApp.kt      # Navigation tabs coordinator & responsive layout
│   │   │   │   │   ├── components/     # Reusable M3 badges, cards & contact bars
│   │   │   │   │   │   └── CommonComponents.kt
│   │   │   │   │   ├── screens/        # Compose UI screens
│   │   │   │   │   │   ├── AuthScreen.kt
│   │   │   │   │   │   ├── BookingDialog.kt
│   │   │   │   │   │   ├── BookingsHistoryScreen.kt
│   │   │   │   │   │   ├── DetailScreen.kt
│   │   │   │   │   │   ├── ExploreScreen.kt
│   │   │   │   │   │   ├── RadarScreen.kt
│   │   │   │   │   │   └── SettingsScreen.kt
│   │   │   │   │   └── theme/          # Hospitality Color, Typography & Theme
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   └── util/               # Location tracker & Notification helper
│   │   │   │       ├── LocationTracker.kt
│   │   │   │       └── NotificationHelper.kt
│   │   │   └── res/                    # Drawables, mipmaps, strings, colors
│   │   └── test/                       # Local JVM Unit Tests
│   │       └── java/com/example/
│   │           ├── ExampleRobolectricTest.kt
│   │           └── ExampleUnitTest.kt
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml              # Centralized dependency catalog
├── .env.example                        # API keys template
├── metadata.json                       # AI Studio project configuration
├── settings.gradle.kts
└── README.md                           # Documentation
```

---

## ⚙️ Configuration & API Keys

### Google Places API Key (Optional)
The application works immediately out-of-the-box using continuous GPS location tracking and the live Overpass OpenStreetMap Spatial Engine.

To enable direct Google Maps Places API queries:
1. Obtain an API key from the [Google Cloud Console](https://console.cloud.google.com/) with the **Places API** enabled.
2. In **Google AI Studio**, open the **Secrets panel** and add:
   ```env
   GOOGLE_PLACES_API_KEY=your_google_places_api_key_here
   ```
3. Alternatively, you can paste and save your key directly inside the app:
   - Go to **Settings** → **Google Maps Places API Engine** → Paste your key → Tap **Save & Apply Key**.

---

## 🚀 Building & Running

### Requirements
- Android SDK version 24 (minSdk) to 36 (targetSdk / compileSdk)
- JDK 11 or higher
- Gradle 8+ with Kotlin 2.2+

### Terminal Commands
To assemble and verify the debug build:
```bash
gradle assembleDebug
```

To run local unit and repository tests:
```bash
gradle :app:testDebugUnitTest
```

---

## 📄 License & Attribution

Designed and developed by **Waves IT**.  
All rights reserved © 2026 Waves IT.
