package com.github.watabee.storeexample.di

import androidx.fragment.app.Fragment
import com.github.watabee.storeexample.ui.ArticleFragment
import dagger.BindsInstance
import dagger.Subcomponent
import javax.inject.Scope

@Retention(AnnotationRetention.RUNTIME)
@Scope
@MustBeDocumented
annotation class FragmentScope

@FragmentScope
@Subcomponent(modules = [])
interface FragmentComponent {

    fun inject(fragment: ArticleFragment)

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance fragment: Fragment): FragmentComponent
    }
}
