package com.github.watabee.storeexample

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.dropbox.android.external.store4.Store
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevConfig
import com.github.watabee.storeexample.paging.DevDataSourceFactory
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val store: Store<DevConfig, List<Article>>
) : ViewModel() {

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
}