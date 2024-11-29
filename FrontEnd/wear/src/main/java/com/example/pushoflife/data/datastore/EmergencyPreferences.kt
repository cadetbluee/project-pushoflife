package com.example.pushoflife.data.datastore


import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

// Emergency에 사용하는 DataStore
val Context.emergencyDataStore by preferencesDataStore(name = "emergency_preferences")

object EmergencyPreferencesKeys {
    val LATITUDE_KEY = doublePreferencesKey("latitude")
    val LONGITUDE_KEY = doublePreferencesKey("longitude")
    val TIMESTAMP_KEY = longPreferencesKey("timestamp") // 타임스탬프 키 추가
}

class EmergencyPreferences(private val context: Context) {

    // 위치 정보 및 타임스탬프 저장 함수
    suspend fun saveLocation(latitude: Double, longitude: Double) {
        val currentTime = System.currentTimeMillis()
        context.emergencyDataStore.edit { preferences ->
            preferences[EmergencyPreferencesKeys.LATITUDE_KEY] = latitude
            preferences[EmergencyPreferencesKeys.LONGITUDE_KEY] = longitude
            preferences[EmergencyPreferencesKeys.TIMESTAMP_KEY] = currentTime // 타임스탬프 저장
        }
        Log.d("savedLocation","$latitude,$longitude")
    }

    // 위치 정보 가져오는 함수 (Flow로 반환), 5분 경과 시 데이터 삭제
    fun getLocation(): Flow<Pair<Double?, Double?>> {
        return context.emergencyDataStore.data.map { preferences ->
            val latitude = preferences[EmergencyPreferencesKeys.LATITUDE_KEY]
            val longitude = preferences[EmergencyPreferencesKeys.LONGITUDE_KEY]
            val timestamp = preferences[EmergencyPreferencesKeys.TIMESTAMP_KEY] ?: 0L

            // 5분이 지났는지 확인
            val currentTime = System.currentTimeMillis()
            val isExpired = currentTime - timestamp > TimeUnit.MINUTES.toMillis(5)

            if (isExpired) {
                // 5분이 경과한 경우 데이터 삭제
                clearLocation()
                Pair(null, null) // null을 반환하여 위치 정보가 없음을 나타냄
            } else {
                // 경과하지 않은 경우 위치 정보 반환
                Pair(latitude, longitude)
            }
        }
    }

    // 위치 정보 삭제 함수
    suspend fun clearLocation() {
        context.emergencyDataStore.edit { preferences ->
            preferences.remove(EmergencyPreferencesKeys.LATITUDE_KEY)
            preferences.remove(EmergencyPreferencesKeys.LONGITUDE_KEY)
            preferences.remove(EmergencyPreferencesKeys.TIMESTAMP_KEY)
        }
    }
}
