package com.liweiyap.foxtrot.util

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [StripDataModel::class], version = 1)
@TypeConverters(DataConverter::class)
abstract class StripDatabase: RoomDatabase() {

    abstract fun getStripDao(): StripDao

    companion object {

        // https://stackoverflow.com/questions/45173677/invoke-operator-operator-overloading-in-kotlin
        operator fun invoke(context: Context) = instance ?: synchronized(this) {
            instance ?: createDatabase(context)
                .also { instance = it }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            StripDatabase::class.java,
            DATABASE_FILENAME
        ).build()

        @Volatile private var instance: StripDatabase? = null  // writes to volatile var are immediately made visible to other threads

        private const val DATABASE_FILENAME = "strips.db"
    }
}