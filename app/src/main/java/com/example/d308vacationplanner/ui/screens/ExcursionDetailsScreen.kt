package com.example.d308vacationplanner.ui.screens

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
import com.example.d308vacationplanner.ui.alerts.AlertScheduler
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcursionDetailsScreen (
    excursion: Excursion?,
    vacation: Vacation,
    vacationId: Long,
    totalSpent: Double,
    onSave: (Excursion) ->  Unit,
    onDelete: (Excursion) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }
    var price by rememberSaveable{ mutableStateOf(excursion?.price?.toString() ?: "") }
    var notes by rememberSaveable { mutableStateOf(excursion?.notes ?: "")}

    val context = LocalContext.current

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
                    title = excursion.title
                    date = excursion.date

                    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
                    price = currencyFormatter.format(excursion.price)
                    notes = excursion.notes ?: ""
                } else {
                    title = ""
                    date = ""
                    price = ""
                    notes = ""
                }
            }

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

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price")},
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it},
                    label = { Text("Notes (optional")},
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                val previewDate = excursion?.let {
                    it.date
                }
                previewDate?.let {
                    Text("- Alert will fire on: $it")
                }
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
                    val normalizedPrice = price
                        .replace("$", "")
                        .replace(",", "")
                        .trim()
                        .let { if (it.startsWith(".")) "0$it" else it }

                    val priceValue = normalizedPrice.toDoubleOrNull()
                    if(priceValue == null){
                        Toast.makeText(context, "Price must be a valid number", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (priceValue < 0){
                        Toast.makeText(
                            context,
                            "Price cannot be negative",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    val oldPrice = excursion?.price ?: 0.0
                    val adjustedTotal = totalSpent - oldPrice
                    val newTotal = adjustedTotal + priceValue
                    if (newTotal > vacation.budget){
                        Toast.makeText(
                            context,
                            "Cannot save excursion. This would exceed the vacation budget.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@Button
                    }
                    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)
                    val formattedPrice = currencyFormatter.format(priceValue)
                    price = formattedPrice

                    val updated = Excursion(
                        id = excursion?.id ?: 0,
                        vacationID = vacationId,
                        title = title,
                        date = date,
                        price = priceValue,
                        notes = notes
                    )
                    onSave(updated)
                }) { Text("Save") }
                if (excursion != null) {
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        AlertScheduler.cancelAlert(context, excursion.id.toInt())
                        onDelete(excursion)
                    }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}