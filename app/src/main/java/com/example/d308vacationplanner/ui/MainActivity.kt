package com.example.d308vacationplanner.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.core.content.ContextCompat
import com.example.d308vacationplanner.ui.alerts.AlertScheduler
import com.example.d308vacationplanner.ui.navigation.AppNavGraph
import com.example.d308vacationplanner.ui.theme.D308VacationPlannerTheme
import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("Main_deeplink", "onCreate fired with extras: ${intent?.extras}")
        super.onCreate(savedInstanceState)

        AlertScheduler.createNotificationChannel(this)


        val deepLinkVacationId = intent.getLongExtra("vacationId", -1L)
        val deepLinkExcursionId = intent.getLongExtra("excursionId", -1L)

        enableEdgeToEdge()
        setContent {
            D308VacationPlannerTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    deepLinkVacationId = deepLinkVacationId,
                    deepLinkExcursionId = deepLinkExcursionId
                )
            }

        }

    }
    override fun onNewIntent(intent: Intent?) {
        Log.d("Main_deeplink", "onNewIntent fired with extras: ${intent?.extras}")
        super.onNewIntent(intent)
        if (intent == null) return

        val vacationId = intent.getLongExtra("vacationId", -1L)
        val excursionId = intent.getLongExtra("excursionId", -1L)

        android.util.Log.d("MAIN_DEEPLINK", "onNewIntent: vacationId=$vacationId excursionId=$excursionId")

        setContent {
            val navController = rememberNavController()
            AppNavGraph(
                navController = navController,
                deepLinkVacationId = vacationId,
                deepLinkExcursionId = excursionId
            )
        }
    }

    var pendingAlertSetup: (() -> Unit)? = null
    var returnedFromNotificationSettings = false
    override fun onResume(){
        super.onResume()

        if (!returnedFromNotificationSettings) return

        val hasPermission = NotificationManagerCompat.from(this).areNotificationsEnabled()

        if (hasPermission && pendingAlertSetup != null){
            pendingAlertSetup?.invoke()
            pendingAlertSetup = null
        }
        returnedFromNotificationSettings = false
    }

}


@Composable
fun MainScreen(modifier: Modifier = Modifier, onAddVacation: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize().padding(20.dp)
    ){
        Button(onClick = onAddVacation){
            Text("Add Vacation")
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    D308VacationPlannerTheme {
        MainScreen(onAddVacation = {})
    }
}*/