package com.github.watabee.storeexample

import android.app.Application
import timber.log.Timber

class StoreExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}