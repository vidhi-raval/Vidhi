package com.example.apicallingdemo.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.apicallingdemo.adapter.RepoAdapter
import com.example.apicallingdemo.database.RepositoryDao
import com.example.apicallingdemo.database.RepositoryDatabase
import com.example.apicallingdemo.databinding.ActivityMainBinding
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.utils.isOnline
import com.example.apicallingdemo.workManager.FetchRepositoriesWorker
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private var repoAdapter: RepoAdapter? = null
    private var repoDao: RepositoryDao? = null
    private var repoList: ArrayList<Repository> = ArrayList()
    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupViews()
        getData()
    }

    private fun setupViews() {
        repoDao = RepositoryDatabase.getInstance(this).repositoryDao()
        repoAdapter = RepoAdapter(this, ArrayList())
        mBinding.rvTrendingList.adapter = repoAdapter

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mBinding.progressBar.isVisible = false
            setSwipeRefresh()
        }

        mBinding.idLayoutNoInternet.btnRetry.setOnClickListener {
            retryFetchingData()
        }

        mBinding.ivSearch.setOnClickListener {
            showSearchLayout()
        }

        mBinding.layoutForSearchId.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(repo: CharSequence?, p1: Int, p2: Int, p3: Int) {
                filter(repo.toString())
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun retryFetchingData() {
        mBinding.swipeRefreshLayout.isRefreshing = true
        mBinding.idLayoutNoInternet.root.isVisible = false
        mBinding.progressBar.isVisible = true
        Handler(Looper.getMainLooper()).postDelayed({
            setSwipeRefresh()
        }, 3000)
    }

    private fun showSearchLayout() {
        mBinding.layoutForSearchId.root.isVisible = true
        mBinding.tvTitle.visibility = View.INVISIBLE
        mBinding.ivSearch.isVisible = false
        openKeyboard()
    }

    private fun setSwipeRefresh() {
        repoAdapter?.setLoading(true)
        repoDao?.getAllRepositories()?.observe(this) { repoListFromDB ->
            repoAdapter?.setLoading(false)
            if (isOnline(this)) {
                fetchAndRefreshData()
                repoList = repoListFromDB as ArrayList
                repoAdapter?.updateData(repoListFromDB)
                mBinding.progressBar.isVisible = false
            } else {
                handleOfflineState(repoListFromDB)
            }
            mBinding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun getData() {
        repoAdapter?.setLoading(true)
        repoDao?.getAllRepositories()?.observe(this) { repoListFromDB ->
            repoAdapter?.setLoading(false)
            if (isOnline(this)) {
                if (repoListFromDB.isEmpty()) {
                    fetchAndRefreshData()
                }
                repoList = repoListFromDB as ArrayList
                mBinding.swipeRefreshLayout.isVisible = true
                repoAdapter?.updateData(repoListFromDB)
            } else {
                handleOfflineState(repoListFromDB)
            }
        }
    }

    private fun handleOfflineState(repoListFromDB: List<Repository>) {
        if (repoListFromDB.isEmpty()) {
            showOfflineMessage()
        } else {
            repoAdapter?.updateData(repoListFromDB)
        }
    }

    private fun fetchAndRefreshData() {
        mBinding.swipeRefreshLayout.isRefreshing = true
        scheduleWork()
    }

    private fun showOfflineMessage() {
        mBinding.progressBar.isVisible = false
        mBinding.swipeRefreshLayout.isRefreshing = false
        mBinding.swipeRefreshLayout.isVisible = false
        mBinding.idLayoutNoInternet.root.isVisible = true
    }

    private fun scheduleWork() {
        Log.e(TAG, "doWork: start working...")
        val workRequest = PeriodicWorkRequestBuilder<FetchRepositoriesWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun filter(text: String) {
        val filteredList = repoList.filter { it.name.contains(text, ignoreCase = true) }
        if (filteredList.isEmpty()) {
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show()
        } else {
            repoAdapter?.filterList(ArrayList(filteredList))
        }
    }

    private fun openKeyboard() {
        mBinding.layoutForSearchId.etSearch.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mBinding.layoutForSearchId.etSearch, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onBackPressed() {
        if (mBinding.layoutForSearchId.root.isVisible) {
            mBinding.layoutForSearchId.etSearch.setText("")
            getData()
            mBinding.layoutForSearchId.root.isVisible = false
            mBinding.tvTitle.isVisible = true
            mBinding.ivSearch.isVisible = true
        } else {
            super.onBackPressed()
        }
    }
}



