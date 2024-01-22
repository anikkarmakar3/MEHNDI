package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityNewProfileBinding
import com.adretsoftware.mehndipvcinterior.databinding.SearchUserFragviewBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.adretsoftware.mehndipvcinterior.models.RetrofitUser
import com.adretsoftware.mehndipvcinterior.models.User
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class SuperA : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var accountType = Constants.DISTRIBUTER
    lateinit var parentUser: User
    lateinit var binding: ActivityNewProfileBinding
    lateinit var users: ArrayList<User>
    lateinit var user: User
    var isUpdate = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = User()
        users = arrayListOf<User>()

        binding = ActivityNewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.sixty1)
        loadParents()
        isUpdate = intent.getBooleanExtra("isUpdate", false)

        if (isUpdate) {
            user = Gson().fromJson(intent.getStringExtra("user"), User::class.java)
            loadDataOfPreviousUser()
        }
        val list = listOf<String>(
            Constants.DISTRIBUTER,
            Constants.AGENT,
            Constants.RETAILER,
            Constants.DEALER
        )
        val typeAdapter: ArrayAdapter<*>
        typeAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list)
        binding.spinnerAccountType.adapter = typeAdapter
        binding.spinnerAccountType.onItemSelectedListener = this
        binding.searchUserText.setOnClickListener(View.OnClickListener { funcUserSpin() })

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
                    it.user_type = accountType
                    it.name = name;
                    it.email = email;
                    it.mobile = number;
                    it.address = address;
                    it.password = password;

                    if (!isUpdate) {
                        it.user_id = Random.nextInt(10000, 100000).toString()
                        if (accountType != Constants.DISTRIBUTER) {
                            if (parentUser != null) {
                                it.parent = parentUser.user_id
                                createUser(user)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "select parent first!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            createUser(user)
                        }
                    } else {
                        update(user)
                    }
                }
            }
        })
    }

    private fun loadDataOfPreviousUser() {
        binding.apply {
            mobile.setText(user.mobile)
            name.setText(user.name)
            address.setText(user.address)
            email.setText(user.email)
            for (p in users) {
                if (p.user_id == user.parent) {
                    searchUserText.setText(p.name)
                    parentUser = p
                    break
                }
            }
            type.setText(user.user_type)
            binding.create.setText("Update")
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        accountType = p0?.selectedItem.toString()
        binding.type.setText(p0?.selectedItem.toString())

    }

    fun createUser(user: User) {
        RetrofitClient.getApiHolder().setUser(user).enqueue(object : Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                if (response.code() == Constants.code_CREATED) {
                    Log.d("TAG", response.code().toString())
                    Toast.makeText(applicationContext, "created! succesfully!", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(applicationContext, Users::class.java))
                    finish()
                } else {
                    Log.d("TAG 2", response.code().toString() + response.message().toString())
                }
            }

            override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                Log.d("TAG", "on failure retro : ${t.localizedMessage}")
            }
        })
    }

    fun update(user: User) {
        RetrofitClient.getApiHolder().updateUser(user).enqueue(object : Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                if (response.code() == Constants.code_CREATED) {
                    Log.d("TAG", response.code().toString())
                    Toast.makeText(applicationContext, "updated! succesfully!", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(applicationContext, Users::class.java))
                    finish()
                } else {
                    Log.d("TAG 2", response.code().toString() + response.message().toString())
                }
            }

            override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                Log.d("TAG", " updateUser : ${t.localizedMessage}")
            }
        })
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun funcUserSpin() {
        val dialogBuilder = AlertDialog.Builder(this)
        val viewBinding = SearchUserFragviewBinding.inflate(layoutInflater)
        dialogBuilder.setView(viewBinding.root)
        val dialog = dialogBuilder.create()
        dialog.show()
        val adapter = getUserAsAdapter()
        viewBinding.listView.adapter = adapter
        viewBinding.listView.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                binding.searchUserText.text = users[p2].name
                parentUser = users[p2]
                dialog.dismiss()
            }
        }
        viewBinding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(p0)
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    fun getUserAsAdapter(): ArrayAdapter<String> {
        var arr = ArrayList<String>()
        for (user in users) {
            arr.add("${user.name} ${user.email} ${user.mobile} ${user.address}")
        }
        val adapter =
            ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_1, arr)
        return adapter
    }

    fun loadParents() {
        RetrofitClient.getApiHolder().getUser().enqueue(object : Callback<RetrofitUser> {
            override fun onResponse(call: Call<RetrofitUser>, response: Response<RetrofitUser>) {
                if (response.code() == Constants.code_OK) {
                    Log.d("TAG", response.code().toString())
                    users = response.body()!!.data
                } else if (response.code() == Constants.code_NO_CONTENT) {

                } else {
                    Log.d("TAG 2", response.code().toString() + response.message().toString())
                }
            }

            override fun onFailure(call: Call<RetrofitUser>, t: Throwable) {
                Log.d("TAG", "on failure retro : ${t.localizedMessage}")

            }

        })
    }
}

