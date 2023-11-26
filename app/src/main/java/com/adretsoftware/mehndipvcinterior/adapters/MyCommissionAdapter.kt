package com.adretsoftware.mehndipvcinterior.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.models.MyCommissionModel
import com.bumptech.glide.Glide

class MyCommissionAdapter() :
    RecyclerView.Adapter<MyCommissionAdapter.ViewHolder>() {
    var myCommission = (arrayListOf<MyCommissionModel>())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.customview_mycommission_item, parent, false)
        val vh = ViewHolder(view)
        return vh
    }

    fun update(myCommission: ArrayList<MyCommissionModel>) {
        this.myCommission = myCommission
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return myCommission.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderitem_name.text = myCommission[position].name
        holder.orderitem_code.text = myCommission[position].code
        holder.orderitem_quantity.text = myCommission[position].quantity

        if (myCommission[position].commission == "Percentage") {
            holder.discountTypeTxt.text = "%"
            holder.orderitem_price.text = myCommission[position].percentage
        } else {
            holder.discountTypeTxt.text = "Rs."
            holder.orderitem_price.text = myCommission[position].set_price
        }

        val url = Constants.apiUrl + Constants.imageUrl + myCommission[position].image_url
        Glide.with(holder.orderitem_name.context).load(url)
            .into(holder.orderitem_image)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var orderitem_name = view.findViewById<TextView>(R.id.orderitem_name)
        var orderitem_image = view.findViewById<ImageView>(R.id.orderitem_image)
        var orderitem_code = view.findViewById<TextView>(R.id.orderitem_code)
        var orderitem_quantity = view.findViewById<TextView>(R.id.orderitem_quantity)
        var orderitem_price = view.findViewById<TextView>(R.id.orderitem_price)
        var discountTypeTxt = view.findViewById<TextView>(R.id.discountTypeTxt)
    }
}