package com.example.d308vacationplanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.entities.Vacation

@Composable
fun VacationListItem (
    vacation: Vacation,
    onClick: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(vacation.title, style = MaterialTheme.typography.titleLarge)
        Text("${vacation.startDate} -> ${vacation.endDate}")
        Text(vacation.hotel, style = MaterialTheme.typography.bodyMedium)
    }
}