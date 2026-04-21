package com.example.advancedwealthtracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.advancedwealthtracker.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var mmVaViewPager: ViewPager2
    private lateinit var mmVaTabLayout: TabLayout
    private lateinit var mmVaAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mmVaViewPager = findViewById(R.id.mm_va_viewPager)
        mmVaTabLayout = findViewById(R.id.mm_va_tabLayout)

        val mmVaLastName = getString(R.string.student_last_name)
        val mmVaVowels = setOf('a', 'e', 'i', 'o', 'u')
        mmVaViewPager.orientation = if (mmVaLastName.first().lowercaseChar() in mmVaVowels) {
            ViewPager2.ORIENTATION_HORIZONTAL
        } else {
            ViewPager2.ORIENTATION_VERTICAL
        }

        mmVaAdapter = ViewPagerAdapter(this)
        mmVaViewPager.adapter = mmVaAdapter

        TabLayoutMediator(mmVaTabLayout, mmVaViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_input)
                1 -> getString(R.string.tab_analytics)
                2 -> getString(R.string.tab_profile)
                else -> ""
            }
        }.attach()
    }
}