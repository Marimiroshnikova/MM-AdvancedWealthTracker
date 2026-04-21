package com.example.advancedwealthtracker.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.advancedwealthtracker.fragments.AnalyticsFragment
import com.example.advancedwealthtracker.fragments.InputFragment
import com.example.advancedwealthtracker.fragments.ProfileFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InputFragment()
            1 -> AnalyticsFragment()
            2 -> ProfileFragment()
            else -> InputFragment()
        }
    }
}