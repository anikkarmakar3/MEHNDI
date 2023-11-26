package com.adretsoftware.mehndipvcinterior.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.OrderDetail
import com.adretsoftware.mehndipvcinterior.adapters.OrderAdapter
import com.adretsoftware.mehndipvcinterior.adapters.orderFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.FragmentOrdersBinding
import com.adretsoftware.mehndipvcinterior.models.Order
import com.adretsoftware.mehndipvcinterior.models.RetrofitOrder
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersFragment(private val activityContext: Context, private val userId: String) : Fragment(),
    orderFunctions {
    lateinit var binding: FragmentOrdersBinding
    lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        adapter = OrderAdapter(this, layoutInflater, activityContext, Constants.COMISSION)
        binding.recyclerView.layoutManager = LinearLayoutManager(activityContext)
        binding.recyclerView.adapter = adapter

        val userId = RequestBody.create(MediaType.parse("text/plain"), userId)
        RetrofitClient.getApiHolder().getOrderByUser(userId)
            .enqueue(object : Callback<RetrofitOrder> {
                override fun onResponse(
                    call: Call<RetrofitOrder>,
                    response: Response<RetrofitOrder>
                ) {
                    if (response.code() == Constants.code_OK) {
                        Log.d("TAG", "getOrder:" + response.code().toString())
                        adapter.update(response.body()!!.data)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Log.d("TAG", "getOrder:" + response.code().toString())
                        binding.empty.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<RetrofitOrder>, t: Throwable) {
                    Log.d("TAG", "getOrder:" + t.localizedMessage)
                }
            })

        return binding.root
    }

    override fun itemClick(order: Order) {
        val gson = Gson()
        val intent = Intent(activityContext, OrderDetail::class.java)
        intent.putExtra("order", gson.toJson(order))
        startActivity(intent)
    }
}