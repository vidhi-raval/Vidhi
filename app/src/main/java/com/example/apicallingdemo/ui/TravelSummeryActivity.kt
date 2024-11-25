package com.example.apicallingdemo.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
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
import com.example.apicallingdemo.viewModel.TravelSummaryViewModel

class TravelSummeryActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTravelSummeryBinding
    private var viewModel: TravelSummaryViewModel? = null
    private var travelSummeryAdapter: TravelSummeryAdapter? = null
    private var travelSummeryList = ArrayList<VehicleSummary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTravelSummeryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        SharedPrefHelper.init(this)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.title = getString(R.string.header_name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back_arrow)

        viewModel = ViewModelProvider(this)[TravelSummaryViewModel::class.java]

        viewModel!!.travelSummary.observe(this) { data ->
            travelSummeryList.addAll(data)
            travelSummeryAdapter = TravelSummeryAdapter(this,data as ArrayList)
                mBinding.rvTravelSummeryList.adapter = travelSummeryAdapter
        }

        viewModel!!.fetchTravelSummary()

        initViewAction()

    }

    private fun initViewAction() {
        mBinding.clDataRange.setOnClickListener {
            showDialog("abc")
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

    private fun showDialog(title: String) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)

        val mDialogBinding = LayoutRangeDialogBinding.inflate(layoutInflater)
        dialog.setContentView(mDialogBinding.root)

        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialog.window?.attributes?.windowAnimations = R.style.DialogSlideAnimation
//        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

        var dataRangeList = ArrayList<String>()
        dataRangeList.add("Custom")
        dataRangeList.add("Today")
        dataRangeList.add("Yesterday")
        dataRangeList.add("Last 7 days")
        dataRangeList.add("Last 14 days")
        dataRangeList.add("Last 30 days")
        dataRangeList.add("This Week")
        dataRangeList.add("Last Week")
        dataRangeList.add("This Month")
        dataRangeList.add("Last Month")

        mDialogBinding.rvDataRange.adapter = DataRangeAdapter(dataRangeList) { pos: Int ->

        }

        mDialogBinding.ivClose.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }
}