package com.bux.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bux.util.Logger

private const val LOG_TAG = "BaseRecyclerAdapter"

abstract class BaseRecyclerAdapter<VH : RecyclerView.ViewHolder, T> : RecyclerView.Adapter<VH>() {

    private lateinit var items: List<T>

    private var contentView: View? = null
    private var emptyView: View? = null

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            Logger.i(LOG_TAG, "onChanged()")

            toggleEmptyView()
        }
    }

    init {
        Logger.addIgnoreTag(LOG_TAG)
        this.registerAdapterDataObserver(observer)
    }

    override fun getItemCount(): Int {
        if (!this::items.isInitialized) {
            return 0
        }

        return items.size
    }

    fun setItems(items: List<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    fun setEmptyView(content: View, empty: View) {
        contentView = content
        emptyView = empty

        toggleEmptyView()

    }

    private fun toggleEmptyView() {
        if (emptyView != null && contentView != null) {
            if (itemCount == 0) {
                emptyView?.visibility = View.VISIBLE
                contentView?.visibility = View.GONE
            } else {
                emptyView?.visibility = View.GONE
                contentView?.visibility = View.VISIBLE
            }
        }
    }

    interface ClickListener<T> {

        fun onItemClick(item: T, position: Int)

        fun onItemLongPress(item: T, position: Int) {
            // Optional
        }

    }

}
