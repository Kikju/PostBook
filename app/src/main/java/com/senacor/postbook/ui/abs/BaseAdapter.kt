package com.senacor.postbook.ui.abs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Usage
 *
 * class FirstViewHolder(parent: ViewGroup): BaseViewHolder<FirstType>(parent, R.layout.row_first){
 *    override fun bind(item: FirstType) {
 *       itemView.viewId.text = item.text
 *    }
 * }
 *
 * BasePagingDataAdapter(
 *    viewHolderCreator { FirstViewHolder(it) },
 *    viewHolderCreator { SecondViewHolder(it) }
 * )
 */
abstract class BaseViewHolder<T>(open val parent: ViewGroup, layoutId: Int):
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
    ) {
    fun bindInternal(item: T?) {
        item?.let { bind(it) }
    }

    abstract fun bind(item: T)

}

interface Diffable {
    fun getId(): Any
}

class IdDiffCallback<T: Diffable>: DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.javaClass == newItem.javaClass && oldItem.getId() == newItem.getId()
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.equals(newItem)
    }
}

inline fun <reified T> viewHolderCreator(noinline creator: (ViewGroup) -> BaseViewHolder<T>): ViewHolderCreator<T> {
    return ViewHolderCreator(T::class.java, creator)
}

data class ViewHolderCreator<T>(
    val clazz: Class<T>,
    val creator: (ViewGroup) -> BaseViewHolder<T>
)

open class BaseListAdapter<T: Diffable>(
    private vararg val creators: ViewHolderCreator<out T>,
    diffCallback: DiffUtil.ItemCallback<T> = IdDiffCallback()
): ListAdapter<T, BaseViewHolder<T>>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return onCreateViewHolderCreators(creators, parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bindInternal(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return getItemViewTypeCreators(creators, getItem(position))
    }

}

private fun <T: Diffable> getItemViewTypeCreators(
    creators: Array<out ViewHolderCreator<out T>>,
    item: T?
): Int {
    val index = creators.indexOfFirst { it.clazz == item?.javaClass }
    if (index < 0) throw RuntimeException(
        "Cannot find view holder viewType: ${item?.javaClass?.simpleName} in list of creators: ${
            creators.map { it.clazz.simpleName }
                .joinToString(", ")
        }")
    return index
}

@Suppress("UNCHECKED_CAST")
private fun <T: Diffable> onCreateViewHolderCreators(
    creators: Array<out ViewHolderCreator<out T>>,
    parent: ViewGroup,
    viewType: Int
): BaseViewHolder<T> {
    return try {
        creators[viewType].creator(parent) as BaseViewHolder<T>
    } catch (e: Exception) {
        throw RuntimeException(
            "Cannot find view holder viewType: $viewType in list of creators: ${
                creators.map { it.clazz.simpleName }
                    .joinToString(", ")
            }")
    }
}