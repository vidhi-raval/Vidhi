package com.example.apicallingdemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apicallingdemo.apiCalling.ApiClientTravelSummery.apiServiceTravel
import com.example.apicallingdemo.model.TravelSummaryResponse
import com.example.apicallingdemo.model.VehicleSummary
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Query

class TravelSummaryViewModel:ViewModel() {
    private val mTAG = javaClass.simpleName

    private val _travelSummary = MutableLiveData<List<VehicleSummary>>()
    val travelSummary: LiveData<List<VehicleSummary>> get() = _travelSummary

    fun fetchTravelSummary() {
        viewModelScope.launch {
            try {
                val response = apiServiceTravel.getTravelSummary(
                    userId = 13533,
                    projectId = 37,
                    packageName = "com.uffizio.trakzee",
                    deviceType = "android",
                    versionInfo = "2.74.0",
                    userIdDuplicate = 13533,
                    fromDate = "19-11-2024 00:00:00",
                    toDate = "19-11-2024 13:52:01",
                    vehicleId = "172021,165016,165019",
                    action = "Filter",
                    subAction = "From Tree",
                    screenId = 1228,
                    screenType = "Overview",
                    entityId = 0,
                    distanceFilterCondition = null,
                    distanceFilterValue = null,)
                if (response.isSuccessful && response.body() != null) {
                    _travelSummary.postValue(response.body()?.data)
                } else {
                    Log.e("API Error", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API Error", "Exception: ${e.message}")
            }
        }
    }

}

