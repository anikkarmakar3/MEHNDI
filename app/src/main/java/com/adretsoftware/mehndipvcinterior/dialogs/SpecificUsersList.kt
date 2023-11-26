package com.adretsoftware.mehndipvcinterior.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.UsersList
import com.adretsoftware.mehndipvcinterior.adapters.UserAdapter
import com.adretsoftware.mehndipvcinterior.adapters.userFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.DlgSpecificUsersListBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitUser
import com.adretsoftware.mehndipvcinterior.models.User
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SpecificUsersList : Dialog, userFunctions {

    private var userType: String = ""
    private var userId: String = ""

    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        themeResId: Int,
        userType: String,
        userId: String
    ) : super(context, themeResId) {
        this.userType = userType
        this.userId = userId
    }

    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    lateinit var binding: DlgSpecificUsersListBinding
    lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlgSpecificUsersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.heading.text = userType

        adapter = UserAdapter(this, "")
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        getData()
    }

    private fun getData() {
        val parentId = RequestBody.create(MediaType.parse("text/plain"), userId)
        val userType = RequestBody.create(MediaType.parse("text/plain"), userType)

        RetrofitClient.getApiHolder().getUsersList(parentId, userType)
            .enqueue(object : Callback<RetrofitUser> {
                override fun onResponse(
                    call: Call<RetrofitUser>,
                    response: Response<RetrofitUser>
                ) {
                    if (response.code() == Constants.code_OK) {
                        adapter.update(response.body()!!.data)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        dismiss()
                        Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.d("TAG", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<RetrofitUser>, t: Throwable) {
                    Log.d("TAG", t.localizedMessage)
                }
            })
    }

    override fun itemClick(user: User) {
        val userType = user.user_type
        val userId = user.user_id

        context.startActivity(
            Intent(context, UsersList::class.java).putExtra(
                "userType",
                userType
            ).putExtra("userId", userId)
        );
    }

    override fun blockUser(user: User) {

    }
}