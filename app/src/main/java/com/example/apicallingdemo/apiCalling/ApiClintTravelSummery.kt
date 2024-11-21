package com.example.apicallingdemo.apiCalling

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClientTravel {
    private const val BASE_URL = "http://192.168.0.81:8091/"

    val retrofitTravel: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClientTravelSummery {
    val apiServiceTravel: TravelSummeryApiService by lazy {
        RetrofitClientTravel.retrofitTravel.create(TravelSummeryApiService::class.java)
    }
}


