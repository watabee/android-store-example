package com.github.watabee.storeexample.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.watabee.storeexample.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ArticleFragment : Fragment(R.layout.fragment_articles) {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleAdapter = ArticleAdapter()

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.adapter = articleAdapter.withLoadStateFooter(LoadStateAdapter(articleAdapter::retry))

        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        val swipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener { articleAdapter.refresh() }

        lifecycleScope.launch {
            viewModel.articles.collectLatest { articleAdapter.submitData(it) }
        }

        lifecycleScope.launch {
            articleAdapter.loadStateFlow.collectLatest { loadStates ->
                swipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading

                if (loadStates.refresh is LoadState.Error) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Error")
                        .setMessage("Retry?")
                        .setPositiveButton(android.R.string.yes) { _, _ -> articleAdapter.retry() }
                        .setNegativeButton(android.R.string.no, null)
                        .show()

                }
            }
        }
    }

    fun scrollToTop() {
        val recyclerView: RecyclerView = view?.findViewById(R.id.recycler_view) ?: return
        val itemCount = recyclerView.adapter?.itemCount ?: 0
        if (itemCount > 0) {
            recyclerView.layoutManager?.scrollToPosition(0)
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
