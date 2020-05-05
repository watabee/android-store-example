package com.github.watabee.storeexample.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.watabee.storeexample.R

class LoadingAdapter : RecyclerView.Adapter<LoadingViewHolder>() {
    var isLoading = false
        set(value) {
            if (field != value) {
                field = value
                if (value) {
                    notifyItemInserted(0)
                } else {
                    notifyItemRemoved(0)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadingViewHolder {
        return LoadingViewHolder(parent)
    }

    override fun onBindViewHolder(holder: LoadingViewHolder, position: Int) {
        // do nothing
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.list_item_loading
    }

    override fun getItemCount(): Int = if (isLoading) 1 else 0
}

class LoadingViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.list_item_loading, parent, false)
)