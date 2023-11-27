package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.CategoryProductFunctions
import com.adretsoftware.mehndipvcinterior.adapters.CategoryProductListAdapter
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityCategoryProductListBinding
import com.adretsoftware.mehndipvcinterior.models.CategoryProductsModelItem
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CategoryProductListActivity : AppCompatActivity(), CategoryProductFunctions {

    lateinit var binding : ActivityCategoryProductListBinding
    lateinit var adapter: CategoryProductListAdapter
    private var isCategory: Boolean = true
    private var parentitemid = ""
    lateinit var catId : String
    lateinit var categorId : RequestBody

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!isCategory) {
                    getCategoriesProductListData()
                } else {
                    finish()
//                    getCategoriesData()
                }
            }
        })

        val bundle = intent.extras

        catId = bundle?.getString("cat_id").toString()
        categorId = RequestBody.create(MediaType.parse("text/plain"), catId)

        adapter = CategoryProductListAdapter(
            this,
            layoutInflater,
            applicationContext,
            "product",
            false
        )

        binding.recyclerViewCategoryProduct.adapter = adapter
        binding.recyclerViewCategoryProduct.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        getCategoriesProductListData()

    }

    private fun getCategoriesProductListData() {
        val params: MutableMap<String, String> = HashMap()

        params["cat_id"] = "3"

        RetrofitClient.getApiHolder().getCategoryProducts(params)
            .enqueue(object : Callback<ArrayList<CategoryProductsModelItem>> {
                override fun onResponse(
                    call: Call<ArrayList<CategoryProductsModelItem>>,
                    response: Response<ArrayList<CategoryProductsModelItem>>
                ) {
                    if (response.isSuccessful){
                        val productList = response.body()!!
                        adapter.update(productList)
                    }
                }

                override fun onFailure(
                    call: Call<ArrayList<CategoryProductsModelItem>>,
                    t: Throwable
                ) {
                    TODO("Not yet implemented")
                }

            })

    }

    override fun ItemClickFunc(item: CategoryProductsModelItem, view: View) {
        val gson = Gson()
        val intent = Intent(applicationContext, ItemDetail::class.java)
        intent.putExtra("productitem", gson.toJson(item))
        startActivity(intent)
    }

    override fun LongItemClick(item: CategoryProductsModelItem, view: View) {
        TODO("Not yet implemented")
    }


}