package com.olympplanner.app.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.olympplanner.app.domain.model.Task

object NotificationScheduler {

    fun scheduleNotification(context: Context, task: Task) {
        if (task.reminders.isEmpty()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        task.reminders.forEach { reminderTime ->
            if (reminderTime > System.currentTimeMillis()) {
                val intent = Intent(context, NotificationReceiver::class.java).apply {
                    putExtra(NotificationReceiver.EXTRA_TASK_ID, task.id)
                    putExtra(NotificationReceiver.EXTRA_TASK_TITLE, task.title)
                    putExtra(NotificationReceiver.EXTRA_TASK_DESCRIPTION, task.description)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (task.id.toString() + reminderTime.toString()).hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminderTime,
                        pendingIntent
                    )
                } catch (e: SecurityException) {
                    // Handle permission denial
                }
            }
        }
    }

    fun cancelNotification(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
    }
}

