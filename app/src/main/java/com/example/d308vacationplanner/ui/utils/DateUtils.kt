package com.example.d308vacationplanner.ui.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val regex = Regex("""\d{2}/\d{2}/\d{4}""")
    private val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")

    fun isValidFormat(date: String): Boolean =
        regex.matches(date)

    fun parse(date: String): LocalDate? =
        try {
            LocalDate.parse(date, formatter)
        } catch (_: Exception){
            null
        }
    fun isEndAfterStart(start: LocalDate, end: LocalDate): Boolean =
        !end.isBefore(start)

    fun daysBetween(start: String, end: String): Long {
        val startDate = LocalDate.parse(start, formatter)
        val endDate = LocalDate.parse(end, formatter)
        return ChronoUnit.DAYS.between(startDate, endDate)
    }
    fun daysUntil(date: String): Long {
        val target = LocalDate.parse(date, formatter)
        val today = LocalDate.now()
        return ChronoUnit.DAYS.between(today, target)
    }
    fun daysBefore(date: String, days: Int): String {
        val parsed = LocalDate.parse(date, formatter)
        val result = parsed.minusDays(days.toLong())
        return result.format(formatter)
    }
}