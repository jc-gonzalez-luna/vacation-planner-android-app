package com.example.d308vacationplanner.ui

import android.util.Log.e
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.entities.Vacation
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    var title by remember { mutableStateOf(vacation?.title ?: "") }
    var hotel by remember { mutableStateOf(vacation?.hotel ?: "") }
    var startDate by remember { mutableStateOf(vacation?.startDate ?: "") }
    var endDate by remember { mutableStateOf(vacation?.endDate ?: "") }

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
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = hotel,
                onValueChange = { hotel = it },
                label = { Text("Hotel") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Start Date (MM/DD/YYYY)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = endDate,
                onValueChange = { endDate = it },
                label = { Text("End Date (MM/DD/YYYY)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Row {
                val context = LocalContext.current
                Button(onClick = {
                    if(title.isBlank() || hotel.isBlank() || startDate.isBlank() || endDate.isBlank()){
                        Toast.makeText(
                            context,
                            "All fields must be filled before saving a vacation.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }

                    val dateRegex = Regex("""\d{2}/\d{2}/\d{4}""")

                    if (!dateRegex.matches(startDate) || !dateRegex.matches(endDate)) {
                        Toast.makeText(
                            context, "Dates must be in MM/DD/YYYY format (e.g., 01/25/2026)",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                    val start: LocalDate
                    val end: LocalDate

                    try {
                        start = LocalDate.parse(startDate, formatter)
                        end = LocalDate.parse(endDate, formatter)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context, "Invalid date. Please enter a real calendar date.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    if (end.isBefore(start)) {
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
                        endDate = endDate
                    )
                    onSave(updated)
                }) {
                    Text("Save")
                }
                Spacer(Modifier.width(8.dp))

                if (vacation != null) {

                    val context = LocalContext.current

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
            Spacer(Modifier.height(16.dp))
            if (vacation != null) {
                val context = LocalContext.current

                Button(onClick = { viewModel.shareVacation(context, vacation) }) {
                    Text("Share")
                }
                Spacer(Modifier.height(8.dp))

                Button(onClick = { onSetAlerts(vacation) }) {
                    Text("Set Alerts")
                }
            }
            Spacer(Modifier.height(24.dp))

            Text("Excursions", style = MaterialTheme.typography.titleLarge)

            LazyColumn {
                items(excursions) { excursion ->
                    Text(
                        text = "${excursion.title} - ${excursion.date}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditExcursion(excursion.id) }
                            .padding(8.dp)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            val context = LocalContext.current
            Button(onClick = {
                if(vacation == null || vacation.id ==0L){
                    Toast.makeText(
                        context,
                        "Please save the vacation before adding excursions.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    onAddExcursion()
                }

            }) {
                Text("Add Excursion")
            }
        }
    }
}