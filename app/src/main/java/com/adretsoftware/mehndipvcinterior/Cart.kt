package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.CartItemAdapter
import com.adretsoftware.mehndipvcinterior.adapters.cartItemFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityCartBinding
import com.adretsoftware.mehndipvcinterior.models.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Cart : AppCompatActivity(), cartItemFunctions {
    lateinit var binding: ActivityCartBinding
    lateinit var adapter: CartItemAdapter
    var items: ArrayList<CartItem> = ArrayList()
    var price = 0F
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.sixty1)

        adapter = CartItemAdapter(this, layoutInflater, this)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        loadData()
        binding.continueBtn.setOnClickListener {
            if (items.isNotEmpty())
                placeOrder()
        }
    }

    override fun deleteItem(position: Int) {
        deleteCart(items[position].user_id, items[position].item_id)
        items.removeAt(position)
        updateData()
    }

    override fun increaseQuantity(holder: CartItemAdapter.ViewHolder, position: Int) {
        val q = items[position].quantity.toInt() + 1
        items[position].quantity = q.toString()
        val quantity = RequestBody.create(MediaType.parse("text/plain"), items[position].quantity)
        val user_id = RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
        val item_id = RequestBody.create(MediaType.parse("text/plain"), items[position].item_id)
        val tp = items[position].quantity.toInt() * items[position].price.toFloat()
        items[position].total_price = tp.toString()
        val total_price =
            RequestBody.create(MediaType.parse("text/plain"), items[position].total_price)
        price += items[position].price.toFloat()
        Log.d("TAG", "increaseQuant::itemID${items.get(position).item_id}")
        Log.d("TAG", "increaseQuant::userId${MySharedStorage.getUserId()}")
        Log.d("TAG", "increaseQuant::quantity${items.get(position).quantity}")
        Log.d("TAG", "increaseQuant::total_price${items.get(position).total_price}")

      price=  items.get(position).total_price.toFloat()
        binding.amount.text = price.toString()
        RetrofitClient.getApiHolder().updateCartQuantity(item_id, user_id, quantity, total_price)
            .enqueue(object : Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    Log.d("TAG", "updateCartQuantity:" + response.code().toString())
                    if (response.code() == Constants.code_OK) {
                        holder.quantity.setText(items[position].quantity)
                        updateData()
                    }
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    Log.d("TAG", "updateCartQuantity:" + t.localizedMessage)
                }
            })
    }

    fun updateData() {
        binding.amount.text = price.toString()
        adapter.update(items)
    }



    override fun decreaseQuantity(holder: CartItemAdapter.ViewHolder, position: Int) {
        if (items[position].quantity.toInt() != 1) {
            val q = items[position].quantity.toInt() - 1
            items[position].quantity = q.toString()
            val quantity =
                RequestBody.create(MediaType.parse("text/plain"), items[position].quantity)
            val user_id =
                RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
            val item_id = RequestBody.create(MediaType.parse("text/plain"), items[position].item_id)
            val tp = items[position].quantity.toInt() * items[position].price.toFloat()
            items[position].total_price = tp.toString()
            val total_price =
                RequestBody.create(MediaType.parse("text/plain"), items[position].total_price)
            price -= items[position].price.toFloat()
            Log.d("TAG", "increaseQuant::itemID${items.get(position).item_id}")
            Log.d("TAG", "increaseQuant::userId${MySharedStorage.getUserId()}")
            Log.d("TAG", "increaseQuant::quantity${items.get(position).quantity}")
            Log.d("TAG", "increaseQuant::total_price${items.get(position).total_price}")
            binding.amount.text = price.toString()
            RetrofitClient.getApiHolder()
                .updateCartQuantity(item_id, user_id, quantity, total_price)
                .enqueue(object : Callback<RetrofitResponse> {
                    override fun onResponse(
                        call: Call<RetrofitResponse>,
                        response: Response<RetrofitResponse>
                    ) {
                        Log.d("TAG", "updateCartQuantity:" + response.code().toString())
                        if (response.code() == Constants.code_OK) {
                            holder.quantity.setText(items[position].quantity)
                            updateData()
                        }
                    }

                    override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                        Log.d("TAG", "updateCartQuantity:" + t.localizedMessage)
                    }
                })
        } else {
            deleteItem(position)
        }
    }

    fun loadData() {
        Log.d("TAG", "zzzzzz:" + MySharedStorage.getUserId())
        val user_id = RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
        RetrofitClient.getApiHolder().getCart(user_id)
            .enqueue(object : retrofit2.Callback<RetrofitCartItem> {
                override fun onResponse(
                    call: Call<RetrofitCartItem>,
                    response: Response<RetrofitCartItem>
                ) {
                    if (response.code() == Constants.code_OK) {
                        binding.empty.visibility = View.GONE
                        items = response.body()!!.data

                        for (item in items) {
                            if (!item.total_price.isEmpty()) {
                                price += item.total_price.toFloat()
                            }
                            Log.d("TAG", "fuck" + price)
                        }
                        updateData()

                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        binding.empty.visibility = View.VISIBLE
                    }
                    Log.d("TAG", "getCart:" + response.code().toString())
                }

                override fun onFailure(call: Call<RetrofitCartItem>, t: Throwable) {
                    Log.d("TAG", "getCart:" + t.localizedMessage)
                }

            })
    }

    fun deleteCart(userId: String, itemId: String) {
        val user_id = RequestBody.create(MediaType.parse("text/plain"), userId)
        val item_id = RequestBody.create(MediaType.parse("text/plain"), itemId)
        RetrofitClient.getApiHolder().deleteCart(item_id, user_id)
            .enqueue(object : Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    if (response.code() == Constants.code_OK) {
                        Log.d("TAG", "deleteCart" + "successfull")
                    }
                    Log.d("TAG", "deleteCart:" + response.code())
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    Log.d("TAG", "deleteCart:" + t.localizedMessage)
                }
            })

    }

    fun placeOrder() {
        val order = Order()
        order.order_id = System.currentTimeMillis().toString()
        val calendar: Calendar =
            Calendar.getInstance() // Returns instance with current date and time set
        val formatter = SimpleDateFormat("yyyy-dd-MM")
        order.date = formatter.format(calendar.time)
        order.user_id = MySharedStorage.getUserId()
        order.name = MySharedStorage.getUserr().name


        val user_id = RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
        for (item in items) {
            order.title = order.title + ", " + item.name
            order.status = "Pending Confirmation"
            order.price = price.toString()
            val orderItem = OrderItem()
            orderItem.fromCartItem(item)
            orderItem.order_id = order.order_id
            order.product_id = item.item_id


            RetrofitClient.getApiHolder().setOrderItems(orderItem)
                .enqueue(object : Callback<RetrofitResponse> {
                    override fun onResponse(
                        call: Call<RetrofitResponse>,
                        response: Response<RetrofitResponse>
                    ) {
                        Log.d("TAG", "setorderITems:" + response.code())
                    }

                    override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                        Log.d("TAG", "setOrderItems:" + t.localizedMessage)
                    }
                })
            if (item == items.last()) {
                order.title = order.title.substring(2, order.title.length)
                RetrofitClient.getApiHolder().deleteWholeCart(user_id)
                    .enqueue(object : Callback<RetrofitResponse> {
                        override fun onResponse(
                            call: Call<RetrofitResponse>,
                            response: Response<RetrofitResponse>
                        ) {
                            Log.d("TAG", "deleteWholeCart:" + response.code().toString())
                        }

                        override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                            Log.d("TAG", "deleteWholeCart:" + t.localizedMessage)
                        }
                    })
                RetrofitClient.getApiHolder().setOrder(order)
                    .enqueue(object : Callback<RetrofitResponse> {
                        override fun onResponse(
                            call: Call<RetrofitResponse>,
                            response: Response<RetrofitResponse>
                        ) {
                            for (deleteItem in items) {
                                deleteCart(deleteItem.user_id, deleteItem.item_id)
                                updateData()
                            }
                            items.clear()

                            Toast.makeText(applicationContext, "done!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, MyOrder::class.java))
                            Log.d("TAG", "setOrder:" + response.code())
                            finish()
                        }

                        override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                            Log.d("TAG", "setOrder:" + t.localizedMessage)
                        }
                    })
            }
        }
    }
}