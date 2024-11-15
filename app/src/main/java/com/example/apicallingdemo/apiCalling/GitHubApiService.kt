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
   /* @GET("search/repositories?sort=stars")
    fun getTrendingRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars"
    ): Call<RepositoryResponse>
*/

   /* @GET("search/repositories")
    fun getTrendingRepositories(
        @Query("q") query: String,           // Query string, for example "language:kotlin"
        @Query("sort") sort: String = "stars" // Sort by stars
    ): Call<RepositoryResponse>
*/

    @GET("search/repositories")
    fun getTrendingRepositories(
        @Query("q") query: String = "stars:>1000", // Query for repositories with more than 1000 stars
        @Query("sort") sort: String = "stars"     // Sorting by stars
    ): Call<RepositoryResponse>

//    suspend fun getContributors(@Url url: String): List<Contributors>

    @GET
    fun getContributorsList(@Url contributorsUrl: String): Call<List<Contributors>>
}