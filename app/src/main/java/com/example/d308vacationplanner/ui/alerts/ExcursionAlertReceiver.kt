package com.example.d308vacationplanner.ui.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.d308vacationplanner.R
import android.Manifest

class ExcursionAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent){
        Log.d("EXC_RECEIVER", "ExcursionAlertReceiver FIRED")
        val title = intent.getStringExtra("title") ?: "Excursion"
        val id = intent.getLongExtra("id", -1)

        val notification = NotificationCompat.Builder(context, "excursion_alert")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Excursion Reminder")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ){
            Log.e("Alert", "POST_NOTIFICATIONS not granted - skipping excursion notification")
            return
        }
        try {
            NotificationManagerCompat.from(context).notify(id.toInt(), notification)
        }catch (e: SecurityException){
            Log.e("Alert", "Failed to post notification", e)
        }


    }

}