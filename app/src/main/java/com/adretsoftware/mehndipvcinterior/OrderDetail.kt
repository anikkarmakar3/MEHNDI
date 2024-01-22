package com.adretsoftware.mehndipvcinterior

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.OrderItemAdapter
import com.adretsoftware.mehndipvcinterior.adapters.orderItemFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityOrderDetailBinding
import com.adretsoftware.mehndipvcinterior.models.*
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetail : AppCompatActivity(), orderItemFunctions {
    lateinit var order: Order
    lateinit var orderItems: ArrayList<OrderItem>
    lateinit var adapter: OrderItemAdapter
    lateinit var binding: ActivityOrderDetailBinding
    var command: String? = ""
    var total : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.sixty1)

        order = Gson().fromJson(intent.getStringExtra("order"), Order::class.java)
        command = intent.getStringExtra("command")

        if (command == Constants.COMISSION)
            adapter = OrderItemAdapter(this, Constants.COMISSION)
        else
            adapter = OrderItemAdapter(this)
        binding.recylerView.layoutManager = LinearLayoutManager(this)
        binding.recylerView.adapter = adapter
        loadData()
        binding.confirmbtn.setOnClickListener { confirm() }

    }

    fun loadData() {
        binding.status.text = order.status
        binding.date.text = order.date
        binding.orderid.text = order.order_id
        binding.price.text = order.price
        binding.invoicebtn.setOnClickListener { invoice() }
        binding.confirmbtn.setOnClickListener { confirm() }
        binding.cancelbtn.setOnClickListener { cancel() }

        if (order.status != Constants.CONFIRMED && MySharedStorage.getUserType() == Constants.MANUFACTURER)
            binding.confirmbtn.visibility = View.VISIBLE
        if (order.status == Constants.CONFIRMED)
            binding.invoicebtn.visibility = View.VISIBLE
        if (order.status != Constants.DELIVERED)
            binding.cancelbtn.visibility = View.VISIBLE
        val order_id = RequestBody.create(MediaType.parse("text/plain"), order.order_id)
        RetrofitClient.getApiHolder().getOrderItems(order_id)
            .enqueue(object : Callback<OrderItemModelDetails>{
                override fun onResponse(
                    call: Call<OrderItemModelDetails>,
                    response: Response<OrderItemModelDetails>
                ) {
                    if (response.code() == Constants.code_OK) {
                        val orderItem: List<OrderItemModelDetailsItem>? = response.body()!!
                        Log.d("TAG", "getOrderITem:" + orderItem)
                        val orderItemDetails = ArrayList<OrderItem>()
                        orderItem?.forEach {
                            val orderItem = OrderItem()
                            orderItem.order_id = it.order_id
                            orderItem.item_id = it.item_id
                            /*orderItem.status = it.item_id*/
                            orderItem.code = it.code
                            orderItem.image_url = it.image_url
                            orderItem.quantity = it.quantity
                            orderItem.total_price = it.total_price
                            orderItem.price = it.price
                            orderItem.about = it.about
                            orderItem.name = it.name
                            orderItem.features = it.features
                            orderItemDetails.add(orderItem)
                            total += it.quantity.toInt()
                        }
                        binding.totalquantity.text = total.toString()
                        adapter.update(orderItemDetails)
                    } else
                        Log.d("TAG", "getOrderITem:" + response.code())
                }

                override fun onFailure(call: Call<OrderItemModelDetails>, t: Throwable) {
                    Log.d("TAG", "getOrderITem:" + t.localizedMessage)
                }

            })

    }

    override fun itemClick(itemId: String) {
    }

    fun invoice() {
        try {
            var download= applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            var PdfUri = Uri.parse(Constants.apiUrl2 + order.temp_path)
            var getPdf = DownloadManager.Request(PdfUri)
            getPdf.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            download.enqueue(getPdf)
            Toast.makeText(applicationContext,"Download Started", Toast.LENGTH_LONG).show()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        /*Toast.makeText(
            applicationContext,
            "please hold on for a moment. Don't press back button",
            Toast.LENGTH_LONG
        ).show()
        val invoiceData = InvoiceData().apply {
            this.items = orderItems
            this.order = order
            this.user = MySharedStorage.getUserr()
        }
        InvoiceGenerator(this, invoiceData)*/
    }

    fun confirm() {
        val order_id = RequestBody.create(MediaType.parse("text/plain"), order.order_id)
        val status = RequestBody.create(MediaType.parse("text/plain"), Constants.CONFIRMED)

        RetrofitClient.getApiHolder().updateOrderStatus(order_id, status)
            .enqueue(object : Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    if (response.code() == Constants.code_OK) {
                        binding.confirmbtn.visibility = View.GONE
                        Toast.makeText(applicationContext, "Confirmed!", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("TAG", "updateOrderStatus:" + response.code())
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    Log.d("TAG", "updateOrderStatus:" + t.localizedMessage)
                }
            })
    }

    fun cancel() {
        val order_id = RequestBody.create(MediaType.parse("text/plain"), order.order_id)
        val status = RequestBody.create(MediaType.parse("text/plain"), Constants.CANCELLED)

        RetrofitClient.getApiHolder().updateOrderStatus(order_id, status)
            .enqueue(object : Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    if (response.code() == Constants.code_OK) {
//                        binding.cancelbtn.visibility = View.GONE
                        Toast.makeText(applicationContext, "Cancelled!", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("TAG", "updateOrderStatus:" + response.code())
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    Log.d("TAG", "updateOrderStatus:" + t.localizedMessage)
                }
            })
    }
}