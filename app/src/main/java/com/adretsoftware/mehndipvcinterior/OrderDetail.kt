package com.adretsoftware.mehndipvcinterior

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
            .enqueue(object : Callback<RetrofitOrderItem> {
                override fun onResponse(
                    call: Call<RetrofitOrderItem>,
                    response: Response<RetrofitOrderItem>
                ) {
                    if (response.code() == Constants.code_OK) {
                        orderItems = response.body()!!.data
                        adapter.update(orderItems)
                    } else
                        Log.d("TAG", "getOrderITem:" + response.code())
                }

                override fun onFailure(call: Call<RetrofitOrderItem>, t: Throwable) {
                    Log.d("TAG", "getOrderItem:" + t.localizedMessage)
                }
            })

    }

    override fun itemClick(itemId: String) {
    }

    fun invoice() {
        Toast.makeText(
            applicationContext,
            "please hold on for a moment. Don't press back button",
            Toast.LENGTH_LONG
        ).show()
        val invoiceData = InvoiceData().apply {
            this.items = orderItems
            this.order = order
            this.user = MySharedStorage.getUserr()
        }
        InvoiceGenerator(this, invoiceData)
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