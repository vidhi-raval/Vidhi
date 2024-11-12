package com.example.apicallingdemo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apicallingdemo.apiCalling.ApiClient
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.model.RepositoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepositoryViewModel: ViewModel() {
    private val _repositories = MutableLiveData<List<Repository>>()
    val repositories: LiveData<List<Repository>> get() = _repositories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchRepositories() {
        if (_isLoading.value == true) return
        _isLoading.value = true
        ApiClient.apiService.getTrendingRepositories().enqueue(object :
            Callback<RepositoryResponse> {
            override fun onResponse(call: Call<RepositoryResponse>, response: Response<RepositoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _repositories.value = response.body()?.items ?: emptyList()

                } else {
                    _error.value = "Error fetching data"
                }
            }

            override fun onFailure(call: Call<RepositoryResponse>, t: Throwable) {
                _isLoading.value = false
                _error.value = t.message
            }
        })
    }
}