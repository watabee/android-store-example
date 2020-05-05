package com.github.watabee.storeexample.ui

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.github.watabee.storeexample.StoreExampleApplication
import com.github.watabee.storeexample.di.ActivityComponent

abstract class BaseActivity : AppCompatActivity {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    val activityComponent: ActivityComponent by lazy(LazyThreadSafetyMode.NONE) {
        (application as StoreExampleApplication).activityComponentFactory.create(this)
    }
}