package com.example.apicallingdemo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apicallingdemo.model.Repository

@Dao
interface RepositoryDao {

    @Query("SELECT * FROM Repository ORDER BY stargazers_count DESC")
     fun getAllRepositories(): LiveData<List<Repository>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAll(repositories: List<Repository>)

    @Query("DELETE FROM Repository")
     fun deleteAll()

    // Fetch the timestamp of the latest cached repository item
    @Query("SELECT lastFetched FROM Repository LIMIT 1")
    suspend fun getLatestTimestamp(): Long?
}