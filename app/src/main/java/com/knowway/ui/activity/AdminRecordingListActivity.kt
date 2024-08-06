package com.knowway.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.knowway.adapter.ViewPagerAdapter
import com.knowway.databinding.ActivityAdminRecordingListBinding
import com.knowway.ui.fragment.AdminRecordingListFragment

class AdminRecordingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminRecordingListBinding
    private var departmentStoreFloorId: Long = -1L
    private var areaNumber: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRecordingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        departmentStoreFloorId = intent.getLongExtra("departmentStoreFloorId", -1L)
        areaNumber = intent.getIntExtra("areaNumber", -1)

        setupViewPager()
    }

    private fun setupViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(this, ViewPagerAdapter.TabType.ADMIN, departmentStoreFloorId, areaNumber, ::refreshData)
        binding.viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "등록된 팁"
                1 -> "선정된 팁"
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }.attach()
    }

    private fun refreshData() {
        (supportFragmentManager.findFragmentByTag("f0") as? AdminRecordingListFragment)?.loadRecords()
        (supportFragmentManager.findFragmentByTag("f1") as? AdminRecordingListFragment)?.loadRecords()
    }
}
