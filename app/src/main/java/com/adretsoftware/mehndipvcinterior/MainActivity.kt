package com.adretsoftware.mehndipvcinterior
/*
Code By Abhikash babu
Conatact us *707360502 Whatspappe
 */
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.adretsoftware.mehndipvcinterior.adapters.SliderAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityMainBinding
import com.adretsoftware.mehndipvcinterior.databinding.ChangePasswordFragviewBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitImage
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.adretsoftware.mehndipvcinterior.models.RetrofitUser
import com.adretsoftware.mehndipvcinterior.ui.Picture_Product_List_Activity
import com.adretsoftware.mehndipvcinterior.ui.UserProfileActivity
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var user_type: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MySharedStorage.func(this)
        window.statusBarColor = getColor(R.color.sixty1)
        user_type = MySharedStorage.getUserType()
        saveUser()

        binding.offers.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    DiscountsManage::class.java
                )
            )
        })

        binding.cardPhotoGallery.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    GalleryCategoryPage::class.java
                )
            )
        }

//        if (user_type == Constants.DISTRIBUTER) {
//            binding.grid.removeView(binding.profits)
//        }

//        binding.grid.removeView(binding.manageProducts)
        if (user_type == Constants.MANUFACTURER) {
            binding.grid.removeView(binding.cart)
//            binding.grid.removeView(binding.manageProducts)
        } else {
            binding.grid.removeView(binding.btnUsers)
//            if (user_type == Constants.RETAILER) {
////                binding.grid.removeView(binding.profits)
//                binding.grid.removeView(binding.offers)
////                binding.grid.removeView(binding.manageProducts)
//            }
        }
        //  binding.changePassword.setOnClickListener { changePassword() }

        binding.myprofile.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java));
        }

        binding.myaccount.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, MyAccount::class.java));
        })

        binding.shop.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, Items::class.java))
        })

//        binding.manageProducts.setOnClickListener(View.OnClickListener {
//            startActivity(Intent(applicationContext, ManageProducts::class.java))
//        })
//        binding.newItem.setOnClickListener(View.OnClickListener {
//            startActivity(Intent(applicationContext, NewItem::class.java))
//        })
//        binding.manageUsers.setOnClickListener(View.OnClickListener {
//            startActivity(Intent(applicationContext,Users::class.java))
//        })
        binding.cart.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, Cart::class.java))
        })

        binding.btnUsers.setOnClickListener {
            val userType = if (Utilis.isLoginAsAdmin()) {
                Constants.MANUFACTURER
            } else {
                MySharedStorage.getUserType()
            }

            val userId = if (!Utilis.isLoginAsAdmin()) {
                MySharedStorage.getUserId()
            } else {
                ""
            }

            startActivity(
                Intent(applicationContext, UsersList::class.java).putExtra(
                    "userType",
                    userType
                ).putExtra("userId", userId)
            )
        }

        binding.profits.setOnClickListener(View.OnClickListener {
            startActivity(Intent(applicationContext, MyComissions::class.java))
        })

        // binding.manageOrders.setOnClickListener { startActivity(Intent(applicationContext,OrdersSeller::class.java)) }
        binding.orders.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    OrdersActivity::class.java
                )
            )
        }

        //   binding.logout.setOnClickListener { MySharedStorage.setUserId("");startActivity(Intent(applicationContext,Login::class.java)); finish() }

        var slideAdapter = SliderAdapter()
        binding.sliderView.setSliderAdapter(slideAdapter)
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
        binding.sliderView.startAutoCycle()

        RetrofitClient.getApiHolder().bannerImageDownload().enqueue(object :
            Callback<RetrofitImage> {
            override fun onResponse(call: Call<RetrofitImage>, response: Response<RetrofitImage>) {
                if (response.code() == Constants.code_OK)
                    slideAdapter.updateData(response.body()!!.data)
                else {
                    Log.d("TAG", response.code().toString())
                }
            }

            override fun onFailure(call: Call<RetrofitImage>, t: Throwable) {
                Log.d("TAG", t.localizedMessage)
            }
        })
    }

    private fun changePassword() {
        val dialogBuilder = AlertDialog.Builder(this)
        val viewBinding = ChangePasswordFragviewBinding.inflate(layoutInflater)
        dialogBuilder.setView(viewBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()
        viewBinding.save.setOnClickListener(View.OnClickListener {
            if (viewBinding.newPassword.text.isBlank())
                Toast.makeText(applicationContext, "enter new password", Toast.LENGTH_SHORT).show()
            else if (viewBinding.newPassword.text.length < 6)
                Toast.makeText(
                    applicationContext,
                    "atleast 6 character required!",
                    Toast.LENGTH_SHORT
                ).show()
            else {
                val user_id =
                    RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
                val password = RequestBody.create(
                    MediaType.parse("text/plain"),
                    viewBinding.newPassword.text.toString()
                )
                RetrofitClient.getApiHolder().updateUserPassword(user_id, password)
                    .enqueue(object : Callback<RetrofitResponse> {
                        override fun onResponse(
                            call: Call<RetrofitResponse>,
                            response: Response<RetrofitResponse>
                        ) {

                        }

                        override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {

                        }
                    })
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
        })
        viewBinding.cancel.setOnClickListener { dialog.dismiss() }
    }

    fun saveUser() {
        Log.d("TAG", "userid:+${MySharedStorage.getUserId()}")
        val id = RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
        RetrofitClient.getApiHolder().getUserById(id).enqueue(object : Callback<RetrofitUser> {
            override fun onResponse(call: Call<RetrofitUser>, response: Response<RetrofitUser>) {
                if (response.code() == Constants.code_OK)
                    MySharedStorage.saveUser(response.body()!!.data[0])
                Log.d("TAG", "getuserbyid:" + response.code().toString())
            }

            override fun onFailure(call: Call<RetrofitUser>, t: Throwable) {
                Log.d("TAG", "getuserbyid:" + t.localizedMessage)
            }
        })
    }
}
