package com.example.d308vacationplanner.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import com.example.d308vacationplanner.ui.navigation.AppNavGraph
import com.example.d308vacationplanner.ui.theme.D308VacationPlannerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        enableEdgeToEdge()
        setContent {
            D308VacationPlannerTheme {
                val navController = androidx.navigation.compose.rememberNavController()
                AppNavGraph(navController = navController)
            }
        }

    }
    private fun createNotificationChannel() {
            val channel = NotificationChannel(
                "vacation_channel",
                "Vacation Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

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

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    D308VacationPlannerTheme {
        MainScreen(onAddVacation = {})
    }
}