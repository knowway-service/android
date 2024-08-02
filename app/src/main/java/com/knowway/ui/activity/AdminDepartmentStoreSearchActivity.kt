package com.knowway.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.knowway.adapter.AdminDepartmentStoreAdapter
import com.knowway.data.model.department.DepartmentStore
import com.knowway.databinding.ActivityAdminDepartmentStoreSearchBinding
import com.knowway.databinding.CustomConfirmButtonBinding
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
            logSelectedDepartmentStore(departmentStore)
            binding.errorTextView.visibility = View.GONE
        }
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.departmentStores.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        lifecycleScope.launch {
            viewModel.searchResults.collectLatest { searchResults ->
                if (searchResults.isNotEmpty()) {
                    adapter.submitSearchResults(searchResults)
                } else {
                    adapter.clearSearchResults()
                    viewModel.departmentStores.collectLatest { pagingData ->
                        adapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    private fun setupSearchButton() {
        binding.searchImageView.setOnClickListener {
            val query = binding.inputEditText.text.toString()
            Log.d("SelectedDepartmentStore", "Selected: $query")
            if (query.isNotEmpty()) {
                viewModel.searchDepartmentStores(query)
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupConfirmButton() {
        val confirmBinding = CustomConfirmButtonBinding.bind(binding.confirmButtonContainer.root)
        confirmBinding.confirmButton.setOnClickListener {
            val selectedDepartmentStore = adapter.getSelectedDepartmentStore()
            if (selectedDepartmentStore != null) {
                val intent = Intent(this, AdminAreaSelectionActivity::class.java).apply {
                    putExtra("selectedDepartmentStoreId", selectedDepartmentStore.departmentStoreId)
                    putExtra("selectedDepartmentStoreName", selectedDepartmentStore.departmentStoreName)
                    putExtra("selectedDepartmentStoreBranch", selectedDepartmentStore.departmentStoreBranch)
                }
                startActivity(intent)
                Log.d("SelectedDepartmentStore", "Selected: $selectedDepartmentStore")
            } else {
                binding.errorTextView.visibility = View.VISIBLE
            }
        }
    }

    private fun logSelectedDepartmentStore(departmentStore: DepartmentStore) {
        Log.d("SelectedDepartmentStore", "Selected: $departmentStore")
    }
}
