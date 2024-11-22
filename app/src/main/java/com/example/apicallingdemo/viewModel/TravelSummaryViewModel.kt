package com.example.apicallingdemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apicallingdemo.apiCalling.ApiClientTravelSummery.apiServiceTravel
import com.example.apicallingdemo.model.VehicleSummary
import kotlinx.coroutines.launch

class TravelSummaryViewModel:ViewModel() {
    private val mTAG = javaClass.simpleName
    private val vehicleId = listOf("172021,165016,165019,165017,165018,173983,173986,173984,173987,173985,173982,173988,173989,165020,165028,164617,164630,164640,174002,174000,174001,173999,172393,171696,173774,174171,174172,174126,174191,174189,174193,174188,174192,174194,174190,174143,174200,174142,174195,174141,174140,174207,174209,174208,174203,174205,174144,174211,174212,174196,174206,174197,174210,174162,174199,174198,174163,174127,174204,174202,174150,174146,174173,174145,174149,174148,174147,174151,174152,174201,174153,174161,174114,174175,174156,174154,174115,174138,174176,174116,174158,174155,174157,174139,174159,174182,174181,174180,174179,174178,174177,174174,174137,174117,174128,174129,174183,174184,174130,174164,174131,174166,174165,174160,174167,174132,174186,174187,174185,174135,174133,174136,174134,174169,174168,174170,174118,174119,174120,174121,174122,174124,174123,174125,173417,173996,173421,173998,172487,164546,172386,164634,171374,164434,164919,164558,164556,164551,172017,164555,164552,171752,171754,171755,165029,172488,164550,172482,164553,164636,165065,172513,172018,172022,164545,172470,165103,172411,172412,172413,164548,164877,164435,164436,164437,164438,164439,164981,164982,171641,172416,173997,172410,172414,172015,172417,172020,164544,164557,164638,172480,164560,164554,172481,172486,172415,172813,172019,172016,164559,164891,172490,172483,165075,171636,172484")

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
                    reAdduserId = 13533,
                    fromDate = "21-10-2024 00:00:00",
                    toDate = "21-11-2024 13:52:01",
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

