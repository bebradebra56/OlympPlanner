package com.olympplanner.app.data.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.olympplanner.app.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(NotificationReceiver.EXTRA_TASK_ID, -1)
        
        if (taskId == -1L) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId.toInt())

        when (intent.action) {
            NotificationReceiver.ACTION_SNOOZE -> {
                val minutes = intent.getIntExtra(NotificationReceiver.EXTRA_SNOOZE_MINUTES, 10)
                scheduleSnooze(context, taskId, minutes)
            }
            NotificationReceiver.ACTION_DONE -> {
                markTaskAsCompleted(context, taskId)
            }
        }
    }

    private fun scheduleSnooze(context: Context, taskId: Long, minutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + minutes * 60 * 1000L

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_TASK_ID, taskId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    private fun markTaskAsCompleted(context: Context, taskId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getInstance(context)
            database.taskDao().updateTaskCompletion(taskId, true)
        }
    }
}

