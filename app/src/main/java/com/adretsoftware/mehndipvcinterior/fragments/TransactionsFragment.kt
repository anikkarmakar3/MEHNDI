package com.adretsoftware.mehndipvcinterior.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.TransactionFunctions
import com.adretsoftware.mehndipvcinterior.adapters.TransactionsAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.FragmentTransactionsBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.adretsoftware.mehndipvcinterior.models.RetrofitTransaction
import com.adretsoftware.mehndipvcinterior.models.TransactionModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionsFragment(private val activityContext: Activity, private val userId: String) :
    Fragment(), TransactionFunctions {

    lateinit var binding: FragmentTransactionsBinding
    lateinit var adapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        adapter = TransactionsAdapter(this, layoutInflater, activityContext, Constants.COMISSION)
        binding.recyclerView.layoutManager = LinearLayoutManager(activityContext)
        binding.recyclerView.adapter = adapter

        getTransactionsData()

        binding.typeTv.setOnClickListener {
            val builder = AlertDialog.Builder(activityContext)
            builder.setTitle("Select transaction type")
                .setPositiveButton(
                    "Purchased"
                ) { _, _ -> binding.typeTv.setText("Purchased") }
                .setNegativeButton(
                    "Received"
                ) { _, _ -> binding.typeTv.setText("Received") }.show()
        }

        binding.btnAddTransaction.setOnClickListener {
            if (binding.amountTv.text.toString().isEmpty()) {
                binding.amountTv.error = "Enter the amount"
                binding.amountTv.requestFocus()
            } else if (binding.typeTv.text.toString().isEmpty()) {
                Toast.makeText(activityContext, "Select the transaction type", Toast.LENGTH_SHORT)
                    .show()
            } else {
                hideKeyboard(activityContext)

                val transactionModel = TransactionModel(
                    MySharedStorage.getUserId(),
                    userId,
                    binding.typeTv.text.toString(),
                    binding.amountTv.text.toString(),
                    binding.noteTv.text.toString()
                )

                val progressDialog = ProgressDialog(activityContext)
                progressDialog.show()

                RetrofitClient.getApiHolder().sendTransactionReport(transactionModel)
                    .enqueue(object : Callback<RetrofitResponse> {
                        override fun onResponse(
                            call: Call<RetrofitResponse>,
                            response: Response<RetrofitResponse>
                        ) {
                            binding.amountTv.setText("")
                            binding.noteTv.setText("")
                            binding.typeTv.setText("")

                            progressDialog.dismiss()
                            getTransactionsData()
                        }

                        override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                            Log.d("TAG", "getOrder:" + t.localizedMessage)
                        }
                    })
            }
        }

        return binding.root
    }

    private fun getTransactionsData() {
        RetrofitClient.getApiHolder().getTransactionReport(Utilis.getUserId(), userId)
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


fun hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val currentFocusedView = activity.currentFocus
    if (currentFocusedView != null) {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocusedView.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}