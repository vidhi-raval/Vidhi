package com.example.apicallingdemo.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.apicallingdemo.apiCalling.ApiClient
import com.example.apicallingdemo.database.RepositoryDatabase
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.databinding.LayoutItemRepositoryBinding
import com.example.apicallingdemo.databinding.ShimmerLayoutBinding
import com.example.apicallingdemo.model.Contributors
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoAdapter(var mContext:Context,var repoList: ArrayList<Repository>/*, var fetchContributors:(url:String)->Unit*/): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var expandedPosition = -1
    private var isLoading = true
    private var TAG = javaClass.simpleName
    var repoDatabase = RepositoryDatabase.getInstance(mContext)
    var repoDao = repoDatabase.repositoryDao()
    var mContributorsList = ArrayList<Contributors>()

    companion object {
        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_DATA = 1
    }

    fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_DATA
    }
    fun updateData(newRepositories: List<Repository>) {
        repoList.clear()
        repoList.addAll(newRepositories)
        notifyDataSetChanged()
    }
    inner class RepositoryViewHolder(private val binding: LayoutItemRepositoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Repository, position: Int) {
            binding.tvTitle.text = item.name
            binding.tvDesc.text = item.description
            binding.tvStargazersCount.text = item.stargazers_count.toString()
            binding.tvWatchersCount.text = item.watchers_count.toString()

            binding.ivStar.isVisible = item.isExpanded
            binding.tvStargazersCount.isVisible = item.isExpanded
            binding.ivFork.isVisible = item.isExpanded
            binding.tvWatchersCount.isVisible = item.isExpanded
            binding.rvContributorsList.isVisible = item.isExpanded

            binding.root.setOnClickListener {
                if (expandedPosition == position) {
                    item.isExpanded = false
                    expandedPosition = -1
                } else {
                    if (expandedPosition >= 0) {
                        repoList[expandedPosition].isExpanded = false
                        notifyItemChanged(expandedPosition)
                    }
                    item.isExpanded = true
                    expandedPosition = position
                }
                Log.e(TAG, "bind: url:${item.contributors_url}", )
                fetchContributors(item.id,item.contributors_url) {responce ->
                    if(responce) {
                        binding.rvContributorsList.adapter = ContributorsAdapter(mContext,mContributorsList)
                    } else {
                        Log.e(TAG, "bind:error in loading data... ", )
                        binding.rvContributorsList.visibility = View.GONE
                    }
                }
                notifyItemChanged(position)
            }
        }
    }
    inner class ShimmerViewHolder(private val binding: ShimmerLayoutBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val binding = ShimmerLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ShimmerViewHolder(binding)

        } else {
            val binding = LayoutItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RepositoryViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
         return if (isLoading) 29 else repoList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RepositoryViewHolder && !isLoading) {
            holder.bind(repoList[position], position)
        }
    }
   /* private fun fetchContributors(contributorsUrl: String): Boolean {
        return try {
            val response = ApiClient.apiService.getContributorsList(contributorsUrl).execute()
            if (response.isSuccessful) {
                contributorsList.clear()
                contributorsList.addAll(response.body()?.contributorsItem?: emptyList())
                true
            } else {
                Log.e(TAG, "fetchContributors: Error fetching data")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchContributors: Exception occurred", e)
            false
        }
    }*/

        private fun fetchContributors(id:Int,contributorsUrl: String, callback: (Boolean) -> Unit) {

            ApiClient.apiService.getContributorsList(contributorsUrl).enqueue(object : Callback<List<Contributors>> {
                override fun onResponse(
                    call: Call<List<Contributors>>,
                    response: Response<List<Contributors>>
                ) {
                    if (response.isSuccessful) {
                       response.body()?.let { contributorsList ->
                           mContributorsList.clear()
                           mContributorsList.addAll(contributorsList)
                           for (contributor in contributorsList) {
                               CoroutineScope(Dispatchers.IO).launch {
                                   val existingContributorinDB = repoDao.getContributor(id)
                                   if (existingContributorinDB == null) {
                                       repoDao.insertContributor(contributor)
                                   }
                               }
                           }
                       }
                        callback(true)
                    } else callback(false)
                }

                override fun onFailure(call: Call<List<Contributors>>, t: Throwable) {
                    callback(false)
                }
            })
        }

    fun filterList(filterlist: ArrayList<Repository>) {
        repoList = filterlist
        notifyDataSetChanged()
    }


}