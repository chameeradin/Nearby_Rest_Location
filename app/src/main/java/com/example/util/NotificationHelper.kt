package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_BOOKINGS = "nearby_rest_bookings_channel"
        const val CHANNEL_PROMOS = "nearby_rest_promos_channel"
        private var notificationIdCounter = 1000
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bookingChannel = NotificationChannel(
                CHANNEL_BOOKINGS,
                "Upcoming Bookings & Appointments",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time updates and reminders for your upcoming stays, cabanas, and dining reservations"
                enableVibration(true)
            }

            val promoChannel = NotificationChannel(
                CHANNEL_PROMOS,
                "Special Promotional Offers",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Exclusive discounts, weekend flash sales, and seasonal travel promotions"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(bookingChannel)
            manager.createNotificationChannel(promoChannel)
        }
    }

    fun canSendNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    fun sendBookingConfirmedNotification(bookingRef: String, placeName: String, date: String, time: String) {
        if (!canSendNotifications()) return

        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_BOOKINGS)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Booking Confirmed: $placeName")
                .setContentText("Ref #$bookingRef for $date at $time. Ready for your rest stay!")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Your reservation at $placeName is confirmed via Booking.com.\nReference: $bookingRef\nDate & Time: $date at $time.\nEnjoy your time with Waves IT hospitality services!")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(notificationIdCounter++, notification)
        } catch (_: SecurityException) {
            // Handled if permission revoked
        }
    }

    fun sendUpcomingAppointmentReminder(placeName: String, scheduledTime: String) {
        if (!canSendNotifications()) return

        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_BOOKINGS)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Upcoming Appointment Reminder 🔔")
                .setContentText("Your reservation at $placeName starts in $scheduledTime!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(notificationIdCounter++, notification)
        } catch (_: SecurityException) {
        }
    }

    fun sendPromotionalOfferNotification(title: String, code: String) {
        if (!canSendNotifications()) return

        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_PROMOS)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("🎁 Special Travel Offer: $title")
                .setContentText("Use voucher code $code for up to 30% off selected hotels & cabanas today!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(context).notify(notificationIdCounter++, notification)
        } catch (_: SecurityException) {
        }
    }
}
