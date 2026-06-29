package com.example.d308vacationplanner.ui.alerts

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.d308vacationplanner.entities.Excursion
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

object AlertScheduler {

    fun createNotificationChannel(context: Context) {
        Log.d("Alert", "Creating notification channels")

        val vacationChannel = NotificationChannel(
            "vacation_alerts",
            "Vacation Alerts",
            NotificationManager.IMPORTANCE_HIGH
        )

        val excursionChannel = NotificationChannel(
            "excursion_alert",
            "Excursion Alerts",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(vacationChannel)
        manager.createNotificationChannel(excursionChannel)
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
        Log.d("Alert", "scheduleAlert CALLED with date=$date title=$title type=$type id=$id")

        if (!ensureExtractAlarmPermission(context)) return

        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val localDate = try {
            LocalDate.parse(date, formatter)
        } catch (e: Exception) {
            Log.d("Alert", "Date parse failed for $date", e)
            return
        }

        val triggerTime = localDate
            .atTime(8, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val requestCode = (id * 1000 + type.hashCode().absoluteValue % 1000).toInt()

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

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }

    fun cancelAlert(context: Context, requestCode: Int) {
        val intent = Intent(context, VacationAlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun cancelVacationAlerts(context: Context, vacationId: Long, reminderDays: Set<Int>) {
        cancelAlert(context, vacationId.toInt())
        cancelAlert(context, vacationId.toInt() + 100000)

        reminderDays.forEach { day ->
            cancelAlert(context, vacationId.toInt() + day)
        }
    }

    fun scheduleAllAlerts(
        context: Context,
        vacationId: Long,
        startDate: String,
        reminderDays: Set<Int>,
        excursions: List<Excursion>
    ) {
        scheduleAlert(
            context,
            startDate,
            "Your trip starts today!",
            "trip_start",
            vacationId
        )

        Log.d("ALERT_DEBUG", "scheduleAllAlerts() CALLED for vacationId=$vacationId")
        reminderDays.forEach { day ->
            val reminderDate = computeReminderDate(startDate, day)
            val message = if (day == 1) "Your trip starts tomorrow!"
            else "Your trip starts in $day days!"

            scheduleAlert(
                context,
                reminderDate,
                message,
                "reminder",
                vacationId + day
            )
        }

        excursions.forEach { ex ->
            scheduleExcursionAlert(context, ex, reminderDays)
        }
    }

    fun scheduleExcursionAlert(context: Context, excursion: Excursion, reminderDays: Set<Int>) {
        Log.d("EXC_ALERT", "Scheduling excursion alert for id=${excursion.id}, title=${excursion.title}, date=${excursion.date}")

        if (!ensureExtractAlarmPermission(context)) {
            Log.d("EXC_ALERT", "Exact alarm permission NOT granted - excursion alert NOT scheduled")
            return
        }

        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

        try{
            val localDate = LocalDate.parse(excursion.date, formatter)
            Log.d("EXC_ALERT", "Parsed excursion date successfully: $localDate")
        } catch (e: Exception){
            Log.d("EXC_ALERT", "FAILED to parse excursion date: ${excursion.date}", e)
            return
        }
        val localDate = LocalDate.parse(excursion.date, formatter)


        val triggerTime = localDate
            .atTime(8, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        Log.d("EXC_ALERT", "Trigger time millis=$triggerTime, human=${java.util.Date(triggerTime)}")

        val requestCode = (excursion.id * 2000).toInt()
        Log.d("EXC_ALERT", "Day-of excursion requestCode=$requestCode")

        val intent = Intent(context, ExcursionAlertReceiver::class.java).apply {
            putExtra("title", excursion.title)
            putExtra("id", excursion.id)
            putExtra("type", "excursion")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            Log.d("EXC_ALERT", "SUCCESS: Excursion alarm scheduled for id=${excursion.id}")
        } catch (e: Exception){
            Log.d("EXC_ALERT", "FAILED to schedule excursion alarm", e)
        }


        reminderDays.forEach { day ->
            val reminderDate = computeReminderDate(excursion.date, day)
            Log.d("EXC_ALERT", "Reminder $day days before = $reminderDate")
            val reminderLocal = LocalDate.parse(reminderDate, formatter)

            val reminderTrigger = reminderLocal
                .atTime(8, 0)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val reminderRequestCode = (excursion.id * 2000 + day).toInt()
            Log.d("EXC_ALERT", "Reminder requestCode=$reminderRequestCode")

            val reminderIntent = Intent(context, ExcursionAlertReceiver::class.java).apply {
                putExtra("title", "${excursion.title} is coming up!")
                putExtra("id", excursion.id)
                putExtra("type", "excursion")
            }

            val reminderPendingIntent = PendingIntent.getBroadcast(
                context,
                reminderRequestCode,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTrigger,
                    reminderPendingIntent
                )
                Log.d("EXC_ALERT", "SUCCESS: Reminder scheduled for day=$day id=${excursion.id}")
            } catch (e: Exception){
                Log.d("EXC_ALERT", "FAILED to schedule reminder for day=$day", e)
            }

        }
    }
}