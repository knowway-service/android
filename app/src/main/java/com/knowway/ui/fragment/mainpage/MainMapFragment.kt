package com.knowway.ui.fragment.mainpage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.knowway.R
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
        loadMapImage(arguments?.getString("map_path") ?: "")

        binding.imageView.setImageDrawable(null)
        val mapPath = arguments?.getString("map_path") ?: return

        if (mapPath.isNotEmpty()) {
            binding.imageView.post {
                Glide.with(this)
                    .load(mapPath)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.picachu)
                    .error(R.drawable.record)
                    .into(binding.imageView)
            }
        }
    }

    fun loadMapImage(mapPath: String) {
        if (mapPath.isNotEmpty()) {
            binding.imageView.post {
                Glide.with(this)
                    .load(mapPath)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.picachu)
                    .error(R.drawable.record)
                    .into(binding.imageView)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}