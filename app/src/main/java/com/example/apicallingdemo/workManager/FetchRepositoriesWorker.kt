package com.example.apicallingdemo.workManager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.apicallingdemo.apiCalling.ApiClient
import com.example.apicallingdemo.database.RepositoryDao
import com.example.apicallingdemo.database.RepositoryDatabase
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.utils.isOnline


class FetchRepositoriesWorker(private val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    private val TAG = javaClass.simpleName

    private lateinit var repoDatabase: RepositoryDatabase
    private lateinit var repoDao: RepositoryDao
    private var updatedRepoList = ArrayList<Repository>()

    override suspend fun doWork(): Result {
        repoDatabase = RepositoryDatabase.getInstance(context)
        repoDao = repoDatabase.repositoryDao()

        return if (isOnline(context)) {

            val oldRepoList = repoDao.getAllRepositories().value ?: emptyList()

            if (oldRepoList.isEmpty()) {
                if (fetchRepositories()) {
                    repoDao.insertAll(updatedRepoList)
                    Log.e(TAG, "doWork: data inserted successfully...", )
                } else {
                    Log.e(TAG, "doWork:data not available in db: Error fetching data...", )
                }
            } else {
                if (fetchRepositories()) {
                    repoDao.deleteAll()
                    repoDao.insertAll(updatedRepoList)
                    Log.e(TAG, "doWork: data updated successfully...", )
                } else {
                    Log.e(TAG, "doWork:data available in db: Error fetching data...", )
                }
            }
            Result.success()
        } else {
            Log.e(TAG, "doWork: Check internet connection...")
            Result.retry()
        }
    }

    private suspend fun fetchRepositories(): Boolean {
        return try {
            val response = ApiClient.apiService.getTrendingRepositories().execute()
            if (response.isSuccessful) {
                updatedRepoList.clear()
                updatedRepoList.addAll(response.body()?.items ?: emptyList())
                true
            } else {
                Log.e(TAG, "fetchRepositories: Error fetching data")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchRepositories: Exception occurred", e)
            false
        }
    }
}
