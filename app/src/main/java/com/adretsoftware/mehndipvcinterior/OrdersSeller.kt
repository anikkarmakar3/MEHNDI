package com.adretsoftware.mehndipvcinterior

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.OrderAdapter
import com.adretsoftware.mehndipvcinterior.adapters.orderFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityOrdersSellerBinding
import com.adretsoftware.mehndipvcinterior.models.Order
import com.adretsoftware.mehndipvcinterior.models.RetrofitOrder
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class OrdersSeller : AppCompatActivity(), orderFunctions {
    lateinit var binding: ActivityOrdersSellerBinding
    lateinit var adapter: OrderAdapter
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrdersSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.sixty1)

        adapter = OrderAdapter(this, layoutInflater, this, Constants.COMISSION)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        searchOrders("", "")

        binding.btnSearch.setOnClickListener {
            if (!binding.tvSelectDate.text.toString().isNullOrEmpty()
                && !binding.tvEndDate.text.toString().isNullOrEmpty()
            ) {
                searchOrders(
                    binding.tvSelectDate.text.toString(),
                    binding.tvEndDate.text.toString()
                )
            }
        }

        binding.tvSelectDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _: DatePicker, year: Int, month: Int, day: Int ->
                    val formattedDay = String.format("%02d", day)
                    val formattedMonth = String.format("%02d", month + 1)
                    val selectedDate = "$year-$formattedDay-$formattedMonth"
                    binding.tvSelectDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.tvEndDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _: DatePicker, year: Int, month: Int, day: Int ->
                    val formattedDay = String.format("%02d", day)
                    val formattedMonth = String.format("%02d", month + 1)
                    val selectedDate = "$year-$formattedDay-$formattedMonth"
                    binding.tvEndDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private lateinit var progressDialog: ProgressDialog
    private fun showLoading() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.show()
    }

    private fun closeLoading() {
        progressDialog.dismiss()
    }

    private fun searchOrders(date: String, endDate: String) {
        val userId = if (Utilis.isLoginAsAdmin()) {
            ""
        } else {
            MySharedStorage.getUserId()
        }

        binding.empty.visibility = View.GONE
        showLoading()
        RetrofitClient.getApiHolder().getOrder(date, endDate, userId).enqueue(object :
            Callback<RetrofitOrder> {
            override fun onResponse(call: Call<RetrofitOrder>, response: Response<RetrofitOrder>) {
                closeLoading()
                adapter.update(ArrayList())
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
    }

    override fun itemClick(order: Order) {
        val gson = Gson()
        val intent = Intent(applicationContext, OrderDetail::class.java)
        intent.putExtra("order", gson.toJson(order))
        startActivity(intent)
    }
}