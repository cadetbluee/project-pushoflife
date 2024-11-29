package com.example.pushoflife.network

data class UserInfoResponse(
    val user_name: String?,
    val user_gender: String?,
    val user_birthday: String?,
    val user_disease: String?,
    val user_phone: String?,
    val user_protector: String?
)