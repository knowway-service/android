package com.knowway.ui.activity.mainpage

import MainFloorSelectFragment
import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.knowway.R
import com.knowway.data.exceptions.mainpage.MainPageApiException
import com.knowway.data.exceptions.mainpage.MainPageException
import com.knowway.data.exceptions.mainpage.MainPageNetworkException
import com.knowway.data.exceptions.mainpage.MainPageUnknownException
import com.knowway.data.model.department.Floor
import com.knowway.data.repository.MainPageRepository
import com.knowway.databinding.ActivityMainPageBinding
import com.knowway.ui.fragment.MapFooterFragment
import com.knowway.ui.fragment.OnAudioCompletionListener
import com.knowway.ui.fragment.OnToggleChangeListener
import com.knowway.ui.fragment.RecordFragment
import com.knowway.ui.fragment.mainpage.MainMapFragment
import com.knowway.ui.fragment.mainpage.MainPersonFragment
import com.knowway.ui.fragment.mainpage.MainRecordTipFragment
import com.knowway.ui.viewmodel.mainpage.MainPageViewModel
import com.knowway.ui.viewmodel.mainpage.MainPageViewModelFactory
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 메인페이지 액티비티
 *
 * @author 김진규
 * @since 2024.08.04
 * @version 1.0
 */
class MainPageActivity : AppCompatActivity(), OnToggleChangeListener, OnAudioCompletionListener {
    private lateinit var binding: ActivityMainPageBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var slidingUpPanelLayout: SlidingUpPanelLayout
    private lateinit var backLayout: View

    private var currentFloor: Floor? = null
    private val viewModel: MainPageViewModel by viewModels {
        MainPageViewModelFactory(MainPageRepository())
    }

    private var displayFlag = false
    private val range = 10.0
    private var isAutoPlayEnabled = false
    private var currentRecordTipFragment: MainRecordTipFragment ?= null
    private var isProximityCheckRunning = false
    private var pendingCheckProximityData: ProximityData? = null

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

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

        binding.mainFrame.addPanelSlideListener(PanelEventListener())
        backLayout = binding.backLayout

        binding.mainRecord.setOnClickListener {
            slidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            replaceFragment()
        }

        sharedPreferences = getSharedPreferences("DeptPref", MODE_PRIVATE)
        updateDeptInfo()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationCallback()

        if (sharedPreferences.getLong("dept_id", -1) != -1L
            && sharedPreferences.getLong("selected_floor_id", -1) != -1L) {
            viewModel.getRecordsByDeptAndFloor(sharedPreferences.getLong("dept_id", -1), sharedPreferences.getLong("selected_floor_id", -1))
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startUpdatingLocation()
        }

        lifecycleScope.launch {
            viewModel.recordsResponse.collect { records ->
                records.forEach { record ->
                    checkProximity(
                        record.recordPath,
                        currentLatitude,
                        currentLongitude,
                        record.recordLatitude.toDouble(),
                        record.recordLongitude.toDouble()
                    )
                }
            }
        }

        binding.mainUpDown.setOnClickListener {
            MainFloorSelectFragment().show(supportFragmentManager, "층 선택 모달창")
        }

        setupInitialFragments()
        setupButtonClickListener()

