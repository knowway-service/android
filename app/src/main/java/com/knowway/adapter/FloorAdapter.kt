package com.knowway.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.knowway.data.model.department.Floor
import com.knowway.databinding.FragmentFloorItemBinding

class FloorAdapter(
    private val onClick: (Floor) -> Unit
) : ListAdapter<Floor, FloorAdapter.FloorViewHolder>(FloorDiffCallback()) {
    inner class FloorViewHolder(val binding: FragmentFloorItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(floor: Floor, onClick: (Floor) -> Unit) {
            binding.floorText.text = floor.departmentStoreFloor
            binding.root.setOnClickListener { onClick(floor) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val binding =
            FragmentFloorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FloorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }
}

class FloorDiffCallback: DiffUtil.ItemCallback<Floor>() {
    override fun areItemsTheSame(oldItem: Floor, newItem: Floor): Boolean = oldItem.departmentStoreFloorId == newItem.departmentStoreFloorId
    override fun areContentsTheSame(oldItem: Floor, newItem: Floor): Boolean = oldItem == newItem
}
