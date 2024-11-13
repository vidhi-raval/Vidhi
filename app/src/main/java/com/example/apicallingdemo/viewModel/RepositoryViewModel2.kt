//package com.example.apicallingdemo.viewModel
//
//import android.app.Application
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.lifecycle.AndroidViewModel
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.PeriodicWorkRequestBuilder
//import androidx.work.WorkInfo
//import androidx.work.WorkManager
//import com.example.apicallingdemo.workManager.FetchRepositoriesWorker
//import java.util.concurrent.TimeUnit
//
//class RepositoryViewModel2(application: Application): AndroidViewModel(application) {
//var TAG = javaClass.simpleName
//
//    private val handler = Handler(Looper.getMainLooper())
//    private val logRunnable = object : Runnable {
//        override fun run() {
//            Log.e(TAG, "Log every minute...")
//            handler.postDelayed(this, TimeUnit.MINUTES.toMillis(1)) // Schedule next log in 1 minute
//        }
//    }
//
//    fun scheduleWork() {
//        Log.e(TAG, "doWork: start working...", )
//        startLoggingEveryMinute()
//
//        val workRequest = PeriodicWorkRequestBuilder<FetchRepositoriesWorker>(15, TimeUnit.MINUTES)
//            .build()
//
//        WorkManager.getInstance(getApplication()).enqueue(workRequest)
//
//        WorkManager.getInstance(getApplication()).getWorkInfoByIdLiveData(workRequest.id).observe(viewLifecycleOwner) { workInfo ->
//            if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
//                // The work has finished, update the RecyclerView here
//                val updatedData = RepositoryDatabase.getInstance(requireContext()).repositoryDao().getAllRepositories()
//                adapter.submitList(updatedData)
//            }
//        }
//    }
//
//    private fun startLoggingEveryMinute() {
//        handler.post(logRunnable) // Start the first log
//    }
//
//    private fun stopLoggingEveryMinute() {
//        handler.removeCallbacks(logRunnable)
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        stopLoggingEveryMinute()
//    }
//
//}