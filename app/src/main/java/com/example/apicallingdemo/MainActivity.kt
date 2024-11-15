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
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var repoAdapter: RepoAdapter
    private lateinit var repoDatabase: RepositoryDatabase
    private lateinit var repoDao: RepositoryDao
    private var repoList = ArrayList<Repository>()
    var TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        repoDatabase = RepositoryDatabase.getInstance(this)
        repoDao = repoDatabase.repositoryDao()

        repoAdapter = RepoAdapter(this, ArrayList())
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

        mBinding.layoutForSearchId.etSearch.addTextChangedListener(object : TextWatcher {
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
                Log.e(TAG, "getData: data not available in db...")
                handleSwipeRefresh(true)

                if (isOnline(this)) {
                    Log.e(TAG, "getData: data not available in db: online...")
                    mBinding.swipeRefreshLayout.isRefreshing = true
                    scheduleWork()
                    repoAdapter.setLoading(false)
                } else {
                    handleNoInternet()
                }
            } else {
                Log.e(TAG, "getData: data available in db...")
                repoList.addAll(repoListFromDb)
                handleSwipeRefresh(false)
                repoAdapter.updateData(repoListFromDb)
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun handleSwipeRefresh(isEmpty: Boolean) {
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            if (isOnline(this)) {
                if (isEmpty) {
                    scheduleWork()
                    repoAdapter.setLoading(false)
                } else {
                    repoAdapter.updateData(repoList)
                }
            } else {
                handleNoInternet()
            }
            mBinding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun handleNoInternet() {
        Log.e(TAG, "getData: no internet...")
        mBinding.swipeRefreshLayout.visibility = View.GONE
        mBinding.idLayoutNoInternet.root.visibility = View.VISIBLE
        repoAdapter.setLoading(true)
        Toast.makeText(this, "Check your internet...", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleWork() {
        Log.e(TAG, "scheduleWork: start working...")
        val workRequest = PeriodicWorkRequestBuilder<FetchRepositoriesWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueue(workRequest)

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->
            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                Log.e(TAG, "scheduleWork: Success!")
            }
        }
    }

    private fun filter(text: String) {
        val filteredList = repoList.filter {
            it.name.contains(text, ignoreCase = true)
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            repoAdapter.filterList(ArrayList(filteredList))
        }
    }

    override fun onBackPressed() {
        if (mBinding.layoutForSearchId.root.visibility == View.VISIBLE) {
            mBinding.layoutForSearchId.etSearch.setText("")
            getData()
            mBinding.layoutForSearchId.root.visibility = View.GONE
            mBinding.tvTitle.visibility = View.VISIBLE
            mBinding.ivSearch.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }
}



