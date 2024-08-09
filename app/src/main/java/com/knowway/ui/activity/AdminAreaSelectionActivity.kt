package com.knowway.ui.activity

import MainFloorSelectFragment
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.knowway.R
import com.knowway.databinding.ActivityAdminAreaSelectionBinding

/**
 * 관리자 구역 선택 페이지 Activity
 *
 * @author 이주현
 * @since 2024.08.02
 * @version 1.0
 */

class AdminAreaSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAreaSelectionBinding
    private var departmentStoreFloorId: Long = -1L
    private var departmentStoreId: Long = -1L

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if (key == "selected_floor_id" || key == "selected_floor_name" || key == "selected_floor_map_path") {
            updateFloorInfo(sharedPreferences)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAreaSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("DeptPref", MODE_PRIVATE)
        departmentStoreId = sharedPreferences.getLong("dept_id", -1L)
        val floorMapPaths = sharedPreferences.getString("dept_floor_map_paths", "").orEmpty().split(",")

        binding.imgPartDialog.clipToOutline = true

        binding.icFloorSelect.setOnClickListener {
            val floorSelectModal = MainFloorSelectFragment()
            floorSelectModal.show(supportFragmentManager, "층 선택 모달창")
        }

        binding.area1.setOnClickListener { navigateToRecordingList(1) }
        binding.area2.setOnClickListener { navigateToRecordingList(2) }
        binding.area3.setOnClickListener { navigateToRecordingList(3) }
        binding.area4.setOnClickListener { navigateToRecordingList(4) }
        binding.area5.setOnClickListener { navigateToRecordingList(5) }
        binding.area6.setOnClickListener { navigateToRecordingList(6) }

        if (floorMapPaths.isNotEmpty() && floorMapPaths[0].isNotEmpty()) {
            loadFloorMapImage(floorMapPaths[0])
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("FloorPref", MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        updateFloorInfo(sharedPreferences)
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences = getSharedPreferences("FloorPref", MODE_PRIVATE)
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun updateFloorInfo(sharedPreferences: SharedPreferences) {
        val floorId = sharedPreferences.getLong("selected_floor_id", -1L)
        val floorName = sharedPreferences.getString("selected_floor_name", "1F")
        val floorMapPath = sharedPreferences.getString("selected_floor_map_path", "")

        departmentStoreFloorId = floorId
        binding.floorText.text = floorName
        loadFloorMapImage(floorMapPath)
    }

    private fun loadFloorMapImage(floorMapPath: String?) {
        if (floorMapPath.isNullOrEmpty()) {
            binding.imgPartDialog.setImageResource(R.drawable.map_1)
        } else {
            Glide.with(this)
                .load(floorMapPath)
                .into(binding.imgPartDialog)
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
