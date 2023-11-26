package com.adretsoftware.mehndipvcinterior

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adretsoftware.mehndipvcinterior.databinding.ActivityImageViewZoomBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener


class ImageViewZoomActivity : AppCompatActivity() {
    private var imageurl: String = ""
    private lateinit var binding: ActivityImageViewZoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_view_zoom)
        init()
    }

    private fun init() {
        intent.getStringExtra("url").let {
            imageurl = it!!
        }

        binding.tvHeading.text = intent.getStringExtra("name")

        showImage()
    }

    private fun showImage() {
        Glide.with(this)
            .load(imageurl)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.setVisibility(View.GONE)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.setVisibility(View.GONE)
                    return false
                }


            })
            .into(binding.photoView)
    }
}