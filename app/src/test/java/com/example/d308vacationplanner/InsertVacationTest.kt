package com.example.d308vacationplanner

import androidx.room.Room
import com.example.d308vacationplanner.dao.VacationDao
import com.example.d308vacationplanner.database.VacationDatabase
import com.example.d308vacationplanner.entities.Vacation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Before
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@ExperimentalCoroutinesApi
class InsertVacationTest {

    private lateinit var db: VacationDatabase
    private lateinit var dao: VacationDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VacationDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.vacationDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertVacation_savesCorrectly() = runTest {
        val vacation = Vacation(
            id = 0,
            title = "Test Trip",
            hotel = "Test Hotel",
            hotelCost = 200.0,
            startDate = "07/11/2026",
            endDate = "08/11/2026",
            budget = 1000.0,
            reminderDays = listOf(1,3,5),
            isFavorite = false
        )

        dao.insert(vacation)
        val result = dao.getAll().first()

        assertTrue(result.isNotEmpty())
        assertEquals("Test Trip", result[0].title)
    }
}