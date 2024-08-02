package com.knowway.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.knowway.databinding.ActivityAdminAreaSelectionBinding
import com.knowway.ui.fragment.mainpage.MainFloorSelectFragment

class AdminAreaSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAreaSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAreaSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val departmentStoreId = intent.getIntExtra("selectedDepartmentStoreId", -1)
        val departmentStoreName = intent.getStringExtra("selectedDepartmentStoreName")
        val departmentStoreBranch = intent.getStringExtra("selectedDepartmentStoreBranch")

        Log.d("ReceivedData", "넘어왔다!")
        Log.d("ReceivedData", "Department Store ID: $departmentStoreId")
        Log.d("ReceivedData", "Department Store Name: $departmentStoreName")
        Log.d("ReceivedData", "Department Store Branch: $departmentStoreBranch")

        // clipToOutline 설정
        binding.imgPartDialog.clipToOutline = true

        // Set up the floor select button click listener
        binding.icFloorSelect.setOnClickListener {
            val floorSelectModal = MainFloorSelectFragment()
            floorSelectModal.show(supportFragmentManager, "층 선택 모달창")
        }

        // Set up area click listeners
        binding.area1.setOnClickListener { navigateToRecordingList(1) }
        binding.area2.setOnClickListener { navigateToRecordingList(2) }
        binding.area3.setOnClickListener { navigateToRecordingList(3) }
        binding.area4.setOnClickListener { navigateToRecordingList(4) }
        binding.area5.setOnClickListener { navigateToRecordingList(5) }
        binding.area6.setOnClickListener { navigateToRecordingList(6) }
    }

    private fun navigateToRecordingList(areaNumber: Int) {
        val intent = Intent(this, AdminRecordingListActivity::class.java)
        intent.putExtra("areaNumber", areaNumber)
        startActivity(intent)
    }
}
