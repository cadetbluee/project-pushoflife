package com.example.pushoflife.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Token에 사용하는 DataStore
val Context.tokenDataStore by preferencesDataStore(name = "auth_preferences")

object TokenPreferencesKeys {
    val TOKEN_KEY = stringPreferencesKey("auth_token")
    val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")  // New key for FCM token
    val LATITUDE= doublePreferencesKey("latitude")
    val LONGITUDE= doublePreferencesKey("longitude")
}

class TokenPreferences(private val context: Context) {
    suspend fun saveAuthToken(token: String) {
        context.tokenDataStore.edit { preferences ->
            preferences[TokenPreferencesKeys.TOKEN_KEY] = token
        }
    }

    fun getAuthToken(): Flow<String?> {
        return context.tokenDataStore.data.map { preferences ->
            preferences[TokenPreferencesKeys.TOKEN_KEY]
        }
    }

    suspend fun clearAuthToken() {
        context.tokenDataStore.edit { preferences ->
            preferences.remove(TokenPreferencesKeys.TOKEN_KEY)
        }
    }
    suspend fun saveFcmToken(fcmToken: String) {  // Save FCM token
        context.tokenDataStore.edit { preferences ->
            preferences[TokenPreferencesKeys.FCM_TOKEN_KEY] = fcmToken
        }
    }
    suspend fun saveLocation(latitude: Double,longitude:Double) {  // Save FCM token
        context.tokenDataStore.edit { preferences ->
            preferences[TokenPreferencesKeys.LATITUDE] = latitude
            preferences[TokenPreferencesKeys.LONGITUDE] = longitude
        }
    }

    fun getFcmToken(): Flow<String?> {  // Retrieve FCM token
        return context.tokenDataStore.data.map { preferences ->
            preferences[TokenPreferencesKeys.FCM_TOKEN_KEY]
        }
    }
    fun getLocation(): Flow<Pair<Double?, Double?>> {  // Retrieve latitude and longitude as a Pair
        return context.tokenDataStore.data.map { preferences ->
            val latitude = preferences[TokenPreferencesKeys.LATITUDE]
            val longitude = preferences[TokenPreferencesKeys.LONGITUDE]
            Pair(latitude, longitude)
        }
    }
}
