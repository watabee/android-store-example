package com.github.watabee.storeexample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.watabee.storeexample.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = Adapter(this)

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = Tags.values()[position].value
        }.attach()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {
                val fragment = supportFragmentManager.findFragmentByTag("f${viewPager.currentItem}") as? ArticleFragment ?: return
                fragment.scrollToTop()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
            }
        })
    }

    private class Adapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = Tags.values().size
        override fun createFragment(position: Int): Fragment = ArticleFragment.newInstance(Tags.values()[position].value)
    }

    private enum class Tags(val value: String) {
        ANDROID("Android"),
        IOS("iOS"),
        KOTLIN("Kotlin"),
        JAVA("Java"),
        SWIFT("Swift"),
        JAVASCRIPT("JavaScript"),
        TYPESCRIPT("TypeScript"),
        RUBY("Ruby"),
        PYTHON("Python"),
        GO("Go"),
    }
}