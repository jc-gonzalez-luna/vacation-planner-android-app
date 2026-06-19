package com.example.d308vacationplanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.entities.Excursion
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExcursionListItem (
    excursion: Excursion,
    onClick: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ){
        Text(excursion.title, style = MaterialTheme.typography.titleMedium)
        Text(excursion.date, style = MaterialTheme.typography.bodyMedium)
        Text(
            currencyFormatter.format(excursion.price),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}