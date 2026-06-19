package com.example.d308vacationplanner.ui.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
}