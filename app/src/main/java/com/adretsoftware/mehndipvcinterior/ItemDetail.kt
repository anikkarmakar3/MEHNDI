package com.adretsoftware.mehndipvcinterior

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import com.adretsoftware.mehndipvcinterior.adapters.SliderAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityItemDetailBinding
import com.adretsoftware.mehndipvcinterior.databinding.CustomviewItemImageBinding
import com.adretsoftware.mehndipvcinterior.models.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Call

class ItemDetail : AppCompatActivity() {
    lateinit var binding: ActivityItemDetailBinding
    /*lateinit var item: Item*/
    lateinit var productItem: CategoryProductsModelItem
    var price: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productItem = Gson().fromJson(intent.getStringExtra("productitem"), CategoryProductsModelItem::class.java)

        binding.about.setText(Html.fromHtml(productItem.about))
        binding.features.setText(Html.fromHtml(productItem.features))
        binding.name.setText(productItem.name)
        binding.code.setText(productItem.code)
        binding.price.setText(productItem.price)
        /*item = Gson().fromJson(intent.getStringExtra("item"), Item::class.java)

        binding.about.setText(Html.fromHtml(item.about))
        binding.features.setText(Html.fromHtml(item.features))
        binding.name.setText(item.name)
        binding.code.setText(item.code)*/

        /*price = if (!item.individual_discount.isNullOrEmpty()) {
            item.individual_discount.toFloat()
        } else {
            item.price.toFloat()
        }*/
        /*binding.price.setText(price.toString())*/

        /*if (!item.discount.isNullOrEmpty()) {
            binding.price.setText((price - item.discount.toInt()).toString())
            price = (price - item.discount.toInt())
        } else if (!item.amount.isNullOrEmpty()) {
            binding.price.setText((price - item.amount.toInt()).toString())
            price = (price - item.amount.toInt())
        }*/

//        if (item.discount != "") {
//            binding.price.setText(item.price.toInt() - item.discount.toInt())
//        }

//        if (Utilis.isLoginAsAdmin()) {
//            binding.addtocart.visibility = View.GONE
//        }

        /*binding.addtocart.setOnClickListener(View.OnClickListener {
            addtoCart()
        })*/
        var imageUrl = ArrayList<Image>()
        productItem.image.forEach {
            val image = Image()
            image.filename = it.substringAfterLast('/')
            imageUrl.add(image)
        }
        var slideAdapter = SliderAdapter()
        binding.sliderView.setSliderAdapter(slideAdapter)
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
        binding.sliderView.startAutoCycle()
        slideAdapter.updateData(imageUrl)
        /*imageLoad(imageUrl as ArrayList<Image>)*/




        /*sliderImageLoad(productItem)*/
//        priceLoad(item)
    }
    /*fun imageLoad(data: ArrayList<Image>) {
        val viewBinding = CustomviewItemImageBinding.inflate(layoutInflater)
        *//*val url = Constants.apiUrl + Constants.imageUrl + i.filename*//*
            Glide.with(applicationContext).load(data.first()).into(viewBinding.imageView)
            binding.images.addView(viewBinding.root)
    }*/

    /*fun sliderImageLoad(item: CategoryProductsModelItem) {
        val item_id = RequestBody.create(MediaType.parse("text/plain"), item.item_id)
        RetrofitClient.getApiHolder().itemImageDownload(item_id)
            .enqueue(object : retrofit2.Callback<RetrofitImage> {
                override fun onResponse(
                    call: Call<RetrofitImage>, response: retrofit2.Response<RetrofitImage>
                ) {
                    if (response.code() == Constants.code_OK) {

                        var slideAdapter = SliderAdapter()
                        binding.sliderView.setSliderAdapter(slideAdapter)
                        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
                        binding.sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
                        binding.sliderView.startAutoCycle()
                        slideAdapter.updateData(response.body()!!.data)
//                        imageLoad(response.body()!!.data)
                    }
                    Log.d("TAG", "itemImageDownload:" + response.code())
                }

                override fun onFailure(call: Call<RetrofitImage>, t: Throwable) {
                    Log.d("TAG", "itemImageDownload:" + t.localizedMessage)
                }
            })
    }*/

    /*fun sliderImageLoad(item: Item) {
        val item_id = RequestBody.create(MediaType.parse("text/plain"), item.item_id)
        RetrofitClient.getApiHolder().itemImageDownload(item_id)
            .enqueue(object : retrofit2.Callback<RetrofitImage> {
                override fun onResponse(
                    call: Call<RetrofitImage>, response: retrofit2.Response<RetrofitImage>
                ) {
                    if (response.code() == Constants.code_OK) {

                        var slideAdapter = SliderAdapter()
                        binding.sliderView.setSliderAdapter(slideAdapter)
                        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
                        binding.sliderView.setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
                        binding.sliderView.startAutoCycle()
                        slideAdapter.updateData(response.body()!!.data)
//                        imageLoad(response.body()!!.data)
                    }
                    Log.d("TAG", "itemImageDownload:" + response.code())
                }

                override fun onFailure(call: Call<RetrofitImage>, t: Throwable) {
                    Log.d("TAG", "itemImageDownload:" + t.localizedMessage)
                }
            })
    }*/