        lifecycleScope.launchWhenStarted {
            viewModel.error.collect { e ->
                e?.let {
                    handleException(it)
                }
            }
        }
    }

    private fun setupButtonClickListener() {
        val clickListener = View.OnClickListener {
            if (displayFlag) {
                binding.buttonIcon.setBackgroundResource(R.drawable.location)
                binding.buttonText.text = "지도 보기"
                showPerson()
            } else {
                binding.buttonIcon.setBackgroundResource(R.drawable.back)
                binding.buttonText.text = "돌아가기"
                val mapPath = sharedPreferences.getString("selected_floor_map_path", "")
                if (!mapPath.isNullOrEmpty()) {
                    showMap(mapPath)
                }
            }
            displayFlag = !displayFlag
        }

        binding.buttonContainer.setOnClickListener(clickListener)
        binding.buttonIcon.setOnClickListener(clickListener)
        binding.buttonText.setOnClickListener(clickListener)
    }

    private fun showPerson() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.card_fragment_container, MainPersonFragment())
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .commit()
    }

    private fun setupInitialFragments() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.card_fragment_container, MainPersonFragment())
            .replace(R.id.footer_container, MapFooterFragment().apply {
                setOnToggleChangeListener(this@MainPageActivity)
            })
            .replace(R.id.fragment_container, RecordFragment())
            .commit()
    }

    private fun handleException(ex: MainPageException) {
        Toast.makeText(
            this, when (ex) {
                is MainPageNetworkException -> ex.message
                is MainPageApiException -> ex.message
                is MainPageUnknownException -> ex.message
                else -> "에러가 발생했습니다. ${ex.message}"
            }, Toast.LENGTH_LONG
        ).show()
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    lifecycleScope.launch {
                        fetchRecordsAndUpdateMapUI(location)
                    }
                }
            }
        }
    }

    private suspend fun fetchRecordsAndUpdateMapUI(location: Location) {
        viewModel.recordsResponse.collect { records ->
            records
                .filter { it.floorId == currentFloor?.departmentStoreFloorId }
                .forEach { record ->
                    checkProximity(
                        record.recordPath,
                        location.latitude,
                        location.longitude,
                        record.recordLatitude.toDouble(),
                        record.recordLongitude.toDouble()
                    )
                }

            withContext(Dispatchers.Main) {
                val mainMapFragment =
                    supportFragmentManager.findFragmentById(R.id.card_fragment_container) as? MainMapFragment
                if (mainMapFragment != null) {
                    mainMapFragment.updateLocationOnMap(location.latitude, location.longitude)
                } else {
                    Log.e("층 지도 에러 발생", "층 지도를 로드하지 못했습니다.")
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

    private fun checkProximity(
        recordPath: String,
        curLat: Double,
        curLnt: Double,
        targetLat: Double,
        targetLnt: Double
    ) {
        if (isProximityCheckRunning) {
            pendingCheckProximityData = ProximityData(recordPath, curLat, curLnt, targetLat, targetLnt)
            return
        }

        val currentLocation = Location("current").apply {
            latitude = curLat
            longitude = curLnt
        }

        var targetLocation = Location("target").apply {
            latitude = targetLat
            longitude = targetLnt
        }

        val fragment = supportFragmentManager.findFragmentById(R.id.card_fragment_container) as? MainPersonFragment
        if (currentLocation.distanceTo(targetLocation) <= range) {
            fragment?.showQuestionButton(recordPath)
            if (isAutoPlayEnabled && currentRecordTipFragment == null) {
                isProximityCheckRunning = true
                currentRecordTipFragment = MainRecordTipFragment().apply {
                    setOnAudioCompletionListener(this@MainPageActivity)
                    playAudio(recordPath)
                }
            }
        } else {
            fragment?.hideQuestionButton()
            stopAndReleaseCurrentRecordTipFragment()
        }
    }


    private fun updateDeptInfo() {
        val floorIdList = sharedPreferences.getString("dept_floor_ids", "")?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
        val floorNameList = sharedPreferences.getString("dept_floor_names", "")?.split(",") ?: emptyList()
        val floorMapPathList = sharedPreferences.getString("dept_floor_map_paths", "")?.split(",") ?: emptyList()

        binding.mainDeptTitle.text = sharedPreferences.getString("dept_name", "백화점")
        binding.mainDeptBranch.text = sharedPreferences.getString("dept_branch", "지점")

        currentFloor = floorIdList.zip(floorNameList).zip(floorMapPathList) { (id, name), mapPath ->
            Floor(id, name, mapPath)
        }.firstOrNull()
        binding.mainFloor.text = currentFloor?.departmentStoreFloor ?: "정보 없음"

        currentFloor?.let { sharedPreferences.edit().putLong("selected_floor_id", it.departmentStoreFloorId).apply() }
        sharedPreferences.edit().putString("selected_floor_map_path", currentFloor?.departmentStoreMapPath).apply()
    }

    fun updateCurrentFloor(floor: Floor) {
        currentFloor = floor
        binding.mainFloor.text = floor.departmentStoreFloor

        sharedPreferences.edit().putLong("selected_floor_id", floor.departmentStoreFloorId).apply()
        sharedPreferences.edit().putString("selected_floor_map_path", floor.departmentStoreMapPath).apply()

        (supportFragmentManager.findFragmentById(R.id.card_fragment_container) as? MainMapFragment)?.loadMapImage(floor.departmentStoreMapPath)

        val deptId = sharedPreferences.getLong("dept_id", -1)
        if (deptId != -1L) {
            viewModel.getRecordsByDeptAndFloor(deptId, floor.departmentStoreFloorId)
        }

        clearProximityCheck()
    }

    private fun clearProximityCheck() {
        stopAndReleaseCurrentRecordTipFragment()
        (supportFragmentManager.findFragmentById(R.id.card_fragment_container) as? MainPersonFragment)?.hideQuestionButton()
    }

    private fun showMap(mapPath: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.card_fragment_container, MainMapFragment().apply {
                arguments = Bundle().apply {
                    putString("map_path", mapPath)
                }
            })
            .addToBackStack(null)
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .commit()
    }

    private fun replaceFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                RecordFragment.newInstance(currentLatitude, currentLongitude)
            )
            .commit()
    }

    fun collapsePanel() {
        slidingUpPanelLayout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    override fun onToggleChanged(isToggled: Boolean) {
        isAutoPlayEnabled = isToggled
        if (!isToggled) {
            stopAndReleaseCurrentRecordTipFragment()
        } else {
            pendingCheckProximityData?.let {
                checkProximity(
                    it.recordPath,
                    it.curLat,
                    it.curLnt,
                    it.targetLat,
                    it.targetLnt
                )
                pendingCheckProximityData = null
            }
        }
    }

    private fun stopAndReleaseCurrentRecordTipFragment() {
        isProximityCheckRunning = false
        currentRecordTipFragment?.stopAndReleaseMediaPlayer()
        currentRecordTipFragment = null
    }

    override fun onAudioCompleted() {
        isProximityCheckRunning = false
        pendingCheckProximityData?.let {
            checkProximity(
                it.recordPath,
                it.curLat,
                it.curLnt,
                it.targetLat,
                it.targetLnt
            )
            pendingCheckProximityData = null
        }
    }

    inner class PanelEventListener : SlidingUpPanelLayout.PanelSlideListener {
        override fun onPanelSlide(panel: View?, slideOffset: Float) {}
        override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {}
    }

    data class ProximityData (
        val recordPath: String,
        val curLat: Double,
        val curLnt: Double,
        val targetLat: Double,
        val targetLnt: Double
    )
}
