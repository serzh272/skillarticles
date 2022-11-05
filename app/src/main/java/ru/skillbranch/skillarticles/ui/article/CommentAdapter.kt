package ru.skillbranch.skillarticles.ui.article

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.data.network.res.CommentRes
import ru.skillbranch.skillarticles.ui.custom.CommentItemView

class CommentAdapter(
    val onClick: (CommentRes) -> Unit
) : PagingDataAdapter<CommentRes, CommentVH>(CommentDiffCallback()) {
    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.bind(getItem(position)!!, onClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        return CommentVH(CommentItemView(parent.context))
    }

}

class CommentDiffCallback : DiffUtil.ItemCallback<CommentRes>() {
    override fun areItemsTheSame(oldItem: CommentRes, newItem: CommentRes): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommentRes, newItem: CommentRes): Boolean {
        return oldItem == newItem
    }

}

class CommentVH(convertView: View) : RecyclerView.ViewHolder(convertView) {
    fun bind(item: CommentRes, onClick: (CommentRes) -> Unit) {
        (itemView as CommentItemView).bind(item)
    }
}
