package com.ssafy.locket.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ssafy.locket.presentation.common.view.MainActivity
import com.ssafy.locket.presentation.login.LoginActivity

private const val TAG = "LocketFirebaseMessaging"
//@AndroidEntryPoint
class LocketFirebaseMessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
//        Log.d(TAG, "onNewToken: $token")
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
        } else {
            val data = message.data
            messageTitle = data["title"].orEmpty()
            messageContent = data["body"].orEmpty()
            messageType = data["type"].orEmpty()

            if(messageType == "price") {
                productId = data["productId"].orEmpty().toInt()
            }
        }

        createNotification(messageTitle, messageContent, messageType, productId)
    }

    private fun createNotification(title: String, content: String, type: String, productId: Int) {
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
        val smallIcon = R.drawable.ic_home_budget
//            if("budget".equals(type)) R.drawable.ic_notification_finance
//        else if("price".equals(type)) R.drawable.ic_notification_product
//        else R.drawable.ic_home_budget // TODO 추후 앱 아이콘으로 변경

        val intent = Intent(this, LoginActivity::class.java).apply {
            putExtra("notification","notification")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(this, 1000, intent, FLAG_MUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(smallIcon)  // 🔥 작은 아이콘 추가 (필수)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify(0, notificationBuilder)

    }
}