package com.github.watabee.storeexample.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.watabee.storeexample.R

class LoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<LoadStateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(parent, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}

class LoadStateViewHolder(
    parent: ViewGroup,
    retry: () -> Unit
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_load_state, parent, false)) {

    private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    private val retryTextView = itemView.findViewById<TextView>(R.id.retry_text)
        .also { it.setOnClickListener { retry() } }

    fun bind(loadState: LoadState) {
        progressBar.isVisible = loadState is LoadState.Loading
        retryTextView.isVisible = loadState is LoadState.Error
    }
}
