package com.example.apicallingdemo.ui.activity

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.apicallingdemo.R
import com.example.apicallingdemo.adapter.DataRangeAdapter
import com.example.apicallingdemo.adapter.TravelSummeryAdapter
import com.example.apicallingdemo.databinding.ActivityTravelSummeryBinding
import com.example.apicallingdemo.databinding.LayoutRangeDialogBinding
import com.example.apicallingdemo.model.VehicleSummary
import com.example.apicallingdemo.utils.SharedPrefHelper
import com.example.apicallingdemo.utils.dataRangeList
import com.example.apicallingdemo.utils.isOnline
import com.example.apicallingdemo.viewModel.TravelSummaryViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TravelSummeryActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTravelSummeryBinding
    private var viewModel: TravelSummaryViewModel? = null
    private var travelSummeryAdapter: TravelSummeryAdapter? = null
    private var travelSummeryList = ArrayList<VehicleSummary>()
    private var dialog: Dialog? = null
    private var mTag = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTravelSummeryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        SharedPrefHelper.init(this)
         dialog = Dialog(this, R.style.FullScreenDialog)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = getString(R.string.header_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        viewModel = ViewModelProvider(this)[TravelSummaryViewModel::class.java]

        mBinding.tvSelectedRange.text = dataRangeList[SharedPrefHelper.getInt(SharedPrefHelper.dataRangeKey,0)]

        makeApiCall(dataRangeList[SharedPrefHelper.getInt(SharedPrefHelper.dataRangeKey,0)])

        initViewAction()

    }

    private fun makeApiCall(selectedRange:String) {

        viewModel!!.isLoading.observe(this) { isLoading ->
            mBinding.progressBar.isVisible = isLoading
            mBinding.rvTravelSummeryList.isVisible = !isLoading
        }

        if(isOnline(this)) {
            mBinding.tvNoData.isVisible = false
            mBinding.rvTravelSummeryList.isVisible = true

            viewModel!!.travelSummary.observe(this) { data ->
                travelSummeryList.clear()
                travelSummeryList.addAll(data)
                travelSummeryAdapter = TravelSummeryAdapter(this, data as ArrayList)
                mBinding.rvTravelSummeryList.adapter = travelSummeryAdapter
            }
            val range = getRange(selectedRange)
            viewModel!!.fetchTravelSummary(range.first, range.second)
        } else {
            mBinding.tvNoData.isVisible = true
            mBinding.rvTravelSummeryList.isVisible = false
        }

    }

    private fun initViewAction() {
        mBinding.clDataRange.setOnClickListener {
            showRangeSelectionDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView

        searchView?.queryHint = getString(R.string.search)

        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filter(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText!!)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                true
            }
            R.id.action_sub_menu -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun filter(text: String) {
        val filteredList = travelSummeryList.filter { it.VEHICLE_NUMBER.contains(text, ignoreCase = true) }
        if (filteredList.isEmpty()) {
            mBinding.tvNoData.isVisible = true
            mBinding.rvTravelSummeryList.isVisible = false
        } else {
            travelSummeryAdapter?.filterList(ArrayList(filteredList))
            mBinding.tvNoData.isVisible = false
            mBinding.rvTravelSummeryList.isVisible = true
        }
    }

    private fun showRangeSelectionDialog() {

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)

        val mDialogBinding = LayoutRangeDialogBinding.inflate(layoutInflater)
        dialog?.setContentView(mDialogBinding.root)

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog?.window?.attributes?.windowAnimations = R.style.DialogSlideAnimation
        dialog?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE


        mDialogBinding.rvDataRange.adapter = DataRangeAdapter(dataRangeList) { pos: Int ->
            Log.e(mTag, "showDialog:pos:${dataRangeList[pos]} ", )
            dialog?.cancel()
            mBinding.tvSelectedRange.text = dataRangeList[pos]
            makeApiCall(dataRangeList[pos])
        }

        mDialogBinding.ivClose.setOnClickListener {
            dialog?.cancel()
        }

        dialog?.show()
    }

    private fun getRange(selectedRange: String): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        when(selectedRange) {
            "Custom" -> {
                return showDateTimePickerDialog()
            }
            "Today" -> {
                Log.e(mTag, "getRange:TODAY:${dateFormat.format(calendar.time)} ", )
                Log.e(mTag, "getRange:TODAY2:${calendar.time} ", )
                Log.e(mTag, "getRange:Today:fullDayWithTime:${getStartAndEndOfDay(calendar.time,calendar.time)} ", )

                return getStartAndEndOfDay(calendar.time,calendar.time)
            }
            "Yesterday" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                Log.e(mTag, "getRange:Yesterday:fullDayWithTime:${getStartAndEndOfDay(calendar.time,calendar.time)} ", )
                return getStartAndEndOfDay(calendar.time,calendar.time)
            }
            "Last 7 days" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val startDate = calendar.time
                val endDate = Calendar.getInstance().time
                Log.e(mTag, "getRange:Last 7 days:fullDayWithTime:${getStartAndEndOfDay(startDate,endDate)} ", )
                return getStartAndEndOfDay(startDate,endDate)
            }
            "Last 14 days" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -14)
                val startDate = calendar.time
                val endDate = Calendar.getInstance().time
                Log.e(mTag, "getRange:Last 14 days:fullDayWithTime:${getStartAndEndOfDay(startDate,endDate)} ", )
                return getStartAndEndOfDay(startDate,endDate)
            }
            "Last 30 days" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                val startDate = calendar.time
                val endDate = Calendar.getInstance().time
                Log.e(mTag, "getRange:Last 30 days:fullDayWithTime:${getStartAndEndOfDay(startDate,endDate)} ", )
                return getStartAndEndOfDay(startDate,endDate)
            }
            "This Week" -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startDate = calendar.time
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val endDate = calendar.time

                Log.e(mTag, "getRange:This Week:fullDayWithTime:${getStartAndEndOfDay(startDate,endDate)} ", )
                return getStartAndEndOfDay(startDate,endDate)
            }
            "Last Week" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startDate = calendar.time
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val endDate = calendar.time

                Log.e(mTag, "getRange:Last Week:fullDayWithTime:${getStartAndEndOfDay(startDate,endDate)} ", )
                return getStartAndEndOfDay(startDate,endDate)
            }
            "This Month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val endDate = calendar.time

                Log.e(mTag, "getRange:This Month:fullDayWithTime:${getStartAndEndOfDay(startDate,endDate)} ", )
                return getStartAndEndOfDay(startDate,endDate)
            }
            "Last Month" -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startDate = calendar.time
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val endDate = calendar.time

                Log.e(mTag, "getRange:Last Month:fullDayWithTime:${getStartAndEndOfDay(startDate,endDate)} ", )
                return getStartAndEndOfDay(startDate,endDate)
            }
            else -> return getStartAndEndOfDay(calendar.time,calendar.time)
        }

    }

    private fun showDateTimePickerDialog() :Pair<String, String> {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        val mDialogBinding = LayoutRangeDialogBinding.inflate(layoutInflater)
        dialog.setContentView(mDialogBinding.root)
         return  Pair("","")
    }

    private fun getStartAndEndOfDay(startDate: Date, endDate: Date): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

        calendar.time = startDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfStartDate = calendar.time

        calendar.time = endDate
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfEndDate = calendar.time

        return Pair(dateFormat.format(startOfStartDate), dateFormat.format(endOfEndDate))
    }

    override fun onBackPressed() {
        Log.e(mTag, "onBackPressed: ", )
        dialog?.cancel()
        super.onBackPressed()
    }

}
