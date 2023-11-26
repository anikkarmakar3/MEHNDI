package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.models.Item
import com.bumptech.glide.Glide

class ManageProductsAdapter(
    listener: ManageProductAdapterFunctions,
    layoutInflater: LayoutInflater,
    applicationContext: Context,
    clickListener: ClickListener,
    whichScreen: String
) : RecyclerView.Adapter<ManageProductsAdapter.ViewHolder>() {

    var listener: ManageProductAdapterFunctions
    var items = arrayListOf<Item>()
    var context: Context
    var clickListener: ClickListener
    var whichScreen: String

    init {
        this.listener = listener
        this.context = applicationContext
        this.clickListener = clickListener
        this.whichScreen = whichScreen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.customview_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(items: ArrayList<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items[position].status == Constants.NotAvailable || items[position].user_set_status == Constants.NotAvailable) {
            holder.tvNotAvailable.visibility = View.VISIBLE
        } else {
            holder.tvNotAvailable.visibility = View.GONE
        }

        holder.name.text = items[position].name
//        if (!items[position].price.isNullOrEmpty()) {
//            if (items[position].user_set_price.isNullOrEmpty()) {
//                holder.price.text = "Set Price"
//                holder.priceUnit.visibility = View.GONE
//            } else {
//                holder.price.text = items[position].user_set_price
//                holder.priceUnit.visibility = View.VISIBLE
//            }
//        } else {
//            holder.price.text = ""
//            holder.priceUnit.visibility = View.GONE
//        }

        holder.root.setOnClickListener(View.OnClickListener {
            listener.ItemClickFunc(items[position], it)
        })

        val url = Constants.apiUrl + Constants.imageUrl + items[position].image_url
        Glide.with(holder.itemView.context).load(url).into(holder.image)

        holder.deleteBtn.visibility = View.GONE
//        holder.itemDiscountLayout.visibility = View.GONE
//
//        if (items[position].discount.isNullOrEmpty()) {
//            holder.itemDiscountLayout.visibility = View.GONE
//        } else {
//            holder.itemDiscountLayout.visibility = View.VISIBLE
//            holder.itemDiscountPrice.text = items[position].discount
//        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image = view.findViewById<ImageView>(R.id.item_image)
//        var price = view.findViewById<TextView>(R.id.item_price)
//        var priceUnit = view.findViewById<TextView>(R.id.item_price_unit)
        var name = view.findViewById<TextView>(R.id.item_title)
        var root = view.findViewById<CardView>(R.id.item_root)
        var deleteBtn = view.findViewById<ImageView>(R.id.deleteBtn)
//        var itemDiscountLayout = view.findViewById<LinearLayout>(R.id.itemDiscountLayout)
//        var itemDiscountPrice = view.findViewById<TextView>(R.id.itemDiscountPrice)
        var tvNotAvailable = view.findViewById<TextView>(R.id.tvNotAvailable)
    }

    interface ClickListener {
        fun deleteItemClick(itemId: String)
    }
}

interface ManageProductAdapterFunctions {
    fun ItemClickFunc(item: Item, view: View)
}
