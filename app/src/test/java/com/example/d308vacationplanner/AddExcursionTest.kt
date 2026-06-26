package com.example.d308vacationplanner

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.d308vacationplanner.dao.ExcursionDao
import com.example.d308vacationplanner.dao.VacationDao
import com.example.d308vacationplanner.database.VacationDatabase
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.entities.Vacation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@ExperimentalCoroutinesApi
class AddExcursionTest {

    private lateinit var db: VacationDatabase
    private lateinit var vacationDao: VacationDao
    private lateinit var excursionDao: ExcursionDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            VacationDatabase::class.java
        ).allowMainThreadQueries().build()

        vacationDao = db.vacationDao()
        excursionDao = db.excursionDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun addExcursion_savesToCorrectVacation() = runTest {
        val vacationId = vacationDao.insert(
            Vacation(
                id = 0,
                title = "Italy Trip",
                hotel = "Hilton",
                hotelCost = 150.0,
                startDate = "07/01/2026",
                endDate = "08/01/2026",
                budget = 3000.0,
                reminderDays = listOf(1,3),
                isFavorite = false
            )
        )

        val excursion = Excursion(
            id = 0,
            vacationID = vacationId,
            title = "Wine Tasting",
            date = "07/03/2026",
            price = 120.0,
            notes = "Bring ID"
        )

        excursionDao.insert(excursion)

        val excursions = excursionDao.getExcursionsForVacation(vacationId).first()

        assertEquals(1, excursions.size)
        assertEquals("Wine Tasting", excursions[0].title)
        assertEquals(vacationId, excursions[0].vacationID)
    }
}