package com.github.watabee.storeexample.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.watabee.storeexample.R
import com.github.watabee.storeexample.api.Article

class ArticleAdapter : PagedListAdapter<Article, ArticleViewHolder>(createItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(parent)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position) ?: return
        holder.bind(article)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.list_item_article
    }

    companion object {
        fun createItemCallback() = object: DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem == newItem
        }
    }
}

class ArticleViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.list_item_article, parent, false)
) {
    private val titleTextView: TextView = itemView.findViewById(R.id.title_text)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.description_text)

    fun bind(article: Article) {
        titleTextView.text = article.title
        descriptionTextView.text = article.description
    }
}
