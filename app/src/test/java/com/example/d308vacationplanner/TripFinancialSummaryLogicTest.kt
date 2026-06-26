package com.example.d308vacationplanner

import org.junit.Test
import kotlin.test.assertEquals


class TripFinancialSummaryLogicTest {

    @Test
    fun tripFinancialSummary_calculatesCorrectValues(){
        val budget = 1000.0
        val hotelCost = 300.0
        val totalSpent = 200.0

        val totalTripCost = hotelCost + totalSpent
        val remaining = budget - totalTripCost

        assertEquals(500.0, remaining, 0.001)
        assertEquals(500.0, totalTripCost, 0.001)
    }

    @Test
    fun tripFinancialSummary_progressCalculation_isCorrect(){
        val budget = 1000.0
        val totalSpent = 400.0

        val progress = when {
            budget <= 0 -> 0f
            totalSpent <= 0 -> 0f
            totalSpent >= budget -> 1f
            else -> (totalSpent/budget).toFloat()
        }
        assertEquals(0.4f, progress)
    }

    @Test
    fun tripFinancialSummary_daysUntilTrip_isCorrect(){
        val today = 100L
        val tripStart = 110L

        val daysUntilTrip = tripStart - today

        assertEquals(10L, daysUntilTrip)
    }

    @Test
    fun tripFinancialSummary_durationDays_isCorrect(){
        val start = 100L
        val end = 105L

        val durationDays = end - start

        assertEquals(5L, durationDays)
    }
}