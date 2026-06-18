package com.example.d308vacationplanner.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.entities.Vacation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationListScreen (
    vacations: List<Vacation>,
    onAddClick: () -> Unit,
    onVacationClick: (Long) -> Unit
){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Vacation Planner",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(vacations) { vacation ->
                VacationListItem(
                    vacation = vacation,
                    onClick = { onVacationClick(vacation.id)}
                )
            }
        }
    }
}

@Composable
fun VacationListItem(vacation: Vacation, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ){
        Text(vacation.title, style = MaterialTheme.typography.titleLarge)
        Text("${vacation.startDate} -> ${vacation.endDate}")
        Text(vacation.hotel)
    }
}