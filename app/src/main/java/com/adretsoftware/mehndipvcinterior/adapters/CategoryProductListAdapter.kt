package com.adretsoftware.mehndipvcinterior.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.models.CategoryProductListModel
import com.adretsoftware.mehndipvcinterior.models.CategoryProductsModelItem
import com.bumptech.glide.Glide

class CategoryProductListAdapter(
    listener: CategoryProductFunctions,
    layoutInflater: LayoutInflater,
    applicationContext: Context,

    whichScreen: String,
    showDiscount: Boolean
) : RecyclerView.Adapter<CategoryProductListAdapter.ViewHolder>(){

    var listener: CategoryProductFunctions
    /*var items = arrayListOf<CategoryProductListModel>()*/
    var items = arrayListOf<CategoryProductsModelItem>()
    var context: Context

    var whichScreen: String
    var showDiscount: Boolean

    init {
        this.listener = listener
        this.context = applicationContext

        this.whichScreen = whichScreen
        this.showDiscount = showDiscount
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryProductListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.customview_item, parent, false)
        return CategoryProductListAdapter.ViewHolder(view)

    }

    override fun onBindViewHolder(holder: CategoryProductListAdapter.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.name.text = items[position].name
        holder.tvNotAvailable.text = items[position].status
        holder.root.setOnClickListener {
            listener.ItemClickFunc(items[position],it)
        }
        /*if (items[position].status == Constants.NotAvailable || items[position].user_set_status == Constants.NotAvailable) {
            holder.tvNotAvailable.visibility = View.VISIBLE
        } else {
            holder.tvNotAvailable.visibility = View.GONE
        }

        holder.name.text = items[position].name

        holder.root.setOnClickListener(View.OnClickListener {
            listener.ItemClickFunc(items[position], it)


        })
        holder.root.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                if (p0 != null) {
                    listener.LongItemClick(items[position], p0)
                    return true
                }
                return false
            }
        })*/


//        Image Url part


        val imageUrl :String = Constants.apiUrl2 + items[position].image.first()
        /* for (i in items[position].image){
             imageUrl = Constants.apiUrl2 + i[0]
             break
         }*/

        /*var url2 = items[position].image

        val url = Constants.apiUrl2 + Constants.imageUrl + url2*/

        Glide.with(holder.itemView.context).load(imageUrl).into(holder.image)


//        if (whichScreen == "product" || whichScreen == "category") {
//            if (Utilis.isLoginAsAdmin()) {
//                holder.deleteBtn.visibility = View.VISIBLE
//                holder.deleteBtn.setOnClickListener {
//                    clickListener.deleteItemClick(items[position].item_id)
//                }
//            } else {
//                holder.deleteBtn.visibility = View.GONE
//            }
//        } else {
//            holder.deleteBtn.visibility = View.GONE
//        }



    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun update(items: ArrayList<CategoryProductsModelItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image = view.findViewById<ImageView>(R.id.item_image)
        /*var price = view.findViewById<TextView>(R.id.item_price)*/
//        var priceUnit = view.findViewById<TextView>(R.id.item_price_unit)
        var name = view.findViewById<TextView>(R.id.item_title)
        var root = view.findViewById<CardView>(R.id.item_root)
        var deleteBtn = view.findViewById<ImageView>(R.id.deleteBtn)
        //        var itemDiscountLayout = view.findViewById<LinearLayout>(R.id.itemDiscountLayout)
//        var itemDiscountPrice = view.findViewById<TextView>(R.id.itemDiscountPrice)
        var tvNotAvailable = view.findViewById<TextView>(R.id.tvNotAvailable)
    }

//    interface ClickListener2 {
//        fun deleteItemClick(itemId: String)
//    }


}

interface CategoryProductFunctions {
    fun ItemClickFunc(item: CategoryProductsModelItem, view: View)
    fun LongItemClick(item: CategoryProductsModelItem, view: View)
}