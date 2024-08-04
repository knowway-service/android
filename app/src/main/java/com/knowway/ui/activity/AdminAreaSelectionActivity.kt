package com.knowway.ui.activity

import MainFloorSelectFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.knowway.data.network.AdminApiService
import com.knowway.databinding.ActivityAdminAreaSelectionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminAreaSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAreaSelectionBinding
    private val apiService: AdminApiService by lazy { AdminApiService.create() }
    private var departmentStoreFloorId: Long = -1L

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

        // Load the default floor map image
        loadFloorMapImage(departmentStoreId.toLong(), "1F")
    }

    private fun loadFloorMapImage(deptId: Long, floor: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getDepartmentStoreFloorMap(deptId, floor)
            if (response.isSuccessful) {
                response.body()?.let { floorMapResponse ->
                    departmentStoreFloorId = floorMapResponse.departmentStoreFloorId
                    withContext(Dispatchers.Main) {
                        val imageUrl = floorMapResponse.departmentStoreFloorMapPath
                        Log.d("ImageURL", "Loading image from URL: $imageUrl")
                        Glide.with(this@AdminAreaSelectionActivity)
                            .load(imageUrl)
                            .into(binding.imgPartDialog)
                    }
                }
            } else {
                Log.e("API Error", "Failed to load floor map image")
            }
        }
    }

    private fun navigateToRecordingList(areaNumber: Int) {
        val intent = Intent(this, AdminRecordingListActivity::class.java).apply {
            putExtra("areaNumber", areaNumber)
            putExtra("departmentStoreFloorId", departmentStoreFloorId)
        }
        startActivity(intent)
    }
}
