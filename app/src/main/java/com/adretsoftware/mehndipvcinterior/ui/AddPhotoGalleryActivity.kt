package com.adretsoftware.mehndipvcinterior.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Surface
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.adretsoftware.mehndipvcinterior.BuildConfig
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Hashtable
import java.util.Locale
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
    val REQUEST_CODE = 400
    var photoFile : File?= null
    val imageCapture = ImageCapture.Builder()
        .setTargetRotation(Surface.ROTATION_0)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo_gallery)
        init()
        binding.capturebtn.setOnClickListener {
            val file = File(externalMediaDirs.first(), "${System.currentTimeMillis()}.jpg")

            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image saved successfully
                    uriGlobal = Uri.fromFile(file)
                    binding.mainImage.imageview.setImageURI(uriGlobal)
                    binding.mainImage.insert.visibility = View.GONE
                    binding.mainImage.delete.visibility = View.GONE
                    imageFile = File(RealPathUtil.getRealPath(applicationContext, uriGlobal!!)!!)
                    Toast.makeText(applicationContext, "Image saved: $uriGlobal", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    // Handle error
                    Log.e(TAG, "Error capturing image: ${exception.message}", exception)
                }
            })
            binding.viewFinder.visibility = View.GONE
            binding.capturebtn.visibility = View.GONE
        }
    }

    private fun showOptionsDialog() {
        val options = arrayOf("Gallery", "Camera")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
            .setItems(options) { dialogInterface: DialogInterface, which: Int ->
                // Dismiss the dialog when an option is selected
                dialogInterface.dismiss()

                // Handle the selected option
                when (which) {
                    0 -> requestPermission(0)
                    1 -> requestPermission(1)
                    else -> {}
                }
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun init() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
            permissionList.add(Manifest.permission.READ_MEDIA_AUDIO)
            permissionList.add(Manifest.permission.READ_MEDIA_VIDEO)
            permissionList.add(Manifest.permission.CAMERA)
        } else {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissionList.add(Manifest.permission.CAMERA)
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

        binding.mainImage.insert.setOnClickListener {
            showOptionsDialog()
        }

        binding.mainImage.root.setOnClickListener(View.OnClickListener {
            showOptionsDialog()
            /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, open the camera
                startCamera()
            } else {
                // Request camera permission
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
            }*/
        })

        /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            if (requestCode == CAMERA_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the camera
                startCamera()
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }*/






//        photoPick(binding.mainImage.hashCode())
//        imageViewTable.put(binding.mainImage.hashCode(),binding.mainImage)
    }

    private fun startCamera() {
        /*val imageCapture = ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()*/
        binding.viewFinder.visibility = View.VISIBLE
        binding.capturebtn.visibility = View.VISIBLE
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            // Bind the camera
            bindCameraUseCases(cameraProvider, imageCapture)
        }, ContextCompat.getMainExecutor(this))

    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider, imageCapture: ImageCapture) {
        val viewFinder: PreviewView = findViewById(R.id.viewFinder)
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind previous use cases before binding new ones
            cameraProvider.unbindAll()

            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture
            )

        } catch (e: Exception) {
            // Handle exceptions
            Log.e(TAG, "Error binding camera use cases", e)
        }
    }


    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }


    private fun requestPermission(data:Int) {
        Dexter.withContext(this)
            .withPermissions(
                permissionList
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    if (report!!.areAllPermissionsGranted()) {

                        when(data){
                            0 -> photoPick(203)
                            1-> startCamera()
                        }

                        /*imageViewTable.put(binding.mainImage.hashCode(), binding.mainImage)*/
                        //  photoPick(2000)
                        /*val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        photoFile = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            // Handle error when creating the file
                            null
                        }
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                applicationContext,
                                BuildConfig.APPLICATION_ID + ".provider",
                                it
                            )
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(cameraIntent, REQUEST_CODE)
                        }
*/
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
            /*val image = data!!.extras!!["data"] as Bitmap?

            if (data == null) {
                // The photo was successfully captured and saved to the file
                // Use the URI of the saved file (photoFile) as needed
                val savedUri = Uri.fromFile(photoFile)
                binding.mainImage.imageview.setImageURI(savedUri)
                binding.mainImage.insert.visibility = View.GONE
                binding.mainImage.delete.visibility = View.GONE
                imageFile = File(RealPathUtil.getRealPath(this, uriGlobal!!)!!)
                *//*Toast.makeText(this, "Image saved: $savedUri", Toast.LENGTH_SHORT).show()*//*
            } else {
                // Handle unexpected data in the intent (this should not happen in this case)
                Toast.makeText(this, "Unexpected data in the intent", Toast.LENGTH_SHORT).show()
            }*/

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
            R.layout.spinner_list,caterGoryName )
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