package com.example

import com.example.data.model.RestCategory
import com.example.data.repository.LocationsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTestClass {

    @Test
    fun verifyLocationsRepositoryCategories() {
        val all = LocationsRepository.getAllLocations()
        assertTrue(all.isNotEmpty())

        val hotels = all.filter { it.category == RestCategory.HOTEL }
        val restaurants = all.filter { it.category == RestCategory.RESTAURANT }
        val cabanas = all.filter { it.category == RestCategory.CABANA }
        val cafes = all.filter { it.category == RestCategory.CAFE }

        assertTrue(hotels.isNotEmpty())
        assertTrue(restaurants.isNotEmpty())
        assertTrue(cabanas.isNotEmpty())
        assertTrue(cafes.isNotEmpty())
    }

    @Test
    fun verifyDistanceCalculation() {
        // Distance between identical coordinates is 0.0
        val distZero = LocationsRepository.calculateDistanceKm(6.0270, 80.2170, 6.0270, 80.2170)
        assertEquals(0.0, distZero, 0.01)

        // Distance between Galle and Mirissa is ~29 - 33 km
        val distGalleToMirissa = LocationsRepository.calculateDistanceKm(6.0270, 80.2170, 5.9482, 80.4578)
        assertTrue(distGalleToMirissa in 25.0..35.0)
    }

    @Test
    fun verifyPlaceContactAndBookingData() {
        val hotel = LocationsRepository.getLocationById("hotel-1")
        assertNotNull(hotel)
        assertTrue(hotel!!.phoneNumber.isNotBlank())
        assertTrue(hotel.email.isNotBlank())
        assertTrue(hotel.address.isNotBlank())
        assertTrue(hotel.bookingOptions.isNotEmpty())
        assertTrue(hotel.reviews.isNotEmpty())
        assertEquals("Booking.com", hotel.bookingProvider)
    }

    @Test
    fun verifyDynamicPlacesForCoordinates() {
        val nearest = LocationsRepository.getNearestPlacesForCoordinates(6.0270, 80.2170, "Galle")
        assertTrue(nearest.isNotEmpty())
        // Should contain both local generated places and curated places
        assertTrue(nearest.any { it.town.equals("Galle", ignoreCase = true) })
        assertTrue(nearest.any { it.category == RestCategory.CABANA })
        assertTrue(nearest.any { it.category == RestCategory.HOTEL })
    }
}
