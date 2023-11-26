package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.models.RetrofitJsonModel
import com.adretsoftware.mehndipvcinterior.models.RetrofitUser
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashScreen : AppCompatActivity() {

    private var PERMISSION_REQUEST_CODE = 200

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now access the location.
            } else {
                // Permission denied, handle this case (e.g., show a message or request again).
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.statusBarColor = getColor(R.color.white)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        actionBar?.hide()
        MySharedStorage.func(applicationContext)

        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            // You already have permission. Do your location-related task here.
        } else {
            // You don't have permission, so request it.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        }

        if (MySharedStorage.getUserId().isBlank()) {
            Handler().postDelayed({
                startActivity(Intent(applicationContext, Login::class.java))
                finish()
            }, 7300)
        } else {
            Handler().postDelayed({
                Volley.newRequestQueue(applicationContext).add(
                    StringRequest(Request.Method.GET,
                        Constants.apiUrl + "check_app_update.php",
                        { response ->
                            val responseData = JSONObject(response)
                            val jsonObject = responseData.getJSONObject("data")

                            if (jsonObject.getString("app_version")
                                    .equals(BuildConfig.VERSION_NAME)
                            ) {
                                RetrofitClient.getApiHolder()
                                    .isUserActive(MySharedStorage.getUserId())
                                    .enqueue(object : Callback<RetrofitUser> {
                                        override fun onResponse(
                                            call: Call<RetrofitUser>,
                                            response: Response<RetrofitUser>
                                        ) {
                                            if (response.code() == Constants.code_OK) {
                                                val fetchedUser = response.body()!!.data[0]
                                                if (fetchedUser.status == "Active") {
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
                                                    MySharedStorage.Logout()
                                                    startActivity(
                                                        Intent(
                                                            applicationContext,
                                                            Login::class.java
                                                        )
                                                    )
                                                    finish()
                                                }
                                            }
                                        }

                                        override fun onFailure(
                                            call: Call<RetrofitUser>,
                                            t: Throwable
                                        ) {
                                            Log.d(
                                                "TAG",
                                                "getUserByMobile:" + t.localizedMessage
                                            )
                                        }
                                    })
                            } else {
                                val downloadLink = jsonObject.getString("download_link")
                                startActivity(
                                    Intent(
                                        this@SplashScreen,
                                        ActAppUpdate::class.java
                                    ).putExtra("downloadLink", downloadLink)
                                )
                                finishAffinity()
                            }
                        },
                        { })
                )

//                RetrofitClient.getApiHolder().checkAppUpdate()
//                    .enqueue(object : Callback<RetrofitJsonModel> {
//                        override fun onResponse(
//                            call: Call<RetrofitJsonModel>,
//                            response: Response<RetrofitJsonModel>
//                        ) {
//                            if (response.code() == Constants.code_OK) {
//                                try {
//                                    val jsonObject =
//                                        JSONObject(Gson().toJson(response.body()?.data))
//
//                                    println(jsonObject.getString("app_version"))
//                                    println(BuildConfig.VERSION_NAME)
//
//                                    if (jsonObject.getString("app_version")
//                                            .equals(BuildConfig.VERSION_NAME)
//                                    ) {
//                                        RetrofitClient.getApiHolder()
//                                            .isUserActive(MySharedStorage.getUserId())
//                                            .enqueue(object : Callback<RetrofitUser> {
//                                                override fun onResponse(
//                                                    call: Call<RetrofitUser>,
//                                                    response: Response<RetrofitUser>
//                                                ) {
//                                                    if (response.code() == Constants.code_OK) {
//                                                        val fetchedUser = response.body()!!.data[0]
//                                                        if (fetchedUser.status == "Active") {
//                                                            startActivity(
//                                                                Intent(
//                                                                    applicationContext,
//                                                                    MainActivity::class.java
//                                                                )
//                                                            )
//                                                            finish()
//                                                        } else {
//                                                            Toast.makeText(
//                                                                applicationContext,
//                                                                "This account is blocked",
//                                                                Toast.LENGTH_SHORT
//                                                            ).show()
//                                                            MySharedStorage.Logout()
//                                                            startActivity(
//                                                                Intent(
//                                                                    applicationContext,
//                                                                    Login::class.java
//                                                                )
//                                                            )
//                                                            finish()
//                                                        }
//                                                    }
//                                                }
//
//                                                override fun onFailure(
//                                                    call: Call<RetrofitUser>,
//                                                    t: Throwable
//                                                ) {
//                                                    Log.d(
//                                                        "TAG",
//                                                        "getUserByMobile:" + t.localizedMessage
//                                                    )
//                                                }
//                                            })
//                                    } else {
//                                        val downloadLink = jsonObject.getString("download_link")
//                                        startActivity(
//                                            Intent(
//                                                this@SplashScreen,
//                                                ActAppUpdate::class.java
//                                            ).putExtra("downloadLink", downloadLink)
//                                        )
//                                        finishAffinity()
//                                    }
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                            }
//                        }
//
//                        override fun onFailure(call: Call<RetrofitJsonModel>, t: Throwable) {
//                            Log.d("TAG", "getUserByMobile:" + t.localizedMessage)
//                        }
//                    })
            }, 7300)
        }
    }
}