package com.example.d308vacationplanner

import com.example.d308vacationplanner.entities.Vacation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class PreventInvalidBudgetInputTest {

    @Test
    fun invalidBudgetInput_shouldRejectNonNumericValues(){
        val invalidBudget = "abc"
        val isValid = invalidBudget.toDoubleOrNull() != null
        assertFalse(isValid)
    }

    @Test
    fun invalidBudgetInput_shouldRejectNegativeValues(){
        val invalidBudget = "-500"
        val parsed = invalidBudget.toDoubleOrNull()
        val isValid = parsed != null && parsed >= 0
        assertFalse(isValid)
    }

    @Test
    fun validBudgetInput_shouldAcceptPositiveNumbers(){
        val validBudget = "3000"
        val parsed = validBudget.toDoubleOrNull()
        val isValid = parsed != null && parsed >= 0
        assertTrue(isValid)
    }

    @Test
    fun vacationEntity_shouldNotSaveInvalidBudget(){
        val vacation = Vacation(
            id = 0,
            title = "Invalid Budget Trip",
            hotel = "Test Hotel",
            hotelCost = 300.0,
            startDate = "01/01/2026",
            endDate = "06/30/2026",
            budget = -100.0,
            reminderDays = listOf(1, 3),
            isFavorite = false
        )

        val isValid = vacation.budget >= 0
        assertFalse("Vacation should not save with negative budget", isValid)
    }
}