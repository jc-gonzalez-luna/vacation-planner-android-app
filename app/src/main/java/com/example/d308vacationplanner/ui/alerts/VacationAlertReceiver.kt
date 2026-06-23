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
        val title = intent.getStringExtra("title") ?: "Vacation"
        val type = intent.getStringExtra("type") ?: "Event"
        val id = intent.getLongExtra("id", -1L)
        Log.d("ALERT_RECEIVER", "Extras: title=$title type=$type id=$id")

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            //action = "$OPEN_${type}_$id"

            if (type == "excursion"){
                putExtra("excursionId", id)
            }else{
                putExtra("vacationId", id)
            }
        }
        val requestCode = (id.toString() + type).hashCode()
        val openPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val message = when {
            type.startsWith("starting_soon_") -> {
                val days = type.removePrefix("starting_soon_").toInt()
                "Your vacation \"$title\" starts in $days days!"
            }
            type == "starting" -> "Your vacation \"$title\" starts today!"
            type == "ending" -> "Your vacation \"$title\" ends today."
            type == "excursion" -> "Your excursion \"$title\" is happening today!"
                else -> "$title is $type today"
            }


        val notification = NotificationCompat.Builder(context, "vacation_alerts")
            .setSmallIcon(com.example.d308vacationplanner.R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .setGroup("vacation_alerts_group")
            .build()



        val summary = NotificationCompat.Builder(context, "vacation_alerts")
            .setSmallIcon(com.example.d308vacationplanner.R.drawable.ic_launcher_foreground)
            .setContentTitle("Vacation Alerts")
            .setContentText("You have upcoming vacation reminders.")
            .setStyle(NotificationCompat.InboxStyle())
            .setGroup("vacation_alerts_group")
            .setGroupSummary(true)
            .build()

        val manager = NotificationManagerCompat.from(context)
        val canNotify = Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
        if(canNotify) {
            Thread.sleep(150)
            manager.notify(requestCode, notification)
            manager.notify(0, summary)
        }
        //NotificationManagerCompat.from(context).notify((0..999999).random(),notification)
    }
}