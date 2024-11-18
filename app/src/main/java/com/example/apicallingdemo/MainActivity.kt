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
    private lateinit var mBinding : ActivityMainBinding
    private  var repoAdapter: RepoAdapter? = null
    private  var repoDao: RepositoryDao ?= null
    private var repoList: ArrayList<Repository> = ArrayList()
    var TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        repoDao = RepositoryDatabase.getInstance(this@MainActivity).repositoryDao()

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
        repoDao!!.getAllRepositories().observe(this) { repoListFromDb ->
            if (repoListFromDb.isNullOrEmpty()) {
                handleEmptyDatabase()
            } else {
                handleDataAvailable(repoListFromDb)
            }
        }
    }

    private fun handleEmptyDatabase() {
        Log.e(TAG, "getData: data not available in db...")
        mBinding.swipeRefreshLayout.setOnRefreshListener { handleSwipeRefresh(isEmpty = true) }

        if (isOnline(this)) {
            Log.e(TAG, "getData: data not available in db:online...")
            fetchAndRefreshData()
        } else {
            showOfflineMessage()
        }
    }

    private fun handleDataAvailable(repoListFromDb: List<Repository>) {
        Log.e(TAG, "getData: data available in db...")
        repoAdapter!!.updateData(repoListFromDb)
        mBinding.swipeRefreshLayout.setOnRefreshListener { handleSwipeRefresh(isEmpty = false, repoListFromDb) }
        mBinding.swipeRefreshLayout.isRefreshing = false
        repoAdapter!!.setLoading(false)
    }

    private fun handleSwipeRefresh(isEmpty: Boolean, repoListFromDb: List<Repository>? = null) {
        if (isOnline(this)) {
            if (repoListFromDb != null) {
                repoAdapter!!.updateData(repoListFromDb)
                Log.e(TAG, "handleSwipeRefresh: data available in db:swipe:online...", )
                mBinding.swipeRefreshLayout.isRefreshing = false
            } else {
                Log.e(TAG, "handleSwipeRefresh: data not available in db", )
                fetchAndRefreshData()
            }
        } else {
            Log.e(TAG, "getData: ${if (isEmpty) "data not available" else "data available"} in db:swipe:offline...")
            showOfflineMessage()
        }
    }

    private fun fetchAndRefreshData() {
        mBinding.swipeRefreshLayout.isRefreshing = true
        scheduleWork()
        repoAdapter!!.setLoading(false)
    }

    private fun showOfflineMessage() {
        mBinding.swipeRefreshLayout.isRefreshing = false
        mBinding.swipeRefreshLayout.visibility = View.GONE
        mBinding.idLayoutNoInternet.root.visibility = View.VISIBLE
        repoAdapter!!.setLoading(true)
        Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
    }


    private fun scheduleWork() {
        Log.e(TAG, "doWork: start working...", )
        val workRequest = PeriodicWorkRequestBuilder<FetchRepositoriesWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueue(workRequest)
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
            repoAdapter!!.filterList(filteredList)
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


