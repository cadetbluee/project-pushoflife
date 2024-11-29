package com.example.pushoflife.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Twilio SMS 전송 요청
    @FormUrlEncoded
    @POST("Accounts/{AccountSid}/Messages.json")
    fun sendSms(
        @Header("Authorization") authHeader: String, // 인증 헤더
        @Path("AccountSid") accountSid: String,
        @Field("To") toPhoneNumber: String,
        @Field("From") fromPhoneNumber: String,
        @Field("Body") messageBody: String
    ): Call<Void>

    // 회원가입
    @POST("POL/users/join")
    fun signUpUser(@Body signUpRequest: SignUpRequest): Call<Void>

    // 로그인
    @POST("login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<Void>

    // 로그아웃
    @POST("users/logout")
    fun logoutUser(@Header("Authorization") token: String): Call<Void>

    // 회원탈퇴
    @DELETE("POL/users")
    fun deleteUser(@Header("Authorization") token: String): Call<Void>

    // 회원정보 수정
    @PUT("POL/users/info")
    fun updateUser(
        @Header("Authorization") token: String,
        @Body updateUserRequest: UpdateUserRequest
    ): Call<UserInfoResponse>

    //회원정보 조회
    @GET("POL/users/info")
    fun getUserInfo(
        @Header("Authorization") token: String
    ): Call<UserInfoResponse>

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

    @POST("POL/location")
    fun locationUpdate(@Body currentLocationRequest:CurrentLocationRequest): Call<Void>

    @POST("POL/location/help")
    fun askForHelp(@Body sendLocationRequest:SendLocationRequest): Call<Void>

}
