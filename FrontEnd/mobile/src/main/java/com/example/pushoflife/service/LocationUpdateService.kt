// LocationUpdatesService.kt
package com.example.pushoflife.service

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pushoflife.data.datastore.TokenPreferences
import com.example.pushoflife.network.CurrentLocationRequest
import com.example.pushoflife.network.RetrofitClient
import com.google.android.gms.location.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LocationUpdatesService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    override fun onCreate() {
        super.onCreate()
        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 위치 요청 설정
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 120000)
            .setMinUpdateIntervalMillis(120000)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationUpdatesService", "Service onStartCommand called")

        // 위치 권한이 있는지 확인합니다.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            Log.e("LocationUpdatesService", "Location permission not granted.")
            stopSelf()
        }
        return START_STICKY
    }

    private fun startLocationUpdates() {
        // LocationSettingsClient 사용하여 위치 설정이 충족되는지 확인
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@addOnSuccessListener
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                Log.d("LocationUpdatesService", "Location updates started.")
            }
            .addOnFailureListener { exception ->
                Log.e("LocationUpdatesService", "Location settings are not satisfied: $exception")
                stopSelf()
            }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            // Retrieve token from DataStore
            GlobalScope.launch(Dispatchers.IO) {
                val tokenPreferences = TokenPreferences(context = applicationContext)
                val fcmToken = tokenPreferences.getFcmToken().first()  // Retrieve token as first element

                locationResult.lastLocation?.let { location ->
                    Log.d("LocationUpdatesService", "Latitude: ${location.latitude}, Longitude: ${location.longitude}, FCM: $fcmToken")
                    tokenPreferences.saveLocation(location.latitude, location.longitude)

                    // 서버로 전송할 LocationRequest 객체 생성
                    val locationRequest = CurrentLocationRequest(
                        fcm_token = fcmToken ?: "",
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    RetrofitClient.apiService.locationUpdate(locationRequest).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Log.d("LocationUpdatesService", "Location data sent successfully.")
                            } else {
                                Log.e("LocationUpdatesService","${locationRequest}")
                                Log.e("LocationUpdatesService", "Failed to send location data. Response code: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("LocationUpdatesService", "Error sending location data: ${t.message}")
                        }
                    })

                    // Broadcast location with FCM token
                    val intent = Intent("ACTION_PROCESS_UPDATES").apply {
                        putExtra("latitude", location.latitude)
                        putExtra("longitude", location.longitude)
                        putExtra("fcmToken", fcmToken)
                    }
                    sendBroadcast(intent)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

