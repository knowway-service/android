package com.knowway.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.knowway.databinding.FragmentAdminRecordingListBinding
import com.knowway.data.model.AdminRecord
import com.knowway.adapter.AdminRecordAdapter

class AdminRecordingListFragment(private val isInSelectionTab: Boolean) : Fragment() {

    private var _binding: FragmentAdminRecordingListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminRecordingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val records = listOf(
            AdminRecord("영등포점 맛집 안내", "https://know-way-record.s3.ap-northeast-2.amazonaws.com/file_example_MP3_700KB.mp3"),
            AdminRecord("목동점 엘리베이터", "https://know-way-record.s3.ap-northeast-2.amazonaws.com/file_example_MP3_700KB.mp3"),
            AdminRecord("목동점 화장실 앞", "https://know-way-record.s3.ap-northeast-2.amazonaws.com/file_example_MP3_700KB.mp3"),
            AdminRecord("목동점 엘리베이터", "https://know-way-record.s3.ap-northeast-2.amazonaws.com/file_example_MP3_700KB.mp3")
        )

        val recordAdapter = AdminRecordAdapter(records, requireContext(), isInSelectionTab)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recordAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
