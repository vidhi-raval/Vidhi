package com.example.apicallingdemo.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.apicallingdemo.apiCalling.ApiClient
import com.example.apicallingdemo.database.RepositoryDao
import com.example.apicallingdemo.database.RepositoryDatabase
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.model.RepositoryResponse
import com.example.apicallingdemo.workManager.FetchRepositoriesWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class RepositoryViewModel2(application: Application): AndroidViewModel(application) {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun scheduleWork() {
        val workRequest = PeriodicWorkRequestBuilder<FetchRepositoriesWorker>(2, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(getApplication()).enqueue(workRequest)
    }

}