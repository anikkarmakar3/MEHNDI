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
import com.adretsoftware.mehndipvcinterior.databinding.SuperLoginBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitUser
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuperLogin : AppCompatActivity() {
    lateinit var binding:SuperLoginBinding
    lateinit var mySharedStorage:MySharedStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= SuperLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MySharedStorage.func(this)
        window.statusBarColor=getColor(R.color.sixty1)


        Log.d("TAG","ff"+MySharedStorage.getUserId())
        binding.signIn.setOnClickListener(View.OnClickListener {
            if(binding.id.text.isBlank() || binding.password.text.isBlank()){
                Toast.makeText(applicationContext,"fill all fields first",Toast.LENGTH_SHORT).show()
            }else{
                val mobile=binding.id.text.toString()
                val password=binding.password.text.toString()
                val mob= RequestBody.create(MediaType.parse("text/plain"),mobile)
                RetrofitClient.getApiHolder().getUserByMobile(mob).enqueue(object: Callback <RetrofitUser>{
                    override fun onResponse(call: Call<RetrofitUser>, response: Response<RetrofitUser>) {
                        if(response.code()==Constants.code_OK){
                            val fetchedUser=response.body()!!.data[0]
                            if(fetchedUser.password==password){
                                Toast.makeText(applicationContext,"welcome!",Toast.LENGTH_SHORT).show()
                                MySharedStorage.setUserId(fetchedUser.user_id)
                                MySharedStorage.setUserType(fetchedUser.user_type)
                                if(fetchedUser.user_type==Constants.MANUFACTURER)
                                startActivity(Intent(applicationContext,MainActivity::class.java))
                                finish()
                            }
                            else {
                                Toast.makeText(applicationContext,"wrong password!",Toast.LENGTH_SHORT).show()
                            }
                        }
                        else if(response.code()==Constants.code_NO_CONTENT){
                            Toast.makeText(applicationContext,"no user found!",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            print("response"+response.errorBody())
                            print("response"+response.code())
                        }
                        print("response"+response.body())
                        print("response"+response.code())
                        Log.d("TAG","getUserByMobile:"+response.code())

                    }
                    override fun onFailure(call: Call<RetrofitUser>, t: Throwable) {
                        Log.d("TAG","getUserByMobile:"+t.localizedMessage)
                    }
                })
            }

        })




    }


}
