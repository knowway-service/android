package com.knowway.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.paging.PagingDataAdapter
import com.knowway.R
import com.knowway.data.model.department.DepartmentStore

class AdminDepartmentStoreAdapter :
        PagingDataAdapter<DepartmentStore, AdminDepartmentStoreAdapter.DepartmentStoreViewHolder>(DepartmentStoreComparator) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentStoreViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_department, parent, false)
                return DepartmentStoreViewHolder(view)
        }

        override fun onBindViewHolder(holder: DepartmentStoreViewHolder, position: Int) {
                val departmentStore = getItem(position)
                if (departmentStore != null) {
                        holder.bind(departmentStore)
                }
        }

        class DepartmentStoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                private val deptNameTextView: TextView = itemView.findViewById(R.id.dept_name)
                private val deptBranchTextView: TextView = itemView.findViewById(R.id.dept_branch)
                private val checkIconImageView: ImageView = itemView.findViewById(R.id.check_icon)

                fun bind(departmentStore: DepartmentStore) {
                        deptNameTextView.text = departmentStore.departmentStoreName
                        deptBranchTextView.text = departmentStore.departmentStoreBranch
                }
        }

        object DepartmentStoreComparator : DiffUtil.ItemCallback<DepartmentStore>() {
                override fun areItemsTheSame(oldItem: DepartmentStore, newItem: DepartmentStore): Boolean {
                        return oldItem.departmentStoreId == newItem.departmentStoreId
                }

                override fun areContentsTheSame(oldItem: DepartmentStore, newItem: DepartmentStore): Boolean {
                        return oldItem == newItem
                }
        }
}
