package com.example.apicallingdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apicallingdemo.databinding.LayoutContributorsListBinding
import com.example.apicallingdemo.model.Contributors

class ContributorsAdapter(private var context: Context, private var contributorsList:ArrayList<Contributors>): RecyclerView.Adapter<ContributorsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutContributorsListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorsAdapter.ViewHolder {
        val binding = LayoutContributorsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return contributorsList.size
    }

    override fun onBindViewHolder(holder: ContributorsAdapter.ViewHolder, position: Int) {
        with(holder){
            with(contributorsList[position]){
                Glide.with(context)
                    .load(avatarUrl).into(binding.ivContributors)

            }
        }
    }
}