package com.github.watabee.storeexample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.watabee.storeexample.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : BaseActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        activityComponent.inject(this)
        super.onCreate(savedInstanceState)

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = Adapter(this)

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = Tags.values()[position].value
        }.attach()
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