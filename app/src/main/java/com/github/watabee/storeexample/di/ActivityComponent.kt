package com.github.watabee.storeexample.di

import androidx.appcompat.app.AppCompatActivity
import com.github.watabee.storeexample.ui.MainActivity
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@Retention(AnnotationRetention.RUNTIME)
@Scope
@MustBeDocumented
annotation class ActivityScope

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)

    fun fragmentComponentFactory(): FragmentComponent.Factory

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: AppCompatActivity): ActivityComponent
    }
}
