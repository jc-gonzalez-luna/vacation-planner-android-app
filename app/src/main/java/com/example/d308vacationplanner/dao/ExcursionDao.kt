package com.example.d308vacationplanner.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.d308vacationplanner.entities.Excursion


@Dao
interface ExcursionDao {

    @Insert
    suspend fun insert(excursion: Excursion): Long


    @Update
    suspend fun update(excursion: Excursion)

    @Delete
    suspend fun delete(excursion: Excursion)

    @Query("SELECT * FROM excursions WHERE vacationID = :vacationID")
    fun getExcursionsForVacation(vacationID: Long): Flow<List<Excursion>>

    @Query("SELECT COUNT(*) FROM excursions WHERE vacationID = :vacationID")
    suspend fun getExcursionCountForVacation(vacationID: Long): Int

    @Query("SELECT * FROM excursions WHERE id = :id LIMIT 1")
    fun getExcursionById(id: Long): Flow<Excursion?>

    @Query("SELECT SUM(price) FROM excursions WHERE vacationID = :vacationID")
    suspend fun getTotalSpent(vacationID: Long): Double?

    @Query("SELECT * FROM excursions WHERE vacationID = :vacationID ORDER BY price DESC")
    fun getExcursionsByPrice(vacationID: Long): Flow<List<Excursion>>
}

