package com.example.apicallingdemo.apiCalling

import com.example.apicallingdemo.apiCalling.ApiClient.apiService
import com.example.apicallingdemo.model.Contributors
import com.example.apicallingdemo.model.RepositoryResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url


interface GitHubApiService {
    @GET("search/repositories")
    fun getTrendingRepositories(
        @Query("q") query: String = "stars:>1000",
        @Query("sort") sort: String = "stars"
    ): Call<RepositoryResponse>

    @GET
    fun getContributorsList(@Url contributorsUrl: String): Call<List<Contributors>>
}