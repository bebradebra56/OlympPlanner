package com.olympplanner.app.bkgoprte.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.olympplanner.app.OlympPlannerActivity
import com.olympplanner.app.R
import com.olympplanner.app.bkgoprte.presentation.app.OlympPlannerApplication

private const val OLYMP_PLANNER_CHANNEL_ID = "olymp_planner_notifications"
private const val OLYMP_PLANNER_CHANNEL_NAME = "OlympPlanner Notifications"
private const val OLYMP_PLANNER_NOT_TAG = "OlympPlanner"

class OlympPlannerPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                olympPlannerShowNotification(it.title ?: OLYMP_PLANNER_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                olympPlannerShowNotification(it.title ?: OLYMP_PLANNER_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            olympPlannerHandleDataPayload(remoteMessage.data)
        }
    }

    private fun olympPlannerShowNotification(title: String, message: String, data: String?) {
        val olympPlannerNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                OLYMP_PLANNER_CHANNEL_ID,
                OLYMP_PLANNER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            olympPlannerNotificationManager.createNotificationChannel(channel)
        }

        val olympPlannerIntent = Intent(this, OlympPlannerActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val olympPlannerPendingIntent = PendingIntent.getActivity(
            this,
            0,
            olympPlannerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val olympPlannerNotification = NotificationCompat.Builder(this, OLYMP_PLANNER_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.olymp_planner_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(olympPlannerPendingIntent)
            .build()

        olympPlannerNotificationManager.notify(System.currentTimeMillis().toInt(), olympPlannerNotification)
    }

    private fun olympPlannerHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(OlympPlannerApplication.OLYMP_PLANNER_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}