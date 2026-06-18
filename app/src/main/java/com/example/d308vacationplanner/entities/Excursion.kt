package com.example.d308vacationplanner.entities

import android.icu.text.CaseMap
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
        tableName = "excursions",
        foreignKeys = [ForeignKey(
                entity = Vacation::class,
                parentColumns = ["id"],
                childColumns = ["vacationID"],
                onDelete = ForeignKey.RESTRICT
                )
            ],
        indices = [Index("vacationID")]
)
data class Excursion (

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vacationID: Long,
    val title: String,
    val date: String
)

