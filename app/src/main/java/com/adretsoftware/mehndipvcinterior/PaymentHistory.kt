package com.adretsoftware.mehndipvcinterior

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.TransactionFunctions
import com.adretsoftware.mehndipvcinterior.adapters.TransactionsAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityPaymentHistoryBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitTransaction
import com.adretsoftware.mehndipvcinterior.models.TransactionModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentHistory : AppCompatActivity(), TransactionFunctions {

    lateinit var binding: ActivityPaymentHistoryBinding
    lateinit var adapter: TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TransactionsAdapter(this, layoutInflater, applicationContext, Constants.COMISSION)
        binding.recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.adapter = adapter

        getTransactionsData()
    }

    private fun getTransactionsData() {
//        var userId = if (Utilis.isLoginAsAdmin()) {
//            ""
//        } else {
//            MySharedStorage.getUserId()
//        }
//
//        var forUser = ""
//        if (MySharedStorage.getUserType() == Constants.RETAILER) {
//            userId = ""
//            forUser = MySharedStorage.getUserId()
//        }

        var userId = if (Utilis.isLoginAsAdmin()) {
            ""
        } else {
            MySharedStorage.getUserId()
        }

        var forUser = ""
        if (!Utilis.isLoginAsAdmin()) {
            userId = ""
            forUser = MySharedStorage.getUserId()
        }

        RetrofitClient.getApiHolder().getTransactionById(userId, forUser)
            .enqueue(object : Callback<RetrofitTransaction> {
                override fun onResponse(
                    call: Call<RetrofitTransaction>,
                    response: Response<RetrofitTransaction>
                ) {
                    if (response.code() == Constants.code_OK) {
                        Log.d("TAG", "getOrder:" + response.code().toString())
                        adapter.update(response.body()!!.data)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Log.d("TAG", "getOrder:" + response.code().toString())
                        Toast.makeText(applicationContext, "No data found", Toast.LENGTH_SHORT)
                            .show()
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