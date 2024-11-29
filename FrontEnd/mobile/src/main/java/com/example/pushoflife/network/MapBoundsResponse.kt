package com.example.pushoflife.network

data class MapBoundsResponse(
    val aed_id: Int,               // AED 고유 ID
    val aed_address: String,       // 주소
    val aed_place: String,         // 장소 이름
    val aed_location: String,      // 상세 위치
    val aed_latitude: Double,      // 위도
    val aed_longitude: Double      // 경도
)