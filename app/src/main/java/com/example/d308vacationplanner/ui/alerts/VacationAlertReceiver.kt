package com.example.d308vacationplanner.ui.alerts

import android.Manifest
import android.R
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.d308vacationplanner.ui.MainActivity

class VacationAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent){
        Log.d("Alert_receiver", "Receiver triggered")
        //val title = intent.getStringExtra("title") ?: "Vacation"
        val messageTitle = intent.getStringExtra("message")?: "You have a vacation update."
        val vacationName = intent.getStringExtra("vacationName") ?: ""
        val type = intent.getStringExtra("type") ?: "event"
        val id = intent.getLongExtra("id", -1L)
        Log.d("ALERT_RECEIVER", "Extras: title=$messageTitle type=$type id=$id")

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            when (type) {
                "excursion" -> putExtra("excursionId", id)
                "trip_start" -> putExtra("vacationId", id)
                "reminder" -> putExtra("vacationId", id)
                else -> putExtra("vacationId", id)
            }
        }
        val requestCode = (id.toString() + type).hashCode()
        val openPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val message = when (type){
            "trip_start" -> "Your vacation starts today!"
            "reminder" -> messageTitle
            "ending" -> "Your vacation ends today."
            "excursion" -> "Your excursion \"$vacationName\" is happening today!"
                else -> messageTitle
            }


        val notification = NotificationCompat.Builder(context, "vacation_alerts")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Vacation Alert ")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .setGroup("vacation_alerts_group")
            .build()



        val summary = NotificationCompat.Builder(context, "vacation_alerts")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Vacation Alerts ")
            .setContentText("You have upcoming vacation reminders.")
            .setStyle(NotificationCompat.InboxStyle())
            .setGroup("vacation_alerts_group")
            .setGroupSummary(true)
            .build()


        val canNotify = Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
        if(canNotify) {
            val manager = NotificationManagerCompat.from(context)
            Thread.sleep(150)
            manager.notify(requestCode, notification)
            manager.notify(0, summary)
        }
        //NotificationManagerCompat.from(context).notify((0..999999).random(),notification)
    }
}