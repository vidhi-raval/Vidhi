package com.example.apicallingdemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apicallingdemo.apiCalling.ApiClientTravelSummery.apiServiceTravel
import com.example.apicallingdemo.model.VehicleSummary
import com.example.apicallingdemo.utils.vehicleId
import kotlinx.coroutines.launch

class TravelSummaryViewModel:ViewModel() {
    private val mTAG = javaClass.simpleName

    private val _travelSummary = MutableLiveData<List<VehicleSummary>>()
    val travelSummary: LiveData<List<VehicleSummary>> get() = _travelSummary

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun fetchTravelSummary(startDate: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            try {
                val response = apiServiceTravel.getTravelSummary(
                    userId = 13533,
                    projectId = 37,
                    packageName = "com.uffizio.trakzee",
                    deviceType = "android",
                    versionInfo = "2.74.0",
                    reAdduserId = 13533,
//                    fromDate = "21-10-2024 00:00:00",
//                    toDate = "21-11-2024 13:52:01",
                    fromDate = startDate,
                    toDate = endDate,
                    vehicleIds = vehicleId,
                    action = "Filter",
                    subAction = "From Tree",
                    screenId = 1228,
                    screenType = "Overview",
                    entityId = 0)
                Log.e(mTAG, "fetchTravelSummary:response:${response.isSuccessful} ", )
                Log.e(mTAG, "fetchTravelSummary:fullResponse:${response} ", )
                if (response.isSuccessful) {
                    Log.e(mTAG, "fetchTravelSummary:response.body()?.data:${response.body()?.DATA} ", )
                    _travelSummary.postValue(response.body()?.DATA)

                    _isLoading.postValue(false)
                } else {
                    Log.e(mTAG, "ErrorCode: ${response.code()}")
                    Log.e(mTAG, "ErrorBody: ${response.errorBody().toString()}")

                }
            } catch (e: Exception) {
                Log.e(mTAG, "Exception: ${e.message}")

            }
        }
    }

}

