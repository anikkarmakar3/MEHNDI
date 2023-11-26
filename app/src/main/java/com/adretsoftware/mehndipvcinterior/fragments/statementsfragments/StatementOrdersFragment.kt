package com.adretsoftware.mehndipvcinterior.fragments.statementsfragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.OrderDetail
import com.adretsoftware.mehndipvcinterior.adapters.OrderAdapter
import com.adretsoftware.mehndipvcinterior.adapters.orderFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.FragmentStatementOrdersBinding
import com.adretsoftware.mehndipvcinterior.models.Order
import com.adretsoftware.mehndipvcinterior.models.RetrofitOrder
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StatementOrdersFragment(private val activityContext: Activity) : Fragment(),
    orderFunctions {

    lateinit var binding: FragmentStatementOrdersBinding
    lateinit var adapter: OrderAdapter
    private val calendar = Calendar.getInstance()
    var startDate = ""
    var endDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatementOrdersBinding.inflate(layoutInflater, container, false)

        adapter = OrderAdapter(this, layoutInflater, activityContext, Constants.COMISSION)
        binding.recyclerView.layoutManager = LinearLayoutManager(activityContext)
        binding.recyclerView.adapter = adapter

        getOrdersData("", "")

        binding.btnSearch.setOnClickListener {
            if (!binding.tvSelectDate.text.toString()
                    .isNullOrEmpty() && !binding.tvEndDate.text.toString().isNullOrEmpty()
            ) {
                getOrdersData(
                    binding.tvSelectDate.text.toString(), binding.tvEndDate.text.toString()
                )
            }
        }

        binding.tvSelectDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                activityContext,
                { _: DatePicker, year: Int, month: Int, day: Int ->
                    val formattedDay = String.format("%02d", day)
                    val formattedMonth = String.format("%02d", month + 1)
                    val selectedDate = "$year-$formattedDay-$formattedMonth"
                    startDate = selectedDate
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
                activityContext,
                { _: DatePicker, year: Int, month: Int, day: Int ->
                    val formattedDay = String.format("%02d", day)
                    val formattedMonth = String.format("%02d", month + 1)
                    val selectedDate = "$year-$formattedDay-$formattedMonth"
                    endDate = selectedDate
                    binding.tvEndDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.btnDownloadPdf.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.apiUrl + "pdf/orders_pdf.php?startDate=$startDate&endDate=$endDate")
                )
            )
        }

        return binding.root
    }

    private fun getOrdersData(startDate: String, endDate: String) {
        RetrofitClient.getApiHolder().getOrder(startDate, endDate, "")
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
    }

    override fun itemClick(order: Order) {
        val gson = Gson()
        val intent = Intent(activityContext, OrderDetail::class.java)
        intent.putExtra("order", gson.toJson(order))
        startActivity(intent)
    }
}