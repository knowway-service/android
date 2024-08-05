package com.knowway.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.knowway.databinding.FragmentAdminRecordingListBinding
import com.knowway.data.model.admin.AdminRecord
import com.knowway.adapter.AdminRecordAdapter
import com.knowway.data.network.AdminApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminRecordingListFragment(
    private val departmentStoreFloorId: Long,
    private val areaNumber: Int,
    private val isInSelectionTab: Boolean,
    private val refreshData: (() -> Unit)? = null
) : Fragment() {

    private var _binding: FragmentAdminRecordingListBinding? = null
    private val binding get() = _binding!!
    private val apiService: AdminApiService by lazy { AdminApiService.create() }
    private lateinit var recordAdapter: AdminRecordAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminRecordingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadRecords()
    }

    private fun setupRecyclerView() {
        recordAdapter = AdminRecordAdapter(emptyList(), requireContext(), isInSelectionTab) {
            refreshData?.invoke()
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recordAdapter
        }
    }

    fun loadRecords() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getRecordsByFloor(departmentStoreFloorId, areaNumber.toLong(), isInSelectionTab)
            if (response.isSuccessful) {
                val records = response.body()?.map {
                    AdminRecord(it.recordId.toString(), it.recordTitle, it.recordPath)
                } ?: emptyList()
                withContext(Dispatchers.Main) {
                    recordAdapter.updateRecords(records)
                }
            } else {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
