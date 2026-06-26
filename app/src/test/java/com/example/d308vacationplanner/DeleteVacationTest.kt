package com.example.d308vacationplanner

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.d308vacationplanner.dao.VacationDao
import com.example.d308vacationplanner.database.VacationDatabase
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
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@ExperimentalCoroutinesApi
class DeleteVacationTest {

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
    fun teardown(){
        db.close()
    }

    @Test
    fun deleteVacation_removesEntryWhenNoExcursionExist() = runTest {
        val vacationId = dao.insert(
            Vacation(
                id = 0,
                title = "Delete Trip",
                hotel = "Test Hotel",
                hotelCost = 200.0,
                startDate = "01/01/2026",
                endDate = "06/30/2026",
                budget = 3000.0,
                reminderDays = listOf(),
                isFavorite = false
            )
        )

        val vacation = dao.getVacationById(vacationId).first()!!

        dao.delete(vacation)

        val result = dao.getAll().first()
        assertTrue { result.isEmpty() }
    }
}