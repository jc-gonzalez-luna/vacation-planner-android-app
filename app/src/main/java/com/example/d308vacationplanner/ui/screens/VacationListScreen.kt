package com.example.d308vacationplanner.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.d308vacationplanner.entities.Vacation
import com.example.d308vacationplanner.ui.components.VacationListItem
import com.example.d308vacationplanner.ui.components.VacationSortDropdown
import com.example.d308vacationplanner.ui.utils.Filters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationListScreen (
    vacations: List<Vacation>,
    onAddClick: () -> Unit,
    onVacationClick: (Long) -> Unit,
    onToggleFavorite: (Vacation) -> Unit
) {
    var vacationSearch by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf("A-Z") }

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
                                text = "Vacation Planner",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White
                            )
                        }
                    },
                    actions = {
                        IconToggleButton(
                            checked = showFavoritesOnly,
                            onCheckedChange = { showFavoritesOnly = it}
                        ) {
                            Icon(
                                imageVector = if (showFavoritesOnly) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Show Favorite"
                            )
                        }
                        VacationSortDropdown(
                            current = sortOption,
                            onSelect = { sortOption = it }
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        val favoriteFiltered = if (showFavoritesOnly)
            vacations.filter { it.isFavorite }
            else {
                vacations
            }


        val filteredVacations =
            if (vacationSearch.isBlank()) favoriteFiltered
            else Filters.filterVacations(favoriteFiltered, vacationSearch)

        val sortedVacation = when (sortOption){
            "A-Z" -> filteredVacations.sortedBy { it.title.lowercase() }
            "Date" -> filteredVacations.sortedBy { it.startDate }
            "Budget" -> filteredVacations.sortedBy { it.budget }
            "Favorite First" -> filteredVacations.sortedByDescending { it.isFavorite }
            else -> filteredVacations
        }

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
            items(sortedVacation) { vacation ->
                VacationListItem(
                    vacation = vacation,
                    onClick = { onVacationClick(vacation.id) },
                    onToggleFavorite = { updated -> onToggleFavorite(updated)}
                )
                Divider()
            }
            item {
                Spacer(Modifier.height(80.dp))
            }
        }
    }
}