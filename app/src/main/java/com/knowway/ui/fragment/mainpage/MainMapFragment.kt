package com.knowway.ui.fragment.mainpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.knowway.databinding.FragmentMainMapBinding

class MainMapFragment : Fragment() {
    private var _binding: FragmentMainMapBinding? = null
    private val binding get() = _binding!!

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

        val mapPath = arguments?.getString("map_path") ?: return

        if (mapPath.isNotEmpty()) {
            Glide.with(this)
                .load(mapPath)  // S3 경로 또는 URL
                .into(binding.imageView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}