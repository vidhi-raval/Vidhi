package com.example.apicallingdemo.apiCalling

import com.example.apicallingdemo.model.TravelSummaryResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TravelSummeryApiService {
    @GET("?method=getTravelSummary")
    suspend fun getTravelSummary(
        @Query("method") method: String = "getTravelSummary",
        @Query("UserId") userId: Int,
        @Query("ProjectId") projectId: Int,
        @Query("PackageName") packageName: String,
        @Query("devicetype") deviceType: String,
        @Query("VersionInfo") versionInfo: String,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String,
        @Query("vehicleId") vehicleId: String,
        @Query("Action") action: String,
        @Query("SubAction") subAction: String,
        @Query("ScreenId") screenId: Int,
        @Query("ScreenType") screenType: String,
        @Query("EntityId") entityId: Int,
        @Query("distance_filter_condition") distanceFilterCondition: String?,
        @Query("distance_filter_value") distanceFilterValue: String?
    ): Response<TravelSummaryResponse>
}
