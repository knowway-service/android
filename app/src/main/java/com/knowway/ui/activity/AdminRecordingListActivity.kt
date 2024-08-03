package com.knowway.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.knowway.adapter.ViewPagerAdapter
import com.knowway.databinding.ActivityAdminRecordingListBinding

class AdminRecordingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminRecordingListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRecordingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val areaNumber = intent.getIntExtra("areaNumber", -1)
        Log.d("ReceivedData", "Selected Area Number: $areaNumber")

        val viewPagerAdapter = ViewPagerAdapter(this, ViewPagerAdapter.TabType.ADMIN)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "등록된 팁"
                1 -> "선정된 팁"
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }.attach()
    }
}
