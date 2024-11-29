package com.example.pushoflife.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class LocationUpdatesReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "ACTION_PROCESS_UPDATES") {
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            Log.d("LocationUpdatesReceiver", "Latitude: $latitude, Longitude: $longitude")
        } else {
            Log.e("LocationUpdatesReceiver", "Unexpected action: ${intent.action}")
        }
    }
}

