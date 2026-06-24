package com.example.d308vacationplanner.entities

import android.icu.text.CaseMap
import android.icu.text.NumberFormat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.d308vacationplanner.ui.components.BaseItem

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
    override val title: String,
    val date: String,
    val price: Double,
    val notes: String = ""
): BaseItem(title){

    @Ignore
    override val icon: ImageVector = Icons.Default.AttachMoney

    override fun displaySummary(): String {
        val currency = NumberFormat.getCurrencyInstance()
        return "Excursion: $title - ${currency.format(price)}"
    }
}


