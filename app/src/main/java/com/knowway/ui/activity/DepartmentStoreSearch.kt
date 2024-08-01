package com.knowway.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowway.BuildConfig
import com.knowway.R
import com.knowway.adapter.DepartmentStoreAdapter
import com.knowway.data.model.department.DepartmentStore
import com.knowway.data.repository.DepartmentStoreRemoteDataSource
import com.knowway.ui.viewmodel.department.DepartmentStoreViewModel
import com.knowway.ui.viewmodel.department.DepartmentStoreViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DepartmentStoreSearch : AppCompatActivity() {
    private val viewModel: DepartmentStoreViewModel by viewModels {
        DepartmentStoreViewModelFactory(DepartmentStoreRemoteDataSource("http://${BuildConfig.BASE_IP_ADDRESS}:8080"))
    }

    private lateinit var adapter: DepartmentStoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department_store_search)

        val recyclerView: RecyclerView = findViewById(R.id.search_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.getDepartmentStores(5, 0)

        adapter = DepartmentStoreAdapter(emptyList())
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.departmentStores.collect { departmentStores ->
                adapter.update(departmentStores)
            }
        }
    }
}