package com.knowway.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.knowway.ui.fragment.AdminRecordingListFragment
import com.knowway.ui.fragment.MypageRecordingListFragment

class ViewPagerAdapter(
    activity: AppCompatActivity,
    private val tabType: TabType,
    private val departmentStoreFloorId: Long? = null,
    private val areaNumber: Int? = null,
    private val refreshData: (() -> Unit)? = null
) : FragmentStateAdapter(activity) {

    enum class TabType {
        ADMIN,
        MY_PAGE
    }

    private val fragments = mutableListOf<Fragment>()

    init {
        when (tabType) {
            TabType.ADMIN -> {
                if (departmentStoreFloorId == null || areaNumber == null) {
                    throw IllegalStateException("departmentStoreFloorId and areaNumber must be provided for ADMIN tabType")
                }
                fragments.add(AdminRecordingListFragment(departmentStoreFloorId, areaNumber, isInSelectionTab = false, refreshData = refreshData))
                fragments.add(AdminRecordingListFragment(departmentStoreFloorId, areaNumber, isInSelectionTab = true, refreshData = refreshData))
            }
            TabType.MY_PAGE -> {
                fragments.add(MypageRecordingListFragment(isInSelectionTab = false))
                fragments.add(MypageRecordingListFragment(isInSelectionTab = true))
            }
        }
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun setFragments(fragments: List<Fragment>) {
        this.fragments.clear()
        this.fragments.addAll(fragments)
        notifyDataSetChanged()
    }
}
