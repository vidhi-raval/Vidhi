package com.example.apicallingdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.apicallingdemo.databinding.LayoutDataRangeBinding
import com.example.apicallingdemo.databinding.LayoutTravelSummeryItemBinding
import com.example.apicallingdemo.model.VehicleSummary

class DataRangeAdapter(private var dataRangeList:ArrayList<String>): RecyclerView.Adapter<DataRangeAdapter.ViewHolder>()  {

    inner class ViewHolder( val mBinding: LayoutDataRangeBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRangeAdapter.ViewHolder {
        val mBinding = LayoutDataRangeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: DataRangeAdapter.ViewHolder, position: Int) {
        val currentItem = dataRangeList[position]
        with(holder) {
           mBinding.tvCustom.text = currentItem
        }
    }

    override fun getItemCount(): Int {
       return dataRangeList.size
    }
}