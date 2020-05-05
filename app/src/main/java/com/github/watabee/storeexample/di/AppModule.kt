package com.github.watabee.storeexample.di

import dagger.Module

@Module(
    subcomponents = [ActivityComponent::class],
    includes = [NetworkModule::class, DbModule::class, StoreModule::class, ViewModelModule::class]
)
abstract class AppModule
