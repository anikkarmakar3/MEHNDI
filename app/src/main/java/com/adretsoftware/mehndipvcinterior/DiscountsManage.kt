package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.ItemAdapter
import com.adretsoftware.mehndipvcinterior.adapters.OfferItemClick
import com.adretsoftware.mehndipvcinterior.adapters.OfferProductItemAdapter
import com.adretsoftware.mehndipvcinterior.adapters.itemFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityDiscountsManageBinding
import com.adretsoftware.mehndipvcinterior.databinding.DiscountOptionsFragviewBinding
import com.adretsoftware.mehndipvcinterior.models.*
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscountsManage : AppCompatActivity(), OfferItemClick {
    lateinit var binding: ActivityDiscountsManageBinding
    lateinit var adapter: OfferProductItemAdapter
    private var isCategory: Boolean = true

    override fun onBackPressed() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscountsManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.sixty1)

        adapter = OfferProductItemAdapter(
            this,
            this
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        /*binding.recyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)*/

        RetrofitClient.getApiHolder().getOfferItems().enqueue(object : Callback<ArrayList<ProductOfferModelItem>> {
            override fun onResponse(
                call: Call<ArrayList<ProductOfferModelItem>>,
                response: Response<ArrayList<ProductOfferModelItem>>
            ) {
                if (response.isSuccessful){
                    adapter.dispatchDataToAdapter(response.body()!!)
                }
            }

            override fun onFailure(call: Call<ArrayList<ProductOfferModelItem>>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

        /*RetrofitClient.getApiHolder().getOfferItems(MySharedStorage.getUserId())
            .enqueue(object : Callback<RetrofitItem> {
                override fun onResponse(
                    call: Call<RetrofitItem>,
                    response: Response<RetrofitItem>
                ) {
                    if (response.code() == Constants.code_OK) {
                        if (response.body()!!.data.isEmpty()) {
                            Toast.makeText(
                                applicationContext,
                                "No items present!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            isCategory = false
                            adapter.update(response.body()!!.data)
                        }
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Toast.makeText(
                            applicationContext,
                            "No Items present!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.d("TAG", "getItembyParent:" + response.code().toString())
                }

                override fun onFailure(call: Call<RetrofitItem>, t: Throwable) {
                    Log.d("TAG", "getItembyParent:" + t.localizedMessage)
                }
            })*/
    }

    /*override fun ItemClickFunc(item: Item, view: View) {
        val gson = Gson()
        val intent = Intent(applicationContext, ItemDetail::class.java)
        intent.putExtra("item", gson.toJson(item))
        startActivity(intent)

        if (!isCategory) {
//            val dialogBuilder = AlertDialog.Builder(this)
//            val viewBinding = DiscountOptionsFragviewBinding.inflate(layoutInflater)
//            dialogBuilder.setView(viewBinding.root)
//            val dialog = dialogBuilder.create()
//            dialog.show()
//
//            if (MySharedStorage.getUserId().isNotBlank()) {
//                getDiscount(MySharedStorage.getUserId(), item.item_id, viewBinding, dialog, item)
//            }
        } else {
//            if (!Utilis.isLoginAsAdmin()) {
//            val parent = RequestBody.create(MediaType.parse("text/plain"), item.item_id)
//            val user_id =
//                RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())

//            RetrofitClient.getApiHolder().getOfferItems(parent)
//                .enqueue(object : Callback<RetrofitItem> {
//                    override fun onResponse(
//                        call: Call<RetrofitItem>,
//                        response: Response<RetrofitItem>
//                    ) {
//                        if (response.code() == Constants.code_OK) {
//                            if (response.body()!!.data.isEmpty()) {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "no items present!",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            } else {
//                                isCategory = false
//                                adapter.update(response.body()!!.data)
//                            }
//                        } else if (response.code() == Constants.code_NO_CONTENT) {
//                            Toast.makeText(
//                                applicationContext,
//                                "no items present!",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                        Log.d("TAG", "getItembyParent:" + response.code().toString())
//                    }
//
//                    override fun onFailure(call: Call<RetrofitItem>, t: Throwable) {
//                        Log.d("TAG", "getItembyParent:" + t.localizedMessage)
//                    }
//                })
//            } else {
//                val parent = RequestBody.create(MediaType.parse("text/plain"), item.item_id)
//                val user_id =
//                    RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
//
//                RetrofitClient.getApiHolder().getItemsByParent(parent, user_id)
//                    .enqueue(object : Callback<RetrofitItem> {
//                        override fun onResponse(
//                            call: Call<RetrofitItem>,
//                            response: Response<RetrofitItem>
//                        ) {
//                            if (response.code() == Constants.code_OK) {
//                                isCategory = false
//                                adapter.update(response.body()!!.data)
//                            } else if (response.code() == Constants.code_NO_CONTENT) {
//                                Toast.makeText(
//                                    applicationContext,
//                                    "no items present!",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            Log.d("TAG", "getItembyParent:" + response.code().toString())
//                        }
//
//                        override fun onFailure(call: Call<RetrofitItem>, t: Throwable) {
//                            Log.d("TAG", "getItembyParent:" + t.localizedMessage)
//                        }
//                    })
//            }
        }
    }*/

    /*override fun LongItemClick(item: Item, view: View) {

    }*/

    fun getDiscount(
        userid: String,
        itemid: String,
        viewBinding: DiscountOptionsFragviewBinding,
        dialog: AlertDialog,
        item: Item,
    ) {
        val item_id = RequestBody.create(MediaType.parse("text/plain"), itemid)
        val user_id = RequestBody.create(MediaType.parse("text/plain"), userid)

        RetrofitClient.getApiHolder().getDiscountOfUser(user_id, item_id).enqueue(object :
            Callback<RetrofitDiscount> {
            override fun onResponse(
                call: Call<RetrofitDiscount>,
                response: Response<RetrofitDiscount>
            ) {
                if (response.code() == Constants.code_OK) {
                    viewBinding.amount.setText(response.body()!!.data[0].amount)
                    if (response.body()!!.data[0].discount_type == Constants.PERCENTAGE)
                        viewBinding.radiopercent.isChecked = true
                    else
                        viewBinding.radioprice.isChecked = true
                } else if (response.code() == Constants.code_NO_CONTENT) {
                } else {
                    Log.d("TAG", "getdiscountByUser:" + response.code().toString())
                }
            }

            override fun onFailure(call: Call<RetrofitDiscount>, t: Throwable) {
                Log.d("TAG", "getDiscountbyUser:" + t.localizedMessage)
            }
        })

        viewBinding.save.setOnClickListener(View.OnClickListener {
            var type = ""
            if (viewBinding.radiogroup.checkedRadioButtonId == viewBinding.radiopercent.id)
                type = Constants.PERCENTAGE
            else if (viewBinding.radiogroup.checkedRadioButtonId == viewBinding.radioprice.id)
                type = Constants.PRICE
            else {
                Toast.makeText(applicationContext, "choose type first", Toast.LENGTH_SHORT).show()
            }

            val price = viewBinding.amount.text.toString()
            if (price.isNullOrBlank())
                Toast.makeText(applicationContext, "Enter Price First", Toast.LENGTH_SHORT).show()
            else {
                val discount = Discount()
                discount.discount_type = type
                discount.user_id = userid
                discount.item_id = itemid
                discount.amount = price
                RetrofitClient.getApiHolder().setDiscount(discount)
                    .enqueue(object : Callback<RetrofitResponse> {
                        override fun onResponse(
                            call: Call<RetrofitResponse>,
                            response: Response<RetrofitResponse>
                        ) {
                            if (response.code() == Constants.code_CREATED) {
                                Toast.makeText(applicationContext, "saved", Toast.LENGTH_SHORT)
                                    .show()
                                dialog.dismiss()
                            }
                            Log.d("TAG", "setDiscount:" + response.code().toString())
                        }

                        override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                            Log.d("TAG", "setDiscount:" + t.localizedMessage)
                        }
                    })

            }
        })
    }

    override fun onProductClick(productOfferModelItem: ProductOfferModelItem) {

    }

}