package com.example.pushoflife.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// API 인터페이스 정의
interface ApiService {
    // GET 요청으로 /POL/test 엔드포인트 호출
    @GET("POL/test")
    fun getTestData(@Query("id") id: Int): Call<TestResponse> // id 파라미터 전달
}
