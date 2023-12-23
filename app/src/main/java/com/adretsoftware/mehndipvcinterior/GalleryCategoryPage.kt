package com.adretsoftware.mehndipvcinterior

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.adretsoftware.mehndipvcinterior.adapters.GalleryCtaegoryAdapter
import com.adretsoftware.mehndipvcinterior.adapters.GalleryItemFunctions
import com.adretsoftware.mehndipvcinterior.adapters.ItemAdapter
import com.adretsoftware.mehndipvcinterior.adapters.itemFunctions
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.data.model.ProductImage.CategoryId.CategoryIdResp
import com.adretsoftware.mehndipvcinterior.databinding.ActivityGalleryCategoryPageBinding
import com.adretsoftware.mehndipvcinterior.databinding.ActivityItemsBinding
import com.adretsoftware.mehndipvcinterior.models.GalleryCategoryModelItem
import com.adretsoftware.mehndipvcinterior.models.Item
import com.adretsoftware.mehndipvcinterior.models.ItemDelete
import com.adretsoftware.mehndipvcinterior.ui.Picture_Product_List_Activity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleryCategoryPage : AppCompatActivity(), GalleryItemFunctions {
    lateinit var binding: ActivityGalleryCategoryPageBinding
    lateinit var adapter: GalleryCtaegoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryCategoryPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = GalleryCtaegoryAdapter(
            this,
            layoutInflater,
            applicationContext,
            object : ItemAdapter.ClickListener {
                override fun deleteItemClick(itemId: String) {
                    /*val builder = AlertDialog.Builder(this@Items)
                    builder.setTitle("Are you sure?")
                        .setMessage("Your going to delete this item this action cannot be undo.")
                        .setPositiveButton(
                            "DELETE"
                        ) { p0, p1 ->
                            deleteItem(ItemDelete(itemId))
                        }
                        .setNegativeButton("CANCEL", null)
                        .show()*/
                }
            },
            "product",
            false
        )
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)

        getGalleryCategory()
    }

    private fun getGalleryCategory(){
        RetrofitClient.getApiHolder().getGalleyCategoryId()
            .enqueue(object : Callback<List<GalleryCategoryModelItem>> {
                override fun onResponse(
                    call: Call<List<GalleryCategoryModelItem>>,
                    response: Response<List<GalleryCategoryModelItem>>
                ) {
                    if (response.isSuccessful){
                        adapter.update(response.body() as ArrayList<GalleryCategoryModelItem>)
                    }
                }

                override fun onFailure(call: Call<List<GalleryCategoryModelItem>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }


    override fun ItemClickFunc(item: GalleryCategoryModelItem, view: View) {
        val intent = Intent(this, Picture_Product_List_Activity::class.java)
        intent.putExtra("cat_id",item.cat_id)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun LongItemClick(item: GalleryCategoryModelItem, view: View) {
        TODO("Not yet implemented")
    }
}