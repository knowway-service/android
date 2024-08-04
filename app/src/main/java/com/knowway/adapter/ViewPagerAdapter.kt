package com.knowway.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.knowway.ui.fragment.AdminRecordingListFragment

class ViewPagerAdapter(
    activity: AppCompatActivity,
    private val tabType: TabType
) : FragmentStateAdapter(activity) {

    enum class TabType {
        ADMIN,
        MY_PAGE
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (tabType) {
            TabType.ADMIN -> {
                when (position) {
                    0 -> AdminRecordingListFragment(isInSelectionTab = false)
                    1 -> AdminRecordingListFragment(isInSelectionTab = true)
                    else -> throw IllegalStateException("Unexpected position $position")
                }
            }
            TabType.MY_PAGE -> {
                when (position) {
                    0 -> MypageRecordingListFragment(isInSelectionTab = false)
                    1 -> MypageRecordingListFragment(isInSelectionTab = true)
                    else -> throw IllegalStateException("Unexpected position $position")
                }
            }
        }
    }
}
