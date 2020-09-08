package com.senacor.postbook.ui.posts

import android.view.ViewGroup
import com.senacor.postbook.R
import com.senacor.postbook.ui.abs.BaseListAdapter
import com.senacor.postbook.ui.abs.BaseViewHolder
import com.senacor.postbook.ui.abs.Diffable
import com.senacor.postbook.ui.abs.viewHolderCreator
import kotlinx.android.synthetic.main.item_post.view.*

data class Post(
    val id: Int,
    val title: String,
    val body: String,
    val favorite: Boolean
): Diffable {
    override fun getId(): Any = id
}

class PostViewHolder(
    parent: ViewGroup,
    private val onItemClick: ((Post) -> Unit)?,
    private val onFavoriteClick: ((Post) -> Unit)?
): BaseViewHolder<Post>(parent, R.layout.item_post) {
    override fun bind(item: Post) {
        itemView.title.setText(item.title)
        itemView.body.setText(item.body)
        itemView.favoriteButton.setImageResource(if (item.favorite) R.drawable.ic_baseline_star_24 else R.drawable.ic_baseline_star_border_24)
        if (onItemClick != null)
            itemView.setOnClickListener { onItemClick.invoke(item) }
        if (onFavoriteClick != null)
            itemView.favoriteButton.setOnClickListener { onFavoriteClick.invoke(item.copy(favorite = !item.favorite)) }
    }
}

class PostsAdapter(
    onItemClick: (Post) -> Unit,
    onFavoriteClick: (Post) -> Unit,
): BaseListAdapter<Post>(
    viewHolderCreator {
        PostViewHolder(
            it,
            onItemClick,
            onFavoriteClick
        )
    }
)
