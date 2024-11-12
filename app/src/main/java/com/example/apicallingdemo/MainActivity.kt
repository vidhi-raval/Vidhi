package com.example.apicallingdemo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.apicallingdemo.adapter.RepoAdapter
import com.example.apicallingdemo.apiCalling.ApiClient
import com.example.apicallingdemo.database.RepositoryDao
import com.example.apicallingdemo.database.RepositoryDatabase
import com.example.apicallingdemo.model.RepositoryResponse
import com.example.apicallingdemo.databinding.ActivityMainBinding
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.utils.isOnline
import com.example.apicallingdemo.viewModel.RepositoryViewModel
import com.example.apicallingdemo.viewModel.RepositoryViewModel2
import com.example.apicallingdemo.workManager.FetchRepositoriesWorker
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityMainBinding
    private lateinit var repoAdapter: RepoAdapter
    private lateinit var viewModel: RepositoryViewModel2
    private lateinit var repoDatabase: RepositoryDatabase
    private lateinit var repoDao: RepositoryDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        viewModel = ViewModelProvider(this)[RepositoryViewModel2::class.java]

        repoDatabase = RepositoryDatabase.getInstance(this@MainActivity)
        repoDao = repoDatabase.repositoryDao()

//        viewModel = ViewModelProvider(this)[RepositoryViewModel::class.java]

        mBinding.rvTrendingList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        repoAdapter = RepoAdapter(ArrayList())
        mBinding.rvTrendingList.adapter = repoAdapter


        repoDao.getAllRepositories().observe(this) { repoListFromDb ->
            if (repoListFromDb.isNullOrEmpty()) {
                Log.e("TAG", "onCreate: Database is empty, calling API...")
//                observeViewModel()
                viewModel.scheduleWork()

            } else {
                Log.e("TAG", "onCreate: Database is not empty")
                repoAdapter.updateData(repoListFromDb)
            }
        }
        /*mBinding.swipeRefreshLayout.setOnRefreshListener {
            mBinding.swipeRefreshLayout.isRefreshing = false
           viewModel.fetchRepositories()
        }
        viewModel.fetchRepositories()*/
    }


/*
    private fun observeViewModel() {
        viewModel.repositories.observe(this) { repositories ->
            repoAdapter.updateData(repositories)
            //add data to db
            insertRepositories(repositories)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            mBinding.swipeRefreshLayout.isRefreshing = isLoading
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
*/

    private fun insertRepositories(repositories: List<Repository>) {
        lifecycleScope.launch {
            repoDao.insertAll(repositories)
        }
        Log.e("TAG", "insertRepositories: data inserted successfully in db... ", )
    }
   /* private fun callApi() {
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

    }*/
}


