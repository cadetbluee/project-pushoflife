package com.example.pushoflife.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle

object LaunchApp {
    fun launchApp(context: Context, targetActivity: Class<*>, extras: Bundle? = null) {
        val intent = Intent(context, targetActivity).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            extras?.let { putExtras(it) }
        }
        context.startActivity(intent)
    }
}
// 실제 코드에서의 활용은 아래처럼
// import com.example.pushoflife.utils.LaunchApp
// LaunchApp.launchApp(this, SecondActivity::class.java)
