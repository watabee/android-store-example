package com.github.watabee.storeexample.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.github.watabee.storeexample.api.Article
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class DevDataSource(
    private val tag: String,
    private val store: Store<DevConfig, List<Article>>
) : PageKeyedDataSource<Int, Article>() {

    private val job = Job()
    private val scope = CoroutineScope(job)
    private var retry: (() -> Unit)? = null

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Article>) {
        job.cancelChildren()
        store.stream(StoreRequest.skipMemory(DevConfig(page = 1, perPage = params.requestedLoadSize, tag = tag), refresh = false))
            .onEach { storeResponse ->
                when (storeResponse) {
                    is StoreResponse.Loading -> {
                        _networkState.postValue(NetworkState.Loading(true))
                    }
                    is StoreResponse.Data -> {
                        retry = null
                        _networkState.postValue(NetworkState.Loaded(true))
                        callback.onResult(storeResponse.value, null, 2)
                    }
                    is StoreResponse.Error -> {
                        retry = {
                            loadInitial(params, callback)
                        }
                        _networkState.postValue(NetworkState.Error(true, storeResponse.errorMessageOrNull().orEmpty()))
                    }
                }
            }
            .launchIn(scope)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Article>) {
        // do nothing.
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Article>) {
        job.cancelChildren()
        val page = params.key
        store.stream(StoreRequest.skipMemory(DevConfig(page = page, perPage = params.requestedLoadSize, tag = tag), refresh = false))
            .onEach { storeResponse ->
                when (storeResponse) {
                    is StoreResponse.Loading -> {
                        _networkState.postValue(NetworkState.Loading(false))
                    }
                    is StoreResponse.Data -> {
                        retry = null
                        _networkState.postValue(NetworkState.Loaded(false))
                        callback.onResult(storeResponse.value, page + 1)
                    }
                    is StoreResponse.Error -> {
                        retry = {
                            loadAfter(params, callback)
                        }
                        _networkState.postValue(NetworkState.Error(false, storeResponse.errorMessageOrNull().orEmpty()))
                    }
                }
            }
            .launchIn(scope)
    }

    override fun invalidate() {
        super.invalidate()
        cancel()
    }

    fun retry() {
        val prevRetry = retry
        retry = null
        prevRetry?.invoke()
    }

    fun cancel() {
        scope.cancel()
    }
}
