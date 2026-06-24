package com.example.d308vacationplanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.ui.theme.D308VacationPlannerTheme

@Composable
fun AlertReminderSection (
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ){
        Column(modifier = Modifier.padding(16.dp)){
            Text(
                text = "Alert Reminders",
                style = MaterialTheme.typography.titleMedium
            )
            Divider(Modifier.padding(vertical = 8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..7).forEach { day ->
                    FilterChip(
                        selected = selectedDays.contains(day),
                        onClick = { onDayToggle(day)},
                        label = { Text("$day days before")},
                        leadingIcon = {
                            Icon(
                                imageVector = if (selectedDays.contains(day))
                                    Icons.Default.NotificationsActive
                                else
                                    Icons.Default.NotificationsNone,
                                contentDescription = null
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Selected reminders will trigger notification before your trip.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewAlertReminderSelection() {
    D308VacationPlannerTheme {
        AlertReminderSection(
            selectedDays = setOf(1, 3, 7),
            onDayToggle = {}
        )
    }
}