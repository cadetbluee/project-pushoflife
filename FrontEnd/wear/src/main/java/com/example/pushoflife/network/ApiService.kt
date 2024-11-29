package com.example.pushoflife.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("POL/aed/within-bounds")
    fun sendMapBounds(
        @Query("nor_latitude") norLatitude: Double,
        @Query("nor_longitude") norLongitude: Double,
        @Query("sou_latitude") souLatitude: Double,
        @Query("sou_longitude") souLongitude: Double
    ): Call<List<MapBoundsResponse>>

    @GET("POL/aed/details/{aedId}")
    fun sendAedDetail(
        @Path("aedId") aedId: Int
    ): Call<AedDetailResponse>

}
