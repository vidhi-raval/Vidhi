package com.example.apicallingdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.apicallingdemo.databinding.LayoutDataRangeBinding
import com.example.apicallingdemo.utils.SharedPrefHelper

class DataRangeAdapter(private var dataRangeList:ArrayList<String>, var itemClick:(pos:Int) -> Unit):
    RecyclerView.Adapter<DataRangeAdapter.ViewHolder>()  {

    var pos = SharedPrefHelper.getInt(SharedPrefHelper.dataRangeKey,0)

    inner class ViewHolder( val mBinding: LayoutDataRangeBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataRangeAdapter.ViewHolder {
        val mBinding = LayoutDataRangeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: DataRangeAdapter.ViewHolder, position: Int) {
        val currentItem = dataRangeList[position]
        with(holder) {
           mBinding.tvCustom.text = currentItem
            mBinding.root.setOnClickListener {
                pos = position
                notifyDataSetChanged()
                SharedPrefHelper.saveInt(SharedPrefHelper.dataRangeKey,position)
                itemClick.invoke(position)

            }
            if (pos == position) mBinding.ivSelected.isVisible = true
            else mBinding.ivSelected.isVisible = false

        }
    }

    override fun getItemCount(): Int {
       return dataRangeList.size
    }
}