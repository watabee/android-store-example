package com.github.watabee.storeexample.di

import android.app.Application
import android.content.Context
import com.github.watabee.storeexample.StoreExampleApplication
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(application: StoreExampleApplication)

    fun activityComponentFactory(): ActivityComponent.Factory

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application, @BindsInstance context: Context): AppComponent
    }

    companion object {
        fun create(application: Application): AppComponent =
            DaggerAppComponent.factory().create(application, application)
    }
}