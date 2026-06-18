package com.example.d308vacationplanner.database

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.example.d308vacationplanner.dao.ExcursionDao
import com.example.d308vacationplanner.dao.VacationDao
import com.example.d308vacationplanner.entities.Excursion
import com.example.d308vacationplanner.entities.Vacation

@Database(
        entities = [Vacation::class, Excursion::class],
        version = 4,
        exportSchema = false
)
abstract class VacationDatabase : RoomDatabase (){

    abstract fun vacationDao(): VacationDao
    abstract fun excursionDao(): ExcursionDao

    companion object{
        @Volatile
        private var INSTANCE: VacationDatabase? = null

        fun getInstance(context: Context): VacationDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VacationDatabase::class.java,
                    "vacations_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}
