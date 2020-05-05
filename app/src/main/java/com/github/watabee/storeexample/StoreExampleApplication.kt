package com.github.watabee.storeexample

import android.app.Application
import com.github.watabee.storeexample.di.AppComponent
import timber.log.Timber

class StoreExampleApplication : Application() {

    private val appComponent: AppComponent by lazy(LazyThreadSafetyMode.NONE) { AppComponent.create(this) }
    val activityComponentFactory by lazy(LazyThreadSafetyMode.NONE) { appComponent.activityComponentFactory() }

    override fun onCreate() {
        super.onCreate()

        appComponent.inject(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}