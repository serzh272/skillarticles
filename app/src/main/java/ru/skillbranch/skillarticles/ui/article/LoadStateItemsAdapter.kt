package ru.skillbranch.skillarticles.ui.article

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.R

class LoadStateItemsAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) {
        if (loadState is LoadState.Error) holder.itemView.setOnClickListener { retry() }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): RecyclerView.ViewHolder = when (loadState) {
        is LoadState.NotLoading -> error("$loadState not available this")
        LoadState.Loading -> LoadVh(
            LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
        )
        is LoadState.Error -> ErrorVh(
            LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
        )
    }
}

class LoadVh(convertView: View) : RecyclerView.ViewHolder(convertView)
class ErrorVh(convertView: View) : RecyclerView.ViewHolder(convertView)