package com.example.pushoflife

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Foreground Service 시작
            val serviceIntent = Intent(context, SensorForegroundService::class.java)
            context.startForegroundService(serviceIntent)
        }
    }
}