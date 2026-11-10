package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleNearbySearchResponse(
    @Json(name = "results") val results: List<GooglePlaceItem>? = null,
    @Json(name = "status") val status: String? = null,
    @Json(name = "error_message") val errorMessage: String? = null
)

@JsonClass(generateAdapter = true)
data class GooglePlaceItem(
    @Json(name = "place_id") val placeId: String,
    @Json(name = "name") val name: String,
    @Json(name = "vicinity") val vicinity: String? = null,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "user_ratings_total") val userRatingsTotal: Int? = null,
    @Json(name = "price_level") val priceLevel: Int? = null,
    @Json(name = "geometry") val geometry: GoogleGeometry? = null,
    @Json(name = "types") val types: List<String>? = null,
    @Json(name = "photos") val photos: List<GooglePlacePhoto>? = null,
    @Json(name = "opening_hours") val openingHours: GoogleOpeningHours? = null
)

@JsonClass(generateAdapter = true)
data class GoogleGeometry(
    @Json(name = "location") val location: GoogleLatLng? = null
)

@JsonClass(generateAdapter = true)
data class GoogleLatLng(
    @Json(name = "lat") val lat: Double,
    @Json(name = "lng") val lng: Double
)

@JsonClass(generateAdapter = true)
data class GooglePlacePhoto(
    @Json(name = "photo_reference") val photoReference: String? = null,
    @Json(name = "width") val width: Int? = null,
    @Json(name = "height") val height: Int? = null
)

@JsonClass(generateAdapter = true)
data class GoogleOpeningHours(
    @Json(name = "open_now") val openNow: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class GooglePlaceDetailsResponse(
    @Json(name = "result") val result: GooglePlaceDetailsResult? = null,
    @Json(name = "status") val status: String? = null
)

@JsonClass(generateAdapter = true)
data class GooglePlaceDetailsResult(
    @Json(name = "place_id") val placeId: String,
    @Json(name = "name") val name: String,
    @Json(name = "formatted_address") val formattedAddress: String? = null,
    @Json(name = "formatted_phone_number") val formattedPhoneNumber: String? = null,
    @Json(name = "international_phone_number") val internationalPhoneNumber: String? = null,
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "user_ratings_total") val userRatingsTotal: Int? = null,
    @Json(name = "website") val website: String? = null,
    @Json(name = "geometry") val geometry: GoogleGeometry? = null,
    @Json(name = "reviews") val reviews: List<GoogleReviewItem>? = null
)

@JsonClass(generateAdapter = true)
data class GoogleReviewItem(
    @Json(name = "author_name") val authorName: String,
    @Json(name = "rating") val rating: Double,
    @Json(name = "relative_time_description") val relativeTimeDescription: String? = null,
    @Json(name = "text") val text: String? = null
)

// Overpass OpenStreetMap Real Spatial Engine Models
@JsonClass(generateAdapter = true)
data class OverpassResponse(
    @Json(name = "elements") val elements: List<OverpassElement>? = null
)

@JsonClass(generateAdapter = true)
data class OverpassElement(
    @Json(name = "id") val id: Long,
    @Json(name = "type") val type: String,
    @Json(name = "lat") val lat: Double? = null,
    @Json(name = "lon") val lon: Double? = null,
    @Json(name = "center") val center: GoogleLatLng? = null,
    @Json(name = "tags") val tags: Map<String, String>? = null
)
