package com.example.pushoflife.network

data class CurrentLocationRequest (
    val fcm_token: String,
    val longitude: Double,
    val latitude: Double
)