//    fun imageLoad(data: ArrayList<Image>) {
//        for (i in data) {
//            val viewBinding = CustomviewItemImageBinding.inflate(layoutInflater)
//            val url = Constants.apiUrl + Constants.imageUrl + i.filename
//            Glide.with(applicationContext).load(url).into(viewBinding.imageView)
//            binding.images.addView(viewBinding.root)
//        }
//    }

    /*fun addtoCart() {
        val cart = CartItem()
        cart.fromItem(item)
        cart.item_id = item.item_id
        cart.user_id = MySharedStorage.getUserId()
        if (price != 0F) {
            cart.price = price.toString()
            cart.total_price = price.toString()
        } else {
            cart.price = item.price
            cart.total_price = item.price
        }*/

//        if (!item.discount.isNullOrEmpty()) {
//            binding.price.setText((price - item.discount.toInt()).toString())
//        } else if (!item.amount.isNullOrEmpty()) {
//            binding.price.setText((price - item.amount.toInt()).toString())
//        }

    /*cart.quantity = "1"
    RetrofitClient.getApiHolder().setCart(cart)
        .enqueue(object : retrofit2.Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>, response: retrofit2.Response<RetrofitResponse>
            ) {
                if (response.code() == Constants.code_CREATED) {
                    Log.d("TAG", "setCart:" + "cart added")
//                        binding.addtocart.text = "Added to cart"
                    binding.addtocart.isClickable = false
                    Toast.makeText(applicationContext, "Added to cart!", Toast.LENGTH_SHORT)
                        .show()

                }
                Log.d("TAG", "setCart:" + response.code())

            }

            override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                Log.d("TAG", "setCart:" + t.localizedMessage)
            }

        })

}*/

    /*fun priceLoad(item: Item) {
        val user_id = RequestBody.create(MediaType.parse("text/plain"), MySharedStorage.getUserId())
        val item_id = RequestBody.create(MediaType.parse("text/plain"), item.item_id)

        RetrofitClient.getApiHolder().getDiscountByUser(user_id, item_id)
            .enqueue(object : retrofit2.Callback<RetrofitDiscount> {
                override fun onResponse(
                    call: Call<RetrofitDiscount>, response: retrofit2.Response<RetrofitDiscount>
                ) {
                    if (response.code() == Constants.code_OK) {
                        loadDiscountToView(response.body()!!.data)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        binding.price.text = item.price
//                        val item_id = RequestBody.create(MediaType.parse("text/plain"), item.parent)
//
//                        RetrofitClient.getApiHolder().getDiscountByUser(user_id, item_id)
//                            .enqueue(object : retrofit2.Callback<RetrofitDiscount> {
//                                override fun onResponse(
//                                    call: Call<RetrofitDiscount>,
//                                    response: retrofit2.Response<RetrofitDiscount>
//                                ) {
//                                    if (response.code() == Constants.code_OK) {
//                                        loadDiscountToView(response.body()!!.data)
//                                    } else if (response.code() == Constants.code_NO_CONTENT) {
//
//                                    }
//                                    Log.d("TAG", "getDiscountByUser:" + response.code().toString())
//                                }
//
//                                override fun onFailure(call: Call<RetrofitDiscount>, t: Throwable) {
//                                    Log.d("TAG", "getDiscountByUser" + t.localizedMessage)
//                                }
//                            })
                    } else {
                        Log.d("TAG", "getDiscountByUser:" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<RetrofitDiscount>, t: Throwable) {
                    Log.d("TAG", "getDiscountByUser" + t.localizedMessage)
                }
            })
    }

    private fun loadDiscountToView(data: ArrayList<Discount>) {
        val disc = data[0]
        price = if (disc.discount_type == Constants.PRICE) {
            item.price.toFloat() - disc.amount.toFloat()
        } else {
            percentageOff(item.price.toFloat(), disc.amount.toFloat())
        }
        binding.price.text = price.toString()
    }

    private fun percentageOff(price: Float, percentageValue: Float): Float {
        return price * (1 - percentageValue / 100)
    }*/
}