package com.knowway.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.knowway.R
import com.knowway.data.model.department.DepartmentStoreResponse

class DepartmentStoreAdapter(
    private var departmentStoreRepons: List<DepartmentStoreResponse>,
    private val onItemClick: (DepartmentStoreResponse) -> Unit
    ): RecyclerView.Adapter<DepartmentStoreAdapter.DepartmentViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class DepartmentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.dept_search_title)
        private val branch = itemView.findViewById<TextView>(R.id.dept_search_branch)
        private val imageView = itemView.findViewById<ImageView>(R.id.dept_image)
        private val container = itemView.findViewById<ConstraintLayout>(R.id.item_container)

        fun bind(departmentStoreResponse: DepartmentStoreResponse, position: Int) {
            title.text = departmentStoreResponse.departmentStoreName
            branch.text = departmentStoreResponse.departmentStoreBranch
            val imageResId = when (position % 4) {
                0 -> R.drawable.dept_1
                1 -> R.drawable.dept_2
                2 -> R.drawable.dept_3
                3 -> R.drawable.dept_4
                else -> R.drawable.dept_1
            }
            imageView.setImageResource(imageResId)
            if (selectedPosition == position) { // 선택된 아이템 배경 변경
                container.setBackgroundResource(R.drawable.dept_selected_item_background)
            } else {
                container.setBackgroundResource(R.drawable.dept_item_background)
            }

            itemView.setOnClickListener {
                notifyItemChanged(selectedPosition) // 이전 선택된 아이템 갱신
                selectedPosition = position
                notifyItemChanged(selectedPosition) // 현재 선택된 아이템 갱신
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
        holder.bind(departmentStore, position)
    }

    override fun getItemCount(): Int = departmentStoreRepons.size

    fun update(newDepartmentStoreResponse: List<DepartmentStoreResponse>) {
        departmentStoreRepons = newDepartmentStoreResponse
        notifyDataSetChanged()
    }
}