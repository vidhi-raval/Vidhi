package com.example.apicallingdemo.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.apicallingdemo.R
import com.example.apicallingdemo.adapter.TravelSummeryAdapter
import com.example.apicallingdemo.databinding.ActivityMainBinding
import com.example.apicallingdemo.databinding.ActivityTravelSummeryBinding
import com.example.apicallingdemo.viewModel.TravelSummaryViewModel

class TravelSummeryActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTravelSummeryBinding
    private var viewModel: TravelSummaryViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTravelSummeryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        viewModel = ViewModelProvider(this)[TravelSummaryViewModel::class.java]

        viewModel!!.travelSummary.observe(this) { data ->
            mBinding.rvTravelSummeryList.adapter = TravelSummeryAdapter(this,data as ArrayList)
        }

        viewModel!!.fetchTravelSummary()
    }
}