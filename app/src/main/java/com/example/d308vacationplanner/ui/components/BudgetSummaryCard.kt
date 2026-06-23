package com.example.d308vacationplanner.ui.components

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.ui.utils.DateUtils
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetSummaryCard (
    budget: Double,
    hotelCost: Double,
    totalSpent: Double,
    durationDays: Long,
    daysUntilTrip: Long

){
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val totalTripCost = hotelCost + totalSpent
    val remaining = budget - totalTripCost

    val progress = when {
        budget <= 0 -> 0f
        totalSpent <= 0 -> 0f
        totalSpent >= budget -> 1f
        else -> (totalSpent/budget).toFloat()
    }
    val progressColor = when {
        progress >= 1f -> MaterialTheme.colorScheme.error
        progress >= 0.8f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(2.dp, progressColor)
    ){
        Column(modifier = Modifier.padding(16.dp)){

            Text("Vacation Budget", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Budget: ${currencyFormatter.format(budget)}")
            Text("Hotel Cost: ${currencyFormatter.format(hotelCost)}")
            Text("Excursion Cost: ${currencyFormatter.format(totalSpent)}")
            Text("Total Trip Cost: ${currencyFormatter.format(totalTripCost)}")
            Text("Remaining: ${currencyFormatter.format(remaining)}",
                color = progressColor)

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress,
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Trip Duration: $durationDays days",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Days Until Trip: ${
                    if (daysUntilTrip < 0) "Trip has ended"
                    else if (daysUntilTrip == 0L) "Starts today"
                    else "$daysUntilTrip days"
                }",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}