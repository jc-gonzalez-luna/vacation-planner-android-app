package com.example.d308vacationplanner.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AlertScheduler {

    fun scheduleAlert(context: Context, date: String, title: String, type: String){
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val localDate = LocalDate.parse(date, formatter)
        val triggerTime = localDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val intent = Intent(context, VacationAlertReceiver::class.java).apply {
            putExtra("title", title)
            putExtra("type", type)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (0..99999).random(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            if(!alarmManager.canScheduleExactAlarms()){
                val settingIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                settingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(settingIntent)
                return
            }
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException){
            val settingIntent = Intent(
                Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            settingIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(settingIntent)
        }
    }
}


