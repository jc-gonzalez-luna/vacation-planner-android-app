package com.example.d308vacationplanner.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.entities.Vacation
import com.example.d308vacationplanner.ui.components.VacationListItem
import com.example.d308vacationplanner.ui.utils.Filters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationListScreen (
    vacations: List<Vacation>,
    onAddClick: () -> Unit,
    onVacationClick: (Long) -> Unit
) {
    var vacationSearch by remember { mutableStateOf("") }

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
        val filteredVacations =
            if (vacationSearch.isBlank()) vacations
            else Filters.filterVacations(vacations, vacationSearch)
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = vacationSearch,
                    onValueChange = { vacationSearch = it },
                    label = { Text("Search vacations") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                )
            }
            if (filteredVacations.isEmpty()) {
                item {
                    Text(
                        text = "No vacations found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            items(filteredVacations) { vacation ->
                VacationListItem(
                    vacation = vacation,
                    onClick = { onVacationClick(vacation.id) }
                )
                Divider()
            }
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}