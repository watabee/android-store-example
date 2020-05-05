package com.github.watabee.storeexample.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.watabee.storeexample.R
import com.github.watabee.storeexample.StoreExampleApplication
import com.github.watabee.storeexample.paging.NetworkState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as StoreExampleApplication).activityComponentFactory.create(this).inject(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val articleAdapter = ArticleAdapter()
        val loadingAdapter = LoadingAdapter()
        val errorAdapter = ErrorAdapter { viewModel.retry() }
        val mergeAdapter = MergeAdapter(articleAdapter, loadingAdapter, errorAdapter)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.adapter = mergeAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        val swipeRefreshLayout: SwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }

        viewModel.articles.observe(this, articleAdapter::submitList)

        viewModel.networkState.observe(this) { networkState ->
            when (networkState) {
                is NetworkState.Loading -> {
                    if (networkState.isInitial) {
                        swipeRefreshLayout.isRefreshing = true
                    } else {
                        loadingAdapter.isLoading = true
                        errorAdapter.isError = false
                    }
                }
                is NetworkState.Loaded -> {
                    if (networkState.isInitial) {
                        swipeRefreshLayout.isRefreshing = false
                    } else {
                        loadingAdapter.isLoading = false
                        errorAdapter.isError = false
                    }
                }
                is NetworkState.Error -> {
                    if (networkState.isInitial) {
                        swipeRefreshLayout.isRefreshing = false

                        MaterialAlertDialogBuilder(this)
                            .setTitle("Error")
                            .setMessage("Retry?")
                            .setPositiveButton(android.R.string.yes) { _, _ -> viewModel.retry() }
                            .setNegativeButton(android.R.string.no, null)
                            .show()
                    } else {
                        loadingAdapter.isLoading = false
                        errorAdapter.isError = true
                    }
                }
            }
        }
    }
}