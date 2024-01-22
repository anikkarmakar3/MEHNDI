package com.adretsoftware.mehndipvcinterior

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.ItemAdapter
import com.adretsoftware.mehndipvcinterior.adapters.itemFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityItemsBinding
import com.adretsoftware.mehndipvcinterior.databinding.DlgManageProductItemBinding
import com.adretsoftware.mehndipvcinterior.fragments.hideKeyboard
import com.adretsoftware.mehndipvcinterior.models.*
import com.adretsoftware.mehndipvcinterior.ui.AddPhotoGalleryActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Items : AppCompatActivity(), itemFunctions {
    lateinit var binding: ActivityItemsBinding
    lateinit var adapter: ItemAdapter
    private var isCategory: Boolean = true
    private var parentitemid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isCategory) {
                    getCategoriesData()
                } else {
                    finish()
//                    getCategoriesData()
                }
            }
        })

        if (Utilis.isLoginAsAdmin()) {
            binding.addproduct.visibility = View.GONE
        } else {
            binding.addproduct.visibility = View.GONE

        }

        window.statusBarColor = getColor(R.color.sixty1)
        binding.addproduct.setOnClickListener(View.OnClickListener {
            if (isCategory) {
                startActivity(Intent(this, BlankPage::class.java))
            } else
                Intent(this, NewItem::class.java).apply {
                    this.putExtra("parentid", parentitemid)
                    startActivity(this)
                }
            //startActivity(Intent(this, NewItem::class.java))

        })
        adapter = ItemAdapter(
            this,
            layoutInflater,
            applicationContext,
            object : ItemAdapter.ClickListener {
                override fun deleteItemClick(itemId: String) {
                    val builder = AlertDialog.Builder(this@Items)
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
            "product",
            false
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        getCategoriesData()

//        RetrofitClient.getApiHolder()
//            .getItems()
//            .enqueue(object : Callback<RetrofitItem> {
//                override fun onResponse(
//                    call: Call<RetrofitItem>,
//                    response: Response<RetrofitItem>
//                ) {
//                    if (response.code() == Constants.code_OK) {
//                        adapter.update(response.body()!!.data)
//                    } else {
//                        Log.d("TAG 2", response.code().toString() + response.message().toString())
//                    }
//                }
//
//                override fun onFailure(call: Call<RetrofitItem>, t: Throwable) {
//                    Log.d("TAG", "on failure retro : ${t.localizedMessage}")
//
//                }
//            })
    }

    private fun getCategoriesData() {
        Volley.newRequestQueue(applicationContext).add(
            StringRequest(
                Request.Method.GET,
                Constants.apiUrl + "category.php",
                { response ->
                    binding.heading.text = "Categories"
                    isCategory = true



                    val responseData = JSONObject(response)
                    val jsonArray = responseData.getJSONArray("data")

                    val items = ArrayList<Item>()
                    for (i in 0 until jsonArray.length()) {
                        items.add(Item.fromJsonObject(jsonArray.getJSONObject(i)))
                    }

                    adapter.update(items)
                },
                { })
        )
    }

    fun deleteItem(item: ItemDelete) {
        RetrofitClient.getApiHolder().deleteItem(item).enqueue(object :
            Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                Toast.makeText(applicationContext, "Deleted!", Toast.LENGTH_SHORT).show()
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
            isCategory = false
            binding.addproduct.text = "Add Product"
            /*binding.heading.text = "Products"*/
//            if (Utilis.isLoginAsAdmin() || MySharedStorage.getUserType() == Constants.AGENT) {
            val parent = RequestBody.create(MediaType.parse("text/plain"), item.item_id)
            val user_id =
                RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())

            RetrofitClient.getApiHolder().getItemsByParent(parent, user_id)
                .enqueue(object : Callback<RetrofitItem> {
                    override fun onResponse(
                        call: Call<RetrofitItem>,
                        response: Response<RetrofitItem>
                    ) {
                        if (response.code() == Constants.code_OK) {
                            Log.e("Response", response.body()?.data.toString())
                            adapter.update(response.body()!!.data)
                        } else if (response.code() == Constants.code_NO_CONTENT) {
                            Toast.makeText(this@Items, "No data found", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Log.d("TAG", response.code().toString())
                            Log.d("TAG", response.message().toString())
                        }
                    }

                    override fun onFailure(call: Call<RetrofitItem>, t: Throwable) {
                        Log.d("TAG", t.localizedMessage)
                    }
                })
//            } else {
//                RetrofitClient.getApiHolder()
//                    .getItemsOfUser(item.item_id, MySharedStorage.getUserId())
//                    .enqueue(object : Callback<RetrofitItemOfUserModel> {
//                        override fun onResponse(
//                            call: Call<RetrofitItemOfUserModel>,
//                            response: Response<RetrofitItemOfUserModel>
//                        ) {
//                            if (response.code() == Constants.code_OK) {
//                                adapter.update(response.body()!!.data)
//                            } else if (response.code() == Constants.code_NO_CONTENT) {
//                                Toast.makeText(this@Items, "No data found", Toast.LENGTH_SHORT)
//                                    .show()
//                            } else {
//                                Log.d("TAG", response.code().toString())
//                            }
//                        }
//
//                        override fun onFailure(call: Call<RetrofitItemOfUserModel>, t: Throwable) {
//                            Log.d("TAG", t.localizedMessage)
//                        }
//                    })
//            }
        } else {
            isCategory = false
//            val gson = Gson()
            val intent = Intent(applicationContext, ItemDetail::class.java)
            item.amount = ""
            item.discount = ""
//            intent.putExtra("item", gson.toJson(item))
            startActivity(intent)
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

    override fun LongItemClick(item: Item, view: View) {
        if (parentitemid != "" && Utilis.isLoginAsAdmin()) {
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
                                    this@Items,
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
        }
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
}
