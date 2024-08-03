package com.knowway.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.knowway.R
import com.knowway.data.model.department.DepartmentStore

class DepartmentStoreAdapter(private var departmentStores: List<DepartmentStore>):
    RecyclerView.Adapter<DepartmentStoreAdapter.DepartmentViewHolder>() {

    inner class DepartmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.dept_search_title)
        private val branch = itemView.findViewById<TextView>(R.id.dept_search_branch)

        fun bind(departmentStore: DepartmentStore) {
            title.text = departmentStore.departmentStoreName
            branch.text = departmentStore.departmentStoreBranch
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DepartmentStoreAdapter.DepartmentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_department, parent, false)
        return DepartmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val departmentStore = departmentStores[position]
        holder.bind(departmentStore)
    }

    override fun getItemCount(): Int = departmentStores.size

    fun update(newDepartmentStore: List<DepartmentStore>) {
        departmentStores = newDepartmentStore
        notifyDataSetChanged()
    }
}