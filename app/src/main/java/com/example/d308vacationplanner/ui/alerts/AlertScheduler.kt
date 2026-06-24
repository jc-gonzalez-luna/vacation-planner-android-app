package com.example.d308vacationplanner.ui.alerts

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.collection.intSetOf
import androidx.compose.ui.text.font.FontVariation
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.ui.alerts.AlertScheduler.computeReminderDate
import com.example.d308vacationplanner.ui.alerts.AlertScheduler.scheduleAlert
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

object AlertScheduler {
    fun createNotificationChannel(context: Context){
        Log.d("Alert", "Creating notification channel: vacation_alerts")
        val channel = NotificationChannel(
            "vacation_alerts",
            "Vacation Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
    fun ensureExtractAlarmPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return false
            }
        }
        return true
    }
    fun computeReminderDate(date: String, daysBefore: Int): String {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val localDate = LocalDate.parse(date, formatter)
        return localDate.minusDays(daysBefore.toLong()).format(formatter)
    }
        fun scheduleAlert(context: Context, date: String, title: String, type: String, id: Long) {
            Log.d("Alert", "scheduleAlert CALLED with:")
            Log.d("Alert", " date=$date")
            Log.d("Alert", " title=$title")
            Log.d("Alert", " type=$type")
            Log.d("Alert", " id=$id")

            if (!ensureExtractAlarmPermission(context))return

            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
            val localDate = try {
                LocalDate.parse(date, formatter)
            } catch (e: Exception) {
                Log.d("Alert", "Date parse failed for date = $date", e)
                return
        }

        val triggerTime = localDate
            .atTime(8, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        //val triggerTime = System.currentTimeMillis() + 10_000
        Log.d("Alert", "Trigger time (millis) = $triggerTime")
        Log.d("Alert", "Trigger time (human) = ${java.util.Date(triggerTime)}")

        //val requestCode = (id.toString() + type + date).hashCode()
        val requestCode = (id * 1000 + type.hashCode().absoluteValue % 1000).toInt()
        Log.d("Alert", "Generated requestCode = $requestCode")

        val intent = Intent(context, VacationAlertReceiver::class.java).apply {
            putExtra("message", title)
            putExtra("vacationName", title)
            putExtra("type", type)
            putExtra("id", id)

        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        Log.d("Alert", "PendingIntent created successfully")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            Log.d("Alert", "Alarm schedule Successfully for requestCode=$requestCode")
        } catch (e: SecurityException) {
            Log.d("Alert", "SecurityException while scheduling alarm", e)
        } catch (e: Exception) {
            Log.d("Alert", "Unexpected error while scheduling alarm", e)
        }
    }
    fun cancelAlert(context: Context, requestCode: Int){
        Log.d("Alert", "Cancelling alert with requestCode = $requestCode")
        val intent = Intent(context, VacationAlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        Log.d("Alert", "Alert cancelled")
    }
    fun cancelVacationAlerts(
        context: Context,
        vacationId: Long,
        reminderDate: Set<Int>
    ){
        Log.d("Alert", "Cancelling All alert for vacationId=$vacationId")
        cancelAlert(context, vacationId.toInt())

        cancelAlert(context, vacationId.toInt() + 100000)

        reminderDate.forEach { day ->
            cancelAlert(context, vacationId.toInt() + day)
        }
    }
    fun scheduleAllAlerts(
        context: Context,
        vacationId: Long,
        startDate: String,
        reminderDays: Set<Int>,
        excursions: List <Excursion>

    ){
        scheduleAlert(
            context,
            startDate,
            "Your trip starts today!",
            "trip_start", vacationId)

        reminderDays.forEach { day ->
            val reminderDate = computeReminderDate(startDate, day)
            val message = when (day) {
                1 -> "Your trip starts tomorrow!"
                else -> "Your trip starts in $day days!"
            }
            scheduleAlert(
                context,
                reminderDate,
                message,
                "reminder",
                vacationId + day)
        }

        excursions.forEach { ex ->
            scheduleAlert(
                context,
                ex.date,
                ex.title,
                "excursion",
                ex.id)
        }
    }


    }
