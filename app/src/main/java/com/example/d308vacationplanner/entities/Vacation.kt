package com.example.d308vacationplanner.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacations")
data class Vacation (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val hotel: String,
    val startDate: String,
    val endDate: String,
    val budget: Double = 0.0,
    val hotelCost: Double = 0.0,
    val reminderDays: List<Int> = emptyList()

    )

