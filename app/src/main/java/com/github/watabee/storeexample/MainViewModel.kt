package com.github.watabee.storeexample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.nonFlowValueFetcher
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevApi
import com.github.watabee.storeexample.api.DevConfig
import com.github.watabee.storeexample.db.AppDatabase
import com.github.watabee.storeexample.db.ArticleWithTags
import com.hadilq.liveevent.LiveEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.Date
import kotlin.time.ExperimentalTime

class MainViewModel(application: Application) : AndroidViewModel(application) {

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

    private val articleDao = AppDatabase.getInstance(application).articleDao()

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val store = StoreBuilder.from<DevConfig, List<Article>, List<Article>>(
        nonFlowValueFetcher { config ->
            Timber.e("nonFlowValueFetcher, config = $config")
            val articles = devApi.findArticles(config.page, config.perPage, config.tag)
            articles.forEach {
                Timber.e("    $it")
            }
            articles
        },
        sourceOfTruth = SourceOfTruth.fromNonFlow(
            reader = { config ->
                Timber.e("reader, config = $config")
                val offset = (config.page - 1) * config.perPage
                val articles = articleDao.findArticles(offset, config.perPage, config.tag)
                if (articles.isEmpty()) null else articles.map { it.toArticle() }
            },
            writer = { _, articles ->
                Timber.e("writer")
                articleDao.insert(articles)
            },
            delete = { config ->
                Timber.e("delete")
                articleDao.deleteByTag(config.tag)
            },
            deleteAll = {
                Timber.e("deleteAll")
                articleDao.deleteAll()
            }
        )
    ).disableCache().build()

    private val dataSourceFactory = DevDataSourceFactory("android", store)
    val articles: LiveData<PagedList<Article>> = dataSourceFactory
        .toLiveData(config = PagedList.Config.Builder().setPageSize(30).setInitialLoadSizeHint(30).build())

    private val refreshEvent = LiveEvent<Unit>()

    init {
        refreshEvent.asFlow()
            .onEach {
                store.clear(DevConfig(1, 30, "android"))
                dataSourceFactory.invalidate()
            }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        refreshEvent.value = Unit
    }

    override fun onCleared() {
        super.onCleared()
        dataSourceFactory.cancel()
    }

    private fun ArticleWithTags.toArticle() =
        Article(
            id = article.id, title = article.title, description = article.description,
            publishedAt = article.publishedAt, tagList = tags
        )

    private class DevDataSource(
        private val tag: String,
        private val store: Store<DevConfig, List<Article>>
    ) : PageKeyedDataSource<Int, Article>() {

        private val scope = CoroutineScope(Job())

        override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Article>) {
            Timber.e("loadInitial: ${params.requestedLoadSize}")
            store.stream(StoreRequest.skipMemory(DevConfig(page = 1, perPage = params.requestedLoadSize, tag = tag), refresh = false))
                .onEach { storeResponse ->
                    when (storeResponse) {
                        is StoreResponse.Loading -> {
                            Timber.e("loadInitial#collect: Loading")
                        }
                        is StoreResponse.Data -> {
                            Timber.e("loadInitial#collect: Data")
                            Timber.e("${storeResponse.value}")
                            callback.onResult(storeResponse.value, null, 2)
                        }
                        is StoreResponse.Error -> {
                            Timber.e("loadInitial#collect: Error, ${storeResponse.errorMessageOrNull()}")
                        }
                    }
                }
                .launchIn(scope)
        }

        override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Article>) {
            // do nothing.
        }

        override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Article>) {
            Timber.e("loadAfter: ${params.key}, ${params.requestedLoadSize}, $callback")
            val page = params.key
            store.stream(StoreRequest.cached(DevConfig(page = page, perPage = params.requestedLoadSize, tag = tag), refresh = false))
                .onEach { storeResponse ->
                    when (storeResponse) {
                        is StoreResponse.Loading -> {
                            Timber.e("loadAfter#collect: Loading")
                        }
                        is StoreResponse.Data -> {
                            Timber.e("${storeResponse.value}")
                            callback.onResult(storeResponse.value, page + 1)
                        }
                        is StoreResponse.Error -> {
                            Timber.e("loadAfter#collect: Error, ${storeResponse.errorMessageOrNull()}")
                        }
                    }
                }
                .launchIn(scope)
        }

        override fun invalidate() {
            super.invalidate()
            cancel()
        }

        fun cancel() {
            scope.cancel()
        }
    }

    private class DevDataSourceFactory(
        private val tag: String,
        private val store: Store<DevConfig, List<Article>>
    ) : DataSource.Factory<Int, Article>() {

        private var dataSource: DevDataSource? = null

        override fun create(): DataSource<Int, Article> {
            val devDataSource = DevDataSource(tag, store)
            dataSource = devDataSource
            return devDataSource
        }

        fun invalidate() {
            dataSource?.invalidate()
        }

        fun cancel() {
            dataSource?.cancel()
        }
    }
}