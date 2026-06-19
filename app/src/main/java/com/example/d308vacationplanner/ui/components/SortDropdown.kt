package com.example.d308vacationplanner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SortDropdown (current: String, onSelect: (String) -> Unit){

    var expanded by remember { mutableStateOf(false) }


    Box {
        Text(
            text = "Sort: $current",
            modifier = Modifier
                .clickable { expanded = true}
                .padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false}
        ) {
            DropdownMenuItem(
                text = { Text("Date")},
                onClick = {
                    onSelect("Date")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Price")},
                onClick = {
                    onSelect("Price")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Title")},
                onClick = {
                    onSelect("Title")
                    expanded = false
                }
            )
        }
    }
}