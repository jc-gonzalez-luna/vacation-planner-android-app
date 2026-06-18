package com.example.d308vacationplanner.ui

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.d308vacationplanner.R

class VacationAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent){

        val title = intent.getStringExtra("title") ?: "Vacation"
        val type = intent.getStringExtra("type") ?: "Event"

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val message = when (type.lowercase()){
            "starting" -> "Your vacation \"$title\" starts today!"
            "ending" -> "Your vacation \"$title\" ends today."
            "excursion" -> "Your excursion \"$title\" is happening today!"
            else -> "$title is $type today"
        }

        val notification = NotificationCompat.Builder(context, "vacation_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .build()
        NotificationManagerCompat.from(context).notify((0..999999).random(),notification)
    }
}

