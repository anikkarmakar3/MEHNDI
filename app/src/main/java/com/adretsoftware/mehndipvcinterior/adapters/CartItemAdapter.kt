package com.adretsoftware.mehndipvcinterior.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.models.CartItem
import com.bumptech.glide.Glide


class CartItemAdapter(
    listener: cartItemFunctions,
    layoutInflater: LayoutInflater,
    applicationContext: Context
): RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

    var listener:cartItemFunctions
    var items= arrayListOf<CartItem>()
    init {
        this.listener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.customview_cart_item,parent,false)
        val vh=ViewHolder(view)
        return vh
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun update(items:ArrayList<CartItem>){
        this.items=items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.quantity.text=items[position].quantity
        /*holder.code.text=items[position].code*/
        holder.name.text=items[position].name
        holder.price.text=items[position].price
        holder.increase.setOnClickListener(View.OnClickListener {
            holder.quantity.text=( items[position].quantity.toInt() + 1).toString()
            listener.increaseQuantity(holder,position)
        })
        holder.decrease.setOnClickListener(View.OnClickListener {
            holder.quantity.text=( items[position].quantity.toInt() - 1).toString()
            listener.decreaseQuantity(holder,position)

        })
//        holder.delete.setOnClickListener(View.OnClickListener {
//            listener.deleteItem(position)
//        })
        if(items[position].image_url.length>0){

        }
        val url=Constants.apiUrl+Constants.imageUrl+(items[position].image_url).substringAfterLast("/")
        Log.d("TAG",url)
        Glide.with(holder.itemView.context).load(url).into(holder.image)
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var image=view.findViewById<ImageView>(R.id.cart_image)
        var price=view.findViewById<TextView>(R.id.cart_price)
        var name=view.findViewById<TextView>(R.id.cart_name)
        /*var code=view.findViewById<TextView>(R.id.cart_code)*/
        var root=view.findViewById<CardView>(R.id.cart_root)
        var decrease=view.findViewById<ImageButton>(R.id.cart_decrease)
        var increase=view.findViewById<ImageButton>(R.id.cart_increase)
//        var delete=view.findViewById<ImageButton>(R.id.cart_remove)
        val quantity=view.findViewById<TextView>(R.id.cart_quantity)

    }
}
interface cartItemFunctions{
    fun deleteItem(position:Int)
    fun increaseQuantity(holder:CartItemAdapter.ViewHolder,position: Int)
    fun decreaseQuantity(holder:CartItemAdapter.ViewHolder,position: Int)
}
