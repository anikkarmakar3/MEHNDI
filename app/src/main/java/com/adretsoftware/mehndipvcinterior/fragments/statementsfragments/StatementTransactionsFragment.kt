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
import com.adretsoftware.mehndipvcinterior.adapters.TransactionFunctions
import com.adretsoftware.mehndipvcinterior.adapters.TransactionsAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.FragmentStatementTransactionsBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitTransaction
import com.adretsoftware.mehndipvcinterior.models.TransactionModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StatementTransactionsFragment(private val activityContext: Activity) : Fragment(),
    TransactionFunctions {

    lateinit var binding: FragmentStatementTransactionsBinding
    lateinit var adapter: TransactionsAdapter
    private val calendar = Calendar.getInstance()
    private var startDate = ""
    private var endDate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatementTransactionsBinding.inflate(layoutInflater, container, false)

        adapter = TransactionsAdapter(this, layoutInflater, activityContext, Constants.COMISSION)
        binding.recyclerView.layoutManager = LinearLayoutManager(activityContext)
        binding.recyclerView.adapter = adapter

        getTransactionsData("", "")

        binding.btnSearch.setOnClickListener {
            if (!binding.tvSelectDate.text.toString()
                    .isNullOrEmpty() && !binding.tvEndDate.text.toString().isNullOrEmpty()
            ) {
                getTransactionsData(
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
                    val selectedDate = "$year-$formattedMonth-$formattedDay"
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
                    val selectedDate = "$year-$formattedMonth-$formattedDay"
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
                    Uri.parse(Constants.apiUrl + "pdf/transaction_pdf.php?startDate=$startDate&endDate=$endDate")
                )
            )
        }

        return binding.root
    }

    private fun getTransactionsData(startDate: String, endDate: String) {
        RetrofitClient.getApiHolder().adminTransactionReport(startDate, endDate)
            .enqueue(object : Callback<RetrofitTransaction> {
                override fun onResponse(
                    call: Call<RetrofitTransaction>, response: Response<RetrofitTransaction>
                ) {
                    if (response.code() == Constants.code_OK) {
                        Log.d("TAG", "getOrder:" + response.code().toString())
                        adapter.update(response.body()!!.data)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Log.d("TAG", "getOrder:" + response.code().toString())
                        binding.empty.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<RetrofitTransaction>, t: Throwable) {
                    Log.d("TAG", "getOrder:" + t.localizedMessage)
                }
            })
    }

    override fun itemClick(transactionModel: TransactionModel) {

    }
}