package com.adretsoftware.mehndipvcinterior.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.CategoryProductListActivity
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.data.model.ProductImage.ProductImageResp
import com.adretsoftware.mehndipvcinterior.models.Item
import com.bumptech.glide.Glide
import com.google.gson.Gson
import org.json.JSONObject


class ItemAdapter(
    listener: itemFunctions,
    layoutInflater: LayoutInflater,
    applicationContext: Context,
    clickListener: ClickListener,
    whichScreen: String,
    showDiscount: Boolean
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    var listener: itemFunctions
    var items = arrayListOf<Item>()
    var context: Context
    var clickListener: ClickListener
    var whichScreen: String
    var showDiscount: Boolean

    init {
        this.listener = listener
        this.context = applicationContext
        this.clickListener = clickListener
        this.whichScreen = whichScreen
        this.showDiscount = showDiscount
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

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        /*if (items[position].status == Constants.NotAvailable || items[position].user_set_status == Constants.NotAvailable) {
            holder.tvNotAvailable.visibility = View.VISIBLE
        } else {
            holder.tvNotAvailable.visibility = View.GONE
        }
*/
        holder.name.text = items[position].name
//        if (items[position].price.isNullOrEmpty() || items[position].price == "null") {
//            holder.price.text = ""

//            holder.priceUnit.visibility = View.GONE
//        } else {
//            if (items[position].individual_discount.isNullOrEmpty()) {
//                holder.price.text = items[position].price
//            } else {
//                holder.price.text = items[position].individual_discount
//            }
//            holder.priceUnit.visibility = View.VISIBLE
//        }
        holder.root.setOnClickListener(View.OnClickListener {
            listener.ItemClickFunc(items[position], it)
            val intent = Intent(context,CategoryProductListActivity::class.java)
            intent.putExtra("cat_id",items[position].id)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        })
        holder.root.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(p0: View?): Boolean {
                if (p0 != null) {
                    listener.LongItemClick(items[position], p0)
                    return true
                }
                return false
            }
        })

        var url2 = items[position].image_url
//        if (items[position].image_url!=null){
//            val imageData =  Gson().fromJson(items[position].image_url, ProductImageResp::class.java)
//             url2=  imageData[0].image.removeRange(0,13)
//        }



        val url = Constants.apiUrl2 + Constants.imageUrl + url2





        Glide.with(holder.itemView.context).load(url).into(holder.image)
        /*if (whichScreen == "product" || whichScreen == "category") {
            if (Utilis.isLoginAsAdmin()) {
                holder.deleteBtn.visibility = View.VISIBLE
                holder.deleteBtn.setOnClickListener {
                    clickListener.deleteItemClick(items[position].item_id)
                }
            } else {
                holder.deleteBtn.visibility = View.GONE
            }
        } else {
            holder.deleteBtn.visibility = View.GONE
        }*/
//
//        if (whichScreen == "discount" && !items[position].price.isNullOrEmpty() && items[position].discount.isNullOrEmpty()) {
//            holder.root.visibility = View.GONE
//        }

//        if (showDiscount) {
//            if (items[position].discount.isNullOrEmpty()) {
//                if (items[position].amount.isNullOrEmpty()) {
//                    holder.itemDiscountLayout.visibility = View.GONE
//                } else {
//                    holder.itemDiscountLayout.visibility = View.VISIBLE
//                    holder.itemDiscountPrice.text = items[position].amount
//                }
//            } else {
//                holder.itemDiscountLayout.visibility = View.VISIBLE
//                holder.itemDiscountPrice.text = items[position].amount
//            }
//        } else {
//            holder.itemDiscountLayout.visibility = View.GONE
//        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var image = view.findViewById<ImageView>(R.id.item_image)
        //        var price = view.findViewById<TextView>(R.id.item_price)
//        var priceUnit = view.findViewById<TextView>(R.id.item_price_unit)
        var name = view.findViewById<TextView>(R.id.item_title)
        var root = view.findViewById<CardView>(R.id.item_root)
        /*var deleteBtn = view.findViewById<ImageView>(R.id.deleteBtn)
        //        var itemDiscountLayout = view.findViewById<LinearLayout>(R.id.itemDiscountLayout)
//        var itemDiscountPrice = view.findViewById<TextView>(R.id.itemDiscountPrice)
        var tvNotAvailable = view.findViewById<TextView>(R.id.tvNotAvailable)*/
    }

    interface ClickListener {
        fun deleteItemClick(itemId: String)
    }
}

interface itemFunctions {
    fun ItemClickFunc(item: Item, view: View)
    fun LongItemClick(item: Item, view: View)
}
