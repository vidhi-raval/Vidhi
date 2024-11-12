package com.example.apicallingdemo.workManager

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.apicallingdemo.apiCalling.ApiClient
import com.example.apicallingdemo.database.RepositoryDatabase
import com.example.apicallingdemo.model.Repository


class FetchRepositoriesWorker(private val context: Context, workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    override suspend fun doWork(): Result {
        _isLoading.value = true
        val service = ApiClient.apiService
        val response = service.getTrendingRepositories().execute()

                return if (response.isSuccessful) {
                    _isLoading.value = false
                    val repositories = response.body()?.items?.map {
                        Repository(
                            id = it.id,
                            node_id = it.node_id,
                            name = it.name,
                            full_name = it.full_name,
                            html_url = it.html_url,
                            description = it.description,
                            stargazers_count = it.stargazers_count,
                            watchers_count = it.watchers_count,
                            forks_count = it.forks_count,
                            watchers = it.watchers,
                            isExpanded = it.isExpanded,
                            owner = it.owner,
                            lastFetched = System.currentTimeMillis() // Current time in milliseconds
                        )
                    } ?: emptyList()

                    val repositoryDao = RepositoryDatabase.getInstance(context).repositoryDao()
                    repositoryDao.insertAll(repositories)

                    Result.success()
                } else {
                    _isLoading.value = false
                    _error.value = response.message()
                    Result.failure()
                }
    }
}