package com.github.watabee.storeexample.di

import android.content.Context
import com.github.watabee.storeexample.db.AppDatabase
import com.github.watabee.storeexample.db.ArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DbModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()
}