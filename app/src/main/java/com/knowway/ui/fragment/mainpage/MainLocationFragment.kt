package com.knowway.ui.fragment.mainpage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.knowway.databinding.FragmentMainLocationBinding
import com.knowway.ui.activity.mainpage.MainPageActivity

class MainLocationFragment : Fragment() {
    private var _binding: FragmentMainLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.locationMap.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("DeptPref", Context.MODE_PRIVATE)
            val selectedFloorName = sharedPreferences.getString("selected_floor", "")

            val floorIds = sharedPreferences.getString("dept_floor_ids", "")
            val floorNames = sharedPreferences.getString("dept_floor_names", "")
            val floorMapPaths = sharedPreferences.getString("dept_floor_map_paths", "")

            val floorNameList = floorNames?.split(",") ?: emptyList()
            val floorMapPathList = floorMapPaths?.split(",") ?: emptyList()

            val floorMapPath = floorNameList.zip(floorMapPathList).find { it.first == selectedFloorName }?.second

            (requireActivity() as? MainPageActivity)?.showMapFragment(floorMapPath ?: "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}