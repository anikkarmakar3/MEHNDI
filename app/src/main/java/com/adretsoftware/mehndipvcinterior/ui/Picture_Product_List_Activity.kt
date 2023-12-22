package com.adretsoftware.mehndipvcinterior.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.adretsoftware.mehndipvcinterior.ImageViewZoomActivity
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.adapters.Picture_Product_ItemAdapter
import com.adretsoftware.mehndipvcinterior.adapters.RecycleItemClickListener
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityPictureProductListBinding
import com.adretsoftware.mehndipvcinterior.models.Data
import com.adretsoftware.mehndipvcinterior.models.GetGalleryModel
import com.adretsoftware.mehndipvcinterior.models.RetrofitPictureGalleryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Picture_Product_List_Activity : AppCompatActivity(), RecycleItemClickListener {
    lateinit var pictureProductItemadapter: Picture_Product_ItemAdapter
    private lateinit var binding: ActivityPictureProductListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_picture_product_list)
        init()
        bindAdapter()
    }

    private fun init() {
//        if (Utilis.isLoginAsAdmin()) {
//            binding.btnFab.visibility = View.VISIBLE
//        } else {
//            binding.btnFab.visibility = View.GONE
//        }
        binding.btnFab.visibility = View.VISIBLE
        binding.btnFab.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext, AddPhotoGalleryActivity::class.java
                )
            )
        }

        RetrofitClient.getApiHolder().getGalleryImage()
            .enqueue(object : Callback<GetGalleryModel> {
                override fun onResponse(
                    call: Call<GetGalleryModel>,
                    response: Response<GetGalleryModel>
                ) {
                    if (response.code() == Constants.code_OK) {
                        pictureProductItemadapter.update(response.body()!!.data as ArrayList<Data>)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Toast.makeText(
                            applicationContext, "no data found", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("TAG", "getUserbyParent:" + response.code())
                    }
                }

                override fun onFailure(call: Call<GetGalleryModel>, t: Throwable) {
                    Toast.makeText(
                        applicationContext, "network problem", Toast.LENGTH_SHORT
                    ).show()
                }

            })
    }

    private fun bindAdapter() {
        pictureProductItemadapter =
            Picture_Product_ItemAdapter(this, layoutInflater, applicationContext)
        binding.recylceviewPictureProduct.adapter = pictureProductItemadapter
        binding.recylceviewPictureProduct.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
    }

    override fun onItemClick(url: String, name: String) {
        Intent(this, ImageViewZoomActivity::class.java).apply {
            putExtra("url", url)
            putExtra("name", name)
            startActivity(this)
        }
    }
}
