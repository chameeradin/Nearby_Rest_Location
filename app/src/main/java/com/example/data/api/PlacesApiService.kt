package com.example.data.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GooglePlacesApi {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String, // "lat,lng"
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("key") apiKey: String
    ): GoogleNearbySearchResponse

    @GET("maps/api/place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,rating,user_ratings_total,formatted_phone_number,international_phone_number,formatted_address,geometry,photos,reviews,opening_hours,website,price_level",
        @Query("key") apiKey: String
    ): GooglePlaceDetailsResponse
}

interface OverpassApi {
    @Headers(
        "User-Agent: NearbyRestLocations/1.0 (Android; wavesit.lk)",
        "Accept: application/json"
    )
    @FormUrlEncoded
    @POST("api/interpreter")
    suspend fun queryNearbyPlaces(
        @Field("data") query: String
    ): OverpassResponse
}

