package com.adretsoftware.mehndipvcinterior

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.UserAdapter
import com.adretsoftware.mehndipvcinterior.adapters.userFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityUsersBinding
import com.adretsoftware.mehndipvcinterior.databinding.UserOptionsFragviewBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.adretsoftware.mehndipvcinterior.models.RetrofitUser
import com.adretsoftware.mehndipvcinterior.models.User
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Users : AppCompatActivity(), userFunctions {
    lateinit var binding: ActivityUsersBinding
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.sixty1)
        //  user= User()
        adapter = UserAdapter(this, "users")

        showLoading()
        getData()
        binding.addUserBtn.setOnClickListener(View.OnClickListener { addUser() })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun getData() {
        RetrofitClient.getApiHolder().getUser().enqueue(object : Callback<RetrofitUser> {
            override fun onResponse(call: Call<RetrofitUser>, response: Response<RetrofitUser>) {
                if (response.code() == Constants.code_OK) {
                    closeLoading()
                    adapter.update(response.body()!!.data)
                } else {
                    Log.d("TAG 2", response.code().toString() + response.message().toString())
                }
            }

            override fun onFailure(call: Call<RetrofitUser>, t: Throwable) {
                Log.d("TAG", "on failure retro : ${t.localizedMessage}")

            }

        })
    }

    fun addUser() {
        startActivity(Intent(applicationContext, NewProfile::class.java))
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

    override fun itemClick(user: User) {
        val dialogBuilder = AlertDialog.Builder(this)
        val viewBinding = UserOptionsFragviewBinding.inflate(layoutInflater)
        dialogBuilder.setView(viewBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()

        viewBinding.setDiscount.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, DiscountsManage::class.java))
        })

        viewBinding.updatePersonalData.setOnClickListener(View.OnClickListener {
            val gson = Gson()
            val intent = Intent(this, NewProfile::class.java)
            intent.putExtra("user", gson.toJson(user))
            intent.putExtra("isUpdate", true)
            startActivity(intent)
        })
    }

    override fun blockUser(user: User) {
        val updateStatus = if (user.status == "Active") {
            "Blocked"
        } else {
            "Active"
        }
        user.status = updateStatus

        showLoading()
        RetrofitClient.getApiHolder().updateUserStatus(user)
            .enqueue(object : Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    if (response.code() == Constants.code_CREATED) {
                        getData()
                    } else {
                        Log.d("TAG 2", response.code().toString() + response.message().toString())
                    }
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    Log.d("TAG", "on failure retro : ${t.localizedMessage}")

                }

            })
    }

}