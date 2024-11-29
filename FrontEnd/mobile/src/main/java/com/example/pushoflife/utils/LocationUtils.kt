package com.example.pushoflife.utils

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import com.google.android.gms.location.*
import java.util.Locale
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.Toast


@Composable
fun GetLocation(context: Context): String {
    var address by remember { mutableStateOf("위치 정보를 불러오는 중...") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // 권한이 부여된 경우에만 위치 요청을 수행
                requestLocation(fusedLocationClient, context) { location ->
                    val geocoder = Geocoder(context, Locale.KOREA)
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        address = addresses[0].getAddressLine(0)
                    }
                }
            } else {
                address = "위치 권한이 없습니다."
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    return address
}

// 새로운 함수: 현재 위치를 Location 객체로 반환
@Composable
fun GetCurrentLocationForMap(context: Context, onLocationReceived: (Location) -> Unit) {
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                requestLocation(fusedLocationClient, context, onLocationReceived)
            } else {
                Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_LONG).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}


fun requestLocation(
    fusedLocationClient: FusedLocationProviderClient,
    context: Context,
    onLocationReceived: (Location) -> Unit
) {
    // 권한이 있는지 확인
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
    ) {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000L
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    onLocationReceived(location)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    } else {
        // 권한이 없는 경우 처리
        throw SecurityException("위치 권한이 필요합니다.")
    }
}

