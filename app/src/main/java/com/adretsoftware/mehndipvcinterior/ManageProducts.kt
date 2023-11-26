package com.adretsoftware.mehndipvcinterior

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.ManageProductAdapterFunctions
import com.adretsoftware.mehndipvcinterior.adapters.ManageProductsAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityManageProductsBinding
import com.adretsoftware.mehndipvcinterior.databinding.DlgManageProductItemBinding
import com.adretsoftware.mehndipvcinterior.fragments.hideKeyboard
import com.adretsoftware.mehndipvcinterior.models.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageProducts : AppCompatActivity(), ManageProductAdapterFunctions {
    lateinit var binding: ActivityManageProductsBinding
    lateinit var adapter: ManageProductsAdapter

    private var parentitemid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = getColor(R.color.sixty1)
        adapter = ManageProductsAdapter(
            this,
            layoutInflater,
            applicationContext,
            object : ManageProductsAdapter.ClickListener {
                override fun deleteItemClick(itemId: String) {
                    val builder = AlertDialog.Builder(this@ManageProducts)
                    builder.setTitle("Are you sure?")
                        .setMessage("Your going to delete this item this action cannot be undo.")
                        .setPositiveButton(
                            "DELETE"
                        ) { p0, p1 ->
                            deleteItem(ItemDelete(itemId))
                        }
                        .setNegativeButton("CANCEL", null)
                        .show()
                }
            },
            "product"
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        RetrofitClient.getApiHolder().getItems().enqueue(object : Callback<RetrofitItem> {
            override fun onResponse(call: Call<RetrofitItem>, response: Response<RetrofitItem>) {
                if (response.code() == Constants.code_OK) {
                    adapter.update(response.body()!!.data)
                } else {
                    Log.d("TAG 2", response.code().toString() + response.message().toString())
                }
            }

            override fun onFailure(call: Call<RetrofitItem>, t: Throwable) {
                Log.d("TAG", "on failure retro : ${t.localizedMessage}")

            }
        })
    }

    fun deleteItem(item: ItemDelete) {
        RetrofitClient.getApiHolder().deleteItem(item).enqueue(object :
            Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                Toast.makeText(applicationContext, "deleted!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, Items::class.java))
                finish()
            }

            override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                Log.d("TAG", "failed ${t.localizedMessage}")
            }
        })
    }

    override fun ItemClickFunc(item: Item, view: View) {
        if (parentitemid.isEmpty()) {
            parentitemid = item.item_id
        }

        if (item.price.isNullOrBlank()) {
            getItemsData(item)
        } else {
            val dialog = Dialog(this)
            val dlgManageProductItemBinding = DlgManageProductItemBinding.inflate(layoutInflater);
            dialog.setContentView(dlgManageProductItemBinding.root)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            getDiscount(
                MySharedStorage.getUserId(),
                item.item_id,
                dlgManageProductItemBinding,
                dialog,
                item
            )

            val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
            val btnSave = dialog.findViewById<Button>(R.id.btnSave)
            val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)

            if (item.status == Constants.Available) {
                radioGroup.check(R.id.radioAvailable)
            } else {
                radioGroup.check(R.id.radioNotAvailable)
            }

            etAmount.setText(item.price)
            btnSave.setOnClickListener {
                if (etAmount.text.toString().isNullOrEmpty()) {
                    Toast.makeText(this, "Enter amount to save", Toast.LENGTH_SHORT)
                        .show()
                    etAmount.requestFocus()
                } else {
                    dialog.dismiss()
                    showLoading()
                    hideKeyboard(this)

                    val status = if (radioGroup.checkedRadioButtonId == R.id.radioAvailable) {
                        Constants.Available
                    } else {
                        Constants.NotAvailable
                    }

                    val manageProductMode = ManageProductModel(
                        parent_id = parentitemid,
                        item_id = item.item_id,
                        price = etAmount.text.toString(),
                        status = status
                    )

                    RetrofitClient.getApiHolder().saveItem(manageProductMode)
                        .enqueue(object :
                            Callback<RetrofitResponse> {
                            override fun onResponse(
                                call: Call<RetrofitResponse>,
                                response: Response<RetrofitResponse>
                            ) {
                                closeLoading()
                                Toast.makeText(
                                    this@ManageProducts,
                                    "Product saved",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }

                            override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                                Log.d("TAG", "upload failed ${t.localizedMessage}")
                            }
                        })
                }
            }

            dialog.show()

//            val dialog = Dialog(this)
//            dialog.setContentView(R.layout.dlg_manage_product_item)
//            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
//            dialog.window?.setLayout(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            )
//
//            val etAmount = dialog.findViewById<EditText>(R.id.etAmount)
//            val btnSave = dialog.findViewById<Button>(R.id.btnSave)
//            val radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup)
//
//            if (item.user_set_status == Constants.Available) {
//                radioGroup.check(R.id.radioAvailable)
//            } else {
//                radioGroup.check(R.id.radioNotAvailable)
//            }
//
//            etAmount.setText(item.user_set_price)
//            btnSave.setOnClickListener {
//                if (etAmount.text.toString().isNullOrEmpty()) {
//                    Toast.makeText(this, "Enter your custom amount to save", Toast.LENGTH_SHORT)
//                        .show()
//                    etAmount.requestFocus()
//                } else {
//                    dialog.dismiss()
//                    showLoading()
//                    hideKeyboard(this)
//
//                    val status = if (radioGroup.checkedRadioButtonId == R.id.radioAvailable) {
//                        Constants.Available
//                    } else {
//                        Constants.NotAvailable
//                    }
//
//                    val manageProductMode = ManageProductModel(
//                        user_id = MySharedStorage.getUserId(),
//                        parent_id = parentitemid,
//                        item_id = item.item_id,
//                        price = etAmount.text.toString(),
//                        status = status
//                    )
//
//                    RetrofitClient.getApiHolder().saveManageProductItem(manageProductMode)
//                        .enqueue(object :
//                            Callback<RetrofitResponse> {
//                            override fun onResponse(
//                                call: Call<RetrofitResponse>,
//                                response: Response<RetrofitResponse>
//                            ) {
//                                closeLoading()
//                                Toast.makeText(
//                                    this@ManageProducts,
//                                    "Product saved",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                finish()
//                            }
//
//                            override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
//                                Log.d("TAG", "upload failed ${t.localizedMessage}")
//                            }
//                        })
//                }
//            }
//
//            dialog.show()

//            val gson = Gson()
//            val intent = Intent(applicationContext, ItemDetail::class.java)
//            intent.putExtra("item", gson.toJson(item))
//            startActivity(intent)
        }
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

    fun getDiscount(
        userid: String,
        itemid: String,
        viewBinding: DlgManageProductItemBinding,
        dialog: Dialog,
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
                Toast.makeText(applicationContext, "enter price first", Toast.LENGTH_SHORT).show()
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

    private fun getItemsData(item: Item) {
        RetrofitClient.getApiHolder()
            .getManageProductsItems(item.item_id, MySharedStorage.getUserId())
            .enqueue(object : Callback<RetrofitItemOfUserModel> {
                override fun onResponse(
                    call: Call<RetrofitItemOfUserModel>,
                    response: Response<RetrofitItemOfUserModel>
                ) {
                    if (response.code() == Constants.code_OK) {
                        Log.e("Response", response.body()?.data.toString())
                        adapter.update(response.body()!!.data)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Toast.makeText(
                            this@ManageProducts,
                            "No data found",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    } else {
                        Log.d("TAG", response.code().toString())
                        Log.d("TAG", response.message().toString())
                    }
                }

                override fun onFailure(call: Call<RetrofitItemOfUserModel>, t: Throwable) {
                    Log.d("TAG", t.localizedMessage)
                }
            })
    }
}