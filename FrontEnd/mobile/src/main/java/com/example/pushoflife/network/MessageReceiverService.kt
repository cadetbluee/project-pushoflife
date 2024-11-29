package com.example.pushoflife.network

import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MessageReceiverService : WearableListenerService() {
    // 해당 서비스 사용할거면 주석 해제하면 됨. 같은 기능이 MessageReceiverMain<-으로 MainActivity에 구현되어있음

//    override fun onMessageReceived(messageEvent: MessageEvent) {
//        // 메시지의 path와 일치하는지 확인
//        if (messageEvent.path == "/emergency_path") {
//            // 메시지 수신
//            val receivedMessage = String(messageEvent.data)
//            Log.d("MessageReceiver", "Received message: $receivedMessage")
//
//        }
//    }
}