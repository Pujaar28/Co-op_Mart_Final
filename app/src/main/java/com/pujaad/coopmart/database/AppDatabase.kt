package com.pujaad.coopmart.database

import androidx.room.Database
import com.pujaad.coopmart.model.ModelDatabase
import androidx.room.RoomDatabase
import com.pujaad.coopmart.database.dao.DatabaseDao


@Database(entities = [ModelDatabase::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun databaseDao(): DatabaseDao?
}