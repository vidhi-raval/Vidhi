package com.example.apicallingdemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var repoList: ArrayList<Repository>
    var TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        repoList = ArrayList()

//        viewModel = ViewModelProvider(this)[RepositoryViewModel::class.java]

        repoDatabase = RepositoryDatabase.getInstance(this@MainActivity)
        repoDao = repoDatabase.repositoryDao()

        mBinding.rvTrendingList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        repoAdapter = RepoAdapter(this,ArrayList())
        mBinding.rvTrendingList.adapter = repoAdapter

        getData()

        mBinding.idLayoutNoInternet.btnNoInternet.setOnClickListener {
            mBinding.swipeRefreshLayout.visibility = View.VISIBLE
            mBinding.idLayoutNoInternet.root.visibility = View.GONE
            Handler(Looper.getMainLooper()).postDelayed({
                getData()
            }, 3000)
        }

        mBinding.ivSearch.setOnClickListener {
            mBinding.layoutForSearchId.root.visibility = View.VISIBLE
            mBinding.tvTitle.visibility = View.INVISIBLE
            mBinding.ivSearch.visibility = View.GONE
        }
        mBinding.layoutForSearchId.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(repo: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filter(repo.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun getData() {
        repoDao.getAllRepositories().observe(this) { repoListFromDb ->
            if (repoListFromDb.isNullOrEmpty()) {
                Log.e(TAG, "getData: data not available in db...", )
                mBinding.swipeRefreshLayout.setOnRefreshListener {
                    if (isOnline(this)) {
                        Log.e(TAG, "getData: data not available in db:swipe:online...", )
                        mBinding.swipeRefreshLayout.isRefreshing = true
                        scheduleWork()
                        repoAdapter.setLoading(false)
                    } else {
                        Log.e(TAG, "getData: data not available in db:swipe:offline...", )
                        mBinding.swipeRefreshLayout.visibility = View.GONE
                        mBinding.idLayoutNoInternet.root.visibility = View.VISIBLE
                        repoAdapter.setLoading(true)
                        mBinding.swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
                    }
                }
                if (isOnline(this)) {
                    Log.e(TAG, "getData: data not available in db:online...", )
                    mBinding.swipeRefreshLayout.isRefreshing = true
                    scheduleWork()
                    repoAdapter.setLoading(false)
                } else {
                    //show shimmer
                    Log.e(TAG, "getData: data not available in db:offline...", )
                    repoAdapter.setLoading(true)
                    mBinding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e(TAG, "getData: data available in db...", )
                repoList.addAll(repoListFromDb)
                mBinding.swipeRefreshLayout.setOnRefreshListener {
                    if (isOnline(this)) {
                        Log.e(TAG, "getData: data available in db:swipe:online...", )
                        repoAdapter.updateData(repoListFromDb)
                        mBinding.swipeRefreshLayout.isRefreshing = false
                    } else {
                        Log.e(TAG, "getData: data available in db:swipe:offline...", )
                        Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
                        mBinding.swipeRefreshLayout.isRefreshing = false
                    }
                }
                repoAdapter.setLoading(false)
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
        val workRequest = PeriodicWorkRequestBuilder<FetchRepositoriesWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueue(workRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                Log.e(TAG, "scheduleWork:workInfo.state:${workInfo.state} ", )
                Log.e(TAG, "scheduleWork:workInfo:${workInfo} ", )
            }
        }
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<Repository> = ArrayList()

        for (item in repoList) {
            if (item.name.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item)
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            repoAdapter.filterList(filteredList)
        }
    }

    override fun onBackPressed() {
        if(mBinding.layoutForSearchId.root.visibility == View.VISIBLE) {
            mBinding.layoutForSearchId.etSearch.setText(" ")
            getData()
            mBinding.layoutForSearchId.root.visibility = View.GONE
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.ivSearch.visibility = View.VISIBLE

        } else{
            super.onBackPressed()
        }
    }
}


