package com.knowway.ui.activity.mainpage

import MainFloorSelectFragment
import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.knowway.Constants.url
import com.knowway.R
import com.knowway.data.model.department.Floor
import com.knowway.data.repository.MainPageRepository
import com.knowway.databinding.ActivityMainPageBinding
import com.knowway.ui.fragment.MapFooterFragment
import com.knowway.ui.fragment.RecordFragment
import com.knowway.ui.fragment.mainpage.MainBackFragment
import com.knowway.ui.fragment.mainpage.MainLocationFragment
import com.knowway.ui.fragment.mainpage.MainMapFragment
import com.knowway.ui.fragment.mainpage.MainPersonFragment
import com.knowway.ui.viewmodel.mainpage.MainPageViewModel
import com.knowway.ui.viewmodel.mainpage.MainPageViewModelFactory
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainPageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainPageBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recordFragment: RecordFragment
    private lateinit var slidingUpPanelLayout: SlidingUpPanelLayout
    private lateinit var backLayout: View

    private var currentFloor: Floor? = null
    private val viewModel: MainPageViewModel by viewModels {
        MainPageViewModelFactory(MainPageRepository(url))
    }

    private var displayFlag = false
    private val range = 5.0

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            startUpdatingLocation()
        } else {
            Log.e("위치 권한 오류", "위치에 대한 권한이 없습니다.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        slidingUpPanelLayout = findViewById(R.id.main_frame)
        slidingUpPanelLayout.addPanelSlideListener(PanelEventListener())
        backLayout = findViewById(R.id.back_layout)

        val recordingBtn: ImageView = findViewById(R.id.main_record)
        recordingBtn.setOnClickListener {
            slidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            replaceFragment()
        }

        sharedPreferences = getSharedPreferences("DeptPref", MODE_PRIVATE)

        updateDeptInfo()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupLocationCallback()

        val deptId = sharedPreferences.getLong("dept_id", -1)
        val floorId = sharedPreferences.getLong("selected_floor_id", -1)

        if (deptId != -1L && floorId != -1L) {
            viewModel.getRecordsByDeptAndFloor(deptId, floorId)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startUpdatingLocation()
        }

        binding.mainUpDown.setOnClickListener {
            val floorSelectModal = MainFloorSelectFragment()
            floorSelectModal.show(supportFragmentManager, "층 선택 모달창")
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.button_fragment_container, MainLocationFragment())
                .replace(R.id.card_fragment_container, MainPersonFragment())
                .replace(R.id.footer_container, MapFooterFragment())
                .replace(R.id.fragment_container, RecordFragment())
                .commit()
        }

        binding.buttonFragmentContainer.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()

            if (displayFlag) {
                transaction.replace(R.id.button_fragment_container, MainLocationFragment())
                    .replace(R.id.card_fragment_container, MainPersonFragment())
                    .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                    .addToBackStack(null)
            } else {
                transaction.replace(R.id.button_fragment_container, MainBackFragment())
                    .replace(R.id.card_fragment_container, MainMapFragment())
                    .addToBackStack(null)
                    .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
            }

            try {
                transaction.commit()
            } catch (e: Exception) {
                Log.e("MainPageActivity", "Fragment transaction failed", e)
            }

            displayFlag = !displayFlag
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    lifecycleScope.launch {
                        viewModel.recordsResponse.collect { records ->
                            records.forEach { record ->
                                val latitude = record.recordLatitude.toDouble()
                                val longitude = record.recordLongitude.toDouble()
                                checkProximity(location.latitude, location.longitude, latitude, longitude)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startUpdatingLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setMinUpdateIntervalMillis(1500)
            .build()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun stopUpdatingLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        stopUpdatingLocation()
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            startUpdatingLocation()
        }
    }

    private fun checkProximity(curLat: Double, curLnt: Double, targetLat: Double, targetLnt: Double) {
        val currentLocation = Location("current").apply {
            latitude = curLat
            longitude = curLnt
        }

        var targetLocation = Location("target").apply {
            latitude = targetLat
            longitude = targetLnt
        }

        val distance = currentLocation.distanceTo(targetLocation)
        val fragment = supportFragmentManager.findFragmentById(R.id.card_fragment_container) as? MainPersonFragment
        fragment?.let {
            if (distance <= range) {
                it.showQuestionButton()
            } else {
                it.hideQuestionButton()
            }
        }
    }

    private fun updateDeptInfo() {
        val deptName = sharedPreferences.getString("dept_name", "백화점")
        val deptBranch = sharedPreferences.getString("dept_branch", "지점")

        val floorIds = sharedPreferences.getString("dept_floor_ids", "")
        val floorNames = sharedPreferences.getString("dept_floor_names", "")
        val floorMapPaths = sharedPreferences.getString("dept_floor_map_paths", "")

        val floorIdList = floorIds?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
        val floorNameList = floorNames?.split(",") ?: emptyList()
        val floorMapPathList = floorMapPaths?.split(",") ?: emptyList()

        val floorList = floorIdList.zip(floorNameList).zip(floorMapPathList) { (id, name), mapPath ->
            Floor(id, name, mapPath)
        }

        binding.mainDeptTitle.text = deptName
        binding.mainDeptBranch.text = deptBranch

        currentFloor = floorList.firstOrNull()
        binding.mainFloor.text = currentFloor?.departmentStoreFloor ?: "정보 없음"
    }

    fun updateCurrentFloor(floor: Floor) {
        currentFloor = floor
        binding.mainFloor.text = floor.departmentStoreFloor

        sharedPreferences.edit().putLong("selected_floor_id", floor.departmentStoreFloorId).apply()
        sharedPreferences.edit().putString("selected_floor_map_path", floor.departmentStoreMapPath).apply()
    }

    fun showMapFragment(mapPath: String) {
        val fragment = MainMapFragment().apply {
            arguments = Bundle().apply {
                putString("map_path", mapPath)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.card_fragment_container, fragment)
            .addToBackStack(null)
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("MainPageActivity", "Back pressed")
    }

    private fun replaceFragment() {
        recordFragment = RecordFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, recordFragment)
            .commit()
    }

    fun collapsePanel() {
        slidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    inner class PanelEventListener : SlidingUpPanelLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {
            // Do something when the panel is sliding
        }

        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
            // Do something when the panel state changes
        }
    }
}
