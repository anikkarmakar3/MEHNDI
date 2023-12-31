package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.*
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMyComissionsBinding
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMyDistributorBinding
import com.adretsoftware.mehndipvcinterior.models.*
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyDistributor : AppCompatActivity(), userFunctions, orderItemFunctions, orderFunctions {
    lateinit var binding: ActivityMyDistributorBinding
    lateinit var userAdapter: UserAdapter
    lateinit var orderAdapter: OrderAdapter
    lateinit var orderItemAdapter: OrderItemAdapter
    lateinit var users: ArrayList<User>
    lateinit var orders: ArrayList<Order>
    lateinit var orderItems: ArrayList<OrderItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyDistributorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.sixty1)

        userAdapter = UserAdapter(this, "")
        orderAdapter = OrderAdapter(this, layoutInflater, this)
        orderItemAdapter = OrderItemAdapter(this, Constants.COMISSION)
        binding.recyclerView.adapter = userAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

//        if (Utilis.isLoginAsAdmin()) {
//            RetrofitClient.getApiHolder().getAdminCommission()
//                .enqueue(object : Callback<RetrofitUser> {
//                    override fun onResponse(
//                        call: Call<RetrofitUser>,
//                        response: Response<RetrofitUser>
//                    ) {
//                        if (response.code() == Constants.code_OK) {
//                            users = response.body()!!.data
//                            userAdapter.update(users)
//                        } else if (response.code() == Constants.code_NO_CONTENT) {
//                            Toast.makeText(
//                                applicationContext,
//                                "No User Present Under You!",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            Log.d("TAG", "getUserbyParent:" + response.code())
//                        } else {
//                            Log.d("TAG", "getUserbyParent:" + response.code())
//                        }
//                    }
//
//                    override fun onFailure(call: Call<RetrofitUser>, t: Throwable) {
//                        Log.d("TAG", "getUserbyParent:" + t.localizedMessage)
//                    }
//                })
//        } else {
        val parent =
            RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
        RetrofitClient.getApiHolder().getUserByParent(parent)
            .enqueue(object : Callback<RetrofitUser> {
                override fun onResponse(
                    call: Call<RetrofitUser>,
                    response: Response<RetrofitUser>
                ) {
                    if (response.code() == Constants.code_OK) {
                        users = response.body()!!.data
                        userAdapter.update(users)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Toast.makeText(
                            applicationContext,
                            "No User Present Under You!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("TAG", "getUserbyParent:" + response.code())
                    } else {
                        Log.d("TAG", "getUserbyParent:" + response.code())
                    }
                }

                override fun onFailure(call: Call<RetrofitUser>, t: Throwable) {
                    Log.d("TAG", "getUserbyParent:" + t.localizedMessage)
                }
            })
//        }
    }

    override fun itemClick(user: User) {
        val intent = Intent(applicationContext, UsersList::class.java)
        intent.putExtra("userId", user.user_id)
        intent.putExtra("userType", Constants.RETAILER)
        intent.putExtra("heading", "My Earnings")
        startActivity(intent)

//        val user_id = RequestBody.create(MediaType.parse("text/plain"), user.user_id)
//        RetrofitClient.getApiHolder().getOrderByUser(user_id)
//            .enqueue(object : Callback<RetrofitOrder> {
//                override fun onResponse(
//                    call: Call<RetrofitOrder>,
//                    response: Response<RetrofitOrder>
//                ) {
//                    if (response.code() == Constants.code_OK) {
//                        orders = response.body()!!.data
//                        binding.recyclerView.adapter = orderAdapter
//                        orderAdapter.update(orders)
//                    } else if (response.code() == Constants.code_NO_CONTENT) {
//                        Toast.makeText(applicationContext, "no orders found!", Toast.LENGTH_SHORT)
//                            .show()
//                    } else {
//                        Log.d("TAG", "getOrderByUser:" + response.code())
//                    }
//                }
//
//                override fun onFailure(call: Call<RetrofitOrder>, t: Throwable) {
//                    Log.d("TAG", "getOrderByUser" + t.localizedMessage)
//                }
//            })
    }

    override fun blockUser(user: User) {

    }

    override fun itemClick(order: Order) {
//        val order_id=RequestBody.create(MediaType.parse("text/plain"),order.order_id)
//        RetrofitClient.getApiHolder().getOrderItems(order_id).enqueue(object:Callback<RetrofitOrderItem>{
//            override fun onResponse(call: Call<RetrofitOrderItem>, response: Response<RetrofitOrderItem>) {
//                if(response.code()==Constants.code_OK){
//                    orderItems=response.body()!!.data
//                    binding.recyclerView.adapter=orderItemAdapter
//                    orderItemAdapter.update(orderItems)
//                }else if(response.code()==Constants.code_NO_CONTENT){
//                    Toast.makeText(applicationContext,"no items found!",Toast.LENGTH_SHORT).show()
//                }else{
//                    Log.d("TAG","getOrderItems:"+response.code())
//                }
//            }
//            override fun onFailure(call: Call<RetrofitOrderItem>, t: Throwable) {
//                Log.d("TAG","getOrderItems:"+t.localizedMessage)
//            }
//        })
        val gson = Gson()
        val intent = Intent(applicationContext, UsersList::class.java)
        intent.putExtra("order", gson.toJson(order))
        intent.putExtra("command", Constants.COMISSION)
        startActivity(intent)
    }

    override fun itemClick(itemId: String) {

    }
}