package com.knowway.ui.fragment.mainpage

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.knowway.R
import com.knowway.databinding.FragmentMainMapBinding

/**
 * 메인페이지-지도보기 프래그먼트
 *
 * @author 김진규
 * @since 2024.08.01
 * @version 1.0
 */
class MainMapFragment : Fragment() {
    private var _binding: FragmentMainMapBinding? = null
    private val binding get() = _binding!!

    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var imgLatRange: Double = 0.0
    private var imgLonRange: Double = 0.0

    private var imageTopLeftLatitude: Double = 0.0
    private var imageTopLeftLongitude: Double = 0.0
    private var imageBottomRightLatitude: Double = 0.0
    private var imageBottomRightLongitude: Double = 0.0

    private val mapCenterLat: Double by lazy { getDeptLat() }
    private val mapCenterLon: Double by lazy { getDeptLon() }

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    private fun getDeptLat(): Double {
        return requireActivity().getSharedPreferences("DeptPref", Context.MODE_PRIVATE).getString("dept_latitude", "0.0")?.toDouble() ?: 0.0
    }

    private fun getDeptLon(): Double {
        return requireActivity().getSharedPreferences("DeptPref", Context.MODE_PRIVATE).getString("dept_longitude", "0.0")?.toDouble() ?: 0.0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadMapImage(arguments?.getString("map_path") ?: "")
    }

    fun loadMapImage(mapPath: String) {
        if (mapPath.isNotEmpty()) {
            Glide.with(this)
                .load(mapPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.loading)
                .error(R.drawable.not_found)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?) {
                        binding.map.setImageDrawable(resource)
                        binding.map.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                imageWidth = binding.map.width
                                imageHeight = binding.map.height

                                val latitudeRangePerPixel = 0.0001
                                val longitudeRangePerPixel = 0.0001

                                imageTopLeftLatitude = mapCenterLat + (imageHeight / 2 * latitudeRangePerPixel)
                                imageTopLeftLongitude = mapCenterLon - (imageWidth / 2 * longitudeRangePerPixel)

                                imageBottomRightLatitude = mapCenterLat - (imageHeight / 2 * latitudeRangePerPixel)
                                imageBottomRightLongitude = mapCenterLon + (imageWidth / 2 * longitudeRangePerPixel)

                                binding.map.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        })
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    fun updateLocationOnMap(latitude: Double, longitude: Double) {
        currentLatitude = latitude
        currentLongitude = longitude
        calculateLoc()
    }

    private fun calculateLoc() {
        imgLatRange = calculateLatRange()
        imgLonRange = calculateLonRange()

        if (imageWidth > 0 && imageHeight > 0) {
            val xPercent = (currentLongitude - imageTopLeftLongitude) / imgLonRange
            val yPercent = (imageTopLeftLatitude - currentLatitude) / imgLatRange

            if (xPercent in 0.0..1.0 && yPercent in 0.0..1.0) {
                updateLocationMarker(xPercent, yPercent)
            } else {
                Log.e("MapDebug", "Percentage values out of bounds: xPercent=$xPercent, yPercent=$yPercent")
            }
        } else {
            Log.e("MapDebug", "지도 이미지 에러")
        }
    }

    private fun updateLocationMarker(xPercent: Double, yPercent: Double) {
        binding.locationMarker.post {
            val layoutParams = binding.locationMarker.layoutParams as? ConstraintLayout.LayoutParams
            if (layoutParams != null) {
                layoutParams.leftMargin = ((binding.map.width * xPercent).toFloat() - binding.locationMarker.width / 2).toInt()
                layoutParams.topMargin = ((binding.map.height * yPercent).toFloat() - binding.locationMarker.height / 2).toInt()

                binding.locationMarker.layoutParams = layoutParams
                binding.locationMarker.requestLayout()
            } else {
                Log.e("MapDebug", "LocationMarker layoutParams is not ConstraintLayout.LayoutParams")
            }
            binding.locationMarker.visibility = View.VISIBLE
        }
    }

    private fun calculateLatRange(): Double {
        return imageTopLeftLatitude - imageBottomRightLatitude
    }

    private fun calculateLonRange(): Double {
        return imageBottomRightLongitude - imageTopLeftLongitude
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}