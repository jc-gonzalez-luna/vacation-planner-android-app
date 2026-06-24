package com.example.d308vacationplanner.repository

import android.app.Application
import com.example.d308vacationplanner.dao.ExcursionDao
import com.example.d308vacationplanner.database.VacationDatabase
import com.example.d308vacationplanner.entities.Excursion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow

class ExcursionRepository (application: Application){

    private val excursionDao: ExcursionDao

    init {
        val db = VacationDatabase.getInstance(application)
        excursionDao = db.excursionDao()
    }
    suspend fun insert(excursion: Excursion): Long{
        return withContext(Dispatchers.IO){
            excursionDao.insert(excursion)
        }
    }
    fun getExcursionFlow(id: Long): Flow<Excursion?>{
        return excursionDao.getExcursionById(id)
    }
    suspend fun update(excursion: Excursion){
        return withContext(Dispatchers.IO){
            excursionDao.update(excursion)
        }
    }
    suspend fun delete(excursion: Excursion){
        return withContext(Dispatchers.IO){
            excursionDao.delete(excursion)
        }
    }
    fun getExcursionsForVacation(vacationId: Long): Flow<List<Excursion>> =
            excursionDao.getExcursionsForVacation(vacationId)

    suspend fun getExcursionCountForVacation(vacationId: Long): Int{
        return withContext(Dispatchers.IO){
            excursionDao.getExcursionCountForVacation(vacationId)
        }
    }
    suspend fun getTotalSpent(vacationId: Long): Double {
        return excursionDao.getTotalSpent(vacationId) ?: 0.0
    }
    fun getExcursionsByPrice(vacationId: Long): Flow<List<Excursion>>{
        return excursionDao.getExcursionsByPrice(vacationId)
    }
}