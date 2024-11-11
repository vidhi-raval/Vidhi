package com.example.apicallingdemo

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.apicallingdemo.adapter.RepoAdapter
import com.example.apicallingdemo.apiCalling.ApiClient
import com.example.apicallingdemo.apiCalling.model.RepositoryResponse
import com.example.apicallingdemo.databinding.ActivityMainBinding
import com.example.apicallingdemo.utils.isOnline
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityMainBinding
    private lateinit var repoAdapter: RepoAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

//        mBinding.shimmerLayout.visibility = View.VISIBLE
//        mBinding.shimmerLayout.startShimmer()

        mBinding.rvTrendingList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        callApi()


        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mBinding.swipeRefreshLayout.isRefreshing = false
            callApi()
            if(::repoAdapter.isInitialized) {
                repoAdapter.notifyDataSetChanged()
            }
        }

    }



    private fun callApi() {
        if (isOnline(this)) {
            val call = ApiClient.apiService.getTrendingRepositories()

            call.enqueue(object : Callback<RepositoryResponse> {
                override fun onResponse(
                    call: Call<RepositoryResponse>,
                    response: Response<RepositoryResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Successful", Toast.LENGTH_SHORT).show()
                        val repositories = response.body()?.items ?: emptyList()
                        repoAdapter = RepoAdapter(repositories as ArrayList)
                        mBinding.rvTrendingList.adapter = repoAdapter

//                        mBinding.shimmerLayout.stopShimmer()
//                        mBinding.shimmerLayout.setVisibility(View.GONE)
                        Log.e("DEMOO", "onResponse:$repositories ")

                    } else {

                        Toast.makeText(this@MainActivity, "error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RepositoryResponse>, t: Throwable) {
                    Log.e("DEMOO", "onFailure:error:${t.message} ")

                }

            })
        } else {

            Toast.makeText(this, "Please check your internet", Toast.LENGTH_SHORT).show()
        }

    }
}