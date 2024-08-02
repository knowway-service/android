package com.knowway.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.knowway.ui.fragment.RegisteredTipsFragment
import com.knowway.ui.fragment.SelectedTipsFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RegisteredTipsFragment()
            1 -> SelectedTipsFragment()
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}