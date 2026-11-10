package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val placeId: String,
    val placeName: String,
    val placeCategory: String,
    val placeAddress: String,
    val placeImageUrl: String,
    val checkInDate: String,
    val checkInTime: String,
    val guests: Int,
    val optionTitle: String,
    val totalPrice: Double,
    val paymentMethod: String,
    val status: String = "Confirmed",
    val bookingReference: String,
    val bookingProvider: String = "Booking.com",
    val createdAt: Long = System.currentTimeMillis()
)
