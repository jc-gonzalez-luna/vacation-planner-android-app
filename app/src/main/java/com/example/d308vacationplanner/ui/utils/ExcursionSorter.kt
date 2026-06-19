package com.example.d308vacationplanner.ui.utils

import com.example.d308vacationplanner.entities.Excursion

object ExcursionSorter {

    fun sort(
        list: List<Excursion>,
        option: String,
        asc: Boolean
    ): List<Excursion>{
        val sorted = when (option) {
            "Price" -> list.sortedBy { it.price}
            "Title" -> list.sortedBy { it.title.lowercase() }
            else -> list.sortedBy { it.date }
        }
        return if (asc) sorted else sorted.reversed()
    }
}