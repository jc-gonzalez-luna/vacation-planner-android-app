package com.example.d308vacationplanner.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.entities.Vacation
import com.example.d308vacationplanner.ui.alerts.AlertScheduler
import com.example.d308vacationplanner.ui.viewmodel.VacationViewModel
import com.example.d308vacationplanner.ui.components.BudgetSummaryCard
import com.example.d308vacationplanner.ui.components.ExcursionListItem
import com.example.d308vacationplanner.ui.components.SortDropdown
import com.example.d308vacationplanner.ui.utils.BudgetUtils
import com.example.d308vacationplanner.ui.utils.DateUtils
import com.example.d308vacationplanner.ui.utils.ExcursionSorter
import com.example.d308vacationplanner.ui.utils.Filters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationDetailsScreen (
    vacation: Vacation?,
    excursions: List<Excursion>,
    onSave: (Vacation) -> Unit,
    onDelete: (Vacation) -> Unit,
    onAddExcursion: () -> Unit,
    onEditExcursion: (Long) -> Unit,
    onSetAlerts: (Vacation, Set<Int>) -> Unit
) {
    val viewModel: VacationViewModel = viewModel()
    val totalSpent = excursions.sumOf { it.price }
    val durationDays =
        if (!vacation?.startDate.isNullOrBlank() && vacation.endDate.isNotBlank())
            DateUtils.daysBetween(vacation.startDate, vacation.endDate)
        else 0L
    val daysUntilTrip =
        if (!vacation?.startDate.isNullOrBlank())
            DateUtils.daysUntil(vacation.startDate)
        else 0L
    val context = LocalContext.current
    val savedDays = vacation?.reminderDays ?: emptyList()

    var title by remember { mutableStateOf(vacation?.title ?: "") }
    var hotel by remember { mutableStateOf(vacation?.hotel ?: "") }
    var hotelCost by remember { mutableStateOf(vacation?.hotelCost?.toInt()?.toString() ?: "0") }
    var startDate by remember { mutableStateOf(vacation?.startDate ?: "") }
    var endDate by remember { mutableStateOf(vacation?.endDate ?: "") }
    var budget by remember { mutableStateOf(vacation?.budget?.toInt()?.toString() ?: "0") }
    var sortOption by remember { mutableStateOf("Date") }
    var sortAscending by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(savedDays.toSet()) }


    val budgetValue = budget.toDoubleOrNull() ?: 0.0
    val start = DateUtils.parse(startDate)
    val end = DateUtils.parse(endDate)
    val hotelCostValue = hotelCost.toDoubleOrNull() ?: 0.0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Vacation Details",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = hotel,
                    onValueChange = { hotel = it },
                    label = { Text("Hotel") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = hotelCost,
                    onValueChange = { hotelCost = it },
                    label = { Text("Hotel Cost") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date (MM/DD/YYYY)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date (MM/DD/YYYY)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                if(vacation != null && vacation.id != 0L){
                    BudgetSummaryCard(
                        budget = vacation.budget,
                        hotelCost = vacation.hotelCost,
                        totalSpent = totalSpent,
                        durationDays = durationDays,
                        daysUntilTrip = daysUntilTrip
                    )
                }

            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Row {
                    Button(onClick = {
                        if (title.isBlank() || hotel.isBlank() || startDate.isBlank() || endDate.isBlank()) {
                            Toast.makeText(
                                context,
                                "All fields must be filled before saving a vacation.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        if (!DateUtils.isValidFormat(startDate) || !DateUtils.isValidFormat(endDate)) {
                            Toast.makeText(
                                context, "Dates must be in MM/DD/YYYY format (e.g., 01/25/2026)",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }


                        /*try {
                            start = LocalDate.parse(startDate, formatter)
                            end = LocalDate.parse(endDate, formatter)
                        } catch (_: Exception) {
                            Toast.makeText(
                                context, "Invalid date. Please enter a real calendar date.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }*/
                        if (start == null || end == null) {
                            Toast.makeText(context, "Invalid calendar date", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        if (!DateUtils.isEndAfterStart(start, end)) {
                            Toast.makeText(
                                context,
                                "End date must be after start date",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        val updated = Vacation(
                            id = vacation?.id ?: 0,
                            title = title,
                            hotel = hotel,
                            hotelCost = hotelCostValue,
                            startDate = startDate,
                            endDate = endDate,
                            budget = budgetValue,
                            reminderDays = selectedDays.toList()
                        )
                        onSave(updated)
                    }) {
                        Text("Save")
                    }
                    Spacer(Modifier.width(8.dp))
                    if (vacation != null) {
                        Button(onClick = {
                            if (excursions.isNotEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Cannot delete a vacation that has excursions.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }
                            AlertScheduler.cancelVacationAlerts(
                                context,
                                vacation.id,
                                selectedDays
                            )
                            onDelete(vacation)
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                if (vacation != null) {
                    Button(onClick = { viewModel.shareVacation(context, vacation) }) {
                        Text("Share")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onSetAlerts(vacation, selectedDays) }) {
                        Text("Set Alerts")
                    }
                }
            }

            item {
                val options = listOf(1,2,3,4,5,6,7)
                Column {
                    Text(
                        "Alert Reminders",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    options.forEach { day ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedDays = if (selectedDays.contains(day)) {
                                        selectedDays - day
                                    } else {
                                        selectedDays + day
                                    }
                                }
                                .padding(vertical = 1.dp)
                        ) {
                            Checkbox(
                                checked = selectedDays.contains(day),
                                onCheckedChange = { isChecked ->
                                    /*val updated = selectedDays.toMutableSet()
                                    if (isChecked){
                                        updated.add(day)
                                    }else {
                                        updated.remove(day)
                                    }
                                    selectedDays = updated*/
                                    selectedDays = if (isChecked){
                                        selectedDays + day
                                    }else{
                                        selectedDays - day
                                    }
                                }
                            )
                            Text("$day days before")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    selectedDays.sorted().forEach { day ->
                        val previewDate = vacation?.let {
                            DateUtils.daysBefore(it.startDate,day)
                        }
                        previewDate?.let {
                            Text("- $day days before -> $it")
                        }

                    }

                }
            }
            /*item {
                Text(
                    text = "Alert will fire on: $previewDate",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }*/
            item {
                Spacer(Modifier.height(24.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Excursions", style = MaterialTheme.typography.titleLarge)

                    Row {
                        SortDropdown(sortOption, onSelect = { sortOption = it }
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = if (sortAscending) "ASC" else "DESC",
                            modifier = Modifier
                                .clickable { sortAscending = !sortAscending }
                                .padding(8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            val filteredExcursions =
                if (searchQuery.isBlank()) excursions
                else Filters.filterExcursions(excursions, searchQuery)

            val sortedExcursions = ExcursionSorter.sort(filteredExcursions, sortOption, sortAscending)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search Excursions") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
            }
            if (filteredExcursions.isEmpty()){
                item {
                    Text(text = "No excursion found",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp))
                }
            }

            items(sortedExcursions) { excursion ->
                ExcursionListItem(excursion = excursion,
                    onClick = { onEditExcursion(excursion.id)}

                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                Button(onClick = {
                    if (vacation == null || vacation.id == 0L) {
                        Toast.makeText(
                            context,
                            "Please save the vacation before adding excursions.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    val budgetValue = BudgetUtils.parseBudget(budget)
                    if (totalSpent >= budgetValue) {
                        Toast.makeText(
                            context,
                            "Cannot add excursion. Budget exceeded",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    onAddExcursion()
                }) {
                    Text("Add Excursion")
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
