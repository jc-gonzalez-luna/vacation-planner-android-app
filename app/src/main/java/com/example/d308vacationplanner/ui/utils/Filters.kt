package com.example.d308vacationplanner.ui.utils

import com.example.d308vacationplanner.entities.Excursion
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
    fun filterExcursions(
        excursions: List<Excursion>,
        query: String
    ): List<Excursion> {
        if (query.isBlank()) return excursions

        return excursions.filter {excursion ->
            excursion.title.contains(query, ignoreCase = true)
        }
    }
}