package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.repository.LocationsRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class UserLocationState(
    val latitude: Double = LocationsRepository.DEFAULT_LATITUDE,
    val longitude: Double = LocationsRepository.DEFAULT_LONGITUDE,
    val townName: String = LocationsRepository.DEFAULT_TOWN,
    val streetAddress: String = "Near Town Center",
    val isRealGps: Boolean = false,
    val isLiveTracking: Boolean = true,
    val manualOverride: Boolean = false,
    val accuracyMeters: Float = 12f
)

class LocationTracker(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val _locationState = MutableStateFlow(UserLocationState())
    val locationState: StateFlow<UserLocationState> = _locationState.asStateFlow()

    private var locationCallback: LocationCallback? = null
    private var nativeLocationListener: LocationListener? = null

    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineGranted || coarseGranted
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdate() {
        if (!hasLocationPermission()) return

        // 1. Try immediate last known location from FusedLocationClient
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    processNewLocation(loc)
                }
            }
        } catch (_: Exception) {}

        // 2. Fallback check from native LocationManager
        try {
            val gpsLoc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val netLoc = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val bestLoc = gpsLoc ?: netLoc
            if (bestLoc != null && !_locationState.value.isRealGps) {
                processNewLocation(bestLoc)
            }
        } catch (_: Exception) {}

        // 3. Register continuous high-accuracy location tracking
        startContinuousTracking()
    }

    @SuppressLint("MissingPermission")
    private fun startContinuousTracking() {
        if (!hasLocationPermission()) return

        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 4000L)
                .setMinUpdateIntervalMillis(2000L)
                .setMinUpdateDistanceMeters(2f)
                .build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val lastLoc = result.lastLocation ?: return
                    processNewLocation(lastLoc)
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (_: Exception) {
            // Native fallback
            try {
                nativeLocationListener = object : LocationListener {
                    override fun onLocationChanged(loc: Location) {
                        processNewLocation(loc)
                    }
                    @Deprecated("Deprecated in Java")
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                }
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    4000L,
                    2f,
                    nativeLocationListener!!,
                    Looper.getMainLooper()
                )
            } catch (_: Exception) {}
        }
    }

    private fun processNewLocation(loc: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            val (resolvedTown, street) = resolveFullAddress(loc.latitude, loc.longitude)
            _locationState.value = _locationState.value.copy(
                latitude = loc.latitude,
                longitude = loc.longitude,
                townName = resolvedTown,
                streetAddress = street,
                isRealGps = true,
                manualOverride = false,
                accuracyMeters = loc.accuracy
            )
        }
    }

    fun setManualLocation(townQuery: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val queryClean = townQuery.trim()
            val geocoded = geocodeTownNameToCoordinates(queryClean)
            val (lat, lng) = if (geocoded != null) {
                geocoded.first to geocoded.second
            } else {
                when (queryClean.lowercase()) {
                    "galle" -> 6.0270 to 80.2170
                    "colombo" -> 6.9271 to 79.8612
                    "kandy" -> 7.2906 to 80.6337
                    "mirissa" -> 5.9482 to 80.4578
                    "ella" -> 6.8667 to 81.0465
                    "negombo" -> 7.2008 to 79.8736
                    "nuwara eliya" -> 6.9497 to 80.7891
                    "matara" -> 5.9549 to 80.5550
                    "bentota" -> 6.4257 to 79.9961
                    "hikkaduwa" -> 6.1395 to 80.1063
                    "unawatuna" -> 6.0105 to 80.2483
                    "new york" -> 40.7128 to -74.0060
                    "london" -> 51.5074 to -0.1278
                    "paris" -> 48.8566 to 2.3522
                    "tokyo" -> 35.6762 to 139.6503
                    "dubai" -> 25.2048 to 55.2708
                    else -> 6.0270 to 80.2170
                }
            }

            _locationState.value = _locationState.value.copy(
                latitude = lat,
                longitude = lng,
                townName = queryClean.replaceFirstChar { it.uppercase() },
                streetAddress = "Selected Town Area",
                isRealGps = false,
                manualOverride = true,
                accuracyMeters = 5f
            )
        }
    }

    private fun geocodeTownNameToCoordinates(townName: String): Pair<Double, Double>? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val list = geocoder.getFromLocationName(townName, 1)
            if (!list.isNullOrEmpty()) {
                val addr = list[0]
                addr.latitude to addr.longitude
            } else null
        } catch (_: Exception) {
            null
        }
    }

    private fun resolveFullAddress(lat: Double, lng: Double): Pair<String, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                val town = addr.locality
                    ?: addr.subAdminArea
                    ?: addr.adminArea
                    ?: addr.featureName
                    ?: "Nearby Town"
                val street = addr.thoroughfare
                    ?: addr.subLocality
                    ?: addr.getAddressLine(0)
                    ?: "Local area"
                town to street
            } else {
                "Nearby Town" to "Current GPS Location"
            }
        } catch (_: Exception) {
            "Galle" to "Coastal Hub"
        }
    }
}
