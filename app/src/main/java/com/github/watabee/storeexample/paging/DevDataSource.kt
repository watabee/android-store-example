package com.github.watabee.storeexample.paging

import androidx.paging.PageKeyedDataSource
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class DevDataSource(
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
