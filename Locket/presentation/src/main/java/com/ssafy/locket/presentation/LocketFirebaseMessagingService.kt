package com.ssafy.locket.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "LocketFirebaseMessaging"
//@AndroidEntryPoint
class LocketFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        var messageTitle = ""
        var messageContent = ""
        var messageType = ""
        var productId = -1

        if(message.notification != null) {
            messageTitle = message.notification?.title.toString()
            messageContent = message.notification?.body.toString()
            Log.d(TAG, "onMessageReceived notification body: ${message.notification?.body.toString()}")
            Log.d(TAG, "onMessageReceived notification title: ${message.notification?.title.toString()}")
        } else {
            val data = message.data
            messageTitle = data["title"].orEmpty()
            messageContent = data["body"].orEmpty()
            messageType = data["type"].orEmpty()

            if(messageType == "price") {
                productId = data["productId"].orEmpty().toInt()
            }

            Log.d(TAG, "onMessageReceived: else ${message.data}")

            // TODO PendingIntent 추가해서 화면 이동시키기, "price"는 상품 상세 화면으로 이동시키고 "budget"는 알림 화면으로 이동시키기
        }

        createNotification(messageTitle, messageContent, messageType)
    }

    private fun createNotification(title: String, content: String, type: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 설정 (Android 8.0 이상 필수)
        val channel = NotificationChannel(
            "default",
            "기본 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "앱 기본 푸시 알림"
        }
        notificationManager.createNotificationChannel(channel)

        // 🔹 작은 아이콘 설정 (이 아이콘이 없으면 앱이 크래시 발생!)
        val smallIcon = if("price".equals(type)) R.drawable.ic_notification_finance
        else if("budget".equals(type)) R.drawable.ic_notification_product
        else R.drawable.image_character_level_icon // TODO 추후 앱 아이콘으로 변경

        val notificationBuilder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(smallIcon)  // 🔥 작은 아이콘 추가 (필수)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setLocalOnly(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(this).notify(0, notificationBuilder);

    }
}