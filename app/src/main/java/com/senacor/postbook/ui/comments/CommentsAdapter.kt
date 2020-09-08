package com.senacor.postbook.ui.comments

import android.view.ViewGroup
import com.senacor.postbook.R
import com.senacor.postbook.ui.abs.BaseListAdapter
import com.senacor.postbook.ui.abs.BaseViewHolder
import com.senacor.postbook.ui.abs.Diffable
import com.senacor.postbook.ui.abs.viewHolderCreator
import com.senacor.postbook.ui.posts.PostViewHolder
import kotlinx.android.synthetic.main.item_comment.view.*

data class Comment(
    val id: Int,
    val name: String,
    val body: String
): Diffable {
    override fun getId() = id
}

class CommentViewHolder(
    parent: ViewGroup
): BaseViewHolder<Comment>(parent, R.layout.item_comment) {
    override fun bind(item: Comment) {
        itemView.title.setText(item.name)
        itemView.body.setText(item.body)
    }
}

class CommentsAdapter: BaseListAdapter<Diffable>(
    viewHolderCreator { PostViewHolder(it, null, null) },
    viewHolderCreator { CommentViewHolder(it) }
)