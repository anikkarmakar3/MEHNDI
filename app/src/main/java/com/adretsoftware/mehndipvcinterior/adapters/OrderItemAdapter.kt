package com.adretsoftware.mehndipvcinterior.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.models.OrderItem
import com.adretsoftware.mehndipvcinterior.models.RetrofitDiscount
import com.bumptech.glide.Glide
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderItemAdapter(
    listener: orderItemFunctions,
    command:String="order"
): RecyclerView.Adapter<OrderItemAdapter.ViewHolder>() {

    var listener:orderItemFunctions
    var items= arrayListOf<OrderItem>()
    var total : Int = 0
    lateinit var command: String
    init {
        this.listener=listener
        this.command=command
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.customview_order_item,parent,false)
        val vh=ViewHolder(view)
        return vh
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun update(items:ArrayList<OrderItem>){
        this.items=items
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.code.visibility = View.GONE
        holder.name.text=items[position].name
        holder.price.text=items[position].price
        holder.quantity.text=items[position].quantity
        holder.root.setOnClickListener { listener.itemClick(items[position].item_id) }
        val url=Constants.apiUrl+Constants.imageUrl+items[position].image_url.substringAfterLast("/")
        Log.d("TAG",url)
        Glide.with(holder.itemView.context).load(url).into(holder.image)
        if(command==Constants.COMISSION){
            priceFetch(holder,items[position])
            holder.commision_portion.visibility=View.VISIBLE
        }
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var image=view.findViewById<ImageView>(R.id.orderitem_image)
        var price=view.findViewById<TextView>(R.id.orderitem_price)
        var name=view.findViewById<TextView>(R.id.orderitem_name)
        var code=view.findViewById<TextView>(R.id.orderitem_code)
        var root=view.findViewById<CardView>(R.id.orderitem_root)
        val quantity=view.findViewById<TextView>(R.id.orderitem_quantity)
        val profit=view.findViewById<TextView>(R.id.orderitem_profit)
        val actualprice=view.findViewById<TextView>(R.id.orderitem_actual_price)
        val commision_portion=view.findViewById<LinearLayout>(R.id.orderitem_comission_portion)
    }
}
interface orderItemFunctions{
    fun itemClick(itemId: String)
}
fun priceFetch(holder: OrderItemAdapter.ViewHolder,orderItem: OrderItem) {
    val user_id= RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
    val item_id= RequestBody.create(MediaType.parse("text/plain"), orderItem.item_id)
    RetrofitClient.getApiHolder().getDiscountByUser(user_id,item_id).enqueue(object:Callback<RetrofitDiscount>{
        override fun onResponse(call: Call<RetrofitDiscount>, response: Response<RetrofitDiscount>) {
            if(response.code()==Constants.code_OK){
                if(response.body()!!.data[0].discount_type == Constants.PRICE){
                    val disc=response.body()!!.data[0].amount
                    val amount=orderItem.actual_price.toFloat()-disc.toFloat()
                    holder.actualprice.text=amount.toString()
                    val profit=orderItem.price.toFloat()-amount
                    holder.profit.text=profit.toString()
                }else{
                    val disc=response.body()!!.data[0].amount
                    val amount="0.${disc}".toFloat() * orderItem.actual_price.toFloat()
                    holder.actualprice.text=amount.toString()
                }
            }else if(response.code()==Constants.code_NO_CONTENT){
                if(orderItem.parent.isNotBlank()){
                    val item_id= RequestBody.create(MediaType.parse("text/plain"), orderItem.parent)
                    RetrofitClient.getApiHolder().getDiscountByUser(user_id,item_id).enqueue(object:Callback<RetrofitDiscount>{
                        override fun onResponse(call: Call<RetrofitDiscount>, response: Response<RetrofitDiscount>) {
                            if(response.code()==Constants.code_OK){
                                if(response.body()!!.data[0].discount_type == Constants.PRICE){
                                    val disc=response.body()!!.data[0].amount
                                    val amount=orderItem.actual_price.toFloat()-disc.toFloat()
                                    holder.actualprice.text=amount.toString()
                                    val profit={amount-orderItem.price.toFloat()}.toString()
                                    holder.profit.text=profit
                                }else{
                                    val disc=response.body()!!.data[0].amount
                                    val amount="0.${disc}".toFloat() * orderItem.actual_price.toFloat()
                                    holder.actualprice.text=amount.toString()
                                }
                            }
                            Log.d("TAG","getdiscountByUser:"+response.code().toString())
                        }
                        override fun onFailure(call: Call<RetrofitDiscount>, t: Throwable) {
                            Log.d("TAG","getdiscountByUser:"+t.localizedMessage)
                        }
                    })
                }

            }
            Log.d("TAG","getdiscountByUser:"+response.code().toString())
        }
        override fun onFailure(call: Call<RetrofitDiscount>, t: Throwable) {
            Log.d("TAG","getdiscountByUser:"+t.localizedMessage)
        }
    })
}
