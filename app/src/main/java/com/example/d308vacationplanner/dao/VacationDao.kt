package com.example.d308vacationplanner.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.d308vacationplanner.entities.Vacation

@Dao
interface VacationDao {
    @Query("SELECT * FROM vacations ORDER BY id ASC")
    fun getAll(): Flow<List<Vacation>>

    @Insert
    suspend fun insert(vacation: Vacation): Long

    @Query("SELECT * FROM vacations WHERE id = :id LIMIT 1")
    fun getVacationById(id: Long): Flow<Vacation?>


    @Update
    suspend fun update(vacation: Vacation)

    @Delete
    suspend fun  delete(vacation: Vacation)
}
