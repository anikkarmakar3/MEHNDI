package com.adretsoftware.mehndipvcinterior.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.RealPathUtil
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.data.model.ProductImage.CategoryId.CategoryIdResp
import com.adretsoftware.mehndipvcinterior.databinding.ActivityAddPhotoGalleryBinding
import com.adretsoftware.mehndipvcinterior.databinding.CustomviewImageBinding
import com.adretsoftware.mehndipvcinterior.models.PictureGalleryModel
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception
import java.util.ArrayList
import java.util.Hashtable
import java.util.Timer
import kotlin.concurrent.timerTask

class AddPhotoGalleryActivity : AppCompatActivity() {
    var imageViewTable: Hashtable<Int, CustomviewImageBinding> =
        Hashtable<Int, CustomviewImageBinding>()
    val NOT_IMAGE = "0"
    val item_id = System.currentTimeMillis().toString()
    var mainImage = ""
    var parent = ""
    val images = arrayListOf<String>()
    val item = PictureGalleryModel()
    private var categoryResponse: CategoryIdResp? = null
    var selectedPosition = 0
    var caterGoryId : ArrayList<String> = arrayListOf()
    var caterGoryName : ArrayList<String> = arrayListOf()
    var uriGlobal:Uri? = null
    private lateinit var imageFile: File

    private val PERMISSION_REQUEST_CODE: Int = 101
    private lateinit var binding: ActivityAddPhotoGalleryBinding
    val SELECT_PICTURE = 107
    private var permissionList = arrayListOf<String>()
    var getUserId = ""

    var requestCode=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo_gallery)
        init()

    }

    private fun init() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        
        

        MySharedStorage.func(this)
        getUserId =MySharedStorage.getUserId()
        Log.d("userid", getUserId)

        getGalleryCategory()

        binding.uploadbtn.setOnClickListener {
            if (uriGlobal!=null) {
                newImageUpload(imageFile)
            } else {
                Toast.makeText(applicationContext, "fill all the fields first!", Toast.LENGTH_LONG)
                    .show()
            }

            /*val imageBinding = imageViewTable[requestCode]
            CoroutineScope(Dispatchers.IO).launch {
                imageUpload(uriGlobal, item_id, imageBinding)
            }*/

        }

        binding.mainImage.root.setOnClickListener(View.OnClickListener {
            requestPermission()
        })





