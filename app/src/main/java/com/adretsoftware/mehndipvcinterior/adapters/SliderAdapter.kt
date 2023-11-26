package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.adretsoftware.mehndipvcinterior.ImageViewZoomActivity
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage.context
import com.adretsoftware.mehndipvcinterior.models.Image
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter


class SliderAdapter() : SliderViewAdapter<SliderAdapter.ViewHolder>() {
    var images: ArrayList<Image> = ArrayList()
    override fun getCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_slider, parent, false)
        return ViewHolder(view)
    }

    fun updateData(images: ArrayList<Image>) {
        this.images = images
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, position: Int) {

        val url = Constants.apiUrl + Constants.imageUrl + images[position].filename
        Glide.with(viewHolder?.imageView!!.context).load(url).into(viewHolder?.imageView!!)


            viewHolder.imageView.setOnClickListener(View.OnClickListener {
                // Handle image click here, open fullscreen activity
                val intent = Intent(context, ImageViewZoomActivity::class.java)
                intent.putExtra("url", url)
                context.startActivity(intent)

            })


    }

    class ViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        var imageView = itemView.findViewById<ImageView>(R.id.slide_image)

    }
}