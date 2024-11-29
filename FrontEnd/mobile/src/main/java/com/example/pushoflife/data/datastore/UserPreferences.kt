package com.example.pushoflife.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Context 확장 함수로 DataStore 생성
val Context.dataStore by preferencesDataStore(name = "user_preferences")

// DataStore 키 정의
object UserPreferencesKeys {
    val USER_NAME_KEY = stringPreferencesKey("user_name")
    val USER_BIRTHDATE_KEY = stringPreferencesKey("user_birthdate")
    val USER_GENDER_KEY = stringPreferencesKey("user_gender")
    val USER_DISEASE_KEY = stringPreferencesKey("user_disease")
    val HOSPITAL_KEY = stringPreferencesKey("hospital")
    val PILL_KEY = stringPreferencesKey("pill")
    val USER_PROTECTOR_KEY = stringPreferencesKey("user_protector")
    val USER_ADDRESS_KEY = stringPreferencesKey("user_address")
    val EMERGENCY_MESSAGE_KEY = stringPreferencesKey("emergency_message")
    // 새로운 감지 허용 여부 키 정의
    val ALLOW_DETECTION_KEY = booleanPreferencesKey("allow_detection")
}

// UserPreferences 클래스 정의
class UserPreferences(private val context: Context) {

    // DataStore에 프로필 정보 저장
    suspend fun saveUserProfile(
        name: String,
        birthdate: String,
        gender: String,
        disease: String,
        hospital: String,
        pill: String,
        protector: String,
        address: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.USER_NAME_KEY] = name
            preferences[UserPreferencesKeys.USER_BIRTHDATE_KEY] = birthdate
            preferences[UserPreferencesKeys.USER_GENDER_KEY] = gender
            preferences[UserPreferencesKeys.USER_DISEASE_KEY] = disease
            preferences[UserPreferencesKeys.HOSPITAL_KEY] = hospital
            preferences[UserPreferencesKeys.PILL_KEY] = pill
            preferences[UserPreferencesKeys.USER_PROTECTOR_KEY] = protector
            preferences[UserPreferencesKeys.USER_ADDRESS_KEY] = address
        }
    }

    // DataStore에서 프로필 정보 읽기
    fun getUserProfile(): Flow<UserProfile> {
        return context.dataStore.data.map { preferences ->
            val name = preferences[UserPreferencesKeys.USER_NAME_KEY] ?: ""
            val birthdate = preferences[UserPreferencesKeys.USER_BIRTHDATE_KEY] ?: ""
            val gender = preferences[UserPreferencesKeys.USER_GENDER_KEY] ?: ""
            val disease = preferences[UserPreferencesKeys.USER_DISEASE_KEY] ?: ""
            val hospital = preferences[UserPreferencesKeys.HOSPITAL_KEY] ?: ""
            val pill = preferences[UserPreferencesKeys.PILL_KEY] ?: ""
            val protector = preferences[UserPreferencesKeys.USER_PROTECTOR_KEY] ?: ""
            val address = preferences[UserPreferencesKeys.USER_ADDRESS_KEY] ?: ""
            UserProfile(name, birthdate, gender, disease, hospital, pill, protector, address)
        }
    }
    // 긴급 메시지 저장
    suspend fun saveEmergencyMessage(messageContent: String) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.EMERGENCY_MESSAGE_KEY] = messageContent
        }
    }
    // 긴급 메시지 읽기
    val emergencyMessage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.EMERGENCY_MESSAGE_KEY] ?: "낙상 환자 발생"
        }
    // 감지 허용 여부 저장
    suspend fun setAllowDetection(allow: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[UserPreferencesKeys.ALLOW_DETECTION_KEY] = allow
        }
    }

    // 감지 허용 여부 읽기
    val allowDetection: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[UserPreferencesKeys.ALLOW_DETECTION_KEY] ?: false
        }
}

// UserProfile 데이터 클래스 정의
data class UserProfile(
    val name: String,
    val birthdate: String,
    val gender: String,
    val disease: String,
    val hospital: String,
    val pill: String,
    val protector: String,
    val address: String
)
