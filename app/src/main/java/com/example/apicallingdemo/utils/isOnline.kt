package com.example.apicallingdemo.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import android.widget.ProgressBar
import androidx.core.content.ContextCompat

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

val vehicleId = "172021%2C165016%2C165019%2C165017%2C165018%2C173983%2C173986%2C173984%2C173987%2C173985%2C173982%2C173988%2C173989%2C165020%2C165028%2C164617%2C164630%2C164640%2C174002%2C174000%2C174001%2C173999%2C172393%2C171696%2C173774%2C174171%2C174172%2C174126%2C174191%2C174189%2C174193%2C174188%2C174192%2C174194%2C174190%2C174143%2C174200%2C174142%2C174195%2C174141%2C174140%2C174207%2C174209%2C174208%2C174203%2C174205%2C174144%2C174211%2C174212%2C174196%2C174206%2C174197%2C174210%2C174162%2C174199%2C174198%2C174163%2C174127%2C174204%2C174202%2C174150%2C174146%2C174173%2C174145%2C174149%2C174148%2C174147%2C174151%2C174152%2C174201%2C174153%2C174161%2C174114%2C174175%2C174156%2C174154%2C174115%2C174138%2C174176%2C174116%2C174158%2C174155%2C174157%2C174139%2C174159%2C174182%2C174181%2C174180%2C174179%2C174178%2C174177%2C174174%2C174137%2C174117%2C174128%2C174129%2C174183%2C174184%2C174130%2C174164%2C174131%2C174166%2C174165%2C174160%2C174167%2C174132%2C174186%2C174187%2C174185%2C174135%2C174133%2C174136%2C174134%2C174169%2C174168%2C174170%2C174118%2C174119%2C174120%2C174121%2C174122%2C174124%2C174123%2C174125%2C173417%2C173996%2C173421%2C173998%2C172487%2C164546%2C172386%2C164634%2C171374%2C164434%2C164919%2C164558%2C164556%2C164551%2C172017%2C164555%2C164552%2C171752%2C171754%2C171755%2C165029%2C172488%2C164550%2C172482%2C164553%2C164636%2C165065%2C172513%2C172018%2C172022%2C164545%2C172470%2C165103%2C172411%2C172412%2C172413%2C164548%2C164877%2C164435%2C164436%2C164437%2C164438%2C164439%2C164981%2C164982%2C171641%2C172416%2C173997%2C172410%2C172414%2C172015%2C172417%2C172020%2C164544%2C164557%2C164638%2C172480%2C164560%2C164554%2C172481%2C172486%2C172415%2C172813%2C172019%2C172016%2C164559%2C164891%2C172490%2C172483%2C165075%2C171636%2C172484"

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

fun changeProgressColor(progressBar: ProgressBar, color: Int, mContext: Context) {
    val layerDrawable = progressBar.progressDrawable as LayerDrawable

    val progressLayer = layerDrawable.findDrawableByLayerId(android.R.id.progress)

    if (progressLayer is ClipDrawable) {
        val drawableInside = progressLayer.drawable
        if (drawableInside is GradientDrawable) {
            drawableInside.setColorFilter(
                ContextCompat.getColor(mContext, color),
                PorterDuff.Mode.SRC_IN
            )
        }
    }
}


