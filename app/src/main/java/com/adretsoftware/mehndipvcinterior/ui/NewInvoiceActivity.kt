package com.adretsoftware.mehndipvcinterior.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.adapters.InvoiceItemAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityNewInvoiceBinding
import com.adretsoftware.mehndipvcinterior.models.InvoiceModel
import com.adretsoftware.mehndipvcinterior.models.RetrofitInvoicesItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Array

class NewInvoiceActivity : AppCompatActivity() {
    lateinit var binding: ActivityNewInvoiceBinding
    lateinit var adater: InvoiceItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adater = InvoiceItemAdapter(this)
        val params= hashMapOf<String,String>()
        params.put("phone",MySharedStorage.getUserr().mobile)
        RetrofitClient.getApiHolder().newGetUserInvoice(params)
            .enqueue(object : Callback<InvoiceModel> {
                override fun onResponse(
                    call: Call<InvoiceModel>,
                    response: Response<InvoiceModel>
                ) {
                    if (response.isSuccessful){
                        val dataInvoice = ArrayList<InvoiceModel>()
                        dataInvoice.add(response.body()!!)
                        println(response.body()!!)
                        binding.invoiceRecylerview.layoutManager = LinearLayoutManager(applicationContext)
                        adater.updateDataToAdapter(dataInvoice)
                        binding.invoiceRecylerview.adapter = adater
                    }
                }

                override fun onFailure(call: Call<InvoiceModel>, t: Throwable) {
                    println(t.localizedMessage)
                }

            })
    }
}