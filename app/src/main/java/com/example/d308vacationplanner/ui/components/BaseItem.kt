package com.example.d308vacationplanner.ui.components

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Ignore

abstract class BaseItem (
    open val title: String
){
    abstract val icon: ImageVector
    abstract fun displaySummary(): String
}
