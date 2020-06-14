package com.github.watabee.storeexample.di

import com.github.watabee.storeexample.repository.DevRepository
import com.github.watabee.storeexample.repository.DevRepositoryFactory
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {
    @Binds
    @ActivityRetainedScoped
    abstract fun bindDevRepositoryFactory(instance: DevRepositoryFactory): DevRepository.Factory
}