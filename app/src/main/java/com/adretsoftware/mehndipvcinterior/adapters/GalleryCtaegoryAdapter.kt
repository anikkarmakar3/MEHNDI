package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.CategoryProductListActivity
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.models.Item

class GalleryCtaegoryAdapter(listener: itemFunctions,
                             layoutInflater: LayoutInflater,
                             applicationContext: Context,
                             clickListener: ItemAdapter.ClickListener,
                             whichScreen: String,
                             showDiscount: Boolean) : RecyclerView.Adapter<GalleryCtaegoryAdapter.ViewHolder>(){

    var listener: itemFunctions
    var items = arrayListOf<Item>()
    var context: Context
    var clickListener: ItemAdapter.ClickListener
    var whichScreen: String
    var showDiscount: Boolean

    init {
        this.listener = listener
        this.context = applicationContext
        this.clickListener = clickListener
        this.whichScreen = whichScreen
        this.showDiscount = showDiscount
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.item_title)
        var root = view.findViewById<CardView>(R.id.item_root)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.gallerycategoryitem, parent, false)
        return GalleryCtaegoryAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(items: ArrayList<Item>) {
        this.items = items
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = items[position].name
        holder.root.setOnClickListener(View.OnClickListener {
            listener.ItemClickFunc(items[position], it)
        })
    }
}


