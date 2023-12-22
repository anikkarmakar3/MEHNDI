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
import com.adretsoftware.mehndipvcinterior.models.Data
import com.adretsoftware.mehndipvcinterior.models.GetGalleryModel
import com.adretsoftware.mehndipvcinterior.models.PictureGalleryModel
import com.bumptech.glide.Glide


class Picture_Product_ItemAdapter(
    listener: RecycleItemClickListener,
    layoutInflater: LayoutInflater,
    applicationContext: Context
) : RecyclerView.Adapter<Picture_Product_ItemAdapter.ViewHolder>() {

    var listener: RecycleItemClickListener
    var items = arrayListOf<Data>()

    init {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inflator_picture_gallery_item_list, parent, false)
        val vh = ViewHolder(view)
        return vh
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(items: ArrayList<Data>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // holder.name.text = items[position].picture_name
//        if(items[position].price.isNullOrEmpty()){
//            holder.price.text=""
//            holder.priceUnit.visibility=View.GONE
//        }else {
//            holder.price.text = items[position].price
//            holder.priceUnit.visibility=View.VISIBLE
//        }
        val url = Constants.apiUrl + Constants.imageUrl + items[position].filename
        holder.root.setOnClickListener(View.OnClickListener {
           // listener.onItemClick(url, items[position].picture_name)
        })
//        Log.d("TAG",url)
        Glide.with(holder.itemView.context).load(url).into(holder.image)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.item_title)
        var image = view.findViewById<ImageView>(R.id.item_image)
        var root = view.findViewById<CardView>(R.id.item_root)
    }
}

interface RecycleItemClickListener {
    fun onItemClick(url: String, name: String)
}
