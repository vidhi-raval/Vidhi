package com.example.apicallingdemo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.apicallingdemo.R
import com.example.apicallingdemo.databinding.LayoutTravelSummeryItemBinding
import com.example.apicallingdemo.model.VehicleSummary
import com.example.apicallingdemo.utils.changeProgressColor

class TravelSummeryAdapter(private var mContext: Context, private var travelerList:ArrayList<VehicleSummary>): RecyclerView.Adapter<TravelSummeryAdapter.ViewHolder>()  {

    inner class ViewHolder( val mBinding: LayoutTravelSummeryItemBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TravelSummeryAdapter.ViewHolder {
        val mBinding = LayoutTravelSummeryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(mBinding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TravelSummeryAdapter.ViewHolder, position: Int) {
        val currentItem = travelerList[position]
        with(holder) {
            with(travelerList[position]) {
                val maxChars = 20
                mBinding.tvVehicleTitle.text = if (currentItem.VEHICLE_NUMBER.length > maxChars) {
                    currentItem.VEHICLE_NUMBER.substring(0, maxChars) + "..."
                } else {
                    currentItem.VEHICLE_NUMBER
                }

                /*val maxChars = 20
                mBinding.tvVehicleTitle.text = if (mContext.getString(R.string.vehicle_no_4_vehicle_no_5ghgfj).length > maxChars) {
                    mContext.getString(R.string.vehicle_no_4_vehicle_no_5ghgfj).substring(0, maxChars) + "..."
                } else {
                    mContext.getString(R.string.vehicle_no_4_vehicle_no_5ghgfj)
                }*/

//                mBinding.tvDistance.text = currentItem.distance_unit

                val maxHours = if (currentItem.working_days != "0") {
                    currentItem.working_days.toInt() * 24
                } else {
                    24
                }

                mBinding.tvKM.text = currentItem.distance_unit
                mBinding.tvSTartLocation.text = currentItem.start_location
                mBinding.tvEndLocation.text = currentItem.end_location
                mBinding.tvAvgValue.text = currentItem.AVGSPEED
                mBinding.tvMaxValue.text = currentItem.MAXSPEED
                mBinding.tvDriverName.text = currentItem.driver
                mBinding.tvRunningTime.text = "${currentItem.RUNNINGTIME} hrs"
                mBinding.tvStopTime.text = "${currentItem.STOPTIME} hrs"
                mBinding.tvIdleTime.text = "${currentItem.IDELTIME} hrs"
                mBinding.tvInActiveTime.text ="${currentItem.INACTIVETIME} hrs"

                mBinding.tvStartOdoMeterDigit.text = currentItem.start_odometer.toString().padStart(7, '0')
                mBinding.tvEndOdoMeterDigit.text = currentItem.end_odometer.toString().padStart(7, '0')

                setProgressBasedOnHours(mBinding.pbRunning,parseHoursFromTime(currentItem.RUNNINGTIME),maxHours)
                setProgressBasedOnHours(mBinding.pbStop,parseHoursFromTime(currentItem.STOPTIME),maxHours)
                setProgressBasedOnHours(mBinding.pbIdle,parseHoursFromTime(currentItem.IDELTIME),maxHours)
                setProgressBasedOnHours(mBinding.pbInactive,parseHoursFromTime(currentItem.INACTIVETIME),maxHours)

                changeProgressColor(mBinding.pbRunning,R.color.seekbar_green_color,mContext)
                changeProgressColor(mBinding.pbStop,R.color.seekbar_red_color,mContext)
                changeProgressColor(mBinding.pbIdle,R.color.seekbar_orange_color,mContext)
                changeProgressColor(mBinding.pbInactive,R.color.seekbar_blue_color,mContext)

            }
        }
    }

    override fun getItemCount(): Int {
        return travelerList.size
    }


    private fun setProgressBasedOnHours(progressBar: ProgressBar, hours: Int, maxHours: Int) {
        val progress = (hours.toFloat() / maxHours * progressBar.max).toInt()
        progressBar.progress = progress
    }

    private fun parseHoursFromTime(time: String): Int {
        return try {
            val parts = time.split(":")
            val hours = parts[0].toInt()
            hours
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun filterList(filterList: ArrayList<VehicleSummary>) {
        travelerList = filterList
        notifyDataSetChanged()
    }
}