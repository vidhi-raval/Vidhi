package com.example.apicallingdemo.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
            return true
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
            return true
        }
    }
    return false
}

/*val service = ApiClient.apiService
           val response = service.getTrendingRepositories().execute()

           if (response.isSuccessful) {
               val repositories = response.body()?.items?.map {
                   Repository(
                       id = it.id,
                       node_id = it.node_id,
                       name = it.name,
                       full_name = it.full_name,
                       html_url = it.html_url,
                       description = it.description,
                       stargazers_count = it.stargazers_count,
                       watchers_count = it.watchers_count,
                       forks_count = it.forks_count,
                       watchers = it.watchers,
                       isExpanded = it.isExpanded,
                       owner = it.owner,
                       lastFetched = System.currentTimeMillis()
                   )
               } ?: emptyList()

               val repositoryDao = RepositoryDatabase.getInstance(context).repositoryDao()
               repositoryDao.insertAll(repositories)

               Result.success()
           }
           else {
               Result.failure()
           }*/