package com.adretsoftware.mehndipvcinterior

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.adretsoftware.mehndipvcinterior.databinding.ActivityActAppUpdateBinding

class ActAppUpdate : AppCompatActivity() {

    private lateinit var binding: ActivityActAppUpdateBinding
    var downloadLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActAppUpdateBinding.inflate(layoutInflater);
        setContentView(binding.root)

        downloadLink = intent.getStringExtra("downloadLink").toString()
        binding.btnUpdateNow.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink)))
        }
    }
}