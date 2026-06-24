package com.example.d308vacationplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.entities.Vacation
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationReportScreen (
    vacation: Vacation,
    excursions: List<Excursion>,
    onBack: () -> Unit
){
    val currency = NumberFormat.getCurrencyInstance()
    val excursionTotal = excursions.sumOf { it.price }
    val remaining = vacation.budget - vacation.hotelCost - excursionTotal

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
            ){
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlightTakeoff,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Vacation Report",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        }
    ){ padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Vacation Summary", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            Text("Title: ${vacation.title}")
            Text("Hotel: ${vacation.hotel}")
            Text("Dates: ${vacation.startDate} -> ${vacation.endDate}")
            Text("Budget: ${currency.format(vacation.budget)}")

            Spacer(Modifier.height(24.dp))

            Text("Excursions", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            excursions.forEach {
                Text("• ${it.title} - ${currency.format(excursionTotal)}")
            }

            Spacer(Modifier.height(8.dp))
            Text("Total Excursion Cost: ${currency.format(excursionTotal)}")

            Spacer(Modifier.height(24.dp))

            Text("Budget breakdown", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            Text("Hotel Cost: ${currency.format(vacation.hotelCost)}")
            Text("Excursions: ${currency.format(excursionTotal)}")
            Text("Remaining: ${currency.format(remaining)}")
        }

    }
}