//        photoPick(binding.mainImage.hashCode())
//        imageViewTable.put(binding.mainImage.hashCode(),binding.mainImage)
    }

    private fun requestPermission() {
        Dexter.withContext(this)
            .withPermissions(
                permissionList
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    if (report!!.areAllPermissionsGranted()) {

                        photoPick(204)
                        /*imageViewTable.put(binding.mainImage.hashCode(), binding.mainImage)*/
                        //  photoPick(2000)

                    } else {
                        Toast.makeText(
                            this@AddPhotoGalleryActivity,
                            "Permissions Denied",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }


    fun photoPick(requestCode: Int) {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), requestCode)

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), requestCode
        )

    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data

            if (uri != null) {
                try {
                  uriGlobal = uri
                    binding.mainImage.imageview.setImageURI(uriGlobal)
                    binding.mainImage.insert.visibility = View.GONE
                    binding.mainImage.delete.visibility = View.GONE
                    imageFile = File(RealPathUtil.getRealPath(this, uriGlobal!!)!!)
                    /*newImageUpload(imageFile)*/
                   this.requestCode=requestCode

                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(
                    this, "Fail",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun newImageUpload(file: File) {
        CoroutineScope(Dispatchers.Main).launch {
            val compressedImage = Compressor.compress(applicationContext, file) {
                default(504, 896, Bitmap.CompressFormat.PNG, 60)
            }
            Log.d("TAG", "orginalSize: ${file.length()} compressedSize:${compressedImage.length()}")
            val fileName = getNewFileName(file.name)
            val requestFile = RequestBody.create(MediaType.parse("image/*"), compressedImage);
            val id = RequestBody.create(MediaType.parse("text/plain"), System.currentTimeMillis().toString())
            val UserId = RequestBody.create(MediaType.parse("text/plain"), getUserId)
            val catid = RequestBody.create(MediaType.parse("text/plain"), caterGoryId[selectedPosition])
            val body = MultipartBody.Part.createFormData("uploaded_file", fileName, requestFile)
            RetrofitClient.getApiHolder().imageUpload(body, id, UserId, catid)
                .enqueue(object :
                    Callback<RetrofitResponse> {
                    override fun onResponse(
                        call: Call<RetrofitResponse>,
                        response: Response<RetrofitResponse>
                    ) {
                        if (response.code() == 200) {
                            Toast.makeText(
                                applicationContext,
                                "uploaded!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            Log.d("TAG ", response.code().toString())
                        } else {
                            Log.d("TAG ", response.code().toString())
                            Toast.makeText(
                                applicationContext,
                                "Failed!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            "upload failed! ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("TAG", "photo upload failed: ${t.localizedMessage}")
                    }
                })
        }
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 2000) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) {
//                } else {
//                }
//            }
//
//
//
////            var imageUri=data!!.data
////
////            if(imageUri!=null)
////            {
////                Log.d("TAG","onActivityResult Image received")
////                val imageBinding= imageViewTable[requestCode]
////                CoroutineScope(Dispatchers.Main).launch {
////                    imageUpload(imageUri, item_id, imageBinding)
////                }
////            }
//
//
//
//        }else{
//
//            var imageUri=data!!.data
////            Log.d("TAG","onActivityResult Image received")
////            val imageBinding= imageViewTable[requestCode]
////            CoroutineScope(Dispatchers.Main).launch {
////                imageUpload(imageUri, item_id, imageBinding)
////            }
//
//            try {
//                if(imageUri!=null)imageUpload
//                {
//                    Log.d("TAG","onActivityResult Image received")
//                    val imageBinding= imageViewTable[requestCode]
//                    CoroutineScope(Dispatchers.Main).launch {
//                        (imageUri, item_id, imageBinding)
//                    }
//                }
//            }catch (e:Exception)
//            {
//
//            }
//        }
//        return
//    }

    private suspend fun imageUpload(
        imageUri: Uri?,
        idd: String,
        imageBinding: CustomviewImageBinding?
    ) {
        if (imageUri != null) {
            withContext(Dispatchers.Main)
            {
                imageBinding?.imageview?.setImageURI(imageUri)
                imageBinding?.insert?.visibility = View.GONE
                /*progressBarFunc(imageBinding!!)
                imageBinding.retry.visibility = View.GONE*/
            }

            val file = File(RealPathUtil.getRealPath(this, imageUri))

            val compressedImage = Compressor.compress(this, file) {
                default(504, 896, Bitmap.CompressFormat.PNG, 60)
            }
            Log.d("TAG", "orginalSize: ${file.length()} compressedSize:${compressedImage.length()}")
            val fileName = getNewFileName(file.name)
            runOnUiThread {
                imageBinding!!.storeName.text = fileName
            }
            val requestFile = RequestBody.create(MediaType.parse("image/*"), compressedImage);
            val id = RequestBody.create(MediaType.parse("text/plain"), idd)
            val UserId = RequestBody.create(MediaType.parse("text/plain"), getUserId)
            val catid = RequestBody.create(MediaType.parse("text/plain"), caterGoryId[selectedPosition])
//                val requestFile= RequestBody.create(MediaType.parse("multipart/form-data"), file);
            val body = MultipartBody.Part.createFormData("uploaded_file", fileName, requestFile)

            /*RetrofitClient.getApiHolder().imageUpload(body, id,  UserId ,catid).enqueue(object :
                Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, "uploaded!", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("TAG ", response.code().toString())

                        if (imageBinding != null) {
                            imageBinding.insert.visibility = View.GONE
                        }
                    } else {
                        Log.d("TAG ", response.code().toString())
                        Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    if (imageBinding != null) {
                        imageBinding.retry.visibility = View.VISIBLE
                    }
                    imageBinding?.retry?.setOnClickListener(View.OnClickListener {
                        CoroutineScope(Dispatchers.Main).launch {
                            imageUpload(imageUri, idd, imageBinding)
                        }
                    })
                    if (imageBinding != null) {
                        imageBinding.progressBar.visibility = View.GONE
                    }
                    if (imageBinding != null) {
                        imageBinding.insert.visibility = View.GONE
                    }
                    Toast.makeText(
                        applicationContext,
                        "upload failed! ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("TAG", "photo upload failed: ${t.localizedMessage}")
                }
            })*/
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

    fun upload() {
        dataStore()
        RetrofitClient.getApiHolder().SaveGalleryImage(item).enqueue(object :
            Callback<RetrofitResponse> {
            override fun onResponse(
                call: Call<RetrofitResponse>,
                response: Response<RetrofitResponse>
            ) {
                Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT)
                    .show()
                // startActivity(Intent(applicationContext, Items::class.java)
                finish()
            }

            override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                Log.d("fhf", "onFailure: "+call)
                Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT)
                    .show()
                // startActivity(Intent(applicationContext, Items::class.java)
                finish()
            }
        })
    }


    private fun getGalleryCategory(){
        RetrofitClient.getApiHolder().getImageCategoryId()
            .enqueue(object : Callback<CategoryIdResp> {
                override fun onResponse(
                    call: Call<CategoryIdResp>,
                    response: Response<CategoryIdResp>
                ) {
                    if (response.code() == Constants.code_OK) {
                        categoryResponse = response.body()
                        creatSpinnerAdapter()

                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Toast.makeText(
                            applicationContext, "no data found", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("TAG", "getUserbyParent:" + response.code())
                    }
                }

                override fun onFailure(call: Call<CategoryIdResp>, t: Throwable) {
                    Log.d("TAG", "getUserbyParent:" + t.getLocalizedMessage())
                }
            })
    }

    private fun creatSpinnerAdapter() {
        caterGoryId.clear()
        caterGoryName.clear()
  categoryResponse?.let {
      it.forEach {

          caterGoryId.add(it.id)
          caterGoryName.add(it.category)
      }
  }

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item,caterGoryName )
        binding.name.adapter = adapter

        binding.name.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                selectedPosition = position
//                Toast.makeText(this@AddPhotoGalleryActivity,
//                    getString(R.string.selected_item) + " " +
//                            "" + languages[position], Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

    }

    fun dataStore() {
        for (i in imageViewTable) {
            if (i.value == binding.mainImage) {
                mainImage = i.value.storeName.text.toString()
            } else {
                images.add(i.value.storeName.text.toString())
            }
        }

        item.user_id = getUserId
        item.picture_image = mainImage
        item.cat_id = caterGoryId[selectedPosition]
       // item.picture_name = System.currentTimeMillis().toString()
    }
}