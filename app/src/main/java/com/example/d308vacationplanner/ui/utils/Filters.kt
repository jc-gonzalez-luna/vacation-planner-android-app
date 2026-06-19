package com.example.d308vacationplanner.ui.utils

import com.example.d308vacationplanner.entities.Vacation

object Filters {
    fun filterVacations(
        vacations: List<Vacation>,
        query: String
    ): List<Vacation> {
        if (query.isBlank()) return vacations

        return vacations.filter {vacation ->
            vacation.title.contains(query, ignoreCase = true) ||
                    vacation.hotel.contains(query, ignoreCase = true)
        }
    }
}