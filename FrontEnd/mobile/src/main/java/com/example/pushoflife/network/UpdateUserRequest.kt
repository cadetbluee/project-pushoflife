package com.example.pushoflife.network

data class UpdateUserRequest(
    val user_name: String? = null,
    val password1: String? = null,
    val password2: String? = null,
    val user_birthday: String? = null,
    val user_gender: String? = null,
    val user_disease: String? = null,
    val user_protector: String? = null
)