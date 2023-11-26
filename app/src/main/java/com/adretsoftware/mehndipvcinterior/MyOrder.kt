package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.OrderAdapter
import com.adretsoftware.mehndipvcinterior.adapters.orderFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMyOrdersBinding

import com.adretsoftware.mehndipvcinterior.models.Order
import com.adretsoftware.mehndipvcinterior.models.RetrofitOrder
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyOrder : AppCompatActivity(),orderFunctions {
    lateinit var binding: ActivityMyOrdersBinding
    lateinit var adapter:OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor=getColor(R.color.sixty1)

        adapter=OrderAdapter(this,layoutInflater,this)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.adapter=adapter



        val user_id= RequestBody.create(MediaType.parse("text/plain"),MySharedStorage.getUserId())
        RetrofitClient.getApiHolder().getOrderByUser(user_id).enqueue(object:Callback<RetrofitOrder>{
            override fun onResponse(call: Call<RetrofitOrder>, response: Response<RetrofitOrder>) {
                if(response.code()==Constants.code_OK){
                    Log.d("TAG","getOrderByUser:"+response.code().toString())
                    adapter.update(response.body()!!.data)
                }else if(response.code()==Constants.code_NO_CONTENT){
                    Log.d("TAG","getOrderByUser:"+response.code().toString())
                    binding.empty.visibility= View.VISIBLE
                }
            }

            override fun onFailure(call: Call<RetrofitOrder>, t: Throwable) {
                Log.d("TAG","getOrderByUser:"+t.localizedMessage)
            }
        })
    }

    override fun itemClick(order: Order) {
        val gson = Gson()
        val intent = Intent(applicationContext, OrderDetail::class.java)
        intent.putExtra("order", gson.toJson(order))
        startActivity(intent)
    }
}