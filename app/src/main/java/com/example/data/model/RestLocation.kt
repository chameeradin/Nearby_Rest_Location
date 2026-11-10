package com.example.data.model

data class RestLocation(
    val id: String,
    val name: String,
    val category: RestCategory,
    val town: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Double,
    val reviewCount: Int,
    val priceLevel: String, // "$", "$$", "$$$", "$$$$"
    val priceAmount: Double, // base price per night / per seat
    val priceUnit: String, // "per night", "per cabana", "per table"
    val phoneNumber: String,
    val email: String,
    val websiteUrl: String,
    val imageUrl: String,
    val photos: List<String>,
    val description: String,
    val amenities: List<String>,
    val isOpenNow: Boolean,
    val openingHours: String,
    val bookingProvider: String = "Booking.com",
    val bookingAvailable: Boolean = true,
    val bookingOptions: List<BookingOption>,
    val reviews: List<UserReview>
)

enum class RestCategory(val displayName: String, val iconEmoji: String) {
    ALL("All", "✨"),
    HOTEL("Hotels", "🏨"),
    RESTAURANT("Restaurants", "🍽️"),
    CABANA("Cabanas", "🛖"),
    CAFE("Cafes", "☕")
}

data class BookingOption(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val capacity: Int,
    val availableCount: Int
)

data class UserReview(
    val id: String,
    val author: String,
    val rating: Double,
    val date: String,
    val comment: String,
    val isVerified: Boolean = true
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val isGoogleUser: Boolean = false,
    val avatarUrl: String = ""
)
