package com.adretsoftware.mehndipvcinterior.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.adapters.MyTeamAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMyTeamBinding
import com.adretsoftware.mehndipvcinterior.models.MyTeamAdapterItem
import com.adretsoftware.mehndipvcinterior.models.RetrofitTransaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyTeam : AppCompatActivity() {

    private lateinit var binding: ActivityMyTeamBinding
    private lateinit var adapter: MyTeamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = MyTeamAdapter(this)
        getMyTeamList()
    }

    private fun getMyTeamList() {
        val params : HashMap<String,String> = HashMap()
        params["user id"] = MySharedStorage.getId()

        RetrofitClient.getApiHolder().getTeamMembers(params)
            .enqueue(object : Callback<List<MyTeamAdapterItem>> {
                override fun onResponse(
                    call: Call<List<MyTeamAdapterItem>>,
                    response: Response<List<MyTeamAdapterItem>>
                ) {
                    if (response.isSuccessful) {
                        binding.myTeamRecylerview.layoutManager = LinearLayoutManager(applicationContext)
                        adapter.updateTeamMeambers(response.body()!! as ArrayList<MyTeamAdapterItem>)
                        binding.myTeamRecylerview.adapter = adapter
                    }
                }

                override fun onFailure(call: Call<List<MyTeamAdapterItem>>, t: Throwable) {
                    Toast.makeText(applicationContext,"Data is empty",Toast.LENGTH_LONG).show()
                }

            })
    }

}