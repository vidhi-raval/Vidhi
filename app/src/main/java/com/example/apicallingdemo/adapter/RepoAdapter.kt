package com.example.apicallingdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apicallingdemo.model.Repository
import com.example.apicallingdemo.databinding.LayoutItemRepositoryBinding
import com.example.apicallingdemo.databinding.ShimmerLayoutBinding

class RepoAdapter(var repoList: ArrayList<Repository>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var expandedPosition = -1
    private var isLoading = true

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

            binding.ivStar.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            binding.tvStargazersCount.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            binding.ivFork.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            binding.tvWatchersCount.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

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

}