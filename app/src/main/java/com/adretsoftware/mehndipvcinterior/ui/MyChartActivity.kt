package com.adretsoftware.mehndipvcinterior.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.adapters.MyChartAdapter
import com.adretsoftware.mehndipvcinterior.adapters.MyTeamAdapter
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMyChartBinding
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMyTeamBinding
import com.adretsoftware.mehndipvcinterior.models.MyChartModel
import com.adretsoftware.mehndipvcinterior.models.MyTeamAdapterItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyChartBinding
    private lateinit var adapter: MyChartAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyChartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MyChartAdapter(this)
        getMyChartList()
    }

    private fun getMyChartList() {

        RetrofitClient.getApiHolder().getRedChart()
            .enqueue(object : Callback<MyChartModel> {
                override fun onResponse(
                    call: Call<MyChartModel>,
                    response: Response<MyChartModel>
                ) {
                    if (response.isSuccessful) {
                        binding.myChartRecylerview.layoutManager = LinearLayoutManager(applicationContext)
                        adapter.updateTeamMeambers(response.body()!!)
                        binding.myChartRecylerview.adapter = adapter
                    }
                }
                override fun onFailure(call: Call<MyChartModel>, t: Throwable) {
                    Toast.makeText(applicationContext,"Data is empty", Toast.LENGTH_LONG).show()
                }

            })
    }
}