package com.knowway.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.knowway.R
import com.knowway.adapter.user.UserRecordAdapter
import com.knowway.data.model.user.UserRecord
import com.knowway.data.model.user.UserRecordResponse
import com.knowway.data.network.ApiClient
import com.knowway.data.network.user.UserApiService
import com.knowway.databinding.FragmentMypageRecordingListBinding
import retrofit2.*

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

        apiService = ApiClient.getClient().create(UserApiService::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserRecordAdapter(records, requireContext())
        binding.recyclerView.adapter = adapter

        fetchUserRecords()
    }

    private fun fetchUserRecords() {
        apiService.getRecord(0, 20, isInSelectionTab).enqueue(object :
            Callback<UserRecordResponse> {
            override fun onResponse(call: Call<UserRecordResponse>, response: Response<UserRecordResponse>) {
                if (response.isSuccessful) {
                    records.clear()
                    response.body()?.let { records.addAll(it.userRecords) }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("MypageFragment", "Failed to load user records")
                }
            }

            override fun onFailure(call: Call<UserRecordResponse>, t: Throwable) {
                Log.e("MypageFragment", "Error fetching user records", t)
            }
        })
    }
}
