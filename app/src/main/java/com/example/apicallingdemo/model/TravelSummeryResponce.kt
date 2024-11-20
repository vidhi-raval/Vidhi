package com.example.apicallingdemo.model

data class TravelSummaryResponse(
    val status: String,
    val message: String,
    val data: List<VehicleSummary>
)
