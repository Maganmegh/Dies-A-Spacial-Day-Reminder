package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BirthdayReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule all active reminders after reboot
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val events = db.eventDao().getAllEventsDirect()
                for (event in events) {
                    if (event.isReminderEnabled) {
                        ReminderScheduler.scheduleReminder(context, event)
                    }
                }
            }
            return
        }

        val eventId = intent.getLongExtra("EXTRA_EVENT_ID", -1L)
        val name = intent.getStringExtra("EXTRA_NAME") ?: "Loved One"
        val eventType = intent.getStringExtra("EXTRA_TYPE") ?: "BIRTHDAY"
        val isToday = intent.getBooleanExtra("EXTRA_IS_TODAY", true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "dies_event_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Birthday & Event Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies you of upcoming birthdays, anniversaries and special days."
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_EVENT_ID", eventId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.toInt(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isToday) {
            if (eventType == "ANNIVERSARY") "💍 Today is $name's Anniversary!" else "🎂 Today is $name's Birthday!"
        } else {
            if (eventType == "ANNIVERSARY") "💍 Tomorrow is $name's Anniversary!" else "🎂 Tomorrow is $name's Birthday!"
        }

        val message = if (isToday) {
            "Open Dies to view memories and send a heartfelt wish!"
        } else {
            "Don't forget to prepare your wishes for tomorrow!"
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify((1000 + eventId).toInt(), notification)
    }
}
