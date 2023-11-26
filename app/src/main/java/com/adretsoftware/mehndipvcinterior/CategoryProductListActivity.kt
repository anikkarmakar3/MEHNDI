package com.adretsoftware.mehndipvcinterior

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.CategoryProductFunctions
import com.adretsoftware.mehndipvcinterior.adapters.CategoryProductListAdapter
import com.adretsoftware.mehndipvcinterior.models.CategoryProductListModel
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityCategoryProductListBinding
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
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


        

    }

    override fun ItemClickFunc(item: CategoryProductListModel, view: View) {
        TODO("Not yet implemented")
    }

    override fun LongItemClick(item: CategoryProductListModel, view: View) {
        TODO("Not yet implemented")
    }


}