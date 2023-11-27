package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.models.ProductOfferModelItem
import com.bumptech.glide.Glide

class OfferProductItemAdapter(context:Context,listener:OfferItemClick) : RecyclerView.Adapter<OfferProductItemAdapter.ViewHolder>(){

    var context : Context
    var listener: OfferItemClick
    init {
        this.context = context
        this.listener = listener
    }

    private var discountedProduct = ArrayList<ProductOfferModelItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_offer_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return discountedProduct.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productImageUrl = Constants.apiUrl + Constants.imageUrl + discountedProduct[position].product_image
        Glide.with(holder.productImage.context).load(productImageUrl).into(holder.productImage)

        holder.productName.text = discountedProduct[position].item_name
        holder.productPrice.text = discountedProduct[position].price
        holder.productDiscountPrice.text = discountedProduct[position].discount_price.toString()
        holder.itemView.setOnClickListener {
            listener.onProductClick(discountedProduct[position])
        }
    }

    fun dispatchDataToAdapter(productItem: ArrayList<ProductOfferModelItem>){
        this.discountedProduct = productItem
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage = view.findViewById<ImageView>(R.id.product_image)
        val productName = view.findViewById<TextView>(R.id.product_name)
        val productPrice = view.findViewById<TextView>(R.id.product_price)
        val productDiscountPrice = view.findViewById<TextView>(R.id.product_discount_price)
    }
}

interface OfferItemClick{
    fun onProductClick(productOfferModelItem: ProductOfferModelItem)
}