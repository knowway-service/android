package com.knowway.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.knowway.R
import com.knowway.adapter.AdminDepartmentStoreAdapter
import com.knowway.ui.viewmodel.AdminDepartmentStoreViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AdminDepartmentStoreSearchActivity : AppCompatActivity() {

    private val viewModel: AdminDepartmentStoreViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminDepartmentStoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_department_store_search)

        recyclerView = findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdminDepartmentStoreAdapter()
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.departmentStores.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}
