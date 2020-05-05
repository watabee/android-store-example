package com.github.watabee.storeexample.di

import android.content.Context
import com.github.watabee.storeexample.api.DevApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.io.File
import java.util.Date
import javax.inject.Singleton

@Module
abstract class NetworkModule {

    companion object {
        @Singleton
        @Provides
        fun provideOkHttpClient(context: Context): OkHttpClient =
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                        override fun log(message: String) {
                            Timber.tag("OkHttp").w(message)
                        }
                    }).apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                )
                .cache(Cache(File(context.cacheDir, "okhttp-cache"), 30 * 1024 * 1024))
                .build()

        @Singleton
        @Provides
        fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
            Retrofit.Builder()
                .baseUrl("https://dev.to/api/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()

        @Singleton
        @Provides
        fun provideDevApi(retrofit: Retrofit): DevApi = retrofit.create(DevApi::class.java)

        @Singleton
        @Provides
        fun provideMoshi(): Moshi = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).build()
    }
}