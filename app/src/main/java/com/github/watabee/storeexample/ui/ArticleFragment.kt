package com.github.watabee.storeexample.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.watabee.storeexample.R
import com.github.watabee.storeexample.paging.NetworkState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class ArticleFragment : BaseFragment(R.layout.fragment_articles) {

    @Inject lateinit var viewModelFactory: ArticleViewModel.Factory

    private val articleTag: String by lazy(LazyThreadSafetyMode.NONE) { requireArguments().getString(ARTICLE_TAG)!! }
    private val viewModel: ArticleViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return viewModelFactory.create(articleTag) as T
            }
        }
    }

    override fun onAttach(context: Context) {
        fragmentComponent.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleAdapter = ArticleAdapter()
        val loadingAdapter = LoadingAdapter()
        val errorAdapter = ErrorAdapter { viewModel.retry() }
        val mergeAdapter = MergeAdapter(articleAdapter, loadingAdapter, errorAdapter)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = mergeAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }

        viewModel.articles.observe(viewLifecycleOwner, articleAdapter::submitList)

        viewModel.networkState.observe(viewLifecycleOwner) { networkState ->
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

                        MaterialAlertDialogBuilder(requireContext())
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

    companion object {
        private const val ARTICLE_TAG = "ARTICLE_TAG"

        fun newInstance(articleTag: String): ArticleFragment {
            val fragment = ArticleFragment()
            fragment.arguments = bundleOf(ARTICLE_TAG to articleTag)
            return fragment
        }
    }
}