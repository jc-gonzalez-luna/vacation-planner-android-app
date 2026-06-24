package com.example.d308vacationplanner.entities

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.d308vacationplanner.ui.components.BaseItem

@Entity(tableName = "vacations")
data class Vacation (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    override val title: String,
    val hotel: String,
    val startDate: String,
    val endDate: String,
    val budget: Double = 0.0,
    val hotelCost: Double = 0.0,
    val reminderDays: List<Int> = emptyList(),
    val isFavorite: Boolean = false

    ): BaseItem(title){

    @Ignore
    override val icon: ImageVector = Icons.Default.FlightTakeoff

    override fun displaySummary(): String {
        return "Vacation: $title ($startDate -> $endDate"
    }
}

