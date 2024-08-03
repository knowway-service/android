package com.knowway.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.knowway.R
import com.knowway.data.model.department.DepartmentStoreResponse

class DepartmentStoreAdapter(
    private var departmentStoreRepons: List<DepartmentStoreResponse>,
    private val onItemClick: (DepartmentStoreResponse) -> Unit
    ): RecyclerView.Adapter<DepartmentStoreAdapter.DepartmentViewHolder>() {

    inner class DepartmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.dept_search_title)
        private val branch = itemView.findViewById<TextView>(R.id.dept_search_branch)

        fun bind(departmentStoreResponse: DepartmentStoreResponse) {
            title.text = departmentStoreResponse.departmentStoreName
            branch.text = departmentStoreResponse.departmentStoreBranch
            itemView.setOnClickListener {
                onItemClick(departmentStoreResponse)
            }
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
        val departmentStore = departmentStoreRepons[position]
        holder.bind(departmentStore)
    }

    override fun getItemCount(): Int = departmentStoreRepons.size

    fun update(newDepartmentStoreResponse: List<DepartmentStoreResponse>) {
        departmentStoreRepons = newDepartmentStoreResponse
        notifyDataSetChanged()
    }
}