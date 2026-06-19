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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.entities.Vacation
import com.example.d308vacationplanner.ui.viewmodel.VacationViewModel
import com.example.d308vacationplanner.ui.components.BudgetSummaryCard
import com.example.d308vacationplanner.ui.components.ExcursionListItem
import com.example.d308vacationplanner.ui.components.SortDropdown
import com.example.d308vacationplanner.ui.utils.BudgetUtils
import com.example.d308vacationplanner.ui.utils.DateUtils
import com.example.d308vacationplanner.ui.utils.ExcursionSorter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacationDetailsScreen (
    vacation: Vacation?,
    excursions: List<Excursion>,
    onSave: (Vacation) -> Unit,
    onDelete: (Vacation) -> Unit,
    onAddExcursion: () -> Unit,
    onEditExcursion: (Long) -> Unit,
    onSetAlerts: (Vacation) -> Unit
) {
    val viewModel: VacationViewModel = viewModel()
    val totalSpent = excursions.sumOf { it.price }
    val context = LocalContext.current

    var title by remember { mutableStateOf(vacation?.title ?: "") }
    var hotel by remember { mutableStateOf(vacation?.hotel ?: "") }
    var startDate by remember { mutableStateOf(vacation?.startDate ?: "") }
    var endDate by remember { mutableStateOf(vacation?.endDate ?: "") }
    var budget by remember { mutableStateOf(vacation?.budget?.toString() ?: "") }
    var sortOption by remember { mutableStateOf("Date") }
    var sortAscending by remember { mutableStateOf(true) }

    val budgetValue = budget.toDoubleOrNull() ?: 0.0

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
                        totalSpent = totalSpent
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
                        val start = DateUtils.parse(startDate)
                        val end = DateUtils.parse(endDate)

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
                            startDate = startDate,
                            endDate = endDate,
                            budget = budgetValue
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
                            } else {
                                onDelete(vacation)
                            }
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
                    Button(onClick = { onSetAlerts(vacation) }) {
                        Text("Set Alerts")
                    }
                }
            }
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

            val sortedExcursions = ExcursionSorter.sort(excursions, sortOption, sortAscending)

            items(sortedExcursions) { excursion ->
                ExcursionListItem(excursion) {
                    onEditExcursion(excursion.id)
                }
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
