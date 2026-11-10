package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.local.BookingEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class BookingRepository(private val appDao: AppDao) {

    fun getAllBookings(): Flow<List<BookingEntity>> = appDao.getAllBookingsFlow()

    suspend fun createBooking(
        userId: String,
        placeId: String,
        placeName: String,
        placeCategory: String,
        placeAddress: String,
        placeImageUrl: String,
        checkInDate: String,
        checkInTime: String,
        guests: Int,
        optionTitle: String,
        totalPrice: Double,
        paymentMethod: String
    ): BookingEntity {
        val bookingRef = "BKG-" + UUID.randomUUID().toString().take(8).uppercase()
        val booking = BookingEntity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            placeId = placeId,
            placeName = placeName,
            placeCategory = placeCategory,
            placeAddress = placeAddress,
            placeImageUrl = placeImageUrl,
            checkInDate = checkInDate,
            checkInTime = checkInTime,
            guests = guests,
            optionTitle = optionTitle,
            totalPrice = totalPrice,
            paymentMethod = paymentMethod,
            status = "Confirmed",
            bookingReference = bookingRef,
            bookingProvider = "Booking.com"
        )
        appDao.insertBooking(booking)
        return booking
    }

    suspend fun cancelBooking(bookingId: String) {
        appDao.deleteBooking(bookingId)
    }

    suspend fun seedSampleBookingIfEmpty() {
        val sampleDate = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault()).format(Date(System.currentTimeMillis() + 86400000L * 2))
        val sample = BookingEntity(
            id = "seed-bkg-1",
            userId = "user-default-101",
            placeId = "hotel-1",
            placeName = "The Heritage Grand Hotel & Spa",
            placeCategory = "Hotels",
            placeAddress = "42 Church Street, Galle Fort, Southern Province",
            placeImageUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80",
            checkInDate = sampleDate,
            checkInTime = "02:00 PM",
            guests = 2,
            optionTitle = "Colonial Deluxe Room",
            totalPrice = 145.0,
            paymentMethod = "Google Pay",
            status = "Confirmed",
            bookingReference = "BKG-HG88921X",
            bookingProvider = "Booking.com"
        )
        appDao.insertBooking(sample)
    }
}
