package com.github.watabee.storeexample.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dropbox.android.external.store4.Store
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.paging.DevConfig
import com.github.watabee.storeexample.paging.DevDataSource
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow

class DevRepository @AssistedInject constructor(
    @Assisted private val tag: String,
    private val store: Store<DevConfig, List<Article>>
) {
    @AssistedInject.Factory
    interface Factory {
        fun create(tag: String): DevRepository
    }

    val pagingData: Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            prefetchDistance = PREFETCH_DISTANCE,
            initialLoadSize = PAGE_SIZE,
            enablePlaceholders = false
        ),
        initialKey = 1
    ) { DevDataSource(tag, store) }
        .flow

    companion object {
        private const val PAGE_SIZE = 30
        private const val PREFETCH_DISTANCE = 20
    }
}