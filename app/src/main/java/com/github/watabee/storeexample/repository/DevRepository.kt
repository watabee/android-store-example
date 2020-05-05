package com.github.watabee.storeexample.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.dropbox.android.external.store4.Store
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevConfig
import com.github.watabee.storeexample.paging.DevDataSourceFactory
import com.github.watabee.storeexample.paging.NetworkState
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class DevRepository @AssistedInject constructor(
    @Assisted private val tag: String,
    private val store: Store<DevConfig, List<Article>>,
    factory: DevDataSourceFactory.Factory
) {
    @AssistedInject.Factory
    interface Factory {
        fun create(tag: String): DevRepository
    }

    private val dataSourceFactory: DevDataSourceFactory = factory.create(tag)
    val networkState: LiveData<NetworkState> = dataSourceFactory.dataSource.switchMap { it.networkState }
    val articles: LiveData<PagedList<Article>> =
        dataSourceFactory.toLiveData(
            config = PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .build()
        )

    fun retry() {
        dataSourceFactory.dataSource.value?.retry()
    }

    suspend fun refresh() {
        store.clear(DevConfig(1, PAGE_SIZE, tag))
        dataSourceFactory.dataSource.value?.invalidate()
    }

    fun cancel() {
        dataSourceFactory.dataSource.value?.cancel()
    }

    companion object {
        private const val PAGE_SIZE = 30
        private const val PREFETCH_DISTANCE = 2
    }
}