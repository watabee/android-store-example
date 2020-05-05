package com.github.watabee.storeexample.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(
    subcomponents = [ActivityComponent::class],
    includes = [NetworkModule::class, DbModule::class, StoreModule::class, ViewModelModule::class, AssistedInject_AppModule::class]
)
abstract class AppModule
