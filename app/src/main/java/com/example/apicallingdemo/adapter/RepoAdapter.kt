package com.example.apicallingdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apicallingdemo.apiCalling.model.Repository
import com.example.apicallingdemo.databinding.LayoutItemRepositoryBinding

class RepoAdapter(var repoList: ArrayList<Repository>): RecyclerView.Adapter<RepoAdapter.RepositoryViewHolder>() {

    private var expandedPosition = -1

//    inner class RepositoryViewHolder(val mBinding: LayoutItemRepositoryBinding) : RecyclerView.ViewHolder(mBinding.root)

    inner class RepositoryViewHolder(private val binding: LayoutItemRepositoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Repository, position: Int) {
            binding.tvTitle.text = item.name
            binding.tvDesc.text = item.description
            binding.tvStargazersCount.text = item.stargazers_count.toString()
            binding.tvWatchersCount.text = item.watchers_count.toString()

            // Toggle visibility based on expanded state
            binding.ivStar.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            binding.tvStargazersCount.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            binding.ivFork.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            binding.tvWatchersCount.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                // Check if this item is already expanded
                if (expandedPosition == position) {
                    item.isExpanded = false
                    expandedPosition = -1  // Collapse the item
                } else {
                    // Collapse the currently expanded item if any
                    if (expandedPosition >= 0) {
                        repoList[expandedPosition].isExpanded = false
                        notifyItemChanged(expandedPosition)
                    }
                    // Expand the clicked item
                    item.isExpanded = true
                    expandedPosition = position
                }
                notifyItemChanged(position)  // Update the clicked item
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        val binding = LayoutItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RepositoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return repoList.size
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        holder.bind(repoList[position], position)
    }
}