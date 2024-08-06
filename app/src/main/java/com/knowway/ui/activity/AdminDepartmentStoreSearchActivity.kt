package com.knowway.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.knowway.R
import com.knowway.adapter.AdminDepartmentStoreAdapter
import com.knowway.data.model.department.DepartmentStoreResponse
import com.knowway.databinding.ActivityAdminDepartmentStoreSearchBinding
import com.knowway.ui.viewmodel.AdminDepartmentStoreViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminDepartmentStoreSearchActivity : AppCompatActivity() {

    private val viewModel: AdminDepartmentStoreViewModel by viewModels()
    private lateinit var adapter: AdminDepartmentStoreAdapter
    private lateinit var binding: ActivityAdminDepartmentStoreSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDepartmentStoreSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeData()
        setupSearchButton()
        setupConfirmButton()
    }

    private fun setupRecyclerView() {
        adapter = AdminDepartmentStoreAdapter { departmentStore ->
            binding.errorTextView.visibility = View.GONE
        }
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.departmentStoresResponse.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            viewModel.searchResults.collectLatest { searchResults ->
                if (searchResults.isNotEmpty()) {
                    adapter.submitSearchResults(searchResults)
                } else {
                    adapter.clearSearchResults()
                    viewModel.departmentStoresResponse.collectLatest { pagingData ->
                        adapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun setupSearchButton() {
        binding.searchImageView.setOnClickListener {
            val query = binding.inputEditText.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchDepartmentStores(query)
            } else {
            }
        }
    }

    private fun setupConfirmButton() {
        val confirmButton = findViewById<ImageButton>(R.id.confirmButton)
        confirmButton.setOnClickListener {
            val selectedDepartmentStore = adapter.getSelectedDepartmentStore()
            if (selectedDepartmentStore != null) {
                saveSelectedStore(selectedDepartmentStore)
                val intent = Intent(this, AdminAreaSelectionActivity::class.java)
                startActivity(intent)
            } else {
                binding.errorTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun saveSelectedStore(dept: DepartmentStoreResponse) {
        val sharedPreferences = getSharedPreferences("DeptPref", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("dept_id", dept.departmentStoreId)
        editor.putString("dept_name", dept.departmentStoreName)
        editor.putString("dept_branch", dept.departmentStoreBranch)

        val floorIds = dept.departmentStoreFloorResponseList.joinToString(",") { it.departmentStoreFloorId.toString() }
        editor.putString("dept_floor_ids", floorIds)

        val floorNames = dept.departmentStoreFloorResponseList.joinToString { it.departmentStoreFloor }
        editor.putString("dept_floor_names", floorNames)

        val floorMapPaths = dept.departmentStoreFloorResponseList.joinToString(",") { it.departmentStoreFloorMapPath }
        editor.putString("dept_floor_map_paths", floorMapPaths)

        editor.apply()
    }
}
