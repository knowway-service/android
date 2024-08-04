package com.knowway.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.knowway.Constants.url
import com.knowway.adapter.DepartmentStoreAdapter
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.repository.DepartmentStoreRepository
import com.knowway.databinding.ActivityDepartmentStoreSearchBinding
import com.knowway.ui.activity.mainpage.MainPageActivity
import com.knowway.ui.viewmodel.department.DepartmentStoreViewModel
import com.knowway.ui.viewmodel.department.DepartmentStoreViewModelFactory
import com.knowway.ui.viewmodel.department.LocationViewModel
import com.knowway.ui.viewmodel.department.LocationViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DepartmentStoreSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDepartmentStoreSearchBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val deptViewModel: DepartmentStoreViewModel by viewModels {
        DepartmentStoreViewModelFactory(DepartmentStoreRepository(url))
    }

    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory(this, fusedLocationClient)
    }

    private lateinit var adapter: DepartmentStoreAdapter

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            locationViewModel.getLastKnownLocation()
        } else {
            Log.e("위치 권한 오류", "위치에 대한 권한이 없습니다.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDepartmentStoreSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationViewModel.getLastKnownLocation()
        }

        binding.searchRv.layoutManager = LinearLayoutManager(this)
        adapter = DepartmentStoreAdapter(emptyList()) { selectedStore ->
            saveSelectedStore(selectedStore)
            navigateToMainPageActivity()
        }
        binding.searchRv.adapter = adapter

        lifecycleScope.launch {
            locationViewModel.location.collect { locationResponse ->
                locationResponse?.let {
                    Log.d("위치 정보", "Location received: Latitude=${locationResponse.latitude}, Longitude=${locationResponse.longitude}")
                    deptViewModel.getDepartmentStoresByLocation(it.latitude, it.longitude)
                    deptViewModel.departmentStoresResponse.collect { departmentStores ->
                        adapter.update(departmentStores)
                        Log.d("위치 정보 업데이트됨", "Department stores updated: ${departmentStores.size} items")
                    }
                }
            }
        }
    }

    private fun saveSelectedStore(dept: DepartmentStoreResponse) {
        val sharedPreferences = getSharedPreferences("DeptPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("dept_id", dept.departmentStoreId)
        editor.putString("dept_name", dept.departmentStoreName)
        editor.putString("dept_branch", dept.departmentStoreBranch)

        val floorIds = dept.departmentStoreFloorResponseList.joinToString(",") { it.departmentStoreFloorId.toString() }
        editor.putString("dept_floor_ids", floorIds)

        val floorNames = dept.departmentStoreFloorResponseList.joinToString { it.departmentStoreFloor }
        editor.putString("dept_floor_names", floorNames)

        val floorMapPaths = dept.departmentStoreFloorResponseList.joinToString(",") { it.departmentStoreFloorMapPath }
        editor.putString("dept_floor_map_paths", floorMapPaths)

        editor.apply()
    }

    private fun navigateToMainPageActivity() {
        val intent = Intent(this, MainPageActivity::class.java)
        startActivity(intent)
    }
}