package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.models.Order

class OrderAdapter(
    listener: orderFunctions,
    layoutInflater: LayoutInflater,
    applicationContext: Context,
    command: String = "order"
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    var listener: orderFunctions
    var items = arrayListOf<Order>()
    var command = ""

    init {
        this.listener = listener
        this.command = command
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.customview_order, parent, false)
        val vh = ViewHolder(view)
        return vh
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(items: ArrayList<Order>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (command == Constants.COMISSION) {
            holder.buyer_layout.visibility = View.VISIBLE
            holder.name.text = items[position].name
        }
        holder.date.text = items[position].date
        holder.title.text = items[position].title
        holder.price.text = items[position].price
        holder.status.text = items[position].status
        holder.root.setOnClickListener { listener.itemClick(items[position]) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var price = view.findViewById<TextView>(R.id.order_price)
        var title = view.findViewById<TextView>(R.id.order_title)
        var root = view.findViewById<CardView>(R.id.order_root)
        var date = view.findViewById<TextView>(R.id.order_date)
        var status = view.findViewById<TextView>(R.id.order_status)
        var buyer_layout = view.findViewById<LinearLayout>(R.id.order_buyer_details)
        var name = view.findViewById<TextView>(R.id.order_name)
    }
}

interface orderFunctions {
    fun itemClick(order: Order)
}
