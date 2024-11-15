package com.example.apicallingdemo.database

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.example.apicallingdemo.model.Contributors
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.utils.Converters


@Database(entities = [Repository::class, Contributors::class], version = 1)
@TypeConverters(Converters::class)
abstract class RepositoryDatabase: RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao

    companion object {
        private var instance: RepositoryDatabase? = null

        @Synchronized
        fun getInstance(context: Context): RepositoryDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(
                    context.applicationContext, RepositoryDatabase::class.java,
                    "RepositoryDatabase")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!

        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                populateDatabase(instance!!)
            }
        }

        private fun populateDatabase(db: RepositoryDatabase) {
            val repositoryDao = db.repositoryDao()
        }
    }


}