package com.example.d308vacationplanner.ui.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun formatVacationDates(
    start: String,
    end: String
): String {
    val input = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    val output = DateTimeFormatter.ofPattern("MMM d")

    val startDate = LocalDate.parse(start, input)
    val endDate = LocalDate.parse(end,input)

    val nights = ChronoUnit.DAYS.between(startDate,endDate)

    return "${startDate.format(output)} - ${endDate.format(output)} • ${nights} nights"
}