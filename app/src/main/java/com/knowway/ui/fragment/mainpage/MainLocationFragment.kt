package com.knowway.ui.fragment.mainpage

import android.content.Context
import android.os.Bundle
import android.util.Log
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
            val mapPath = sharedPreferences.getString("selected_floor_map_path", "")

            if (!mapPath.isNullOrEmpty()) {
                (requireActivity() as? MainPageActivity)?.showMapFragment(mapPath)
            } else {
                Log.e("MainLocationFragment", "Map path is empty or null")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}