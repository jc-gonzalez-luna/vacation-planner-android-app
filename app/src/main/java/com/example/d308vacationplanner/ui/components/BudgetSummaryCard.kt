package com.example.d308vacationplanner.ui.components

import android.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
        else -> (totalTripCost/budget).toFloat()
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "budgetProgress"
    )
    val progressColor = when {
        progress >= 1f -> MaterialTheme.colorScheme.error
        progress >= 0.8f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    val percentUsed = (progress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(2.dp, progressColor)
    ){
        Column(modifier = Modifier.padding(16.dp)){

            Text("Vacation Budget", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.width(6.dp))
                Text("Budget: ${currencyFormatter.format(budget)}",
                    color = MaterialTheme.colorScheme.onSurface)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.width(6.dp))
                Text("Hotel Cost: ${currencyFormatter.format(hotelCost)}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.width(6.dp))
                Text("Excursion Cost: ${currencyFormatter.format(totalSpent)}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.width(6.dp))
                Text("Total Trip Cost: ${currencyFormatter.format(totalTripCost)}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
                Spacer(Modifier.width(6.dp))
                Text("Remaining: ${currencyFormatter.format(remaining)}",
                    color = if (remaining < 0) MaterialTheme.colorScheme.error else progressColor
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Budget Usage: $percentUsed%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = animatedProgress,
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Trip Duration: $durationDays days",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Days Until Trip: ${
                        when {
                            daysUntilTrip < 0 -> "Trip has ended"
                            daysUntilTrip == 0L -> "Starts today"
                            else -> "$daysUntilTrip days"
                        }
                    }",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

        }
    }
}