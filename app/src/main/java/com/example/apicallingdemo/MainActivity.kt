package com.example.apicallingdemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.apicallingdemo.adapter.RepoAdapter
import com.example.apicallingdemo.database.RepositoryDao
import com.example.apicallingdemo.database.RepositoryDatabase
import com.example.apicallingdemo.databinding.ActivityMainBinding
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.utils.isOnline
import com.example.apicallingdemo.workManager.FetchRepositoriesWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding:ActivityMainBinding
    private lateinit var repoAdapter: RepoAdapter
//    private lateinit var viewModel: RepositoryViewModel
    private lateinit var repoDatabase: RepositoryDatabase
    private lateinit var repoDao: RepositoryDao
    var TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

//        viewModel = ViewModelProvider(this)[RepositoryViewModel::class.java]

        repoDatabase = RepositoryDatabase.getInstance(this@MainActivity)
        repoDao = repoDatabase.repositoryDao()

        mBinding.rvTrendingList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        repoAdapter = RepoAdapter(ArrayList())
        mBinding.rvTrendingList.adapter = repoAdapter

        getData()

        mBinding.idLayoutNoInternet.btnNoInternet.setOnClickListener {
            mBinding.swipeRefreshLayout.visibility = View.VISIBLE
            mBinding.idLayoutNoInternet.root.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                getData()
            }, 3000)
        }

        /*repoDao.getAllRepositories().observe(this) { repoListFromDb ->
            if (repoListFromDb.isNullOrEmpty()) {
                Log.e(TAG, "onCreate: Database is empty, calling API...")
//                observeViewModel()
                if(isOnline(this)) {
                    viewModel.scheduleWork()
                } else {
                    Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e(TAG, "onCreate: Database is not empty")
                repoAdapter.updateData(repoListFromDb)
            }
        }
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mBinding.swipeRefreshLayout.isRefreshing = false
           viewModel.fetchRepositories()
        }
        viewModel.fetchRepositories()*/
    }

    private fun getData() {
        repoDao.getAllRepositories().observe(this) { repoListFromDb ->
            if (repoListFromDb.isNullOrEmpty()) {
                mBinding.swipeRefreshLayout.setOnRefreshListener {
                    if (isOnline(this)) {
                        mBinding.swipeRefreshLayout.isRefreshing = true
                        scheduleWork()
                        repoAdapter.setLoading(false)
                    } else {
                        mBinding.swipeRefreshLayout.visibility = View.GONE
                        mBinding.idLayoutNoInternet.root.visibility = View.VISIBLE
                        repoAdapter.setLoading(true)
                        mBinding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
                    }
                }
                if (isOnline(this)) {
                    mBinding.swipeRefreshLayout.isRefreshing = true
                    scheduleWork()
                    repoAdapter.setLoading(false)
                } else {
                    //show shimmer
                    repoAdapter.setLoading(true)
                    mBinding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
                }
            } else {
                mBinding.swipeRefreshLayout.setOnRefreshListener {
                    if (isOnline(this)) {
                        repoAdapter.updateData(repoListFromDb)
                        mBinding.swipeRefreshLayout.isRefreshing = false
                    } else {
                        Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
                        mBinding.swipeRefreshLayout.isRefreshing = false
                    }
                }
                repoAdapter.updateData(repoListFromDb)
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
        }
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
        Log.e(TAG, "insertRepositories: data inserted successfully in db... ", )
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

    private fun scheduleWork() {
        Log.e(TAG, "doWork: start working...", )
//        startLoggingEveryMinute()
        val workRequest = PeriodicWorkRequestBuilder<FetchRepositoriesWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(workRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                Log.e(TAG, "scheduleWork:workInfo.state:${workInfo.state} ", )
                Log.e(TAG, "scheduleWork:workInfo:${workInfo} ", )
            }
        }
    }
}


