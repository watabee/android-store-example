package com.github.watabee.storeexample

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.get
import com.dropbox.android.external.store4.nonFlowFetcher
import com.dropbox.android.external.store4.nonFlowValueFetcher
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Date
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

class MainViewModel : ViewModel() {

    private val okHttpClient = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dev.to/api/")
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter()).build()
            )
        )
        .client(okHttpClient)
        .build()

    private val devApi: DevApi = retrofit.create(DevApi::class.java)

    @OptIn(ExperimentalTime::class)
    private val store = StoreBuilder.from(
        nonFlowValueFetcher<Int, List<Article>> { page -> devApi.findArticles(page) }
    ).cachePolicy(
        MemoryPolicy.builder()
            .setMemorySize(200)
            .setExpireAfterAccess(10.minutes)
            .build()
    ).build()

    fun findArticles() {
        viewModelScope.launch {
            try {
                val articles = store.get(0)
                Log.e("MainViewModel", "articles = $articles")
            } catch (e: Throwable) {
                Log.e("MainViewModel", "error: $e")
            }
        }
    }
}