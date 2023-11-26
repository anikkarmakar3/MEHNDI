package com.adretsoftware.mehndipvcinterior

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.adretsoftware.mehndipvcinterior.adapters.ItemAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.databinding.ActivityBlankPageBinding
import com.adretsoftware.mehndipvcinterior.databinding.CustomviewImageBinding
import com.adretsoftware.mehndipvcinterior.models.Item
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.Hashtable
import java.util.Timer
import kotlin.concurrent.timerTask

class BlankPage : AppCompatActivity() {
    val NOT_IMAGE = "0"
    var imageViewTable: Hashtable<Int, CustomviewImageBinding> =
        Hashtable<Int, CustomviewImageBinding>()
    lateinit var binding: ActivityBlankPageBinding
    lateinit var adapter: ItemAdapter
    var mainImage = ""
    var parent = ""
    val images = arrayListOf<String>()
    val item = Item()
    val item_id = System.currentTimeMillis().toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlankPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.sixty1)
        binding.mainImage.insert.setOnClickListener(View.OnClickListener {
            photoPick(binding.mainImage.hashCode())
            imageViewTable.put(binding.mainImage.hashCode(), binding.mainImage)
        })
        binding.uploadbtn.setOnClickListener(View.OnClickListener {
            if (binding.name.text.isBlank() || imageViewTable.isEmpty)
                Toast.makeText(applicationContext, "fill all the fields first!", Toast.LENGTH_LONG)
                    .show()
            else
                upload()
        })
        permission()
    }


    fun photoPick(requestCode: Int) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val imageUri = data!!.data
                Log.d("TAG", "onActivityResult Image received")
                val imageBinding = imageViewTable[requestCode]
                CoroutineScope(Dispatchers.Main).launch {
                    imageUpload(imageUri, item_id, imageBinding)
                }
            }
        } else {
            if (resultCode == RESULT_OK) {
                val imageUri = data!!.data
                Log.d("TAG", "onActivityResult Image received")
                val imageBinding = imageViewTable[requestCode]
                CoroutineScope(Dispatchers.Main).launch {
                    imageUpload(imageUri, item_id, imageBinding)
                }
            }
        }
        return
    }

    private suspend fun imageUpload(
        imageUri: Uri?,
        idd: String,
        imageBinding: CustomviewImageBinding?
    ) {
        if (imageUri != null) {
            runOnUiThread {
                imageBinding?.imageview?.setImageURI(imageUri)
                imageBinding?.insert?.visibility = View.GONE
                progressBarFunc(imageBinding!!)
                imageBinding.retry.visibility = View.GONE
            }

            val file = File(RealPathUtil.getRealPath(this, imageUri))

            val compressedImage = Compressor.compress(this, file) {
                default(504, 896, Bitmap.CompressFormat.PNG, 60)
            }
            Log.d("TAG", "orginalSize: ${file.length()} compressedSize:${compressedImage.length()}")
            val fileName = getNewFileName(file.name)
            imageBinding!!.storeName.text = fileName
            val requestFile = RequestBody.create(MediaType.parse("image/*"), compressedImage);
            val id = RequestBody.create(MediaType.parse("text/plain"), idd)
            val body = MultipartBody.Part.createFormData("uploaded_file", fileName, requestFile)


            RetrofitClient.getApiHolder().imageUpload(body, id).enqueue(object :
                Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    if (response.code() == Constants.code_CREATED) {
                        Toast.makeText(applicationContext, "uploaded!", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("TAG ", response.code().toString())
                        imageBinding!!.insert.visibility = View.GONE
                    } else {
                        Log.d("TAG ", response.code().toString())
                        Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    imageBinding!!.retry.visibility = View.VISIBLE
                    imageBinding!!.retry.setOnClickListener(View.OnClickListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            imageUpload(imageUri, idd, imageBinding)
                        }
                    })
                    imageBinding.progressBar.visibility = View.GONE
                    imageBinding.insert.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "upload failed! ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("onFailuer", "photo upload failed: ${t.localizedMessage}")
                }
            })
        }
    }

    private fun progressBarFunc(viewBinding: CustomviewImageBinding) {
        viewBinding.progressBar.visibility = View.VISIBLE
        var counter = 0
        var timer = Timer()
        var timertask = timerTask {
            run() {
                super.runOnUiThread(Runnable {
                    counter++;
                    viewBinding.progressBar.setProgress(counter)
                    if (counter == 100) {
                        timer.cancel()
                        viewBinding.progressBar.visibility = View.INVISIBLE
                    }
                })
            }
        }
        timer.schedule(timertask, 0, 15)
    }

    private fun getNewFileName(n: String): String {
        var name = System.currentTimeMillis().toString()
        if (n.contains(".jpg") || n.contains("JPG")) {
            name += ".jpg"
        } else if (n.contains(".png")) {
            name += ".png"
        } else {
            name = NOT_IMAGE
        }
        return name
    }


    fun permission() {
        if (!checkPermission()) {
            showPermissionDialog()
        }
    }

    private fun showPermissionDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                ),
                333
            )
        } else ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            333
        )
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val read = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_MEDIA_IMAGES
            )
            read == PackageManager.PERMISSION_GRANTED
        } else {
            val write = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val read = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            write == PackageManager.PERMISSION_GRANTED &&
                    read == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 333) {
            if (grantResults.isNotEmpty()) {
//                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
//                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
//                if (read && write) {
//                } else {
//                }
            }
        }
    }

    fun upload() {
        dataStore()
        RetrofitClient.getApiHolder().addCategory(item).enqueue(object :
            Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                println("RAW RESPONSE " + response.raw().toString().replace("/", "\""))
                println("BODY RESPONSE " + response.body().toString())
                Log.d("TAG", "upload success ${response.code()}")
                Toast.makeText(applicationContext, "uploaded!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, Items::class.java))
                finish()
            }

            override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                Log.d("TAG", "upload failed ${t.localizedMessage}")
            }
        })
    }

    fun dataStore() {
        for (i in imageViewTable) {
            if (i.value == binding.mainImage) {
                mainImage = i.value.storeName.text.toString()
            } else {
                images.add(i.value.storeName.text.toString())
            }
        }
        item.name = binding.name.text.toString()
        item.image_url = mainImage
        item.parent = parent
        item.item_id = item_id
    }
}