package com.adretsoftware.mehndipvcinterior

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.*
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMyComissionsBinding
import com.adretsoftware.mehndipvcinterior.models.*
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyComissions : AppCompatActivity(), userFunctions, orderItemFunctions, orderFunctions,Clicklistner {
    lateinit var binding: ActivityMyComissionsBinding

    //    lateinit var userAdapter: UserAdapter
    lateinit var myCommissionAdapter: MyCommissionAdapter
    lateinit var orderAdapter: OrderAdapter
    lateinit var orderItemAdapter: OrderItemAdapter
    lateinit var users: ArrayList<User>
    lateinit var myCommission: ArrayList<MyCommissionModel>
    lateinit var orders: ArrayList<Order>
    lateinit var orderItems: ArrayList<OrderItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyComissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.sixty1)

        myCommissionAdapter = MyCommissionAdapter(this)
//        userAdapter = UserAdapter(this, "")
        /*orderAdapter = OrderAdapter(this, layoutInflater, this)
        orderItemAdapter = OrderItemAdapter(this, Constants.COMISSION)*/
//        binding.recyclerView.adapter = userAdapter
        binding.recyclerView.adapter = myCommissionAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val params: MutableMap<String, String> = HashMap()

        params["id"] = MySharedStorage.getId()
        params["user_id"] = MySharedStorage.getUserId()
        /*params["id"] = "23"
        params["user_id"] = "1686380538723"*/

        RetrofitClient.getApiHolder().getNewAgentUserCommission(params)
            .enqueue(object : Callback<ArrayList<NewCommisionModelItem>> {
                override fun onResponse(
                    call: Call<ArrayList<NewCommisionModelItem>>,
                    response: Response<ArrayList<NewCommisionModelItem>>
                ) {
                    if (response.isSuccessful){
                        myCommissionAdapter.update(response.body()!! )
                    }
                }

                override fun onFailure(call: Call<ArrayList<NewCommisionModelItem>>, t: Throwable) {
                    Toast.makeText(applicationContext,"data is empty",Toast.LENGTH_SHORT).show()
                }

            })

        /*if (MySharedStorage.getUserType() == Constants.AGENT) {
            binding.btnMyDistributors.visibility = View.VISIBLE
            binding.btnMyDistributors.setOnClickListener {
                startActivity(Intent(this@MyComissions, MyDistributor::class.java))
            }
        }*/

        /*RetrofitClient.getApiHolder().getUserCommission(MySharedStorage.getUserId())
            .enqueue(object : Callback<RetrofitMyCommission> {
                override fun onResponse(
                    call: Call<RetrofitMyCommission>,
                    response: Response<RetrofitMyCommission>
                ) {
                    if (response.code() == Constants.code_OK) {
                        myCommission = response.body()!!.data
                        myCommissionAdapter.update(myCommission)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Toast.makeText(
                            applicationContext,
                            "No Data Found!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("TAG", "getUserbyParent:" + response.code())
                    } else {
                        Log.d("TAG", "getUserbyParent:" + response.code())
                    }
                }

                override fun onFailure(call: Call<RetrofitMyCommission>, t: Throwable) {
                    Log.d("TAG", "getUserbyParent:" + t.localizedMessage)
                }
            })*/

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
//            val parent =
//                RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
//            RetrofitClient.getApiHolder().getUserByParent(parent)
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
        try {
            var download= applicationContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            var PdfUri = Uri.parse(Constants.apiUrl2 + itemId)
            var getPdf = DownloadManager.Request(PdfUri)
            getPdf.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            download.enqueue(getPdf)
            Toast.makeText(applicationContext,"Download Started", Toast.LENGTH_LONG).show()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
}