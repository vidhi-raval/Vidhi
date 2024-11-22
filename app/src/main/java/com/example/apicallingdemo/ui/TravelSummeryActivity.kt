package com.example.apicallingdemo.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.apicallingdemo.R
import com.example.apicallingdemo.adapter.TravelSummeryAdapter
import com.example.apicallingdemo.databinding.ActivityTravelSummeryBinding
import com.example.apicallingdemo.viewModel.TravelSummaryViewModel

class TravelSummeryActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTravelSummeryBinding
    private var viewModel: TravelSummaryViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTravelSummeryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.title = getString(R.string.header_name)

        viewModel = ViewModelProvider(this)[TravelSummaryViewModel::class.java]

        viewModel!!.travelSummary.observe(this) { data ->
            mBinding.rvTravelSummeryList.adapter = TravelSummeryAdapter(this,data as ArrayList)
        }

        viewModel!!.fetchTravelSummary()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                true
            }
            R.id.action_filter -> {
                true
            }
            R.id.action_sub_menu -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}