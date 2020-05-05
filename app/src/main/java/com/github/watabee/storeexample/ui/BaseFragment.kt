package com.github.watabee.storeexample.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.github.watabee.storeexample.di.FragmentComponent

abstract class BaseFragment : Fragment {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    val fragmentComponent: FragmentComponent by lazy(LazyThreadSafetyMode.NONE) {
        (requireActivity() as BaseActivity).activityComponent.fragmentComponentFactory().create(this)
    }
}