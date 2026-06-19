package com.example.d308vacationplanner.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BudgetSummaryCard (
    budget: Double,
    totalSpent: Double,
    modifier: Modifier = Modifier
){
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
    val remaining = budget - totalSpent

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
        elevation = CardDefaults.cardElevation(4.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)){

            Text("Vacation Budget", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Budget: ${currencyFormatter.format(budget)}")
            Text("Spent: ${currencyFormatter.format(totalSpent)}")
            Text("Remaining: ${currencyFormatter.format(remaining)}")

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress,
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth().height(8.dp)
            )
        }
    }
}