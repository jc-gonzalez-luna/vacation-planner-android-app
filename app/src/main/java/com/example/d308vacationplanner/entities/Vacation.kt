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
    val endDate: String

    )

