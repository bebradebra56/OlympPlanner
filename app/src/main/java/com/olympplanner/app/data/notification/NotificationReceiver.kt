package com.olympplanner.app.data.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.olympplanner.app.MainActivity
import com.olympplanner.app.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1)
        val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE) ?: "Task Reminder"
        val taskDescription = intent.getStringExtra(EXTRA_TASK_DESCRIPTION) ?: ""

        if (taskId != -1L) {
            showNotification(context, taskId, taskTitle, taskDescription)
        }
    }

    private fun showNotification(
        context: Context,
        taskId: Long,
        title: String,
        description: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze actions
        val snooze10Intent = createSnoozeIntent(context, taskId, 10)
        val snooze30Intent = createSnoozeIntent(context, taskId, 30)
        val snooze60Intent = createSnoozeIntent(context, taskId, 60)

        // Done action
        val doneIntent = createDoneIntent(context, taskId)

        val notification = NotificationCompat.Builder(context, OlympPlannerApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_snooze,
                context.getString(R.string.notification_action_snooze_10),
                snooze10Intent
            )
            .addAction(
                R.drawable.ic_snooze,
                context.getString(R.string.notification_action_snooze_30),
                snooze30Intent
            )
            .addAction(
                R.drawable.ic_done,
                context.getString(R.string.notification_action_done),
                doneIntent
            )
            .build()

        notificationManager.notify(taskId.toInt(), notification)
    }

    private fun createSnoozeIntent(context: Context, taskId: Long, minutes: Int): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_TASK_ID, taskId)
            putExtra(EXTRA_SNOOZE_MINUTES, minutes)
        }
        return PendingIntent.getBroadcast(
            context,
            (taskId * 1000 + minutes).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createDoneIntent(context: Context, taskId: Long): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_DONE
            putExtra(EXTRA_TASK_ID, taskId)
        }
        return PendingIntent.getBroadcast(
            context,
            taskId.toInt() + 10000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TASK_TITLE = "task_title"
        const val EXTRA_TASK_DESCRIPTION = "task_description"
        const val EXTRA_SNOOZE_MINUTES = "snooze_minutes"
        const val ACTION_SNOOZE = "com.olympplanner.app.ACTION_SNOOZE"
        const val ACTION_DONE = "com.olympplanner.app.ACTION_DONE"
    }
}

