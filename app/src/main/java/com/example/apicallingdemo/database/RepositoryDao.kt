package com.example.apicallingdemo.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.apicallingdemo.model.Contributors
import com.example.apicallingdemo.model.Repository

@Dao
interface RepositoryDao {

    @Query("SELECT * FROM Repository ORDER BY stargazers_count DESC")
     fun getAllRepositories(): LiveData<List<Repository>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAll(repositories: List<Repository>)

    @Query("DELETE FROM Repository")
     fun deleteAll()

    @Query("SELECT * FROM contributors WHERE repoId = :repoId")
    suspend fun getContributorsForRepo(repoId: Int): List<Contributors>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContributor(contributor: Contributors)
}