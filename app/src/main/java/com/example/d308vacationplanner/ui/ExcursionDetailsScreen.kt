package com.example.d308vacationplanner.ui

import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.entities.Vacation
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcursionDetailsScreen (
    excursion: Excursion?,
    vacation: Vacation,
    vacationId: Long,
    onSave: (Excursion) ->  Unit,
    onDelete: (Excursion) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Excursion Details",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            LaunchedEffect(excursion?.id) {
                if (excursion != null) {
                    title = excursion?.title ?: ""
                    date = excursion?.date ?: ""
                } else {
                    title = ""
                    date = ""
                }
            }
            val context = LocalContext.current

            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Excursion Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Excursion Date (MM/DD/YYYY)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                Button(onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(
                            context,
                            "Excursion title cannot be empty",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return@Button
                    }

                    val dateRegex = Regex("""\d{2}/\d{2}/\d{4}""")
                    if (!dateRegex.matches(date)) {
                        Toast.makeText(
                            context, "Date must be in MM/DD/YYYY format (e.g., 01/25/2026)",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
                    val excursionDate: LocalDate

                    try {
                        excursionDate = LocalDate.parse(date, formatter)
                    } catch (_: Exception) {
                        Toast.makeText(
                            context,
                            "Invalid date. Please enter a real calendar date.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    val vacationStart = LocalDate.parse(vacation.startDate, formatter)
                    val vacationEnd = LocalDate.parse(vacation.endDate, formatter)

                    if (excursionDate.isBefore(vacationStart) || excursionDate.isAfter(vacationEnd)) {
                        Toast.makeText(
                            context,
                            "Excursion date must fall within the vacation dates (${vacation.startDate} to ${vacation.endDate})",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    val updated = Excursion(
                        id = excursion?.id ?: 0,
                        vacationID = vacationId,
                        title = title,
                        date = date
                    )
                    onSave(updated)
                }) { Text("Save") }
                if (excursion != null) {
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onDelete(excursion) }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}