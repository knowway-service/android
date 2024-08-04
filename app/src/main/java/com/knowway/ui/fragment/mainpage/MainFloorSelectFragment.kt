package com.knowway.ui.fragment.mainpage

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.knowway.adapter.FloorAdapter
import com.knowway.data.model.department.Floor
import com.knowway.databinding.FragmentFloorSelectBinding
import com.knowway.ui.activity.mainpage.MainPageActivity

class MainFloorSelectFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentFloorSelectBinding
    private val adapter: FloorAdapter by lazy {
        FloorAdapter { floor ->
            // 층 선택 시 처리
            (activity as? MainPageActivity)?.updateCurrentFloor(floor)
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFloorSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floorRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.floorRecyclerView.adapter = adapter

        loadAndDisplayFloorData()
    }

    private fun loadAndDisplayFloorData() {
        val sharedPreferences = requireContext().getSharedPreferences("DeptPref", MODE_PRIVATE)
        val floorIds = sharedPreferences.getString("dept_floor_ids", "")
        val floorNames = sharedPreferences.getString("dept_floor_names", "")
        val floorMapPaths = sharedPreferences.getString("dept_floor_map_paths", "")

        val floorIdList = floorIds?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
        val floorNameList = floorNames?.split(",") ?: emptyList()
        val floorMapPathList = floorMapPaths?.split(",") ?: emptyList()

        val floorList = floorIdList.zip(floorNameList).zip(floorMapPathList) { (id, name), mapPath ->
            Floor(id, name, mapPath)
        }

        adapter.submitList(floorList)
    }
}