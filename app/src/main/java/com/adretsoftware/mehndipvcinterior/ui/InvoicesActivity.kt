package com.adretsoftware.mehndipvcinterior.ui

import android.Manifest
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.adretsoftware.mehndipvcinterior.R
import com.adretsoftware.mehndipvcinterior.adapters.InvoiceItemClickListener
import com.adretsoftware.mehndipvcinterior.adapters.InvoicesItemAdapter
import com.adretsoftware.mehndipvcinterior.daos.Constants
import com.adretsoftware.mehndipvcinterior.daos.MySharedStorage
import com.adretsoftware.mehndipvcinterior.daos.RetrofitClient
import com.adretsoftware.mehndipvcinterior.daos.Utilis
import com.adretsoftware.mehndipvcinterior.databinding.ActivityInvoicesListBinding
import com.adretsoftware.mehndipvcinterior.models.InvoicesModel
import com.adretsoftware.mehndipvcinterior.models.RetrofitInvoicesItem
import com.adretsoftware.mehndipvcinterior.models.RetrofitResponse
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.ArrayList

class InvoicesActivity : AppCompatActivity(), InvoiceItemClickListener, PickiTCallbacks {
    private val FILE_PICKER = 100
    lateinit var invoicesItemAdapter: InvoicesItemAdapter
    private lateinit var binding: ActivityInvoicesListBinding
    private lateinit var pickiT: PickiT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_invoices_list)
        pickiT = PickiT(this, this, this)
        init()
        bindAdapter()
    }

    private fun init() {
        if (Utilis.isLoginAsAdmin()) {
            binding.btnAddInvoice.visibility = View.VISIBLE
        } else {
            binding.btnAddInvoice.visibility = View.GONE
        }

//        binding.btnFab.setOnClickListener {
//            startActivity(
//                Intent(
//                    applicationContext, AddPhotoGalleryActivity::class.java
//                )
//            )
//        }

        binding.btnAddInvoice.setOnClickListener {
            permission()
        }

        loadData()
    }

    private fun loadData() {
        RetrofitClient.getApiHolder().getUserInvoices(MySharedStorage.getUserr().mobile)
            .enqueue(object : Callback<RetrofitInvoicesItem> {
                override fun onResponse(
                    call: Call<RetrofitInvoicesItem>,
                    response: Response<RetrofitInvoicesItem>
                ) {
                    if (response.code() == Constants.code_OK) {
                        invoicesItemAdapter.update(response.body()!!.data)
                    } else if (response.code() == Constants.code_NO_CONTENT) {
                        Toast.makeText(
                            applicationContext, "No data found", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("TAG", "getUserbyParent:" + response.code())
                    }
                }

                override fun onFailure(call: Call<RetrofitInvoicesItem>, t: Throwable) {
                    Log.d("TAG", "getUserbyParent:" + t.getLocalizedMessage())
                }
            })

//        RetrofitClient.getApiHolder().getInvoices()
//            .enqueue(object : Callback<RetrofitInvoicesItem> {
//                override fun onResponse(
//                    call: Call<RetrofitInvoicesItem>,
//                    response: Response<RetrofitInvoicesItem>
//                ) {
//                    if (response.code() == Constants.code_OK) {
//                        invoicesItemAdapter.update(response.body()!!.data)
//                    } else if (response.code() == Constants.code_NO_CONTENT) {
//                        Toast.makeText(
//                            applicationContext, "No data found", Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        Log.d("TAG", "getUserbyParent:" + response.code())
//                    }
//                }
//
//                override fun onFailure(call: Call<RetrofitInvoicesItem>, t: Throwable) {
//                    Log.d("TAG", "getUserbyParent:" + t.getLocalizedMessage())
//                }
//            })
    }

    fun permission() {
        if (!checkPermission()) {
            showPermissionDialog()
        } else {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "application/pdf")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(intent, FILE_PICKER)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_PICKER && resultCode == RESULT_OK && data != null) {
            val selectedFileUri: Uri? = data.data
            showLoading()
            pickiT.getPath(selectedFileUri, Build.VERSION.SDK_INT)
//            val file = File(RealPathUtil.getRealPath(this, imageUri))
//            imageUpload(selectedFileUri, System.currentTimeMillis().toString())
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

    private fun imageUpload(
        file: File?,
        idd: String,
    ) {
        if (file != null) {
            Log.d("TAG", "orginalSize: ${file.length()} compressedSize:${file.length()}")

            val requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            val id = RequestBody.create(MediaType.parse("text/plain"), idd)
//                val requestFile= RequestBody.create(MediaType.parse("multipart/form-data"), file);
            val body = MultipartBody.Part.createFormData("uploaded_file", file.name, requestFile)
            RetrofitClient.getApiHolder().imageUpload(body, id)
                .enqueue(object : Callback<RetrofitResponse> {
                    override fun onResponse(
                        call: Call<RetrofitResponse>,
                        response: Response<RetrofitResponse>
                    ) {
                        if (response.code() == Constants.code_CREATED) {
                            saveInvoiceIntoDatabase(file.name)
                        } else {
                            closeLoading()
                            Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                        closeLoading()
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

    private fun saveInvoiceIntoDatabase(name: String) {
        val invoicesModel = InvoicesModel(invo_lbl = name)
        RetrofitClient.getApiHolder().saveInvoices(invoicesModel)
            .enqueue(object : Callback<RetrofitResponse> {
                override fun onResponse(
                    call: Call<RetrofitResponse>,
                    response: Response<RetrofitResponse>
                ) {
                    closeLoading()
                    if (response.code() == Constants.code_CREATED) {
                        Toast.makeText(applicationContext, "uploaded!", Toast.LENGTH_SHORT)
                            .show()
                        loadData()
                    } else {
                        Log.d("TAG", "getUserbyParent:" + response.code())
                    }
                }

                override fun onFailure(call: Call<RetrofitResponse>, t: Throwable) {
                    Log.d("TAG", "getUserbyParent:" + t.getLocalizedMessage())
                }
            })
    }

    fun downloadFile(url: String, title: String, description: String) {
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(title)
            .setDescription(description)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(applicationContext, "File downloading started...", Toast.LENGTH_SHORT).show()
        closeLoading()
    }

    private fun bindAdapter() {
        invoicesItemAdapter =
            InvoicesItemAdapter(this, layoutInflater, applicationContext)
        binding.recylceView.adapter = invoicesItemAdapter
        binding.recylceView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
    }

    override fun onItemClick(url: String, name: String) {
        showLoading()
        downloadFile(url, name, "Downloading the file...")
    }

    override fun PickiTonUriReturned() {

    }

    override fun PickiTonStartListener() {

    }

    override fun PickiTonProgressUpdate(progress: Int) {

    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        if (wasSuccessful) {
            imageUpload(path?.let { File(it) }, System.currentTimeMillis().toString())
        }
    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
    }
}
