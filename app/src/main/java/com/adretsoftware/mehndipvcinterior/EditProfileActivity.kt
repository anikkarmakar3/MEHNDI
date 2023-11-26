package com.adretsoftware.mehndipvcinterior

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityEditProfileBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.adretsoftware.mehndipvcinterior.models.User
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
        loadDataOfPreviousUser()

        binding.create.setOnClickListener(View.OnClickListener {
            val name = binding.name.text.toString();
            val email = binding.email.text.toString();
            val number = binding.mobile.text.toString()
            val address = binding.address.text.toString()
            val password = binding.password.text.toString()

            if (name.isBlank() || email.isBlank() || number.isBlank() || password.isBlank())
                Toast.makeText(applicationContext, "fill all fields first!", Toast.LENGTH_SHORT)
                    .show()
            else {
                user.let {
                    it.name = name;
                    it.email = email;
                    it.mobile = number;
                    it.address = address;
                    it.password = password;

                    update(user)
                }
            }
        })
    }

    fun update(user: User) {
        RetrofitClient.getApiHolder().updateSingleUser(user)
            .enqueue(object : Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    if (response.code() == Constants.code_CREATED) {
                        Toast.makeText(
                            applicationContext,
                            "Profile saved successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        MySharedStorage.saveUser(user)
                        Log.d("UPDATE_PROFILE ", response.code().toString())
                        finish()
                    } else {
                        Log.d(
                            "UPDATE_PROFILE ",
                            response.code().toString() + response.message().toString()
                        )
                    }
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    Log.d("UPDATE_PROFILE ", " updateUser : ${t.localizedMessage}")
                }
            })
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataOfPreviousUser() {
        binding.apply {
            mobile.setText(user.mobile)
            name.setText(user.name)
            address.setText(user.address)
            email.setText(user.email)
            password.setText(user.password)
        }
    }
}