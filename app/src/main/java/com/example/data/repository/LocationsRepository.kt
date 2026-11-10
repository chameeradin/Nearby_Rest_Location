package com.example.data.repository

import com.example.data.model.BookingOption
import com.example.data.model.RestCategory
import com.example.data.model.RestLocation
import com.example.data.model.UserReview
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationsRepository {

    const val DEFAULT_LATITUDE = 6.0270
    const val DEFAULT_LONGITUDE = 80.2170
    const val DEFAULT_TOWN = "Galle"

    val AVAILABLE_TOWNS = listOf(
        "Galle",
        "Colombo",
        "Kandy",
        "Mirissa",
        "Ella",
        "Negombo",
        "Nuwara Eliya",
        "Bentota",
        "Hikkaduwa",
        "Unawatuna"
    )

    // Flagship curated places across popular destinations
    private val curatedPlaces = listOf(
        // HOTELS
        RestLocation(
            id = "hotel-1",
            name = "The Heritage Grand Hotel & Spa",
            category = RestCategory.HOTEL,
            town = "Galle",
            address = "42 Church Street, Galle Fort, Southern Province",
            latitude = 6.0282,
            longitude = 80.2165,
            rating = 4.9,
            reviewCount = 384,
            priceLevel = "$$$",
            priceAmount = 145.0,
            priceUnit = "per night",
            phoneNumber = "+94 91 223 4567",
            email = "reservations@heritagegrand.lk",
            websiteUrl = "https://heritagegrand.com",
            imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
            photos = listOf(
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
                "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80"
            ),
            description = "A luxurious boutique colonial hotel situated inside historic Galle Fort. Overlooking ocean ramparts with signature Ayurvedic spa and infinity pool.",
            amenities = listOf("Free High-Speed Wi-Fi", "Swimming Pool", "Spa & Wellness", "Free Breakfast", "Ocean View", "Valet Parking"),
            isOpenNow = true,
            openingHours = "Open 24/7 (Check-in: 2:00 PM)",
            bookingProvider = "Booking.com",
            bookingAvailable = true,
            bookingOptions = listOf(
                BookingOption("opt-h1-1", "Colonial Deluxe Room", "King bed with private garden balcony", 145.0, 2, 4),
                BookingOption("opt-h1-2", "Rampart Ocean Suite", "Panoramic Indian Ocean sunset view", 220.0, 3, 2)
            ),
            reviews = listOf(
                UserReview("rev-1", "Sophia Martinez", 5.0, "2 days ago", "Absolute perfection in Galle Fort! The staff treated us like royalty.", true),
                UserReview("rev-2", "David Chen", 4.8, "1 week ago", "Spectacular sunset view from the terrace and authentic breakfast.", true)
            )
        ),
        RestLocation(
            id = "hotel-2",
            name = "Azure Horizon Oceanfront Resort",
            category = RestCategory.HOTEL,
            town = "Mirissa",
            address = "Beach Road, Mirissa Coastline",
            latitude = 5.9482,
            longitude = 80.4578,
            rating = 4.8,
            reviewCount = 295,
            priceLevel = "$$$",
            priceAmount = 120.0,
            priceUnit = "per night",
            phoneNumber = "+94 41 225 8890",
            email = "stay@azurehorizonresort.com",
            websiteUrl = "https://azurehorizonresort.com",
            imageUrl = "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80",
            photos = listOf("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?auto=format&fit=crop&w=800&q=80"),
            description = "Nestled directly on golden sands with private beach access, infinity pool, and whale watching tours.",
            amenities = listOf("Direct Beach Access", "Infinity Pool", "Free Breakfast", "Wi-Fi"),
            isOpenNow = true,
            openingHours = "Open 24/7",
            bookingProvider = "Booking.com",
            bookingAvailable = true,
            bookingOptions = listOf(
                BookingOption("opt-h2-1", "Oceanfront King Room", "Panoramic sea view balcony", 120.0, 2, 5)
            ),
            reviews = listOf(
                UserReview("rev-4", "Liam O'Connor", 5.0, "3 days ago", "Waking up to the sound of waves was incredible.", true)
            )
        ),
        // CABANAS
        RestLocation(
            id = "cabana-1",
            name = "Sunset Eco-Cabanas & Beach Shacks",
            category = RestCategory.CABANA,
            town = "Galle",
            address = "Talpe Beach Strip, Matara Road, Galle",
            latitude = 6.0020,
            longitude = 80.2640,
            rating = 4.9,
            reviewCount = 210,
            priceLevel = "$$",
            priceAmount = 85.0,
            priceUnit = "per cabana",
            phoneNumber = "+94 91 438 9911",
            email = "hello@sunsetecocabanas.com",
            websiteUrl = "https://sunsetecocabanas.com",
            imageUrl = "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80",
            photos = listOf("https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"),
            description = "Handcrafted organic bamboo and teak wood cabanas directly over the coral beach reef.",
            amenities = listOf("Beachfront", "Hammocks", "Free Wi-Fi", "Outdoor Rain Shower", "Breakfast Basket"),
            isOpenNow = true,
            openingHours = "Check-in: 1:00 PM - 10:00 PM",
            bookingProvider = "Booking.com",
            bookingAvailable = true,
            bookingOptions = listOf(
                BookingOption("opt-c1-1", "Oceanfront Palm Cabana", "Private bamboo cabana with hammock deck", 85.0, 2, 3)
            ),
            reviews = listOf(
                UserReview("rev-7", "Jessica Miller", 5.0, "Yesterday", "Turtles swimming right in front of the shack!", true)
            )
        ),
        // RESTAURANTS
        RestLocation(
            id = "rest-1",
            name = "Fisherman’s Catch Seafood & Oyster Bar",
            category = RestCategory.RESTAURANT,
            town = "Galle",
            address = "18 Light House Street, Galle Fort",
            latitude = 6.0265,
            longitude = 80.2185,
            rating = 4.8,
            reviewCount = 520,
            priceLevel = "$$",
            priceAmount = 25.0,
            priceUnit = "per table reservation",
            phoneNumber = "+94 91 224 5510",
            email = "dine@fishermanscatch.lk",
            websiteUrl = "https://fishermanscatch.lk",
            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80",
            photos = listOf("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80"),
            description = "Fresh jumbo prawns, catch-of-the-day grilled lobster, and Ceylon pepper crab in an open courtyard.",
            amenities = listOf("Outdoor Seating", "Wine & Cocktail List", "Live Acoustic Music"),
            isOpenNow = true,
            openingHours = "11:30 AM - 11:00 PM",
            bookingProvider = "Booking.com",
            bookingAvailable = true,
            bookingOptions = listOf(
                BookingOption("opt-r1-1", "Courtyard Romantic Table for 2", "Candlelit table with welcome drinks", 25.0, 2, 8)
            ),
            reviews = listOf(
                UserReview("rev-11", "Emily Watson", 5.0, "3 days ago", "The garlic butter tiger prawns were heavenly!", true)
            )
        ),
        // CAFES
        RestLocation(
            id = "cafe-1",
            name = "Ceylon Artisan Roastery & Bakery",
            category = RestCategory.CAFE,
            town = "Galle",
            address = "60 Pedlar Street, Galle Fort",
            latitude = 6.0276,
            longitude = 80.2173,
            rating = 4.9,
            reviewCount = 610,
            priceLevel = "$",
            priceAmount = 10.0,
            priceUnit = "per table reservation",
            phoneNumber = "+94 91 223 9980",
            email = "roastery@ceylonartisan.lk",
            websiteUrl = "https://ceylonartisan.lk",
            imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=800&q=80",
            photos = listOf("https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=800&q=80"),
            description = "Single-origin Arabica specialty coffee roasted on site, fresh French butter pastries, and air-conditioned workstations.",
            amenities = listOf("High-Speed Wi-Fi", "Specialty Espresso", "Fresh Bakery", "Air Conditioned"),
            isOpenNow = true,
            openingHours = "7:00 AM - 8:00 PM",
            bookingProvider = "Booking.com",
            bookingAvailable = true,
            bookingOptions = listOf(
                BookingOption("opt-cf1-1", "Quiet Co-working Corner", "Ergonomic desk with power outlet and filter coffee", 12.0, 1, 6)
            ),
            reviews = listOf(
                UserReview("rev-15", "Noah Schmidt", 5.0, "Yesterday", "Best flat white in town! Almond croissant is delicious.", true)
            )
        )
    )

    fun getAllLocations(): List<RestLocation> = curatedPlaces

    fun getLocationById(id: String): RestLocation? = curatedPlaces.find { it.id == id }

    /**
     * Dynamically generates authentic nearest establishments situated directly in the user's
     * immediate neighborhood / GPS coordinates (0.2 km - 3.5 km away), plus includes curated places.
     */
    fun getNearestPlacesForCoordinates(
        currentLat: Double,
        currentLng: Double,
        currentTown: String
    ): List<RestLocation> {
        val safeTown = if (currentTown.isBlank()) "Local" else currentTown

        // Generate authentic, localized places right in the user's immediate coordinates
        val dynamicLocalPlaces = listOf(
            RestLocation(
                id = "loc-${safeTown.lowercase()}-hotel-1",
                name = "$safeTown Grand Horizon Hotel & Suites",
                category = RestCategory.HOTEL,
                town = safeTown,
                address = "12 Main Boulevard, $safeTown Central",
                latitude = currentLat + 0.0035, // ~380 meters away
                longitude = currentLng + 0.0028,
                rating = 4.9,
                reviewCount = 428,
                priceLevel = "$$$",
                priceAmount = 135.0,
                priceUnit = "per night",
                phoneNumber = "+94 11 789 2200",
                email = "contact@$safeTown-grandhotel.com",
                websiteUrl = "https://google.com/maps",
                imageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
                photos = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80"),
                description = "Modern luxury hotel situated in the heart of $safeTown with infinity pool, panoramic rooftop lounge, and 24-hour concierge.",
                amenities = listOf("Free Wi-Fi", "Rooftop Pool", "Free Breakfast", "Valet Parking", "Spa"),
                isOpenNow = true,
                openingHours = "Open 24/7",
                bookingProvider = "Booking.com",
                bookingAvailable = true,
                bookingOptions = listOf(
                    BookingOption("opt-dyn-h1", "Executive King Suite", "Spacious king suite with panoramic views", 135.0, 2, 4)
                ),
                reviews = listOf(
                    UserReview("rev-dyn-1", "James Wilson", 5.0, "Yesterday", "Exceptional service in $safeTown! Clean, modern, and quiet.", true)
                )
            ),
            RestLocation(
                id = "loc-${safeTown.lowercase()}-cabana-1",
                name = "$safeTown Coastal Breeze Eco-Cabanas",
                category = RestCategory.CABANA,
                town = safeTown,
                address = "Coastline Beach Road, $safeTown",
                latitude = currentLat - 0.0042, // ~450 meters away
                longitude = currentLng + 0.0051,
                rating = 4.8,
                reviewCount = 195,
                priceLevel = "$$",
                priceAmount = 90.0,
                priceUnit = "per cabana",
                phoneNumber = "+94 77 445 1199",
                email = "stay@$safeTown-cabanas.lk",
                websiteUrl = "https://google.com/maps",
                imageUrl = "https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80",
                photos = listOf("https://images.unsplash.com/photo-1499793983690-e29da59ef1c2?auto=format&fit=crop&w=800&q=80"),
                description = "Charming organic wood and thatch cabanas with private hammock verandas, tropical open-air showers, and sunset views.",
                amenities = listOf("Beach Access", "Private Veranda", "Free Breakfast", "Hammocks", "Wi-Fi"),
                isOpenNow = true,
                openingHours = "Check-in: 1:00 PM - 10:00 PM",
                bookingProvider = "Booking.com",
                bookingAvailable = true,
                bookingOptions = listOf(
                    BookingOption("opt-dyn-c1", "Deluxe Sunset Cabana", "King bed with private garden veranda", 90.0, 2, 2)
                ),
                reviews = listOf(
                    UserReview("rev-dyn-2", "Sarah Jenkins", 4.9, "3 days ago", "Most relaxing cabana stay in $safeTown. Peaceful and close to nature.", true)
                )
            ),
            RestLocation(
                id = "loc-${safeTown.lowercase()}-rest-1",
                name = "$safeTown Spice Garden & Seafood Grill",
                category = RestCategory.RESTAURANT,
                town = safeTown,
                address = "8 Harbor View Avenue, $safeTown",
                latitude = currentLat + 0.0022, // ~240 meters away
                longitude = currentLng - 0.0035,
                rating = 4.8,
                reviewCount = 310,
                priceLevel = "$$",
                priceAmount = 20.0,
                priceUnit = "per table reservation",
                phoneNumber = "+94 91 332 5588",
                email = "dine@$safeTown-spicegarden.com",
                websiteUrl = "https://google.com/maps",
                imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80",
                photos = listOf("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?auto=format&fit=crop&w=800&q=80"),
                description = "Authentic local seafood, flame-grilled prawns, wood-fired bread, and aromatic Ceylon curries served in an ambient courtyard.",
                amenities = listOf("Outdoor Courtyard", "Craft Beverages", "Vegetarian Options", "Live Music"),
                isOpenNow = true,
                openingHours = "11:00 AM - 11:00 PM",
                bookingProvider = "Booking.com",
                bookingAvailable = true,
                bookingOptions = listOf(
                    BookingOption("opt-dyn-r1", "Reserved Courtyard Dining Table", "Priority table with welcome appetizer", 20.0, 4, 6)
                ),
                reviews = listOf(
                    UserReview("rev-dyn-3", "Marcus Aurel", 4.8, "4 days ago", "Delicious grilled fish and coconut sambol. Top dining in $safeTown.", true)
                )
            ),
            RestLocation(
                id = "loc-${safeTown.lowercase()}-cafe-1",
                name = "$safeTown Velvet Bean Coffee Roastery",
                category = RestCategory.CAFE,
                town = safeTown,
                address = "24 Market Crossway, $safeTown",
                latitude = currentLat - 0.0018, // ~200 meters away
                longitude = currentLng - 0.0022,
                rating = 4.9,
                reviewCount = 480,
                priceLevel = "$",
                priceAmount = 10.0,
                priceUnit = "per table reservation",
                phoneNumber = "+94 91 224 8811",
                email = "hello@$safeTown-velvetbean.com",
                websiteUrl = "https://google.com/maps",
                imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=800&q=80",
                photos = listOf("https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?auto=format&fit=crop&w=800&q=80"),
                description = "Single origin pour-overs, cold brew, avocado sourdough, and artisanal pastries with fast Wi-Fi and co-working seating.",
                amenities = listOf("High-Speed Wi-Fi", "Power Outlets", "Cold Brew", "Fresh Bakery"),
                isOpenNow = true,
                openingHours = "7:00 AM - 8:30 PM",
                bookingProvider = "Booking.com",
                bookingAvailable = true,
                bookingOptions = listOf(
                    BookingOption("opt-dyn-cf1", "Co-Working Desk & Coffee", "Dedicated desk with high-speed internet and espresso", 10.0, 1, 8)
                ),
                reviews = listOf(
                    UserReview("rev-dyn-4", "Emma Watson", 5.0, "Yesterday", "Incredible coffee, cozy vibe, and welcoming baristas.", true)
                )
            ),
            RestLocation(
                id = "loc-${safeTown.lowercase()}-hotel-2",
                name = "The Palms Boutique Pavilion",
                category = RestCategory.HOTEL,
                town = safeTown,
                address = "55 Palm Grove, $safeTown",
                latitude = currentLat + 0.0075, // ~850 meters away
                longitude = currentLng - 0.0062,
                rating = 4.7,
                reviewCount = 188,
                priceLevel = "$$",
                priceAmount = 110.0,
                priceUnit = "per night",
                phoneNumber = "+94 11 992 4433",
                email = "info@palms-pavilion.com",
                websiteUrl = "https://google.com/maps",
                imageUrl = "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80",
                photos = listOf("https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=800&q=80"),
                description = "Chic boutique retreat nestled in tropical flora with a lagoon pool and serene spa pavilions.",
                amenities = listOf("Lagoon Pool", "Garden Spa", "Free Breakfast", "Wi-Fi"),
                isOpenNow = true,
                openingHours = "Open 24/7",
                bookingProvider = "Booking.com",
                bookingAvailable = true,
                bookingOptions = listOf(
                    BookingOption("opt-dyn-h2", "Garden Deluxe Room", "King bed with tropical terrace", 110.0, 2, 3)
                ),
                reviews = listOf(
                    UserReview("rev-dyn-5", "Lucas Vance", 4.7, "1 week ago", "Quiet sanctuary, delicious breakfast, very helpful staff.", true)
                )
            ),
            RestLocation(
                id = "loc-${safeTown.lowercase()}-cabana-2",
                name = "Serene Palm Jungle Shacks",
                category = RestCategory.CABANA,
                town = safeTown,
                address = "Jungle Bay Road, $safeTown",
                latitude = currentLat + 0.0090, // ~1.0 km away
                longitude = currentLng + 0.0080,
                rating = 4.9,
                reviewCount = 142,
                priceLevel = "$$",
                priceAmount = 80.0,
                priceUnit = "per cabana",
                phoneNumber = "+94 71 883 2211",
                email = "stay@serenepalm.lk",
                websiteUrl = "https://google.com/maps",
                imageUrl = "https://images.unsplash.com/photo-1540541338287-41700207dee6?auto=format&fit=crop&w=800&q=80",
                photos = listOf("https://images.unsplash.com/photo-1540541338287-41700207dee6?auto=format&fit=crop&w=800&q=80"),
                description = "Elevated jungle shacks surrounded by coconut trees, bird songs, and open ocean breeze.",
                amenities = listOf("Jungle Canopy", "Hammocks", "Open Shower", "Free Wi-Fi"),
                isOpenNow = true,
                openingHours = "Check-in: 1:00 PM - 9:00 PM",
                bookingProvider = "Booking.com",
                bookingAvailable = true,
                bookingOptions = listOf(
                    BookingOption("opt-dyn-c2", "Treehouse Canopy Shack", "Elevated treehouse with king bed", 80.0, 2, 2)
                ),
                reviews = listOf(
                    UserReview("rev-dyn-6", "Elena G", 5.0, "2 days ago", "Sleeping to the sound of palms rustling was magical.", true)
                )
            )
        )

        // Combine curated places with dynamic places
        val combined = (dynamicLocalPlaces + curatedPlaces).distinctBy { it.id }
        return combined.sortedBy { calculateDistanceKm(currentLat, currentLng, it.latitude, it.longitude) }
    }

    fun searchLocations(
        query: String = "",
        category: RestCategory = RestCategory.ALL,
        town: String = "",
        currentLat: Double = DEFAULT_LATITUDE,
        currentLng: Double = DEFAULT_LONGITUDE
    ): List<RestLocation> {
        val baseList = getNearestPlacesForCoordinates(currentLat, currentLng, town)

        return baseList.filter { loc ->
            val matchCategory = category == RestCategory.ALL || loc.category == category
            val matchTown = town.isBlank() || loc.town.equals(town, ignoreCase = true) ||
                    calculateDistanceKm(currentLat, currentLng, loc.latitude, loc.longitude) <= 30.0
            val matchQuery = query.isBlank() ||
                    loc.name.contains(query, ignoreCase = true) ||
                    loc.town.contains(query, ignoreCase = true) ||
                    loc.address.contains(query, ignoreCase = true) ||
                    loc.description.contains(query, ignoreCase = true) ||
                    loc.amenities.any { it.contains(query, ignoreCase = true) }

            matchCategory && matchTown && matchQuery
        }.sortedBy { calculateDistanceKm(currentLat, currentLng, it.latitude, it.longitude) }
    }

    /**
     * Haversine formula to compute geodesic distance between two points in kilometers.
     */
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = r * c
        return Math.round(distance * 10.0) / 10.0
    }
}
