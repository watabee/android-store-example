package com.github.watabee.storeexample

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dropbox.android.external.store4.MemoryPolicy
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.get
import com.dropbox.android.external.store4.nonFlowValueFetcher
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevApi
import com.github.watabee.storeexample.api.DevConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val store = StoreBuilder.from(
        nonFlowValueFetcher<DevConfig, List<Article>> { config -> devApi.findArticles(config.page, config.tag) }
    ).cachePolicy(
        MemoryPolicy.builder()
            .setMemorySize(200)
            .setExpireAfterAccess(10.minutes)
            .build()
    ).build()

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    fun findArticles() {
        viewModelScope.launch {
            try {
                _articles.value = store.get(DevConfig(0, "android"))
            } catch (e: Throwable) {
                Log.e("MainViewModel", "error: $e")
            }
        }
    }
}