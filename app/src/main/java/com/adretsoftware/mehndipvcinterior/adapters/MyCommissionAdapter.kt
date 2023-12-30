package com.adretsoftware.mehndipvcinterior.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.models.CommissionModelItem
import com.adretsoftware.mehndipvcinterior.models.NewCommisionModelItem

class MyCommissionAdapter() :
    RecyclerView.Adapter<MyCommissionAdapter.ViewHolder>() {
    /*var myCommission = (arrayListOf<MyCommissionModel>())*/
    private var commissionData = arrayListOf<NewCommisionModelItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.customview_mycommission_item, parent, false)
        val vh = ViewHolder(view)
        return vh
    }

    fun update(myCommission: ArrayList<NewCommisionModelItem>) {
        this.commissionData = myCommission
        notifyDataSetChanged()
    }

    /*fun update(myCommission: ArrayList<MyCommissionModel>) {
        this.myCommission = myCommission
        notifyDataSetChanged()
    }*/

    override fun getItemCount(): Int {
        return commissionData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderitem_name.text = commissionData[position].order_id
        holder.orderitem_price.text = commissionData[position].earning_amount
        holder.orderbyname.text = commissionData[position].name
        /*holder.orderitem_code.text = myCommission[position].code*/
        /*holder.orderitem_quantity.text = commissionData[position].*/

        /*if (myCommission[position].commission == "Percentage") {
            holder.discountTypeTxt.text = "%"
            holder.orderitem_price.text = myCommission[position].percentage
        } else {
            holder.discountTypeTxt.text = "Rs."
            holder.orderitem_price.text = myCommission[position].set_price
        }

        val url = Constants.apiUrl + Constants.imageUrl + myCommission[position].image_url
        Glide.with(holder.orderitem_name.context).load(url)
            .into(holder.orderitem_image)*/
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var orderitem_name = view.findViewById<TextView>(R.id.orderitem_name)
        var orderbyname = view.findViewById<TextView>(R.id.orderbyname)
        var orderitem_image = view.findViewById<ImageView>(R.id.orderitem_image)
        var orderitem_code = view.findViewById<TextView>(R.id.orderitem_code)
        var orderitem_quantity = view.findViewById<TextView>(R.id.orderitem_quantity)
        var orderitem_price = view.findViewById<TextView>(R.id.orderitem_price)
        var discountTypeTxt = view.findViewById<TextView>(R.id.discountTypeTxt)
    }
}