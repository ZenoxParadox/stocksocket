package com.bux.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bux.R
import com.bux.domain.model.Product

/**
 * Adapter for products
 */
class ProductAdapter(context: Context, val listener: ClickListener<Product>) :
    BaseRecyclerAdapter<ProductAdapter.Holder, Product>() {

    private val LOG_TAG = this::class.java.simpleName

    private val inflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = inflater.inflate(R.layout.item_product, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = getItem(position)
        holder.bind(item, position)
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvName:TextView = view.findViewById(R.id.item_product_displayName)
        private val tvCode:TextView = view.findViewById(R.id.item_product_securityCode)

        fun bind(item: Product, position: Int) {
            itemView.setOnClickListener {
                listener.onItemClick(item, position)
            }

            tvName.text = item.displayName
            tvCode.text = item.symbol
        }
    }

}