package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.model.EventEntity
import com.example.util.DateUtils
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object ReminderScheduler {

    fun scheduleReminder(context: Context, event: EventEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, BirthdayReminderReceiver::class.java).apply {
            action = "com.aistudio.dies.ACTION_REMINDER"
            putExtra("EXTRA_EVENT_ID", event.id)
            putExtra("EXTRA_NAME", event.name)
            putExtra("EXTRA_TYPE", event.eventType.name)
            putExtra("EXTRA_IS_TODAY", event.reminderDaysBefore == 0)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (!event.isReminderEnabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val today = LocalDate.now()
        val nextOccurrence = DateUtils.getNextOccurrence(event.dateDay, event.dateMonth, today)
        val triggerDate = nextOccurrence.minusDays(event.reminderDaysBefore.toLong())

        var reminderDateTime = LocalDateTime.of(
            triggerDate.year,
            triggerDate.monthValue,
            triggerDate.dayOfMonth,
            event.reminderHour,
            event.reminderMinute
        )

        // If time already passed today, advance to next year's cycle
        val now = LocalDateTime.now()
        if (reminderDateTime.isBefore(now)) {
            val futureOccurrence = DateUtils.getNextOccurrence(event.dateDay, event.dateMonth, today.plusDays(1))
            val futureTrigger = futureOccurrence.minusDays(event.reminderDaysBefore.toLong())
            reminderDateTime = LocalDateTime.of(
                futureTrigger.year,
                futureTrigger.monthValue,
                futureTrigger.dayOfMonth,
                event.reminderHour,
                event.reminderMinute
            )
        }

        val triggerMillis = reminderDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            // Inexact fallback if exact alarm permission is restricted
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, eventId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BirthdayReminderReceiver::class.java).apply {
            action = "com.aistudio.dies.ACTION_REMINDER"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            eventId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
