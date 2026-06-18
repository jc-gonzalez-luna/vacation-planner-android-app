package com.example.d308vacationplanner.repository

import android.app.Application

import com.example.d308vacationplanner.dao.ExcursionDao
import com.example.d308vacationplanner.dao.VacationDao
import com.example.d308vacationplanner.database.VacationDatabase
import com.example.d308vacationplanner.entities.Vacation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow


class VacationRepository (application: Application) {

    private val vacationDao: VacationDao
    private val excursionDao: ExcursionDao

    init {
        val db = VacationDatabase.getInstance(application)
        vacationDao = db.vacationDao()
        excursionDao = db.excursionDao()
    }
    suspend fun insert(vacation: Vacation): Long {
        return withContext(Dispatchers.IO){
            vacationDao.insert(vacation)
        }
    }
    fun getAllVacations(): Flow<List<Vacation>>{
        return vacationDao.getAll()
    }
    fun getVacationFlow(id: Long): Flow<Vacation?>{
        return vacationDao.getVacationById(id)
    }
    suspend fun updateVacation(vacation: Vacation){
        withContext(Dispatchers.IO){
            vacationDao.update(vacation)
        }
    }
    suspend fun deleteVacation(vacation: Vacation, callback: DeleteCallback){
        withContext(Dispatchers.IO){
            val count = excursionDao.getExcursionCountForVacation(vacation.id)
            if(count > 0){
                callback.onDeleteFailed("Cannot delete vacation with excursions")
            } else {
                vacationDao.delete(vacation)
                callback.onDeleteSuccess()
            }
        }
    }
    interface DeleteCallback{
        fun onDeleteSuccess()
        fun onDeleteFailed(message: String)
    }
}