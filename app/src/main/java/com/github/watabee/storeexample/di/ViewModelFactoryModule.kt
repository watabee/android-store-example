package com.github.watabee.storeexample.di

import com.github.watabee.storeexample.ui.ArticleViewModel
import com.github.watabee.storeexample.ui.ArticleViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindArticleViewModelFactory(instance: ArticleViewModelFactory): ArticleViewModel.Factory
}