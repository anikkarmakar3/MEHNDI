package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityLoginBinding
import com.adretsoftware.mehndipvcinterior.models.InvoiceData
import com.adretsoftware.mehndipvcinterior.models.OrderItem
import com.adretsoftware.mehndipvcinterior.models.RetrofitUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var mySharedStorage: MySharedStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MySharedStorage.func(this)
        window.statusBarColor = getColor(R.color.sixty1)

        binding.newProfile.setOnClickListener {
            startActivity(Intent(applicationContext, NewProfile::class.java))
        }
//        binding.superas.setOnClickListener {
//            startActivity(Intent(applicationContext,SuperLogin::class.java))
//        }

        binding.forgetPassword.setOnClickListener {
            startActivity(Intent(applicationContext, ForgotPassword::class.java))
        }


        Log.d("TAG", "ff" + MySharedStorage.getUserId())
        binding.signIn.setOnClickListener(View.OnClickListener {
            if (binding.id.text.isBlank() || binding.password.text.isBlank()) {
                Toast.makeText(applicationContext, "All Fields are Required!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val mobile = binding.id.text.toString()
                val password = binding.password.text.toString()
                val mob = RequestBody.create(MediaType.parse("text/plain"), mobile)
                RetrofitClient.getApiHolder().getUserByMobile(mob)
                    .enqueue(object : Callback<RetrofitUser> {
                        override fun onResponse(
                            call: Call<RetrofitUser>,
                            response: Response<RetrofitUser>
                        ) {

                            if (response.code() == Constants.code_OK) {
                                val fetchedUser = response.body()!!.data[0]
                                if (fetchedUser.password == password) {

                                    if (fetchedUser.status == "Active") {
                                        Toast.makeText(
                                            applicationContext,
                                            "Welcome!",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        MySharedStorage.setId(fetchedUser.id)
                                        MySharedStorage.setUserId(fetchedUser.user_id)
                                        MySharedStorage.setUserType(fetchedUser.user_type)
                                        MySharedStorage.setUserAdmin(fetchedUser.is_admin)

                                        Log.d("fetchedUser", "onResponse: "+MySharedStorage.getUserId())
                                        if (fetchedUser.user_type == Constants.MANUFACTURER)
                                            notificationTopicSubscribe()
                                        startActivity(
                                            Intent(
                                                applicationContext,
                                                MainActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            "This account is blocked",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "wrong password!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else if (response.code() == Constants.code_NO_CONTENT) {
                                Toast.makeText(
                                    applicationContext,
                                    "no user found!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                print("response" + response.errorBody())
                                print("response" + response.code())
                            }
                            print("response" + response.body())
                            print("response" + response.code())
                            Log.d("TAG", "getUserByMobile:" + response.code())

                        }

                        override fun onFailure(call: Call<RetrofitUser>, t: Throwable) {
                            Log.d("TAG", "getUserByMobile:" + t.localizedMessage)
                        }
                    })
            }

        })


        binding.invoice.setOnClickListener {

            val data = InvoiceData()
            data.apply {
                user.user_id = "24524675"
                user.name = "Maharaj Dey"
                user.email = "maharajdey23@gmail.com"
                user.mobile = "1234567890"
                user.address = "Madarat,Baruipur"
                order.date = "2023-10-4"
                order.order_id = "64226926262"
                order.price = "11000"
                val c = OrderItem().apply {
                    code = "ASD-32"
                    item_id = "465426262"
                    name = "Wardrobe"
                    quantity = "10"
                    price = "1000"
                    total_price = "10000"
                }
                items.add(c)
                items.add(c)
                items.add(c)
                items.add(c)
                items.add(c)
                items.add(c)

            }
            InvoiceGenerator(this, data)

        }

    }

    fun notificationTopicSubscribe() {
        //        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//            val token = task.result
//            RealtimeDatabaseDao.reference.setValue(token)
//            .addOnCompleteListener {
//                Log.d("TAG","data upload finished")
//            }.addOnFailureListener {
//                Log.d("TAG","realtime data upload failed: ${it.localizedMessage}")
//            }
//            Log.d("TAG", "got token: $token")
//        })
        Firebase.messaging.subscribeToTopic("OrderNotification")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                Log.d("TAG", msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
    }

}
