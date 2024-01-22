package com.adretsoftware.mehndipvcinterior

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityNewProfileBinding
import com.adretsoftware.mehndipvcinterior.databinding.SearchUserFragviewBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.adretsoftware.mehndipvcinterior.models.RetrofitUser
import com.adretsoftware.mehndipvcinterior.models.User
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Random

class NewProfile : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    var accountType = Constants.CHOOSE_ACCOUNT_TYPE
    var parentUser = User()
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
            Constants.CHOOSE_ACCOUNT_TYPE,
            Constants.AGENT,
            Constants.DISTRIBUTER,
            Constants.RETAILER,
            Constants.DEALER
        )

        val typeAdapter: ArrayAdapter<*>
        typeAdapter = ArrayAdapter<String>(this, R.layout.spinner_list, list)
        binding.spinnerAccountType.adapter = typeAdapter
        binding.spinnerAccountType.onItemSelectedListener = this
        binding.searchUserText.setOnClickListener(View.OnClickListener { funcUserSpin() })

        if (Utilis.isLoginAsAdmin()) {
            binding.layoutSelectParent.visibility = View.VISIBLE
        } else {
            binding.layoutSelectParent.visibility = View.GONE
        }

        binding.create.setOnClickListener(View.OnClickListener {
            val name = binding.name.text.toString();
            val email = binding.email.text.toString();
            val number = binding.mobile.text.toString()
            val address = binding.address.text.toString()
            val password = binding.password.text.toString()

            if (name.isBlank()
                || email.isBlank()
                || number.isBlank()
                || password.isBlank()
                || accountType == Constants.CHOOSE_ACCOUNT_TYPE
            )
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
                        it.user_id = kotlin.random.Random.nextInt(10000, 100000).toString()
                        if (accountType != Constants.DISTRIBUTER) {
                            it.parent = parentUser.user_id
                            createUser(user)
                        } else {
                            createUser(user)
                        }
                    } else {
                        it.parent = parentUser.user_id
                        update(user)
                    }
                }
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
            for (p in users) {
                if (p.user_id == user.parent) {
                    searchUserText.text = p.name
                    parentUser = p
                    break
                }
            }
            type.text = user.user_type
            binding.create.text = "Update"
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        accountType = p0?.selectedItem.toString()
        binding.type.text = p0?.selectedItem.toString()

    }

    fun createUser(user: User) {
        RetrofitClient.getApiHolder().setUser(user).enqueue(object : Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                if (response.code() == Constants.code_CREATED) {
                    Toast.makeText(
                        applicationContext,
                        "created account successfully Please Login! ",
                        Toast.LENGTH_LONG
                    ).show()

                    Intent(applicationContext, Login::class.java).apply {
                        this.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(this)
                        finish()
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        response.body()?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d(
                        "TAG 2",
                        (response.body()?.status ?: "code error - ") + (response.body()?.message
                            ?: " message error")
                    )
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
        viewBinding.listView.onItemClickListener =
            AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
                binding.searchUserText.text = users[p2].name
                parentUser = users[p2]
                dialog.dismiss()
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
        val arr = ArrayList<String>()
        for (user in users) {
            arr.add("${user.user_type}\n${user.name}\n${user.email}\n${user.mobile}\n${user.address}")
        }
        val adapter =
            ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, arr)
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
