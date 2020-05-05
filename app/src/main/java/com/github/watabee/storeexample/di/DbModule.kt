package com.github.watabee.storeexample.di

import android.content.Context
import com.github.watabee.storeexample.db.AppDatabase
import com.github.watabee.storeexample.db.ArticleDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class DbModule {

    companion object {
        @Singleton
        @Provides
        fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.getInstance(context)

        @Singleton
        @Provides
        fun provideArticleDao(database: AppDatabase): ArticleDao = database.articleDao()
    }
}