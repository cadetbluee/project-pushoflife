package com.example.pushoflife.network

import com.example.pushoflife.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit 클라이언트 생성
object RetrofitClient {
    private const val BASE_URL = BuildConfig.BACK_SERVER

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)  // API 베이스 URL 설정
        .addConverterFactory(GsonConverterFactory.create())  // Gson 컨버터 추가
        .build()

    // ApiService 인터페이스 구현체 생성
    val apiService: ApiService = retrofit.create(ApiService::class.java)

}

// Twilio Retrofit 클라이언트 생성
object TwilioRetrofitClient {
    private const val TWILIO_BASE_URL = "https://api.twilio.com/2010-04-01/"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(TWILIO_BASE_URL)  // Twilio API 베이스 URL 설정
        .addConverterFactory(GsonConverterFactory.create())  // Gson 컨버터 추가
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}