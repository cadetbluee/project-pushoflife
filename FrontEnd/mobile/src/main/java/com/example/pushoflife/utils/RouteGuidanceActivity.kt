package com.example.pushoflife.utils

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.FrameLayout
import com.example.pushoflife.BuildConfig
import com.example.pushoflife.R
import com.skt.tmap.TMapData
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
import com.skt.tmap.TMapView
import android.util.Log
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.overlay.TMapMarkerItem
import android.location.Location

class RouteGuidanceActivity : AppCompatActivity(), TMapGpsManager.OnLocationChangedListener {

    private lateinit var tMapView: TMapView
    private lateinit var tMapGpsManager: TMapGpsManager
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_guidance)

        val tmapViewContainer = findViewById<FrameLayout>(R.id.tmapViewContainer)

        // TMapView 초기화 및 설정
        tMapView = TMapView(this).apply {
            setSKTMapApiKey(BuildConfig.T_MAP_KEY)  // Tmap API 키 설정
        }
        tmapViewContainer.addView(tMapView) // TMapView를 FrameLayout에 추가

        tMapGpsManager = TMapGpsManager(this).apply {
            minTime = 1000
            minDistance = 10f
        }

        tMapView.setOnMapReadyListener {
            Log.d("RouteGuidanceActivity", "TMapView is ready")

            // 출발지와 도착지 인텐트에서 받기
            val startLatitude = intent.getDoubleExtra("startLatitude", 0.0)
            val startLongitude = intent.getDoubleExtra("startLongitude", 0.0)
            val endLatitude = intent.getDoubleExtra("endLatitude", 0.0)
            val endLongitude = intent.getDoubleExtra("endLongitude", 0.0)

            Log.d("RouteGuidanceActivity", "Start Point: ($startLatitude, $startLongitude)")
            Log.d("RouteGuidanceActivity", "End Point: ($endLatitude, $endLongitude)")

            val startPoint = TMapPoint(startLatitude, startLongitude)
            val endPoint = TMapPoint(endLatitude, endLongitude)

            // 먼저 출발지로 지도의 중심 이동
            tMapView.post {
                tMapView.setCenterPoint(startLongitude, startLatitude, true)
                tMapView.setZoomLevel(15) // 출발지에 적절한 줌 레벨로 설정
                Log.d("RouteGuidanceActivity", "Moved to start point: ($startLatitude, $startLongitude)")

                // 1초 후 경로 탐색 시작 (지연을 줘서 지도가 준비될 시간을 확보)
                handler.postDelayed({
                    findRoute(startPoint, endPoint)
                }, 1000)
            }
        }
    }

    override fun onLocationChange(location: android.location.Location) {
        val userLocation = TMapPoint(location.latitude, location.longitude)

        val startLatitude = intent.getDoubleExtra("startLatitude", 0.0)
        val startLongitude = intent.getDoubleExtra("startLongitude", 0.0)
        tMapView.setCenterPoint(startLongitude, startLatitude, true)
        Log.d("RouteGuidanceActivity", "User location updated to: (${location.latitude}, ${location.longitude})")
    }

    private fun findRoute(startPoint: TMapPoint, endPoint: TMapPoint) {
        // 경로 탐색 및 지도에 경로 표시
        TMapData().findPathData(startPoint, endPoint, object : TMapData.OnFindPathDataListener {
            override fun onFindPathData(tMapPolyLine: TMapPolyLine?) {
                if (tMapPolyLine == null) {
                    Toast.makeText(this@RouteGuidanceActivity, "경로를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    Log.e("RouteGuidanceActivity", "Failed to find path data: tMapPolyLine is null")
                    return
                }
                // 경로 스타일 설정
                tMapPolyLine.lineWidth = 3f
                tMapPolyLine.lineColor = Color.BLUE
                tMapPolyLine.outLineWidth = 5f
                tMapPolyLine.outLineColor = Color.RED

                // 지도에 경로 추가
                tMapView.addTMapPolyLine(tMapPolyLine)
                tMapView.setCenterPoint(startPoint.longitude, startPoint.latitude, true)
                Log.d("RouteGuidanceActivity", "Path added to map")

                // 경로에 맞춰 지도 중심 및 줌 레벨 설정
                val boundingBox = tMapPolyLine.linePointList
                if (boundingBox.isNotEmpty()) {
                    val minLatitude = boundingBox.minOf { it.latitude }
                    val maxLatitude = boundingBox.maxOf { it.latitude }
                    val minLongitude = boundingBox.minOf { it.longitude }
                    val maxLongitude = boundingBox.maxOf { it.longitude }

                    val centerLatitude = (minLatitude + maxLatitude) / 2
                    val centerLongitude = (minLongitude + maxLongitude) / 2

                    Log.d("RouteGuidanceActivity", "Centering map to: ($centerLatitude, $centerLongitude)")
                    tMapView.post {
                        tMapView.setCenterPoint(centerLongitude, centerLatitude, true)
                        tMapView.setZoomLevel(13) // 적절한 줌 레벨로 설정
                    }
                } else {
                    Log.e("RouteGuidanceActivity", "Bounding box is empty, unable to center map")
                }
            }
        })
    }
}
