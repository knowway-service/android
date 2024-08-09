package com.knowway.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.knowway.R
import com.knowway.adapter.DepartmentStoreAdapter
import com.knowway.data.exceptions.department.*
import com.knowway.data.exceptions.location.LocationException
import com.knowway.data.exceptions.location.LocationLoadException
import com.knowway.data.exceptions.location.LocationPermissionException
import com.knowway.data.exceptions.location.LocationUnknownException
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.data.repository.DepartmentStoreRepository
import com.knowway.databinding.ActivityDepartmentStoreSearchBinding
import com.knowway.ui.activity.mainpage.MainPageActivity
import com.knowway.ui.fragment.SelectFooterFragment
import com.knowway.ui.viewmodel.department.DepartmentStoreViewModel
import com.knowway.ui.viewmodel.department.DepartmentStoreViewModelFactory
import com.knowway.ui.viewmodel.department.LocationViewModel
import com.knowway.ui.viewmodel.department.LocationViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * 백화점 액티비티
 *
 * @author 김진규
 * @since 2024.07.25
 * @version 1.0
 */
class DepartmentStoreSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDepartmentStoreSearchBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val deptViewModel: DepartmentStoreViewModel by viewModels {
        DepartmentStoreViewModelFactory(DepartmentStoreRepository())
    }

    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory(this, fusedLocationClient)
    }

    private lateinit var adapter: DepartmentStoreAdapter
    private var selectedStore: DepartmentStoreResponse? = null
    private var destination: Int = 0

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

        destination = intent.getIntExtra("key", 0)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationViewModel.getLastKnownLocation()
        }

        binding.searchRv.layoutManager = LinearLayoutManager(this)
        adapter = DepartmentStoreAdapter(emptyList()) { selectedStore ->
            this.selectedStore = selectedStore
        }
        binding.searchRv.adapter = adapter

        binding.searchImageView.setOnClickListener {
            val query = binding.inputEditText.text.toString()
            if (query.isNotEmpty()) {
                deptViewModel.getDepartmentStoreByBranch(query)
            }
        }

        binding.nextBtn.setOnClickListener {
            if (selectedStore == null) {
                binding.errorText.visibility = View.VISIBLE
            } else {
                binding.errorText.visibility = View.GONE
                saveSelectedStore(selectedStore!!)
                if (destination == 0) {
                    navigateToMainPageActivity()
                } else {
                    navigateToChatActivity()
                }
            }
        }

        lifecycleScope.launch {
            deptViewModel.departmentStoresResponse.collect { departmentStores ->
                adapter.update(departmentStores)
            }
        }

        lifecycleScope.launch {
            locationViewModel.location.collect { locationResponse ->
                locationResponse?.let {
                    deptViewModel.getDepartmentStoresByLocation(it.latitude, it.longitude)
                    deptViewModel.departmentStoresResponse.collect { departmentStores ->
                        adapter.update(departmentStores)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            combine(
                deptViewModel.error,
                locationViewModel.error
            ) { deptErr, locationErr ->
                deptErr?.let { handleDeptException(it) }
                locationErr?.let { handleLocationException(it) }
            }.collect()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.footer_fragment_container, SelectFooterFragment())
                .commit()
        }
    }

    private fun handleLocationException(ex: LocationException) {
        val msg = when (ex) {
            is LocationPermissionException -> ex.message
            is LocationLoadException -> ex.message
            is LocationUnknownException -> ex.message
            else -> "위치 에러가 발생했습니다. ${ex.message}"
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        Log.e("위치 에러 발생", msg, ex)
    }

    private fun handleDeptException(ex: DepartmentStoreException) {
        val msg = when (ex) {
            is DeptNetworkException -> ex.message
            is DeptByBranchApiException -> ex.message
            is DeptByLocationApiException -> ex.message
            is DeptUnknownException -> ex.message
            else -> "백화점 리스팅 에러가 발생했습니다. ${ex.message}"
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        Log.e("백화점 리스팅 페이지 오류 발생", msg, ex)
    }

    private fun saveSelectedStore(dept: DepartmentStoreResponse) {
        val sharedPreferences = getSharedPreferences("DeptPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("dept_id", dept.departmentStoreId)
        editor.putString("dept_name", dept.departmentStoreName)
        editor.putString("dept_branch", dept.departmentStoreBranch)
        editor.putString("dept_latitude", dept.departmentStoreLatitude.toString())
        editor.putString("dept_longitude", dept.departmentStoreLongitude.toString())

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

    private fun navigateToChatActivity() {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
}