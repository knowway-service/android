package com.knowway.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.knowway.adapter.user.UserRecordAdapter
import com.knowway.data.model.user.UserRecord
import com.knowway.data.network.ApiClient
import com.knowway.data.network.user.UserApiService
import com.knowway.databinding.FragmentMypageRecordingListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageRecordingListFragment(private val isInSelectionTab: Boolean) : Fragment() {

    private lateinit var binding: FragmentMypageRecordingListBinding
    private lateinit var apiService: UserApiService
    private lateinit var adapter: UserRecordAdapter
    private val records = mutableListOf<UserRecord>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageRecordingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = ApiClient.getClient().create(UserApiService::class.java)

        adapter = UserRecordAdapter(records, requireContext(), isInSelectionTab)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchUserRecords()
    }

    override fun onResume() {
        super.onResume()
        fetchUserRecords()
    }

    private fun fetchUserRecords() {
        val apiCall = if (isInSelectionTab) {
            apiService.getRecord(0, 20, true)
        } else {
            apiService.getRecord(0, 20, null)
        }

        apiCall.enqueue(object : Callback<List<UserRecord>> {
            override fun onResponse(call: Call<List<UserRecord>>, response: Response<List<UserRecord>>) {
                if (response.isSuccessful) {
                    response.body()?.let { newRecords ->
                        records.clear()
                        records.addAll(newRecords)
                        adapter.notifyDataSetChanged()
                        updateEmptyStateView()
                    } ?: run {
                        Log.e("MypageRecordingListFragment", "Response body is null")
                        updateEmptyStateView()
                    }
                } else {
                    Log.e("MypageRecordingListFragment", "Failed to load user records. Response code: ${response.code()}")
                    updateEmptyStateView()
                }
            }

            override fun onFailure(call: Call<List<UserRecord>>, t: Throwable) {
                Log.e("MypageRecordingListFragment", "Error fetching user records", t)
                updateEmptyStateView()
            }
        })
    }

    private fun updateEmptyStateView() {
        if (records.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyStateText.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyStateText.visibility = View.GONE
        }
    }
}

