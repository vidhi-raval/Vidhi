package com.example.apicallingdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.apicallingdemo.databinding.LayoutContributorsListBinding
import com.example.apicallingdemo.databinding.LayoutTravelSummeryItemBinding
import com.example.apicallingdemo.model.Contributors
import com.example.apicallingdemo.model.TravelSummaryResponse
import com.example.apicallingdemo.model.VehicleSummary

class TravelSummeryAdapter(private var mContext: Context, private var travelerList:ArrayList<VehicleSummary>): RecyclerView.Adapter<TravelSummeryAdapter.ViewHolder>()  {

    inner class ViewHolder( val mBinding: LayoutTravelSummeryItemBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TravelSummeryAdapter.ViewHolder {
        val mBinding = LayoutTravelSummeryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: TravelSummeryAdapter.ViewHolder, position: Int) {
        val currentItem = travelerList[position]
        with(holder){
            with(travelerList[position]){
                mBinding.tvVehicleTitle.text = currentItem.VEHICLE_NUMBER
                mBinding.tvDistance.text = currentItem.distance_unit
                mBinding.tvKM.text = currentItem.distance_unit
                mBinding.tvSTartLocation.text = currentItem.start_location
                mBinding.tvEndLocation.text = currentItem.end_location
                mBinding.tvAvgValue.text = currentItem.AVGSPEED
                mBinding.tvMaxValue.text = currentItem.MAXSPEED
                mBinding.tvDriverName.text = currentItem.driver

            }
        }
    }

    override fun getItemCount(): Int {
        return travelerList.size
    }
}