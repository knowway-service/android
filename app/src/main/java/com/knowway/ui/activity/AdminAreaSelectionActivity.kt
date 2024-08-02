package com.knowway.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.knowway.R

class AdminAreaSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_area_selection)

        val departmentStoreId = intent.getIntExtra("selectedDepartmentStoreId", -1)
        val departmentStoreName = intent.getStringExtra("selectedDepartmentStoreName")
        val departmentStoreBranch = intent.getStringExtra("selectedDepartmentStoreBranch")

        Log.d("ReceivedData", "넘어왔다!")
        Log.d("ReceivedData", "Department Store ID: $departmentStoreId")
        Log.d("ReceivedData", "Department Store Name: $departmentStoreName")
        Log.d("ReceivedData", "Department Store Branch: $departmentStoreBranch")

    }
}
