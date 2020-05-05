package com.github.watabee.storeexample.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.watabee.storeexample.R

class ErrorAdapter(private val retryAction: () -> Unit) : RecyclerView.Adapter<ErrorViewHolder>() {
    var isError = false
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ErrorViewHolder {
        return ErrorViewHolder(parent, retryAction)
    }

    override fun onBindViewHolder(holder: ErrorViewHolder, position: Int) {
        // do nothing
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.list_item_error
    }

    override fun getItemCount(): Int = if (isError) 1 else 0
}

class ErrorViewHolder(parent: ViewGroup, retryAction: () -> Unit) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.list_item_error, parent, false)
) {
    init {
        itemView.findViewById<View>(R.id.retry_text).setOnClickListener { retryAction() }
    }
}
