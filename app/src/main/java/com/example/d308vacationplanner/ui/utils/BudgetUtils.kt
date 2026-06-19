package com.example.d308vacationplanner.ui.utils

object BudgetUtils {

    fun parseBudget(input: String): Double {
        val cleaned = input
            .replace("$", "")
            .replace(",", "")
            .trim()
            .let { if(it.startsWith(".")) "0$it" else it}
        return cleaned.toDoubleOrNull() ?: 0.0
    }
}